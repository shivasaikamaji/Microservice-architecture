# Java OOP Fundamentals --- Study Notes

## 1. Introduction to Object-Oriented Programming

Object-Oriented Programming (OOP) organizes code around **classes** and
**objects**. It helps make software reusable, organized, maintainable,
and easier to extend.

This document covers the six topics required for Story 1: Java OOP
Fundamentals.

## 2. Classes and Objects

A **class** is a blueprint that defines data (fields) and behavior
(methods). An **object** is an instance of a class.

``` java
class Car {
    String color = "Black";

    void drive() {
        System.out.println("The car is moving");
    }

    public static void main(String[] args) {
        Car car1 = new Car();
        System.out.println(car1.color);
        car1.drive();
    }
}
```

Output:

``` text
Black
The car is moving
```

-   `Car` is the class.
-   `car1` is a reference variable referring to the object.
-   `new Car()` creates an object.
-   `color` is an instance field.
-   `drive()` is an instance method.

Diagram:

``` text
Class: Car
  - color
  - drive()
       |
       | creates
       v
Object: car1
  - color = "Black"
  - can run drive()
```

**Real-world use case:** An online shop might have a `Product` class
with `name` and `price` fields and a `displayDetails()` method. Each
product is an object.

## 3. Constructors and Constructor Chaining

A **constructor** initializes an object when it is created. It has the
same name as its class and no return type.

Example:

``` java
class Student {
    String name;

    Student() {
        name = "Shiva";
    }

    public static void main(String[] args) {
        Student student = new Student();
        System.out.println(student.name);
    }
}
```

Output: `Shiva`

**Constructor chaining** means one constructor calls another
constructor. - `this(...)` calls another constructor in the same
class. - `super(...)` calls a constructor in the parent class. -
`this(...)` or `super(...)` must be the first statement in a
constructor. - If no constructor call is written, Java normally inserts
`super()` when the parent has an accessible no-argument constructor.

Example using `this(...)`:

``` java
class Student {
    String name;
    int age;

    Student() {
        this("Shiva", 22);
    }

    Student(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public static void main(String[] args) {
        Student student = new Student();
        System.out.println(student.name + " " + student.age);
    }
}
```

Output: `Shiva 22`

Example using `super(...)`:

``` java
class Person {
    String name;

    Person(String name) {
        this.name = name;
    }
}

class Student extends Person {
    Student(String name) {
        super(name);
    }

    public static void main(String[] args) {
        Student student = new Student("Shiva");
        System.out.println(student.name);
    }
}
```

Output: `Shiva`

**Real-world use case:** A user object may be created with default
values or with a supplied name and email. Constructor chaining avoids
repeating initialization logic.

## 4. Instance and Static Members

**Instance members** belong to individual objects. **Static members**
belong to the class and are shared at class level.

``` java
class Student {
    String name;                     // Instance field
    static String college = "Indur"; // Static field

    Student(String name) {
        this.name = name;
    }

    public static void main(String[] args) {
        Student s1 = new Student("Shiva");
        Student s2 = new Student("Ravi");

        System.out.println(s1.name);
        System.out.println(s2.name);
        System.out.println(Student.college);
    }
}
```

Output:

``` text
Shiva
Ravi
Indur
```

  -----------------------------------------------------------------------
  Instance member                     Static member
  ----------------------------------- -----------------------------------
  Belongs to an object                Belongs to the class

  Each object can have its own value  A static field is shared by
                                      instances of the class

  Commonly accessed through an object Preferably accessed using the class
                                      name

  Instance methods can directly       Static methods cannot directly
  access instance and static members  access instance members without an
                                      object reference
  -----------------------------------------------------------------------

**Real-world use case:** Each student has an individual name, while
students in the same college can share a common college name.

## 5. Access Modifiers and Encapsulation

Access modifiers control where classes and class members can be
accessed.

  -----------------------------------------------------------------------
  Modifier                            General access
  ----------------------------------- -----------------------------------
  `public`                            Accessible wherever the declaring
                                      type is accessible

  `private`                           Only within the declaring class

  `protected`                         Within the same package and through
                                      subclass access rules

  Default (no modifier)               Within the same package
  -----------------------------------------------------------------------

Top-level classes can generally be `public` or package-private; they
cannot be `private` or `protected`.

**Encapsulation** means keeping data and related behavior together while
controlling direct access to internal state. A common approach is to
make fields `private` and provide methods that validate changes.

``` java
class BankAccount {
    private double balance;

    public BankAccount(double openingBalance) {
        if (openingBalance < 0) {
            throw new IllegalArgumentException("Opening balance cannot be negative");
        }
        balance = openingBalance;
    }

    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit must be positive");
        }
        balance += amount;
    }

    public double getBalance() {
        return balance;
    }
}
```

