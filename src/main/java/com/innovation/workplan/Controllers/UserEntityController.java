package com.innovation.workplan.Controllers;

import com.innovation.workplan.CollectionModels.*;

import com.innovation.workplan.Repositories.UserEntityRepository;
import com.innovation.workplan.Repositories.UserGroupRepository;
import com.innovation.workplan.ServiceImplementations.MyCustomUserDetailsService;
import com.innovation.workplan.Services.UserEntityService;
import com.innovation.workplan.Utilities.JWTUtility;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.ServletException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:8080" , maxAge = 36000)
//@RequestMapping("/user")

public class UserEntityController {

    RestTemplate restTemplate=new RestTemplate();
    UserDetails userDetails;

    String BASE_URL="http://10.16.83.32:8087/api/auth";

    private static final int EXPIRATION = 60 * 24;


    @Autowired
    private JWTUtility jwtUtility;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    UserEntityRepository userEntityRepository;

    @Autowired
    private final UserGroupRepository userGroupRepository;

    private JwtResponse authenticationResponse;

    @Autowired
    private UserEntityService userEntityService;



    @Autowired
    public MyCustomUserDetailsService myCustomUserDetailsService;


    @PostMapping("/saveUser")
//    @PreAuthorize("hasAnyAuthority('SAVE_USER')")
    @Operation(summary = "save a user into the database")
    public String saveUser(@RequestBody UserEntity user) throws ServletException {


            return userEntityService.signUp(user);
    }
    @GetMapping("/User/{id}")
    @PreAuthorize("hasAnyAuthority('GET_USER')")
    @Operation(summary = "get a user from the database")
    public UserEntity getUser(@RequestParam String id){

                UserEntity u =userEntityService.getUser(id);
                if(u!=null){
                    return u;
                }
                return new UserEntity();
    }
//    @PostMapping("/adminlogin")
//    @Operation(summary = "authenticates admin users ")
//    public ResponseEntity<?> adminlogin(@RequestBody JwtRequest authenticationRequest) throws Exception {
//        UserEntity ue=userEntityRepository.findById(authenticationRequest.getUsername()).orElse(null);
//        if(ue!=null){
//            ue.setLogAs("admin");
//            userEntityRepository.save(ue);
//        }
//        if(userEntityService.getUser(authenticationRequest.getUsername()).getUserRole().contains("ADMIN")){
//
//        try{
//            Boolean activeDirectory = restTemplate.getForObject(BASE_URL+"?"+"Username="+authenticationRequest.getUsername()
//                    +"&"+"Password="+authenticationRequest.getPassword(),Boolean.class);
//            authenticationResponse=new JwtResponse();
//            if(Boolean.TRUE.equals(activeDirectory)){
//                UserDetails userDetails=myCustomUserDetailsService.loadUserByUsername(authenticationRequest.getUsername());
//
//                authenticationResponse.setJwtToken("Bearer "+jwtUtility.generateToken(userDetails));
//            }
//            else {
//                authenticationResponse.setJwtToken(null);
//
//            }
//        }
//        catch(BadCredentialsException e){
//            throw  new Exception("Invalid Credentials",e);
//
//        }
//        }
//        return ResponseEntity.ok(authenticationResponse);
//    }

@PostMapping("/adminlogin")
@Operation(summary = "authenticates admin users ")
public ResponseEntity<?> adminlogin(@RequestBody JwtRequest authenticationRequest) throws Exception {
    UserEntity ue = userEntityRepository.findById(authenticationRequest.getUsername()).orElse(null);
    if (ue != null) {
        ue.setLogAs("admin");
        userEntityRepository.save(ue);
    }
    if (userEntityService.getUser(authenticationRequest.getUsername()).getUserRole().contains("ADMIN")) {
        try {
            // Change from GET to POST
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<JwtRequest> requestEntity = new HttpEntity<>(authenticationRequest, headers);

            Boolean activeDirectory = restTemplate.postForObject(
                    BASE_URL,
                    requestEntity,
                    Boolean.class
            );

            authenticationResponse = new JwtResponse();
            if (Boolean.TRUE.equals(activeDirectory)) {
                UserDetails userDetails = myCustomUserDetailsService.loadUserByUsername(authenticationRequest.getUsername());
                authenticationResponse.setJwtToken("Bearer " + jwtUtility.generateToken(userDetails));
            } else {
                authenticationResponse.setJwtToken(null);
            }
        } catch (BadCredentialsException e) {
            throw new Exception("Invalid Credentials", e);
        }
    }
    return ResponseEntity.ok(authenticationResponse);
}

