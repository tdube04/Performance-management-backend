package com.innovation.workplan.Controllers;



import com.innovation.workplan.CollectionModels.UserGroup;
import com.innovation.workplan.Services.UserGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/user-group")
public class UserGroupController  {


    @Autowired
    private UserGroupService groupService;


    @PostMapping(value= "/save")
    @PreAuthorize("hasAnyAuthority('SAVE_GROUP')")
    public String saveGroup(@RequestBody UserGroup group){
        return groupService.save(group);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('UPDATE_GROUP')")
    public void updateGroup(@PathVariable String id, @RequestBody UserGroup group ){
        group.setName(id);
        this.groupService.update(group);
    }

    @GetMapping("/searchGroup")
    @PreAuthorize("hasAnyAuthority('SEARCH_GROUP')")
    public Page<UserGroup> searchGroup(

            @RequestParam (required=false) String name,

            @RequestParam (defaultValue="0") Integer page,
            @RequestParam (defaultValue="5") Integer size){
        Pageable pageable= PageRequest.of(page,size);
        return groupService.searchGroup(name,pageable);
    }

    @PostMapping(value = "/{name}")
    @PreAuthorize("hasAnyAuthority('DELETE_GROUP')")
    public void deleteGroup(@PathVariable String name) {
        groupService.deleteGroup(name);
    }
    @GetMapping("/GetAllPermissions")
    @PreAuthorize("hasAnyAuthority('GET_ALL_PERMISSIONS')")
    public ArrayList<String> getAllPermissions() {

        return groupService.getAllPermissions();
    }
}
