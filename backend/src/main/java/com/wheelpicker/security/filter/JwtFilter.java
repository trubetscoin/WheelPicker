package com.wheelpicker.security.filter;

import com.wheelpicker.component.JwtUtility;
import com.wheelpicker.service.MyUserDetailsService;
import io.jsonwebtoken.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private final String TOKEN_HEADER = "Authorization";
    private final String TOKEN_PREFIX = "Bearer ";

    private final JwtUtility jwtUtility;
    private final MyUserDetailsService myUserDetailsService;

    public JwtFilter(JwtUtility jwtUtility, MyUserDetailsService myUserDetailsService) {
        this.jwtUtility = jwtUtility;
        this.myUserDetailsService = myUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        try {
            String bearerToken = request.getHeader(TOKEN_HEADER);
            if (bearerToken == null || !bearerToken.startsWith(TOKEN_PREFIX)) {
                chain.doFilter(request, response);
                return;
            }

            String token = bearerToken.substring(TOKEN_PREFIX.length());
            Claims claims = jwtUtility.extractAccessClaims(token);
            String email = claims.getSubject();

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = myUserDetailsService.loadUserByUsername(email);

                UsernamePasswordAuthenticationToken JWTtoken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(JWTtoken);
            }
        }
        catch (ExpiredJwtException e) {
           throw new BadCredentialsException("JWT expired");
        }
        catch (RuntimeException e) {
            throw new BadCredentialsException("Invalid JWT");
        }
        chain.doFilter(request, response);
    }
}
