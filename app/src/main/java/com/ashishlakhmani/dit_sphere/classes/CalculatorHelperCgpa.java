package com.ashishlakhmani.dit_sphere.classes;


public class CalculatorHelperCgpa {

    private double sgpa;
    private double totalCredits;

    public CalculatorHelperCgpa(double sgpa, double totalCredits) {
        this.sgpa = sgpa;
        this.totalCredits = totalCredits;
    }

    public double getSgpa() {
        return sgpa;
    }

    public double getTotalCredits() {
        return totalCredits;
    }
}
