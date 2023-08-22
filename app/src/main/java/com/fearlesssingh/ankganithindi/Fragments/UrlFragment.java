package com.fearlesssingh.ankganithindi.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fearlesssingh.ankganithindi.R;
import com.fearlesssingh.ankganithindi.databinding.FragmentUrlBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UrlFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UrlFragment extends Fragment {

    FragmentUrlBinding binding;
    WebView webView;
    TextView textView;
    ProgressBar progressBar;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UrlFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UrlFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UrlFragment newInstance(String param1, String param2) {
        UrlFragment fragment = new UrlFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentUrlBinding.inflate(inflater, container, false);
        View v = binding.getRoot();
        findXmlVar();

        textView.setVisibility(View.VISIBLE);
        textView.setText("privacy");


        webView.setVisibility(View.VISIBLE);
        webView.loadUrl("https://play.google.com/store/apps/dev?id=9136110636517078139");

        return v;
    }

    public void findXmlVar() {
        webView = binding.webView;
        textView = binding.tittleTv;
        progressBar = binding.progressBar;
    }
}