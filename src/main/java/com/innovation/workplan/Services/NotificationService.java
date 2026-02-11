package com.innovation.workplan.Services;

import com.innovation.workplan.CollectionModels.Notification;
import com.innovation.workplan.Repositories.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    public List<Notification> getAllNotifications() {
        return notificationRepository.findByIsDeletedFalse();
    }
    
    public List<Notification> getActiveNotifications() {
        return notificationRepository.findByIsActiveTrueAndIsDeletedFalse();
    }
    
    public List<Notification> getNotificationsByAudience(String audience) {
        return notificationRepository.findByTargetAudienceAndIsDeletedFalse(audience);
    }
    
    public Optional<Notification> getNotificationById(String id) {
        return notificationRepository.findById(id);
    }
    
    public Notification createNotification(Notification notification) {
        notification.setCreatedAt(LocalDateTime.now());
        notification.setDeleted(false);
        notification.setActive(true);
        return notificationRepository.save(notification);
    }
    
    public Notification updateNotification(String id, Notification notification) {
        Optional<Notification> existing = notificationRepository.findById(id);
        if (existing.isPresent()) {
            Notification updated = existing.get();
            updated.setTitle(notification.getTitle());
            updated.setMessage(notification.getMessage());
            updated.setPriority(notification.getPriority());
            updated.setTargetAudience(notification.getTargetAudience());
            updated.setActive(notification.isActive());
            updated.setStartDate(notification.getStartDate());
            updated.setEndDate(notification.getEndDate());
            updated.setUpdatedAt(LocalDateTime.now());
            return notificationRepository.save(updated);
        }
        return null;
    }
    
    public Notification softDeleteNotification(String id, String deletedBy) {
        Optional<Notification> existing = notificationRepository.findById(id);
        if (existing.isPresent()) {
            Notification notification = existing.get();
            notification.setDeleted(true);
            notification.setDeletedAt(LocalDateTime.now());
            notification.setDeletedBy(deletedBy);
            return notificationRepository.save(notification);
        }
        return null;
    }
    
    public Notification restoreNotification(String id, String restoredBy) {
        Optional<Notification> existing = notificationRepository.findById(id);
        if (existing.isPresent()) {
            Notification notification = existing.get();
            notification.setDeleted(false);
            notification.setDeletedAt(null);
            notification.setDeletedBy(null);
            notification.setUpdatedAt(LocalDateTime.now());
            notification.setUpdatedBy(restoredBy);
            return notificationRepository.save(notification);
        }
        return null;
    }
}
