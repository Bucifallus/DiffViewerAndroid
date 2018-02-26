package com.android.tomac.sidebyside;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;

import java.util.LinkedList;
import java.util.List;

import com.android.tomac.algorithm.diff_match_patch;

import static com.android.tomac.algorithm.diff_match_patch.*;
import static com.android.tomac.algorithm.diff_match_patch.Operation.LAST;

/**
 * Created by cosmin on 18.02.2018.
 */

public class DiffGenerator implements IDiffGenerator {
    public static class Builder {
        private short editCost = 4;
        private float timeOut = 1.0f;
        private int deletedCharColor = Color.GREEN;
        private int insertedCharColor = Color.BLUE;
        private int equalCharColor = Color.WHITE;

        ISideBySideDiffGenerator iDiffGenerator = new SideBySideGenerator();

        private Builder(){

        }

        public IDiffGenerator build() {
            return new DiffGenerator(this);
        }

        public Builder setEditCost(short cost) {
            editCost = cost;
            return this;
        }

        public Builder setTimeOut(float time) {
            timeOut = time;
            return this;
        }

        public Builder setIDiffGenerator(ISideBySideDiffGenerator iDiffGenerator) {
            this.iDiffGenerator = iDiffGenerator;
            return this;
        }

        public Builder setDeletedCharColor(int deletedCharColor) {
            this.deletedCharColor = deletedCharColor;
            return this;
        }

        public Builder setInsertedCharColor(int insertedCharColor) {
            this.insertedCharColor = insertedCharColor;
            return this;
        }

        public Builder setEqualCharColor(int equalCharColor) {
            this.equalCharColor = equalCharColor;
            return this;
        }
    }

    private final short editCost;
    private final float timeOut;
    private final int deletedCharColor;
    private final int insertedCharColor;
    private final int equalCharColor;

    List<Diff> leftDiffs = new LinkedList<>();
    List<Diff> rightDiffs = new LinkedList<>();
    ISideBySideDiffGenerator iDiffGenerator;
    diff_match_patch dmp;

    public DiffGenerator(Builder builder) {
        editCost = builder.editCost;
        timeOut = builder.timeOut;
        iDiffGenerator = builder.iDiffGenerator;
        deletedCharColor = builder.deletedCharColor;
        insertedCharColor = builder.insertedCharColor;
        equalCharColor = builder.equalCharColor;
    }

    public static Builder create() {
        return new Builder();
    }

    public SpannableStringsWrapper getDiffs(String leftText, String rightText) {
        SideBySideDiffs sDiffs = iDiffGenerator.getDiffs(difference(leftText, rightText));

        String modifiedStr1 = sDiffs.getLeftText();
        String modifiedStr2 = sDiffs.getRightText();

        SpannableString text1 = new SpannableString(modifiedStr1);
        SpannableString text2 = new SpannableString(modifiedStr2);

        int leftCounter = 0, rightCounter = 0;
        for (int j = 0; j < sDiffs.size(); j++) {
            SideBySideDiff sideBySideDiff = sDiffs.get(j);
            Diff leftDiff = sideBySideDiff.getLeftDiff();
            Diff rightDiff = sideBySideDiff.getRightDiff();

            switch (leftDiff.operation) {
                case DELETE:
                    text1.setSpan(new BackgroundColorSpan(deletedCharColor), leftCounter, leftCounter + leftDiff.text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    leftCounter += leftDiff.text.length();
                    break;
                case INSERT:
                    text2.setSpan(new BackgroundColorSpan(insertedCharColor), rightCounter, rightCounter + leftDiff.text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    rightCounter += leftDiff.text.length();
                    break;
                case EQUAL:
                    text1.setSpan(new BackgroundColorSpan(equalCharColor), leftCounter, leftCounter + leftDiff.text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    leftCounter += leftDiff.text.length();
                    break;
                case VIRTUAL:
                    text1.setSpan(new BackgroundColorSpan(Color.RED), leftCounter, leftCounter + leftDiff.text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    leftCounter += leftDiff.text.length();
                    break;
            }

            switch (rightDiff.operation) {
                case DELETE:
                    text1.setSpan(new BackgroundColorSpan(deletedCharColor), leftCounter, leftCounter + rightDiff.text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    leftCounter += rightDiff.text.length();
                    break;
                case INSERT:
                    text2.setSpan(new BackgroundColorSpan(insertedCharColor), rightCounter, rightCounter + rightDiff.text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    rightCounter += rightDiff.text.length();
                    break;
                case EQUAL:
                    text2.setSpan(new BackgroundColorSpan(equalCharColor), rightCounter, rightCounter + rightDiff.text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    rightCounter += rightDiff.text.length();
                    break;
                case VIRTUAL:
                    text2.setSpan(new BackgroundColorSpan(Color.RED), rightCounter, rightCounter + rightDiff.text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    rightCounter += rightDiff.text.length();
                    break;
            }

        }

        SpannableStringsWrapper sWrapper = new SpannableStringsWrapper(text1, text2);
        return sWrapper;
    }

    private LinkedList<Diff> difference(String leftText, String rightText) {
        if (dmp == null) {
            dmp = new diff_match_patch();
            dmp.Diff_EditCost = editCost;
            dmp.Diff_Timeout = timeOut;
        }

        LinkedList<Diff> diffs = dmp.diff_main(leftText, rightText, false);
        dmp.diff_cleanupEfficiency(diffs);

        diffs.add(new Diff(LAST, ""));

        return diffs;
    }
}
