package com.android.tomac.algorithm;

import com.android.tomac.algorithm.diff_match_patch.Diff;

import java.util.LinkedList;

import static com.android.tomac.algorithm.diff_match_patch.Operation.LAST;

/**
 * Author: cosmin
 * Date  : 04.03.2018.
 */

public class DiffBuilder implements IDiffBuilder {
    diff_match_patch dmp;

    public LinkedList<Diff> difference(String leftText, String rightText, short editCost, float timeOut) {
        if (dmp == null) {
            dmp = new diff_match_patch();
            dmp.Diff_EditCost = editCost;
            dmp.Diff_Timeout = timeOut;
        }

        LinkedList<Diff> diffs = dmp.diff_main(leftText, rightText, false);
        dmp.diff_cleanupSemantic(diffs);

        return diffs;
    }
}
