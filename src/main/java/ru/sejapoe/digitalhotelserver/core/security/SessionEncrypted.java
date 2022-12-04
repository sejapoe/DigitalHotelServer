package ru.sejapoe.digitalhotelserver.core.security;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SessionEncrypted {

}
