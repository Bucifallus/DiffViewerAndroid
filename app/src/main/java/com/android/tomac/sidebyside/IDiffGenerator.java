package com.android.tomac.sidebyside;

import com.android.tomac.algorithm.diff_match_patch;

import java.util.LinkedList;

/**
 * Created by cosmin on 18.02.2018.
 */

public interface IDiffGenerator {
    SpannableStringsWrapper getDiffs(String leftText, String rightText);

    LinkedList<diff_match_patch.Diff> difference(String leftText, String rightText);
}