    @PostMapping("/login")
    @Operation(summary = "authenticates user and log in ")
    public ResponseEntity<?> login(@RequestBody JwtRequest authenticationRequest) throws Exception {
        UserEntity ue = userEntityService.getUser(authenticationRequest.getUsername());

        if (ue != null) {
            ue.setLogAs("user");
            userEntityRepository.save(ue);

            authenticationResponse = new JwtResponse();
            try {
                // Change from GET to POST
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<JwtRequest> requestEntity = new HttpEntity<>(authenticationRequest, headers);

                Boolean activeDirectory = restTemplate.postForObject(
                        BASE_URL,
                        requestEntity,
                        Boolean.class
                );

                if (Boolean.TRUE.equals(activeDirectory)) {
                    userDetails = myCustomUserDetailsService.loadUserByUsername(authenticationRequest.getUsername());
                    authenticationResponse.setJwtToken("Bearer " + jwtUtility.generateToken(userDetails));
                } else if (Integer.parseInt(ue.getGrade()) == 0 && authenticationRequest.getUsername().equals(ue.getUsername())) {
                    if (ue.getPassword().equals(authenticationRequest.getPassword())) {
                        userDetails = myCustomUserDetailsService.loadUserByUsername(authenticationRequest.getUsername());
                        authenticationResponse.setJwtToken("Bearer " + jwtUtility.generateToken(userDetails));
                    }
                } else {
                    authenticationResponse.setJwtToken(null);
                }
            } catch (BadCredentialsException e) {
                throw new Exception("Invalid Credentials", e);
            }
        }
        return ResponseEntity.ok(authenticationResponse);
    }

//    @PostMapping("/forget-password/{email}")
//    @Operation(summary = "generate token and send to email for password reset")
//    public ResponseEntity<Void> processForgotPassword(@PathVariable(value = "email") String email) throws Exception {
//        userEntityService.createPasswordResetTokenForUser(email);
//
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//    }

//    @PutMapping("/change-password")
//    @Operation(summary = "change user password for the principal")
//    public ResponseEntity<Void> changePassword(@RequestParam(value = "OldPassword") String oldPassword,@RequestParam(value = "NewPassword") String newPassword,@RequestParam(value = "ConfirmPassword") String ConfirmNewPassword) throws Exception {
//        userEntityService.changePassword(oldPassword,newPassword,ConfirmNewPassword);
//
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//    }

//    @PutMapping("/reset-password")
//    @Operation(summary = "use the generated token from email to reset password")
//    public ResponseEntity<Void> resetForgotPassword(@RequestParam(value = "token") String token,
//                                                    @RequestParam(value = "NewPassword") String newPassword,
//                                                    @RequestParam(value = "ConfirmPassword") String confirmNewPassword) throws Exception {
//        userEntityService.getByResetPasswordToken(token,newPassword,confirmNewPassword);
//
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//    }


    @GetMapping("/searchUser")
    @PreAuthorize("hasAnyAuthority('SEARCH_USER')")
    @Operation(summary = "search user in the database using  user_id or name")
    public Page<UserEntity> searchUser(
            @RequestParam (required=false) String id,

            @RequestParam (required=false) String name,

            @RequestParam (defaultValue="0") Integer page,
            @RequestParam (defaultValue="5") Integer size){
        Pageable pageable= PageRequest.of(page,size);
        return userEntityService.searchUser(id,name,pageable);
    }

