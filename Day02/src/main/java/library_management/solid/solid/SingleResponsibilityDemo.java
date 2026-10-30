package library_management.solid.solid;
public class SingleResponsibilityDemo {

    static class Invoice {
        private final double amount;

        Invoice(double amount) {
            this.amount = amount;
        }

        double calculateTotal() {
            return amount + (amount * 0.18);
        }
    }

    static class InvoicePrinter {
        void print(double total) {
            System.out.println("Invoice total: Rs. " + total);
        }
    }

    public static void run() {
        Invoice invoice = new Invoice(1000);
        InvoicePrinter printer = new InvoicePrinter();

        double total = invoice.calculateTotal();
        printer.print(total);
    }
}