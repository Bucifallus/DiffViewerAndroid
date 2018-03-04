package com.android.tomac.instrumented;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.text.SpannableString;

import com.android.tomac.algorithm.diff_match_patch;
import com.android.tomac.sidebyside.DiffGenerator;
import com.android.tomac.sidebyside.IDiffGenerator;
import com.android.tomac.sidebyside.ReconstructSideBySideGenerator;
import com.android.tomac.sidebyside.SideBySideDiffs;
import com.android.tomac.sidebyside.SpannableStringsWrapper;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class SideBySideGeneratorAndroidUnitTest extends TestCase {

    ReconstructSideBySideGenerator generator;
    List<diff_match_patch.Diff> diffs;
    IDiffGenerator diffGenerator;

    @Before
    public void setUp() throws Exception {
        generator = new ReconstructSideBySideGenerator();

        diffGenerator = DiffGenerator.create()
                .setIDiffGenerator(new ReconstructSideBySideGenerator())
                .build();
    }

    public void testNullDiffs() {
        SideBySideDiffs diffs = generator.getDiffs(null);

        assertEquals(1, diffs.size());
    }

    public void testEmptyDiffs() {
        SideBySideDiffs diffs = generator.getDiffs(new LinkedList<diff_match_patch.Diff>());

        assertEquals(1, diffs.size());
    }

    @Test
    public void testSameNrOfLines() {

        SpannableStringsWrapper sWrapper = diffGenerator.getDiffs("abcd/n/n", "abcd/n");
        SpannableString lString = sWrapper.getLeftText();
        SpannableString rString = sWrapper.getRightText();

        assertEquals(0, 0);
    }
}