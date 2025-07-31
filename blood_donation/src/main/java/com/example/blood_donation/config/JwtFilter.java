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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
            "POST:/api/generate-otp",
            "POST:/api/verify-otp",
            "POST:/api/contact",
            "GET:/api/token-expired",
            "GET:/v3/api-docs/**",
            "GET:/swagger-ui/**",
            "GET:/swagger-ui.html",           // thêm nếu dùng SpringFox 2.x
            "GET:/swagger-ui/index.html",     // thêm chính xác file bạn truy cập
            "GET:/"                           // rất quan trọng để cho phép truy cập trang gốc `/`
    );


    public boolean isPermitted(String uri, String method) {
        System.out.println(uri);
        System.out.println(method);
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
            Claims claims = tokenService.extractAllClaims(token);
            // ✅ Load đúng UserDetails
            String username = claims.getSubject();
            String role = claims.get("authorities", String.class);
            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
            // Log debug xác thực
            System.out.println("🔑 Authenticated user: " + username);
            System.out.println("✅ Authorities: " + authorities);

            // ✅ Gán Authentication
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(username, null, authorities);
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
