package com.innovation.workplan.Repositories;

import com.innovation.workplan.CollectionModels.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {
    
    List<Notification> findByIsActiveTrueAndIsDeletedFalse();
    
    List<Notification> findByIsDeletedFalse();
    
    List<Notification> findByTargetAudienceAndIsDeletedFalse(String targetAudience);
    
    List<Notification> findByIsActiveTrueAndIsDeletedFalseAndStartDateBeforeAndEndDateAfter(
        java.time.LocalDateTime now, java.time.LocalDateTime now2);
}
