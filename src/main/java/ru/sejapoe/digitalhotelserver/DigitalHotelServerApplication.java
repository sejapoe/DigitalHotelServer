package ru.sejapoe.digitalhotelserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.sejapoe.digitalhotelserver.core.security.SessionInterceptor;
import ru.sejapoe.digitalhotelserver.core.security.UserSessionResolver;

import java.util.List;

@SpringBootApplication
public class DigitalHotelServerApplication implements WebMvcConfigurer {

    public static void main(String[] args) {
        SpringApplication.run(DigitalHotelServerApplication.class, args);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SessionInterceptor());
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new UserSessionResolver());
    }
}