    @GetMapping("/searchUserByDivision")
    @PreAuthorize("hasAnyAuthority('SEARCH_USER_BY_DIVISION')")
    @Operation(summary = "search user in the database using  Division or Section")
    public Page<UserEntity> searchUserByDivisionOrSection(
            @RequestParam (required=true) String Division,

            @RequestParam (required=false) String Section,

            @RequestParam (defaultValue="0") Integer page,
            @RequestParam (defaultValue="5") Integer size){
        Pageable pageable= PageRequest.of(page,size);
        return userEntityService.searchUserByDivisionOrSection(Division,Section,pageable);
    }

    @GetMapping("/searchUserByGrade")
    @PreAuthorize("hasAnyAuthority('SEARCH_USER_BY_GRADE')")
    @Operation(summary = "search user in the database using  Grade")
    public Page<UserEntity> searchUserByGrade(
            @RequestParam (required=true) String grade,

            @RequestParam (required=false) String division,

            @RequestParam (defaultValue="0") Integer page,
            @RequestParam (defaultValue="5") Integer size){
        Pageable pageable= PageRequest.of(page,size);
        return userEntityService.searchUserByGrade(grade,division,pageable);
    }


    @PostMapping(value = "/deleteUser/{email}")
    @PreAuthorize("hasAnyAuthority('DELETE_USER')")
    @Operation(summary = "deleting user from the database")
    public void deleteUser(@PathVariable @RequestParam (required = true) String email) {
        userEntityService.deleteUser(email);

    }

    @PutMapping("/updateUser/{id}")
    @PreAuthorize("hasAnyAuthority('UPDATE_USER')")
    @Operation(summary = "updating a user in the database")
    public void updateUser(@PathVariable String id, @RequestBody UserEntity user){
        user.setUsername(id);
        this.userEntityService.update(user);
    }
    @PutMapping("/assignUserAppraiser/{userEmail}")
    @PreAuthorize("hasAnyAuthority('ASSIGN_APPRAISER')")
    @Operation(summary = "updating a user in the database")
    public void assignAppraiser(@PathVariable String userEmail, String appraiserEmail) {
        userEntityService.assignAppraiser(userEmail,appraiserEmail);

    }

    @PutMapping("/deleteAprraiser/{userEmail}")
    @PreAuthorize("hasAnyAuthority('DELETE_APPRAISER')")
    @Operation(summary = "deleting a user  appraiser in the database")
    public String deleteAppraiser(@PathVariable String userEmail, String appraiserEmail) {
        userEntityService.deleteAppraiser(userEmail);
        return "Successfully Deleted Appraisal";

    }
    @PutMapping("/assignUserAppraisees")
    @PreAuthorize("hasAnyAuthority('ASSIGN_APPRAISEES')")
    @Operation(summary = "assigning a user appraisees in the database")

    public String assignAppraisees( @RequestParam String appraiseeEmail){
        return userEntityService.assignAppraisees(appraiseeEmail);


    }
    @PutMapping("/updateUserAppraisees/{userEmail}")
    @PreAuthorize("hasAnyAuthority('UPDATE_APPRAISEES')")
    @Operation(summary = "updating a user appraisees in the database")
    public void updateAppraisees(@PathVariable String userEmail, String appraiseesEmails){
        userEntityService.updateAppraisees(userEmail,appraiseesEmails);

    }
    @GetMapping("/getAppraisees")
    @PreAuthorize("hasAnyAuthority('GET_APPRAISEES')")
    @Operation(summary = "updating a user appraisees in the database")
    public  List<String> getAppraisees(@PathVariable String userEmail){
        return userEntityService.getAppraisees(userEmail);

    }

    @GetMapping("/getAllUsers")
    @PreAuthorize("hasAnyAuthority('GET_ALL_USERS')")
    @Operation(summary = "get all users  in the database")
    public List<UserEntity> getAllUsers(){
        return userEntityService.getAllUsers();
    }

//    @GetMapping("/getUsersBySection")
//    @PreAuthorize("hasAnyAuthority('GET_USERS_BY_SECTION')")
//    @Operation(summary = "get users by section in the database")
//    public List<UserEntity> getBySection(@PathVariable String sectionName){
//        return userEntityService.getBySection(sectionName);
//    }
}
