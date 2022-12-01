import java.io.*;
import java.lang.reflect.Array;
import java.net.*;
import java.security.InvalidParameterException;
import java.util.*;

/**
 * A program that runs the actual marketplace,
 * where sellers and customers can interact with stores and do certain actions according to their respective roles and
 * permissions.
 * <p>
 * Purdue University -- CS18000 -- Fall 2022 -- Proj 5
 *
 * @author Catherine Park, Zander Carpenter, Jennifer Wang, Sruthi Vadakuppa, Vanshika Balaji
 * @version Nov 4, 2022
 */
public class MarketPlace extends Thread {
    private static ArrayList<Seller> sellers = new ArrayList<>();
    private Socket socket;
    public static Object obj = new Object();

    public void run() {
        BufferedReader reader;
        PrintWriter writer;
        ObjectOutputStream oos;
        ObjectInputStream ois;
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream());
            oos = new ObjectOutputStream(new DataOutputStream(socket.getOutputStream()));
            ois = new ObjectInputStream(new DataInputStream(socket.getInputStream()));

        } catch (IOException e1) {
            e1.printStackTrace();
            return;
        }

        Customer customer = null;
        Seller seller = null;
        int id = 0;
        String line;
        String cOrS = null;
        try {
            cOrS = reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (cOrS.equals("Customer")) {
            id = 1;
        }

        String[] userpass = new String[0];
        try {
            userpass = reader.readLine().split(";;");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (id == 1) {
            while (true) {
                ArrayList<String> usernames = readFile("customers.txt");
                boolean usernameFound = false;
                boolean loggedIn = false;
                for (int i = 0; i < usernames.size(); i++) {
                    String[] splitLine = usernames.get(i).split(",");
                    if (userpass[0].equals(splitLine[0])) {
                        usernameFound = true;
                        if (userpass[1].equals(splitLine[1])) {
                            writer.write("true"); // isValid
                            loggedIn = true;
                        } else {
                            writer.write("false");
                        }
                        writer.println();
                        writer.flush();
                        break;
                    }
                }

                if (!loggedIn) {
                    try {
                        if (reader.readLine().equals("newAccount")) {
                            if (!usernameFound) {
                                synchronized(obj) {
                                    addUserPass("customers.txt", userpass[0], userpass[1]);
                                }
                                customer = new Customer(userpass[0], userpass[1]);
                                writer.write("true");
                                break;
                            } else {
                                writer.write("false");
                            }
                            writer.println();
                            writer.flush();
                        } else {
                            customer = new Customer(userpass[0], userpass[1]);
                            break;
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    customer = new Customer(userpass[0], userpass[1]);
                    break;
                }
            }

            do {
                // this method prints all stores
                homescreen();
                writer.write("1. view store,2. search,3. purchase,4. edit cart,5. view cart,6. view statistics," +
                        "7. delete account,8. logout");
                try {
                    line = reader.readLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                switch (line) {
                    case "1. view store":
                        Store store = null;
                        do {
                            ArrayList<Store> allStores = new ArrayList<>();
                            for (int i = 0; i < sellers.size(); i++) {
                                for (int j = 0; j < sellers.get(i).getStores().size(); j++) {
                                    allStores.add(sellers.get(i).getStores().get(j));
                                }
                            }
                            Store[] allStoresArray = new Store[allStores.size()];
                            for (int i = 0; i < allStores.size(); i++) {
                                allStoresArray[i] = allStores.get(i);
                            }

                            try {
                                oos.writeObject(allStoresArray);
                                oos.flush();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        } while (store == null);
                        boolean valid = false;
                        writer.write(store.listAllProducts() + "");
                        break;
                    case "2. search":
                        HashSet<Object> searchResult;
                        do {
                            System.out.println("Enter the product name you'd like to search:");
                            searchResult = this.search(reader, writer); // this.search(scanner.nextLine());
                        } while (searchResult == null);
                        for (Object o : searchResult) {
                            System.out.println(o.toString());
                        }
                        this.runSearch(scanner, customer, userpass[0]);
                        break;
                    case "3. purchase": //server writes over string returned from purchasecart method
                        Product[] list = customer.getCustomerCart().getProducts(userpass[0]);
                        list = this.updateProductQuantities(list);
                        if (list.length > 0) {
                            customer.purchaseCart(list);
                            this.decrementQuantity(list);
                            System.out.println("Cart has been bought!");
                        } else {
                            System.out.println("Your cart is empty! Go buy some stuff");
                        }
                        break;
                    case "4. edit cart":
                        Product[] cart = customer.getCustomerCart().getProducts(userpass[0]);
                        System.out.println(Arrays.toString(cart));
                        Product item = null;
                        do {
                            System.out.println("Do you want to delete an item? (yes/no)");
                            line = scanner.nextLine();
                            if (line.equals("yes") || line.equals("Yes")) {
                                System.out.println("Which item do you want to delete? Enter a " +
                                        "product name");
                                for (int i = 0; i < cart.length; i++) {
                                    System.out.println(cart[i].getProductName());
                                }
                                line = scanner.nextLine();
                                for (int i = 0; i < cart.length; i++) {
                                    if (cart[i].getProductName().equals(line)) {
                                        item = cart[i];
                                    }
                                }

                                if (item != null) {
                                    customer.deleteFromCart(userpass[0], item);
                                } else {
                                    System.out.println("This item isn't in your cart!!");
                                    break;
                                }

                            } else {
                                System.out.println("Invalid command!");
                                break;
                            }
                        } while (true);
                        break;
                    case "5. view cart":
                        Product[] toPrint = customer.getCustomerCart().getProducts(userpass[0]);
                        if (toPrint.length == 0) {
                            System.out.println("your cart is empty!");
                        } else {
                            System.out.println("Current cart items:");
                            for (int i = 0; i < toPrint.length; i++) {
                                System.out.println(toPrint[i]);
                            }
                        }
                        break;
                    case "6. view statistics":
                        boolean bool = true;
                        ArrayList<String> dashboard = new ArrayList<>();
                        while (bool) {
                            boolean again = true;
                            String dash = null;
                            try {
                                dash = reader.readLine();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            if (dash.equals("Number of products sold by each store")) {
                                dashboard = Dashboard.getCustomerDashboard1("testsample2.txt",
                                        "testsample1.txt");
                                if (dashboard.size() > 1) {
                                    for (int i = 0; i < dashboard.size(); i++) {
                                        writer.write(dashboard.get(i));
                                        writer.println();
                                        writer.flush();
                                    }
                                    writer.write("");
                                    writer.println();
                                    writer.flush();
                                } else {
                                    writer.write("There are no stores");
                                    writer.println();
                                    writer.flush();
                                    writer.write("");
                                    writer.println();
                                    writer.flush();
                                    again = false;
                                }
                            } else if (dash.equals("Your purchased items by store")) {
                                dashboard = Dashboard.getCustomerDashboard2("username",
                                        "testsample2.txt");
                                if (dashboard.size() > 1) {
                                    for (int i = 0; i < dashboard.size(); i++) {
                                        writer.write(dashboard.get(i));
                                        writer.println();
                                        writer.flush();
                                    }
                                    writer.write("");
                                    writer.println();
                                    writer.flush();
                                } else {
                                    writer.write("You haven't bought anything...");
                                    writer.println();
                                    writer.flush();
                                    writer.write("");
                                    writer.println();
                                    writer.flush();
                                    again = false;
                                }
                            } else {
                                writer.write("");
                                writer.println();
                                writer.flush();
                                again = false;
                                bool = false;
                            }
                            while (again) {
                                String sort = null;
                                try {
                                    sort = reader.readLine();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                if (sort.equals("Alphabetically(A-Z)")) {
                                    dashboard = Dashboard.sortAlphabetically(dashboard, true);
                                } else if (sort.equals("Alphabetically(Z-A)")) {
                                    dashboard = Dashboard.sortAlphabetically(dashboard, false);
                                } else if (sort.equals("Quantity(high-low)")) {
                                    dashboard = Dashboard.sortQuantity(dashboard, true);
                                } else if (sort.equals("Quantity(low-high)")) {
                                    dashboard = Dashboard.sortQuantity(dashboard, false);
                                } else {
                                    again = false;
                                    writer.write("false");
                                    writer.println();
                                    writer.flush();
                                    writer.write("");
                                    writer.println();
                                    writer.flush();
                                }
                                if (again) {
                                    for (int i = 0; i < dashboard.size(); i++) {
                                        writer.write(dashboard.get(i));
                                        writer.println();
                                        writer.flush();
                                    }
                                    writer.write("");
                                    writer.println();
                                    writer.flush();
                                }
                            }
                        }
                        break;
                    case "7. delete account":
                        System.out.println("Are you sure? (yes or no)");
                        if (scanner.nextLine().contains("yes")) {
                            customer.deleteAccount(userpass[0]);
                            System.out.println("Account deleted, ejected, and rejected. Goodbye");
                        } else {
                            break;
                        }
                        return;
                    case "8. logout":
                        customer.getCustomerCart().writeFile();
                        System.out.println("Goodbye!");
                        writeFile();
                        return;
                    default:
                        System.out.println("Enter a valid command :{");
                } // here
            } while (true);
        } else if (id == 2) {
            int sellerID = -1;

            while (true) {
                ArrayList<String> usernames = readFile("sellers.txt");
                boolean usernameFound = false;
                boolean loggedIn = false;
                for (int i = 0; i < usernames.size(); i++) {
                    String[] splitLine = usernames.get(i).split(",");
                    if (userpass[0].equals(splitLine[0])) {
                        usernameFound = true;
                        if (userpass[1].equals(splitLine[1])) {
                            writer.write("true"); // isValid
                            loggedIn = true;
                        } else {
                            writer.write("false");
                        }
                        writer.println();
                        writer.flush();
                        break;
                    }
                }

                if (!loggedIn) {
                    try {
                        if (reader.readLine().equals("newAccount")) {
                            if (!usernameFound) {
                                synchronized(obj) {
                                    addUserPass("sellers.txt", userpass[0], userpass[1]);
                                    sellers.add(seller);
                                    sellerID = sellers.indexOf(seller);
                                }
                                System.out.println("New account created!");
                                writer.write("true");
                                break;
                            } else {
                                writer.write("false");
                            }
                            writer.println();
                            writer.flush();
                        } else {
                            seller = new Seller(new ArrayList<>(), userpass[0]);
                            break;
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    for (Seller s : sellers) {
                        if (userpass[0].equals(s.getSellerName())) {
                            seller = s;
                            sellerID = sellers.indexOf(s);
                        }
                    }
                    if (sellerID == -1) {
                        synchronized(obj) {
                            sellers.add(seller);
                        }
                        sellerID = sellers.indexOf(seller);
                    }
                }
            }

            do {
                writer.write("1. list your stores,2. edit stores,3. view sales,4. create store,5. " +
                        "view statistics,6. delete a store,7. import stores from a CSV,8. export stores as a CSV,9. " +
                        "delete account,10. log out?");
                try {
                    line = reader.readLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                switch (line) {
                    case "1. list your stores":
                        int k = 1;
                        if (seller.getStores().size() == 0) {
                            writer.write("no stores");
                            writer.println();
                            writer.flush();
                        } else {
                            writer.write("has stores");
                            writer.println();
                            writer.flush();
                            try {
                                oos.writeObject(seller.getStores());
                                oos.flush();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        break;
                    case "2. edit stores":
                        boolean continuing = false;
                        if (sellers.get(sellerID).getStores().size() == 0) {
                            System.out.println("you have no stores!");
                            writer.write("no stores");
                            writer.println();
                            writer.flush();
                        } else {
                            writer.write("has stores");
                            writer.println();
                            writer.flush();
                            do {
                                try {
                                    oos.writeObject(sellers.get(sellerID).getStores());
                                    oos.flush();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }

                                Store currentStore = null;
                                try {
                                    currentStore = (Store) ois.readObject();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                } catch (ClassNotFoundException e) {
                                    throw new RuntimeException(e);
                                }
                                int currentStoreID = seller.getStores().indexOf(currentStore);

                                boolean valid = false;
                                while (!valid) {
                                    String todo = null;
                                    try {
                                        todo = reader.readLine();
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                    if (todo.equalsIgnoreCase("create")) {
                                        Product toAdd = null;
                                        try {
                                            toAdd = (Product) ois.readObject();
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        } catch (ClassNotFoundException e) {
                                            throw new RuntimeException(e);
                                        }
                                        currentStore.addProduct(toAdd);
                                        seller.setStore(currentStoreID, currentStore);
                                        synchronized(obj) {
                                            sellers.set(sellerID, seller);
                                        }
                                    } else if (todo.equalsIgnoreCase("edit")) {
                                        valid = true;
                                        try {
                                            oos.writeObject(currentStore.getProductList());
                                            Product toEdit = (Product) ois.readObject();
                                            int currProductIndex = Integer.parseInt(reader.readLine());
                                            ArrayList<Product> products = currentStore.getProductList();
                                            products.set(currProductIndex, toEdit);
                                            currentStore.setProductList(products);
                                            seller.setStore(currentStoreID, currentStore);
                                            synchronized(obj) {
                                                sellers.set(sellerID, seller);
                                            }
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        } catch (ClassNotFoundException e) {
                                            throw new RuntimeException(e);
                                        }
                                    } else if (todo.equalsIgnoreCase("delete")) {
                                        valid = true;
                                        try {
                                            oos.writeObject(currentStore.getProductList());
                                            int currProductIndex = Integer.parseInt(reader.readLine());
                                            ArrayList<Product> products = currentStore.getProductList();
                                            products.remove(currProductIndex);
                                            currentStore.setProductList(products);
                                            seller.setStore(currentStoreID, currentStore);
                                            synchronized(obj) {
                                                sellers.set(sellerID, seller);
                                            }
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                    } else {
                                        System.out.println("Please type 'create', 'edit', or 'delete'.");
                                    }
                                }
                                // System.out.println("Would you like to do something else?");
                                // if (scanner.nextLine().toLowerCase().startsWith("y")) {
                                //     continuing = true;
                                // } else {
                                //     continuing = false;
                                // }
                            } while (continuing);
                        }
                        break;
                    case "3. view sales":
                        if (sellers.get(sellerID).getStores().size() == 0) {
                            System.out.println("you have no stores!");
                            writer.write("no stores");
                            writer.println();
                            writer.flush();
                        } else {
                            writer.write("has stores");
                            writer.println();
                            writer.flush();
                            try {
                                System.out.println("Type a store name to see it's sales, or 'all' to see all of your " +
                                        "store sales");
                                String storeName = reader.readLine();
                                writer.write(seller.viewSales(storeName));
                                writer.println();
                                writer.flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    case "4. create store":
                        String storeName = reader.readLine();
                        seller.createStore(seller.getSellerName(), storeName);
                        synchronized(obj) {
                            sellers.set(sellerID, seller);
                        }
                        break;
                    case "5.view statistics":
                        boolean bool = true;
                        ArrayList<String> dashboard = new ArrayList<>();
                        while (bool) {
                            boolean again = true;
                            String dash = null;
                            try {
                                dash = reader.readLine();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            if (dash.equals("Number of products bought by each customer at a specific store")) {
                                String store = null;
                                try {
                                    store = reader.readLine();
                                } catch (IOException e){
                                    throw new RuntimeException(e);
                                }
                                ArrayList<String> marketplaceinfo = Dashboard.readFile("testsample1.txt");
                                boolean status = false;
                                for (int i = 0; i < marketplaceinfo.size(); i++) {
                                    String[] sellerinfo = marketplaceinfo.get(i).split(";");
                                    if (sellerinfo[0].equals(seller.getSellerName())) {
                                        for (int j = 1; j < sellerinfo.length; j++) {
                                            String[] storeinfo = sellerinfo[j].split("-");
                                            if (storeinfo[0].equalsIgnoreCase(store)) {
                                                status = true;
                                            }
                                        }
                                    }
                                }
                                if (status) {
                                    writer.write("true");
                                    writer.println();
                                    writer.flush();
                                    dashboard = Dashboard.getSellerDashboard1(store, "testsample2.txt");
                                    if (dashboard.size() > 1) {
                                        for (int i = 0; i < dashboard.size(); i++) {
                                            writer.write(dashboard.get(i));
                                            writer.println();
                                            writer.flush();
                                        }
                                        writer.write("");
                                        writer.println();
                                        writer.flush();
                                    } else {
                                        writer.write("No one has bought anything...");
                                        writer.println();
                                        writer.flush();
                                        writer.write("");
                                        writer.println();
                                        writer.flush();
                                        again = false;
                                    }
                                } else {
                                    writer.write("false");
                                    writer.println();
                                    writer.flush();
                                    again = false;
                                }
                            } else if (dash.equals("Number of items sold for each product at a specific store")) {
                                String store = null;
                                try {
                                    store = reader.readLine();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                ArrayList<String> marketplaceinfo = Dashboard.readFile("testsample1.txt");
                                boolean status = false;
                                for (int i = 0; i < marketplaceinfo.size(); i++) {
                                    String[] sellerinfo = marketplaceinfo.get(i).split(";");
                                    if (sellerinfo[0].equals(seller.getSellerName())) {
                                        for (int j = 1; j < sellerinfo.length; j++) {
                                            String[] storeinfo = sellerinfo[j].split("-");
                                            if (storeinfo[0].equalsIgnoreCase(store)) {
                                                status = true;
                                            }
                                        }
                                    }
                                }
                                if (status) {
                                    writer.write("true");
                                    writer.println();
                                    writer.flush();
                                    dashboard = Dashboard.getSellerDashboard2(store, seller.getSellerName(), "testsample2.txt", "testsample1.txt");
                                    if (dashboard.size() > 1) {
                                        for (int i = 0; i < dashboard.size(); i++) {
                                            writer.write(dashboard.get(i));
                                            writer.println();
                                            writer.flush();
                                        }
                                        writer.write("");
                                        writer.println();
                                        writer.flush();
                                    } else {
                                        writer.write("This store has no products...");
                                        writer.println();
                                        writer.flush();
                                        writer.write("");
                                        writer.println();
                                        writer.flush();
                                        again = false;
                                    }
                                } else {
                                    writer.write("false");
                                    writer.println();
                                    writer.flush();
                                    again = false;
                                }
                            } else {
                                writer.write("");
                                writer.println();
                                writer.flush();
                                again = false;
                                bool = false;
                            }
                            while (again) {
                                String sort = null;
                                try {
                                    sort = reader.readLine();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                if (sort.equals("Alphabetically(A-Z)")) {
                                    dashboard = Dashboard.sortAlphabetically(dashboard, true);
                                } else if (sort.equals("Alphabetically(Z-A)")) {
                                    dashboard = Dashboard.sortAlphabetically(dashboard, false);
                                } else if (sort.equals("Quantity(high-low)")) {
                                    dashboard = Dashboard.sortQuantity(dashboard, true);
                                } else if (sort.equals("Quantity(low-high)")) {
                                    dashboard = Dashboard.sortQuantity(dashboard, false);
                                } else {
                                    again = false;
                                    writer.write("false");
                                    writer.println();
                                    writer.flush();
                                    writer.write("");
                                    writer.println();
                                    writer.flush();
                                }
                                if (again) {
                                    for (int i = 0; i < dashboard.size(); i++) {
                                        writer.write(dashboard.get(i));
                                        writer.println();
                                        writer.flush();
                                    }
                                    writer.write("");
                                    writer.println();
                                    writer.flush();
                                }
                            }
                        }
                        break;
                    case "6. delete a store":
                        if (sellers.get(sellerID).getStores().size() == 0) {
                            System.out.println("you have no stores!");
                            writer.write("no stores");
                            writer.println();
                            writer.flush();
                        } else {
                            writer.write("has stores");
                            writer.println();
                            writer.flush();
                            String deleteStore = reader.readLine();
                            Store storeInQuestion = null;
                            ArrayList<Store> currStores = sellers.get(sellerID).getStores();
                            for (int i = 0; i < currStores.size(); i++) {
                                if (currStores.get(i).getStoreName().equals(deleteStore)) {
                                    storeInQuestion = currStores.get(i);
                                    break;
                                }
                            }
                            if (storeInQuestion == null) {
                                writer.write("not store");
                                writer.println();
                                writer.flush();
                                break;
                            }
                            writer.write("is store");
                            writer.println();
                            writer.flush();
                        
                            currStores.remove(storeInQuestion);
                            seller.setStores(currStores);
                            synchronized(obj) {
                                sellers.set(sellerID, seller);
                            }
                            System.out.println("Store deleted from marketplace");
                        }
                        break;
                    case "7. import stores from a CSV":
                        String fileImport = reader.readLine();
                        ArrayList<Store> importedStores;
                        try {
                            importedStores = seller.importCSV(fileImport);
                        } catch (Exception e) {
                            writer.write("error");
                            writer.println();
                            writer.flush();
                            break;
                        } 
                        writer.write("no error");
                        writer.println();
                        writer.flush();
                        
                        for (int i = 0; i < importedStores.size(); i++) {
                            seller.addStore(importedStores.get(i));
                        }
                        synchronized(obj) {
                            sellers.set(sellerID, seller);
                        }
                        System.out.println("Imported!");
                        break;
                    case "8. export stores as a CSV":
                        System.out.println("Enter the name of the file you want your stores to be exported to.");
                        if (seller.exportCSV(reader.readLine())) {
                            System.out.println("Exported!");
                        } else {
                            System.out.println("there was a problem!");
                        }
                        break;
                    case "9.delete account":
                        synchronized(obj) {
                            sellers.remove(sellerID); // TODO does this delete all their stores from the file? it
                                                        // should right
                        }                               // yeah it won't write this seller's stuff to the file
                                                        // but their history will still exist
                                                        // that's probably fine right
                                           
                        System.out.println("Account deleted, stores ejected, rejected and taken care of. Goodbye.");
                        return;
                    case "10. log out":
                        synchronized(obj) {
                            sellers.set(sellerID, seller);
                        }
                        System.out.println("Goodbye!");
                        MarketPlace.writeFile();
                        return;
                    default:
                        System.out.println("Please enter a valid command");
                        break;
                }
            } while (true);

        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(6969);
        System.out.println("waiting for users to connect");
        while (true) {
            Socket socket = serverSocket.accept();
            MarketPlace server = new MarketPlace(socket);
            new Thread(server).start();
        }
    }

    /**
     * Constructor; runs parseFile to parse the file
     *
     * @return none
     * @param, none
     */
    public MarketPlace(Socket socket) {
        this.socket = socket;
        parseFile();
    }

    /**
     * Displays all the stores in the marketplace; is the homescreen of the customer
     *
     * @return none
     * @param, none
     */
    public static void homescreen() {
        int i = 1;
        for (Seller s : sellers) {
            for (Store st : s.getStores()) {
                System.out.println(i + ": " + st.toString());
                i++;
            }
        }

        if (i == 1) {
            System.out.println("The Market is quiet for once. No life breathes");
        }
    }

    public static Store getStoreIndex(int index) {
        int i = 1;
        for (Seller s : sellers) {
            for (Store st : s.getStores()) {
                if (index == i) {
                    return st;
                }
                i++;
            }
        }

        if (i == 1) {
            System.out.println("The Market is quiet for once. No life breathes");
        }
        return null;
    }

    /**
     * Parses file by splitting the line in the marketplace file into their respective products, stores, and sellers
     *
     * @return none
     * @param, none
     */
    public void parseFile() {
        ArrayList<Seller> thisSellers = new ArrayList<Seller>();
        try (BufferedReader br = new BufferedReader(new FileReader(new File("marketplace.txt")))) {
            String line = br.readLine();
            while (line != null) {
                String[] ownerAndStores = line.split(";");
                ArrayList<Store> thisStores = new ArrayList<Store>();
                for (int i = 1; i < ownerAndStores.length; i++) {
                    String[] storesAndProducts = ownerAndStores[i].split("-");
                    ArrayList<Product> thisProducts = new ArrayList<Product>();
                    for (int j = 1; j < storesAndProducts.length; j++) {
                        String[] productsAndDesc = storesAndProducts[j].split(",");

                        if (productsAndDesc.length != 5) {
                            //System.out.print(productsAndDesc.length);
                            System.out.println("Product format is not right! something is missing!");
                        }
                        // System.out.println(storesAndProducts[j]);
                        // System.out.println(Arrays.toString(productsAndDesc));
                        thisProducts.add(new Product(productsAndDesc[0], productsAndDesc[1],
                                Integer.parseInt(productsAndDesc[2]), Double.parseDouble(productsAndDesc[3]),
                                productsAndDesc[4]));
                    }
                    thisStores.add(new Store(ownerAndStores[0], storesAndProducts[0], thisProducts));
                }
                sellers.add(new Seller(thisStores, ownerAndStores[0]));
                line = br.readLine();
            }
        } catch (Exception e) {
            System.out.println("File has problems!");
            e.printStackTrace();
        }
    }

    /**
     * Returns an object array of the search results aka all the products that match the description
     *
     * @param, keyword A String word that the customer inputs to search for
     * @return An HashSet type Object
     */
    public Product search(BufferedReader reader, PrintWriter writer, ObjectOutputStream oos, ObjectInputStream ois) {
        Product curr;
        try {
            // ObjectOutputStream oos = new ObjectOutputStream(new DataOutputStream(socket.getOutputStream()));
            String keyword = reader.readLine();
            HashSet<Object> searchResult = new HashSet<Object>();
            for (Seller seller : sellers) {
                for (Store store : seller.getStores()) {
                    for (Product product : store.getProductList()) {
                        if (product.getProductName().toLowerCase().contains(keyword.toLowerCase())
                                || product.getDescription().toLowerCase().equalsIgnoreCase(keyword.toLowerCase())) {
                            searchResult.add(product);
                        }
                    }
                }
            }
            oos.writeObject(searchResult);
            oos.flush();
            keyword = reader.readLine(); // get index
            for (Object p : searchResult) {
                curr = (Product) p;
                if (curr.getProductName().equals(keyword)) {
                    oos.writeObject(curr); // send product
                    oos.flush();
                    break;
                }
            }
            curr = (Product) ois.readObject();
            return curr;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Returns an ArrayList of strings that contains the lines from the file
     *
     * @return An ArrayList of Strings
     * @param, fileName A string of the file to be read
     */
    private static ArrayList<String> readFile(String filename) {
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

        return tempList;
    }

    /**
     * Adds a new user to the file containing all users of the type seller/customer
     *
     * @param user String of the user's username
     * @param pass String of the user's password
     * @param, fileName A String of the file to add the user's info to
     */
    private static void addUserPass(String filename, String user, String pass) {
        try {
            File f = new File(filename);
            FileOutputStream fos = new FileOutputStream(f, true);
            PrintWriter pw = new PrintWriter(fos);

            pw.println(user + "," + pass);

            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes to the marketplace file of their respective products, stores, and sellers
     *
     * @return none
     * @param, none
     */
    public static void writeFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File("marketplace.txt"),
                false))) {
            for (Seller seller : sellers) {
                String lineString = "";
                lineString += seller.getSellerName();
                for (Store store : seller.getStores()) {
                    lineString += ";" + store.getStoreName();
                    for (Product product : store.getProductList()) {
                        lineString += "-" + product.getProductName() +
                                "," + product.getDescription() +
                                "," + product.getQuantity() +
                                "," + product.getPrice() +
                                "," + store.getStoreName();
                    }
                }
                bw.write(lineString);
                bw.newLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void decrementQuantity(Product[] products) {
        for (int j = 0; j < products.length; j++) {
            int x = 0;
            int y = 0;
            int z = 0;
            Seller currSeller;
            Store currStore;
            for (x = 0; x < sellers.size(); x++) {
                currSeller = sellers.get(x);
                for (y = 0; y < currSeller.getStores().size(); y++) {
                    currStore = currSeller.getStores().get(y);
                    for (z = 0; z < currStore.getProductList().size(); z++) {
                        ArrayList<Product> currProductList = currStore.getProductList();
                        if (currProductList.get(z).equals(products[j])) {
                            currProductList.remove(currStore.getProductList().get(z));
                            currProductList.add(z, new Product(products[j].getProductName(),
                                    products[j].getDescription(),
                                    products[j].getQuantity() - 1, products[j].getPrice(),
                                    products[j].getStoreName()));
                            currStore.setProductList(currProductList);
                        }
                    }
                    currSeller.setStore(y, currStore);
                }
                sellers.remove(x);
                sellers.add(x, currSeller);

            }
        }
    }

    /*
    public void updateProducts(Product updatedProduct) {
        for (int j = 0; j < 1; j++) {
            for (int i = 0; i < this.products.size(); i++) {
                if (this.products.get(i).equals(updatedProduct)) {
                    this.products.remove(i);
                    this.products.add(i, updatedProduct);
                }
            }

            int x = 0;
            int y = 0;
            int z = 0;
            Seller currSeller;
            Store currStore;
            for (x = 0; x < sellers.size(); x++) {
                currSeller = sellers.get(x);
                for (y = 0; y < currSeller.getStores().size(); y++) {
                    currStore = currSeller.getStores().get(y);
                    for (z = 0; z < currStore.getProductList().size(); z++) {
                        ArrayList<Product> currProductList = currStore.getProductList();
                        if (currProductList.get(z).equals(updatedProduct)) {
                            currProductList.remove(currStore.getProductList().get(z));
                            currProductList.add(z, updatedProduct);
                            currStore.setProductList(currProductList);
                        }
                    }
                    currSeller.setStore(y, currStore);
                }
                sellers.remove(x);
                sellers.add(x, currSeller);

            }
        }
    }*/

    public Product[] updateProductQuantities(Product[] products) {
        Product[] outputArray = new Product[products.length];
        ArrayList<Product> output = new ArrayList<>();
        for (int j = 0; j < products.length; j++) {
            int x = 0;
            int y = 0;
            int z = 0;
            Seller currSeller;
            Store currStore;
            for (x = 0; x < sellers.size(); x++) {
                currSeller = sellers.get(x);
                for (y = 0; y < currSeller.getStores().size(); y++) {
                    currStore = currSeller.getStores().get(y);
                    for (z = 0; z < currStore.getProductList().size(); z++) {
                        ArrayList<Product> currProductList = currStore.getProductList();
                        if (currProductList.get(z).equals(products[j])) {
                            output.add(currProductList.get(z));
                        }
                    }
                }
            }
        }

        for (int i = 0; i < output.size(); i++) {
            outputArray[i] = output.get(i);
        }

        return outputArray;
    }

    /*
    public void updateSeller(Seller seller) {
        Seller slay = null;
        for (Seller s : sellers) {
            if (s.getSellerName().equals(seller.getSellerName())) {
                slay = s;
                break;
            }
        }
        if (slay == null) {
            sellers.add(seller);
        } else {
            sellers.remove(slay);
            sellers.add(seller);
        }
    }*/
}