package com.innovation.workplan.CollectionModels;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.cglib.beans.FixedKeySet;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data

@Document(collection = "users_tbl")

public class UserEntity extends Base{

    @Id
    private String username;
    private String password;

    private String email;
    private String ec_number;
    private String grade;
    private String name;
    private String surname;
    private String positionName;

    private String divisionName;

    private String sectionName;
    private List<String> userRole=new ArrayList<>();
    private String signature;
    private String signatureStatus;
    private boolean enabled;
    private List<String> appraisees=new ArrayList<>();
    private String appraiser_status;

    private String appraiserEmail;
    private String resetToken;
    private LocalDateTime reset_token_expiry_date;
    private String token_status;
    private String logAs;



}
