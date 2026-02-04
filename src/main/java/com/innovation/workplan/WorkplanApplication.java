package com.innovation.workplan;

import com.innovation.workplan.CollectionModels.Position;
import com.innovation.workplan.CollectionModels.Quarter;
import com.innovation.workplan.CollectionModels.UserEntity;
import com.innovation.workplan.CollectionModels.UserGroup;

import com.innovation.workplan.Services.QuarterService;
import com.innovation.workplan.Services.UserEntityService;
import com.innovation.workplan.Services.UserGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@SpringBootApplication
@Component("com.innovation.workplan")

public class WorkplanApplication {
	@Lazy
	@Autowired
	private UserEntityService userEntityService;

	@Autowired
	private UserGroupService groupService;
	@Autowired
	private QuarterService quarterService;

	public static void main(String[] args) {
		SpringApplication.run(WorkplanApplication.class, args);
	}

	@Bean
	public PasswordEncoder getPasswordEncoder(){
		return NoOpPasswordEncoder.getInstance();
	}


//	@Bean
//	public void addUser(){

//		UserGroup group=new UserGroup();
//		group.setName("USER");
//		List<String> perms=new ArrayList<>();
//		perms.add("VIEW_PAGE");

//		group.setPermissions(perms);

//
//		UserEntity user=new UserEntity();
//		user.setUsername("tsilongwe");
//		user.setPassword("1234");
//		user.setEmail("tsilongwe@zimra.co.zw");
//		user.setEc_number("5140");
//		user.setGrade("14");
//		user.setName("Thando");
//		user.setSurname("Silongwe");
//		user.setDivisionName("It");
//		user.setPositionName("Software Development Manager");
//		user.setSectionName("Business Solutions");
//		user.setUserRole("ADMIN");
//		userEntityService.save(user);
//
//		UserEntity user2 =new UserEntity();
//		user2.setUsername("pmuleya");
//		user2.setPassword("4321");
//		user2.setEmail("cmutimbanyoka@zimra.co.zw");
//		user2.setEc_number("5134");
//		user2.setGrade("14");
//		user2.setName("Confidence");
//		user2.setSurname("Mutimbanyoka");
//		user.setDivisionName("It");
//		user.setPositionName("Gt");
//		user.setSectionName("Projects");
//		user2.setUserRole("ADMIN");
//		userEntityService.save(user2);
//
//		UserEntity user3 =new UserEntity();
//		user3.setUsername("ymakwara");
//		user3.setPassword("1508");
//		user3.setEmail("tdube1@zimra.co.zw");
//		user3.setEc_number("5134");
//		user3.setGrade("14");
//		user3.setName("Isaac");
//		user3.setSurname("Sharara");
//		user.setDivisionName("It");
//		user.setPositionName("Gt");
//		user.setSectionName("Projects");
//		user3.setUserRole("ADMIN");
//
//		user3.setAppraisees(new ArrayList<>());
//		userEntityService.save(user3);
//	}

	@Bean
	public void addUser(){

//		UserGroup group=new UserGroup();
//		group.setName("USER");
//		List<String> perms=new ArrayList<>();
//		perms.add("VIEW_PAGE");

//		group.setPermissions(perms);


		UserEntity user=new UserEntity();
		user.setUsername("tdube1");
		user.setPassword("1234");
		user.setEmail("tdube1@zimra.co.zw");
		user.setEc_number("5140");
		user.setGrade("1");
		user.setName("Tafadzwa");
		user.setSurname("Dube");
		user.setDivisionName("It");
		user.setPositionName("Gt");
		user.setSectionName("Projects");
		user.setUserRole(new ArrayList<>(List.of("USER")));
		user.setAppraisees(new ArrayList<>(){{add("amuchoko");
			add("mbanda");}});
		userEntityService.save(user);

		UserEntity user2 =new UserEntity();
		user2.setUsername("mandiwanzira");
		user2.setPassword("123");
		user2.setEmail("mrmandiwanzira@gmail.com");
		user2.setEc_number("EC0000");
		user2.setGrade("0");
		user2.setName("Mr");
		user2.setSurname("Mandiwanzira");
		user2.setDivisionName("Zimra");
		user2.setPositionName("Board Chairman");
		user2.setSectionName("Zimra");
		user2.setUserRole(new ArrayList<>(List.of("USER")));
//		user2.setAppraisees(new ArrayList<>());
		userEntityService.save(user2);

		UserEntity user3 =new UserEntity();
		user3.setUsername("mbanda");
		user3.setPassword("1508");
		user3.setEmail("mbanda@zimra.co.zw");
		user3.setEc_number("5134");
		user3.setGrade("14");
		user3.setName("Melissa");
		user3.setSurname("Banda");
		user3.setDivisionName("It");
		user3.setPositionName("Systems Developer");
		user3.setSectionName("Projects");
		user3.setLogAs("user");
		user3.setUserRole(new ArrayList<>(List.of("USER")));
		userEntityService.save(user3);
	}


