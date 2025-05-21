package com.innovation.workplan.ServiceImplementations;



import com.innovation.workplan.CollectionModels.UserEntity;
import com.innovation.workplan.CollectionModels.UserGroup;
import com.innovation.workplan.Repositories.UserEntityRepository;
import com.innovation.workplan.Repositories.UserGroupRepository;
import com.innovation.workplan.Services.EmailService;
import com.innovation.workplan.Services.UserEntityService;
import com.innovation.workplan.Utilities.JWTUtility;
import com.innovation.workplan.Utilities.PrincipalHelper;
import com.innovation.workplan.Utilities.SecurityUtils;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service

public class UserEntityServiceImpl implements UserEntityService {
    @Autowired
    private UserEntityRepository userEntityRepository;
    private static final long EXPIRE_TOKEN_AFTER_MINUTES = 30;

    @Autowired
    private  UserGroupRepository userGroupRepository;
    @Autowired
    public MyCustomUserDetailsService myCustomUserDetailsService1;

    @Autowired
    private PrincipalHelper principalUtil;

    @Autowired
    private HttpServletRequest httpServletRequest;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private JWTUtility userjwtUtility;

    @Autowired
    public MyCustomUserDetailsService myCustomUserDetailsService;

    @Autowired

    private  EmailService emailService;

    @Autowired
    @Lazy
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public String save(UserEntity user) {
        String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}";
        //Compile regular expression to get the pattern
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(user.getEmail());
        Boolean emailValidator=matcher.matches();
        String non_numbers_regex="^[A-Za-z]";


        String grade_regex="^[0-9]";
        Pattern string_pattern = Pattern.compile(non_numbers_regex);
        Matcher string_matcher=string_pattern.matcher(user.getDivisionName());
        Matcher string_matcher1=string_pattern.matcher(user.getSectionName());

        Pattern grade_pattern = Pattern.compile(grade_regex);
        Matcher grade_matcher = grade_pattern.matcher(user.getGrade());
        Matcher ec_matcher=grade_pattern.matcher(user.getEc_number());
        Boolean ecvalidator=ec_matcher.matches();
        Boolean gradeValidator=grade_matcher.matches();
        Boolean Divisionvalidator=string_matcher.matches();
        Boolean sectionValidator=string_matcher1.matches();
//        if(user.getEmail()==null | emailValidator==false){
//            return "Not correct email format";
//        } if (user.getUserRole()==null) {
//            user.setUserRole("USER");
//        }
//        if (ecvalidator==false ) {
//            return "Please put correct ec number as numbers only";
//        }
//        if(Integer.parseInt(user.getGrade())>2){
//            if(user.getDivisionName()==null|Divisionvalidator==false){
//                return "Please put valid string Division name";
//            }
//            if(user.getSectionName()==null|sectionValidator==false){
//                return "Please put valid string Section name";
//            }
//        }

//        if (user.getGrade()==null|gradeValidator==false) {
//            return "Please put your Grade as numbers only";
//        }
//        user.setUserRole("USER");
        user.setAppraiser_status("UnAssigned");

