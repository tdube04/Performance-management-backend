package com.innovation.workplan.Controllers;

import com.innovation.workplan.CollectionModels.Notification;
import com.innovation.workplan.Services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/notifications")
public class NotificationController {
    
    @Autowired
    private NotificationService notificationService;
    
    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications() {
        List<Notification> notifications = notificationService.getAllNotifications();
        return ResponseEntity.ok(notifications);
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<Notification>> getActiveNotifications() {
        List<Notification> notifications = notificationService.getActiveNotifications();
        return ResponseEntity.ok(notifications);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Notification> getNotificationById(@PathVariable String id) {
        return notificationService.getNotificationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Notification> createNotification(@RequestBody Notification notification) {
        Notification created = notificationService.createNotification(notification);
        return ResponseEntity.ok(created);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Notification> updateNotification(
            @PathVariable String id, 
            @RequestBody Notification notification) {
        Notification updated = notificationService.updateNotification(id, notification);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }
    
    @PutMapping("/{id}/soft-delete")
    public ResponseEntity<Notification> softDeleteNotification(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        String deletedBy = body.get("deletedBy");
        Notification deleted = notificationService.softDeleteNotification(id, deletedBy);
        if (deleted != null) {
            return ResponseEntity.ok(deleted);
        }
        return ResponseEntity.notFound().build();
    }
    
    @PutMapping("/{id}/restore")
    public ResponseEntity<Notification> restoreNotification(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        String restoredBy = body.get("restoredBy");
        Notification restored = notificationService.restoreNotification(id, restoredBy);
        if (restored != null) {
            return ResponseEntity.ok(restored);
        }
        return ResponseEntity.notFound().build();
    }
}
