package oopchallenges;

public class Student {

    private int studentId;
    private String studentName;
    private int age;
    private double marks;

    // Constructor
    public Student(int studentId, String studentName,
                   int age, double marks) {

        this.studentId = studentId;
        this.studentName = studentName;
        this.age = age;
        this.marks = marks;
    }

    // Display method
    public void displayDetails() {

        System.out.println("Student ID: " + studentId);
        System.out.println("Student Name: " + studentName);
        System.out.println("Age: " + age);
        System.out.println("Marks: " + marks);
    }

    public static void main(String[] args) {

        Student s1 = new Student(101, "Shiva", 22, 85.5);

        s1.displayDetails();
    }
}