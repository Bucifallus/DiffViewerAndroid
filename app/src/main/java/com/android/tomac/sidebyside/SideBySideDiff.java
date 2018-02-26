package com.android.tomac.sidebyside;

import com.android.tomac.algorithm.diff_match_patch.Diff;

/**
 * Created by cosmin on 18.02.2018.
 */

public class SideBySideDiff {
    public SideBySideDiff(Diff leftDiff, Diff rightDiff) {
        this.leftDiff = leftDiff;
        this.rightDiff = rightDiff;
    }

    public Diff getLeftDiff() {
        return leftDiff;
    }

    public Diff getRightDiff() {
        return rightDiff;
    }

    Diff leftDiff;
    Diff rightDiff;

    @Override
    public String toString() {
        return "SideBySideDiff{" +
                "leftDiff=" + leftDiff +
                ", rightDiff=" + rightDiff +
                '}';
    }
}
