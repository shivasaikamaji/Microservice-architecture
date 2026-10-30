package oopchallenges;

class VehicleBase {

    String brand;
    int speed;

    VehicleBase(String brand, int speed) {
        this.brand = brand;
        this.speed = speed;
    }

    void displayDetails() {
        System.out.println("Brand: " + brand);
        System.out.println("Speed: " + speed + " km/h");
    }
}

class Car extends VehicleBase {

    int numberOfDoors;

    Car(String brand, int speed, int numberOfDoors) {
        super(brand, speed);
        this.numberOfDoors = numberOfDoors;
    }

    void displayCar() {
        displayDetails();
        System.out.println("Doors: " + numberOfDoors);
    }
}

class Bike extends VehicleBase {

    String bikeType;

    Bike(String brand, int speed, String bikeType) {
        super(brand, speed);
        this.bikeType = bikeType;
    }

    void displayBike() {
        displayDetails();
        System.out.println("Bike Type: " + bikeType);
    }
}

public class Vehicle {

    public static void main(String[] args) {

        Car car = new Car("Toyota", 180, 4);
        Bike bike = new Bike("Yamaha", 120, "Sports");

        System.out.println("Car Details");
        car.displayCar();

        System.out.println("----------------");

        System.out.println("Bike Details");
        bike.displayBike();
    }
}