import java.io.Serializable;

/**
 * This class contains all the methods that directly pertain to products. Getters and setters allow other classes to
 * have access to a product's name, store name, description, quantity, and price. Constructors allow for products to
 * be made in other classes. Methods also allow products to be parsed, and for inputted products to be checked if
 * they are equivalent to other products.
 *
 * @author Sruthi Vadakuppa
 * @version November 13, 2022
 */
public class Product implements Serializable {
    // add to store: add product w/ product name, description, quantity, and price to each store
    private String productName;
    private String description;
    private int quantity;
    private double price;
    private String storeName;

    public static void main(String[] args) {
        String storeName = "Dollar Store Walmart";
        Product newP = new Product("Computer", "Device with a keyboard.", 90, 100.99, storeName);

        // tests for equals method
        System.out.println(newP.equals(new Product("Computer", "Device with a keyboard.", 90, 100.99, storeName)));
    }

    // constructor for product
    public Product(String productName, String description, int quantity, double price, String storeName) {
        this.productName = productName;
        this.description = description;
        this.quantity = quantity;
        this.price = price;
        this.storeName = storeName;
    }

    /**
     * to make it easier to convert strings from shoppingcart to product objects
     * @param toParse in format "product,store,desc"
     */
    public Product(String toParse) {
        String[] cut = toParse.split(",");
        this.productName = cut[0];
        this.description = cut[3];
        this.quantity = 1;
        this.price = Double.parseDouble(cut[2]);
        this.storeName = cut[1];
    }

    /**
     * also to make it easier to make shoppingcart file
     *
     */
    public String writeToString() {
        return String.format("%s,%s,%.2f,%s,", productName, storeName, price, description);
    }

    public String toString() {
        return String.format("%s from store %s for %.2f: %s", productName, storeName, price, description);
    }

    //returns true if given object is equivalent to a product, returns false otherwise
    public boolean equals(Object o) {
        if (o instanceof Product) {
            Product compare = (Product) o;
            if ((this.productName.equals(compare.getProductName()) && this.storeName.equals(compare.getStoreName()))) {
                return true;
            }
        }
        return false;
    }

    public String getProductName() {
        return this.productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return this.price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getStoreName() {
        return this.storeName;
    }


}