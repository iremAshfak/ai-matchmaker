package ru.petproject.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.petproject.model.Message;
import ru.petproject.model.enums.MessageStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m WHERE " +
            "((m.sender.id = :userId1 AND m.receiver.id = :userId2) OR " +
            "(m.sender.id = :userId2 AND m.receiver.id = :userId1)) AND " +
            "((:userId1 = :currentUserId AND m.deletedForSender = false) OR " +
            "(:userId2 = :currentUserId AND m.deletedForReceiver = false)) " +
            "ORDER BY m.createdAt DESC")
    Page<Message> findChatMessages(@Param("currentUserId") Long currentUserId,
                                   @Param("userId1") Long userId1,
                                   @Param("userId2") Long userId2,
                                   Pageable pageable);

    @Query("SELECT m FROM Message m WHERE " +
            "(m.receiver.id = :userId AND m.status = 'DELIVERED') AND " +
            "m.deletedForReceiver = false " +
            "ORDER BY m.createdAt DESC")
    List<Message> findUnreadMessages(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE Message m SET m.status = :status, m.readAt = :readAt " +
            "WHERE m.receiver.id = :userId AND m.sender.id = :senderId " +
            "AND m.status != 'READ'")
    int markMessagesAsRead(@Param("userId") Long userId,
                           @Param("senderId") Long senderId,
                           @Param("status") MessageStatus status,
                           @Param("readAt") LocalDateTime readAt);

    @Query("SELECT COUNT(m) FROM Message m WHERE " +
            "m.receiver.id = :userId AND m.status = :status AND " +
            "m.deletedForReceiver = false")
    int countUnreadMessages(@Param("userId") Long userId,
                            @Param("status") MessageStatus status);

    @Query("SELECT COUNT(m) FROM Message m WHERE " +
            "m.receiver.id = :userId AND m.sender.id = :senderId AND " +
            "m.status = 'DELIVERED' AND m.deletedForReceiver = false")
    int countUnreadMessagesFromUser(@Param("userId") Long userId,
                                    @Param("senderId") Long senderId);

    @Query("SELECT m FROM Message m WHERE " +
            "m.id = :messageId AND " +
            "((m.sender.id = :userId AND m.deletedForSender = false) OR " +
            "(m.receiver.id = :userId AND m.deletedForReceiver = false))")
    Optional<Message> findMessageForUser(@Param("messageId") Long messageId,
                                         @Param("userId") Long userId);

    @Query(value = """
        SELECT DISTINCT ON (least(m.sender_id, m.receiver_id), greatest(m.sender_id, m.receiver_id)) 
        m.* FROM messages m
        WHERE (m.sender_id = :userId OR m.receiver_id = :userId)
        AND ((m.sender_id = :userId AND m.deleted_for_sender = false) OR 
             (m.receiver_id = :userId AND m.deleted_for_receiver = false))
        ORDER BY least(m.sender_id, m.receiver_id), 
                 greatest(m.sender_id, m.receiver_id), 
                 m.created_at DESC
        """, nativeQuery = true)
    List<Message> findLastMessagesForEachChat(@Param("userId") Long userId);

    @Query("SELECT m FROM Message m WHERE " +
            "((m.sender.id = :userId AND m.deletedForSender = false) OR " +
            "(m.receiver.id = :userId AND m.deletedForReceiver = false)) AND " +
            "m.createdAt > :since AND " +
            "m.status != 'READ' " +
            "ORDER BY m.createdAt ASC")
    List<Message> findNewMessagesSince(@Param("userId") Long userId,
                                       @Param("since") LocalDateTime since);

    @Query("SELECT COUNT(m) FROM Message m WHERE " +
            "m.receiver.id = :userId AND m.sender.id = :senderId AND " +
            "m.status = 'DELIVERED' AND m.deletedForReceiver = false AND " +
            "(:since IS NULL OR m.createdAt > :since)")
    int countUnreadMessagesFromUser(@Param("userId") Long userId,
                                    @Param("senderId") Long senderId,
                                    @Param("since") LocalDateTime since);

    @Query("SELECT DISTINCT m.sender.id FROM Message m WHERE " +
            "m.receiver.id = :userId AND " +
            "m.deletedForReceiver = false AND " +
            "m.createdAt > :since")
    List<Long> findNewSenders(@Param("userId") Long userId,
                              @Param("since") LocalDateTime since);

    @Query("SELECT m FROM Message m WHERE " +
            "((m.sender.id = :userId1 AND m.receiver.id = :userId2) OR " +
            "(m.sender.id = :userId2 AND m.receiver.id = :userId1)) AND " +
            "((:userId1 = :currentUserId AND m.deletedForSender = false) OR " +
            "(:userId2 = :currentUserId AND m.deletedForReceiver = false)) " +
            "ORDER BY m.createdAt DESC LIMIT 1")
    Optional<Message> findLastMessageBetweenUsers(@Param("currentUserId") Long currentUserId,
                                                  @Param("userId1") Long userId1,
                                                  @Param("userId2") Long userId2);
}
