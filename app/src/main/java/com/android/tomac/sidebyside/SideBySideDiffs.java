package com.android.tomac.sidebyside;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by cosmin on 18.02.2018.
 */

public class SideBySideDiffs {
    private List<SideBySideDiff> sDiffs = new LinkedList<SideBySideDiff>();

    public void add(SideBySideDiff s) {
        sDiffs.add(s);
    }

    public int size() {
        return sDiffs.size();
    }

    public String getLeftText() {
        StringBuilder sb = new StringBuilder();
        for (SideBySideDiff diff:
             sDiffs) {
            sb.append((diff.getLeftDiff().text));
        }

        return sb.toString();
    }

    public String getRightText() {
        StringBuilder sb = new StringBuilder();
        for (SideBySideDiff diff:
                sDiffs) {
            sb.append((diff.getRightDiff().text));
        }

        return sb.toString();
    }

    public SideBySideDiff get(int i) {
        return sDiffs.get(i);
    }

    @Override
    public String toString() {
        return "SideBySideDiffs{" +
                "sDiffs=" + sDiffs +
                '}';
    }
}
