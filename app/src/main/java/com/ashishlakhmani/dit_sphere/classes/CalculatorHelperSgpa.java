package com.ashishlakhmani.dit_sphere.classes;

import java.util.HashMap;

public class CalculatorHelperSgpa {

    private double credit;
    private String grade;

    private static final HashMap<String, Integer> map = new HashMap<>();

    static {
        map.put("A+", 10);
        map.put("A", 9);
        map.put("B+", 8);
        map.put("B", 7);
        map.put("C+", 6);
        map.put("C", 5);
        map.put("D+", 4);
        map.put("D", 0);
        map.put("E+", 0);
        map.put("E", 0);
        map.put("F+", 0);
        map.put("F", 0);
    }

    public CalculatorHelperSgpa(double credit, String grade) {
        this.credit = credit;
        this.grade = grade;
    }

    public double getCredit() {
        return credit;
    }

    public int getGradePoints() {
        if (map.get(grade) != null) {
            return map.get(grade);
        }
        return -1;
    }
}
