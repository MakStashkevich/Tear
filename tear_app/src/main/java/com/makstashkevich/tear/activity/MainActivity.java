package com.makstashkevich.tear.activity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.NavUtils;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.makstashkevich.tear.R;
import com.makstashkevich.tear.activity.adapter.SectionedRecyclerViewAdapter;
import com.makstashkevich.tear.activity.fragments.CardSections;
import com.makstashkevich.tear.tasks.RetrieveDataTask;
import com.makstashkevich.tear.tasks.VKPostsTask;
import com.makstashkevich.tear.utils.Alarm;
import com.makstashkevich.tear.utils.Const;
import com.makstashkevich.tear.utils.Data;
import com.makstashkevich.tear.utils.Schedule;

import java.util.ArrayList;

import static com.makstashkevich.tear.utils.Const.REQUEST_SETTINGS;
import static com.makstashkevich.tear.utils.Const.RESULT_ALARM;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView rv;
    private SwipeRefreshLayout swipe;
    private ArrayList<Data> parameters;

    private int contentView = 0;
    private Schedule savedData;

    private String errorText = "";
    private boolean errorHello = false;

    private SearchView searchView;
    private MenuItem starButton;

    public static MainActivity activity;

    static {
        // Make sure that vector drawables work on pre-Lollipop devices
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        initializeActivity(false);
        initializeCards();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_SETTINGS:
                switch (resultCode) {
                    case RESULT_OK:
                        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
                        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor edit = sharedPref.edit();
                        edit.putString("savedName", pref.getString("sett_user_text", ""));
                        edit.putBoolean("savedZaoch", pref.getBoolean("sett_user_zaoch", false));
                        edit.putInt("savedParam", getParam(pref.getString("sett_user_list", "group")));
                        edit.apply();
                        initializeCards();
                        break;
                    case RESULT_ALARM:
                        initializeSettings();
                        break;
                }
                break;
        }
    }

    private int getParam(String list) {
        switch (list) {
            default:
            case "group":
                return Const.PARAM_GROUP;
            case "teach":
                return Const.PARAM_TEACHER;
            case "cabinet":
                return Const.PARAM_CABINET;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (!errorText.equals("") && contentView == R.layout.activity_error)
            initializeActivity(true, errorText, errorHello, true);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem searchViewMenuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchViewMenuItem.getActionView();
        searchView.setSubmitButtonEnabled(false);
        searchView.setQueryHint(getResources().getString(R.string.search));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                SharedPreferences sharedPref = MainActivity.activity.getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = sharedPref.edit();
                edit.putString("savedName", query);
                edit.putInt("savedParam", Const.PARAM_GROUP);
                edit.apply();
                initializeCards();

                searchView.onActionViewCollapsed();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return !newText.isEmpty();
            }
        });
        searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) searchView.onActionViewCollapsed();
        });
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
//        starButton = menu.findItem(R.id.action_star);
//        starButton.setVisible(contentView == R.layout.activity_main);

        //прячем до обновы
