package com.fearlesssingh.ankganithindi;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.fearlesssingh.ankganithindi.Fragments.AboutFragment;
import com.fearlesssingh.ankganithindi.Fragments.HomeFragment;
import com.fearlesssingh.ankganithindi.InternetPermission.NetworkStateReceiver;
import com.google.android.gms.ads.AdRequest;
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
import com.google.android.play.core.tasks.OnCompleteListener;
import com.google.android.play.core.tasks.OnFailureListener;
import com.google.android.play.core.tasks.Task;
import com.google.android.ump.ConsentForm;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.FormError;
import com.google.android.ump.UserMessagingPlatform;

import java.util.Calendar;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NetworkStateReceiver.NetworkStateReceiverListener {

    ////////// others /////////////////
    boolean shows = false;
    // Drawer navigation variable
    NavigationView navigationView;
    DrawerLayout drawerLayout;

    // variable for toolbar
    MaterialToolbar toolbar;
    AppBarLayout appBarLayout;

    // make variable for net connection
    public NetworkStateReceiver networkStateReceiver;

    /////////// end others ///////////////

    // double back press variable
    long back_pressed;

    //auto update ads check /////////////
    //banner ads variable
    AdView mAdView;

    private static final int UPDATE_CODE = 22;

    AppUpdateManager updateManager;
    // rating on phone use google api
    ReviewInfo reviewInfo;
    /////////// update end ///////////////


    // set variable for theme
    static SharedPreferences bottomPreferences;

    static SharedPreferences.Editor bottomEditors;


    ///////// end theme //////////////////

    /// gdpr /////
    private ConsentInformation consentInformation;
    public ConsentForm consentForm;
    ///end  gdpr /////


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // find xml layout variable
        toolbar = findViewById(R.id.topToolbar);
        appBarLayout = findViewById(R.id.appBarLayout);
        drawerLayout = findViewById(R.id.main_drawer_layout);
        navigationView = findViewById(R.id.nav_main);
        // init banner ads and find banner id view and init
        mAdView = findViewById(R.id.adView);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).show();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        inAppUpdate();
        bottomsheetDialog();
        drawerMainMenuListener();


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START, true);
            }
        });


        // Network receiver class init
        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        this.registerReceiver(networkStateReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        // gdpr policy
        // Set tag for under age of consent. false means users are not under
        // age.

        ConsentRequestParameters params = new ConsentRequestParameters
                .Builder()
                .setTagForUnderAgeOfConsent(false)
                .build();

        consentInformation = UserMessagingPlatform.getConsentInformation(this);
        consentInformation.requestConsentInfoUpdate(
                this,
                params,
                new ConsentInformation.OnConsentInfoUpdateSuccessListener() {
                    @Override
                    public void onConsentInfoUpdateSuccess() {
                        // The consent information state was updated.
                        // You are now ready to check if a form is available.
                        if (consentInformation.isConsentFormAvailable()) {
                            loadForm();
                        }
                    }
                },
                new ConsentInformation.OnConsentInfoUpdateFailureListener() {
                    @Override
                    public void onConsentInfoUpdateFailure(@NonNull FormError formError) {
                        // Handle the error.
                    }
                });

    }

    /// gdpr policy
    public void loadForm() {
        // Loads a consent form. Must be called on the main thread.
        UserMessagingPlatform.loadConsentForm(
                this,
                new UserMessagingPlatform.OnConsentFormLoadSuccessListener() {
                    @Override
                    public void onConsentFormLoadSuccess(@NonNull ConsentForm consentForm) {
                        MainActivity.this.consentForm = consentForm;
                    }
                },
                new UserMessagingPlatform.OnConsentFormLoadFailureListener() {
                    @Override
                    public void onConsentFormLoadFailure(@NonNull FormError formError) {
                        // Handle the error.
                        Log.e("form error", formError.getMessage());
                    }
                }
        );
    }

    // check, show dialog and check mobile day night mode //////////////////////


    ///// bottom dialog show join telegram group/////////////////////
    public void bottomsheetDialog() {
        Calendar calendar = Calendar.getInstance();
        int days = calendar.get(Calendar.DAY_OF_WEEK);
        if (days == Calendar.WEDNESDAY) {
            bottomPreferences = MainActivity.this.getSharedPreferences("bottom", MODE_PRIVATE);
            shows = bottomPreferences.getBoolean("boolea", false);
            if (!shows) {
                try {
                    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialog);
                    View v = LayoutInflater.from(this).inflate(R.layout.bottomsheet_layout, findViewById(R.id.bottmsheets_lay));
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
    /////////////// end telegram group //////////////////////////////


//////////////// end theme //////////////////////////////////


    // when app update Available then run this method///////////////////////
    public void inAppUpdate() {
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
                Toast.makeText(MainActivity.this, "update error \n" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        updateManager.registerListener(installStateUpdatedListener);

    }

    InstallStateUpdatedListener installStateUpdatedListener = installState -> {
        if (installState.installStatus() == InstallStatus.DOWNLOADED) {
            popUp();
        } else if (installState.totalBytesToDownload() == InstallStatus.DOWNLOADED) {

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
                updateManager.completeUpdate().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isComplete()) {
                            Toast.makeText(MainActivity.this, "enjoy latest version", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        snackbar.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPDATE_CODE) {
            if (requestCode != RESULT_OK) {
                Toast.makeText(this, "Thank You Updating This App Enjoy❤", Toast.LENGTH_SHORT).show();
            }
        }
    }
//////////////////////////////////////////////////  end update ////////////////////////////////////


    // exit method double back press then show exit dialog ////////////////////////////////
    @Override
    public void onBackPressed() {
        // check visibility nav bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            View decorView = getWindow().getDecorView();
            appBarLayout.setVisibility(View.VISIBLE);
            toolbar.setVisibility(View.VISIBLE);

            if (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION == 2) {
                decorView.setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_IMMERSIVE
                                | View.VISIBLE
                );
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    getWindow().setDecorFitsSystemWindows(true);
                }

            } else {
                Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
            }
        }
        doubleBackPress();
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

    //////////////////////// end bak button work ///////////////////////////


    //////////// show banner ad and check server connection////////////////////////////

    private void showBannerAd() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
                if (initializationStatus != null) {
                    AdRequest adRequest = new AdRequest.Builder().build();
                    if (adRequest != null) {
                        mAdView.setVisibility(View.VISIBLE);
                        mAdView.loadAd(adRequest);
                    } else {
                        mAdView.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this, "ad request null", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


    }

    // load rating dialog  show when app is restart
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
                    Toast.makeText(this, "💕thank you❤ \n rating and review this app ", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "error while review ", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        loadRating();
    }

    @Override
    public void networkAvailable() {
        showBannerAd();
    }

    @Override
    public void networkUnavailable() {
    }
    //////////////// end show banner ad and check firebase connection ////////////////////////////

    private void dialog(@NonNull String url) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setIcon(R.drawable.baseline_web_24);
        builder.setTitle("open web url");
        builder.setMessage("may be this url open another app no control this app. open url your own risk\n" + url);
        builder.setPositiveButton("Open", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create();
        builder.show();

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
                        String sdstr = "https://play.google.com/store/apps/details?id=com.rockingdeveloper.sdyadvankganithindi";
                        dialog(sdstr);
                        break;
                    case R.id.privacy:
                        String privacyStr = "https://sites.google.com/view/rsagrawalankganit/home";
                        dialog(privacyStr);
                        break;
                    case R.id.terms:
                        String termStr ="https://sites.google.com/view/rsagrawalankganit/terms-condition";
                        dialog(termStr);
                        break;
                    case R.id.update:
                        String updateStr = "https://play.google.com/store/apps/details?id=" + getPackageName();
                        dialog(updateStr);
                        break;
                    case R.id.more_app:
                        String moreStr = "https://play.google.com/store/apps/dev?id=9136110636517078139";
                        dialog(moreStr);
                        break;
                    case R.id.rating:
                        String ratingStr = "https://play.google.com/store/apps/details?id=com.fearlesssingh.ankganithindi";
                        dialog(ratingStr);
                        break;
                    case R.id.arihant_reasoning:
                        String arihantStr = "https://play.google.com/store/apps/details?id=com.rockingdeveloper.arihantReasoning";
                        dialog(arihantStr);
                        break;
                    case R.id.telegram:
                        String teleStr = "https://t.me/rockingcommunity";
                        dialog(teleStr);
                        break;
                    case R.id.about:
                        fragReplace(new AboutFragment());
                        break;
                    case R.id.exit_app:
                        exitApp();
                        break;
                    case R.id.share:
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Rs Agarwal Ankganit");
                        shareIntent.putExtra(Intent.EXTRA_TEXT, "Rs Agarwal Ankganit🥰 Awesome Ui user easy use❤ All competitive exam railway,ssc.banking etc most useful books  🥰 Thank you so much 💖 share this application 👇Download Now" +
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
