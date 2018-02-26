package com.android.tomac.SidebySide;

import android.text.SpannableString;

/**
 * Created by cosmin on 18.02.2018.
 */

public class SpannableStringsWrapper {
    SpannableString leftText;
    SpannableString rightText;

    public SpannableStringsWrapper(SpannableString leftText, SpannableString rightText) {
        this.leftText = leftText;
        this.rightText = rightText;
    }

    public SpannableString getLeftText() {
        return leftText;
    }

    public SpannableString getRightText() {
        return rightText;
    }

}
