package com.makstashkevich.tear.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.text.Html;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.makstashkevich.tear.R;

public class ContactsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        Fragment fragment = new ContactsFragment();
        FragmentTransaction frTr = getFragmentManager().beginTransaction();
        if (savedInstanceState == null) {
            frTr.add(R.id.list_contacts, fragment, "contacts_fragment");
            frTr.commit();
        }
    }

    public static class ContactsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.contacts);

            Preference dev = getPreferenceManager().findPreference("dev");
            dev.setOnPreferenceClickListener(preference -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(preference.getContext());
                View layout = getText(preference,
                        "Я в социальных сетях:<br/>" +
                                "<b><a href=http://vk.com/makstashkevich>Вконтакте</a></b>, " +
                                "<b><a href=http://t.me/makstashkevich>Telegram</a></b><br/><br/>" +
                                "Почта для связи: <i><a href=mailto:makstashkevich@gmail.com>makstashkevich@gmail.com</a></i>"
                );
                builder.setTitle("Максим Сташкевич")
                        .setView(layout)
                        .setCancelable(false)
                        .setNegativeButton("Закрыть",
                                (dialog, which) -> dialog.cancel());
                AlertDialog alert = builder.create();
                alert.show();
                return false;
            });

            Preference helper = getPreferenceManager().findPreference("helper");
            helper.setOnPreferenceClickListener(preference -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(preference.getContext());
                View layout = getText(preference,
                        "<a href=http://vk.com/id145565650>Динар Зарипов</a><br/>" +
                                "<a href=http://vk.com/id997714>Максим Бондарев</a><br/>" +
                                "<a href=http://vk.com/id297967422>Павел Синица</a><br/>" +
                                "<a href=http://vk.com/id359336028>Тема Новиков</a><br/>" +
                                "<a href=http://vk.com/id168427912>Даниил Малькович</a>"
                );
                builder.setTitle("Благодарности")
                        .setView(layout)
                        .setCancelable(false)
                        .setNegativeButton("Закрыть",
                                (dialog, which) -> dialog.cancel());
                AlertDialog alert = builder.create();
                alert.show();
                return false;
            });
        }

        private View getText(Preference preference, String data) {
            LayoutInflater inflater = (LayoutInflater) preference.getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            @SuppressLint("InflateParams") View layout = inflater.inflate(R.layout.dialog_settings, null);
            TextView text = layout.findViewById(R.id.text);
            text.setMovementMethod(LinkMovementMethod.getInstance());
            Spannable s = (Spannable) Html.fromHtml(data);
            for (URLSpan u : s.getSpans(0, s.length(), URLSpan.class)) {
                s.setSpan(new UnderlineSpan() {
                    public void updateDrawState(TextPaint tp) {
                        tp.setUnderlineText(false);
                    }
                }, s.getSpanStart(u), s.getSpanEnd(u), 0);
            }
            text.setText(s);
            return layout;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contacts, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
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
