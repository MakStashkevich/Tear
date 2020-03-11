package com.makstashkevich.tear.tasks;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import com.makstashkevich.tear.activity.MainActivity;
import com.makstashkevich.tear.utils.Schedule;
import com.makstashkevich.tear.utils.Subject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class RetrieveDataTask extends AsyncTask<String, Void, String[][][][]> {

    private Exception exception;

    @SuppressLint("StaticFieldLeak")
    private MainActivity parent;

    public RetrieveDataTask(MainActivity parent) {
        this.parent = parent;
    }

    @Override
    protected String[][][][] doInBackground(String... main) {
        try {
            try {
                URL url = new URL("http://college-ripo.by");
                HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                urlc.setRequestProperty("User-Agent", "test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1000);
                urlc.connect();

                if (urlc.getResponseCode() != 200) return null;
            } catch (IOException e) {
                Log.wtf("RetrieveDataTask", "Ошибка подключения к интернету: " + e);
                return null;
            }

            Document doc = Jsoup.connect("http://college-ripo.by/schedule/setka.htm").get();
            Elements p = doc.select("p.MsoPlainText[style*='mso-line-height-rule:exactly']");
            int i = 0, q = 0;
            String[][][][] list = new String[8][1000][20][3];
            String teacher = "";
            for (Element pp : p) {
                String text = pp.text();
                if (!text.contains("╥") && !text.contains("╫") && !text.contains("╜") && !text.contains("╨") && !text.contains("║ 1 │")) {
                    String[] a = text.split("(│|║)");
                    for (int b = 0; b < a.length; b++) {
                        String bb;
                        /*if (!text.contains(".")) bb = a[b].replaceAll("\\s+", "");
                        else bb = a[b].trim();*/
                        bb = a[b].trim();
                        bb = (bb == null) ? "" : bb;

                        int day = Subject.getDay(bb);
                        if (day != 0 && day != i) {
                            if (day > i || (day == 1 && i >= 6)) { //MEGA SUPER PUPER FIXXXXXX
                                q = 0;
                                i = day;
                            }
                        }

                        if (bb != null && !bb.equals("")) {
                            if (b == 1) teacher = bb;
                        }

                        list[i][q][b] = Subject.get(bb, teacher, q - 1 >= 0 ? list[i][q - 1][b][0] : "");
                    }
                    q++;
                }
            }

            Document docz = Jsoup.connect("http://college-ripo.by/schedule/zamena.htm").get();
            Elements pz = docz.select("p.MsoPlainText");
            int iz = 0;
            int[] qz = {1, 2};
            String group = "000";
            boolean zaoch = false, stop = false;
            for (Element ppz : pz) {
                String textz = ppz.text();
                if (textz.contains("г. З")) {
                    iz = Subject.getDay(textz);
                    zaoch = textz.contains("ЗАОЧНО");
                }
                if (zaoch && !textz.contains("ИЗМЕНЕНИЯ") && !textz.contains("Ауд") && !textz.contains("Замена") && !textz.contains("г. ") && !textz.contains("┐") && !textz.contains("┤") && !textz.contains("┘")) {
                    String[] a = textz.split("(│|║)");
                    for (int b = 0; b < a.length; b++) {
                        switch (b) {
                            case 1: //num
                                a[b] = a[b].replaceAll("\\s+", "");
                                if (a[b].contains("з")) {
                                    group = a[b].split("з")[0] + "з";
                                    a[b] = a[b].replaceAll(group, "");
                                }

                                String[] num = a[b].split("-");
                                try {
                                    qz[0] = Integer.parseInt(num[0]);
                                } catch (NumberFormatException e) {
                                    qz[0] = Integer.parseInt(num[0].substring(0, 3));
                                }
                                qz[1] = qz[0] + 1;
                                break;
                            case 2: //subj
                                a[b] = a[b].substring(0, 3);
                                break;
                            case 3: //cabinet
                                break;
                            case 4: //teacher
                                a[2] = Subject.get(a[2], a[b], String.valueOf(qz[1]))[0];
                                a[b] = a[b].trim();
                                break;
                            case 5: //replace
                                for (int cc = 0; cc < 100; cc++) {
                                    if (stop) {
                                        stop = false;
                                        break;
                                    }
                                    for (int c = 0; c < 20; c++) {
                                        if (list[iz][cc][c][0] != null && list[iz][cc + 1][c][0] != null && list[iz][cc + 2][c][0] != null && list[iz][cc][1][0] != null && !list[iz][cc + 1][c][0].equals("")
                                                && list[iz][cc][c][0].equals(group.replaceAll("з", "")) //group
                                                && list[iz][cc + 1][c][0].equals(a[2]) //subj
                                                && list[iz][cc + 2][c][0].equals(a[3]) //cabinet
                                                && list[iz][cc][1][0].equals(a[4]) //teacher
                                                && c == (qz[0] + 1) //num
                                        ) {
                                            list[iz][cc][c][0] = group;
                                            if (list[iz][cc][c + 1][0] != null && list[iz][cc + 1][c + 1][0] != null && list[iz][cc + 2][c + 1][0] != null
                                                    && !list[iz][cc + 1][c + 1][0].equals("") //check is empty subj
                                                    && list[iz][cc][c + 1][0].equals(group.replaceAll("з", "")) //group
                                                    && list[iz][cc + 1][c + 1][0].equals(a[2]) //subj
                                                    && list[iz][cc + 2][c + 1][0].equals(a[3]) //cabinet
                                                    && (c + 1) == (qz[1] + 1) //num
                                            ) {
                                                list[iz][cc][c + 1][0] = group;
                                            }
                                            stop = true;
                                            break;
                                        }
                                    }
                                }
                                break;
                        }
                    }
                }
            }
            return list;
        } catch (Exception e) {
            this.exception = e;
            return null;
        }
    }

    @Override
    protected void onProgressUpdate(Void... progress) {
    }

    @Override
    protected void onPostExecute(String[][][][] result) {
        Exception e = this.exception;
        if (e != null) {
            Log.wtf("Exception get data", e);
            parent.sendErrorActivity(
                    "Ошибка получения данных." + System.lineSeparator() + System.lineSeparator() +
                            "Плохое соединение или неполадки с сайтом" + System.lineSeparator() +
                            "(может помочь переподключение к интернету)"
            );
        } else parent.initializeData(new Schedule(result));
    }
}