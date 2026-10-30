
public class PolymorphismExample {

    // Parent class
    static class Animal {
        void sound() {
            System.out.println("Animal makes a sound");
        }
    }

    // Child class overrides the parent method
    static class Dog extends Animal {
        @Override
        void sound() {
            System.out.println("Dog barks");
        }
    }

    // Another child class overrides the parent method
    static class Cat extends Animal {
        @Override
        void sound() {
            System.out.println("Cat meows");
        }
    }

    public static void main(String[] args) {

        // Parent reference points to a Dog object
        Animal animal1 = new Dog();

        // Parent reference points to a Cat object
        Animal animal2 = new Cat();

        animal1.sound();
        animal2.sound();
    }
}
