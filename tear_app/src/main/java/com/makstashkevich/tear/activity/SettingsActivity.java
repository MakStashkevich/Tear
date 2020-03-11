package com.makstashkevich.tear.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.makstashkevich.tear.R;
import com.makstashkevich.tear.utils.Const;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        String versionName = "";
        try {
            versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Fragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString("version", versionName);
        fragment.setArguments(args);

        FragmentTransaction frTr = getFragmentManager().beginTransaction();
        if (savedInstanceState == null) {
            frTr.add(R.id.list_settings, fragment, "settings_fragment");
            frTr.commit();
        }
    }

    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void setArguments(Bundle args) {
            super.setArguments(args);
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);

            /* SETTINGS LIST */
            final ListPreference list = (ListPreference) getPreferenceScreen().findPreference("sett_user_list");
            final SwitchPreference zaoch = (SwitchPreference) getPreferenceScreen().findPreference("sett_user_zaoch");
            final EditTextPreference text = (EditTextPreference) getPreferenceScreen().findPreference("sett_user_text");

            //update in open
            zaoch.setEnabled(list.getValue().equals("group"));
            updatePref(text, list.getValue());

            //listeners
            list.setOnPreferenceChangeListener((preference, o) -> {
                zaoch.setEnabled(o.equals("group"));
                text.setText("");
                updatePref(text, o.toString());
                setUpdate();
                return true;
            });
            zaoch.setOnPreferenceChangeListener((preference, newValue) -> {
                setUpdate();
                return true;
            });
            text.setOnPreferenceChangeListener((preference, newValue) -> {
                String value = (String) newValue;
                switch (list.getValue()) {
                    case "teach":
                        if (value.equals("")) {
                            toast("Вы не указали фамилию!");
                            return false;
                        } else {
                            if (isNumber(value)) {
                                toast("Указывайте именно фамилию!");
                                return false;
                            } else if (value.contains(" ")) {
                                toast("Укажите только фамилию, без лишних символов!");
                                return false;
                            }
                        }
                        break;
                    case "group":
                        if (value.equals("")) {
                            toast("Вы не указали группу!");
                            return false;
                        } else {
                            if (!isNumber(value)) {
                                toast("Указывайте именно номер своей группы!");
                                return false;
                            } else if (isItThreeNumber(Integer.parseInt(value))) {
                                toast("Укажите трехзначное число!");
                                return false;
                            }
                        }
                        break;
                    case "cabinet":
                        if (value.equals("")) {
                            toast("Вы не указали кабинет!");
                            return false;
                        } else {
                            if (!isNumber(value)) {
                                toast("Указывайте именно номер кабинета!");
                                return false;
                            } else if (isItThreeNumber(Integer.parseInt(value))) {
                                toast("Укажите трехзначное число!");
                                return false;
                            }
                        }
                        break;
                }
                setUpdate();
                return true;
            });

            /* NOTIFICATIONS */
            SwitchPreference not = (SwitchPreference) getPreferenceScreen().findPreference("sett_notification");
            not.setEnabled(false);
            not.setChecked(false);

            SwitchPreference alarm = (SwitchPreference) getPreferenceScreen().findPreference("sett_notification_morning");
            alarm.setOnPreferenceChangeListener((preference, newValue) -> {
                setUpdate(Const.RESULT_ALARM);
                return true;
            });

            Preference time = getPreferenceScreen().findPreference("sett_notification_update_morning");
            final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            time.setOnPreferenceClickListener(preference -> {
                new TimePickerDialog(getActivity(),
                        (view, hourOfDay, minute) -> {
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putInt(getString(R.string.hour), hourOfDay);
                            editor.putInt(getString(R.string.min), minute);
                            editor.apply();
                        },
                        sharedPref.getInt(getString(R.string.hour), 7),
                        sharedPref.getInt(getString(R.string.min), 0), true)
                        .show();
                setUpdate(Const.RESULT_ALARM);
                return true;
            });

            /* ABOUT */
            Preference voice = getPreferenceScreen().findPreference("sett_tear_voice");
            voice.setOnPreferenceClickListener(preference -> {
                final String appPackageName = "com.makstashkevich.tear"; // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (ActivityNotFoundException activity) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                return true;
            });

            final String ver = getArguments().getString("version");
            Preference info = getPreferenceScreen().findPreference("sett_tear_app");
            info.setSummary(
                    "Tear — неофициальное приложение колледжа КСТМиА УО РИПО для просмотра текущего расписания на всю неделю." +
                            System.lineSeparator() + System.lineSeparator() +
                            "Текущая версия: " + ver
            );

            Preference contacts = getPreferenceScreen().findPreference("sett_tear_contacts");
            contacts.setOnPreferenceClickListener(preference -> {
                startActivity(new Intent(preference.getContext(), ContactsActivity.class));
                return true;
            });
        }

        private void updatePref(EditTextPreference pref, String list) {
            int title, summary, input;
            switch (list) {
                default:
                case "group":
                    title = R.string.title_group;
                    summary = R.string.summary_group;
                    input = InputType.TYPE_CLASS_NUMBER;
                    break;
                case "teach":
                    title = R.string.title_teach;
                    summary = R.string.summary_teach;
                    input = InputType.TYPE_CLASS_TEXT;
                    break;
                case "cabinet":
                    title = R.string.title_cabinet;
                    summary = R.string.summary_cabinet;
                    input = InputType.TYPE_CLASS_NUMBER;
                    break;
            }
            pref.setDialogTitle(title);
            pref.setTitle(title);
            pref.setSummary(summary);
            pref.getEditText().setInputType(input);
        }

        private void toast(String text) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Toast.makeText(getContext(), text, Toast.LENGTH_LONG).show();
            }
        }

        private static boolean isItThreeNumber(int number) {
            return (100 > number) || (number > 999);
        }

        private boolean isNumber(String str) {
            if (str == null || str.isEmpty()) return false;
            for (int i = 0; i < str.length(); i++) {
                if (!Character.isDigit(str.charAt(i))) return false;
            }
            return true;
        }

        private void setUpdate() {
            setUpdate(RESULT_OK);
        }

        private void setUpdate(int result) {
            Activity settings = getActivity();
            settings.setResult(result, settings.getIntent());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_site:
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://college-ripo.by")));
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(this, "Для открытия сайта, установите браузер на свое устройство!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                return true;
            case R.id.action_back:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
