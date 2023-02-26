package com.fearlesssingh.ankganithindi.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.fearlesssingh.ankganithindi.Adapters.AnkganitItemAdapter;
import com.fearlesssingh.ankganithindi.Model.AnkganitItemModel;
import com.fearlesssingh.ankganithindi.R;
import com.fearlesssingh.ankganithindi.databinding.FragmentHomeBinding;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import java.util.ArrayList;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    FragmentHomeBinding binding;

    String getRvPosition, getPageNum, getChNum;
    RecyclerView recyclerView;
    AnkganitItemAdapter adapter;
    ArrayList<AnkganitItemModel> bookModels;
    LinearLayoutManager layoutManager;
    public MaterialToolbar toolbar;
    public AppBarLayout appBarLayout;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        // Inflate the layout for this fragment


        recyclerView = binding.homeRv;
        //call method
        loadChapter();
        //getting saved data on shared preference
        getSavedDataPref();

        // find xml variable
        toolbar = requireActivity().findViewById(R.id.topToolbar);
        appBarLayout = requireActivity().findViewById(R.id.appBarLayout);
        appBarLayout.setVisibility(View.VISIBLE);
        toolbar.setVisibility(View.VISIBLE);

        AppCompatActivity appCompatActivity = (AppCompatActivity) requireActivity();
        Objects.requireNonNull(appCompatActivity.getSupportActionBar()).show();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    // Scrolling up

                } else {
                    // Scrolling down
                    binding.continueReading.animate().translationX(1)
                            .setInterpolator(new DecelerateInterpolator()).start();
                }
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    // Do something
                    binding.continueReading.animate().translationX(binding.continueReading.getBottom())
                            .setInterpolator(new AccelerateInterpolator()).start();
                }
            }
        });


        binding.continueReading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // all method call
                sendBundleLoadingFrag();
            }
        });


        return view;
    }

    private void getSavedDataPref() {
        SharedPreferences preferences = requireContext().getSharedPreferences("position", Context.MODE_PRIVATE);
        getRvPosition = preferences.getString("chPosition", null);
        getPageNum = preferences.getString("pagePosition", null);
        getChNum = preferences.getString("chNumber", null);
    }

    private void sendBundleLoadingFrag() {

        if (getRvPosition != null && getPageNum != null && getChNum != null) {
            Bundle bundle = new Bundle();
            bundle.putString("positions", getRvPosition);
            bundle.putString("pageNum", getPageNum);
            bundle.putString("chNumbers",getChNum);
            LoadingFragment loadingFragment = new LoadingFragment();
            loadingFragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, loadingFragment)
                    .addToBackStack("info")
                    .commit();

        } else {
            try {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext(), R.style.BottomSheetDialog);
                View v = LayoutInflater.from(requireContext()).inflate(R.layout.continuer_reading_layout, (LinearLayout) requireActivity().findViewById(R.id.contunue_reading_linear));
                bottomSheetDialog.setContentView(v);
                bottomSheetDialog.show();

            } catch (Exception e) {
                Toast.makeText(requireContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(requireContext(), "Welcome 😊 \n please continue reading book 🤗", Toast.LENGTH_SHORT).show();
        }
    }
    // make method create all chapter
    @SuppressLint("NotifyDataSetChanged")
    public void loadChapter() {
        // Initialize model list on mainActivity
        bookModels = new ArrayList<>();
        bookModels.add(new AnkganitItemModel("01", "संख्या पद्दति", "Number System", "Continue ( Click Here )"));
        bookModels.add(new AnkganitItemModel("02", "महत्म समावर्तक और लघुत्तम समावर्तक", "HCF AND LCM", "Continue ( Click Here )"));
        bookModels.add(new AnkganitItemModel("03", "दशमलव भिन्न", "Decimal Fraction", "Continue ( Click Here )"));
        bookModels.add(new AnkganitItemModel("04", "सरलीकरण", "Simplification", "Continue ( Click Here )"));
        bookModels.add(new AnkganitItemModel("05", "वर्गमूल और घनमूल", "Square Root And Cube Root", "Continue ( Click Here )"));
        bookModels.add(new AnkganitItemModel("06", "औसत", "Average", "Continue ( Click Here )"));
        bookModels.add(new AnkganitItemModel("07", "संख्याओं पर आधारित प्रश्न", "Numbers Based Questions", "Continue ( Click Here )"));
        bookModels.add(new AnkganitItemModel("08", "आयु सम्बन्धी प्रश्न", "Problems Of Ages", "Continue ( Click Here )"));
        bookModels.add(new AnkganitItemModel("09", "घातांक और करणी", "Surds And Indices", "Continue ( Click Here )"));
        bookModels.add(new AnkganitItemModel("10", "प्रतिशतता", "Percentage", "Continue ( Click Here )"));
        bookModels.add(new AnkganitItemModel("11", "लाभ तथा  हानि", "Profit And Loss", "Continue ( Click Here )"));
        bookModels.add(new AnkganitItemModel("12", "अनुपात तथा समानुपात", "Ratio & Proportion", "Continue ( Click Here )"));
        bookModels.add(new AnkganitItemModel("13", "साझा", "Partnership", "Continue ( Click Here )"));
        bookModels.add(new AnkganitItemModel("14", "मिश्र समानुपात", "Compound Proportion", "Continue ( Click Here )"));
        bookModels.add(new AnkganitItemModel("15", "समय तथा कार्य", "Time And Work", "Continue ( Click Here )"));
        bookModels.add(new AnkganitItemModel("16", "पाइप तथा टंकी के प्रश्न", "Pipes And Cisterns", "Continue ( Click Here )"));
        bookModels.add(new AnkganitItemModel("17", "समय तथा दूरी", "Time And Distance", "Continue ( Click Here )"));
        bookModels.add(new AnkganitItemModel("18", "रेल सम्बन्धी प्रश्न", "Problems Of Trains", "Continue ( Click Here )"));
        bookModels.add(new AnkganitItemModel("19", "धरा तथा नाव सम्बन्धी प्रश्न", "Boats And Streams", "Continue ( Click Here )"));
        bookModels.add(new AnkganitItemModel("20", "मिश्रण ", "Allegation & Mixture", "Continue ( Click Here )"));
        bookModels.add(new AnkganitItemModel("21", "साधारण ब्याज ", "Simple Interest", "Continue ( Click Here )"));
        bookModels.add(new AnkganitItemModel("22", "चक्र्वृद्धि ब्याज", "Compound interest", "Continue ( Click Here )"));
        bookModels.add(new AnkganitItemModel("23", "क्षेत्रफल", "Aera", "Continue ( Click Here )"));
        bookModels.add(new AnkganitItemModel("24", "ठोस वस्तुओं के आयतन", "Volume of Solids", "Continue ( Click Here )"));
        bookModels.add(new AnkganitItemModel("25", "दौड़", "Races", "Continue ( Click Here )"));
        bookModels.add(new AnkganitItemModel("26", "कैलेंडर ", "Calender", "Continue ( Click Here )"));
        bookModels.add(new AnkganitItemModel("27", "घडी ", "Clocks", "Continue ( Click Here )"));
        bookModels.add(new AnkganitItemModel("28", "स्टॉक तथा शेयर", "Stock & Shares", "Continue ( Click Here )"));
        bookModels.add(new AnkganitItemModel("29", "मिती काटा ", "True Discount", "Continue ( Click Here )"));
        bookModels.add(new AnkganitItemModel("30", "महाजनी बट्टा", "Banker’s Discount", "Continue ( Click Here )"));
        bookModels.add(new AnkganitItemModel("31", "बीजगणित", "Algebra", "Continue ( Click Here )"));
        bookModels.add(new AnkganitItemModel("32", "दो चरों में रेखिक समीकरण", "Lines Equations in Two Variables", "Continue ( Click Here )"));
        bookModels.add(new AnkganitItemModel("33", "दिघट समीकरण", " Quadratic Equations", "Continue ( Click Here )"));
        bookModels.add(new AnkganitItemModel("34", "त्रिकोंमिति ", "Trigonometry", "Continue ( Click Here )"));
        bookModels.add(new AnkganitItemModel("35", "रेखायें तथा कोण", "Lines and Angles", "Continue ( Click Here )"));
        bookModels.add(new AnkganitItemModel("36", "त्रिभुज ", "Triangles", "Continue ( Click Here )"));
        bookModels.add(new AnkganitItemModel("37", "चतुभुर्ग ", "Quardrilaterals", "Continue ( Click Here )"));
        bookModels.add(new AnkganitItemModel("38", "वृत्त  ", "Circle", "Continue ( Click Here )"));
        bookModels.add(new AnkganitItemModel("39", "बहुभुज", "Polygons", "Continue ( Click Here )"));
        bookModels.add(new AnkganitItemModel("40", "सरणियन ", "Tabulation", "Continue ( Click Here )"));
        bookModels.add(new AnkganitItemModel("41", "दण्ड-आलेख", "bar – Graphs", "Continue ( Click Here )"));
        bookModels.add(new AnkganitItemModel("42", "रेखाचित्र ", "Line – Graphs", "Continue ( Click Here )"));
        bookModels.add(new AnkganitItemModel("43", "पाई-चार्ट", "Pie-Chart", "Continue ( Click Here )"));
        bookModels.add(new AnkganitItemModel("44", "संख्या श्रेणी", "Number Series", "Continue ( Click Here )"));

        adapter = new AnkganitItemAdapter(bookModels, getContext());
        adapter.notifyDataSetChanged();
        layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

    }
}
