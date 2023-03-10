package com.fearlesssingh.ankganithindi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;

import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fearlesssingh.ankganithindi.Fragments.AboutFragment;
import com.fearlesssingh.ankganithindi.Fragments.HomeFragment;
import com.fearlesssingh.ankganithindi.InternetPermission.NetworkStateReceiver;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.OnFailureListener;
import com.google.android.play.core.tasks.Task;

import java.util.Calendar;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NetworkStateReceiver.NetworkStateReceiverListener {

    // double back press variable
    long back_pressed;

    //auto update check
    private static final int UPDATE_CODE = 22;
    AppUpdateManager updateManager;

    // rating on phone use google api
    ReviewInfo reviewInfo;

    boolean shows = false;
    // set variable for theme
    public SharedPreferences sharedPreferences, bottomPreferences;
    public SharedPreferences.Editor editor, bottomEditors;
    public int checkedItem;
    public String selected;
    public final String CHECKED_ITEM = "checked_item";

    //banner ads variable
    AdView mAdView;


    // Drawer navigation variable
    NavigationView navigationView;
    DrawerLayout drawerLayout;

    // variable for toolbar
    MaterialToolbar toolbar;
    AppBarLayout appBarLayout;

    // make variable for net connection
    public NetworkStateReceiver networkStateReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //auto update check
        inAppUpdate();
        bottomsheetDialog();


        // find xml layout variable
        toolbar = findViewById(R.id.topToolbar);
        appBarLayout = findViewById(R.id.appBarLayout);
        drawerLayout = findViewById(R.id.main_drawer_layout);
        navigationView = findViewById(R.id.nav_main);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).show();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);


        // init banner ads and find banner id view and init
        mAdView = findViewById(R.id.adView);


        sharedPreferences = this.getSharedPreferences("themes", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        switch (getCheckedItem()) {
            case 0:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
            case 1:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case 2:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
        }


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START, true);
            }
        });
        drawerMainMenuListener();

        // Network receiver class init
        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));

    }

    public void bottomsheetDialog() {
        Calendar calendar = Calendar.getInstance();
        int days = calendar.get(Calendar.DAY_OF_WEEK);
        if (days == Calendar.MONDAY) {
            bottomPreferences = MainActivity.this.getSharedPreferences("bottom", MODE_PRIVATE);
            shows = bottomPreferences.getBoolean("boolea", false);
            if (shows == false) {
                try {
                    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialog);
                    View v = LayoutInflater.from(this).inflate(R.layout.bottomsheet_layout, (LinearLayout) findViewById(R.id.bottmsheets_lay));
                    bottomSheetDialog.setContentView(v);
                    TextView joinNow = v.findViewById(R.id.join_now);
                    joinNow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            bottomCheckBoolean(true);
                            Uri jon = Uri.parse("https://t.me/maths_our_sol");
                            Intent now = new Intent(Intent.ACTION_VIEW, jon);
                            startActivity(now);
                            bottomSheetDialog.cancel();
                        }
                    });
                    bottomSheetDialog.show();

                    bottomCheckBoolean(false);

                } catch (Exception e) {
                    bottomCheckBoolean(false);
                    Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                bottomCheckBoolean(false);
            }
        } else {
            bottomCheckBoolean(false);
        }

    }

    public void bottomCheckBoolean(boolean b) {

        bottomPreferences = this.getSharedPreferences("bottom", MODE_PRIVATE);
        bottomEditors = bottomPreferences.edit();
        bottomEditors.putBoolean("boolea", b);
        bottomEditors.apply();
    }


    // create this method check auto app update available then show dialog
    private boolean inAppUpdate() {
        if (isNetworkConnected(this)) {
            updateManager = AppUpdateManagerFactory.create(this);
            Task<AppUpdateInfo> task = updateManager.getAppUpdateInfo();
            task.addOnSuccessListener(appUpdateInfo -> {
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                        && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                    try {
                        updateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.FLEXIBLE, MainActivity.this, UPDATE_CODE);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }
            });
            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                }
            });
            updateManager.registerListener(installStateUpdatedListener);
        }
        return false;
    }

    InstallStateUpdatedListener installStateUpdatedListener = installState -> {
        if (installState.installStatus() == InstallStatus.DOWNLOADED) {
            popUp();
        }
    };

    private void popUp() {
        Snackbar snackbar = Snackbar.make(
                findViewById(androidx.appcompat.R.id.content),
                "App Update Almost Done",
                Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("install", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateManager.completeUpdate();
            }
        });
        snackbar.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPDATE_CODE) {
            if (requestCode != RESULT_OK) {
                Toast.makeText(this, "Thank You Updating This App Enjoy???", Toast.LENGTH_SHORT).show();
            }
        }
    }


    // load rating dialog  show when app is restart
    @Override
    protected void onRestart() {
        loadRating();
        super.onRestart();
    }

    public void loadRating() {

        ReviewManager manager = ReviewManagerFactory.create(this);
        Task<ReviewInfo> request = manager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // We can get the ReviewInfo object
                reviewInfo = task.getResult();
            } else {
                // There was some problem, log or handle the error code.
                Toast.makeText(this, "review failed to start", Toast.LENGTH_SHORT).show();
            }
        });

        if (reviewInfo != null) {
            Task<Void> flow = manager.launchReviewFlow(this, reviewInfo);
            flow.addOnCompleteListener(task -> {
                // The flow has finished. The API does not indicate whether the user
                // reviewed or not, or even whether the review dialog was shown. Thus, no
                // matter the result, we continue our app flow.
                if (task.isSuccessful()) {
                    // custom dialog show on success
                } else {
                    Toast.makeText(this, "error while review ", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "rate", Toast.LENGTH_SHORT).show();
        }

    }


    // check, show dialog and check mobile day night mode
    private void showDialog() {
        String[] themes = this.getResources().getStringArray(R.array.theme);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Choose Theme");
        builder.setIcon(R.drawable.ic_baseline_color_lens_24);
        builder.setSingleChoiceItems(R.array.theme, getCheckedItem(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                selected = themes[i];
                checkedItem = i;

            }
        });

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (selected == null) {
                    selected = themes[getCheckedItem()];
                    checkedItem = getCheckedItem();

                }

                switch (checkedItem) {
                    case 0:
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

                        Intent intent = getIntent();
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        finish();
                        startActivity(intent);
                        break;

                    case 1:
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

                        Intent intent1 = getIntent();
                        intent1.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        finish();
                        startActivity(intent1);
                        break;
                    case 2:
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

                        Intent intent2 = getIntent();
                        intent2.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        finish();
                        startActivity(intent2);

                        break;
                }
                setCheckedItem(checkedItem);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public int getCheckedItem() {
        return sharedPreferences.getInt(CHECKED_ITEM, checkedItem);
    }

    private void setCheckedItem(int i) {
        editor.putInt(CHECKED_ITEM, i);
        editor.apply();
    }


    // create method double back press then show exit dialog
    @SuppressLint("ObsoleteSdkInt")
    @Override
    public void onBackPressed() {
        // check visibility nav bar
        checkFullScreen();
        doubleBackPress();
    }

    public void checkFullScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            View decorView = getWindow().getDecorView();
            if (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION == 2) {
                decorView.setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_IMMERSIVE
                );
                appBarLayout.setVisibility(View.VISIBLE);
                toolbar.setVisibility(View.VISIBLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    getWindow().setDecorFitsSystemWindows(true);
                }
            } else {
                Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void exitApp() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setIcon(R.drawable.ic_baseline_exit_to_app_24);
        builder.setTitle("Do You Want Exit This App");
        builder.setMessage("Please Rate This App (click rate)");
        builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setNeutralButton("Rate", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Uri rateUri = Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName());
                Intent rate = new Intent(Intent.ACTION_VIEW, rateUri);
                startActivity(rate);
            }
        });
        builder.create();
        builder.show();
    }

    private void doubleBackPress() {

        if (back_pressed + 2000 >= System.currentTimeMillis()) {
            exitApp();
        }
        back_pressed = System.currentTimeMillis();
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new HomeFragment()).commit();

    }


    // multiple fragment navigate this method
    private void fragReplace(Fragment fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_layout, fragment);
        transaction.addToBackStack("info").commit();
    }

    // check internet connection this method
    public boolean isNetworkConnected(@NonNull Context context) {
        try {

            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // show banner ad
    private void showBannerAd() {

        AdRequest adRequest = new AdRequest.Builder().build();
        if (adRequest != null) {
            mAdView.setVisibility(View.VISIBLE);
            mAdView.loadAd(adRequest);
        } else {
            Toast.makeText(this, "ad request null", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void networkAvailable() {
        MobileAds.initialize(MainActivity.this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
                showBannerAd();
            }
        });
    }

    @Override
    public void networkUnavailable() {
    }


    public void drawerMainMenuListener() {
        navigationView.setNavigationItemSelectedListener(new NavigationView
                .OnNavigationItemSelectedListener() {
            @SuppressLint({"NonConstantResourceId", "IntentReset"})
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                item.setChecked(true);
                drawerLayout.closeDrawer(GravityCompat.START, true);
                switch (itemId) {
                    case R.id.home:
                        fragReplace(new HomeFragment());
                        break;
                    case R.id.sd_yadav:
                        Uri sdUri = Uri.parse("https://play.google.com/store/apps/details?id=com.rockingdeveloper.sdyadvankganithindi");
                        Intent sdIntent = new Intent(Intent.ACTION_VIEW, sdUri);
                        startActivity(sdIntent);
                        break;
                    case R.id.privacy:
                        Uri privacyUri = Uri.parse("https://frearlessdevlopers.blogspot.com/2022/02/rs-agarwal-ankganit-privacy-policy.html");
                        Intent privacyIntent = new Intent(Intent.ACTION_VIEW, privacyUri);
                        startActivity(privacyIntent);
                        break;
                    case R.id.terms:
                        Uri termsUri = Uri.parse("https://frearlessdevlopers.blogspot.com/2022/02/rs-agrawal-ankganit-terms-and-conditions.html?m=1");
                        Intent termIntent = new Intent(Intent.ACTION_VIEW, termsUri);
                        startActivity(termIntent);
                        break;
                    case R.id.update:
                        Uri updateUri = Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName());
                        Intent updateIntent = new Intent(Intent.ACTION_VIEW, updateUri);
                        startActivity(updateIntent);
                        break;
                    case R.id.more_app:
                        Uri u = Uri.parse("https://play.google.com/store/apps/dev?id=9136110636517078139");
                        Intent moreIntents = new Intent(Intent.ACTION_VIEW, u);
                        startActivity(moreIntents);
                        break;
                    case R.id.rating:
                        Uri ratingUri = Uri.parse("https://play.google.com/store/apps/details?id=com.fearlesssingh.ankganithindi");
                        Intent ratingIntent = new Intent(Intent.ACTION_VIEW, ratingUri);
                        startActivity(ratingIntent);
                        break;
                    case R.id.arihant_reasoning:
                        Uri aUri = Uri.parse("https://play.google.com/store/apps/details?id=com.rockingdeveloper.arihantReasoning");
                        Intent aIntent = new Intent(Intent.ACTION_VIEW, aUri);
                        startActivity(aIntent);
                        break;
                    case R.id.telegram:
                        Uri jon = Uri.parse("https://t.me/rockingcommunity");
                        Intent now = new Intent(Intent.ACTION_VIEW, jon);
                        startActivity(now);
                        break;
                    case R.id.about:
                        fragReplace(new AboutFragment());
                        break;
                    case R.id.exit_app:
                        exitApp();
                        break;
                    case R.id.theme:
                        showDialog();
                        break;
                    case R.id.share:
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Rs Agarwal Ankganit");
                        shareIntent.putExtra(Intent.EXTRA_TEXT, "Rs Agarwal Ankganit???? Awesome Ui user easy use??? All competitive exam railway,ssc.banking etc most useful books  ???? Thank you so much ???? share this application ????Download Now" +
                                "\n https://play.google.com/store/apps/details?id=" + getPackageName());
                        shareIntent.setType("text/*");
                        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        try {
                            startActivity(Intent.createChooser(shareIntent, "share with"));
                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, "share failed", Toast.LENGTH_SHORT).show();
                        }
                        break;

                    default:
                        return true;
                }
                return true;
            }
        });
    }

}
