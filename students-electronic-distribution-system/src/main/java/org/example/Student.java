package org.example;

public class Student {
    private String name;
    private double average;
    protected String asignedCourseName = null;
    public Student(String name, double average) {
        this.name = name;
        this.average = average;
    }

    protected void setName(String name) {
        this.name = name;
    }
    protected String getName() {
        return name;
    }
    protected void setAverage(double average) {
        this.average = average;
    }
    protected double getAverage() {
        return average;
    }
}
