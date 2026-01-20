package ru.petproject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatPreviewDTO {
    private Long partnerId;
    private String partnerName;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private int unreadCount;
    private boolean isOnline;
    private LocalDateTime lastSeen;
    private Long matchId; // ID мэтча с этим пользователем (опционально)
    private LocalDateTime matchCreatedAt; // Когда был создан мэтч
    private boolean isBlocked; // Заблокирован ли пользователь
    private boolean hasBlockedYou; // Заблокировал ли вас пользователь


    // Для групповых чатов
    private Long chatId;
    private String chatName;
    private String chatAvatar;
    private Integer participantsCount;
    private Boolean isGroupChat;
}
