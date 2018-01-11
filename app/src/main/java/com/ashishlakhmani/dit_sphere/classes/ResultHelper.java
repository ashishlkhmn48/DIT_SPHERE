package com.ashishlakhmani.dit_sphere.classes;

public class ResultHelper {
    private String name, code, credit, grade;

    public ResultHelper(String name, String code, String credit, String grade) {
        this.name = name;
        this.code = code;
        this.credit = credit;
        this.grade = grade;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getCredit() {
        return credit;
    }

    public String getGrade() {
        return grade;
    }

}
