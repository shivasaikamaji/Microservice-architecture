
import java.util.ArrayList;
import java.util.List;

final class Student {
    private final String name;
    private final List<String> subjects;

    public Student(String name, List<String> subjects) {
        this.name = name;

        // Defensive copy when receiving the list
        this.subjects = new ArrayList<>(subjects);
    }

    public String getName() {
        return name;
    }

    public List<String> getSubjects() {
        // Defensive copy when returning the list
        return new ArrayList<>(subjects);
    }
}

public class ImmutabilityExample {
    public static void main(String[] args) {
        List<String> subjects = new ArrayList<>();
        subjects.add("Java");
        subjects.add("SQL");

        Student student = new Student("Ravi", subjects);

        // Change the original list
        subjects.add("Python");

        // Change the list returned by the getter
        List<String> copiedSubjects = student.getSubjects();
        copiedSubjects.add("JavaScript");

        System.out.println("Student: " + student.getName());
        System.out.println("Subjects: " + student.getSubjects());
    }
}
