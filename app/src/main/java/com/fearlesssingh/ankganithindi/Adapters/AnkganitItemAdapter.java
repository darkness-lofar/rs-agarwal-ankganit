package com.fearlesssingh.ankganithindi.Adapters;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.fearlesssingh.ankganithindi.Fragments.LoadingFragment;
import com.fearlesssingh.ankganithindi.Model.AnkganitItemModel;
import com.fearlesssingh.ankganithindi.R;


import java.util.ArrayList;

public class AnkganitItemAdapter extends RecyclerView.Adapter<AnkganitItemAdapter.AnkganitItemAdapterViewHolder> {

    ArrayList<AnkganitItemModel> itemModelArrayList;
    Context context;


    public AnkganitItemAdapter(ArrayList<AnkganitItemModel> itemModelArrayList, Context context) {
        this.itemModelArrayList = itemModelArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public AnkganitItemAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rv_item_model, parent, false);
        return new AnkganitItemAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnkganitItemAdapterViewHolder holder, int position) {
        AnkganitItemModel itemModel = itemModelArrayList.get(position);
        holder.chapterNumber.setText(itemModel.getChapterNumber());
        holder.chapterNameHindi.setText(itemModel.getChapterNameHindi());
        holder.chapterNameEnglish.setText(itemModel.getChapterNameEnglish());
        holder.button.setText(itemModel.getButton());

        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int adapterPosition = holder.getAdapterPosition();
                Bundle bundle = new Bundle();
                bundle.putString("positions", String.valueOf(adapterPosition));
                bundle.putString("chNumbers", itemModel.getChapterNumber());
                bundle.putString("pageNum","0");
                LoadingFragment loadingFragment = new LoadingFragment();
                loadingFragment.setArguments(bundle);

                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_layout, loadingFragment)
                        .addToBackStack("info")
                        .commit();
            }
        });

    }


    @Override
    public int getItemCount() {
        return itemModelArrayList.size();
    }

    public static class AnkganitItemAdapterViewHolder extends RecyclerView.ViewHolder {

        TextView chapterNameHindi, chapterNameEnglish, chapterNumber, button;

        public AnkganitItemAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            chapterNumber = itemView.findViewById(R.id.chapter_number);
            chapterNameHindi = itemView.findViewById(R.id.Chapter_name_hindi);
            chapterNameEnglish = itemView.findViewById(R.id.Chapter_name_english);
            button = itemView.findViewById(R.id.button);

        }
    }



}
