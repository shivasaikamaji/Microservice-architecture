package library_management.solid.solid;
public class OpenClosedDemo {

    interface Discount {
        double apply(double price);
    }

    static class NoDiscount implements Discount {
        public double apply(double price) {
            return price;
        }
    }

    static class FestivalDiscount implements Discount {
        public double apply(double price) {
            return price * 0.80;
        }
    }

    static class StudentDiscount implements Discount {
        public double apply(double price) {
            return price * 0.90;
        }
    }

    static class BillingService {
        double calculate(double price, Discount discount) {
            return discount.apply(price);
        }
    }

    public static void run() {
        BillingService billing = new BillingService();

        System.out.println("No discount: Rs. "
                + billing.calculate(1000, new NoDiscount()));

        System.out.println("Festival discount: Rs. "
                + billing.calculate(1000, new FestivalDiscount()));

        System.out.println("Student discount: Rs. "
                + billing.calculate(1000, new StudentDiscount()));
    }
}