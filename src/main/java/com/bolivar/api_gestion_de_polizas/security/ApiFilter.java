package com.bolivar.api_gestion_de_polizas.security;

import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import java.io.IOException;

@Component
public class ApiFilter implements Filter{
    @Value("${api.key.header}")
    private String apiKeyHeader;

    @Value("${api.key.value}")
    private String apiKeyValue;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String apiKey = httpRequest.getHeader(apiKeyHeader);

        if (apiKey == null || !apiKey.equals(apiKeyValue)) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.setContentType("application/json");
            httpResponse.getWriter().write(
                "{\"error\": \"API Key inválida o ausente\", \"status\": 401}"
            );
            return;
        }

        chain.doFilter(request, response);
    }
}

