package library_management.solid.solid;
public class LiskovSubstitutionDemo {

    static class Bird {
        void eat() {
            System.out.println("Bird is eating.");
        }
    }

    interface Flyable {
        void fly();
    }

    static class Sparrow extends Bird implements Flyable {
        public void fly() {
            System.out.println("Sparrow is flying.");
        }
    }

    static class Penguin extends Bird {
        void swim() {
            System.out.println("Penguin is swimming.");
        }
    }

    static void feedBird(Bird bird) {
        bird.eat();
    }

    public static void run() {
        Bird sparrow = new Sparrow();
        Bird penguin = new Penguin();

        feedBird(sparrow);
        feedBird(penguin);

        ((Flyable) sparrow).fly();
        ((Penguin) penguin).swim();
    }
}