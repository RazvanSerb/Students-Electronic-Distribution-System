package org.example;

import java.io.*;
import java.util.*;

public class AdministrativeOffice {
    protected ArrayList<Student> students = new ArrayList<>();
    protected ArrayList<Course<StudentLicense>> coursesLicense = new ArrayList<>();
    protected ArrayList<Course<StudentMaster>> coursesMaster = new ArrayList<>();
    protected Map<Student, ArrayList<String>> preferences = new HashMap<>();
    public AdministrativeOffice() {}

    void addStudent(String studyProgram, String name) throws ExceptionStudentDuplicate {
        for (Student s : students)
            if (s.getName().equals(name))
                throw new ExceptionStudentDuplicate();
        switch (studyProgram) {
            case "licenta":
                students.add(new StudentLicense(name, 0));
                break;
            case "master":
                students.add(new StudentMaster(name, 0));
                break;
        }
    }
    void addCourse(String studyProgram, String name, int maxCapacity) {
        switch (studyProgram) {
            case "licenta":
                coursesLicense.add(new Course<>(name, maxCapacity));
                break;
            case "master":
                coursesMaster.add(new Course<>(name, maxCapacity));
                break;
        }
    }
    public void loadAverages(String folderPath) {
        ArrayList<File> listFilesWithAverages = new ArrayList<>();
        int cnt = 1;
        while (true) {
            File file = new File(folderPath + "/" + "note_" + cnt + ".txt");
            if (file.exists()) {
                listFilesWithAverages.add(file);
                cnt++;
                continue;
            }
            break;
        }
        for (File fileInput : listFilesWithAverages) {
            Scanner scanner;
            try {
                scanner = new Scanner(fileInput);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            while (scanner.hasNextLine()) {
                String[] studentData = scanner.nextLine().split(" - ");
                String name = studentData[0];
                double average = Double.parseDouble(studentData[1]);
                for (Student s : students)
                    if (s.getName().equals(name)) {
                        s.setAverage(average);
                        break;
                    }
            }
            scanner.close();
        }
    }
    public void postAverages(PrintWriter printWriter) {
        students.sort(new Comparator<>() {
            public int compare(Student a, Student b) {
                if (a.getAverage() == b.getAverage())
                    return a.getName().compareTo(b.getName());
                return Double.compare(b.getAverage(), a.getAverage());
            }
        });
        printWriter.println("***");
        for (Student s : students)
            printWriter.println(s.getName() + " - " + s.getAverage());
    }
    public void contestation(String name, double newAverage) {
        for (Student s : students)
            if (s.getName().equals(name)) {
                s.setAverage(newAverage);
                break;
            }
    }
    public void addPreferences(String name, ArrayList<String> preferencesRequest) {
        for (Student s : students)
            if (s.getName().equals(name))
                preferences.put(s, preferencesRequest);
    }
    void distribute() {
        students.sort(new Comparator<>() {
            public int compare(Student a, Student b) {
                if (a.getAverage() == b.getAverage())
                    return a.getName().compareTo(b.getName());
                return Double.compare(b.getAverage(), a.getAverage());
            }
        });
        for (Student s : students) {
            if (preferences.get(s) == null)
                continue;
            if (s instanceof StudentLicense) {
                for (String p : preferences.get(s)) {
                    boolean studentEnrollStatus = false;
                    for (Course<StudentLicense> cl : coursesLicense)
                        if (cl.name.equals(p)) {
                            if (cl.participants.size() < cl.maxCapacity) {
                                cl.enrollStudent((StudentLicense) s);
                                cl.lastAverageAccepted = s.getAverage();
                                studentEnrollStatus = true;
                            } else if (s.getAverage() == cl.lastAverageAccepted) {
                                cl.enrollStudent((StudentLicense) s);
                                studentEnrollStatus = true;
                            }
                            break;
                        }
                    if (studentEnrollStatus)
                        break;
                }
            }
            if (s instanceof StudentMaster) {
                for (String p : preferences.get(s)) {
                    boolean studentEnrollStatus = false;
                    for (Course<StudentMaster> cm : coursesMaster)
                        if (cm.name.equals(p)) {
                            if (cm.participants.size() < cm.maxCapacity) {
                                cm.enrollStudent((StudentMaster) s);
                                cm.lastAverageAccepted = s.getAverage();
                                studentEnrollStatus = true;
                            } else if (s.getAverage() == cm.lastAverageAccepted) {
                                cm.enrollStudent((StudentMaster) s);
                                studentEnrollStatus = true;
                            }
                            break;
                        }
                    if (studentEnrollStatus)
                        break;
                }
            }
        }
    }
    void postCourseDetails(PrintWriter printWriter, String name) {
        for (Course<StudentLicense> cl : coursesLicense)
            if (cl.name.equals(name)) {
                printWriter.println("***");
                printWriter.println(name + " (" + cl.maxCapacity + ")");
                cl.participants.sort(new Comparator<>() {
                    public int compare(StudentLicense a, StudentLicense b) {
                        return a.getName().compareTo(b.getName());
                    }
                });
                for (Student s : cl.participants)
                    printWriter.println(s.getName() + " - " + s.getAverage());
                break;
            }
        for (Course<StudentMaster> cm : coursesMaster)
            if (cm.name.equals(name)) {
                printWriter.println("***");
                printWriter.println(name + " (" + cm.maxCapacity + ")");
                cm.participants.sort(new Comparator<>() {
                    public int compare(StudentMaster a, StudentMaster b) {
                        return a.getName().compareTo(b.getName());
                    }
                });
                for (Student s : cm.participants)
                    printWriter.println(s.getName() + " - " + s.getAverage());
                break;
            }
    }
    void postStudentDetails(PrintWriter printWriter, String name) {
        for (Student s : students)
            if (s.getName().equals(name)) {
                printWriter.println("***");
                if (s instanceof StudentLicense)
                    printWriter.print("Student Licenta: ");
                if (s instanceof StudentMaster)
                    printWriter.print("Student Master: ");
                printWriter.print(name + " - " + s.getAverage() + " - ");
                printWriter.println(Objects.requireNonNullElse(s.asignedCourseName, "0"));
                break;
            }
    }
    /* THE COURSES ARE ORDERED DESCENDINGLY BY THE LAST AVERAGE ACCEPTED, BUT IF TWO COURSES HAVE THE SAME LAST AVERAGE,
    THE ORDER BETWEEN THEM WILL BE DONE ALPHABETICALLY */
    /* IN ORDER OF AVERAGE, NON-ENROLLED STUDENTS ARE ASSIGNED TO THE FIRST ELIGIBLE COURSE */
    /* IF THE CAPACITY OF A COURSE IS REACHED, AND A STUDENT HAS AN AVERAGE EQUAL TO THE LAST AVERAGE OF THE COURSE,
    HE IS ASSIGNED TO THAT COURSE */
    void distributeAutomat() {
        students.sort(new Comparator<>() {
            public int compare(Student a, Student b) {
                if (a.getAverage() == b.getAverage())
                    return a.getName().compareTo(b.getName());
                return Double.compare(b.getAverage(), a.getAverage());
            }
        });
        for (Student s : students)
            if (s.asignedCourseName == null) {
                if (s instanceof StudentLicense) {
                    coursesLicense.sort(new Comparator<>() {
                        public int compare(Course<StudentLicense> a, Course<StudentLicense> b) {
                            if (a.lastAverageAccepted == b.lastAverageAccepted)
                                return a.name.compareTo(b.name);
                            return Double.compare(b.lastAverageAccepted, a.lastAverageAccepted);
                        }
                    });
                    for (Course<StudentLicense> cl : coursesLicense)
                        if (cl.participants.size() < cl.maxCapacity) {
                            cl.enrollStudent((StudentLicense) s);
                            cl.lastAverageAccepted = s.getAverage();
                            break;
                        } else if (s.getAverage() == cl.lastAverageAccepted) {
                            cl.enrollStudent((StudentLicense) s);
                            break;
                        }
                }
                if (s instanceof StudentMaster) {
                    coursesMaster.sort(new Comparator<>() {
                        public int compare(Course<StudentMaster> a, Course<StudentMaster> b) {
                            if (a.lastAverageAccepted == b.lastAverageAccepted)
                                return a.name.compareTo(b.name);
                            return Double.compare(b.lastAverageAccepted, a.lastAverageAccepted);
                        }
                    });
                    for (Course<StudentMaster> cm : coursesMaster)
                        if (cm.participants.size() < cm.maxCapacity) {
                            cm.enrollStudent((StudentMaster) s);
                            cm.lastAverageAccepted = s.getAverage();
                            break;
                        } else if (s.getAverage() == cm.lastAverageAccepted) {
                            cm.enrollStudent((StudentMaster) s);
                            break;
                        }
                }
            }
    }
}
