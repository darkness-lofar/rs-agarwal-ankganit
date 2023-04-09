package com.fearlesssingh.ankganithindi.Fragments;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import com.fearlesssingh.ankganithindi.R;
import com.fearlesssingh.ankganithindi.databinding.FragmentBookViewBinding;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.barteksc.pdfviewer.util.FitPolicy;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.Objects;


public class BookViewFragment extends Fragment {

    CardView pdf_cardView;

    // variable for binding xml layout
    private FragmentBookViewBinding binding;

    // variable for receive position, ch number
    public String getPosition, chNumber, getPageNum, pageNum;
    public Boolean getBoolean,orientation = false;
    ImageView rotationMode, telegram, theme_change;

    // variable for view pdf
    private PDFView pdfView;

    // variable for night or day mode
    int currentNightMode;

    // variable for toolbar, appbar
    public MaterialToolbar toolbar;
    public AppBarLayout appBarLayout;
    boolean show = false , chackTheme = false;
    long secondRemaining;
    private CountDownTimer countDownTimer;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public BookViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BookViewFragment.
     */
    // TODO: Rename and change types and number of parameters
    @NonNull
    public static BookViewFragment newInstance(String param1, String param2) {
        BookViewFragment fragment = new BookViewFragment();
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

    @SuppressLint("ObsoleteSdkInt")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBookViewBinding.inflate(inflater, container, false);
        View v = binding.getRoot();
        //
        // Inflate the layout for this fragment

        getBundle();
        initVar();
        getLightModePdf();
       // ifCheckPosition();
        //init back method
        handleBackPressAndAutoFullscreen(v);
        // Ui visibility
        systemUiVisibility();

        // find xml variable
        toolbar = requireActivity().findViewById(R.id.topToolbar);
        appBarLayout = requireActivity().findViewById(R.id.appBarLayout);
        appBarLayout.setVisibility(View.VISIBLE);
        toolbar.setVisibility(View.VISIBLE);

        // click pdf then fullscreen mode
        pdfView.setOnClickListener(new View.OnClickListener() {
            boolean show = false;

            @Override
            public void onClick(View view) {
                oneClickFullscreen();
            }
        });

        binding.imgTelegram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri jon = Uri.parse("https://t.me/maths_our_sol");
                Intent now = new Intent(Intent.ACTION_VIEW, jon);
                startActivity(now);
            }
        });

        binding.imgRotation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rotation();
            }
        });

        return v;
    }

    private void systemUiVisibility() {
        // check visibility nav bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            View decorView = requireActivity().getWindow().getDecorView();
            if (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION == 2 || View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION == 512) {
                decorView.setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                );
                decorView.setFitsSystemWindows(true);
                decorView.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(requireContext(), "unexpected error", Toast.LENGTH_SHORT).show();
            }
        }


    }

    @SuppressLint("SourceLockedOrientationActivity")
    private void rotation() {

        if (orientation) {
            requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            binding.imgRotation.setImageResource(R.drawable.baseline_screen_lock_portrait_24);
            Toast.makeText(requireContext(), "portrait Mode", Toast.LENGTH_SHORT).show();

            orientation = false;
        } else {
            requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            binding.imgRotation.setImageResource(R.drawable.baseline_screen_lock_landscape_24);
            Toast.makeText(requireContext(), "landscape Mode", Toast.LENGTH_SHORT).show();

            orientation = true;
        }
    }

    private void oneClickFullscreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (show) {
                hideSystemUI();
                show = false;
            } else {
                showSystemUI();
                show = true;
            }
        }
    }

    private void handleBackPressAndAutoFullscreen(@NonNull View v) {
        // automate pdf full screen view
        countDownTimer = new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long l) {
                secondRemaining = ((l / 1000) + 1);
            }

            @Override
            public void onFinish() {
                secondRemaining = 0;
                hideSystemUI();
            }

        };
        countDownTimer.start();

        // handle back press
        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    if (i == KeyEvent.KEYCODE_BACK) {
                        countDownTimer.cancel();
                        saveDataPref();
                        showSystemUI();

                        HomeFragment homeFragment = new HomeFragment();
                        requireActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.frame_layout, homeFragment)
                                .addToBackStack("back")
                                .commit();
                    }
                }
                return true;
            }
        });
    }

    private void saveDataPref() {
        try {
            if (getPosition != null && pageNum != null && chNumber != null) {
                SharedPreferences pref = requireActivity().getSharedPreferences("position", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("chPosition", getPosition);
                editor.putString("pagePosition", pageNum);
                editor.putString("chNumber", chNumber);
                editor.apply();
            }

        } catch (Exception e) {
            Toast.makeText(requireContext(), "not save: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onDestroy() {
        saveDataPref();
        showSystemUI();
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        saveDataPref();
        showSystemUI();
        super.onDetach();
    }

    @Override
    public void onPause() {
        saveDataPref();
        super.onPause();
    }

    @Override
    public void onResume() {
        saveDataPref();
        super.onResume();
    }

    // restart app
    private static void triggerRebirth(@NonNull Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(context.getPackageName());
        ComponentName componentName = intent.getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(componentName);
        context.startActivity(mainIntent);
        Runtime.getRuntime().exit(0);
    }

    // hide toolbar, status bar, etc
    private void hideSystemUI() {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AppCompatActivity appCompatActivity = (AppCompatActivity) requireActivity();
            Objects.requireNonNull(appCompatActivity.getSupportActionBar()).hide();

            View decorView = requireActivity().getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    //View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    // | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    // View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    // | View.SYSTEM_UI_FLAG_IMMERSIVE
                    View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            |View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            |View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            );
            decorView.setFitsSystemWindows(true);

            appBarLayout.animate().translationY(-appBarLayout.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
            toolbar.animate().translationY(-toolbar.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
//            rotationMode.animate().translationY(-rotationMode.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
//            telegram.animate().translationY(-telegram.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
            pdf_cardView.animate().translationY(-pdf_cardView.getBottom()).setInterpolator(new AccelerateInterpolator()).start();

//            rotationMode.setVisibility(View.GONE);
//            telegram.setVisibility(View.GONE);
            pdf_cardView.setVisibility(View.GONE);
            

            //================= Change Theme ===================//
            theme_change.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (chackTheme  == true){
                        getLightModePdf();
                        chackTheme = false;
                    }else {
                        getDarkModePdf();
                        chackTheme = true;
                    }
                }
            });

        }
        try {

        } catch (Exception e) {
            Toast.makeText(requireContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // show toolbar, status bar, etc
    public void showSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AppCompatActivity appCompatActivity = (AppCompatActivity) requireActivity();
            Objects.requireNonNull(appCompatActivity.getSupportActionBar()).show();

            View decorView = requireActivity().getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            decorView.setFitsSystemWindows(true);
            decorView.setVisibility(View.VISIBLE);
            decorView.setSystemUiVisibility(View.VISIBLE);
//
            appBarLayout.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();
            toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();
//            rotationMode.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();
//            telegram.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();
            pdf_cardView.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();

//            rotationMode.setVisibility(View.VISIBLE);
//            telegram.setVisibility(View.VISIBLE);
            pdf_cardView.setVisibility(View.VISIBLE);
        }

    }

    private void getBundle() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            getPosition = bundle.getString("position");
            chNumber = bundle.getString("chNumber");
            getPageNum = bundle.getString("pageNum");
            getBoolean = bundle.getBoolean("admins");

        } else {
            Toast.makeText(getContext(), "bundle null", Toast.LENGTH_SHORT).show();
        }
    }

    private void initVar() {
        pdfView = binding.pdfView;
        rotationMode = binding.imgRotation;
        telegram = binding.imgTelegram;
        pdf_cardView = binding.pdfCardView;
        theme_change = binding.themeChange;
    }

