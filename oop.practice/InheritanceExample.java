
public class InheritanceExample {

    // Parent class
    static class Animal {
        void eat() {
            System.out.println("Animal eats food");
        }
    }

    // Single inheritance: Dog extends Animal
    static class Dog extends Animal {
        void bark() {
            System.out.println("Dog barks");
        }
    }

    // Multilevel inheritance: Puppy extends Dog, Dog extends Animal
    static class Puppy extends Dog {
        void weep() {
            System.out.println("Puppy weeps");
        }
    }

    // Hierarchical inheritance: Cat also extends Animal
    static class Cat extends Animal {
        void meow() {
            System.out.println("Cat meows");
        }
    }

    public static void main(String[] args) {
        System.out.println("Single Inheritance:");
        Dog dog = new Dog();
        dog.eat();
        dog.bark();

        System.out.println("\nMultilevel Inheritance:");
        Puppy puppy = new Puppy();
        puppy.eat();
        puppy.bark();
        puppy.weep();

        System.out.println("\nHierarchical Inheritance:");
        Cat cat = new Cat();
        cat.eat();
        cat.meow();
    }
}
