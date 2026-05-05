package com.danhaywood.cfct.sqlserver;

import com.danhaywood.cfct.model.ColumnMetadata;
import com.danhaywood.cfct.spi.ColumnValueNormalizer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColumnValueNormalizerUsingExtendedProperties implements ColumnValueNormalizer {

    private final Map<String, Pattern> maskPatternByMask = new ConcurrentHashMap<>();

    @Override
    public NormalizedValues normalize(
            final ColumnMetadata columnMetadata,
            final String leftValue,
            final String rightValue) {
        final String mask = columnMetadata.normalizeMaskExtendedPropertyValue();
        if (mask == null || mask.isBlank()) {
            return new NormalizedValues(leftValue, rightValue);
        }

        final Pattern pattern = toPattern(mask);
        if (pattern == null) {
            return new NormalizedValues(leftValue, rightValue);
        }

        return new NormalizedValues(
                scrubValue(leftValue, mask, pattern),
                scrubValue(rightValue, mask, pattern));
    }

    private Pattern toPattern(final String mask) {
        return maskPatternByMask.computeIfAbsent(mask, this::compilePattern);
    }

    private Pattern compilePattern(final String mask) {
        final StringBuilder regex = new StringBuilder();
        int i = 0;
        while (i < mask.length()) {
            final char ch = mask.charAt(i);
            if (isSupportedToken(ch)) {
                int j = i + 1;
                while (j < mask.length() && mask.charAt(j) == ch) {
                    j++;
                }
                regex.append("\\d{").append(j - i).append('}');
                i = j;
                continue;
            }
            if (ch == ':' || ch == '.') {
                regex.append("[\\.:]");
            } else {
                regex.append(Pattern.quote(String.valueOf(ch)));
            }
            i++;
        }
        return Pattern.compile(regex.toString());
    }

    private String scrubValue(final String value, final String mask, final Pattern pattern) {
        if (value == null || value.isBlank()) {
            return value;
        }
        final Matcher matcher = pattern.matcher(value);
        if (!matcher.find()) {
            return value;
        }
        return matcher.replaceAll(Matcher.quoteReplacement(mask));
    }

    private boolean isSupportedToken(final char ch) {
        return ch == 'y' || ch == 'M' || ch == 'd' || ch == 'h' || ch == 'H' || ch == 'm' || ch == 's' || ch == 'S';
    }
}
