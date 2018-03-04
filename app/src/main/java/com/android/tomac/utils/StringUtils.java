package com.android.tomac.utils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by cosmin on 18.02.2018.
 */

public class StringUtils {
    public static final String NEWLINE = "\n";

    public static List<String> split(String content) {
        List<String> lines = new LinkedList<>();
        int lineStart = 0;
        int lineEnd = -1;
        String line;
        int lineCount = 0;
        StringBuilder chars = new StringBuilder();

        while (lineEnd < content.length() - 1) {
            lineEnd = content.indexOf(NEWLINE, lineStart);
            if (lineEnd == -1) {
                lineEnd = content.length() - 1;
            }
            line = content.substring(lineStart, lineEnd + 1);
            lines.add(line);
            lineCount++;
            lineStart = lineEnd + 1;
        }
        return lines;
    }
}
