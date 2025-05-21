package com.innovation.workplan.ServiceImplementations;


import com.innovation.workplan.CollectionModels.UserEntity;
import com.innovation.workplan.CollectionModels.UserGroup;
import com.innovation.workplan.Repositories.UserGroupRepository;
import com.innovation.workplan.Services.UserGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserGroupServiceImpl implements UserGroupService {
    @Autowired
    UserGroupRepository userGroupRepository;

    @Autowired
    private MongoTemplate mongoTemplate;
    @Override
    public String save(UserGroup group) {
        return userGroupRepository.save(group).getName();
    }



    @Override
    public UserGroup update(UserGroup group) {

        Query query = new Query();
        query.addCriteria(Criteria.where("name").is(group.getName()));
        Update updateGroup = new Update();
        updateGroup.set("Permissions", group.getPermissions());
        return mongoTemplate.findAndModify(query, updateGroup, new FindAndModifyOptions().returnNew(true), UserGroup.class);

        }


    @Override
    public Page<UserGroup> searchGroup(String name, Pageable pageable) {

        Query query=new Query().with(pageable);
        List<Criteria> criteria= new ArrayList<>();

        if(name!=null && !name.isEmpty()){
            criteria.add(Criteria.where("group.name").regex(name,"i"));
        }

        if(!criteria.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteria.toArray(new Criteria[0])));
        }
        Page<UserGroup> groups= PageableExecutionUtils.getPage(mongoTemplate.find(query,UserGroup.class),
                pageable,()->mongoTemplate
                        .count(query.skip(0).limit(0),UserGroup.class));
        return groups;
    }

    @Override
    public ArrayList<String> getAllPermissions() {
       List<String> list1=userGroupRepository.findById("ADMIN").orElse(null).getPermissions();
        List<String> list2=userGroupRepository.findById("USER").orElse(null).getPermissions();
        Set union = new HashSet(list1);
        union.addAll(new HashSet(list2));
        return new ArrayList(union);
    }



    @Override
    public void deleteGroup(String name) {
        userGroupRepository.deleteById(name);
    }




}
