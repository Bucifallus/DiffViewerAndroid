package com.android.tomac.main;

import com.android.tomac.sidebyside.DiffGenerator;
import com.android.tomac.sidebyside.ReconstructSideBySideGenerator;
import com.android.tomac.sidebyside.IDiffGenerator;
import com.android.tomac.sidebyside.SpannableStringsWrapper;
import com.android.tomac.unittest.R;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    private TextView cTextView;
    private TextView cTextView2;

    String leftFile = "";
    String rightFile = "";

    private Draw draw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cTextView = (TextView) findViewById(R.id.textView);
        cTextView.setHorizontallyScrolling(true);
        cTextView.setMovementMethod(new ScrollingMovementMethod());

        cTextView2 = (TextView) findViewById(R.id.textView2);
        cTextView2.setHorizontallyScrolling(true);
        cTextView2.setMovementMethod(new ScrollingMovementMethod());

        setupScrolling();

        getText();

        IDiffGenerator diffGenerator = DiffGenerator.create()
                .setEditCost((short)4)
                .setTimeOut(1.0f)
                .setIDiffGenerator(new ReconstructSideBySideGenerator())
                .setDeletedCharColor(Color.RED)
                .setInsertedCharColor(Color.YELLOW)
                .setEqualCharColor(Color.WHITE)
                .build();
        SpannableStringsWrapper spannableStrings = diffGenerator.getDiffs(leftFile, rightFile);

        cTextView.setText(spannableStrings.getLeftText());
        cTextView2.setText(spannableStrings.getRightText());

        draw =  (Draw) findViewById(R.id.view3);
        draw.setRelativeTextViewer(cTextView);
    }

    @NonNull
    private void getText() {
        InputStream is = getResources().openRawResource(R.raw.file9);
        InputStream is2 = getResources().openRawResource(R.raw.file10);

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder out = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                out.append(line + "\n");   // add everything to StringBuilder
            }
        } catch (IOException ex) {

        }

        BufferedReader reader2 = new BufferedReader(new InputStreamReader(is2));
        StringBuilder out2 = new StringBuilder();

        try {
            while ((line = reader2.readLine()) != null) {
                out2.append(line + "\n");   // add everything to StringBuilder
            }
        } catch (IOException ex) {

        }

        leftFile = out.toString();
        rightFile = out2.toString();
    }

    private void setupScrolling() {
        IScrollNotifier view;
        ScrollManager scrollManager = new ScrollManager();

        // timeline horizontal scroller
        view = (IScrollNotifier) findViewById(R.id.textView);
        scrollManager.addScrollClient(view);

        // services vertical scroller
        view = (IScrollNotifier) findViewById(R.id.textView2);
        scrollManager.addScrollClient(view);
    }

}
