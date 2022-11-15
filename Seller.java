import java.io.*;
import java.util.ArrayList;

/**
 * This class contains all the methods that directly pertain towards helping the seller. This class contains all
 * constructors, getters and setters to allow other classes access to a seller's name and stores. This class also
 * allows sellers to view all sales they have made, or from any specific store, add or delete a store, list all the
 * stores the seller has, update the marketplace based on new seller information, and import and export CSV files.
 *
 * @author Sruthi Vadakuppa
 * @version November 13, 2022
 */
public class Seller {
    private ArrayList<Store> stores;
    private String sellerName;

    //seller tests
    public static void main(String[] args) throws IOException {
        String sellerName = "seller123";
        ArrayList<Store> myStores = new ArrayList<>();
        Seller newS = new Seller(myStores, sellerName);
        String storeName = "Grocery Store";
        ArrayList<Product> myProducts = new ArrayList<>();
        myProducts.add(new Product("Apple", "A red, yummy fruit", 50, 1.99, "Grocery Store"));
        Store testStore = new Store(sellerName, storeName, myProducts);

        //test for createStore method
        newS.createStore(sellerName, storeName);
        String createStoreExpected = "The seller's name is seller123, and the store's name is Grocery Store.";
        String createStoreOutput = "";
        createStoreOutput += "The seller's name is ";
        createStoreOutput += newS.stores.get(0).getSellerName();
        createStoreOutput += ", and the store's name is ";
        createStoreOutput += newS.stores.get(0).getStoreName();
        createStoreOutput += ".";
        System.out.println(createStoreExpected.equals(createStoreOutput));

        //test for addStore method
        newS.addStore(testStore);
        String addStoreExpected = "Apple;A red, yummy fruit;50;1.99;Grocery Store";
        String addStoreOutput = (testStore.getProductList().get(0).getProductName() + ";");
        addStoreOutput += (testStore.getProductList().get(0).getDescription() + ";");
        addStoreOutput += (testStore.getProductList().get(0).getQuantity() + ";");
        addStoreOutput += (testStore.getProductList().get(0).getPrice() + ";");
        addStoreOutput += (testStore.getProductList().get(0).getStoreName());
        System.out.println(addStoreExpected.equals(addStoreOutput));
    }

    // constructor for seller
    public Seller(ArrayList<Store> stores, String sellerName) {
        this.stores = stores;
        this.sellerName = sellerName;
    }

