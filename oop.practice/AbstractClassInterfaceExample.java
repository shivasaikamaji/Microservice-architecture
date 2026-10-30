
public class AbstractClassInterfaceExample {

    // Abstract class
    abstract static class Shape {
        abstract void draw();

        void message() {
            System.out.println("Drawing a shape");
        }
    }

    // Interface
    interface Printable {
        void print();
    }

    // Circle extends an abstract class and implements an interface
    static class Circle extends Shape implements Printable {

        @Override
        void draw() {
            System.out.println("Drawing a circle");
        }

        @Override
        public void print() {
            System.out.println("Printing the circle");
        }
    }

    public static void main(String[] args) {
        Circle circle = new Circle();

        circle.message();
        circle.draw();
        circle.print();
    }
}
