package com.android.tomac.SidebySide;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import com.android.tomac.algorithm.diff_match_patch;
import com.android.tomac.utils.StringUtils;

/**
 * Created by cosmin on 18.02.2018.
 */

public class SideBySideGenerator implements ISideBySideDiffGenerator {
    @Override
    public SideBySideDiffs getDiffs(List<diff_match_patch.Diff> diffs) {
        SideBySideDiffs sDiffs = new SideBySideDiffs();

        ListIterator<diff_match_patch.Diff> pointer = diffs.listIterator();
        diff_match_patch.Diff thisDiff = pointer.next();
        List<diff_match_patch.Diff> tempDiffs = new LinkedList<> ();



        //loop through diffs, process on EQUAL diff
        while (thisDiff != null) {
            if (thisDiff.operation == diff_match_patch.Operation.EQUAL || thisDiff.operation == diff_match_patch.Operation.LAST) {
                //can have only delete, only insert
                if (tempDiffs.size() == 1) {
                    diff_match_patch.Diff diff = tempDiffs.get(0);
                    String[] lines =  StringUtils.split(diff.text).toArray(new String[0]); // split in lines
                    boolean endsWithNewLine = diff.text.endsWith(StringUtils.NEWLINE); //if not do not add new line for last line
                    for (int i = 0; i < lines.length; i++) {
                        String line = lines[i];
                        boolean addNewLine = endsWithNewLine || i != lines.length - 1;
                        diff_match_patch.Diff newDiff = new diff_match_patch.Diff(diff.operation, line + (addNewLine ? StringUtils.NEWLINE : ""));
                        newDiff.operation = diff.operation;
                        if (diff.operation == diff_match_patch.Operation.INSERT) {
                            //create a null line on the other side
                            addSDiff(sDiffs, new diff_match_patch.Diff(diff_match_patch.Operation.VIRTUAL, (addNewLine ? StringUtils.NEWLINE : "")), newDiff);
                        } else if (diff.operation == diff_match_patch.Operation.DELETE) {
                            addSDiff(sDiffs, newDiff, new diff_match_patch.Diff(diff_match_patch.Operation.VIRTUAL, (addNewLine ? StringUtils.NEWLINE : "")));
                        } else {
                            //log error
                        }
                    }
                } else if (tempDiffs.size() == 2){
                    //order is delete, insert -> if it changes in the future we have to add error checking
                    //delete
                    diff_match_patch.Diff delDiff = tempDiffs.get(0);
                    diff_match_patch.Diff addDiff = tempDiffs.get(1);

                    List<diff_match_patch.Diff> left = new LinkedList<>(); //will add diffs to this temp list
                    List<diff_match_patch.Diff> right = new LinkedList<>();

                    String[] leftLines = StringUtils.split(delDiff.text).toArray(new String[0]); // split in lines
                    boolean endsWithNewLine = delDiff.text.endsWith(StringUtils.NEWLINE); //if not do not add new line for last line

                    String[] rightLines = StringUtils.split(addDiff.text).toArray(new String[0]);
                    boolean endsWithNewLineRight = addDiff.text.endsWith(StringUtils.NEWLINE);

                    boolean thisDiffEndsWithNewLine = thisDiff.text.endsWith(StringUtils.NEWLINE);
                    boolean thisDiffStartsWithNewLine = thisDiff.text.startsWith(StringUtils.NEWLINE);

                    for (int i = 0; i < Math.max(leftLines.length, rightLines.length) ; i++)
                    {
                        //only add new line to the last line if needed
                        boolean addNewLineLeft = endsWithNewLine || i != leftLines.length - 1;
                        boolean addNewLineRight = endsWithNewLineRight || i != rightLines.length - 1;

                        getDiff(left, leftLines, i, addNewLineLeft, diff_match_patch.Operation.DELETE, thisDiffEndsWithNewLine && thisDiffStartsWithNewLine);
                        getDiff(right, rightLines, i, addNewLineRight, diff_match_patch.Operation.INSERT, thisDiffEndsWithNewLine && thisDiffStartsWithNewLine);

                    }

                    for (int i = 0; i < left.size(); i++)
                    {
                        addSDiff(sDiffs, left.get(i), right.get(i));
                    }
                }
                addSDiff(sDiffs, thisDiff, thisDiff);

                tempDiffs.clear();
            } else {
                tempDiffs.add(thisDiff);
            }

            thisDiff = pointer.hasNext() ? pointer.next() : null;
        }



        return sDiffs;
    }

    private void addSDiff(SideBySideDiffs sDiffs, diff_match_patch.Diff leftDiff, diff_match_patch.Diff rightDiff) {
        SideBySideDiff diff = new SideBySideDiff(leftDiff, rightDiff);
        sDiffs.add(diff);
    }

    private void getDiff(List<diff_match_patch.Diff> diffs, String[] lines, int i, boolean addNewLine, diff_match_patch.Operation operation, boolean doNotReverse) {
        diff_match_patch.Diff newDelDiff;
        if (lines.length > i) {
            newDelDiff = new diff_match_patch.Diff(operation, lines[i] + (addNewLine ? StringUtils.NEWLINE : ""));
            diffs.add(newDelDiff);
        } else {
            newDelDiff = new diff_match_patch.Diff(diff_match_patch.Operation.VIRTUAL, addNewLine ? StringUtils.NEWLINE : "");
            diffs.add(doNotReverse ? diffs.size() : diffs.size() - 1, newDelDiff); // add before changed text
        }
    }

}
