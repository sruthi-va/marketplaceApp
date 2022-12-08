import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Manages the customer's shopping cart and reads/writes everything to a file
 *
 * Purdue University -- CS18000 -- Fall 2022 -- Proj 4
 *
 * @author Catherine Park, Jennifer Wang, Zander Carpenter, Vanshika Balaji, Sruthi Vadakuppa
 * @version Nov 13, 2022
 */
public class ShoppingCart {
    private ArrayList<ArrayList<Object>> cart = new ArrayList<>();

    // testing
    public static void main(String[] args) {
        ShoppingCart cart = new ShoppingCart();
        String emptyListOutput = Arrays.toString(cart.getProducts("someoneRandom"));
        String emptyListExpected = "[]";
        System.out.println(emptyListOutput);
        System.out.println(emptyListExpected);
        System.out.println(emptyListExpected.equals(emptyListOutput));

        Product testProduct = new Product("testProduct", "testDesc", 1, 0.50, "testStore");
        cart.addItem("someoneRandom", testProduct);
        cart.addItem("someoneRandom", new Product("thing,someStore,4.5,description"));
        String addCartOutput = Arrays.toString(cart.getProducts("someoneRandom"));
        String addCartExpected = "[testProduct from store testStore for 0.50: testDesc," +
                "thing from store someStore for 4.50: description]";
        System.out.println(addCartOutput);
        System.out.println(addCartExpected);
        System.out.println(addCartExpected.equals(addCartOutput));

        cart.removeItem("someoneRandom", new Product("testProduct,testStore,0.50,testDesc"));
        String removeCartOutput = Arrays.toString(cart.getProducts("someoneRandom"));
        String removeCartExpected = "[thing from store someStore for 4.50: description]";
        System.out.println(removeCartOutput);
        System.out.println(removeCartExpected);
        System.out.println(removeCartExpected.equals(removeCartOutput));

        cart.deleteUser("someoneRandom");
        String deleteOutput = Arrays.toString(cart.getProducts("someoneRandom"));
        String deleteExpected = "[]";
        System.out.println(deleteOutput);
        System.out.println(deleteExpected);
        System.out.println(deleteExpected.equals(deleteOutput));
    }

    public ShoppingCart() {
        ArrayList<String> tempList = new ArrayList<>();

        FileReader fr;
        BufferedReader bfr;
        File f = new File("shoppingcart.txt");

        String line;

        try {
            fr = new FileReader(f);
            bfr = new BufferedReader(fr);

            line = bfr.readLine();
            while (line != null) {
                tempList.add(line);
                line = bfr.readLine();
            }
            bfr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < tempList.size(); i++) {
            ArrayList<Object> temp = new ArrayList<>();
            String[] t = tempList.get(i).split(",");
            temp.add(t[0]);
            for (int j = 1; j < t.length - 3; j += 4) {
                String[] product = {
                        t[j],
                        t[j + 1],
                        t[j + 2],
                        t[j + 3]
                };
                temp.add(new Product(String.join(",", product)));
            }

            this.cart.add(temp);
        }
    }

    public ArrayList<ArrayList<Object>> getAllCarts() {
        return this.cart;
    }

    /**
     * applicable for both if item was bought or removed
     * @param username of customer
     * @param product to remove
     * @return true if the first item found was successfully found and removed
     */
    public boolean removeItem(String username, Product product) {
        int index = findUsername(username);
        if (index != -1) {
            ArrayList<Object> user = cart.get(index);
            for (int i = 1; i < user.size(); i++) {
                Product curr = (Product) user.get(i);
                if (curr.equals(product)) {
                    user.remove(i);
                    cart.remove(index);
                    cart.add(index, user);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * adds item to shopping cart
     * @param username of customer
     * @param product to add
     */
    public void addItem(String username, Product product) {
        int index = findUsername(username);
        if (index != -1) {
            ArrayList<Object> user = cart.get(index);
            user.add(product);
            cart.remove(index);
            cart.add(index, user);
        } else {
            ArrayList<Object> user = new ArrayList<>();
            user.add(username);
            user.add(product);
            cart.add(user);
        }
    }

    /**
     * @return true if user was successfully found and removed
     */
    public boolean deleteUser(String username) {
        int index = findUsername(username);
        if (index != -1) {
            cart.remove(index);
            return true;
        }
        return false;
    }

    /**
     * @param username of customer
     * @return array of products in their cart
     */
    public Product[] getProducts(String username) {
        int index = findUsername(username);
        if (index == -1) {
            return new Product[0];
        } else {
            ArrayList<Object> user = cart.get(index);
            Product[] product = new Product[user.size() - 1];
            for (int i = 1; i < user.size(); i++) {
                product[i - 1] = (Product) user.get(i);
            }
            return product;
        }
    }

    /**
     * writes customer array to file
     * call this as customer leaves/logs out
     */
    public void writeFile() {
        try {
            File f = new File("shoppingcart.txt");
            FileOutputStream fos = new FileOutputStream(f, false);
            PrintWriter pw = new PrintWriter(fos);

            for (int i = 0; i < this.cart.size(); i++) {
                ArrayList<Object> curr = this.cart.get(i);
                pw.print((String) curr.get(0) + ",");
                for (int j = 1; j < curr.size(); j++) {
                    pw.print(((Product) curr.get(j)).writeToString());
                }
                pw.print("\n");
            }

            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int findUsername(String username) {
        int output = -1;
        for (int i = 0; i < cart.size(); i++) {
            if (cart.get(i).get(0).equals(username)) {
                output = i;
                break;
            }
        }
        return output;
    }
}