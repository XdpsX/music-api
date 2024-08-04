package com.xdpsx.music.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xdpsx.music.dto.common.ErrorDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;

@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        String jwtAuthError = (String) request.getAttribute("JWT_AUTH_ERROR");
        ErrorDetails errorDetails = new ErrorDetails();
        if (jwtAuthError != null) {
            // Handle the JWT validation error
            errorDetails.setError(jwtAuthError);
        } else {
            // Handle other authentication exceptions
            errorDetails.setError(authException.getMessage());
        }
        errorDetails.setStatus(HttpStatus.UNAUTHORIZED.value());
        errorDetails.setPath(request.getRequestURI());

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        OutputStream out = response.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(out, errorDetails);
        out.flush();
    }
}
