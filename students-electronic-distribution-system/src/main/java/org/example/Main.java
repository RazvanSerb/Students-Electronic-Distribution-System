package org.example;

import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        if (args == null || args.length == 0)
            return;
        AdministrativeOffice administrativeOffice = new AdministrativeOffice();
        String folderPath = "src/main/resources/" + args[0];
        File fileInput = new File(folderPath + "/" + args[0] + ".in");
        Scanner scanner;
        try {
            scanner = new Scanner(fileInput);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        File fileOutput = new File(folderPath + "/" + args[0] + ".out");
        PrintWriter printWriter;
        try {
            printWriter = new PrintWriter(fileOutput);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        while (scanner.hasNextLine()) {
            ArrayList<String> commandParameters = new ArrayList<>();
            commandParameters.addAll(List.of(scanner.nextLine().split(" - ")));
            String commandDirective = commandParameters.get(0);
            switch (commandDirective) {
                case "adauga_student":
                    String addStudentStudyProgram = commandParameters.get(1);
                    String addStudentName = commandParameters.get(2);
                    try {
                        administrativeOffice.addStudent(addStudentStudyProgram, addStudentName);
                    } catch (ExceptionStudentDuplicate e) {
                        printWriter.println("***");
                        printWriter.println("Student duplicat: " + addStudentName);
                    }
                    break;
                case "adauga_curs":
                    String addCourseStudyProgram = commandParameters.get(1);
                    String addCourseName = commandParameters.get(2);
                    int addCourseMaxCapacity = Integer.parseInt(commandParameters.get(3));
                    administrativeOffice.addCourse(addCourseStudyProgram, addCourseName, addCourseMaxCapacity);
                    break;
                case "citeste_mediile":
                    administrativeOffice.loadAverages(folderPath);
                    break;
                case "posteaza_mediile":
                    administrativeOffice.postAverages(printWriter);
                    break;
                case "contestatie":
                    String contestationStudentName = commandParameters.get(1);
                    double contestationNewAverage = Double.parseDouble(commandParameters.get(2));
                    administrativeOffice.contestation(contestationStudentName, contestationNewAverage);
                    break;
                case "adauga_preferinte":
                    String addPreferencesStudentName = commandParameters.get(1);
                    ArrayList<String> addPreferencesRequest = new ArrayList<>();
                    addPreferencesRequest.addAll(commandParameters.subList(2, commandParameters.size()));
                    administrativeOffice.addPreferences(addPreferencesStudentName, addPreferencesRequest);
                    break;
                case "repartizeaza":
                    administrativeOffice.distribute();
                    break;
                case "posteaza_curs":
                    String postCourseDetailsName = commandParameters.get(1);
                    administrativeOffice.postCourseDetails(printWriter, postCourseDetailsName);
                    break;
                case "posteaza_student":
                    String postStudentDetailsName = commandParameters.get(1);
                    administrativeOffice.postStudentDetails(printWriter, postStudentDetailsName);
                    break;
                case "repartizeaza_automat":
                    administrativeOffice.distributeAutomat();
                    break;
            }
        }
        scanner.close();
        printWriter.close();
    }
}
