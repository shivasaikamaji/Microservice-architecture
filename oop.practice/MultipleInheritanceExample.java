
public class MultipleInheritanceExample {

    interface Printable {
        void print();
    }

    interface Scannable {
        void scan();
    }

    // One class implements two interfaces
    static class Machine implements Printable, Scannable {

        @Override
        public void print() {
            System.out.println("Printing document");
        }

        @Override
        public void scan() {
            System.out.println("Scanning document");
        }
    }

    public static void main(String[] args) {
        Machine machine = new Machine();

        machine.print();
        machine.scan();
    }
}
