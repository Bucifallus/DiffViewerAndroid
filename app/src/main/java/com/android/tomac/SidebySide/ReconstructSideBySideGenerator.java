package com.android.tomac.SidebySide;

import com.android.tomac.utils.StringUtils;
import com.android.tomac.algorithm.diff_match_patch.Diff;
import com.android.tomac.algorithm.diff_match_patch.Operation;
import com.android.tomac.utils.CorrectIterator;

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
        public List<Diff> getDiffs() {
            return delDiffs;
        }

        public void setDelDiffs(List<Diff> delDiffs) {
            this.delDiffs = delDiffs;
        }

        List<Diff> delDiffs = new LinkedList<>();

        public void clear() {
            getDiffs().clear();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (Diff d :
                    delDiffs) {
                sb.append(d.text);
            }
            return sb.toString();
        }
    }

    String[] leftLines;
    String[] rightLines;

    @Override
    public SideBySideDiffs getDiffs(List<Diff> diffs) {
        SideBySideDiffs sDiffs = new SideBySideDiffs();
        diffs.add(new Diff(LAST, ""));

        ListIterator<Diff> pointer = diffs.listIterator();
        Diff thisDiff = pointer.next();

        LinkedList<Diff> delDiffs = new LinkedList<>();
        LinkedList<Diff> insDiffs = new LinkedList<>();
        int delCounter = 0;
        int insCounter = 0;
        //loop through diffs, process on EQUAL diff
        while (thisDiff != null) {
            //thisDiff = pointer.previous();
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
                case VIRTUAL:
                    break;
                case LAST:
                    //addSDiff(sDiffs, thisDiff, thisDiff);
                    break;
            }
            thisDiff = pointer.hasNext() ? pointer.next() : null;
        }

        List<Line> leftLines = getLines(delDiffs);
        List<Line> rightLines = getLines(insDiffs);

        int max =  Math.max(leftLines.size(), rightLines.size());
        Pointer rightPtr, leftPtr;
        rightPtr = new Pointer();
        leftPtr = new Pointer();


        for (int i = 0; i < max; i++) {
            Line dummy = new Line();
            dummy.getDiffs().add(new Diff(VIRTUAL, ""));
            Line left = i >= leftLines.size() ? dummy : leftLines.get(i);
            Line right = i >= rightLines.size() ? dummy : rightLines.get(i);

            if (left.getDiffs().get(0).operation == EQUAL) {
                //could be anchor, search for it in right
                Pointer searchPtr = search(left.getDiffs().get(0), rightLines, rightPtr);
                if (searchPtr.startLineIndex != i && searchPtr != null) {
                    //align
                    for (int j = 0; j < i - searchPtr.startLineIndex; j++) {
                        rightLines.add(searchPtr.startLineIndex, dummy);
                    }

                }
                rightPtr.startLineIndex = i;
            }
        }

        for (int i = 0; i < max; i++) {
            Line dummy = new Line();
            dummy.getDiffs().add(new Diff(VIRTUAL, ""));
            Line left = i >= leftLines.size() ? dummy : leftLines.get(i);
            Line right = i >= rightLines.size() ? dummy : rightLines.get(i);

            if (right.getDiffs().get(0).operation == EQUAL) {
                //could be anchor, search for it in right
                Pointer searchPtr = search(right.getDiffs().get(0), leftLines, leftPtr);
                if (searchPtr.startLineIndex != i && leftPtr != null) {
                    //align
                    for (int j = 0; j < i - searchPtr.startLineIndex; j++) {
                        leftLines.add(searchPtr.startLineIndex, dummy);
                    }

                }
                leftPtr.startLineIndex=i;
            }
        }

        getsDiffsFromLines(sDiffs, leftLines, rightLines);
        return sDiffs;
    }

    private Pointer search(Diff diff, List<Line> rightLines, Pointer searchPointer) {
        Pointer p = new Pointer();
        for (int i = searchPointer.startLineIndex + 1; i < rightLines.size(); i++) {
            Line l = rightLines.get(i);
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

    private List<Line> getLines(LinkedList<Diff> delDiffs) {
        List<Line> lines = new LinkedList<>();
        Line l = new Line();

        CorrectIterator<Diff> pointer = new CorrectIterator<>(delDiffs.listIterator());
        Diff del = pointer.next();
        boolean prevEndsWithNewLine = false;
        boolean lineIsCompleted = false;
        while (del != null) {
            if (del.text.contains(StringUtils.NEWLINE)) {
                List<String> split = StringUtils.split(del.text);
                boolean endWithNewLine = del.text.endsWith(StringUtils.NEWLINE);
                if (pointer.hasPrevious() && !prevEndsWithNewLine) {
                    Diff prevDiff = pointer.previous();
                    prevEndsWithNewLine = prevDiff.text.endsWith(StringUtils.NEWLINE);
                    pointer.next();
                }

                boolean nextStartsWithNewLine = false;
                if (pointer.hasNext()) {
                    Diff nextDiff = pointer.next();
                    nextStartsWithNewLine = nextDiff.text.startsWith(StringUtils.NEWLINE);
                    pointer.previous();
                }

                //if previous is not finished add to temp line first line
                //if this one is not finished add to temp
                for (int i = 0; i < split.size(); i++) {
                    if (i == 0 && !lineIsCompleted) {
                        l.getDiffs().add(new Diff(del.operation, split.get(i).replaceAll(StringUtils.NEWLINE, "")));
                        Line tempL = new Line();
                        tempL.getDiffs().addAll(l.getDiffs());
                        lines.add(tempL);
                        l.clear();
                    } else if (i == split.size() - 1 && !endWithNewLine) {
                        l.getDiffs().add(new Diff(del.operation, split.get(i).replaceAll(StringUtils.NEWLINE, "")));
                    } else {
                        l.getDiffs().add(new Diff(del.operation, split.get(i).replaceAll(StringUtils.NEWLINE, "")));
                        Line tempL = new Line();
                        tempL.getDiffs().addAll(l.getDiffs());
                        lines.add(tempL);
                        l.clear();
                    }
                }

                lineIsCompleted = endWithNewLine;
            } else {

                l.getDiffs().add(new Diff(del.operation, del.text.replaceAll(StringUtils.NEWLINE, "")));
            }
            del = pointer.hasNext() ? pointer.next() : null;
        }

        return lines;
    }

    private void addSDiff(SideBySideDiffs sDiffs, Diff leftDiff, Diff rightDiff) {
        SideBySideDiff diff = new SideBySideDiff(leftDiff, rightDiff);
        sDiffs.add(diff);
    }
}

