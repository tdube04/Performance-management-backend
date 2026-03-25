package com.innovation.workplan.Filters;

import java.util.Arrays;
import com.innovation.workplan.ServiceImplementations.MyCustomUserDetailsService;
import com.innovation.workplan.Utilities.JWTUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JWTUtility jwtUtility;

    @Autowired
    private MyCustomUserDetailsService myCustomUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        String authorization = httpServletRequest.getHeader("Authorization");
        String token = null;
        String userName = null;

        if(null != authorization && authorization.startsWith("Bearer ")) {
            token = authorization.substring(7);
            userName = jwtUtility.extractUsername(token);
        }

        if(null != userName && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails
                    = myCustomUserDetailsService.loadUserByUsername(userName);

            if(jwtUtility.validateToken(token,userDetails)) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                        = new UsernamePasswordAuthenticationToken(userDetails,
                        null, userDetails.getAuthorities());

                usernamePasswordAuthenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(httpServletRequest)
                );

SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                
                // DEBUG LOGGING for 403 issues
                System.out.println("=== JWT DEBUG ===");
System.out.println("Username: " + userName);
                System.out.println("Authorities: " + Arrays.toString(usernamePasswordAuthenticationToken.getAuthorities().toArray()));
                System.out.println("=================");
                
            }

        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
