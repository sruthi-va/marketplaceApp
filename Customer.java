import java.io.*;
import java.util.ArrayList;

/**
 * This class contains all the methods for customers.
 * It includes a method to add products into their shopping cart,
 * a method to remove products from their shopping cart,
 * a method to purchase their cart and save their purchase history,
 * and a method to delete their entire account
 *
 * @author Jennifer Wang
 * @version November 14, 2022
 */
public class Customer {
    private String username;
    private String password;
    private ShoppingCart customerCart = new ShoppingCart();
    private ArrayList<String> buyHistory = new ArrayList<>();
    private ArrayList<String> exportBuyHistory = new ArrayList<>();


    //tests
    /*
    public static void main(String[] args) {
        String username = "user1";
        String password = "pandas";
        Product apple = new Product("apple", "fruit", 5, 2, "costco");
        Product potato = new Product("potato", "veggie", 6, 3, "winco");
        Customer newC = new Customer(username, password);

        //test for addToCart method correct
        newC.addToCart(username, apple);
        newC.addToCart(username, potato);
        String addToCartExpected = "apple,potato";
        String addToCartOutput = "";
        Product[] list = newC.customerCart.getProducts(username);
        addToCartOutput = list[0].getProductName();
        for (int i = 1; i < list.length; i++) {
            addToCartOutput = addToCartOutput + "," + list[i].getProductName();
        }

        //test for deleteFromCart method correct
        newC.deleteFromCart(username, apple);
        String deleteFromCartExpected = "potato";
        String deleteFromCartOutput = "";
        Product[] list2 = newC.customerCart.getProducts(username);
        for (int i = 0; i < list2.length; i++) {
            deleteFromCartOutput = list2[i].getProductName();
        }
        System.out.println(addToCartExpected.equals(addToCartOutput));

        // test for purchase cart
        newC.addToCart(username, potato);
        newC.purchaseCart(list2); //clear cart method
        String purchaseCartExpected = "potato";
        String purchaseCartOutput = "";
        Product[] list4 = newC.customerCart.getProducts(username);
        for (int i = 0; i < list4.length; i++) {
            purchaseCartOutput = list4[i].getProductName();
        }
        System.out.println(purchaseCartExpected.equals(purchaseCartOutput));

        //test for deleteFromCart method incorrect
        newC.deleteFromCart(username, apple);
        String deleteFromCartExpected2 = "";
        String deleteFromCartOutput2 = "";
        Product[] list3 = newC.customerCart.getProducts(username);
        for (int i = 0; i < list3.length; i++) {
            deleteFromCartOutput2 = list3[i].getProductName();
        }
        System.out.println(deleteFromCartExpected.equals(deleteFromCartOutput));
        System.out.println(deleteFromCartExpected2.equals(deleteFromCartOutput2));

    }
*/

    //constructor for customer
    public Customer(String username, String password) {
        this.username = username;
        this.password = password;
    }

    //adds an item to the customer's cart
    public String addToCart(String userName, Product addProduct) {
        String output;
        if (addProduct.getQuantity() == 0) {
            output = "Output of stock L";
        } else {
            customerCart.addItem(userName, addProduct);
            output = "Added to cart!";
        }

        return output;
    }

    //deletes an item from the customer's cart
    public void deleteFromCart(String userName, Product deleteProduct) {
        customerCart.removeItem(userName, deleteProduct);
    }

    //purchases customer's shopping cart, and updates their buy history
    public String purchaseCart(Product[] products) {
        String output = "";
        for (int i = 0; i < products.length; i++) {
            if (products[i].getQuantity() < 1) {
                output += String.format("%s is out of stock! The item was left in your cart.\n",
                    products[i].getProductName());
            } else {
                String temp = products[i].getProductName() + "-" + products[i].getStoreName();
                buyHistory.add(temp);
                exportBuyHistory.add(temp);
                customerCart.removeItem(username, products[i]);
                output += products[i].getProductName() + " has been bought!\n";
            }
        }

        //updates customer file
        try {
            String userLine = "";
            ArrayList<String> updateFile = new ArrayList<>();
            File f = new File("customers.txt");
            FileReader fr = new FileReader(f);
            BufferedReader bfr = new BufferedReader(fr);
            String line = bfr.readLine();
            while (line != null) {
                if (line.contains(this.username)) {
                    userLine = line;
                    updateFile.add(line);
                    line = bfr.readLine();
                } else {
                    updateFile.add(line);
                    line = bfr.readLine();
                }
            }

            bfr.close();

            for (int i = 0; i < buyHistory.size(); i++) {
                userLine = userLine + "," + buyHistory.get(i);
            }

            for (int i = 0; i < updateFile.size(); i++) {
                if (updateFile.get(i).contains(this.username)) {
                    updateFile.set(i, userLine);
                }
            }

            FileOutputStream fos = new FileOutputStream("customers.txt", false);
            PrintWriter pw = new PrintWriter(fos);
            for (int i = 0; i < updateFile.size(); i++) {
                pw.println(updateFile.get(i));
            }

            buyHistory.clear();
            pw.close();

        } catch (Exception e) {
            // e.printStackTrace();
        }
        return output;

    }

    //deletes user's account and removes their info from the customer file
    public void deleteAccount(String customerUser) {
        ArrayList<String> updateAccounts = new ArrayList<>();
        try {
            File f = new File("customers.txt");
            FileReader fr = new FileReader(f);
            BufferedReader bfr = new BufferedReader(fr);
            String line = bfr.readLine();
            while (line != null) {
                String[] temp = line.split(",", 0);
                if (!temp[0].equals(customerUser)) {
                    updateAccounts.add(line);
                    line = bfr.readLine();
                } else {
                    line = bfr.readLine();
                }
            }

            bfr.close();
            FileOutputStream fos = new FileOutputStream("customers.txt", false);
            PrintWriter pw = new PrintWriter(fos);
            for (int i = 0; i < updateAccounts.size(); i++) {
                pw.println(updateAccounts.get(i));
            }

            pw.close();

        } catch (Exception e) {
            //e.printStackTrace();
        }

    }

    public boolean customerExportCSV(String fileToWrite) {
        try {
            File f = new File(fileToWrite);
            f.createNewFile();
            FileOutputStream fos = new FileOutputStream(f, false);
            PrintWriter pw = new PrintWriter(fos);

            String userLine = "";
            File file = new File("customers.txt");
            FileReader fr = new FileReader(file);
            BufferedReader bfr = new BufferedReader(fr);
            String line = bfr.readLine();
            while (line != null) {
                if (line.contains(this.username)) {
                    userLine = line;
                    line = bfr.readLine();
                } else {
                    line = bfr.readLine();
                }
            }

            bfr.close();

            String[] customerInfo = userLine.split(",", 0);
            if (customerInfo.length < 2) {
                return false;
            } else {
            String output = customerInfo[2];
            for (int i = 3; i < customerInfo.length; i++) {
                output = output + "," + customerInfo[i];
            }

            pw.println(output);
            pw.close();
            return true;
            }
 
        
        } catch (Exception e) {
            // e.printStackTrace();
            return false;
        }

    }

    /**
     * @return String return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return String return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return ShoppingCart return the customerCart
     */
    public ShoppingCart getCustomerCart() {
        return customerCart;
    }

    /**
     * @param customerCart the customerCart to set
     */
    public void setCustomerCart(ShoppingCart customerCart) {
        this.customerCart = customerCart;
    }

    /**
     * @return ArrayList<Product> return the buyHistory
     */

}