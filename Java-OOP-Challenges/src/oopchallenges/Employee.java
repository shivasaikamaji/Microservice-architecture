package oopchallenges;

public class Employee {

    // Employee details
    private int employeeId;
    private String employeeName;
    private double salary;

    // Constructor
    public Employee(int employeeId,
                    String employeeName,
                    double salary) {

        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.salary = salary;
    }

    // Display employee details
    public void displayDetails() {

        System.out.println("Employee ID: " + employeeId);
        System.out.println("Employee Name: " + employeeName);
        System.out.println("Monthly Salary: " + salary);
    }

    // Calculate annual salary
    public double calculateAnnualSalary() {

        return salary * 12;
    }

    // Main method
    public static void main(String[] args) {

        Employee emp1 =
                new Employee(101, "Shiva", 25000);

        Employee emp2 =
                new Employee(102, "Ravi", 30000);

        System.out.println("Employee 1 Details");

        emp1.displayDetails();

        System.out.println("Annual Salary: "
                + emp1.calculateAnnualSalary());

        System.out.println("--------------------");

        System.out.println("Employee 2 Details");

        emp2.displayDetails();

        System.out.println("Annual Salary: "
                + emp2.calculateAnnualSalary());
    }
}