package com.alwx.backend.configs;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.alwx.backend.utils.jwt.JwtTokenUtil;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;

@Component
public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {

    @Autowired
    private JwtTokenUtil jwtU;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {

        String token = request.getHeaders().getFirst("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            try {
                token = token.substring(7);
                jwtU.getUsername(token);
                return true; 

            } catch (ExpiredJwtException e) {
                return false;
            } catch (SignatureException e) {
                return false;
            } catch (Exception e) {
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return false;
            }
        } else {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false; // Отклоняем соединение
        }
    }


    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }

}