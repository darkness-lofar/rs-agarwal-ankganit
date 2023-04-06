package com.fearlesssingh.ankganithindi.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.fearlesssingh.ankganithindi.MainActivity;
import com.fearlesssingh.ankganithindi.R;
import com.fearlesssingh.ankganithindi.SplashActivity;
import com.fearlesssingh.ankganithindi.databinding.FragmentLoadingBinding;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;


public class LoadingFragment extends Fragment {

    // init binding
    FragmentLoadingBinding binding;
    //variable for get bundle
    String getPositions, chNumbers, pageNum;
    Boolean getBooleans;
    Handler handler = new Handler();
    // variable for interstitial ads
    private InterstitialAd mInterstitialAd;
    ConnectivityManager connectivityManager;
    NetworkInfo networkInfo;
    AppBarLayout appBarLayout;
    MaterialToolbar toolbar;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LoadingFragment() {
        // Required empty public constructor
    }

    @NonNull
    public static LoadingFragment newInstance(String param1, String param2) {
        LoadingFragment fragment = new LoadingFragment();
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
        binding = FragmentLoadingBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // call back button method init
        stopWorkingBackButton(view);

        // call getBundle method
        getBundle();
        //isNetworkConnected(requireContext());

        // find xml component
        toolbar = requireActivity().findViewById(R.id.topToolbar);
        appBarLayout = requireActivity().findViewById(R.id.appBarLayout);
        // invisible toolbar and layout
        toolbar.setVisibility(View.GONE);
        appBarLayout.setVisibility(View.GONE);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        MobileAds.initialize(requireContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
                if (initializationStatus != null) {
                    isNetworkConnected(requireContext());
                } else {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            sendPdfFragBundle();
                        }
                    }, 3000);
                }
            }
        });
    }

    // load interstitial ad
    public void loadInterstitialAd() {
        if (System.currentTimeMillis() >= 2500) {
            AdRequest adRequest = new AdRequest.Builder().build();
            InterstitialAd.load(requireContext(), requireContext().getResources().getString(R.string.INTERSTITIAL_Test_AD_UNIT_ID), adRequest,
                    new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                            // The mInterstitialAd reference will be null until
                            // an ad is loaded.

                            mInterstitialAd = interstitialAd;

                            if (mInterstitialAd != null) {
                                mInterstitialAd.show((Activity) requireContext());
                                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        super.onAdDismissedFullScreenContent();
                                        mInterstitialAd = null;
                                        sendPdfFragBundle();
                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                        // Called when ad fails to show.
                                        Log.e("TAG", "Ad failed to show fullscreen content.");
                                        mInterstitialAd = null;
                                        sendPdfFragBundle();
                                    }

                                });
                            } else {
                                sendPdfFragBundle();
                            }

                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle the error
                            Log.d("TAG", "Ad failed to show fullscreen content.");
                            mInterstitialAd = null;
                            sendPdfFragBundle();
                        }
                    });
        } else {
            sendPdfFragBundle();
        }
    }

    // check internet
    public void isNetworkConnected(Context context) {
        try {
            connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null) {
                if (MainActivity.connect) {
                    handler.postAtTime(new Runnable() {
                        @Override
                        public void run() {
                            loadInterstitialAd();
                        }
                    }, 8000);

                } else {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            sendPdfFragBundle();
                        }
                    }, 3000);
                }
            } else {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sendPdfFragBundle();
                    }
                }, 3000);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // handle back button
    private void stopWorkingBackButton(@NonNull View view) {
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    return keyCode == KeyEvent.KEYCODE_BACK;
                }
                return false;
            }
        });
    }

    // send position, ch number to pdf frag
    private void sendPdfFragBundle() {
        if (getPositions != null && chNumbers != null && pageNum != null) {
            Bundle bundles = new Bundle();
            bundles.putString("position", getPositions);
            bundles.putString("chNumber", chNumbers);
            bundles.putString("pageNum", pageNum);
            BookViewFragment bookViewFragment = new BookViewFragment();
            bookViewFragment.setArguments(bundles);


            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, bookViewFragment)
                    .addToBackStack("info")
                    .commit();

        } else {
            Toast.makeText(requireContext(), "send bundle null", Toast.LENGTH_SHORT).show();
        }
    }

    //get position,ch number in adapter
    private void getBundle() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            getPositions = bundle.getString("positions");
            chNumbers = bundle.getString("chNumbers");
            pageNum = bundle.getString("pageNum");
            getBooleans = bundle.getBoolean("admin");

        } else {
            Toast.makeText(getContext(), "bundle null in loading", Toast.LENGTH_SHORT).show();
        }
    }

}