// JwtAuthenticationFilter.java
package kr.co.backend.filter;

import jakarta.servlet.http.Cookie;
import kr.co.backend.service.CustomUserDetailsService;
import kr.co.backend.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();


    public JwtAuthenticationFilter(JwtUtil jwtUtil, CustomUserDetailsService customUserDetailsService) {
        this.jwtUtil = jwtUtil;
        this.customUserDetailsService = customUserDetailsService;
    }



    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // 특정 경로에 대해 JWT 검사 제외
        if (pathMatcher.match("/api/user/check-signup", path) || pathMatcher.match("/api/category/**", path)
        || pathMatcher.match("/api/product/**", path)) {
            chain.doFilter(request, response);
            return;
        }

        Cookie[] cookies = request.getCookies();
        final String authorizationHeader = request.getHeader("Authorization");

        String jwt = null;

        if(cookies != null){
            for(Cookie cookie : cookies){
                if("token".equals(cookie.getName())){
                    jwt = cookie.getValue();
                    break;

                }
            }
        }

        if (jwt != null) {
            try {
                String name = jwtUtil.getUserNameFromToken(jwt);
                logger.info("Extracted username from JWT: {}", name);

                if (name != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = this.customUserDetailsService.loadUserByUsername(name);

                    if (userDetails != null && !jwtUtil.isTokenExpired(jwt)) {
                        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        usernamePasswordAuthenticationToken
                                .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    }
                }
            } catch (Exception e) {
                logger.error("JWT token parsing failed", e);
            }
        } else {
            logger.warn("JWT token is missing in cookies");
        }

        try {
            chain.doFilter(request, response);
        } catch (HttpMessageNotWritableException e) {
            logger.error("HttpMessageNotWritableException occurred", e);
            if (!response.isCommitted()) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error writing HTTP message");
            } else {
                logger.error("Cannot call sendError() after the response has been committed", e);
            }
        } catch (Exception e) {
            logger.error("Error during filtering request", e);
            if (!response.isCommitted()) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An internal server error occurred");
            } else {
                logger.error("Cannot call sendError() after the response has been committed", e);
            }
        }
    }
}
