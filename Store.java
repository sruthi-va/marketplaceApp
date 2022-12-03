import java.io.*;
import java.util.ArrayList;
import java.io.Serializable;

/**
 * This class contains all the methods that directly pertain towards editing the store. This class has the
 * getters, setters, and constructors, which allows other classes to make Store objects, and gain access to
 * the store's seller's name, store's name, and the store's products. It also allows a store to create and
 * delete products, and list all their products.
 *
 * @author Sruthi Vadakuppa
 * @version November 13, 2022
 */
public class Store implements Serializable {
    String sellerName;
    String storeName;
    ArrayList<Product> productList = new ArrayList<>();

    public static void main(String[] args) {
        String sellerName = "seller123";
        String storeName = "store111";
        ArrayList<Product> productList = new ArrayList<>();
        Store newS = new Store(sellerName, storeName, productList);
        String productName = "Milk";
        String productDescription = "A carton of milk. Great for your bones!";
        int quantity = 55;
        double price = 12.99;

        //test for createProduct
        newS.createProduct(productName, productDescription, quantity, price);
        String createProductExpected = "Milk;A carton of milk. Great for your bones!;55;12.99;store111";
        String createProductOutput = newS.getProductList().get(0).getProductName() + ";";
        createProductOutput += newS.getProductList().get(0).getDescription() + ";";
        createProductOutput += newS.getProductList().get(0).getQuantity() + ";";
        createProductOutput += newS.getProductList().get(0).getPrice() + ";";
        createProductOutput += newS.getProductList().get(0).getStoreName();
        System.out.println(createProductExpected.equals(createProductOutput));

        //test for deleteProduct
        newS.createProduct("Butter", "Pasteurized milk!", 43, 7.99);
        newS.deleteProduct("Milk");
        String deleteProductExpected = "Butter;Pasteurized milk!43;7.99;store111";
        String deleteProductOutput = newS.getProductList().get(0).getProductName() + ";";
        deleteProductOutput += newS.getProductList().get(0).getDescription() + ";";
        deleteProductOutput += newS.getProductList().get(0).getQuantity() + ";";
        deleteProductOutput += newS.getProductList().get(0).getPrice() + ";";
        deleteProductOutput += newS.getProductList().get(0).getStoreName();
        System.out.println(deleteProductExpected.equals(deleteProductOutput));

    }
    // constructor for store
    public Store(String sellerName, String storeName, ArrayList<Product> productList) {
        this.sellerName = sellerName;
        this.storeName = storeName;
        this.productList = productList;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public ArrayList<Product> getProductList() {
        return productList;
    }

    public void setProductList(ArrayList<Product> productList) {
        this.productList = productList;
    }

    // creates a new product in the store, based on information received in main method
    public void createProduct(String productName, String description, int quantity, double price) {
        Product newProduct = new Product(productName, description, quantity, price, storeName);
        productList.add(newProduct);
    }

    public void addProduct(Product prod) {
        productList.add(prod);
    }

    // deletes a product given the store in the main method, and the product's name
    public void deleteProduct(String productName) {
        boolean found = false;
        for (int i = 0; i < productList.size(); i++) {
            if (productList.get(i).getProductName().equalsIgnoreCase(productName)) {
                productList.remove(i);
                found = true;
            }
        }
        if (!found) {
            System.out.println("Product not found!");
        }
    }

    //lists all the product sold by the seller in this specific store
    public String[] listAllProducts() {
        ArrayList<String> productArrayList = new ArrayList<String>();
        for (int i = 0; i < productList.size(); i++) {
            if (productList.get(i).getQuantity() > 0) {
                productArrayList.add(productList.get(i).getProductName());
            }
        }
        String[] trueProductList = productArrayList.toArray(new String[0]);
        // for (int i = 0; i < productArrayList.size(); i++) {
        //     trueProductList[0] = productArrayList.get(i);
        // }
        return trueProductList;
    }

    @Override
    public String toString() {
        return String.format("%s by user %s", this.storeName, this.sellerName);
    }
}
