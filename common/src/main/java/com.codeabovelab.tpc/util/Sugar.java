package com.codeabovelab.tpc.util;

import java.util.Collection;

/**
 */
public final class Sugar {
    public static boolean isEmpty(Collection<?> c) {
        return c == null || c.isEmpty();
    }
}
