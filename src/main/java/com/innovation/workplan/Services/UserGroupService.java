package com.innovation.workplan.Services;


import com.innovation.workplan.CollectionModels.UserGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;

public interface  UserGroupService {
    public String save(UserGroup group);


    Page<UserGroup> searchGroup(String name, Pageable pageable);

    ArrayList<String> getAllPermissions();



    void deleteGroup(String name);

    UserGroup update(UserGroup group);
}
