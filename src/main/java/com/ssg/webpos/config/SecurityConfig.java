package com.ssg.webpos.config;

import com.ssg.webpos.config.entrypoint.CustomAuthenticationEntryPoint;
import com.ssg.webpos.config.jwt.JwtFilter;
import com.ssg.webpos.repository.UserRepository;
import com.ssg.webpos.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.*;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserRepository userRepository;
    private static final String[] PERMIT_URL = {
//            "/api/v1/branchadmin-staff/join", "/api/v1/branchadmin-staff/login",
//            "/api/v1/branchadmin-manager/join", "/api/v1/branchadmin-manage/login",
//            "/api/v1/hqadmin/join", "/api/v1/hqadmin/login",
//            "/api/v1/pay/**",
            "/api/v1/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .httpBasic().disable()
                .csrf().disable()
                .cors().and()
//                .sessionManagement()
//                .sessionCreationPolicy(STATELESS)
//                .and()
                .exceptionHandling()
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                .and()
                .authorizeRequests()
                .antMatchers(PERMIT_URL).permitAll()
                .antMatchers("/api/v1/branchadmin-manager/**").hasRole("MANAGER") // "ROLE_" 자동생성됨
                .antMatchers("/api/v1/branchadmin-staff/**").hasRole("STAFF")
                .antMatchers("/api/v1/hqadmin/**").hasRole("HQ")
//                .anyRequest().permitAll()
                .and()
//                .formLogin()
//                .loginPage("/login")
//                .defaultSuccessUrl("/main")
//                .permitAll()
//                .and()
//                .logout()
//                .logoutSuccessUrl("/main")
//                .permitAll()
                .logout()
                .logoutSuccessUrl("/")
                .and()
//                .addFilterBefore(new JwtFilter(userRepository), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}