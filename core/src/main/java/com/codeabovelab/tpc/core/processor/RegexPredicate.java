package com.codeabovelab.tpc.core.processor;

import com.codeabovelab.tpc.core.text.Text;
import com.codeabovelab.tpc.core.text.TextCoordinates;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 */
public final class RegexPredicate implements RulePredicate {
    private final Pattern pattern;

    public RegexPredicate(String regexp) {
        this.pattern = Pattern.compile(regexp);
    }

    @Override
    public List<TextCoordinates> test(PredicateContext pc, Text text) {
        Matcher matcher = pattern.matcher(text.getData());
        List<TextCoordinates> list = null;
        while(matcher.find()) {
            int offset = matcher.start();
            int len = matcher.end() - offset;
            TextCoordinates coord = text.getCoordinates(offset, len);
            if(list == null) {
                list = new ArrayList<>();
            }
            list.add(coord);
        }
        if(list == null) {
            // we always must return non null value
            return Collections.emptyList();
        }
        return list;
    }
}
