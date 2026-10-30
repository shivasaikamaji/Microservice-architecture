
public class OverloadingOverridingExample {

    // Method overloading: same method name, different parameters
    static class Calculator {

        int add(int a, int b) {
            return a + b;
        }

        int add(int a, int b, int c) {
            return a + b + c;
        }
    }

    // Parent class for method overriding
    static class Animal {
        void sound() {
            System.out.println("Animal makes a sound");
        }
    }

    // Child class overrides the parent's method
    static class Dog extends Animal {

        @Override
        void sound() {
            System.out.println("Dog barks");
        }
    }

    public static void main(String[] args) {

        // Test method overloading
        Calculator calculator = new Calculator();

        System.out.println("Overloading:");
        System.out.println(calculator.add(10, 20));
        System.out.println(calculator.add(10, 20, 30));

        // Test method overriding
        System.out.println("Overriding:");
        Animal animal = new Dog();
        animal.sound();
    }
}
