package com.ddangme.sns.util;

import java.util.Optional;

public class ClassUtils {

    public static <T> Optional<T> getSafeCastInstance(Object o, Class<T> clazz) {
        if (clazz != null && clazz.isInstance(o)) {
            return Optional.of(clazz.cast(o));
        }

        return Optional.empty();
    }
}
