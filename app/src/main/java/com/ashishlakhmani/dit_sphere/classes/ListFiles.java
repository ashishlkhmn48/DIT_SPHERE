package com.ashishlakhmani.dit_sphere.classes;

import android.content.Context;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.ashishlakhmani.dit_sphere.R;
import com.ashishlakhmani.dit_sphere.adapters.DownloadsAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class ListFiles extends AsyncTask<Void, Void, Void> {

    private Context context;
    private View view;
    private RecyclerView recyclerView;


    private ProgressBar progressBar;
    private ConstraintLayout no;


    private List<File> file_list = new ArrayList<>();
    private File folder;

    public ListFiles(Context context, View view, RecyclerView recyclerView, File folder) {
        this.context = context;
        this.view = view;
        this.recyclerView = recyclerView;
        this.folder = folder;

        no = view.findViewById(R.id.no_download);
        progressBar = view.findViewById(R.id.progressBar);
    }

    @Override
    protected void onPreExecute() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        findFile(folder);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        progressBar.setVisibility(View.INVISIBLE);
        if (file_list.size() > 0) {
            no.setVisibility(View.INVISIBLE);
            DownloadsAdapter adapter = new DownloadsAdapter(context, file_list, no);
            recyclerView.setAdapter(adapter);
        } else {
            no.setVisibility(View.VISIBLE);
        }
    }


    private void findFile(File folder) {
        File[] list = folder.listFiles();

        if (list != null) {
            for (File file : list) {
                if (file.isDirectory()) {
                    findFile(file);
                } else if (file.getName().endsWith("jpg") || file.getName().endsWith("pdf")) {
                    file_list.add(file);
                }
            }
        } else {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}
