
public class CompositionAggregationExample {

    // A class representing an Engine
    static class Engine {
        void start() {
            System.out.println("Engine started");
        }
    }

    // Composition: Car creates and owns its Engine
    static class Car {
        private final Engine engine = new Engine();

        void startCar() {
            engine.start();
            System.out.println("Car started");
        }
    }

    // A class representing a Teacher
    static class Teacher {
        String name;

        Teacher(String name) {
            this.name = name;
        }
    }

    // Aggregation: Department receives an existing Teacher
    static class Department {
        private final Teacher teacher;

        Department(Teacher teacher) {
            this.teacher = teacher;
        }

        void showTeacher() {
            System.out.println(
                "Department teacher: " + teacher.name
            );
        }
    }

    public static void main(String[] args) {

        System.out.println("Composition:");
        Car car = new Car();
        car.startCar();

        System.out.println("\nAggregation:");
        Teacher teacher = new Teacher("Ravi");
        Department department = new Department(teacher);
        department.showTeacher();
    }
}
