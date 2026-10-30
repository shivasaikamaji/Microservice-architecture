package library_management.solid.solid;
public class InterfaceSegregationDemo {

    interface Printer {
        void print();
    }

    interface Scanner {
        void scan();
    }

    static class SimplePrinter implements Printer {
        public void print() {
            System.out.println("Printing document...");
        }
    }

    static class MultiFunctionPrinter implements Printer, Scanner {
        public void print() {
            System.out.println("Printing document...");
        }

        public void scan() {
            System.out.println("Scanning document...");
        }
    }

    public static void run() {
        Printer simplePrinter = new SimplePrinter();
        simplePrinter.print();

        MultiFunctionPrinter officePrinter =
                new MultiFunctionPrinter();

        officePrinter.print();
        officePrinter.scan();
    }
}