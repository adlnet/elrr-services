package com.deloitte.elrr.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.web.bind.annotation.CrossOrigin;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
@CrossOrigin
public @interface CrossOriginsList {
    /**
     *
     * @return String[]
     */
    String[] crossOrigins() default { "http://localhost:3001",
            "http://localhost:5000" };
}
