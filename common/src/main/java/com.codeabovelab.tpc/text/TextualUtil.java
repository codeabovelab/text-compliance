package com.codeabovelab.tpc.text;

/**
 */
public final class TextualUtil {
    /**
     * Read textual to string. It may doing it in non efficient way, therefore you can use it only for
     * debugging and testing.
     * @param textual textual or null
     * @return string or null
     */
    public static String read(Textual textual) {
        if(textual == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        textual.read(ctx -> sb.append(ctx.getData()));
        return sb.toString();
    }
}
