package com.innovation.workplan.Configuration;


import com.innovation.workplan.Filters.JwtFilter;
import com.innovation.workplan.ServiceImplementations.MyCustomUserDetailsService;
import org.springframework.web.filter.CorsFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)

public class MySecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Autowired
    private MyCustomUserDetailsService myCustomUserDetailsService;
    @Autowired
    private JwtFilter jwtRequestFilter;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(myCustomUserDetailsService).passwordEncoder(bCryptPasswordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().headers().frameOptions().sameOrigin().and().
        csrf().disable()
                .authorizeRequests()
                .antMatchers(

                        "*" ,
                        "/file/**",
                                "/login",
                             "/adminlogin",
                             "/temp-login",
                             "/saveUser",
                             "/selectRole",
//                        "/workplan/updateWorkplan/{id}",
                     //     "/scorecard/searchScorecardByEvaluator",
                                "/logout",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/webjars/**",
                        "/v2/**",
                        "/swagger-resources/**",
                        "/change-password",
                        "/forget-password",
                        "/reset-password",
                        "http://192.168.56.1:5173/",
                        "http://10.45.0.99:5173/",
                        "http://localhost:5173/",
                        "http://10.45.0.122:5173/",
                        "http://10.45.0.214:5173/",
                        "http://localhost:3001/",
                        "http://10.45.0.229:5173",
                        "http://10.18.6.193:8080/temp-login",
                        "http://172.20.10.4:8080/temp-login",
                        "http://172.20.10.4:8080"


                        )
                .permitAll().
                and().authorizeRequests().anyRequest().authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }


    @Bean
    public CorsConfiguration corsConfiguration() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "http://192.168.56.1:5173/",
                "http://10.45.0.99:5173/",
                "http://localhost:5173/",
                "http://10.45.0.229:5173",
                "http://10.45.0.122:5173/",
                "http://10.45.0.214:5173/"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return configuration;
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration());
        return new CorsFilter(source);
    }
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
       return super.authenticationManagerBean();
    }


//    @Bean
//    public PasswordEncoder passwordEncoder(){
//        return (NoOpPasswordEncoder.getInstance());
//    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }



}
