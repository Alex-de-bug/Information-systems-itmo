package com.alwx.backend.utils;


import java.io.IOException;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

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
        String authHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;

        if(authHeader != null && authHeader.startsWith("Bearer ")){
            jwt = authHeader.substring(7);
            try {
                username = jwtTokenUtil.getUsername(jwt);
            } catch (ExpiredJwtException e) {
                logger.info("Время жизни токена всё");
            } catch (SignatureException e) {
                logger.info("Подпись неверная");
            }
        }

        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, null, jwtTokenUtil.getRoles(jwt).stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
            SecurityContextHolder.getContext().setAuthentication(token);
        }
        filterChain.doFilter(request, response);
    }
}
