package com.innovation.workplan.ServiceImplementations;


import com.innovation.workplan.CollectionModels.UserEntity;
import com.innovation.workplan.CollectionModels.UserGroup;
import com.innovation.workplan.Repositories.UserGroupRepository;
import com.innovation.workplan.Services.UserEntityService;
import com.innovation.workplan.Utilities.JWTUtility;
import com.innovation.workplan.Utilities.PrincipalHelper;
import com.innovation.workplan.Utilities.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.servlet.http.HttpServletRequest;
import java.net.http.HttpRequest;
import java.util.stream.Collectors;

@Service
public class MyCustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserEntityService userEntityService;
    @Autowired
    private UserGroupRepository userGroupRepository;
    @Autowired private PrincipalHelper principalHelper;

    @Autowired private HttpServletRequest httpRequest;
    @Autowired
    JWTUtility jwtUtility;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        if(jwtUtility.extractUsername(httpRequest.getHeader("Authorization").substring(7))==username){
//            UserEntity userEntity=userEntityService.findByUsername(username);
//            if(userEntity!=null){
//                return new User(userEntity.getUsername(),"",userGroupRepository.findById(userEntity.getUserRole()).orElse(null)
//                        .getPermissions().
//                        parallelStream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
//            }
//
//        }
        UserEntity userEntity=userEntityService.findByUsername(username);
        if(userEntity!=null){
            if(userEntity.getUserRole().contains("USER")){
        return new User(userEntity.getUsername(),"",userGroupRepository.findById("USER").orElse(null)
                .getPermissions().
                parallelStream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));}
            if(userEntity.getUserRole().contains("ADMIN")){
                return new User(userEntity.getUsername(),"",userGroupRepository.findById("ADMIN").orElse(null)
                        .getPermissions().
                        parallelStream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));}
            if(userEntity.getUserRole().contains("HC")){
                return new User(userEntity.getUsername(),"",userGroupRepository.findById("HC_USER").orElse(null)
                        .getPermissions().
                        parallelStream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));}

        }


        UserGroup group=userGroupRepository.findById("NEW_USER").orElse(null);

        return new User(username,"",group
                .getPermissions().
                parallelStream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));


    }

}
