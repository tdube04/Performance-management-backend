package com.innovation.workplan.Services;


import com.innovation.workplan.CollectionModels.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.servlet.ServletException;
import java.util.List;

public interface UserEntityService {
    String signUp(UserEntity user) throws ServletException;
    UserEntity getUser( String id);


    UserEntity findByUsername(String username);

    Page<UserEntity> searchUser(String id, String name, Pageable pageable);

    UserEntity getUserByUsername(String username);

    String save(UserEntity user);




    void changePassword(String oldPassword, String newPassword, String confirmNewPassword) throws Exception;

    void createPasswordResetTokenForUser(String email) throws Exception;

    void getByResetPasswordToken(String token, String newPassword, String confirmNewPassword) throws Exception;

    void deleteUser(String email);

    UserEntity update(UserEntity user);

    String assignAppraiser(String userEmail,String appraiserEmail);

    String deleteAppraiser(String userEmail);

    String assignAppraisees( String appraiseesEmails);

    String updateAppraisees(String userEmail, String appraiseesEmails);


    List<String> getAppraisees(String userEmail);


    List<UserEntity> getAllUsers();


    Page<UserEntity> searchUserByDivisionOrSection(String divisionName,String sectionName,Pageable pageable);

    Page<UserEntity> searchUserByGrade(String grade, String division, Pageable pageable);


//    List<UserEntity> getBySection(String sectionName);
}
