package com.innovation.workplan.Utilities;


import com.innovation.workplan.CollectionModels.UserEntity;
import com.innovation.workplan.Repositories.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PrincipalHelper {
    private final UserEntityRepository userEntityRepository;


    public UserEntity getPrincipal() {
        String currentLoggedUserName = SecurityUtils.getCurrentUserLogin().get().toString();
        final UserEntity user = userEntityRepository.findById(currentLoggedUserName).orElseThrow();
        return user;
    }

}
