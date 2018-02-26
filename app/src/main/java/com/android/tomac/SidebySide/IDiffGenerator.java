package com.android.tomac.SidebySide;

/**
 * Created by cosmin on 18.02.2018.
 */

public interface IDiffGenerator {
    SpannableStringsWrapper getDiffs(String leftText, String rightText);
}
