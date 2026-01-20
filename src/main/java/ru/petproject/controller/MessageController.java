package ru.petproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import ru.petproject.dto.*;
import ru.petproject.model.User;
import ru.petproject.service.MessageService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
@Slf4j
public class MessageController {
    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<MessageDTO> sendMessage(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody SendMessageRequest request) {
        MessageDTO message = messageService.sendMessage(currentUser.getId(), request);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/chat/{partnerId}")
    public ResponseEntity<Page<MessageDTO>> getChatHistory(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long partnerId,
            @PageableDefault(size = 50) Pageable pageable) {
        Page<MessageDTO> messages = messageService.getChatHistory(
                currentUser.getId(), partnerId, pageable);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/wait")
    public DeferredResult<ResponseEntity<List<MessageDTO>>> waitForNewMessages(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime lastCheck,
            @RequestParam(required = false) Long timeoutMs) {
        log.debug("Long polling запрос от пользователя {}, lastCheck: {}",
                currentUser.getId(), lastCheck);
        return messageService.waitForNewMessages(
                currentUser.getId(),
                lastCheck,
                timeoutMs
        );
    }

    @GetMapping("/check")
    public ResponseEntity<NewMessagesCheckDTO> checkNewMessages(
            @AuthenticationPrincipal User currentUser,
            @RequestParam Map<String, String> lastSeenParams) {
        Map<Long, LocalDateTime> lastSeenMap = convertLastSeenParams(lastSeenParams);
        NewMessagesCheckDTO result = messageService.checkNewMessages(
                currentUser.getId(),
                lastSeenMap
        );
        return ResponseEntity.ok(result);
    }

    @GetMapping("/new")
    public ResponseEntity<List<MessageDTO>> getNewMessages(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime since) {
        List<MessageDTO> messages = messageService.getNewMessagesSince(
                currentUser.getId(),
                since
        );
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/chats")
    public ResponseEntity<List<ChatPreviewDTO>> getChats(
            @AuthenticationPrincipal User currentUser) {
        List<ChatPreviewDTO> chats = messageService.getChats(currentUser.getId());
        return ResponseEntity.ok(chats);
    }

    @GetMapping("/unread/count")
    public ResponseEntity<Integer> getUnreadCount(
            @AuthenticationPrincipal User currentUser) {
        int count = messageService.getUnreadMessagesCount(currentUser.getId());
        return ResponseEntity.ok(count);
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteMessage(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long messageId) {
        messageService.deleteMessage(currentUser.getId(), messageId);
        return ResponseEntity.noContent().build();
    }

    private Map<Long, LocalDateTime> convertLastSeenParams(Map<String, String> params) {
        Map<Long, LocalDateTime> result = new java.util.HashMap<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            try {
                Long partnerId = Long.parseLong(entry.getKey());
                LocalDateTime lastSeen = LocalDateTime.parse(entry.getValue());
                result.put(partnerId, lastSeen);
            } catch (Exception e) {
                log.warn("Неверный формат параметра: {}={}",
                        entry.getKey(), entry.getValue());
            }
        }
        return result;
    }
}