        return userEntityRepository.save(user).getUsername();
    }

    @Override
    public String signUp(UserEntity user) throws ServletException {
        String id= save(user);
        UserGroup userGroup=userGroupRepository.findById("USER").orElse(null);

        if(id!=null){
            UserDetails userDetails=myCustomUserDetailsService.loadUserByUsername(user.getUsername());

//            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
//                    = new UsernamePasswordAuthenticationToken(userDetails,
//                    null, userGroup.getPermissions().
//                    parallelStream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
////
//            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

            return("Bearer "+userjwtUtility.generateToken(userDetails));

        }
        return "unable to save user";
    }

    @Override
    public UserEntity getUser(String username) {

        return userEntityRepository.findById(username).orElse(null);
    }

    @Override
    public UserEntity findByUsername(String username) {
        return userEntityRepository.findById(username).orElse(null);
    }


    @Override
    public Page<UserEntity> searchUser(String id, String name, Pageable pageable) {

        Query query=new Query().with(pageable);
        List<Criteria> criteria= new ArrayList<>();
        if(id!=null ){
            criteria.add(Criteria.where("id").is(id));
        }



        if(name!=null && !name.isEmpty()){
            criteria.add(Criteria.where("stations.name").regex(name,"i"));
        }

        if(!criteria.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteria.toArray(new Criteria[0])));
        }
        Page<UserEntity> users= PageableExecutionUtils.getPage(mongoTemplate.find(query,UserEntity.class),
                pageable,()->mongoTemplate
                        .count(query.skip(0).limit(0),UserEntity.class));
        return users;
    }

    @Override
    public UserEntity getUserByUsername(String username) {
        return userEntityRepository.findByUsername(username).orElseThrow();
    }

    @Override
    public void changePassword(String oldPassword, String newPassword, String confirmNewPassword) throws Exception {

    }

