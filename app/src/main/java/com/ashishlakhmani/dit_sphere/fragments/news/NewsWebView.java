package com.ashishlakhmani.dit_sphere.fragments.news;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.ashishlakhmani.dit_sphere.R;
import com.parse.ParseObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewsWebView extends Fragment {

    private WebView webView;
    private ParseObject object;
    private ProgressBar progressBar;

    public NewsWebView() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_news_web_view, container, false);

        webView = view.findViewById(R.id.web_view);

        object = getArguments().getParcelable("parse_object");
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        WebSettings webSettings = webView.getSettings();
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webView.loadUrl(object.getString("url"));

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.INVISIBLE);
                super.onPageFinished(view, url);
            }
        });

        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                registerForContextMenu(webView);
                getActivity().openContextMenu(webView);
                return true;
            }
        });

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterForContextMenu(webView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Select The Action");
        menu.add(0, v.getId(), 0, "Share");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals("Share")) {
            shareTask();
        }
        return true;
    }

    private void shareTask() {
        String url = object.getString("url").trim();
        String heading = object.getString("heading").trim();
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, heading);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, heading + "\n\n" + url);
        getContext().startActivity(Intent.createChooser(sharingIntent, "Share News Using"));
    }
}
