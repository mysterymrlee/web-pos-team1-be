package com.ssg.webpos.config;

import com.ssg.webpos.config.entrypoint.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.http.HttpMethod.POST;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private static final String[] PERMIT_URL = {
            "/api/v1/branchadmin-staff/join", "/api/v1/branchadmin-staff/login",
            "/api/v1/branchadmin-manager/join", "/api/v1/branchadmin-manage/login",
            "/api/v1/hqadmin/join", "/api/v1/hqadmin/login"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .httpBasic().disable()
                .csrf().disable()
                .cors().and()
                .exceptionHandling()
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                .and()
                .authorizeRequests()
                .antMatchers(PERMIT_URL).permitAll()
                .antMatchers("/api/v1/branchadmin-manager/**").hasRole("ROLE_MANAGER")
                .antMatchers("/api/v1/branchadmin-staff/**").hasRole("ROLE_STAFF")
                .antMatchers("/api/v1/hqadmin/**").hasRole("ROLE_HQ")
                .anyRequest().permitAll()
                .and()
                .logout()
                .logoutSuccessUrl("/")
                .and()
                .build();
    }

}