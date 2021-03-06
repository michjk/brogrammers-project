package com.cz2006.curator.UI;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.cz2006.curator.Managers.ExhibitionManager;
import com.cz2006.curator.Objects.Exhibition;
import com.cz2006.curator.R;

import java.util.ArrayList;


/**
 * ExhibitionUI is a boundary class for displaying list of exhibitions of a museum.
 * It is managed by ExhibitionManager.
 */
public class ExhibitionUI extends AppCompatActivity {

    private final static String EXTRA_MESSAGE = "com.cz2006.curator.MESSAGE";

    private RecyclerView rv;

    private LinearLayoutManager llm;

    private ArrayList<Exhibition> exhibitionList;

    private ExhibitionAdapter adapter;

    private String museumName;

    private ExhibitionManager exhibitionManager;

    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //display a loading spinner while data loads
        //Log.e("Spinner", spinner.toString());
        setContentView(R.layout.activity_exhibition_ui);
        spinner = (ProgressBar)findViewById(R.id.progressBar2);
        spinner.setVisibility(View.VISIBLE);

        rv = (RecyclerView) findViewById(R.id.rv);

        llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(llm);


        exhibitionList = new ArrayList<>();
        adapter = new ExhibitionAdapter(exhibitionList);
        rv.setAdapter(adapter);

        museumName = getIntent().getStringExtra(EXTRA_MESSAGE);

        exhibitionManager = new ExhibitionManager(exhibitionList, adapter, this, spinner, museumName);

    }

    @Override
    protected void onResume() {
        super.onResume();
        exhibitionManager.refresh();
    }
}
