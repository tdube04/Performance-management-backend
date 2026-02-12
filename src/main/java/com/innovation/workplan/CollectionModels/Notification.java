package com.innovation.workplan.CollectionModels;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "notifications")
public class Notification {
    
    @Id
    private String id;
    
    private String title;
    private String message;
    private String priority; // low, normal, medium, high
    private String targetAudience; // all, admin, hc, appraisees, appraisers
    private boolean isActive;
    
    // Visibility controls - HC can toggle these
    private boolean visibleToAll;
    private boolean visibleToAppraisees;
    private boolean visibleToAppraisers;
    private boolean visibleToHC;
    private boolean visibleToAdmin;
    
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
    private String deletedBy;
    private LocalDateTime deletedAt;
    private boolean isDeleted;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    
    public Notification() {
        this.isActive = true;
        this.isDeleted = false;
        this.visibleToAll = true;
        this.visibleToAppraisees = true;
        this.visibleToAppraisers = true;
        this.visibleToHC = true;
        this.visibleToAdmin = true;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getPriority() {
        return priority;
    }
    
    public void setPriority(String priority) {
        this.priority = priority;
    }
    
    public String getTargetAudience() {
        return targetAudience;
    }
    
    public void setTargetAudience(String targetAudience) {
        this.targetAudience = targetAudience;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    public boolean isVisibleToAll() {
        return visibleToAll;
    }
    
    public void setVisibleToAll(boolean visibleToAll) {
        this.visibleToAll = visibleToAll;
    }
    
    public boolean isVisibleToAppraisees() {
        return visibleToAppraisees;
    }
    
    public void setVisibleToAppraisees(boolean visibleToAppraisees) {
        this.visibleToAppraisees = visibleToAppraisees;
    }
    
    public boolean isVisibleToAppraisers() {
        return visibleToAppraisers;
    }
    
    public void setVisibleToAppraisers(boolean visibleToAppraisers) {
        this.visibleToAppraisers = visibleToAppraisers;
    }
    
    public boolean isVisibleToHC() {
        return visibleToHC;
    }
    
    public void setVisibleToHC(boolean visibleToHC) {
        this.visibleToHC = visibleToHC;
    }
    
    public boolean isVisibleToAdmin() {
        return visibleToAdmin;
    }
    
    public void setVisibleToAdmin(boolean visibleToAdmin) {
        this.visibleToAdmin = visibleToAdmin;
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getUpdatedBy() {
        return updatedBy;
    }
    
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public String getDeletedBy() {
        return deletedBy;
    }
    
    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }
    
    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }
    
    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
    
    public boolean isDeleted() {
        return isDeleted;
    }
    
    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    // Helper method to check if notification is visible to a specific audience
    public boolean isVisibleTo(String userRole) {
        if (!isActive || isDeleted) {
            return false;
        }
        
        // Check date range
        LocalDate now = LocalDate.now();
        if (startDate != null && now.isBefore(startDate)) {
            return false;
        }
        if (endDate != null && now.isAfter(endDate)) {
            return false;
        }
        
        // Check visibility based on user role
        switch (userRole.toLowerCase()) {
            case "hc":
                return visibleToAll || visibleToHC;
            case "admin":
                return visibleToAll || visibleToAdmin;
            case "appraisee":
                return visibleToAll || visibleToAppraisees;
            case "appraiser":
                return visibleToAll || visibleToAppraisers;
            default:
                return visibleToAll;
        }
    }
}
