package com.deloitte.elrr.util;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;

import com.openpojo.random.RandomGenerator;

public class LocalDateRandomGenerator implements RandomGenerator {
    private static final Class<?>[] TYPES = new Class<?>[] { LocalDate.class };

    public Object doGenerate(Class<?> type) {
        return LocalDate.now();
    }

    public Collection<Class<?>> getTypes() {
        return Arrays.asList(TYPES);
    }
}
