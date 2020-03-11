package com.makstashkevich.tear.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.makstashkevich.tear.R;
import com.makstashkevich.tear.activity.MainActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Schedule {
    private String[][][][] list;

    public Schedule(String[][][][] data) {
        list = data;
    }

    public ArrayList<Data> getParameters(MainActivity main) {
        SharedPreferences sharedPref = main.getPreferences(Context.MODE_PRIVATE);
        String savedName = sharedPref.getString("savedName", "");
        int savedParam = sharedPref.getInt("savedParam", Const.PARAM_NONE);
        boolean zaoch = sharedPref.getBoolean("savedZaoch", false);

        String[] lists;
        String[][] alist;
        String name = "";
        ArrayList<Data> parameters = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        int week = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);

        int dayX = 3; //4
        int iss = 0;

        boolean future = false;
        int a = (hour >= 17 ? week == 7 ? 1 : week + 1 : week);

        try { //fix NullPointerException
            if (list[1][1][1][0] != null) {
                if (week == 0 || week >= 5) {
                    future = true;
                    a = 1;
                }
            }
        } catch (NullPointerException ex) {
            future = false;
        }

        int all = 8 - a;

        for (; a <= 7; a++) { //перебор дней недели
            alist = new String[30][10]; //список предметов
            int e = 0;
            String group;
            switch (savedParam) {
                case Const.PARAM_GROUP:
                    group = savedName + (zaoch ? "з" : "");
                    for (int b = 0; b < 200; b++) {
                        for (int c = 0; c < 20; c++) {
                            if (list[a][b][c][0] != null && list[a][b + 1][c][0] != null) {
                                if (list[a][b][c][0].equals(group) && !list[a][b + 1][c][0].equals("") && !isNumber(list[a][b + 1][c][0])) {
                                    List<Object> d = getTime((a == dayX ? 1 : 0), c);
                                    String cab = list[a][b + 2][c][0];
                                    cab = (cab.equals("")) ? "спортзал и др." : cab + " каб.";
                                    String teacher = list[a][b][1][0];
                                    String subj = list[a][b + 1][c][0];

                                    lists = new String[10];
                                    lists[0] = (String) d.get(0);
                                    lists[1] = subj;

                                    if (alist[(int) d.get(2)][0] != null) {
                                        String[] cc = alist[(int) d.get(2)];
                                        if (cc[1] != null &&
                                                cc[1].contains(subj)) {
                                            if (cc[2] != null && !cc[2].contains(cab))
                                                cc[2] = (cc[2] + System.lineSeparator() + cab);
                                            if (cc[3] != null && !cc[3].contains(teacher))
                                                cc[3] = (cc[3] + System.lineSeparator() + teacher);
                                        }
                                    } else {
                                        lists[2] = cab;
                                        lists[3] = teacher;
                                        lists[4] = (String) d.get(1);
                                        lists[5] = String.valueOf(!future && getStrike(a, week, hour, min, (String) d.get(4)));
                                        lists[6] = list[a][b + 1][c][1];
                                        alist[(int) d.get(2)] = lists;
                                    }
                                    e++;
                                    c++;
                                }
                            }
                        }
                    }
                    break;
                case Const.PARAM_TEACHER:
                    for (int b = 0; b < 100; b++)
                        if (list[a][b][1][0] != null) {
                            if (list[a][b][1][0].toLowerCase().contains(savedName.toLowerCase() + " ")) {
                                name = list[a][b][1][0];
                                for (int c = 2; c < 20; c++) {
                                    if (list[a][b][c][0] != null && !list[a][b][c][0].equals("")) {
                                        List<Object> d = getTime((a == dayX ? 1 : 0), c);
                                        String cab = list[a][b + 2][c][0];
                                        cab = (cab.equals("")) ? "спортзал и др." : cab + " каб.";
                                        group = list[a][b][c][0] + " группа";
                                        if (group.contains("з"))
                                            group = group.replaceFirst("з", " заочная");
                                        String subj = list[a][b + 1][c][0];

                                        lists = new String[10];
                                        lists[0] = (String) d.get(0);
                                        lists[1] = group;

                                        if (alist[(int) d.get(2)][0] != null) {
                                            String[] cc = alist[(int) d.get(2)];
                                            if (cc[1] != null &&
                                                    cc[1].contains(group)) {
                                                if (cc[2] != null && !cc[2].contains(cab))
                                                    cc[2] = (cc[2] + System.lineSeparator() + cab);
                                                if (cc[3] != null && !cc[3].contains(subj))
                                                    cc[3] = (cc[3] + System.lineSeparator() + subj);
                                            }
                                        } else {
                                            lists[2] = cab;
                                            lists[3] = list[a][b + 1][c][0];
                                            lists[4] = (String) d.get(1);
                                            lists[5] = String.valueOf(!future && getStrike(a, week, hour, min, (String) d.get(4)));
                                            lists[6] = list[a][b + 1][c][1];
                                            alist[(int) d.get(2)] = lists;
                                        }
                                        e++;
                                    }
                                }
                                b = 100;
                            }
                        }
                    break;
                case Const.PARAM_CABINET:
                    name = savedName + " кабинет";
                    for (int b = 0; b < 200; b++) {
                        for (int c = 0; c < 20; c++) {
                            if (b > 1)
                                if (list[a][b][c][0] != null && list[a][b - 1][c][0] != null) {
                                    if (list[a][b][c][0].equals(savedName) && !list[a][b - 1][c][0].equals("")) {
                                        List<Object> d = getTime((a == dayX ? 1 : 0), c);
                                        String groups = list[a][b - 2][c][0] + " группа";
                                        if (groups.contains("з"))
                                            groups = groups.replaceFirst("з", " заочная");
                                        String teacher = list[a][b - 2][1][0];
                                        String subj = list[a][b - 1][c][0];

                                        lists = new String[10];
                                        lists[0] = (String) d.get(0);
                                        lists[1] = subj;

                                        if (alist[(int) d.get(2)][0] != null) {
                                            String[] cc = alist[(int) d.get(2)];
                                            if (cc[1] != null &&
                                                    cc[1].contains(subj)) {
                                                if (cc[2] != null && !cc[2].contains(groups))
                                                    cc[2] = (cc[2] + System.lineSeparator() + groups);
                                                if (cc[3] != null && !cc[3].contains(teacher))
                                                    cc[3] = (cc[3] + System.lineSeparator() + teacher);
                                            }
                                        } else {
                                            lists[2] = groups;
                                            lists[3] = teacher;
                                            lists[4] = (String) d.get(1);
                                            lists[5] = String.valueOf(!future && getStrike(a, week, hour, min, (String) d.get(4)));
                                            lists[6] = list[a][b - 1][c][1];
                                            alist[(int) d.get(2)] = lists;
                                        }
                                        e++;
                                        c++;
                                    }
                                }
                        }
                    }
                    break;
            }

            ArrayList<String[]> list;
            if (e == 0) {
                list = null;
                iss++;
            } else {
                list = new ArrayList<>();
                for (String[] anAlist : alist) if (anAlist[1] != null) list.add(anAlist);
            }
            parameters.add(new Data(getDay(a, future), list));
        }

        if (iss == all) { //ничего не найдено
            String error = "Расписание не найдено..";
            switch (savedParam) {
                case Const.PARAM_GROUP:
                    error = "Расписание для" + (zaoch ? " заочной " : " ") + "группы " + savedName + " не найдено." +
                            System.lineSeparator() + System.lineSeparator() + "Возможно группа на практике или каникулах";
                    break;
                case Const.PARAM_TEACHER:
                    error = "Расписание не найдено." + System.lineSeparator() + System.lineSeparator() + "Возможно вы не правильно ввели фамилию преподавателя";
                    break;
                case Const.PARAM_CABINET:
                    error = "Расписание не найдено." + System.lineSeparator() + System.lineSeparator() + "Возможно вы не правильно ввели номер кабинета";
                    break;
            }
            main.sendErrorActivity(error);
            return null;
        }

        main.setTitle(
                savedParam == Const.PARAM_GROUP ? (
                        (zaoch) ? savedName + " заочная группа" : savedName + " группа"
                ) : (
                        name.equals("") ? (String) main.getText(R.string.name_main) : name
                )
        );
        return parameters;
    }

    private boolean getStrike(int a, int week, int hour, int min, String time) {
        if (a < week) {
            return true;
        } else if (a == week) {
            String[] times = time.split(":");
            if (times.length >= 2) { //fix ArrayIndexOutOfBoundsException
                int h = Integer.decode(times[0]);
                return hour > h || hour == h && min >= Integer.decode(times[1]);
            }
        }
        return false;
    }

    private ArrayList<Object> getDay(int i, boolean future) {
        String day = "Понедельник";
        switch (i) {
            case 1:
                day = "Понедельник";
                break;
            case 2:
                day = "Вторник";
                break;
            case 3:
                day = "Среда";
                break;
            case 4:
                day = "Четверг";
                break;
            case 5:
                day = "Пятница";
                break;
            case 6:
                day = "Суббота";
                break;
            case 7:
                day = "Воскресенье";
                break;
        }

        Calendar calendar = Calendar.getInstance();
        String data = calendar.get(Calendar.DAY_OF_MONTH) + "." + (calendar.get(Calendar.MONTH) + 1);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("d MMM");
        int week = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        int color = R.drawable.block_all;

        if (future) {
            if (week == 0) week = 7;
            if (i == 1 && week == 7) {
                data = "завтра";
                color = R.drawable.block_tomorrow;
            } else
                data = sdf.format(new Date(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH) + ((7 - week) + i)));
        } else {
            if (i == week) {
                data = "сегодня";
                color = R.drawable.block_today;
            } else if (i < week) {
                int date = week - i;
                if (date == 1) {
                    data = "вчера";
                    color = R.drawable.block_yesterday;
                } else
                    data = sdf.format(new Date(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH) - date));
            } else if (i > week) {
                int date = i - week;
                if (date == 1) {
                    data = "завтра";
                    color = R.drawable.block_tomorrow;
                } else
                    data = sdf.format(new Date(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH) + date));
            }
        }

        ArrayList<Object> ret = new ArrayList<>();
        ret.add(day);
        ret.add(data);
        ret.add(color);
        ret.add(week);
        return ret;
    }

    private List<Object> getTime(int type, int num) {
        String text = "";
        String timeStart = "";
        String timeEnd = "";
        int n = 0;
        switch (type) {
            default:
            case 0:
                switch (num) {
                    case 1:
                    case 2:
                    case 3:
                        text = "1" + System.lineSeparator() + "2";
                        timeStart = "8:30";
                        timeEnd = "10:05";
                        n = 0;
                        break;
                    case 4:
                    case 5:
                        text = "3" + System.lineSeparator() + "4";
                        timeStart = "10:25";
                        timeEnd = "12:00";
                        n = 1;
                        break;
                    case 6:
                    case 7:
                        text = "5" + System.lineSeparator() + "6";
                        timeStart = "12:20";
                        timeEnd = "13:55";
                        n = 2;
                        break;
                    case 8:
                    case 9:
                        text = "7" + System.lineSeparator() + "8";
                        timeStart = "14:10";
                        timeEnd = "15:45";
                        n = 3;
                        break;
                    case 10:
                    case 11:
                        text = "9" + System.lineSeparator() + "10";
                        timeStart = "15:55";
                        timeEnd = "17:30";
                        n = 4;
                        break;
                    case 12:
                    case 13:
                        text = "11" + System.lineSeparator() + "12";
                        timeStart = "17:40";
                        timeEnd = "19:15";
                        n = 5;
                        break;
                    case 14:
                    case 15:
                        text = "13" + System.lineSeparator() + "14";
                        timeStart = "19:25";
                        timeEnd = "20:50";
                        n = 6;
                        break;
                    case 16:
                    case 17:
                        text = "15" + System.lineSeparator() + "16";
                        timeStart = "21:00";
                        timeEnd = "22:30";
                        n = 7;
                        break;
                }
                break;
            case 1:
                switch (num) {
                    case 1:
                    case 2:
                    case 3:
                        text = "1" + System.lineSeparator() + "2";
                        timeStart = "08:30";
                        timeEnd = "10:05";
                        n = 0;
                        break;
                    case 4:
                    case 5:
                        text = "3" + System.lineSeparator() + "4";
                        timeStart = "10:25";
                        timeEnd = "12:00";
                        n = 1;
                        break;
                    case 6:
                        text = "5";
                        timeStart = "12:20";
                        timeEnd = "13:05";
                        n = 2;
                        break;
                    case 7:
                    case 8:
                        text = "6" + System.lineSeparator() + "7";
                        timeStart = "13:15";
                        timeEnd = "14:50";
                        n = 3;
                        break;
                    case 9:
                    case 10:
                        text = "8" + System.lineSeparator() + "9";
                        timeStart = "15:00";
                        timeEnd = "16:35";
                        n = 4;
                        break;
                    case 11:
                    case 12:
                        text = "10" + System.lineSeparator() + "11";
                        timeStart = "16:45";
                        timeEnd = "18:20";
                        n = 5;
                        break;

                    case 13:
                    case 14:
                        text = "12" + System.lineSeparator() + "13";
                        timeStart = "18:30";
                        timeEnd = "20:05";
                        n = 6;
                        break;
                    case 15:
                    case 16:
                        text = "14" + System.lineSeparator() + "15";
                        timeStart = "20:10";
                        timeEnd = "21:45";
                        n = 7;
                        break;
                    case 17:
                    case 18:
                        text = "16" + System.lineSeparator() + "17";
                        timeStart = "21:55";
                        timeEnd = "23:30";
                        n = 8;
                        break;
                }
        }

        List<Object> list = new ArrayList<>();
        list.add(0, text);
        list.add(1, timeStart + " - " + timeEnd);
        list.add(2, n);
        list.add(3, timeStart);
        list.add(4, timeEnd);
        return list;
    }

    static boolean isNumber(String str) {
        if (str == null || str.isEmpty()) return false;
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) return false;
        }
        return true;
    }
}
