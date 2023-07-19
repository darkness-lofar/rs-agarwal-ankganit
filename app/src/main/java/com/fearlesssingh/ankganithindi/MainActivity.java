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
import android.widget.LinearLayout;
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
import com.google.android.ump.ConsentDebugSettings;
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
    public static boolean connect = false;
    AppUpdateManager updateManager;
    // rating on phone use google api
    ReviewInfo reviewInfo;
    /////////// update end ///////////////


    // set variable for theme
    SharedPreferences sharedPreferences;
    static SharedPreferences bottomPreferences;
    SharedPreferences.Editor editor;
    static SharedPreferences.Editor bottomEditors;
    int checkedItem;
    String selected = "System Default";
    String CHECKED_ITEM;

    ///////// end theme //////////////////

    /// gdpr /////
    private ConsentInformation consentInformation;
    private ConsentForm consentForm;
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

      /*  sharedPreferences = this.getSharedPreferences("themes", Context.MODE_PRIVATE);
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
        }*/


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
                        }                    }
                },
                new ConsentInformation.OnConsentInfoUpdateFailureListener() {
                    @Override
                    public void onConsentInfoUpdateFailure(FormError formError) {
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



/*    public int getCheckedItem() {
        return sharedPreferences.getInt(CHECKED_ITEM, 0);
    }

    public void setCheckedItem(int i) {
        editor.putInt(CHECKED_ITEM, i);
        editor.apply();
    }*/

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
    public boolean inAppUpdate() {
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
                Toast.makeText(MainActivity.this, "update error \n"+ e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        updateManager.registerListener(installStateUpdatedListener);

        return false;
    }

    InstallStateUpdatedListener installStateUpdatedListener = installState -> {
        if (installState.installStatus() == InstallStatus.DOWNLOADED) {
            popUp();
        }else if (installState.totalBytesToDownload() == InstallStatus.DOWNLOADED){

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
                        if (task.isComplete()){
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
   /* private void okHttp() {
        try {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("https://www.google.com")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showCheckConnectionBottomDialog();
                        }
                    });
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        Logger.getLogger(OkHttpClient.class.getName()).setLevel(Level.FINE);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                connect = true;
                                showBannerAd();
                                inAppUpdate();
                                loadRating();
                            }
                        });
                    }
                }
            });

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }*/

  /*  public void showCheckConnectionBottomDialog() {
        try {
            BottomSheetDialog bottomSheetDialogs = new BottomSheetDialog(MainActivity.this, com.google.android.material.R.style.Theme_Design_BottomSheetDialog);
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.bottoom_navigation_settings,
                    (LinearLayout) findViewById(R.id.linear_bottom), false);
            bottomSheetDialogs.setContentView(view);
            bottomSheetDialogs.setCancelable(false);
            TextView settings, telegram;
            settings = view.findViewById(R.id.settings_tv_btn);
            telegram = view.findViewById(R.id.join_tele_tv_btn);
            telegram.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Uri join = Uri.parse("https://t.me/rockingcommunity");
                    Intent nows = new Intent(Intent.ACTION_VIEW, join);
                    startActivity(nows);
                    finish();
                    bottomSheetDialogs.cancel();
                }
            });
            settings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                        finish();
                        bottomSheetDialogs.cancel();
                    } catch (Exception e) {
                        //Open the generic Apps page:
                        Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                        startActivity(intent);
                        Log.d("LOG", "setting exception\n " + e.getLocalizedMessage());
                    }

                }
            });
            bottomSheetDialogs.show();
        } catch (Exception e) {
            Log.d("LOG", "exception\n " + e.getLocalizedMessage());
            throw new RuntimeException(e);
        }
    }*/

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
    public void networkAvailable(){
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
                if (initializationStatus != null){
                    showBannerAd();
                }
            }
        });
    }

    @Override
    public void networkUnavailable() {
    }
    //////////////// end show banner ad and check firebase connection ////////////////////////////


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
                        Uri privacyUri = Uri.parse("https://sites.google.com/view/sagir-ahmad-math-book-privacy/home");
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


/*            public void showDialog() {
                final String[] theme = {"System Default", "Light", "Dark"};
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this);
                builder.setTitle("Choose Theme");
                builder.setSingleChoiceItems(theme, getCheckedItem(), (dialog, i) -> {
                    selected = theme[i];
                    checkedItem = i;
                    setCheckedItem(getCheckedItem());
                });
                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        if (selected == null) {
                            selected = theme[i];
                            checkedItem = i;


                        }
                        switch (selected) {
                            case "System Default":
                                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                                break;
                            case "Light":
                                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                                break;
                            case "Dark":
                                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                                break;
                        }
                        setCheckedItem(checkedItem);


                        FragmentManager fm = getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.replace(R.id.frame_layout, new HomeFragment());
                        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE); //
                        ft.commit();


                    }

                });
                builder.setNegativeButton("Exit", (dialog, i) -> dialog.dismiss());
                AlertDialog alertDialog = builder.create();
                alertDialog.show();*/

     /*   String[] themes = this.getResources().getStringArray(R.array.theme);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Choose Theme");
        builder.setIcon(R.drawable.ic_baseline_color_lens_24);

        builder.setSingleChoiceItems(themes, getCheckedItem(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                selected = themes[i];
                checkedItem = i;
                setCheckedItem(getCheckedItem());
            }
        });

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (selected == null) {
                    selected = themes[i];
                    checkedItem = i;

                }

                switch (selected) {
                    case "System Default":
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                        Intent intent = getIntent();
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        finish();
                        startActivity(intent);
                        break;

                    case "Light":
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        Intent intent1 = getIntent();
                        intent1.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        finish();
                        startActivity(intent1);
                        break;
                    case "Dark":
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

        builder.create();
        builder.show();
            }*/
        });

    }

}
