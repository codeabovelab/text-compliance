package com.codeabovelab.tpc.text;

import lombok.Value;

/**
 * Default implementation of {@link TextCoordinates }
 */
@Value
public final class TextCoordinatesImpl implements TextCoordinates {
    private final String textualId;
    private final int offset;
    private final int length;
}
