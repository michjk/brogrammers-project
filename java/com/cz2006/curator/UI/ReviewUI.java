package com.cz2006.curator.UI;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ProgressBar;


import com.cz2006.curator.Objects.Review;
import com.cz2006.curator.R;

import java.util.ArrayList;

/**
 * ReviewUI is a boundary class for displaying list of review of a museum.
 * The reviews are from a museum object.
 */
public class ReviewUI extends AppCompatActivity {

    private RecyclerView rv;
    private LinearLayoutManager llm;
    private ArrayList<Review> reviewList;
    private ReviewAdapter radapter;
    private String museumName;
    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_review_ui);
        reviewList = (ArrayList<Review>)getIntent().getSerializableExtra("reviews");

        for(Review r:reviewList){
            Log.e("author", r.getAuthorName());
        }

        rv = (RecyclerView) findViewById(R.id.rr);

        llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(llm);

        radapter = new ReviewAdapter(reviewList);
        rv.setAdapter(radapter);
    }


}
