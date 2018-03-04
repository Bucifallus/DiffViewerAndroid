package com.android.tomac.unittest;

import android.graphics.Color;
import android.text.SpannableString;

import com.android.tomac.algorithm.DiffBuilder;
import com.android.tomac.algorithm.IDiffBuilder;
import com.android.tomac.algorithm.diff_match_patch;
import com.android.tomac.sidebyside.DiffGenerator;
import com.android.tomac.sidebyside.IDiffGenerator;
import com.android.tomac.sidebyside.ReconstructSideBySideGenerator;
import com.android.tomac.sidebyside.SideBySideDiff;
import com.android.tomac.sidebyside.SideBySideDiffs;
import com.android.tomac.sidebyside.SpannableStringsWrapper;

import junit.framework.TestCase;

import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class SideBySideGeneratorUnitTest extends TestCase {

    ReconstructSideBySideGenerator generator;
    List<diff_match_patch.Diff> diffs;
    IDiffGenerator diffGenerator;
    IDiffBuilder diffBuilder;
    @Override
    protected void setUp() throws Exception {
        generator = new ReconstructSideBySideGenerator();
        diffBuilder = new DiffBuilder();
    }

    public void testNullDiffs() {
        SideBySideDiffs diffs = generator.getDiffs(null);

        assertEquals(1, diffs.size());
    }

    public void testEmptyDiffs() {
        SideBySideDiffs diffs = generator.getDiffs(new LinkedList<diff_match_patch.Diff>());

        assertEquals(1, diffs.size());
    }

    public void testSameNrOfLines() {
        List<diff_match_patch.Diff> diffs = diffBuilder.difference("abcd/n/n", "/nabcd/n", (short)4, 1.0f);
        SideBySideDiffs sDiffs = generator.getDiffs(diffs);

        assertEquals(sDiffs.get(sDiffs.size() - 1).getLeftDiff().operation, diff_match_patch.Operation.LAST);
        assertEquals(sDiffs.get(sDiffs.size() - 1).getRightDiff().operation, diff_match_patch.Operation.LAST);
    }
}