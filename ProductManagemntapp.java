 

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class ProductManagementApp {
    private final ProductController controller = new ProductController();
    private final Scanner scanner = new Scanner(System.in);
  public static void main(String[] args) {
        new ProductManagementApp().run();
    }

    public void run() {
        boolean running = true;
        System.out.println("=============================================");
        System.out.println("  Product Management Application (JDBC)  ");
        System.out.println("=============================================");
        while (running) {
            displayMenu();
            try {
                int choice = scanner.nextInt();
                scanner.nextLine();  
                switch (choice) {
                    case 1: addProduct(); break;
                    case 2: viewAllProducts(); break;
                    case 3: updateProduct(); break;
                    case 4: deleteProduct(); break;
                    case 5: running = false; System.out.println("Exiting application. Goodbye!"); break;
                    default: System.out.println("Invalid choice. Please enter a number from 1 to 5.");
                }
            } catch (InputMismatchException e) {
                System.out.println("\n--- Invalid input. Please enter a valid number for the menu choice. ---\n");
                scanner.nextLine();
            }
            if (running) {
                System.out.println("\nPress ENTER to continue...");
                scanner.nextLine();
            }
        }
        scanner.close();
    }

    private void displayMenu() {
        System.out.println("\n--- Menu ---");
        System.out.println("1. Add New Product");
        System.out.println("2. View All Products");
        System.out.println("3. Update Product Details");
        System.out.println("4. Delete Product Record");
        System.out.println("5. Exit");
        System.out.print("Enter your choice: ");
    }

    private void addProduct() {
        System.out.println("\n--- Add New Product ---");
        System.out.print("Enter Product Name: "); String name = scanner.nextLine();
        System.out.print("Enter Price: "); double price = getValidDouble();
        System.out.print("Enter Quantity: "); int quantity = getValidInt();
        Product newProduct = new Product(name, price, quantity);
        if (controller.addProduct(newProduct)) {
            System.out.println("\n[SUCCESS] Product '" + name + "' added successfully!");
        } else {
            System.out.println("\n[ERROR] Failed to add product. Check database connection/logs.");
        }
    }

    private void viewAllProducts() {
        System.out.println("\n--- All Products ---");
        List<Product> products = controller.getAllProducts();
        if (products.isEmpty()) {
            System.out.println("No products found in the database.");
            return;
        }
        System.out.println("-------------------------------------------------------------------------");
        System.out.printf("| %-4s | %-25s | %-10s | %-5s |\n", "ID", "Name", "Price", "Qty");
        System.out.println("-------------------------------------------------------------------------");
        for (Product product : products) {
            System.out.println(product);
        }
        System.out.println("-------------------------------------------------------------------------");
    }

    private void updateProduct() {
        System.out.println("\n--- Update Product Details ---");
        System.out.print("Enter Product ID to update: ");
        int id = getValidInt();
        
        List<Product> products = controller.getAllProducts();
        Product productToUpdate = products.stream().filter(p -> p.getProductId() == id).findFirst().orElse(null);
        
        if (productToUpdate == null) {
            System.out.println("Product with ID " + id + " not found.");
            return;
        }
        System.out.println("Current Details: " + productToUpdate);
        
        System.out.print("Enter new name (or press Enter to keep): ");
        scanner.nextLine();  
        String newName = scanner.nextLine();
        if (!newName.isEmpty()) productToUpdate.setProductName(newName);
        
        System.out.print("Enter new price (or enter -1 to keep): ");
        double newPrice = getValidDouble();
        if (newPrice != -1) productToUpdate.setPrice(newPrice);
        
        System.out.print("Enter new quantity (or enter -1 to keep): ");
        int newQty = getValidInt();
        if (newQty != -1) productToUpdate.setQuantity(newQty);
        
        if (controller.updateProduct(productToUpdate)) {
            System.out.println("\n[SUCCESS] Product ID " + id + " updated successfully!");
        } else {
            System.out.println("\n[ERROR] Failed to update product. Transaction was rolled back.");
        }
    }
    
    private void deleteProduct() {
        System.out.println("\n--- Delete Product Record ---");
        System.out.print("Enter Product ID to delete: ");
        int id = getValidInt();
        
        if (controller.deleteProduct(id)) {
            System.out.println("\n[SUCCESS] Product ID " + id + " deleted successfully!");
        } else {
            System.out.println("\n[FAILURE] Product with ID " + id + " not found or deletion failed. Transaction was rolled back.");
        }
    }

    private int getValidInt() {
        while (true) {
            try {
                return scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner.nextLine();
            }
        }
    }

    private double getValidDouble() {
        while (true) {
            try {
                return scanner.nextDouble();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner.nextLine();
            }
        }
    }
}