	@Bean
	public void addGroup(){
		UserGroup group=new UserGroup();
		group.setName("ADMIN");
		List<String> perms= List.of(

				"CLOSE_EVALUATION_PERIOD",
				"OPEN_EVALUATION_PERIOD",

				"UPDATE_QUARTER",
				"SAVE_QUARTER",
				"GET_ALL_QUARTERS",

				"SEARCH_USER_BY_GRADE",
				"SEARCH_USER_BY_DIVISION",
				"VIEW_USER",
				"VIEW_ALL_USER",
				"SAVE_USER",
				"UPDATE_USER",
				"DELETE_USER",
				"SEARCH_USER",
				"SEARCH_ALL_USER",
				"SHOW_PDF",
				"SHOW_SCORECARD_PDF",
				"VIEW_ACTIVE_PILLARS",

				"SAVE_DIVISION",
                 "VIEW_ALL_DIVISION",
                 "DELETE_DIVISION",
                 "UPDATE_DIVISION",
                 "ADD_SECTION",
                 "DELETE_SECTION",
				"DEACTIVATE_DIVISION",

//				"DOWNLOAD_FILE",
//				"UPLOAD_FILE",

				"SAVE_GRADE",
				"VIEW_GRADE",
				"DELETE_GRADE",

                 "SAVE_GROUP",
				"UPDATE_GROUP",
				"SEARCH_GROUP",
				"DELETE_GROUP",

				"SAVE_PERFORMANCE_AREA",
				"VIEW_ALL_PERFORMANCE_AREA",
				"DELETE_PERFORMANCE_AREA",
				"UPDATE_PERFORMANCE_AREA",
				"DELETE_PROGRAM",
				"ADD_PROGRAM",
				 "FIND_ACTIVE_PERFORMANCE_AREA",
				 "VIEW_ALL_ACTIVE_DIVISIONS",
				 "DEACTIVATE_PERFORMANCE_AREA",

                 "SAVE_PILLAR",
				"VIEW_ALL_PILLAR",
				"VIEW_PILLAR",
				"DELETE_PILLAR",
				"UPDATE_PILLAR",
				"DEACTIVATE_PILLAR",

			    "VIEW_RIGHTS",
			    "SAVE_RIGHTS",


                 "SAVE_SCORECARD",
				 "SEARCH_SCORECARD_BY_APPRAISEE",
			     "SEARCH_SCORECARD",
			     "UPDATE_SCORECARD",
			     "SHOW_SCORECARD_PDF",
				"SEARCH_SCORECARD_BY_EVALUATOR",
				"SEARCH_SCORECARD_BY_STATUS",
				"UPLOAD_SIGNATURE",
				"DOWNLOAD_SIGNATURE",
				"FIND_PERFORMANCE_AREA",


			    "GET_USER",
				"GET_ALL_USERS",
			    "SEARCH_USER",
			    "DELETE_USER",
				"UPDATE_USER",
			    "ASSIGN_APPRAISER",
			    "DELETE_APPRAISER",
			    "ASSIGN_APPRAISEES",
			    "UPDATE_APPRAISEES",
			    "GET_APPRAISEES",

			    "SAVE_WORKPLAN",
				"SEARCH_WORKPLAN",
				"APPROVE_WORKPLAN",
				"DISAPPROVE_WORKPLAN",
				"DELETE_INDICATOR",
			    "SHOW_PDF",
			    "UPDATE_WORKPLAN",
			    "SEARCH_WORKPLAN_BY_APPRAISER",
			    "SEARCH_WORKPLAN_BY_EVALUATOR",
			    "SEARCH_WORKPLAN_BY_APPRAISEE",
				"SEARCH_WORKPLAN_BY_STATUS",
				"FIND_UNIT",
				"SAVE_UNIT_OF_MEASURE",
				"UPDATE_UNIT",
				"DELETE_UNIT",
				"VIEW_ALL_UNITS_OF_MEASURE",
				"DELETE_UNIT_PERMANENTLY",
				"UPDATE_RATIO",
				"DELETE_RATIO",
				"VIEW_ALL_RATIOS",
				"SAVE_RATIO",
				"GET_ALL_PERMISSIONS"
				);


		group.setPermissions(perms);
        groupService.save(group);

		UserGroup userGroup=new UserGroup();
		userGroup.setName("USER");
		List<String> prms = List.of(
				"VIEW_USER",
				"UPDATE_USER",
				"GET_ALL_USERS",
				"VIEW_DIVISION",
				"VIEW_PILLAR",
				"VIEW_GRADE",
				"VIEW_ALL_PERFORMANCE_AREA",
				"UPDATE_PERFORMANCE_AREA",
				"VIEW_ALL_DIVISION",
				"UPLOAD_SIGNATURE",
				"SAVE_SCORECARD",
				"SEARCH_SCORECARD_BY_APPRAISEE",
				"SEARCH_SCORECARD",
				"UPDATE_SCORECARD",
				"SHOW_SCORECARD_PDF",
//				"DOWNLOAD_FILE",
//				"UPLOAD_FILE",
				"UPDATE_WORKPLAN",
				"SAVE_WORKPLAN",
				"SEARCH_WORKPLAN",
				"APPROVE_WORKPLAN",
				"DISAPPROVE_WORKPLAN",
				"DELETE_INDICATOR",
				"SEARCH_WORKPLAN_BY_APPRAISEE",
				"SHOW_PDF",
				"SAVE_PILLAR",
				"SAVE_PERFORMANCE_AREA",
				"GET_USER",
				"ASSIGN_APPRAISEES",
				"VIEW_ACTIVE_PILLARS",
				"FIND_ACTIVE_PERFORMANCE_AREA",
				"VIEW_ALL_ACTIVE_DIVISIONS",
				"CALCULATE_RATIO",
				"SEARCH_USER_BY_DIVISION",
				"SEARCH_USER_BY_GRADE"

				);
		userGroup.setPermissions(prms);
		groupService.save(userGroup);


		UserGroup newUserGroup=new UserGroup();
		newUserGroup.setName("NEW_USER");
		List<String> new_prms = List.of(
				"SAVE_USER",
				"GET_USER"

		);
		newUserGroup.setPermissions(new_prms);
		groupService.save(newUserGroup);

	}

	@Bean
	public void addQuarters(){
		List<String> quarters= Arrays.asList("Q1","Q2","Q3","Q4");
		for(String q:quarters){
			Quarter quarter=new Quarter(q,10);
			quarterService.saveQuarter(quarter);
		}
	}


}

