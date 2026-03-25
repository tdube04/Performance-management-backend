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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@SpringBootApplication
@Component("com.innovation.workplan")

public class WorkplanApplication {
	private static final Logger logger = LoggerFactory.getLogger(WorkplanApplication.class);
	
	// Default Super Admin credentials - override with environment variables
	private static final String SUPER_ADMIN_USERNAME = System.getenv("SUPER_ADMIN_USERNAME") != null ? 
		System.getenv("SUPER_ADMIN_USERNAME") : "superadmin";
	private static final String SUPER_ADMIN_PASSWORD = System.getenv("SUPER_ADMIN_PASSWORD") != null ? 
		System.getenv("SUPER_ADMIN_PASSWORD") : "SuperAdmin@123";
	private static final String SUPER_ADMIN_EMAIL = System.getenv("SUPER_ADMIN_EMAIL") != null ? 
		System.getenv("SUPER_ADMIN_EMAIL") : "superadmin@zimra.co.zw";
	
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
		// Using BCrypt with strength 12 (default rounds = 10, but 12 is more secure)
		return new BCryptPasswordEncoder(12);
	}


	@Bean
	public void seedSuperAdmin(){
		try {
			// Check if super admin already exists
			if (userEntityService.findByUsername(SUPER_ADMIN_USERNAME) != null) {
				logger.info("Super Admin account already exists - skipping seed");
				return;
			}
			
			// Validate password strength
			if (!isValidPassword(SUPER_ADMIN_PASSWORD)) {
				logger.warn("Super Admin password does not meet strength requirements. Using default.");
				logger.warn("Password requirements: minimum 8 characters, at least one uppercase, one lowercase, one number and one special character");
			}
			
			// Create Super Admin user
			UserEntity superAdmin = new UserEntity();
			superAdmin.setUsername(SUPER_ADMIN_USERNAME);
			
			// Hash password with BCrypt
			String hashedPassword = org.mindrot.jbcrypt.BCrypt.hashpw(SUPER_ADMIN_PASSWORD, org.mindrot.jbcrypt.BCrypt.gensalt(12));
			superAdmin.setPassword(hashedPassword);
			
			superAdmin.setEmail(SUPER_ADMIN_EMAIL);
			superAdmin.setEc_number("SUPER001");
			superAdmin.setGrade("18");
			superAdmin.setName("Super");
			superAdmin.setSurname("Administrator");
			superAdmin.setDivisionName("IT");
			superAdmin.setPositionName("System Administrator");
			superAdmin.setSectionName("Administration");
			superAdmin.setLogAs("superadmin");
			superAdmin.setUserRole(new ArrayList<>(List.of("SUPER_ADMIN", "ADMIN", "USER", "HC", "BOARD")));
			superAdmin.setEnabled(true);
			superAdmin.setAppraiser_status("UnAssigned");
			superAdmin.setAppraisees(new ArrayList<>());
			
			userEntityService.save(superAdmin);
			
			logger.info("Super Admin account created successfully with username: {}", SUPER_ADMIN_USERNAME);
			logger.info("Password has been hashed with BCrypt (strength: 12 rounds)");
			
		} catch (Exception e) {
			logger.error("Failed to seed Super Admin account: {}", e.getMessage(), e);
		}
	}
	
	/**
	 * Validates password strength
	 * Requirements: minimum 8 characters, at least one uppercase, one lowercase, one number and one special character
	 */
	private boolean isValidPassword(String password) {
		if (password == null || password.length() < 8) {
			return false;
		}
		
		boolean hasUppercase = false;
		boolean hasLowercase = false;
		boolean hasDigit = false;
		boolean hasSpecial = false;
		
		for (char c : password.toCharArray()) {
			if (Character.isUpperCase(c)) hasUppercase = true;
			else if (Character.isLowerCase(c)) hasLowercase = true;
			else if (Character.isDigit(c)) hasDigit = true;
			else hasSpecial = true;
		}
		
		return hasUppercase && hasLowercase && hasDigit && hasSpecial;
	}
	
	@Bean
	public void addUser(){

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
 		user.setUserRole(new ArrayList<>(List.of("ADMIN","USER")));
		user.setAppraisees(new ArrayList<>(){{add("mbanda");
			add("amuchoko");}});
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
 		user3.setLogAs("admin");
 		user3.setUserRole(new ArrayList<>(List.of("USER","ADMIN")));

 		user3.setAppraisees(new ArrayList<>(){{add("amuchoko");
 			add("tdube1");}});
 		userEntityService.save(user3);

 		// HC Admin User
 		UserEntity user4 =new UserEntity();
 		user4.setUsername("hcadmin");
 		user4.setPassword("hcpass123");
 		user4.setEmail("hc@zimra.co.zw");
 		user4.setEc_number("HC001");
 		user4.setGrade("17");
 		user4.setName("Human Capital");
 		user4.setSurname("Administrator");
 		user4.setDivisionName("Human Capital");
 		user4.setPositionName("HC Manager");
 		user4.setSectionName("HC Administration");
 		user4.setLogAs("hc");
 		user4.setUserRole(new ArrayList<>(List.of("HC")));
 		user4.setEnabled(true);
 		user4.setAppraiser_status("UnAssigned");
 		user4.setAppraisees(new ArrayList<>());
 		userEntityService.save(user4);


		UserEntity user5 =new UserEntity();
		user5.setUsername("amuchoko");
		user5.setPassword("1508");
		user5.setEmail("amuchoko@zimra.co.zw");
		user5.setEc_number("5134");
		user5.setGrade("16");
		user5.setName("Anesu");
		user5.setSurname("Muchoko");
		user5.setDivisionName("It");
		user5.setPositionName("Systems Developer");
		user5.setSectionName("Projects");
		user5.setLogAs("user");
		user5.setUserRole(new ArrayList<>(List.of("USER")));

		user5.setAppraisees(new ArrayList<>(){{add("kmasaya");
			add("kmachekanyanga");}});
		userEntityService.save(user5);


		UserEntity user6 =new UserEntity();
		user6.setUsername("kmasaya");
		user6.setPassword("1508");
		user6.setEmail("kmasaya@zimra.co.zw");
		user6.setEc_number("5134");
		user6.setGrade("16");
		user6.setName("Kuda");
		user6.setSurname("Masaya");
		user6.setDivisionName("It");
		user6.setPositionName("Systems Developer");
		user6.setSectionName("Projects");
		user6.setLogAs("user");
		user6.setUserRole(new ArrayList<>(List.of("USER")));

		user5.setAppraisees(new ArrayList<>(){});
		userEntityService.save(user6);
	}


	@Bean
	public void addGroup(){
		// SUPER ADMIN Group - Full system access
		UserGroup superAdminGroup = new UserGroup();
		superAdminGroup.setName("SUPER_ADMIN");
		List<String> superAdminPerms = Arrays.asList(
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
			"DOWNLOAD_FILE",
			"UPLOAD_FILE",
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
			"GET_ALL_PERMISSIONS",
			// Super Admin specific permissions
			"MANAGE_SUPER_ADMIN",
			"OVERRIDE_ADMIN_RESTRICTIONS",
			"MANAGE_ALL_ADMINS"
		);
		superAdminGroup.setPermissions(superAdminPerms);
		groupService.save(superAdminGroup);
		logger.info("Super Admin group created with full system permissions");
 		UserGroup group=new UserGroup();
 		group.setName("ADMIN");
 		List<String> perms = Arrays.asList(
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
 			"DOWNLOAD_FILE",
 			"UPLOAD_FILE",
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
 			"GET_ALL_PERMISSIONS",


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

 				"DOWNLOAD_FILE",
 				"UPLOAD_FILE",

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
 				"CONFIRM_SCORECARD",
 				"SHOW_SCORECARD_PDF",
 				"DOWNLOAD_FILE",
 				"UPLOAD_FILE",
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

 		// HC User Group for Human Capital Dashboard
 		UserGroup hcUserGroup=new UserGroup();
 		hcUserGroup.setName("HC_USER");
 		List<String> hc_prms = List.of(
 				"GET_ALL_USERS",
 				"SEARCH_USER",
 				"GET_USER",
 				"SEARCH_USER_BY_GRADE",
 				"SEARCH_USER_BY_DIVISION",
 				"GET_HC_DASHBOARD",
 				"HC_ACCESS",
 				"MANAGE_NOTIFICATIONS",
 				"GET_NOTIFICATIONS",
 				"CREATE_NOTIFICATION",
 				"UPDATE_NOTIFICATION",
 				"DELETE_NOTIFICATION"
 		);
 		hcUserGroup.setPermissions(hc_prms);
 		groupService.save(hcUserGroup);

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
