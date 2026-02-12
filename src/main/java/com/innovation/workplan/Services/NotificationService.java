package com.innovation.workplan.Services;

import com.innovation.workplan.CollectionModels.Notification;
import com.innovation.workplan.Repositories.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
    
    // Get notifications visible to a specific user role
    public List<Notification> getNotificationsVisibleTo(String userRole) {
        List<Notification> allActive = notificationRepository.findByIsActiveTrueAndIsDeletedFalse();
        LocalDate now = LocalDate.now();
        
        return allActive.stream()
            .filter(n -> {
                // Check date range
                if (n.getStartDate() != null && now.isBefore(n.getStartDate())) {
                    return false;
                }
                if (n.getEndDate() != null && now.isAfter(n.getEndDate())) {
                    return false;
                }
                
                // For backward compatibility: if no visibility fields are set, show to all
                boolean hasVisibilitySettings = n.isVisibleToAll() || n.isVisibleToAppraisees() 
                    || n.isVisibleToAppraisers() || n.isVisibleToHC() || n.isVisibleToAdmin();
                
                if (!hasVisibilitySettings) {
                    // Old notifications without visibility settings - show to everyone
                    return true;
                }
                
                // Check visibility based on user role
                if (n.isVisibleToAll()) {
                    return true;
                }
                
                switch (userRole.toLowerCase()) {
                    case "hc":
                        return n.isVisibleToHC();
                    case "admin":
                        return n.isVisibleToAdmin();
                    case "appraisee":
                        return n.isVisibleToAppraisees();
                    case "appraiser":
                        return n.isVisibleToAppraisers();
                    default:
                        return n.isVisibleToAll();
                }
            })
            .toList();
    }
    
    public Notification createNotification(Notification notification) {
        notification.setCreatedAt(LocalDateTime.now());
        notification.setDeleted(false);
        notification.setActive(true);
        
        // Set default visibility values if not provided
        if (!notification.isVisibleToAll() && 
            !notification.isVisibleToAppraisees() && 
            !notification.isVisibleToAppraisers() && 
            !notification.isVisibleToHC() && 
            !notification.isVisibleToAdmin()) {
            notification.setVisibleToAll(true);
            notification.setVisibleToAppraisees(true);
            notification.setVisibleToAppraisers(true);
            notification.setVisibleToHC(true);
            notification.setVisibleToAdmin(true);
        }
        
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
            
            // Update visibility fields
            updated.setVisibleToAll(notification.isVisibleToAll());
            updated.setVisibleToAppraisees(notification.isVisibleToAppraisees());
            updated.setVisibleToAppraisers(notification.isVisibleToAppraisers());
            updated.setVisibleToHC(notification.isVisibleToHC());
            updated.setVisibleToAdmin(notification.isVisibleToAdmin());
            
            updated.setUpdatedAt(LocalDateTime.now());
            return notificationRepository.save(updated);
        }
        return null;
    }
    
    // Update only visibility settings
    public Notification updateVisibility(String id, boolean visibleToAll, boolean visibleToAppraisees, 
                                       boolean visibleToAppraisers, boolean visibleToHC, boolean visibleToAdmin) {
        Optional<Notification> existing = notificationRepository.findById(id);
        if (existing.isPresent()) {
            Notification updated = existing.get();
            updated.setVisibleToAll(visibleToAll);
            updated.setVisibleToAppraisees(visibleToAppraisees);
            updated.setVisibleToAppraisers(visibleToAppraisers);
            updated.setVisibleToHC(visibleToHC);
            updated.setVisibleToAdmin(visibleToAdmin);
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
