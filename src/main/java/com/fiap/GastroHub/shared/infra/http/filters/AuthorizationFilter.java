package com.fiap.GastroHub.shared.infra.http.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class AuthorizationFilter extends OncePerRequestFilter {

    private final RequestContext requestContext;

    public AuthorizationFilter(RequestContext requestContext) {
        this.requestContext = requestContext;
    }

//    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException{
//        String token = (request.getHeader("Authorization"));
//        if (token == null || token.isEmpty()){
//            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
//            return;
//        }

        requestContext.setRequestId(UUID.randomUUID().toString());

        chain.doFilter(request, response);
    }
}
