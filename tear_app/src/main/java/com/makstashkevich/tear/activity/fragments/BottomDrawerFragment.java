package com.makstashkevich.tear.activity.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.navigation.NavigationView;
import com.makstashkevich.tear.R;
import com.makstashkevich.tear.activity.MainActivity;
import com.makstashkevich.tear.utils.Const;

public class BottomDrawerFragment extends BottomSheetDialogFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_drawer, container, false);
        NavigationView navigationView = view.findViewById(R.id.bottom_drawer_navigation);

        final MainActivity main = MainActivity.activity.isDestroyed() ? null : MainActivity.activity;
        SharedPreferences sharedPref = main.getPreferences(Context.MODE_PRIVATE);
        Menu menu = navigationView.getMenu();

        initializeAction(1, menu.findItem(R.id.action_bottom_one), sharedPref, main);
        initializeAction(2, menu.findItem(R.id.action_bottom_two), sharedPref, main);

        getDialog().setOnShowListener(dialog -> {
            View bottomSheetInternal = getDialog().findViewById(R.id.design_bottom_sheet);
            BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_EXPANDED);
        });
        return view;
    }

    private void initializeAction(int num, MenuItem item, SharedPreferences sharedPref, MainActivity main) {
        SharedPreferences.Editor editor = sharedPref.edit();
        int savedParam = sharedPref.getInt("savedParam", Const.PARAM_NONE);
        int param = getParam(num, savedParam);
        String name = getName(savedParam, param, getArguments().getStringArray("dataList"));

        item.setTitle(name);
        item.setVisible(!name.contains("спортзал"));
        item.setOnMenuItemClickListener(v -> {
            editor.putInt("savedParam", param);
            editor.putString("savedName", fixName(param, name));
            editor.putBoolean("savedZaoch", param == Const.PARAM_GROUP && name.contains("заочн"));
            editor.apply();
            main.initializeCards();
            dismiss();
            return false;
        });
    }

    private String getName(int savedParam, int param, String[] data) {
        switch (savedParam) {
            default:
            case Const.PARAM_GROUP:
            case Const.PARAM_TEACHER:
                switch (param) {
                    case Const.PARAM_GROUP:
                        return data[1];
                    case Const.PARAM_TEACHER:
                        return data[3];
                    case Const.PARAM_CABINET:
                        return data[2];
                }
            case Const.PARAM_CABINET:
                switch (param) {
                    case Const.PARAM_GROUP:
                        return data[2];
                    case Const.PARAM_TEACHER:
                        return data[3];
                    case Const.PARAM_CABINET:
                        return data[1];
                }
        }
        return data[1];
    }

    private String fixName(int param, String text) {
        return param == Const.PARAM_TEACHER ? (text.contains(" ") ? text.substring(0, text.indexOf(" ")) : text) : text.replaceAll("[\\D]", "");
    }

    private int getParam(int a, int param) {
        switch (param) {
            default:
            case Const.PARAM_GROUP:
                return a == 1 ? Const.PARAM_TEACHER : Const.PARAM_CABINET;
            case Const.PARAM_TEACHER:
                return a == 1 ? Const.PARAM_GROUP : Const.PARAM_CABINET;
            case Const.PARAM_CABINET:
                return a == 1 ? Const.PARAM_TEACHER : Const.PARAM_GROUP;
        }
    }

    @Override
    public int getTheme() {
        return R.style.BottomSheetDialogTheme;
    }
}
