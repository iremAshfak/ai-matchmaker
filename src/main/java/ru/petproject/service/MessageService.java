package ru.petproject.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.async.DeferredResult;
import ru.petproject.dto.*;
import ru.petproject.exception.NotFoundException;
import ru.petproject.model.Message;
import ru.petproject.model.User;
import ru.petproject.model.enums.MessageStatus;
import ru.petproject.repository.MessageRepository;
import ru.petproject.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final MatchService matchService;

    private final Map<Long, List<DeferredResult<ResponseEntity<List<MessageDTO>>>>>
            deferredResults = new ConcurrentHashMap<>();

    private final ExecutorService asyncExecutor = Executors.newCachedThreadPool();

    private static final long LONG_POLL_TIMEOUT_MS = 30000;
    private static final long POLLING_INTERVAL_MS = 5000;

    @Transactional
    public MessageDTO sendMessage(Long senderId, SendMessageRequest request) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new NotFoundException("Отправитель не найден"));
        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new NotFoundException("Получатель не найден"));
        validateCanMessage(sender, receiver);
        Message message = Message.builder()
                .fromUser(sender)
                .toUser(receiver)
                .messageText(request.getContent().trim())
                .status(MessageStatus.SENT)
                .createdAt(LocalDateTime.now())
                .build();
        if (request.getReplyToId() != null) {
            Message replyTo = messageRepository.findById(request.getReplyToId())
                    .orElseThrow(() -> new NotFoundException("Сообщение для ответа не найдено"));
            validateMessageBelongsToChat(replyTo, senderId, request.getReceiverId());
            message.setReplyTo(replyTo);
        }
        Message savedMessage = messageRepository.save(message);
        log.info("Сообщение отправлено от {} к {}", senderId, request.getReceiverId());
        savedMessage.setStatus(MessageStatus.DELIVERED);
        notifyNewMessage(savedMessage);
        return convertToDTO(savedMessage, senderId);
    }

    private void validateCanMessage(User sender, User receiver) {
        if (!matchService.checkForMatch(sender, receiver)) {
            throw new NotFoundException("Вы не можете отправлять сообщения этому пользователю");
        }
    }

    private void validateMessageBelongsToChat(Message message, Long userId1, Long userId2) {
        Long fromUserId = message.getFromUser().getId();
        Long toUserId = message.getToUser().getId();

        boolean isValid = (fromUserId.equals(userId1) && toUserId.equals(userId2)) ||
                (fromUserId.equals(userId2) && toUserId.equals(userId1));

        if (!isValid) {
            throw new NotFoundException("Сообщение не принадлежит этому чату");
        }
    }

    @Transactional(readOnly = true)
    public Page<MessageDTO> getChatHistory(Long currentUserId, Long partnerId, Pageable pageable) {
        userRepository.findById(currentUserId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        userRepository.findById(partnerId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Page<Message> messages = messageRepository.findChatMessages(
                currentUserId, currentUserId, partnerId, pageable);

        markMessagesAsRead(currentUserId, partnerId);

        return messages.map(message -> convertToDTO(message, currentUserId));
    }

    public DeferredResult<ResponseEntity<List<MessageDTO>>> waitForNewMessages(
            Long userId,
            LocalDateTime lastCheck,
            Long timeoutMs) {

        DeferredResult<ResponseEntity<List<MessageDTO>>> deferredResult =
                new DeferredResult<>(timeoutMs != null ? timeoutMs : LONG_POLL_TIMEOUT_MS);

        List<MessageDTO> immediateMessages = getNewMessagesSince(userId, lastCheck);

        if (!immediateMessages.isEmpty()) {
            deferredResult.setResult(ResponseEntity.ok(immediateMessages));
            return deferredResult;
        }

        registerDeferredResult(userId, deferredResult);

        deferredResult.onTimeout(() -> {
            removeDeferredResult(userId, deferredResult);
            deferredResult.setResult(ResponseEntity.ok(Collections.emptyList()));
        });

        deferredResult.onCompletion(() -> removeDeferredResult(userId, deferredResult));
        deferredResult.onError((e) -> {
            log.error("Error in deferred result for user {}", userId, e);
            removeDeferredResult(userId, deferredResult);
            deferredResult.setErrorResult(ResponseEntity
                    .internalServerError()
                    .body("Internal server error"));
        });

        return deferredResult;
    }


    @Transactional(readOnly = true)
    public List<MessageDTO> getNewMessagesSince(Long userId, LocalDateTime since) {
        if (since == null) {
            since = LocalDateTime.now().minusDays(1);
        }

        List<Message> messages = messageRepository.findNewMessagesSince(userId, since);

        messages.stream()
                .filter(m -> m.getToUser().getId().equals(userId))
                .filter(m -> m.getStatus() != MessageStatus.READ)
                .forEach(m -> m.setStatus(MessageStatus.READ));

        return messages.stream()
                .map(m -> convertToDTO(m, userId))
                .toList();
    }


    @Transactional(readOnly = true)
    public NewMessagesCheckDTO checkNewMessages(Long userId, Map<Long, LocalDateTime> lastSeenMap) {
        LocalDateTime now = LocalDateTime.now();
        Map<Long, Integer> unreadCounts = new HashMap<>();
        Map<Long, MessageDTO> lastMessages = new HashMap<>();
        boolean hasNewMessages = false;

        for (Map.Entry<Long, LocalDateTime> entry : lastSeenMap.entrySet()) {
            Long partnerId = entry.getKey();
            LocalDateTime lastSeen = entry.getValue();

            int unreadCount = messageRepository.countUnreadMessagesFromUser(
                    userId, partnerId, lastSeen);

            if (unreadCount > 0) {
                hasNewMessages = true;
                unreadCounts.put(partnerId, unreadCount);

                // Получаем последнее сообщение
                Optional<Message> lastMessage = messageRepository
                        .findLastMessageBetweenUsers(userId, userId, partnerId);

                lastMessage.ifPresent(message ->
                        lastMessages.put(partnerId, convertToDTO(message, userId)));
            }
        }

        // Также проверяем сообщения от новых пользователей
        List<Long> newSenders = messageRepository.findNewSenders(userId,
                lastSeenMap.values().stream().max(LocalDateTime::compareTo).orElse(now.minusDays(1)));

        if (!newSenders.isEmpty()) {
            hasNewMessages = true;
            for (Long senderId : newSenders) {
                if (!lastSeenMap.containsKey(senderId)) {
                    unreadCounts.put(senderId,
                            messageRepository.countUnreadMessagesFromUser(userId, senderId, null));

                    Optional<Message> lastMessage = messageRepository
                            .findLastMessageBetweenUsers(userId, userId, senderId);

                    lastMessage.ifPresent(message ->
                            lastMessages.put(senderId, convertToDTO(message, userId)));
                }
            }
        }

        return NewMessagesCheckDTO.builder()
                .hasNewMessages(hasNewMessages)
                .unreadCounts(unreadCounts)
                .lastMessages(lastMessages)
                .checkTime(now)
                .build();
    }


    private void notifyNewMessage(Message message) {
        Long receiverId = message.getToUser().getId();
        List<DeferredResult<ResponseEntity<List<MessageDTO>>>> results =
                deferredResults.get(receiverId);

        if (results != null && !results.isEmpty()) {
            // Выполняем в отдельном потоке
            asyncExecutor.submit(() -> {
                List<MessageDTO> messageList = List.of(convertToDTO(message, receiverId));

                for (DeferredResult<ResponseEntity<List<MessageDTO>>> result : results) {
                    if (!result.isSetOrExpired()) {
                        try {
                            result.setResult(ResponseEntity.ok(messageList));
                        } catch (Exception e) {
                            log.error("Error setting deferred result", e);
                        }
                    }
                }

                // Очищаем список после уведомления
                deferredResults.remove(receiverId);
            });
        }
    }

    private void registerDeferredResult(Long userId,
                                        DeferredResult<ResponseEntity<List<MessageDTO>>> deferredResult) {

        deferredResults.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>())
                .add(deferredResult);
    }

    private void removeDeferredResult(Long userId,
                                      DeferredResult<ResponseEntity<List<MessageDTO>>> deferredResult) {

        List<DeferredResult<ResponseEntity<List<MessageDTO>>>> results =
                deferredResults.get(userId);

        if (results != null) {
            results.remove(deferredResult);
            if (results.isEmpty()) {
                deferredResults.remove(userId);
            }
        }
    }

    @Transactional
    public void markMessagesAsRead(Long userId, Long senderId) {
        LocalDateTime now = LocalDateTime.now();
        int updated = messageRepository.markMessagesAsRead(
                userId, senderId, MessageStatus.READ, now);

        if (updated > 0) {
            log.debug("Помечено {} сообщений как прочитанные для пользователя {}",
                    updated, userId);
        }
    }

    @Transactional(readOnly = true)
    public List<ChatPreviewDTO> getChats(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        List<Message> lastMessages = messageRepository.findLastMessagesForEachChat(userId);

        return lastMessages.stream()
                .map(message -> createChatPreview(message, userId))
                .toList();
    }

    private ChatPreviewDTO createChatPreview(Message lastMessage, Long currentUserId) {
        User partner = getPartnerFromMessage(lastMessage, currentUserId);

        int unreadCount = messageRepository.countUnreadMessagesFromUser(
                currentUserId, partner.getId(), null);

        return ChatPreviewDTO.builder()
                .partnerId(partner.getId())
                .partnerName(partner.getName())
                .lastMessage(lastMessage.getMessageText())
                .lastMessageTime(lastMessage.getCreatedAt())
                .unreadCount(unreadCount)
                .isOnline(false)
                .build();
    }

    private User getPartnerFromMessage(Message message, Long currentUserId) {
        return message.getFromUser().getId().equals(currentUserId)
                ? message.getToUser()
                : message.getFromUser();
    }

    @Transactional
    public void deleteMessage(Long userId, Long messageId) {
        Message message = messageRepository.findMessageForUser(messageId, userId)
                .orElseThrow(() -> new NotFoundException("Сообщение не найдено"));

        if (message.getFromUser().getId().equals(userId)) {
            message.setStatus(MessageStatus.DELETED);
        } else {
            message.setStatus(MessageStatus.DELETED);
        }

        messageRepository.save(message);
        log.info("Сообщение {} удалено для пользователя {}", messageId, userId);
    }

    @Transactional(readOnly = true)
    public int getUnreadMessagesCount(Long userId) {
        return messageRepository.countUnreadMessages(userId, MessageStatus.DELIVERED);
    }

    private MessageDTO convertToDTO(Message message, Long currentUserId) {
        MessageDTO dto = MessageDTO.builder()
                .id(message.getId())
                .fromUserId(message.getFromUser().getId())
                .fromUserName(message.getFromUser().getName())
                .toUserId(message.getToUser().getId())
                .toUserName(message.getToUser().getName())
                .messageText(message.getMessageText())
                .status(message.getStatus())
                .createdAt(message.getCreatedAt())
                .readAt(message.getReadAt())
                .build();

        if (message.getReplyTo() != null) {
            dto.setReplyToId(message.getReplyTo().getId());
            dto.setReplyToMessageText(message.getReplyTo().getMessageText());
        }

        return dto;
    }
}
