package com.android.tomac.sidebyside;

/**
 * Created by cosmin on 18.02.2018.
 */

public interface IDiffGenerator {
    SpannableStringsWrapper getDiffs(String leftText, String rightText);
}