    //view all the products sold by this store, or all of the seller's stores
    public void viewSales(String storeName) throws IOException {
        try {
            if (storeName.equalsIgnoreCase("all")) {
                BufferedReader br = new BufferedReader(new FileReader("purchasehistory.txt"));
                int bought = 0;
                String line;
                String customer;
                String tempItem;
                String tempStore;
                int tempQuantity;
                String item = "";
                String store = "";
                int quantity = 0;

                while ((line = br.readLine()) != null) {
                    for (int index = 0; index < stores.size(); index++) {
                        storeName = stores.get(index).getStoreName().toLowerCase();
                        customer = line.substring(0, line.indexOf(","));
                        if (line.contains(storeName)) {
                            line = line.substring(line.indexOf(",") + 1);
                            tempItem = line.substring(0, line.indexOf(","));
                            line = line.substring(line.indexOf(",") + 1);
                            tempStore = line.substring(0, line.indexOf(","));
                            line = line.substring(line.indexOf(",") + 1);
                            if (!(line.contains(","))) {
                                tempQuantity = Integer.valueOf(line.substring(0));
                            } else {
                                tempQuantity = Integer.valueOf(line.substring(0, line.indexOf(",")));
                            }
                            if (tempStore.equalsIgnoreCase(storeName)) {
                                item = tempItem;
                                store = tempStore;
                                quantity = tempQuantity;
                            }
                        }
                        double price = 0.0;
                        for (int i = 0; i < stores.size(); i++) {
                            if (stores.get(i).getStoreName().equalsIgnoreCase(store)) {
                                for (int j = 0; j < stores.get(i).getProductList().size(); i++) {
                                    if (stores.get(i).getProductList().get(j).getProductName().equalsIgnoreCase(item)) {
                                        price = stores.get(i).getProductList().get(j).getPrice();
                                    }
                                }
                            }
                        }
                        if (quantity > 0) {
                            System.out.printf("%s bought %s %d time(s), you made %f from this sale.\n", customer, item, quantity,
                                    (quantity*price));
                            bought++;
                        }
                    }



                }
                if (bought == 0) {
                    System.out.println("No customers have bought your products yet. :(");
                }
                br.close();
            } else {
                storeName = storeName.toLowerCase();
                BufferedReader br = new BufferedReader(new FileReader("purchasehistory.txt"));
                String line;
                String customer;
                String tempItem;
                String tempStore;
                int tempQuantity;
                String item = "";
                String store = "";
                int quantity = 0;
                int bought = 0;

                while ((line = br.readLine()) != null) {
                    customer = line.substring(0, line.indexOf(","));
                    if (line.contains(storeName)) {
                        line = line.substring(line.indexOf(",") + 1);
                        tempItem = line.substring(0, line.indexOf(","));
                        line = line.substring(line.indexOf(",") + 1);
                        tempStore = line.substring(0, line.indexOf(","));
                        line = line.substring(line.indexOf(",") + 1);
                        try {
                            tempQuantity = Integer.valueOf(line.substring(0, line.indexOf(",")));
                        } catch (StringIndexOutOfBoundsException siobe) {
                            tempQuantity = Integer.valueOf(line.substring(0));
                        }
                        if (tempStore.equalsIgnoreCase(storeName)) {
                            item = tempItem;
                            store = tempStore;
                            quantity = tempQuantity;
                        }
                        double price = 0.0;
                        for (int i = 0; i < stores.size(); i++) {
                            if (stores.get(i).getStoreName().equalsIgnoreCase(store)) {
                                for (int j = 0; j < stores.get(i).getProductList().size(); i++) {
                                    if (stores.get(i).getProductList().get(j).getProductName().equalsIgnoreCase(item)) {
                                        price = stores.get(i).getProductList().get(j).getPrice();
                                    }
                                }
                            }
                        }
                        if (quantity > 0) {
                            System.out.printf("%s bought %s %d time(s), you made %f from this sale.\n", customer, item, quantity,
                                    (quantity*price));
                            bought++;
                        }
                    }
                }
                if (bought == 0) {
                    System.out.println("No customers have bought your products yet. :(");
                }
                br.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Problem reading all customer's purchase history!");
        }
    }

    // create a new store, given a seller's name and store name
    public Store createStore(String sellerName, String storeName) {
        Store newStore = new Store(sellerName, storeName, new ArrayList<Product>());
        stores.add(newStore);
        return newStore;
    }

    public void addStore(Store storeToAdd) {
        stores.add(storeToAdd);
    }

    public ArrayList<Store> getStores() {
        return stores;
    }

    public void setStores(ArrayList<Store> stores) {
        this.stores = stores;
    }

    public void setStore(int index, Store store) {
        this.stores.remove(index);
        this.stores.add(index, store);
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    // list all the stores owned by the seller
    public void listAllStores() {
        if (stores.size() > 0) {
            System.out.println(sellerName + "'s Stores:");
            for (int i = 0; i < stores.size(); i++) {
                System.out.println((i+1) + ". " + stores.get(i).getStoreName());
            }
        } else {
            System.out.println("This seller has no stores!");
        }

    }

    /**
     * imports
     * @param filename
     * @return
     */
    // imports csv file and parses store information from it
    public ArrayList<Store> importCSV(String filename) {
        ArrayList<Store> output = new ArrayList<>();
        ArrayList<String> tempList = new ArrayList<>();

        FileReader fr;
        BufferedReader bfr;
        File f = new File(filename);

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

        try {
            for (int j = 0; j < tempList.size(); j++) {
                ArrayList<Product> products = new ArrayList<>();
                String[] thisLine = tempList.get(j).split(",");
                for (int i = 1; i < thisLine.length - 2; i += 3) {
                    products.add(new Product(thisLine[i], thisLine[i + 2], 1, Double.parseDouble(thisLine[i + 1]), thisLine[0]));
                }
                output.add(new Store(this.sellerName, thisLine[0], products));
            }
        } catch (Exception e) {
            System.out.println("Invalid file formatting!");
        }

        return output;
    }

    // exports a csv file based on the store information for the seller
    public boolean exportCSV(String fileToWrite) {
        try {
            File f = new File(fileToWrite);
            f.createNewFile();
            FileOutputStream fos = new FileOutputStream(f, false);
            PrintWriter pw = new PrintWriter(fos);

            pw.println(this.sellerName);

            for (int i = 0; i < stores.size(); i++) {
                ArrayList<Product> products = this.stores.get(i).getProductList();
                pw.print(this.stores.get(i).getStoreName() + ",");
                for (int j = 0; j < products.size(); j++) {
                    pw.printf("%s,%f,%s,", products.get(i).getProductName(), products.get(i).getPrice(), products.get(i).getDescription());
                }
                pw.println();
            }

            pw.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public String toString() {
        return String.format("The seller %s owns these stores:%s", this.sellerName, stores.toString());
    }


}
