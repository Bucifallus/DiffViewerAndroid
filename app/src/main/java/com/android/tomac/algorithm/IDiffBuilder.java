package com.android.tomac.algorithm;

import com.android.tomac.algorithm.diff_match_patch;

import java.util.LinkedList;
import java.util.List;

/**
 * Author: cosmin
 * Date  : 04.03.2018.
 */

public interface IDiffBuilder {
    public LinkedList<diff_match_patch.Diff> difference(String leftText, String rightText, short editCost, float timeOut);
}