-   `balance` is private, so outside code cannot change it directly.
-   `deposit()` checks the amount before changing the balance.
-   `getBalance()` provides controlled read access.

**Real-world use case:** Banking software should not allow arbitrary
code to set an account balance to any value. Encapsulation helps
validate operations.

## 6. `this` and `super`

-   `this` refers to the current object. It can distinguish a field from
    a parameter with the same name.
-   `super` accesses the parent-class part of the current object,
    including accessible parent members.

``` java
class Animal {
    String name = "Animal";

    void sound() {
        System.out.println("Animal makes a sound");
    }
}

class Dog extends Animal {
    String name = "Dog";

    void display() {
        System.out.println(this.name);
        System.out.println(super.name);
        super.sound();
    }

    public static void main(String[] args) {
        Dog dog = new Dog();
        dog.display();
    }
}
```

Output:

``` text
Dog
Animal
Animal makes a sound
```

  -----------------------------------------------------------------------
  `this`                              `super`
  ----------------------------------- -----------------------------------
  Refers to the current object        Accesses the parent-class part of
                                      the current object

  Can access current-class members    Can access accessible parent-class
                                      members

  `this(...)` calls another           `super(...)` calls a parent-class
  constructor in the same class       constructor
  -----------------------------------------------------------------------

**Real-world use case:** A `Dog` class can reuse behavior from an
`Animal` parent class while defining its own behavior.

## 7. Packages and Object Lifecycle Basics

### Packages

A **package** groups related Java classes and interfaces. Packages
organize code, support access control, and reduce naming conflicts.

``` java
package com.example.student;

public class Student {
    public void display() {
        System.out.println("Student details");
    }
}
```

The package declaration appears at the beginning of the source file,
before imports and class declarations. Conventionally, the file is
stored in `com/example/student/Student.java`.

### Object lifecycle

1.  **Creation:** An object is created, commonly with `new`.
2.  **Initialization:** Its constructor initializes it.
3.  **Use:** The program uses its fields and methods while it is
    reachable.
4.  **Eligibility for garbage collection:** When the object is no longer
    reachable, it may become eligible for garbage collection.
5.  **Memory reclamation:** The JVM may reclaim its memory.

``` text
Create object with new
          |
          v
Constructor initializes it
          |
          v
Program uses object
          |
          v
Object becomes unreachable
          |
          v
Eligible for garbage collection
          |
          v
JVM may reclaim memory
```

Java does not provide a general manual `delete` operation for ordinary
objects. Garbage collection timing is managed by the JVM and is not
guaranteed to happen immediately.

**Real-world use case:** A service may create a temporary object to
process a request. Once no reachable code references that object, it may
eventually be garbage-collected.

## 8. Quick Comparison of Related Concepts

  -----------------------------------------------------------------------
  Concepts                            Difference
  ----------------------------------- -----------------------------------
  Class vs object                     A class is a blueprint; an object
                                      is an instance of that class.

  Constructor vs method               A constructor initializes a new
                                      object and has no return type; a
                                      method defines behavior and may
                                      return a value.

  Instance vs static                  Instance members belong to objects;
                                      static members belong to the class.

  Encapsulation vs access modifier    Encapsulation is a design
                                      principle; access modifiers help
                                      enforce access boundaries.

  `this` vs `super`                   `this` refers to the current
                                      object; `super` accesses the
                                      parent-class part of the current
                                      object.

  Package vs class                    A class defines a type; a package
                                      groups related types.
  -----------------------------------------------------------------------

## 9. Key Takeaways

-   A class defines fields and methods; an object is an instance of a
    class.
-   Constructors initialize objects; constructor chaining reuses
    initialization logic.
-   Instance members belong to objects; static members belong to the
    class.
-   Access modifiers control visibility, and encapsulation protects
    internal state.
-   `this` refers to the current object; `super` accesses parent-class
    functionality.
-   Packages organize related types.
-   The JVM manages object memory through garbage collection.

## 10. Practice Questions

1.  What is the difference between a class and an object?
2.  Why does a constructor have no return type?
3.  What is the difference between `this()` and `super()`?
4.  How are instance fields different from static fields?
5.  Why should a bank account's balance usually be private?
6.  What is the difference between `this` and `super`?
7.  Why do Java applications use packages?
8.  When does an object become eligible for garbage collection?

## Completion Checklist

-   [ ] Read all six required topics.
-   [ ] Run and understand the Java examples.
-   [ ] Review the diagrams and real-world use cases.
-   [ ] Answer the practice questions.
-   [ ] Save this document as `oop-notes.md` in the project repository.