//    private void ifCheckPosition() {
//        if (AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM == AppCompatDelegate.getDefaultNightMode()) {
//            checkMode();
//        } else if (AppCompatDelegate.MODE_NIGHT_YES == AppCompatDelegate.getDefaultNightMode()) {
//            getDarkModePdf();
//        } else if (AppCompatDelegate.MODE_NIGHT_NO == AppCompatDelegate.getDefaultNightMode()) {
//            getLightModePdf();
//        }
//    }

    private void checkMode() {
        currentNightMode = requireActivity().getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK;
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                getLightModePdf();
                break;
            case Configuration.UI_MODE_NIGHT_YES:
                getDarkModePdf();
                break;
        }
    }

    private void lightModePdf(String name) {
        pdfView.fromAsset(name + ".pdf")
                .enableDoubletap(true)
                .enableSwipe(true)
                .defaultPage(Integer.parseInt(getPageNum))
                .scrollHandle(new DefaultScrollHandle(getContext()))
                .onError(new OnErrorListener() {
                    @Override
                    public void onError(Throwable t) {
                        Toast.makeText(getContext(), "internal error \n pdf can't load", Toast.LENGTH_SHORT).show();
                    }
                })
                .onPageChange(new OnPageChangeListener() {
                    @Override
                    public void onPageChanged(int page, int pageCount) {
                        pageNum = String.valueOf(page);
                    }
                })
                .pageFitPolicy(FitPolicy.WIDTH)
                .enableAnnotationRendering(true)
                .load();
    }

    private void nightModePdf(String name) {
        pdfView.fromAsset(name + ".pdf")
                .nightMode(true)
                .enableDoubletap(true)
                .enableSwipe(true)
                .defaultPage(Integer.parseInt(getPageNum))
                .scrollHandle(new DefaultScrollHandle(getContext()))
                .onError(new OnErrorListener() {
                    @Override
                    public void onError(Throwable t) {
                        Toast.makeText(getContext(), "internal error \n pdf can't load", Toast.LENGTH_SHORT).show();
                    }
                })
                .onPageChange(new OnPageChangeListener() {
                    @Override
                    public void onPageChanged(int page, int pageCount) {
                        pageNum = String.valueOf(page);
                    }
                })
                .pageFitPolicy(FitPolicy.WIDTH)
                .enableAnnotationRendering(true)
                .load();
    }

    private void getLightModePdf() {
        switch (getPosition) {
            case "0":
                lightModePdf(chNumber);
                Toast.makeText(getContext(), "01", Toast.LENGTH_SHORT).show();
                break;
            case "1":
                lightModePdf(chNumber);
                Toast.makeText(getContext(), "02", Toast.LENGTH_SHORT).show();
                break;
            case "2":
                lightModePdf(chNumber);
                Toast.makeText(getContext(), "03", Toast.LENGTH_SHORT).show();
                break;
            case "3":
                lightModePdf(chNumber);
                Toast.makeText(getContext(), "04", Toast.LENGTH_SHORT).show();
                break;
            case "4":
                lightModePdf(chNumber);
                Toast.makeText(getContext(), "05", Toast.LENGTH_SHORT).show();
                break;
            case "5":
                lightModePdf(chNumber);
                Toast.makeText(getContext(), "06", Toast.LENGTH_SHORT).show();
                break;
            case "6":
                lightModePdf(chNumber);
                Toast.makeText(getContext(), "07", Toast.LENGTH_SHORT).show();
                break;
            case "7":
                lightModePdf(chNumber);
                Toast.makeText(getContext(), "08", Toast.LENGTH_SHORT).show();
                break;
            case "8":
                lightModePdf(chNumber);
                Toast.makeText(getContext(), "09", Toast.LENGTH_SHORT).show();
                break;
            case "9":
                lightModePdf(chNumber);
                Toast.makeText(getContext(), "10", Toast.LENGTH_SHORT).show();
                break;
            case "10":
                lightModePdf(chNumber);
                Toast.makeText(getContext(), "11", Toast.LENGTH_SHORT).show();
                break;
            case "11":
                lightModePdf(chNumber);
                Toast.makeText(getContext(), "12", Toast.LENGTH_SHORT).show();
                break;
            case "12":
                lightModePdf(chNumber);
                Toast.makeText(getContext(), "13", Toast.LENGTH_SHORT).show();
                break;
            case "13":
                lightModePdf(chNumber);
                Toast.makeText(getContext(), "14", Toast.LENGTH_SHORT).show();
                break;
            case "14":
                lightModePdf(chNumber);
                Toast.makeText(getContext(), "15", Toast.LENGTH_SHORT).show();
                break;
            case "15":
                lightModePdf(chNumber);
                Toast.makeText(getContext(), "16", Toast.LENGTH_SHORT).show();
                break;
            case "16":
                lightModePdf(chNumber);
                Toast.makeText(getContext(), "17", Toast.LENGTH_SHORT).show();
                break;
            case "17":
                lightModePdf(chNumber);
                Toast.makeText(getContext(), "18", Toast.LENGTH_SHORT).show();
                break;
            case "18":
                lightModePdf(chNumber);
                Toast.makeText(getContext(), "19", Toast.LENGTH_SHORT).show();
                break;
            case "19":
                lightModePdf(chNumber);
                Toast.makeText(getContext(), "20", Toast.LENGTH_SHORT).show();
                break;
            case "20":
                lightModePdf(chNumber);
                Toast.makeText(getContext(), "21", Toast.LENGTH_SHORT).show();
                break;
            case "21":
                lightModePdf(chNumber);
                Toast.makeText(getContext(), "22", Toast.LENGTH_SHORT).show();
                break;
            case "22":
                lightModePdf(chNumber);
                Toast.makeText(getContext(), "23", Toast.LENGTH_SHORT).show();
                break;
            case "23":
                lightModePdf(chNumber);
                Toast.makeText(getContext(), "24", Toast.LENGTH_SHORT).show();
                break;
            case "24":
                lightModePdf(chNumber);
                Toast.makeText(getContext(), "25", Toast.LENGTH_SHORT).show();
                break;
            case "25":
                lightModePdf(chNumber);
                Toast.makeText(getContext(), "26", Toast.LENGTH_SHORT).show();
                break;
            case "26":
                lightModePdf(chNumber);
                Toast.makeText(getContext(), "27", Toast.LENGTH_SHORT).show();
                break;
            case "27":
                lightModePdf(chNumber);
                Toast.makeText(getContext(), "28", Toast.LENGTH_SHORT).show();
                break;
            case "28":
                lightModePdf(chNumber);
                Toast.makeText(getContext(), "29", Toast.LENGTH_SHORT).show();
                break;
            case "29":
                lightModePdf(chNumber);
                Toast.makeText(getContext(), "30", Toast.LENGTH_SHORT).show();
                break;
            case "30":
                lightModePdf(chNumber);
                Toast.makeText(getContext(), "31", Toast.LENGTH_SHORT).show();
                break;
            case "31":
                lightModePdf(chNumber);
                Toast.makeText(getContext(), "32", Toast.LENGTH_SHORT).show();
                break;
            case "32":
                lightModePdf(chNumber);
                Toast.makeText(getContext(), "33", Toast.LENGTH_SHORT).show();
                break;
            case "33":
                lightModePdf(chNumber);
                Toast.makeText(getContext(), "34", Toast.LENGTH_SHORT).show();
                break;
            case "34":
                lightModePdf(chNumber);
                Toast.makeText(getContext(), "35", Toast.LENGTH_SHORT).show();
                break;
            case "35":
                lightModePdf(chNumber);
                Toast.makeText(getContext(), "36", Toast.LENGTH_SHORT).show();
                break;
            case "36":
                lightModePdf(chNumber);
                Toast.makeText(getContext(), "37", Toast.LENGTH_SHORT).show();
                break;
            case "37":
                lightModePdf(chNumber);
                Toast.makeText(getContext(), "38", Toast.LENGTH_SHORT).show();
                break;
            case "38":
                lightModePdf(chNumber);
                Toast.makeText(getContext(), "39", Toast.LENGTH_SHORT).show();
                break;
            case "39":
                lightModePdf(chNumber);
                Toast.makeText(getContext(), "40", Toast.LENGTH_SHORT).show();
                break;
            case "40":
                lightModePdf(chNumber);
                Toast.makeText(getContext(), "41", Toast.LENGTH_SHORT).show();
                break;
            case "41":
                lightModePdf(chNumber);
                Toast.makeText(getContext(), "42", Toast.LENGTH_SHORT).show();
                break;
            case "42":
                lightModePdf(chNumber);
                Toast.makeText(getContext(), "43", Toast.LENGTH_SHORT).show();
                break;
            case "43":
                lightModePdf(chNumber);
                Toast.makeText(getContext(), "44", Toast.LENGTH_SHORT).show();
                break;


        }
    }

    private void getDarkModePdf() {

        switch (getPosition) {
            case "0":
                nightModePdf(chNumber);
                Toast.makeText(getContext(), "01", Toast.LENGTH_SHORT).show();
                break;
            case "1":
                nightModePdf(chNumber);
                Toast.makeText(getContext(), "02", Toast.LENGTH_SHORT).show();
                break;
            case "2":
                nightModePdf(chNumber);
                Toast.makeText(getContext(), "03", Toast.LENGTH_SHORT).show();
                break;
            case "3":
                nightModePdf(chNumber);
                Toast.makeText(getContext(), "04", Toast.LENGTH_SHORT).show();
                break;
            case "4":
                nightModePdf(chNumber);
                Toast.makeText(getContext(), "05", Toast.LENGTH_SHORT).show();
                break;
            case "5":
                nightModePdf(chNumber);
                Toast.makeText(getContext(), "06", Toast.LENGTH_SHORT).show();
                break;
            case "6":
                nightModePdf(chNumber);
                Toast.makeText(getContext(), "07", Toast.LENGTH_SHORT).show();
                break;
            case "7":
                nightModePdf(chNumber);
                Toast.makeText(getContext(), "08", Toast.LENGTH_SHORT).show();
                break;
            case "8":
                nightModePdf(chNumber);
                Toast.makeText(getContext(), "09", Toast.LENGTH_SHORT).show();
                break;
            case "9":
                nightModePdf(chNumber);
                Toast.makeText(getContext(), "10", Toast.LENGTH_SHORT).show();
                break;
            case "10":
                nightModePdf(chNumber);
                Toast.makeText(getContext(), "11", Toast.LENGTH_SHORT).show();
                break;
            case "11":
                nightModePdf(chNumber);
                Toast.makeText(getContext(), "12", Toast.LENGTH_SHORT).show();
                break;
            case "12":
                nightModePdf(chNumber);
                Toast.makeText(getContext(), "13", Toast.LENGTH_SHORT).show();
                break;
            case "13":
                nightModePdf(chNumber);
                Toast.makeText(getContext(), "14", Toast.LENGTH_SHORT).show();
                break;
            case "14":
                nightModePdf(chNumber);
                Toast.makeText(getContext(), "15", Toast.LENGTH_SHORT).show();
                break;
            case "15":
                nightModePdf(chNumber);
                Toast.makeText(getContext(), "16", Toast.LENGTH_SHORT).show();
                break;
            case "16":
                nightModePdf(chNumber);
                Toast.makeText(getContext(), "17", Toast.LENGTH_SHORT).show();
                break;
            case "17":
                nightModePdf(chNumber);
                Toast.makeText(getContext(), "18", Toast.LENGTH_SHORT).show();
                break;
            case "18":
                nightModePdf(chNumber);
                Toast.makeText(getContext(), "19", Toast.LENGTH_SHORT).show();
                break;
            case "19":
                nightModePdf(chNumber);
                Toast.makeText(getContext(), "20", Toast.LENGTH_SHORT).show();
                break;
            case "20":
                nightModePdf(chNumber);
                Toast.makeText(getContext(), "21", Toast.LENGTH_SHORT).show();
                break;
            case "21":
                nightModePdf(chNumber);
                Toast.makeText(getContext(), "22", Toast.LENGTH_SHORT).show();
                break;
            case "22":
                nightModePdf(chNumber);
                Toast.makeText(getContext(), "23", Toast.LENGTH_SHORT).show();
                break;
            case "23":
                nightModePdf(chNumber);
                Toast.makeText(getContext(), "24", Toast.LENGTH_SHORT).show();
                break;
            case "24":
                nightModePdf(chNumber);
                Toast.makeText(getContext(), "25", Toast.LENGTH_SHORT).show();
                break;
            case "25":
                nightModePdf(chNumber);
                Toast.makeText(getContext(), "26", Toast.LENGTH_SHORT).show();
                break;
            case "26":
                nightModePdf(chNumber);
                Toast.makeText(getContext(), "27", Toast.LENGTH_SHORT).show();
                break;
            case "27":
                nightModePdf(chNumber);
                Toast.makeText(getContext(), "28", Toast.LENGTH_SHORT).show();
                break;
            case "28":
                nightModePdf(chNumber);
                Toast.makeText(getContext(), "29", Toast.LENGTH_SHORT).show();
                break;
            case "29":
                nightModePdf(chNumber);
                Toast.makeText(getContext(), "30", Toast.LENGTH_SHORT).show();
                break;
            case "30":
                nightModePdf(chNumber);
                Toast.makeText(getContext(), "31", Toast.LENGTH_SHORT).show();
                break;
            case "31":
                nightModePdf(chNumber);
                Toast.makeText(getContext(), "32", Toast.LENGTH_SHORT).show();
                break;
            case "32":
                nightModePdf(chNumber);
                Toast.makeText(getContext(), "33", Toast.LENGTH_SHORT).show();
                break;
            case "33":
                nightModePdf(chNumber);
                Toast.makeText(getContext(), "34", Toast.LENGTH_SHORT).show();
                break;
            case "34":
                nightModePdf(chNumber);
                Toast.makeText(getContext(), "35", Toast.LENGTH_SHORT).show();
                break;
            case "35":
                nightModePdf(chNumber);
                Toast.makeText(getContext(), "36", Toast.LENGTH_SHORT).show();
                break;
            case "36":
                nightModePdf(chNumber);
                Toast.makeText(getContext(), "37", Toast.LENGTH_SHORT).show();
                break;
            case "37":
                nightModePdf(chNumber);
                Toast.makeText(getContext(), "38", Toast.LENGTH_SHORT).show();
                break;
            case "38":
                nightModePdf(chNumber);
                Toast.makeText(getContext(), "39", Toast.LENGTH_SHORT).show();
                break;
            case "39":
                nightModePdf(chNumber);
                Toast.makeText(getContext(), "40", Toast.LENGTH_SHORT).show();
                break;
            case "40":
                nightModePdf(chNumber);
                Toast.makeText(getContext(), "41", Toast.LENGTH_SHORT).show();
                break;
            case "41":
                nightModePdf(chNumber);
                Toast.makeText(getContext(), "42", Toast.LENGTH_SHORT).show();
                break;
            case "42":
                nightModePdf(chNumber);
                Toast.makeText(getContext(), "43", Toast.LENGTH_SHORT).show();
                break;
            case "43":
                nightModePdf(chNumber);
                Toast.makeText(getContext(), "44", Toast.LENGTH_SHORT).show();
                break;
        }
    }


}