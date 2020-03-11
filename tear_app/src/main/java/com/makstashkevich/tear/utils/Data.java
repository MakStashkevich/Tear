package com.makstashkevich.tear.utils;

import java.util.ArrayList;
import java.util.List;

public class Data {
    public List<Object> date;
    public List<String[]> subj;

    Data(ArrayList<Object> date, List<String[]> subj) {
        this.date = date;
        this.subj = subj;
    }
}