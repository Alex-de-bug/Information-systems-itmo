package com.alwx.backend.utils.jwt;


import java.io.IOException;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.alwx.backend.dtos.AppError;
import com.alwx.backend.utils.UserError;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * Фильтр для обработки JWT-токенов в запросах.
 */
@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter{
    private static final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);
    private final JwtTokenUtil jwtTokenUtil;



    /**
     * Метод для фильтрации запросов и проверки JWT-токена.
     *
     * @param request  входящий HTTP-запрос
     * @param response HTTP-ответ
     * @param filterChain цепочка фильтров
     * @throws ServletException в случае ошибки сервлета
     * @throws IOException в случае ошибки ввода-вывода
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        if (!request.getRequestURI().equals("/api/auth") && !request.getRequestURI().equals("/api/reg") && !request.getRequestURI().startsWith("/ws")) {
            String authHeader = request.getHeader("Authorization");
            String username = null;
            String jwt = null;
            try {
                jwt = authHeader.substring(7);
                username = jwtTokenUtil.getUsername(jwt);
                if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
                    UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                        username, 
                        null, 
                        jwtTokenUtil.getRoles(jwt).stream()
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList())
                    );
                    SecurityContextHolder.getContext().setAuthentication(token);
                }
            } catch (ExpiredJwtException | SignatureException e){
                response.setContentType("application/json;charset=UTF-8");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                AppError error = new AppError(HttpServletResponse.SC_UNAUTHORIZED, UserError.TOKEN_EXPIRED.getMessage());
                ObjectMapper mapper = new ObjectMapper();
                response.getWriter().write(mapper.writeValueAsString(error));
                return;
            } catch (Exception e) {
                response.setContentType("application/json;charset=UTF-8");
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                AppError error = new AppError(HttpStatus.BAD_REQUEST.value(), UserError.TOKEN_INVALID.getMessage());
                ObjectMapper mapper = new ObjectMapper();
                response.getWriter().write(mapper.writeValueAsString(error));
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
