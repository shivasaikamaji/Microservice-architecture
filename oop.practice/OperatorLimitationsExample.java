
public class OperatorLimitationsExample {
    public static void main(String[] args) {
        int a = 10;
        int b = 20;

        // Operators work with primitive values
        System.out.println("Addition: " + (a + b));
        System.out.println("Equality: " + (a == b));

        // Use methods to compare objects
        String name1 = new String("Java");
        String name2 = new String("Java");

        System.out.println("Same content: " + name1.equals(name2));
        System.out.println("Same reference: " + (name1 == name2));
    }
}
