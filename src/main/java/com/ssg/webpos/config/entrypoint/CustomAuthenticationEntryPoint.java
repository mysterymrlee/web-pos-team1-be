package com.ssg.webpos.config.entrypoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssg.webpos.domain.result.ErrorResponse;
import com.ssg.webpos.exception.ErrorCode;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        final ErrorCode ERROR_CODE = ErrorCode.INVALID_PERMISSION;
        ObjectMapper objectMapper = new ObjectMapper();

        ErrorResponse errorResponse = new ErrorResponse(ERROR_CODE.toString(), ERROR_CODE.getMessage());

        response.setStatus(401);
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
