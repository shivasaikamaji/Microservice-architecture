
import java.util.Objects;

class Employee implements Comparable<Employee> {
    private final int id;
    private final String name;
    private final int age;

    Employee(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }
public String getName() {
    return name;
}
    // Convert object to readable text
    @Override
    public String toString() {
        return "Employee{id=" + id +
                ", name='" + name +
                "', age=" + age + "}";
    }

    // Compare employees using their ID
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Employee)) return false;

        Employee other = (Employee) obj;
        return this.id == other.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // Sort employees by ID
    @Override
    public int compareTo(Employee other) {
        return Integer.compare(this.id, other.id);
    }
}

public class ObjectMethodsExample {
    public static void main(String[] args) {
        Employee e1 = new Employee(103, "Ravi", 25);
        Employee e2 = new Employee(101, "Sita", 24);
        Employee e3 = new Employee(103, "Ravi Kumar", 28);

        // toString()
        System.out.println("Employee details:");
        System.out.println(e1);

        // equals()
        System.out.println("\nAre e1 and e3 equal? "
                + e1.equals(e3));

        // hashCode()
        System.out.println("Same hash code? "
                + (e1.hashCode() == e3.hashCode()));

        // Comparable and sorting
        Employee[] employees = {e1, e2, e3};
        java.util.Arrays.sort(employees);

        System.out.println("\nEmployees sorted by ID:");
        for (Employee employee : employees) {
            System.out.println(employee);
        }
        // Sort employees by name
java.util.Arrays.sort(
    employees,
    java.util.Comparator.comparing(Employee::getName)
);

System.out.println("\nEmployees sorted by name:");
for (Employee employee : employees) {
    System.out.println(employee);
}
    }
}