//    @Override
//    public void changePassword(String oldPassword, String newPassword, String confirmNewPassword) throws Exception {
//        UserEntity user = principalUtil.getPrincipal();
//        if (!Objects.equals(newPassword, confirmNewPassword))
//            throw new Exception("Passwords Do Not Match");
////        if(passwordEncoder.matches(oldPassword, user.getPassword()))
//
//        if (oldPassword.equals(user.getPassword())) {
//            user.setPassword(newPassword);
//            userEntityRepository.save(user);
//        } else throw new Exception("Incorrect Password ");
//    }

    @Override
    public void createPasswordResetTokenForUser(String email) throws Exception {

//        deleteExpiredTokens();

        UserEntity user = userEntityRepository.findByUsername(email).orElseThrow();
        String token = RandomString.make(20);

        user.setResetToken(token);
        user.setReset_token_expiry_date(LocalDateTime.now().plusMinutes(20));
        //String username = user.getUsername();

//        emailService.sendSimpleMessage(email, PASSWORD_RESET_SUBJECT, PASSWORD_RESET_MESSAGE + token + "\n" + " \n ");
        emailService.sendSimpleMessage(email, "Password Reset ", "Good day kindly find your password reset token:"+ "\n" + token  + " \n ");
        userEntityRepository.save(user);
    }

    @Override
    public void getByResetPasswordToken(String token, String newPassword, String confirmNewPassword) throws Exception {
        if (!Objects.equals(newPassword,confirmNewPassword))
            throw new Exception("Passwords Do Not Match");

//        deleteExpiredTokens();

//        String token = user.getToken();
//        String password = request.getNewPassword();

        //checking if the token exist in the database
        UserEntity user = userEntityRepository.findByresetToken(token).orElse(null);

        if(user!=null) {

//
//            user.setPassword(bCryptPasswordEncoder.encode(newPassword));

            user.setResetToken(null);
            userEntityRepository.save(user);
//        User user = passwordResetToken.getUser();
//        updatePassword(user, password);

        }



    }

    @Override
    public void deleteUser(String email) {
        userEntityRepository.deleteById(email);
    }


    public  UserEntity update(UserEntity user){

        Query query = new Query();
        query.addCriteria(Criteria.where("username").is(user.getUsername()));
        Update updateUser = new Update();
        updateUser.set("email", user.getEmail());
        updateUser.set("ec_number",user.getEc_number());
        updateUser.set("grade", user.getGrade());
        updateUser.set("name", user.getName());
        updateUser.set("password", user.getPassword());

        updateUser.set("surname", user.getSurname());
        updateUser.set("positionName",user.getPositionName());
        updateUser.set("divisionName",user.getDivisionName());
        updateUser.set("sectionName",user.getSectionName());
        updateUser.set("userRole", user.getUserRole());
        updateUser.set("signature",user.getSignature());
        updateUser.set("signatureStatus",user.getSignatureStatus());
        updateUser.set("enabled",user.isEnabled());

        updateUser.set("appraisees",user.getAppraisees());
        updateUser.set("appraiserEmail", user.getAppraiserEmail());
        updateUser.set("appraiser_status",user.getAppraiser_status());
        updateUser.set("resetToken",user.getResetToken());
        updateUser.set("reset_token_expiry_date",user.getReset_token_expiry_date());
        updateUser.set("token_status",user.getToken_status());


        return mongoTemplate.findAndModify(query, updateUser, new FindAndModifyOptions().returnNew(true), UserEntity.class);
    }


    @Override
    public String assignAppraiser(String userEmail, String appraiserEmail) {
        UserEntity user=userEntityRepository.findById(userEmail).orElse(null);
        user.setAppraiserEmail(appraiserEmail);
        user.setAppraiser_status("Assigned");
        update(user);
        return "Assigned";



    }

    @Override
    public String deleteAppraiser(String userEmail) {
        UserEntity user=userEntityRepository.findById(userEmail).orElse(null);
        if(user!=null&& (user.getAppraiserEmail()!=null|user.getAppraiserEmail()=="Assigned")){
            user.setAppraiserEmail(null);
            user.setAppraiser_status("UnAssigned");
            return "Appraiser Removed";
        }
        else{
            return "Could not Delete";
        }

    }
    // below is for Profile appraisees crud
    @Override
    public String assignAppraisees(String appraiseeEmail) {
        String user_email= SecurityUtils.getCurrentUserLogin().get().toString();
        UserEntity user=userEntityRepository.findById(user_email).orElse(null);
        UserEntity aprraisee= userEntityRepository.findById(appraiseeEmail).orElse(null);
        if(aprraisee!=null&& aprraisee.getAppraiser_status().toString().equalsIgnoreCase("UnAssigned")){
            if(!user.getAppraisees().contains(appraiseeEmail)) {
                Query query = new Query();
                query.addCriteria(Criteria.where("username").is(user.getUsername()));
                Update updateUser = new Update();
                updateUser.set("email", user.getEmail());
                updateUser.set("ec_number", user.getEc_number());
                updateUser.set("grade", user.getGrade());
                updateUser.set("name", user.getName());
                updateUser.set("password", user.getPassword());

                updateUser.set("surname", user.getSurname());
                updateUser.set("positionName", user.getPositionName());
                updateUser.set("divisionName", user.getDivisionName());
                updateUser.set("sectionName", user.getSectionName());
                updateUser.set("userRole", user.getUserRole());
                updateUser.set("signature", user.getSignature());
                updateUser.set("signatureStatus", user.getSignatureStatus());
                updateUser.set("enabled", user.isEnabled());
                List<String> subordinates=user.getAppraisees();
                subordinates.add(appraiseeEmail);
                updateUser.set("appraisees", subordinates);
                updateUser.set("appraiserEmail", user.getAppraiserEmail());
                updateUser.set("appraiser_status", user.getAppraiser_status());
                updateUser.set("resetToken", user.getResetToken());
                updateUser.set("reset_token_expiry_date", user.getReset_token_expiry_date());
                updateUser.set("token_status", user.getToken_status());
                mongoTemplate.findAndModify(query, updateUser, new FindAndModifyOptions().returnNew(true), UserEntity.class);

                for(String email:user.getAppraisees()){
                    UserEntity each_appraisee=userEntityRepository.findById(email).orElse(null);
                    if(each_appraisee!=null){
                        Query query1 = new Query();
                        query1.addCriteria(Criteria.where("username").is(each_appraisee.getUsername()));
                        Update updateUser1 = new Update();
                        updateUser1.set("email", each_appraisee.getEmail());
                        updateUser1.set("ec_number", each_appraisee.getEc_number());
                        updateUser1.set("grade", each_appraisee.getGrade());
                        updateUser1.set("name", each_appraisee.getName());
                        updateUser1.set("password", each_appraisee.getPassword());

                        updateUser1.set("surname", each_appraisee.getSurname());
                        updateUser1.set("positionName", each_appraisee.getPositionName());
                        updateUser1.set("divisionName", each_appraisee.getDivisionName());
                        updateUser1.set("sectionName", each_appraisee.getSectionName());
                        updateUser1.set("userRole", each_appraisee.getUserRole());
                        updateUser1.set("signature", each_appraisee.getSignature());
                        updateUser1.set("signatureStatus", each_appraisee.getSignatureStatus());
                        updateUser1.set("enabled", each_appraisee.isEnabled());
                        updateUser1.set("appraisees", each_appraisee.getAppraisees());
                        updateUser1.set("appraiserEmail", user_email);
                        updateUser1.set("appraiser_status", "Assigned");
                        updateUser1.set("resetToken", each_appraisee.getResetToken());
                        updateUser1.set("reset_token_expiry_date", each_appraisee.getReset_token_expiry_date());
                        updateUser1.set("token_status", each_appraisee.getToken_status());
                        mongoTemplate.findAndModify(query1, updateUser1, new FindAndModifyOptions().returnNew(true), UserEntity.class);
                    }
                }
                return "successfully assigned";
            }
        }




        return "Unable to assign";

    }




    @Override
    public String updateAppraisees(String userEmail, String appraiseesEmails) {
        UserEntity user=userEntityRepository.findById(userEmail).orElse(null);
        return "updated";
    }

    @Override
    public List<String> getAppraisees(String userEmail) {
        UserEntity user=userEntityRepository.findById(userEmail).orElse(null);
        if(user!=null){
            return user.getAppraisees();

        }

        return null;
    }

    @Override
    public List<UserEntity> getAllUsers() {
        return userEntityRepository.findAll();
    }

    @Override
    public Page<UserEntity> searchUserByDivisionOrSection(String Division, String Section, Pageable pageable) {

        Query query=new Query().with(pageable);
        List<Criteria> criteria= new ArrayList<>();
        if(Division!=null ){
            criteria.add(Criteria.where("divisionName").is(Division));
        }



        if(Section!=null && !Section.isEmpty()){
            criteria.add(Criteria.where("sectionName").regex(Section,"i"));
        }

        if(!criteria.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteria.toArray(new Criteria[0])));
        }
        Page<UserEntity> users= PageableExecutionUtils.getPage(mongoTemplate.find(query,UserEntity.class),
                pageable,()->mongoTemplate
                        .count(query.skip(0).limit(0),UserEntity.class));
        return users;
    }

    @Override
    public Page<UserEntity> searchUserByGrade(String grade, String division, Pageable pageable) {
        Query query=new Query().with(pageable);
        List<Criteria> criteria= new ArrayList<>();
        if(grade!=null ){
            criteria.add(Criteria.where("grade").is(grade));
        }



        if(division!=null && !division.isEmpty()){
            criteria.add(Criteria.where("divisionName").regex(division,"i"));
        }

        if(!criteria.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteria.toArray(new Criteria[0])));
        }
        Page<UserEntity> users= PageableExecutionUtils.getPage(mongoTemplate.find(query,UserEntity.class),
                pageable,()->mongoTemplate
                        .count(query.skip(0).limit(0),UserEntity.class));
        return users;
    }

//    @Override
//    public List<UserEntity> getBySection(String sectionName) {
//        return userEntityRepository.findBySection(sectionName);
//    }


    public void deleteExpiredTokens() throws Exception {
        try {
            List<UserEntity> users = userEntityRepository.findAll();
            for (UserEntity user : users) {
                String token = user.getResetToken();
                LocalDateTime expiryday = user.getReset_token_expiry_date();
                if (expiryday.isBefore(LocalDateTime.now())) {
                    user.setResetToken(null);
                    userEntityRepository.save(user);
//                passwordResetTokenRepository.delete(token);
                }
            }
        }
        catch (Exception e){
            throw new Exception("Could not find Any User");
        }
    }

//    public void updatePassword(UserEntity user, String newPassword) {
//        user.setPassword((newPassword));
//    }


}
