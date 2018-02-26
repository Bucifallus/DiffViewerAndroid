package com.android.tomac.algorithm;

import java.util.LinkedList;

/**
 * Author: cosmin
 * Date  : 26.02.2018.
 */

interface Idiff_match_patch {
    LinkedList<diff_match_patch.Diff> diff_main(String text1, String text2);

    LinkedList<diff_match_patch.Diff> diff_main(String text1, String text2,
                                                boolean checklines);
}
