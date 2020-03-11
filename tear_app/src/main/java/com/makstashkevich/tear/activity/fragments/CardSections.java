package com.makstashkevich.tear.activity.fragments;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.makstashkevich.tear.R;
import com.makstashkevich.tear.activity.adapter.SectionParameters;
import com.makstashkevich.tear.activity.adapter.StatelessSection;

import java.util.List;

public class CardSections extends StatelessSection {
    private int layout;
    private String data, dataWeek;
    private int dataColor;
    private List<String[]> param;

    private FragmentManager fragmentManager;

    public CardSections(List<Object> data, List<String[]> preferences, @LayoutRes int layout, FragmentManager fragmentManager) {
        super(SectionParameters.builder()
                .itemResourceId(layout)
                .headerResourceId(R.layout.list_block)
                .build());
        this.data = (String) data.get(0);
        this.dataWeek = (String) data.get(1);
        this.dataColor = (int) data.get(2);
        this.param = preferences;
        this.layout = layout;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public int getContentItemsTotal() {
        if (layout == R.layout.list_notitem) return 1;
        return param.size();
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new HeaderViewHolder(view);
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        if (layout == R.layout.list_notitem) return new NoItemViewHolder(view);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        HeaderViewHolder itemHolder = (HeaderViewHolder) holder;
        itemHolder.date.setText(data);
        itemHolder.dayWeek.setText(dataWeek);
        itemHolder.dayWeek.setBackgroundResource(dataColor);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int i) {
        if (layout == R.layout.list_notitem) return;
        ItemViewHolder itemHolder = (ItemViewHolder) holder;
        String[] p = param.get(i);
        itemHolder.num.setText(p[0]);
        itemHolder.subj.setText(p[1]);
        itemHolder.room.setText(p[2]);
        itemHolder.teacher.setText(p[3]);
        itemHolder.time.setText(p[4]);

        if (Boolean.parseBoolean(p[5])) {
            itemHolder.subj.setPaintFlags(itemHolder.subj.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            itemHolder.card.setCardBackgroundColor(Color.rgb(245, 245, 245));
        }

        /*String category = p[6];
        itemHolder.category.setText(category);
        itemHolder.category.setBackgroundResource(Subject.getBackgroundColor(category));*/


        //менюшка
        BottomDrawerFragment menu = new BottomDrawerFragment();
        Bundle list = new Bundle();
        list.putStringArray("dataList", p);
        menu.setArguments(list);
        itemHolder.card.setOnClickListener(v -> {
            Fragment old = fragmentManager.findFragmentByTag("bottom_drawer");
            if (old == null) menu.show(fragmentManager, "bottom_drawer");
        });
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        private final TextView date;
        private final TextView dayWeek;

        HeaderViewHolder(View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            dayWeek = itemView.findViewById(R.id.dayWeek);
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView num;
        private final TextView subj;
        private final TextView room;
        private final TextView teacher;
        private final TextView time;
        private final CardView card;
//        private final TextView category;

        ItemViewHolder(View itemView) {
            super(itemView);
            num = itemView.findViewById(R.id.num);
            subj = itemView.findViewById(R.id.subj);
            room = itemView.findViewById(R.id.room);
            teacher = itemView.findViewById(R.id.teacher);
            time = itemView.findViewById(R.id.time);
            card = itemView.findViewById(R.id.card);
//            category = itemView.findViewById(R.id.category);
        }
    }

    class NoItemViewHolder extends RecyclerView.ViewHolder {
        NoItemViewHolder(View itemView) {
            super(itemView);
        }
    }
}