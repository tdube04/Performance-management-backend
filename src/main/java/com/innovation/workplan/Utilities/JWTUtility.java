package com.innovation.workplan.Utilities;

import com.innovation.workplan.CollectionModels.JwtRequest;
import com.innovation.workplan.CollectionModels.UserEntity;
import com.innovation.workplan.Repositories.UserGroupRepository;
import com.innovation.workplan.Services.UserEntityService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;


@Service
public class JWTUtility {

    private String SECRET_KEY = "secret";
//    @Autowired
//    private JwtRequest jwtRequest;



    private UserDetails userDetails;

    @Autowired
    private UserEntityService userEntityService;

    @Autowired
    private UserGroupRepository groupRepository;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        Map<String, Object> claims1 = new HashMap<>();
        String username=userDetails.getUsername();
        String password=userDetails.getPassword();
        UserEntity userEntity=userEntityService.findByUsername(userDetails.getUsername());
        if(userEntity!=null){
            if(userEntity.getLogAs().equalsIgnoreCase("hc") && userEntity.getUserRole().contains("HC")){
                String key="HC_USER";
                List<String> roles=groupRepository.findById(key).orElse(null).getPermissions();

                claims.put(key, roles);
                claims.put("logAs", "hc");
                return createToken(claims, userDetails.getUsername());
            }
            else if(userEntity.getLogAs().equalsIgnoreCase("user")&& userEntity.getUserRole().contains("USER")){

            String key="USER";
            List<String> roles=groupRepository.findById(key).orElse(null).getPermissions();

            claims.put(key, roles);
            claims.put("logAs", "user");
            return createToken(claims, userDetails.getUsername());}
            else if (userEntity.getLogAs().equalsIgnoreCase("admin")&& userEntity.getUserRole().contains("ADMIN")) {
                String key="ADMIN";
                List<String> roles=groupRepository.findById(key).orElse(null).getPermissions();

                claims.put(key, roles);
                claims.put("logAs", "admin");
                return createToken(claims, userDetails.getUsername());
            }
            else if (userEntity.getLogAs().equalsIgnoreCase("board") && userEntity.getUserRole().contains("BOARD")) {
                String key="BOARD";
                List<String> roles=groupRepository.findById(key).orElse(null).getPermissions();

                claims.put(key, roles);
                claims.put("logAs", "board");
                return createToken(claims, userDetails.getUsername());
            }
        }

            String key1 = "NEW_USER";
            List<String> roles1 = groupRepository.findById(key1).orElse(null).getPermissions();

            claims1.put(key1, roles1);
            claims1.put("logAs", "user");





//            List<String> roles=userEntity.getUserGroup().getPermissions();



       

//        claims.put("Role_2", "HELLO1");
        return createToken(claims1, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {

        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}