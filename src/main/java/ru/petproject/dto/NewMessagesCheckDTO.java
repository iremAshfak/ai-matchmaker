package ru.petproject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewMessagesCheckDTO {
    private boolean hasNewMessages;
    private Map<Long, Integer> unreadCounts;
    private Map<Long, MessageDTO> lastMessages;
    private LocalDateTime checkTime;
    private int totalUnread;

    public int getTotalUnread() {
        return unreadCounts.values().stream().mapToInt(Integer::intValue).sum();
    }
}