//        starButton.setVisible(false);
        menu.findItem(R.id.action_search).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_settings:
                startActivityForResult(new Intent(this, SettingsActivity.class), REQUEST_SETTINGS);
                return true;
            case R.id.action_search:
                return true;
            /*case R.id.action_star:
                int icon = R.drawable.ic_star_off;
                String text = getString(R.string.star_off);
                if(item.getIcon().getConstantState().equals(getResources().getDrawable(R.drawable.ic_star_off).getConstantState())){
                    icon = R.drawable.ic_star_on;
                    text = getString(R.string.star_on);
                }
                item.setIcon(icon);
                item.setTitle(text);
                return true;*/
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()) searchView.setIconified(true);
        else super.onBackPressed();
    }

    public void initializeActivity(boolean error) {
        initializeActivity(error, "", false, false);
    }

    public void initializeActivity(boolean error, String errortext, boolean hello, boolean update) {
        int layout = (error) ? R.layout.activity_error : R.layout.activity_main;
        errorHello = hello;

        if (contentView == layout) {
            if (update) setContentView(layout);
            if (error) {
                TextView text = findViewById(R.id.error_text);
                text.setText(errortext);

                ImageView src = findViewById(R.id.error_src);
                if (src != null)
                    src.setImageResource(hello ? R.drawable.hello : R.drawable.not_found);
            }
            return;
        }

        setContentView(layout);
        contentView = layout;

        swipe = findViewById(R.id.swipe_refresh);
        swipe.setOnRefreshListener(this);

        if (!error) {
            rv = findViewById(R.id.rv);
            rv.setLayoutManager(new LinearLayoutManager(this));
            rv.setHasFixedSize(true);
            if (starButton != null) starButton.setVisible(true);
        } else {
            TextView text = findViewById(R.id.error_text);
            text.setText(errortext);

            ImageView src = findViewById(R.id.error_src);
            if (src != null) src.setImageResource(hello ? R.drawable.hello : R.drawable.not_found);
            if (starButton != null) starButton.setVisible(false);
        }
    }

    public void initializeSettings() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        if (pref.getBoolean("sett_notification_morning", true)) {
            Alarm.send(getBaseContext());
        } else {
            PendingIntent pendingIntent = Alarm.getPendingIntent(getBaseContext());
            AlarmManager manager = (AlarmManager) getBaseContext().getSystemService(Context.ALARM_SERVICE);
            manager.cancel(pendingIntent);
        }
    }

    /**
     * Load data cards & set animation
     */
    public void initializeCards() {
        if (isNetworkDisable()) {
            sendErrorActivity("Без интернета получить данные не получиться..");
            sendErrorNetwork();
            return;
        }

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        int savedParam = sharedPref.getInt("savedParam", Const.PARAM_NONE);
        setTitle(R.string.name_main);

        switch (savedParam) {
            default:
            case Const.PARAM_NONE:
                sendErrorActivity("Укажите в настройках" + System.lineSeparator() + "свою группу, преподавателя или кабинет", true);
                return;
            case Const.PARAM_GROUP:
                if (sharedPref.getString("savedName", "").equals("")) {
                    sendErrorActivity("Укажите в настройках" + System.lineSeparator() + "свою группу", false);
                    return;
                }
                break;
            case Const.PARAM_TEACHER:
                if (sharedPref.getString("savedName", "").equals("")) {
                    sendErrorActivity("Укажите в настройках" + System.lineSeparator() + "фамилию преподавателя", false);
                    return;
                }
                break;
            case Const.PARAM_CABINET:
                if (sharedPref.getString("savedName", "").equals("")) {
                    sendErrorActivity("Укажите в настройках" + System.lineSeparator() + "нужный кабинет", false);
                    return;
                }
                break;
        }

        initializeActivity(false);
        errorText = "";
        errorHello = false;

        if (savedData != null) {
            swipe.setRefreshing(true);
            initializeData(savedData);
        } else {
            initializeData();
        }
    }

    public void sendErrorActivity(String text) {
        sendErrorActivity(text, false);
    }

    public void sendErrorActivity(String text, boolean hello) {
        setTitle(R.string.name_main);
        swipe.setRefreshing(false);

        if (!errorText.equals(text)) {
            initializeActivity(true, text, hello, false);
            errorText = text;
        }
    }

    public void sendNotice(String[] data) {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        int savedParam = sharedPref.getInt("VKPost", 0);
        int id = Integer.parseInt(data[0]);
        if (id > savedParam) {
            sharedPref.edit().putInt("VKPost", id).apply();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
            @SuppressLint("InflateParams") View layout = inflater.inflate(R.layout.dialog_custom, null);
            TextView text = layout.findViewById(R.id.text);
            String s = data[1];
            if (s.length() > 180) {
                s = s.substring(0, 180);
                s = s.substring(0, Math.max(s.lastIndexOf(' '), Math.max(s.lastIndexOf(','), s.lastIndexOf('.'))));
                s += "..";
            }
            text.setText(s);
            builder.setTitle("Новости")
                    .setView(layout)
                    .setCancelable(false)
                    .setNegativeButton("Закрыть",
                            (dialog, which) -> dialog.cancel())
                    .setPositiveButton("Читать", (dialog, which) -> {
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://vk.com/wall-" + data[2] + "_" + id)));
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(activity, "Для открытия сайта, установите браузер или приложение вконтакте на свое устройство!", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    /**
     * Load data & set parameters
     */
    private void initializeData() {
        swipe.setRefreshing(true);
        new RetrieveDataTask(this).execute();
    }

    /**
     * @param schedule
     */
    public void initializeData(Schedule schedule) {
        if (schedule == null) { //NullPointerException
            sendErrorActivity("Расписание не было получено!" + System.lineSeparator() + System.lineSeparator() + "Сообщите об этом разработчику по любой из ссылок в контактах");
            return;
        }

        ArrayList<Data> scheduleParameters;
        try {
            scheduleParameters = schedule.getParameters(this);
        } catch (Exception ex) {
            sendErrorActivity("Ошибка в обработке таблицы расписания!" + System.lineSeparator() + System.lineSeparator() + "Сообщите об этом разработчику по любой из ссылок в контактах");
            return;
        }

        if (scheduleParameters == null) {
            parameters = new ArrayList<>();
            return;
        }
        parameters = scheduleParameters;
        savedData = schedule;

        initializeAdapter();
        initializeAnimation();
        swipe.setRefreshing(false);

        new VKPostsTask(this).execute();
    }

    /**
     * Set cards adapter
     */
    private void initializeAdapter() {
        SectionedRecyclerViewAdapter sectionAdapter = new SectionedRecyclerViewAdapter();
        for (Data data : parameters) {
            int layout = (data.subj != null) ? R.layout.list_item : R.layout.list_notitem;
            sectionAdapter.addSection(new CardSections(data.date, data.subj, layout, getSupportFragmentManager()));
        }
        rv.setAdapter(sectionAdapter);
    }

    /**
     * Set cards load animation
     */
    private void initializeAnimation() {
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(rv.getContext(), R.anim.layout_fall_down);

        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setLayoutAnimation(controller);
        rv.getAdapter().notifyDataSetChanged();
        rv.scheduleLayoutAnimation();
    }

    @Override
    public void onRefresh() {
        if (isNetworkDisable()) {
            swipe.setRefreshing(false);
            sendErrorNetwork();
            return;
        }

        initializeCards();
    }

    private boolean isNetworkDisable() {
        return !isNetworkAvailable();
    }

    /**
     * Check network connection
     *
     * @return boolean
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return (activeNetworkInfo != null && activeNetworkInfo.isConnected());
    }

    /**
     * Send toast about don't network connection
     */
    private void sendErrorNetwork() {
        final Toast toast = Toast.makeText(getApplicationContext(), "Нет подключения к интернету...", Toast.LENGTH_LONG);
        toast.show();
    }
}