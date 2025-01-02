package com.pbw.sportsync.aspect;
import com.pbw.sportsync.RequiredRole;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.Arrays;

@Aspect
@Component
public class AuthorizationAspect {

    @Before("@annotation(requiredRole)")
    public void checkAuthorization(JoinPoint joinPoint, RequiredRole requiredRole) throws Throwable{
        HttpSession session = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getSession();
        
        if(session.getAttribute("username") == null){ //belum login
            throw new SecurityException("Please login first");
        }

        String[] roles = requiredRole.value();
        if(Arrays.asList(roles).contains("*")){
            return;
        }

        String curUserRole = (String) session.getAttribute("role");
        if(!Arrays.asList(roles).contains(curUserRole)){
            throw new SecurityException("User is not authorized to perform this action.");
        }
    }
}

