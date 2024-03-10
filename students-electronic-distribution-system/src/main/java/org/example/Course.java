package org.example;

import java.util.*;

public class Course<T extends Student> {
    protected String name;
    protected int maxCapacity;
    protected ArrayList<T> participants = new ArrayList<>();
    protected double lastAverageAccepted = 0;
    public Course(String name, int maxCapacity) {
        this.name = name;
        this.maxCapacity = maxCapacity;
    }

    protected void enrollStudent(T student) {
        participants.add(student);
        student.asignedCourseName = name;
    }
}
