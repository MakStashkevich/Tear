package com.makstashkevich.tear.utils;

import com.makstashkevich.tear.R;

public class Subject {

    public static String[] get(String name, String teacher, String groups) {
        int group = Schedule.isNumber(groups) ? Integer.parseInt(groups) : 0;
        teacher = teacher.trim();
        name = name.trim();
        String category = "лекция";

        switch (name) {
            case "К/ч":
                name = "Кураторский час";
                category = "прочее";
                break;
            case "Охр":
                switch (teacher) {
                    case "Галкина М.В.":
                        name = "Охрана окружающей среды";
                        break;
                    case "Васько Е.Н.":
                        name = "Охрана труда";
                        break;
                }
                break;
            case "Эко":
                name = "Экономика организации";
                break;
            case "Тов":
                name = "Товароведение";
                break;
            case "Спе":
                name = "Специальные технологии";
                break;
            case "Нор":
                name = "Нормирование точности и технические измерения";
                break;
            case "Инт":
                name = "Интегрированные системы автоматизированного проектирования";
                break;

            case "Сис":
                name = "Системы управления оборудования";
                break;
            case "Гиб":
                name = "Гибкие производственные системы";
                break;

            case "Ста":
                name = "Стандартизация и качество продукции";
                break;
            case "При":
                switch (teacher) {
                    case "Жданович А.Г.":
                    case "Скроб О.В.":
                        name = "Прикладная информатика";
                        break;
                    default:
                        name = "Приводы технологического оборудования";
                        break;
                }
                break;
            case "Био":
                name = "Биология";
                break;
            case "Физ":
                switch (teacher) {
                    case "Языченко М.И.":
                        name = "Физика";
                        break;
                    case "Дубатовка О.А.":
                    case "Самосейко И.В.":
                        name = "Физическая культура и здоровье";
                        category = "практика";
                        break;
                }
                break;
            case "Аст":
                name = "Астрономия";
                break;
            case "Мат":
                name = "Математика";
                break;
            case "Хим":
                name = "Химия";
                break;
            case "Защ":
                name = "Защита населения и территорий от чрезвычайных ситуаций";
                break;
            case "Ист":
                name = "История Беларуси";
                break;
            case "ФКЗ":
                name = "Физическая культура и здоровье (факультатив)";
                category = "практика";
                break;
            case "Общ":
                name = "Обществоведение";
                break;
            case "Все":
                name = "Всемирная история";
                break;
            case "МРС":
                name = "Металлорежущие станки";
                break;
            case "Рус":
                name = "Русский язык/литература";
                break;
            case "Бел":
                name = "Белорусский язык/литература";
                break;
            case "Инф":
                name = "Информатика/Информационные технологии";
                break;
            case "Инж":
                name = "Инженерная графика";
                category = "практика";
                break;
            case "Над":
                name = "Надёжность и диагностика технологического оборудования";
                break;
            case "Эле":
                switch (teacher) {
                    case "Кузьмич В.В.":
                        name = "Электрооборудование авто";
                        break;
                    case "Бондарев М.Б.":
                        name = "Электротехника (с основами электроники)";
                        break;
                    case "Синица П.В.":
                        name = "Электротехника/Электронная техника";
                        break;
                }
                break;
            case "Ос.":
                name = "Основы социальной гуманитарии";
                break;
            case "Лог":
                name = "Логистика";
                break;
            case "Уче":
                name = "Учет и отчетность";
                break;
            case "Обо":
                name = "Оборудование хранилищ и устройства для погрузочно-разгрузочных работ";
                break;
            case "Тра":
                name = "Транспортная логистика";
                break;
            case "Осн":
                switch (teacher) {
                    case "Соловей И.А.":
                        switch (group) {
                            case 771:
                            case 772:
                                name = "Основы управления ТС и безопасного движения";
                                break;
                            case 821:
                            case 822:
                            case 811:
                            case 812:
                            case 817:
                                name = "Основы технологии машиностроения";
                                break;
                        }
                        break;
                    case "Смекалова Р.Ч.":
                        name = "Основы социально-гуманитарных наук";
                        break;
                    case "Бондарева М.А.":
                        name = "Основы механики манипуляторов/инженерной графики/технической механики";
                        break;
                    case "Попов А.К.":
                    case "Козловская Ю.И":
                        name = "Основы права";
                        break;
                }
                break;
            case "Тех":
                switch (teacher) {
                    case "Вербицкая С.А.":
                    case "Метелица Е.Г.":
                        name = "Техническое черчение";
                        category = "практика";
                        break;
                    case "Бибчикова А.Е.":
                    case "Бондарева М.А.":
                        name = "Техническая механика";
                        break;
                    case "Синдикевич Д.":
                        name = "Технология механической обработки деталей";
                        break;
                    case "Кмитин А.В.":
                        name = "Технологическое оборудование машиностроительного производства";
                        break;
                    case "Соловей И.А.":
                        name = "Технология машиностроения";
                        break;
                }
                break;
            case "ПДД":
                name = "Правила дорожного движения";
                category = "прочее";
                break;
            case "Обр":
                name = "Обработка материалов и инстументов";
                break;
            case "М-В":
                name = "Материаловедение и технология материалов";
                break;
            case "Ино":
            case "Ин.":
                name = "Иностранный язык";
                category = "практика";
                break;
            case "Про":
                name = "Программирование и наладка автоматизированного оборудования";
                break;
            case "Авт":
                name = "Автоматизация производственных процессов в машиностроении";
                break;
            case "Пси":
                name = "Психология и этика деловых отношений";
                break;
            case "Доп":
                name = "Допуски, посадки и технические измерения";
                break;
            case "Сов":
                name = "Современные технологии";
                break;
            case "Уст":
                name = "Устройство ТС";
                break;
            case "ТО":
                switch (teacher) {
                    case "Каратай И.К.":
                        name = "ТО отрасли";
                        break;
                    case "Алексеев А.Н.":
                        name = "ТО транспортных средств";
                        break;
                }
                break;
            case "Пра":
                switch (teacher) {
                    case "Соловей И.А.":
                        switch (group) {
                            case 771:
                            case 772:
                                name = "Правовые основы дорожного движения";
                                break;
                        }
                        break;
                    default:
                        name = "Практикум";
                        category = "прочее";
                        break;
                }
                break;
            case "Гид":
                name = "Гидропневмоавтоматика";
                break;
            case "Эл.":
                switch (teacher) {
                    case "Синица П.В.":
                    case "Чернявский А.И.":
                    case "Чернявский А.И":
                        name = "Электропривод и электроавтоматика";
                        break;
                }
                break;
            case "ДПЮ":
                name = "Допризывная подготовка юнца";
                category = "практика";
                break;
            case "Гео":
                name = "География";
                break;
        }
        return new String[]{name, category};
    }

    public static int getBackgroundColor(String name) {
        if (name == null) return R.drawable.bb_aqua;
        switch (name) {
            default:
            case "лекция":
                return R.drawable.bb_aqua;
            case "практика":
                return R.drawable.bb_red;
            case "прочее":
                return R.drawable.bb_orange;
        }
    }

    public static int getDay(String text) {
        String[] list = {"ПОНЕДЕЛЬНИК", "ВТОРНИК", "СРЕДА", "ЧЕТВЕРГ", "ПЯТНИЦА", "СУББОТА", "ВОСКРЕСЕНЬЕ"};
        for (int i = 0; i < list.length; ++i) {
            if (text.contains(list[i])) return i + 1;
        }
        return 0;
    }
}
