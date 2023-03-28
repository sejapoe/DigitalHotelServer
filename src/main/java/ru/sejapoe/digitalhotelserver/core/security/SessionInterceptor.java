package ru.sejapoe.digitalhotelserver.core.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import ru.sejapoe.digitalhotelserver.user.session.Session;

import java.security.Key;

@Component
public class SessionInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) {
        if (handler instanceof HandlerMethod handlerMethod
                && handlerMethod.getBean() instanceof SessionServiceHolder bean
                && handlerMethod.hasMethodAnnotation(AuthorizationRequired.class)) {
            String authorization = request.getHeader("Authorization");
            if (authorization == null) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                return false;
            }
            String token = authorization.split(" ")[1];
            JwtParser jwtParser = Jwts.parserBuilder().setSigningKeyResolver(
                    new SigningKeyResolver() {
                        @Override
                        public Key resolveSigningKey(JwsHeader header, Claims claims) {
                            return bean.getSessionService().getKey(claims.getSubject());
                        }

                        @Override
                        public Key resolveSigningKey(JwsHeader header, String plaintext) {
                            return null;
                        }
                    }
            ).build();
            try {
                Jws<Claims> claimsJws = jwtParser.parseClaimsJws(token);
                Session session = bean.getSessionService().getSession(claimsJws.getBody().getSubject());
                request.setAttribute("session", session);
            } catch (IllegalArgumentException | SignatureException | ExpiredJwtException e) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                return false;
            }
        }
        return true;
    }
}
