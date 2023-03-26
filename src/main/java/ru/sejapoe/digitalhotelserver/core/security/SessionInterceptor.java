package ru.sejapoe.digitalhotelserver.core.security;

import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.security.Key;

public class SessionInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) {
        if (handler instanceof HandlerMethod handlerMethod
                && handlerMethod.getBean() instanceof SessionServiceHolder bean
                && handlerMethod.hasMethodAnnotation(AuthorizationRequired.class)) {
            String authorization = request.getHeader("Authorization");
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
            if (!jwtParser.isSigned(token)) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                return false;
            }
//            if (request.getMethod().equalsIgnoreCase("post")) {
//                try {
//                    Pair<Session, String> decrypt = bean.getSessionService().decrypt(request.getReader().lines().collect(Collectors.joining(System.lineSeparator())));
//                    Object o = new ObjectMapper().readValue(decrypt.getSecond(), handlerMethod.getMethodParameters()[1].getParameterType());
//                    handlerMethod.getMethod().invoke(bean, decrypt.getFirst().getUser(), o);
//                } catch (SessionService.Unauthorized e) {
//                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
//                } catch (DatabindException e) {
//                    LoggerFactory.getLogger(SessionInterceptor.class).warn(e.getMessage());
//                    response.setStatus(HttpStatus.BAD_REQUEST.value());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
//                }
//                return false;
//            }
        }
        return true;
    }
}
