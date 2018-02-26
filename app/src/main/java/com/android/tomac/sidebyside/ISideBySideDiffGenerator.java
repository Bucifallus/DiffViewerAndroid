package com.android.tomac.sidebyside;

import com.android.tomac.algorithm.diff_match_patch;

import java.util.List;

/**
 * Created by cosmin on 18.02.2018.
 */

interface ISideBySideDiffGenerator {
    SideBySideDiffs getDiffs(List<diff_match_patch.Diff> diffs);
}
