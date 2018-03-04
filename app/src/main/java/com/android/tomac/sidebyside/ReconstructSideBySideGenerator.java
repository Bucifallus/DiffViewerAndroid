package com.android.tomac.sidebyside;

import com.android.tomac.utils.StringUtils;
import com.android.tomac.algorithm.diff_match_patch.Diff;
import com.android.tomac.utils.CustomIterator;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import static com.android.tomac.algorithm.diff_match_patch.Operation.EQUAL;
import static com.android.tomac.algorithm.diff_match_patch.Operation.LAST;
import static com.android.tomac.algorithm.diff_match_patch.Operation.VIRTUAL;

/**
 * Created by TomaC on 19.02.2018.
 */

public class ReconstructSideBySideGenerator implements ISideBySideDiffGenerator {

    private class Pointer {
        int startLineIndex = -1;
    }

    private class Line {
        private List<Diff> getDiffs() {
            return delDiffs;
        }

        List<Diff> delDiffs = new LinkedList<>();

        void clear() {
            getDiffs().clear();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (Diff d : delDiffs) {
                sb.append(d.text);
            }
            return sb.toString();
        }
    }

    @Override
    public SideBySideDiffs getDiffs(List<Diff> diffs) {
        SideBySideDiffs sDiffs = new SideBySideDiffs();

        if (diffs == null) {
            diffs = new LinkedList<>();
        }

        diffs.add(new Diff(VIRTUAL, ""));

        if (diffs.size() == 1) { //if null or empty
            sDiffs.add(new SideBySideDiff(diffs.get(0), diffs.get(0)));
        } else {
            ListIterator<Diff> pointer = diffs.listIterator();
            Diff thisDiff = pointer.next();

            LinkedList<Diff> delDiffs = new LinkedList<>(); //left
            LinkedList<Diff> insDiffs = new LinkedList<>(); //right

            //construct left/right diffs
            while (thisDiff != null) {
                switch (thisDiff.operation) {
                    case DELETE:
                        delDiffs.add(thisDiff);
                        break;
                    case INSERT:
                        insDiffs.add(thisDiff);
                        break;
                    case EQUAL:
                        delDiffs.add(thisDiff);
                        insDiffs.add(thisDiff);
                        break;
                    case LAST:
                        delDiffs.add(thisDiff);
                        insDiffs.add(thisDiff);
                        break;
                }
                thisDiff = pointer.hasNext() ? pointer.next() : null;
            }

            List<Line> leftLines = getLines(delDiffs);
            List<Line> rightLines = getLines(insDiffs);

            int max = Math.max(leftLines.size(), rightLines.size());

            align(leftLines, rightLines, max);
            align(rightLines, leftLines, max);

            getsDiffsFromLines(sDiffs, leftLines, rightLines);
            sDiffs.add(new SideBySideDiff(new Diff(LAST, ""), new Diff(LAST, "")));
        }

        return sDiffs;
    }

    private void align(List<Line> toAlignLines, List<Line> alignWithLines, int max) {
        Pointer rightPtr = new Pointer();
        for (int i = 0; i < max; i++) {
            Line dummy = new Line();
            dummy.getDiffs().add(new Diff(VIRTUAL, ""));
            Line left = i >= toAlignLines.size() ? dummy : toAlignLines.get(i);

            if (left.getDiffs().get(0).operation == EQUAL) {
                //could be anchor, search for it in right
                Pointer searchPtr = search(left.getDiffs().get(0), alignWithLines, rightPtr);
                if (searchPtr.startLineIndex != i && searchPtr != null) {
                    //align
                    for (int j = 0; j < i - searchPtr.startLineIndex; j++) {
                        alignWithLines.add(searchPtr.startLineIndex, dummy);
                    }

                }
                rightPtr.startLineIndex = i;
            }
        }
    }

    private Pointer search(Diff diff, List<Line> lines, Pointer searchPointer) {
        Pointer p = new Pointer();
        for (int i = searchPointer.startLineIndex + 1; i < lines.size(); i++) {
            Line l = lines.get(i);
            List<Diff> diffs =  l.getDiffs();
            for (int j = 0; j < diffs.size(); j++) {
                boolean isPresent = diff.equals(diffs.get(j));
                if (isPresent) {
                    p.startLineIndex = i;
                    return p;
                }
            }
        }

        return null;
    }

    private void getsDiffsFromLines(SideBySideDiffs sDiffs, List<Line> leftLines, List<Line> rightLines) {
        for (int i = 0; i < Math.max(leftLines.size(), rightLines.size()); i++) {
            Line left = i >= leftLines.size() ? new Line() : leftLines.get(i);
            Line right = i >= rightLines.size() ? new Line() : rightLines.get(i);
            List<Diff> leftDiffs = left.getDiffs();
            List<Diff> rightDiffs = right.getDiffs();

            for (int j = 0; j < Math.max(leftDiffs.size(), rightDiffs.size()); j++) {
                Diff leftDiff = j >= leftDiffs.size() ? new Diff(VIRTUAL, "") : leftDiffs.get(j);
                Diff rightDiff = j >= rightDiffs.size() ? new Diff(VIRTUAL, "") : rightDiffs.get(j);
                addSDiff(sDiffs, leftDiff, rightDiff);
            }

            //dummy new line
            addSDiff(sDiffs, new Diff(VIRTUAL, StringUtils.NEWLINE), new Diff(VIRTUAL, StringUtils.NEWLINE));
        }
    }

    private List<Line> getLines(LinkedList<Diff> diffs) {
        List<Line> lines = new LinkedList<>();
        Line l = new Line(); //add diffs to this temp line
        boolean prevEndsWithNewLine = false;
        boolean lineIsCompleted = false;

        CustomIterator<Diff> pointer = new CustomIterator<>(diffs.listIterator());
        Diff diff = pointer.next();

        while (diff != null) {
            if (diff.text.contains(StringUtils.NEWLINE)) {
                List<String> split = StringUtils.split(diff.text);
                boolean endWithNewLine = diff.text.endsWith(StringUtils.NEWLINE);
                if (pointer.hasPrevious() && !prevEndsWithNewLine) {
                    Diff prevDiff = pointer.previous();
                    prevEndsWithNewLine = prevDiff.text.endsWith(StringUtils.NEWLINE);
                    pointer.next();
                }

                for (int i = 0; i < split.size(); i++) {
                   if (i == split.size() - 1 && !endWithNewLine) {
                        l.getDiffs().add(new Diff(diff.operation, split.get(i).replaceAll(StringUtils.NEWLINE, "")));
                    } else {
                        l.getDiffs().add(new Diff(diff.operation, split.get(i).replaceAll(StringUtils.NEWLINE, "")));
                        Line tempL = new Line();
                        tempL.getDiffs().addAll(l.getDiffs());
                        lines.add(tempL);
                        l.clear();
                    }
                }

                lineIsCompleted = endWithNewLine;
            } else {
                l.getDiffs().add(new Diff(diff.operation, diff.text.replaceAll(StringUtils.NEWLINE, "")));
            }
            diff = pointer.hasNext() ? pointer.next() : null;
        }

        if (l.getDiffs().size() != 0) {
            Line tempL = new Line();
            tempL.getDiffs().addAll(l.getDiffs());
            lines.add(tempL);
            l.clear();
        }

        return lines;
    }

    private void addSDiff(SideBySideDiffs sDiffs, Diff leftDiff, Diff rightDiff) {
        SideBySideDiff diff = new SideBySideDiff(leftDiff, rightDiff);
        sDiffs.add(diff);
    }
}

