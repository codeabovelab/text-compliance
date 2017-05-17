package com.codeabovelab.tpc.core.text;

import lombok.Data;

/**
 * Simple text implementation.
 */
@Data
public final class TextImpl implements Text {

    private final String id;
    private final String data;

    @Override
    public int getLength() {
        return data.length();
    }

    @Override
    public TextCoordinates getCoordinates(int offset, int length) {
        if(length == -1) {
            length = getLength();
        }
        return new TextCoordinatesImpl(id, offset, length);
    }
}
