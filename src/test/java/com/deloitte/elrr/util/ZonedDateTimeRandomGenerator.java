package com.deloitte.elrr.util;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collection;

import com.openpojo.random.RandomGenerator;

public class ZonedDateTimeRandomGenerator implements RandomGenerator {
    private static final Class<?>[] TYPES = new Class<?>[] { ZonedDateTime.class };

    public Object doGenerate(Class<?> type) {
        return ZonedDateTime.now();
    }

    public Collection<Class<?>> getTypes() {
        return Arrays.asList(TYPES);
    }
}
