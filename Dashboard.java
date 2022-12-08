import java.io.*;
import java.util.ArrayList;
/**
 * This class has methods that display or sort statistics for the user.
 * Implemented in MarketPlace.
 *
 * Purdue University -- CS18000 -- Fall 2022 -- Proj 4
 *
 * @author Catherine Park, Zander Carpenter, Jennifer Wang, Sruthi Vadakuppa, Vanshika Balaji
 * @version Nov 4, 2022
 */
public class Dashboard {

    /**
     * Reads a file and returns an arraylist of the contents
     */
    public static ArrayList < String > readFile(String filename) {
        ArrayList < String > list = new ArrayList < > ();
        File f = new File(filename);
        try {
            FileReader fr = new FileReader(f);
            BufferedReader bfr = new BufferedReader(fr);
            String line = bfr.readLine();
            while (line != null) {
                list.add(line);
                line = bfr.readLine();
            }
            bfr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Prints out a given arraylist line by line
     */
    public static void printDashboard(ArrayList < String > list) {
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i));
        }
    }

    /**
     * For the seller dashboard: returns an arraylist of customers by number of products
     * when given the store name
     */
    public static ArrayList < String > getSellerDashboard1(String store, String purchasehistoryfile) {
        ArrayList < String > purchasehistory = readFile(purchasehistoryfile);
        ArrayList < String > dashboard = new ArrayList < > ();
        dashboard.add("NUMBER OF ITEMS BOUGHT BY CUSTOMERS AT " + store.toUpperCase());
        for (int i = 0; i < purchasehistory.size(); i++) {
            int quantity = 0;
            String[] splitlist = purchasehistory.get(i).split(",", -1);
            for (int j = 2; j < splitlist.length; j++) {
                String[] productstore = splitlist[j].split("-");
                if (productstore[1].equalsIgnoreCase(store)) {
                    quantity++;
                }
                if (j == splitlist.length - 1 && quantity > 0) {
                    if (quantity == 1) {
                        dashboard.add(splitlist[0] + ": " + quantity + " item bought");
                    } else {
                        dashboard.add(splitlist[0] + ": " + quantity + " items bought");
                    }
                }
            }
        }
        return dashboard;
    }

    /**
     * For the seller dashboard: returns an arraylist of store products by number of sales
     * when given the store name and the seller name
     */
    public static ArrayList < String > getSellerDashboard2(String store, String sellerName, String purchasehistoryfile,
        String sellerinfofile) {
        ArrayList < String > purchasehistory = readFile(purchasehistoryfile);
        ArrayList < String > sellinginfo = readFile(sellerinfofile);
        ArrayList < String > dashboard = new ArrayList < > ();
        dashboard.add("NUMBER OF SALES BY " + store.toUpperCase() + " PRODUCTS");
        for (int i = 0; i < sellinginfo.size(); i++) {
            String[] splitlist = sellinginfo.get(i).split(";", -1);
            if (splitlist[0].equals(sellerName)) {
                for (int j = 1; j < splitlist.length; j++) {
                    String[] storedetails = splitlist[j].split("-", -1);
                    if (storedetails[0].equalsIgnoreCase(store)) {
                        for (int k = 1; k < storedetails.length; k++) {
                            String[] productdetails = storedetails[k].split(",", -1);
                            int quantity = 0;
                            for (int l = 0; l < purchasehistory.size(); l++) {
                                String[] splitcart = purchasehistory.get(l).split(",", -1);
                                for (int m = 1; m < splitcart.length; m++) {
                                    if (splitcart[m].equalsIgnoreCase(productdetails[0] + "-" + store)) {
                                        quantity++;
                                    }
                                    if (l == purchasehistory.size() - 1 && m == splitcart.length - 1) {
                                        dashboard.add(productdetails[0] + ": " + quantity + " sold");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return dashboard;
    }

    /**
     * For the customer dashboard: returns an arraylist of number of products
     * sold by each store
     */
    public static ArrayList < String > getCustomerDashboard1(String purchasehistoryfile, String sellerinfofile) {
        ArrayList < String > sellinginfo = readFile(sellerinfofile);
        ArrayList < String > purchasehistory = readFile(purchasehistoryfile);
        ArrayList < String > dashboard = new ArrayList < > ();
        dashboard.add("NUMBER OF PRODUCTS EACH STORE HAS SOLD");
        for (int i = 0; i < sellinginfo.size(); i++) {
            String[] splitlist = sellinginfo.get(i).split(";", -1);
            for (int j = 1; j < splitlist.length; j++) {
                String[] storedetails = splitlist[j].split("-", -1);
                int quantity = 0;
                for (int k = 0; k < purchasehistory.size(); k++) {
                    String[] splitcart = purchasehistory.get(k).split(",", -1);
                    for (int l = 2; l < splitcart.length; l++) {
                        String[] productstore = splitcart[l].split("-");
                        if (storedetails[0].equalsIgnoreCase(productstore[1])) {
                            quantity++;
                        }
                    }
                }
                if (quantity == 1) {
                    dashboard.add(storedetails[0] + ": " + quantity + " product sold");
                } else {
                    dashboard.add(storedetails[0] + ": " + quantity + " products sold");
                }
            }
        }
        return dashboard;
    }

    /**
     * For the customer dashboard: returns an arraylist of the customer's purchased items
     * by store given the customer name
     */
    public static ArrayList < String > getCustomerDashboard2(String customerName, String purchasehistoryfile) {
        ArrayList < String > purchasehistory = readFile(purchasehistoryfile);
        ArrayList < String > dashboard = new ArrayList < > ();
        dashboard.add("YOUR PURCHASED ITEMS BY STORE");
        for (int i = 0; i < purchasehistory.size(); i++) {
            String[] splitlist = purchasehistory.get(i).split(",", -1);
            if (splitlist[0].equals(customerName)) {
                for (int j = 2; j < splitlist.length; j++) {
                    String[] productstore = splitlist[j].split("-");
                    StringBuilder products = new StringBuilder();
                    boolean bool = true;
                    for (int k = 2; k < j; k++) {
                        String[] productstore2 = splitlist[k].split("-");
                        if (productstore[1].equals(productstore2[1])) {
                            bool = false;
                        }
                    }
                    if (bool) {
                        products.append(productstore[1]);
                        products.append(": ");
                        products.append(productstore[0]);
                        for (int l = j + 1; l < splitlist.length; l++) {
                            bool = true;
                            String[] productstore3 = splitlist[l].split("-");
                            if (productstore[1].equals(productstore3[1])) {
                                for (int m = 2; m < l; m++) {
                                    String[] productstore4 = splitlist[m].split("-");
                                    if (productstore4[0].equals(productstore3[0])) {
                                        //bool = false;
                                    }
                                }
                                if (bool) {
                                    products.append(",");
                                    products.append(productstore3[0]);
                                }
                            }
                        }
                        dashboard.add(products.toString());
                    }
                }
            }
        }
        return dashboard;
    }

    /**
     * Sorts the given arraylist alphabetically by the first character. Also needs
     * a boolean, true sorts from A-Z and false sorts from Z-A
     */
    public static ArrayList < String > sortAlphabetically(ArrayList < String > list, boolean direction) {
        int character = 0;
        boolean again = false;
        if (direction) {
            for (int i = 1; i < list.size(); i++) {
                for (int j = i + 1; j < list.size(); j++) {
                    character = 0;
                    do {
                        again = false;
                        if (((int) list.get(i).toLowerCase().charAt(character) >
                                (int) list.get(j).toLowerCase().charAt(character) &&
                                list.get(i).charAt(character) != ':') || (list.get(j).charAt(character) == ':')) {
                            String temp = list.get(i);
                            list.set(i, list.get(j));
                            list.set(j, temp);
                        } else if ((int) list.get(i).toLowerCase().charAt(character) ==
                            (int) list.get(j).toLowerCase().charAt(character)) {
                            again = true;
                            character++;
                        }
                    } while (again);
                }
            }
        } else {
            for (int i = 1; i < list.size(); i++) {
                for (int j = i + 1; j < list.size(); j++) {
                    character = 0;
                    do {
                        again = false;
                        if (((int) list.get(i).toLowerCase().charAt(character) <
                                (int) list.get(j).toLowerCase().charAt(character) &&
                                list.get(j).charAt(character) != ':') || (list.get(i).charAt(character) == ':')) {
                            String temp = list.get(i);
                            list.set(i, list.get(j));
                            list.set(j, temp);
                        } else if ((int) list.get(i).toLowerCase().charAt(character) ==
                            (int) list.get(j).toLowerCase().charAt(character)) {
                            again = true;
                            character++;
                        }
                    } while (again);
                }
            }
        }
        return list;
    }

    /**
     * Sorts the given arraylist by quantity. Also needs a boolean, true
     * sorts from high-low and false sorts from low-high
     */
    public static ArrayList < String > sortQuantity(ArrayList < String > list, boolean direction) {
        if (direction) {
            for (int i = 1; i < list.size(); i++) {
                if (list.size() > 2) {
                    boolean bool = true;
                    String[] test = list.get(i).split(": ", -1);
                    String[] test2 = test[1].split(" ", -1);
                    try {
                        Integer.parseInt(test2[0]);
                    } catch (Exception e) {
                        bool = false;
                    }
                    for (int j = i + 1; j < list.size(); j++) {
                        String[] spliti = list.get(i).split(": ", -1);
                        String[] spliti2 = spliti[1].split(" ", -1);
                        String[] splitj = list.get(j).split(": ", -1);
                        String[] splitj2 = splitj[1].split(" ", -1);
                        if (bool) {
                            if (Integer.parseInt(spliti2[0]) < Integer.parseInt(splitj2[0])) {
                                String temp = list.get(i);
                                list.set(i, list.get(j));
                                list.set(j, temp);
                            }
                        } else {
                            if (spliti[1].split(",", -1).length < splitj[1].split(",", -1).length) {
                                String temp = list.get(i);
                                list.set(i, list.get(j));
                                list.set(j, temp);
                            }
                        }
                    }
                }
            }
        } else {
            for (int i = 1; i < list.size(); i++) {
                if (list.size() > 2) {
                    boolean bool = true;
                    String[] test = list.get(i).split(": ", -1);
                    String[] test2 = test[1].split(" ", -1);
                    try {
                        Integer.parseInt(test2[0]);
                    } catch (Exception e) {
                        bool = false;
                    }
                    for (int j = i + 1; j < list.size(); j++) {
                        String[] spliti = list.get(i).split(": ", -1);
                        String[] spliti2 = spliti[1].split(" ", -1);
                        String[] splitj = list.get(j).split(": ", -1);
                        String[] splitj2 = splitj[1].split(" ", -1);
                        if (bool) {
                            if (Integer.parseInt(spliti2[0]) > Integer.parseInt(splitj2[0])) {
                                String temp = list.get(i);
                                list.set(i, list.get(j));
                                list.set(j, temp);
                            }
                        } else {
                            if (spliti[1].split(",", -1).length > splitj[1].split(",", -1).length) {
                                String temp = list.get(i);
                                list.set(i, list.get(j));
                                list.set(j, temp);
                            }
                        }
                    }
                }
            }
        }
        return list;
    }

    /**
     * Main method which tests the methods in Dashboard using the
     * sample files testsample1.txt and testsample2.txt
     */
    public static void main(String[] args) {
        // test for getSellerDashboard1 method
        ArrayList < String > seller1Output = getSellerDashboard1("target", "testsample2.txt");
        ArrayList < String > expectedSeller1Output = new ArrayList < > ();
        expectedSeller1Output.add("NUMBER OF ITEMS BOUGHT BY CUSTOMERS AT TARGET");
        expectedSeller1Output.add("username: 2 items bought");
        expectedSeller1Output.add("username2: 1 item bought");
        boolean correct = true;
        for (int i = 0; i < seller1Output.size(); i++) {
            if (!(seller1Output.get(i).equals(expectedSeller1Output.get(i)))) {
                correct = false;
            }
        }
        if (correct) {
            System.out.println("getSellerDashboard1: test passed");
        } else {
            System.out.println("Output:");
            printDashboard(seller1Output);
            System.out.println("Expected:");
            printDashboard(expectedSeller1Output);
        }

        // test for getSellerDashboard2 method
        ArrayList < String > seller2Output = getSellerDashboard2("target", "Seller2",
            "testsample2.txt", "testsample1.txt");
        ArrayList < String > expectedSeller2Output = new ArrayList < > ();
        expectedSeller2Output.add("NUMBER OF SALES BY TARGET PRODUCTS");
        expectedSeller2Output.add("grape: 1 sold");
        expectedSeller2Output.add("orange: 2 sold");
        correct = true;
        for (int i = 0; i < seller2Output.size(); i++) {
            if (!(seller2Output.get(i).equals(expectedSeller2Output.get(i)))) {
                correct = false;
            }
        }
        if (correct) {
            System.out.println("getSellerDashboard2: test passed");
        } else {
            System.out.println("Output:");
            printDashboard(seller2Output);
            System.out.println("Expected:");
            printDashboard(expectedSeller2Output);
        }

        // test for getCustomerDashboard1 method
        ArrayList < String > customer1Output = getCustomerDashboard1("testsample2.txt",
            "testsample1.txt");
        ArrayList < String > expectedCustomer1Output = new ArrayList < > ();
        expectedCustomer1Output.add("NUMBER OF PRODUCTS EACH STORE HAS SOLD");
        expectedCustomer1Output.add("walmart: 2 products sold");
        expectedCustomer1Output.add("aldi: 1 product sold");
        expectedCustomer1Output.add("target: 3 products sold");
        correct = true;
        for (int i = 0; i < customer1Output.size(); i++) {
            if (!(customer1Output.get(i).equals(expectedCustomer1Output.get(i)))) {
                correct = false;
            }
        }
        if (correct) {
            System.out.println("getCustomerDashboard1: test passed");
        } else {
            System.out.println("Output:");
            printDashboard(customer1Output);
            System.out.println("Expected:");
            printDashboard(expectedCustomer1Output);
        }

        // test for getCustomerDashboard2 method
        ArrayList < String > customer2Output = getCustomerDashboard2("username",
            "testsample2.txt");
        ArrayList < String > expectedCustomer2Output = new ArrayList < > ();
        expectedCustomer2Output.add("YOUR PURCHASED ITEMS BY STORE");
        expectedCustomer2Output.add("walmart: apple");
        expectedCustomer2Output.add("target: grape,orange");
        correct = true;
        for (int i = 0; i < customer2Output.size(); i++) {
            if (!(customer2Output.get(i).equals(expectedCustomer2Output.get(i)))) {
                correct = false;
            }
        }
        if (correct) {
            System.out.println("getCustomerDashboard2: test passed");
        } else {
            System.out.println("Output:");
            printDashboard(customer2Output);
            System.out.println("Expected:");
            printDashboard(expectedCustomer2Output);
        }

        // test for sortAlphabetically method
        ArrayList < String > alphaOutput = sortAlphabetically(getCustomerDashboard1("testsample2.txt",
            "testsample1.txt"), true);
        ArrayList < String > expectedAlphaOutput = new ArrayList < > ();
        expectedAlphaOutput.add("PRODUCTS SOLD BY STORE");
        expectedAlphaOutput.add("aldi: 1 product sold");
        expectedAlphaOutput.add("target: 3 products sold");
        expectedAlphaOutput.add("walmart: 2 products sold");
        correct = true;
        for (int i = 0; i < alphaOutput.size(); i++) {
            if (!(alphaOutput.get(i).equals(expectedAlphaOutput.get(i)))) {
                correct = false;
            }
        }
        if (correct) {
            System.out.println("sortAlphabetically: test passed");
        } else {
            System.out.println("Output:");
            printDashboard(alphaOutput);
            System.out.println("Expected:");
            printDashboard(expectedAlphaOutput);
        }

        // test for sortQuantity method
        ArrayList < String > quantityOutput = sortQuantity(getCustomerDashboard1("testsample2.txt",
            "testsample1.txt"), true);
        ArrayList < String > expectedQuantityOutput = new ArrayList < > ();
        expectedQuantityOutput.add("PRODUCTS SOLD BY STORE");
        expectedQuantityOutput.add("target: 3 products sold");
        expectedQuantityOutput.add("walmart: 2 products sold");
        expectedQuantityOutput.add("aldi: 1 product sold");
        correct = true;
        for (int i = 0; i < alphaOutput.size(); i++) {
            if (!(quantityOutput.get(i).equals(expectedQuantityOutput.get(i)))) {
                correct = false;
            }
        }
        if (correct) {
            System.out.println("sortQuantity: test passed");
        } else {
            System.out.println("Output:");
            printDashboard(quantityOutput);
            System.out.println("Expected:");
            printDashboard(expectedQuantityOutput);
        }
    }
}