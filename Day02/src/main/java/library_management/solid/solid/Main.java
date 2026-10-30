package library_management.solid.solid;
public class Main {

    public static void main(String[] args) {

        System.out.println("\n=== SRP: Single Responsibility ===");
        SingleResponsibilityDemo.run();

        System.out.println("\n=== OCP: Open/Closed ===");
        OpenClosedDemo.run();

        System.out.println("\n=== LSP: Liskov Substitution ===");
        LiskovSubstitutionDemo.run();

        System.out.println("\n=== ISP: Interface Segregation ===");
        InterfaceSegregationDemo.run();

        System.out.println("\n=== DIP: Dependency Inversion ===");
        DependencyInversionDemo.run();
    }
}