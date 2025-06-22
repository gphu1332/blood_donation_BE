package com.example.blood_donation.config;

import com.example.blood_donation.exception.exceptons.AuthenticationException;
import com.example.blood_donation.service.AuthenticationService;
import com.example.blood_donation.service.TokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.security.auth.message.AuthException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AuthenticationService authenticationService;

    private final List<String> PUBLIC_API = List.of(
            "POST:/api/register",
            "POST:/api/login",
            "GET:/v3/api-docs/**",
            "GET:/swagger-ui/**"
    );

    public boolean isPermitted(String uri, String method) {
        AntPathMatcher matcher = new AntPathMatcher();

        return PUBLIC_API.stream().anyMatch(pattern -> {
            String[] parts = pattern.split(":", 2);
            if (parts.length != 2) return false;

            String allowedMethod = parts[0];
            String allowedUri = parts[1];

            return method.equalsIgnoreCase(allowedMethod) && matcher.match(allowedUri, uri);
        });
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();
        String method = request.getMethod();

        System.out.println("🌀 JwtFilter is called: " + uri);

        if (isPermitted(uri, method)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = getToken(request);
        if (token == null) {
            resolver.resolveException(request, response, null, new AuthenticationException("Empty token!"));
            return;
        }

        try {
            // ✅ Lấy username từ token
            String username = tokenService.extractUsername(token);

            // ✅ Load đúng UserDetails
            UserDetails userDetails = authenticationService.loadUserByUsername(username);

            // Log debug xác thực
            System.out.println("🔑 Authenticated user: " + userDetails.getUsername());
            System.out.println("✅ Authorities: " + userDetails.getAuthorities());

            // ✅ Gán Authentication
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(userDetails, token, userDetails.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);

            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            resolver.resolveException(request, response, null, new AuthException("Expired Token!"));
        } catch (MalformedJwtException e) {
            resolver.resolveException(request, response, null, new AuthException("Invalid Token!"));
        } catch (Exception e) {
            resolver.resolveException(request, response, null, new AuthException("Unexpected error: " + e.getMessage()));
        }
    }

    public String getToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;
        return authHeader.substring(7);
    }
}
