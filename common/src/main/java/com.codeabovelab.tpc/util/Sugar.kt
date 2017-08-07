package com.codeabovelab.tpc.util;


fun <S: Any, R> S?.letIfNotNull(consumer: (S) -> R) : R? {
    if(this == null) {
        return null
    }
    return consumer(this)
}