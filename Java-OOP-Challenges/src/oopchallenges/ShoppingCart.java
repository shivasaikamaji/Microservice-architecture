package oopchallenges;

import java.util.ArrayList;

// Product class
class Product {

    int productId;
    String productName;
    double price;

    Product(int productId, String productName, double price) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
    }

    void displayProduct() {
        System.out.println(
            productId + " | " + productName + " | Rs." + price
        );
    }
}

// Shopping Cart class
public class ShoppingCart {

    ArrayList<Product> cart = new ArrayList<>();

    // Add product
    public void addProduct(Product product) {
        cart.add(product);
        System.out.println(product.productName + " added to cart.");
    }

    // Remove product
    public void removeProduct(int productId) {

        boolean removed = cart.removeIf(
            product -> product.productId == productId
        );

        if (removed) {
            System.out.println("Product removed successfully.");
        } else {
            System.out.println("Product not found.");
        }
    }

    // Display products
    public void displayCart() {

        System.out.println("\nShopping Cart:");

        if (cart.isEmpty()) {
            System.out.println("Cart is empty.");
            return;
        }

        for (Product product : cart) {
            product.displayProduct();
        }
    }

    // Calculate total
    public double calculateTotal() {

        double total = 0;

        for (Product product : cart) {
            total += product.price;
        }

        return total;
    }

    // Main method
    public static void main(String[] args) {

        ShoppingCart shoppingCart = new ShoppingCart();

        Product p1 = new Product(101, "Laptop", 50000);
        Product p2 = new Product(102, "Mouse", 500);
        Product p3 = new Product(103, "Keyboard", 1000);

        shoppingCart.addProduct(p1);
        shoppingCart.addProduct(p2);
        shoppingCart.addProduct(p3);

        shoppingCart.displayCart();

        System.out.println("\nTotal Amount: Rs."
                + shoppingCart.calculateTotal());

        System.out.println("\nRemoving Mouse...");

        shoppingCart.removeProduct(102);

        shoppingCart.displayCart();

        System.out.println("\nUpdated Total: Rs."
                + shoppingCart.calculateTotal());
    }
}