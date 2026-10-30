package oopchallenges;

// Abstract parent class
abstract class Shape {

    abstract double calculateArea();
}

// Circle child class
class Circle extends Shape {

    double radius;

    Circle(double radius) {
        this.radius = radius;
    }

    @Override
    double calculateArea() {
        return Math.PI * radius * radius;
    }
}

// Rectangle child class
class Rectangle extends Shape {

    double length;
    double width;

    Rectangle(double length, double width) {
        this.length = length;
        this.width = width;
    }

    @Override
    double calculateArea() {
        return length * width;
    }
}

// Main class
public class ShapeAreaCalculator {

    public static void main(String[] args) {

        Shape circle = new Circle(5);

        Shape rectangle = new Rectangle(10, 5);

        System.out.println("Circle Area: "
                + circle.calculateArea());

        System.out.println("Rectangle Area: "
                + rectangle.calculateArea());
    }
}