package ru.sejapoe.digitalhotelserver.core.security;

import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import ru.sejapoe.digitalhotelserver.user.session.Session;
import ru.sejapoe.digitalhotelserver.user.session.SessionService;

import java.util.stream.Collectors;

public class SessionInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) {
        if (handler instanceof HandlerMethod handlerMethod
                && handlerMethod.getBean() instanceof SessionServiceHolder bean
                && handlerMethod.hasMethodAnnotation(SessionEncrypted.class)) {
            if (request.getMethod().equalsIgnoreCase("post")) {
                try {
                    Pair<Session, String> decrypt = bean.getSessionService().decrypt(request.getReader().lines().collect(Collectors.joining(System.lineSeparator())));
                    Object o = new ObjectMapper().readValue(decrypt.getSecond(), handlerMethod.getMethodParameters()[1].getParameterType());
                    handlerMethod.getMethod().invoke(bean, decrypt.getFirst().getUser(), o);
                } catch (SessionService.Unauthorized e) {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                } catch (DatabindException e) {
                    LoggerFactory.getLogger(SessionInterceptor.class).warn(e.getMessage());
                    response.setStatus(HttpStatus.BAD_REQUEST.value());
                } catch (Exception e) {
                    e.printStackTrace();
                    response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                }
                return false;
            }
        }
        return true;
    }
}
