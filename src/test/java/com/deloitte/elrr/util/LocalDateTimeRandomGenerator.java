package com.deloitte.elrr.util;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;

import com.openpojo.random.RandomGenerator;

public class LocalDateTimeRandomGenerator implements RandomGenerator {
    private static final Class<?>[] TYPES = new Class<?>[] { LocalDateTime.class };

    public Object doGenerate(Class<?> type) {
        return LocalDateTime.now();
    }

    public Collection<Class<?>> getTypes() {
        return Arrays.asList(TYPES);
    }
}
