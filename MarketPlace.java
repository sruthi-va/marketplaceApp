import java.io.*;
import java.net.*;
import java.util.*;

/**
 * A program that runs the actual marketplace,
 * where sellers and customers can interact with stores and do certain actions
 * according to their respective roles and
 * permissions.
 * 
 * Purdue University -- CS18000 -- Fall 2022 -- Proj 5
 *
 * @author Catherine Park, Zander Carpenter, Jennifer Wang, Sruthi Vadakuppa,
 *         Vanshika Balaji
 * @version Nov 4, 2022
 */
public class MarketPlace implements Runnable {
    private static ArrayList<Seller> sellers = new ArrayList<>();
    private static boolean sellersInitialized = false;
    private Socket socket;
    private static Object obj = new Object();
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    public void run() {
        String welcome = null;
        try {
            oos = new ObjectOutputStream(new DataOutputStream(socket.getOutputStream()));
            ois = new ObjectInputStream(new DataInputStream(socket.getInputStream()));
            welcome = (String) ois.readObject();
        } catch (Exception e) {
            // throw new RuntimeException(e);
        }
        if (welcome.equals("exit")) {
            return;
        }

        Customer customer = null;
        Seller seller = null;
        int id = 2;
        String line = "";
        String cOrS = null;
        try {
            cOrS = (String) ois.readObject();
            System.out.println(cOrS);
        } catch (Exception e) {
            // e.printStackTrace();
        }

        if (cOrS.equals("Customer")) {
            id = 1;
        } else if (cOrS.equals(null)) {
            return;
        }

        String[] userpass = new String[0];

        if (id == 1) {
            while (true) {
                System.out.println("Starting loop");
                try {
                    userpass = ((String) ois.readObject()).split(";;");
                    System.out.println("from server: " + userpass[0] + " " + userpass[1]);
                } catch (Exception e) {
                    // throw new RuntimeException(e);
                }

                ArrayList<String> usernames = readFile("customers.txt");
                boolean usernameFound = false;
                boolean loggedIn = false;
                for (int i = 0; i < usernames.size(); i++) {
                    String[] splitLine = usernames.get(i).split(",");
                    if (userpass[0].equals(splitLine[0])) {
                        usernameFound = true;
                        if (userpass[1].equals(splitLine[1])) {
                            writeAndFlush("true", oos); // isValid
                            loggedIn = true;
                        } else {
                            writeAndFlush("false", oos);
                        }
                        break;
                    }
                }

                if (!usernameFound) {
                    writeAndFlush("false", oos);
                }

                if (!loggedIn) {
                    try {
                        String input = (String) ois.readObject();
                        if (input.equals("newAccount")) {
                            System.out.println("recieved new account");
                            if (!usernameFound) {
                                System.out.println("username doesn't already exist");
                                synchronized (obj) {
                                    addUserPass("customers.txt", userpass[0], userpass[1]);
                                }
                                customer = new Customer(userpass[0], userpass[1]);
                                writeAndFlush("true", oos);
                                System.out.println("sent true"); // TEST
                                break;
                            } else {
                                writeAndFlush("false", oos);
                                System.out.println("sent false"); // TEST
                            }
                        } else if (input.equals("tryAgain")) {
                            continue;
                        } else {
                            customer = new Customer(userpass[0], userpass[1]);
                            break;
                        }
                    } catch (Exception e) {
                        // throw new RuntimeException(e);
                    }
                } else {
                    customer = new Customer(userpass[0], userpass[1]);
                    break;
                }
            }

            writeAndFlush("1. view store,2. search,3. purchase,4. edit cart,5. view cart," +
                    "6. view statistics,7. export buy history to csv file,8. delete account,9. logout", oos);
            System.out.println("here");
            do {
                // removeSellerDuplicates();
                try {
                    line = (String) ois.readObject();
                } catch (Exception e3) {
                    // e3.printStackTrace();
                    return;
                }
                System.out.println("from server: " + line);
                switch (line) {
                    case "1. view store":
                        Store store = null; // where is this used?
                        ArrayList<Store> allStores = new ArrayList<>();
                        for (Seller s: sellers) {
                            allStores.addAll(s.getStores());
                        }
                        String[] allStoresArray = new String[allStores.size()];
                        for (int i = 0; i < allStores.size(); i++) {
                            allStoresArray[i] = allStores.get(i).getStoreName();
                        }
                        try {
                            writeAndFlush(allStoresArray, oos);
                            if (allStoresArray.length == 0) {
                                System.out.println("no stores");
                            } else {
                                // receive which store to view
                                line = (String) ois.readObject();
                                Store chosen = new Store("", "", null);
                                for (Store st: allStores) {
                                    if (st.getStoreName().equals(line)) {
                                        chosen = st;
                                        writeAndFlush(st.listAllProducts(), oos);
                                        break;
                                    }
                                }

                                if (chosen.getProductList().size() == 0) {
                                    System.out.println("no stores");
                                } else {
                                    // receive product name
                                    line = (String) ois.readObject();
                                    // find the obejct
                                    for (Product p: chosen.getProductList()) {
                                        if (p.getProductName().equals(line)) {
                                            oos.writeObject(p);
                                            break;
                                        }
                                    }
                                    whatToDoWithProductReply(customer, ois);
                                }
                            }
                        } catch (Exception e) {
                            // e.printStackTrace();
                        }
                        break;
                    case "2. search":
                        search(oos, ois, customer);
                        break;
                    case "3. purchase": // server writes over string returned from purchasecart method
                        Product[] list = customer.getCustomerCart().getProducts(userpass[0]);
                        // list = this.updateProductQuantities(list);
                        if (list.length > 0) {
                            writeAndFlush("has stuff", oos);
                            String output = customer.purchaseCart(list);
                            writeAndFlush(output, oos);
                            this.decrementQuantity(list);
                        } else {
                            writeAndFlush("no stuff", oos);
                        }
                        break;
                    case "4. edit cart":
                        Product[] cart = customer.getCustomerCart().getProducts(userpass[0]);
                        if (cart.length <= 0) {
                            writeAndFlush(null, oos);
                        } else {
                            try {
                                writeAndFlush(cart, oos);
                            } catch (Exception e2) {
                                // e2.printStackTrace();
                            }
                        }

                        Product item = null;
                        try {
                            item = (Product) ois.readObject();
                        } catch (Exception e1) {
                            // e1.printStackTrace();
                        }
                        if (item != null) {
                            customer.deleteFromCart(customer.getUsername(), item);
                        }
                        break;
                    case "5. view cart":
                        Product[] toPrint = customer.getCustomerCart().getProducts(customer.getUsername());
                        System.out.println(Arrays.toString(toPrint));
                        String cartString = "";
                        for (Product pr: toPrint) {
                            cartString += pr.toString();
                            cartString += ";;";
                        }
                        System.out.println(cartString); // TEST
                        writeAndFlush(cartString, oos);
                        break;
                    case "6. view statistics":
                        boolean bool = true;
                        ArrayList<String> dashboard = new ArrayList<>();
                        while (bool) {
                            boolean again = true;
                            String dash = null;
                            try {
                                dash = (String) ois.readObject();
                            } catch (Exception e) {
                                // throw new RuntimeException(e);
                            }
                            if (dash.equals("Number of products sold by each store")) {
                                dashboard = Dashboard.getCustomerDashboard1("customers.txt",
                                        "marketplace.txt");
                                if (dashboard.size() > 1) {
                                    for (int i = 0; i < dashboard.size(); i++) {
                                        writeAndFlush(dashboard.get(i), oos);
                                    }
                                    writeAndFlush("", oos);
                                } else {
                                    writeAndFlush("There are no stores", oos);
                                    writeAndFlush("", oos);
                                    again = false;
                                }
                            } else if (dash.equals("Your purchased items by store")) {
                                dashboard = Dashboard.getCustomerDashboard2(customer.getUsername(),
                                        "customers.txt");
                                if (dashboard.size() > 1) {
                                    for (int i = 0; i < dashboard.size(); i++) {
                                        writeAndFlush(dashboard.get(i), oos);
                                    }
                                    writeAndFlush("", oos);
                                } else {
                                    writeAndFlush("You haven't bought anything...", oos);
                                    writeAndFlush("", oos);
                                    again = false;
                                }
                            } else {
                                writeAndFlush("", oos);
                                again = false;
                                bool = false;
                            }
                            while (again) {
                                String sort = null;
                                try {
                                    sort = (String) ois.readObject();
                                } catch (Exception e) {
                                    // throw new RuntimeException(e);
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
                                    writeAndFlush("false", oos);
                                    writeAndFlush("", oos);
                                }
                                if (again) {
                                    for (int i = 0; i < dashboard.size(); i++) {
                                        writeAndFlush(dashboard.get(i), oos);
                                    }
                                    writeAndFlush("", oos);
                                }
                            }
                        }
                        break;
                    case "7. export buy history to csv file":
                        try {
                            if (customer.customerExportCSV((String) ois.readObject())) {
                                writeAndFlush("Exported!", oos);
                            } else {
                                writeAndFlush("There was a problem!", oos);
                            }
                        } catch (Exception e) {
                            // e.printStackTrace();
                        }
                        break;
                    case "8. delete account":
                        customer.deleteAccount(customer.getUsername());
                        return;
                    case "9. logout":
                        MarketPlace.writeFile();
                        return;
                    default:
                        return;
                }
                // refreshStream(ois, oos);
            } while (true);
        } else if (id == 2) {
            int sellerID = -1;

            while (true) {
                System.out.println("Starting loop");
                try {
                    userpass = ((String) ois.readObject()).split(";;");
                    System.out.println("from client: " + userpass[0] + " " + userpass[1]);
                } catch (Exception e) {
                    // throw new RuntimeException(e);
                }
                // removeSellerDuplicates();
                ArrayList<String> usernames = readFile("sellers.txt");
                boolean usernameFound = false;
                boolean loggedIn = false;
                for (int i = 0; i < usernames.size(); i++) {
                    String[] splitLine = usernames.get(i).split(",");
                    if (userpass[0].equals(splitLine[0])) {
                        usernameFound = true;
                        if (userpass[1].equals(splitLine[1])) {
                            writeAndFlush("true", oos); // isValid
                            loggedIn = true;
                        } else {
                            writeAndFlush("false", oos);
                        }
                        break;
                    }
                }

                if (!usernameFound) {
                    writeAndFlush("false", oos);
                }

                if (!loggedIn) {
                    try {
                        String input = (String) ois.readObject();
                        if (input.equals("newAccount")) {
                            System.out.println("recieved new account");
                            if (!usernameFound) {
                                System.out.println("username doesn't already exist");
                                synchronized (obj) {
                                    addUserPass("sellers.txt", userpass[0], userpass[1]);
                                    seller = new Seller(new ArrayList<>(), userpass[0]);
                                    sellers.add(seller);
                                    sellerID = sellers.indexOf(seller);
                                }
                                writeAndFlush("true", oos);
                                System.out.println("sent true");
                                break;
                            } else {
                                writeAndFlush("false", oos);
                                System.out.println("sent false");
                            }
                        } else if (input.equals("tryAgain")) {
                            continue;
                        } else {
                            seller = new Seller(new ArrayList<>(), userpass[0]);
                            sellers.add(seller);
                            sellerID = sellers.indexOf(seller);
                            break;
                        }
                    } catch (Exception e) {
                        // throw new RuntimeException(e);
                    }
                } else {
                    seller = new Seller(new ArrayList<>(), userpass[0]);
                    boolean found = false;
                    for (int i = 0; i < sellers.size(); i++) {
                        if (seller.getSellerName().equals(sellers.get(i).getSellerName())) {
                            seller = sellers.get(i);
                            sellerID = i;
                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        sellers.add(seller);
                        sellerID = sellers.indexOf(seller);
                    }
                    break;
                }
                // refreshStream(ois, oos);
            }

            ShoppingCart cart = new ShoppingCart();

            writeAndFlush("1. list your stores,2. edit stores,3. view sales,4. create store,5. " +
                    "view statistics,6. delete a store,7. view customer shopping carts,8. import stores from a CSV," +
                    "9. export stores as a CSV,10. delete account,11. log out", oos);

            do {
                try {
                    line = (String) ois.readObject();
                } catch (Exception e2) {
                    // e2.printStackTrace();
                }
                System.out.println(line);
                switch (line) {
                    case "1. list your stores":
                        if (seller.getStores().size() == 0) {
                            writeAndFlush("no stores", oos);
                            System.out.println("no stores??");
                        } else {
                            writeAndFlush("has stores", oos);
                            System.out.println("has stores??");
                            try {
                                writeAndFlush(seller.getStores(), oos);
                                System.out.println("sent stores");
                            } catch (Exception e) {
                                // throw new RuntimeException(e);
                            }
                        }
                        break;
                    case "2. edit stores":
                        if (sellers.get(sellerID).getStores().size() == 0) {
                            writeAndFlush("no stores", oos);
                        } else {
                            writeAndFlush("has stores", oos);
                            try {
                                writeAndFlush(sellers.get(sellerID).getStores(), oos);
                            } catch (Exception e) {
                                // throw new RuntimeException(e);
                            }

                            Store currentStore = null;
                            try {
                                currentStore = (Store) ois.readObject();
                                System.out.println(currentStore);
                            } catch (Exception e) {
                                // throw new RuntimeException(e);
                            }
                            System.out.println(Arrays.toString(seller.getStores().toArray()));

                            int currentStoreID = -1;
                            for (int i = 0; i < seller.getStores().size(); i++) {
                                if (seller.getStores().get(i).toString().equals(currentStore.toString())) {
                                    currentStoreID = i;
                                }
                                System.out
                                        .println(seller.getStores().get(i).toString().equals(currentStore.toString()));
                            }

                            System.out.println(currentStoreID);

                            String todo = null;
                            try {
                                todo = (String) ois.readObject();
                                System.out.println(todo);
                            } catch (Exception e) {
                                // throw new RuntimeException(e);
                            }
                            System.out.println("started if statements");
                            if (todo.equalsIgnoreCase("create product")) {
                                Product toAdd = null;
                                try {
                                    toAdd = (Product) ois.readObject();
                                    System.out.println("read" + toAdd.toString());
                                } catch (Exception e) {
                                    // throw new RuntimeException(e);
                                }
                                currentStore.addProduct(toAdd);
                                seller.setStore(currentStoreID, currentStore);
                                synchronized (obj) {
                                    sellers.set(sellerID, seller);
                                }
                            } else if (todo.equalsIgnoreCase("edit product")) {
                                try {
                                    oos.writeObject(currentStore.getProductList());
                                    Product toEdit = (Product) ois.readObject();
                                    System.out.println("read ois " + toEdit.toString());
                                    int currProductIndex = (Integer) ois.readObject();
                                    System.out.println("read reader " + currProductIndex);
                                    ArrayList<Product> products = currentStore.getProductList();
                                    products.set(currProductIndex, toEdit);
                                    currentStore.setProductList(products);
                                    seller.setStore(currentStoreID, currentStore);
                                    synchronized (obj) {
                                        sellers.set(sellerID, seller);
                                    }
                                } catch (Exception e) {
                                    // throw new RuntimeException(e);
                                }
                            } else if (todo.equalsIgnoreCase("delete product")) {
                                try {
                                    oos.writeObject(currentStore.getProductList());
                                    int currProductIndex = (Integer) ois.readObject();
                                    ArrayList<Product> products = currentStore.getProductList();
                                    products.remove(currProductIndex);
                                    currentStore.setProductList(products);
                                    seller.setStore(currentStoreID, currentStore);
                                    synchronized (obj) {
                                        sellers.set(sellerID, seller);
                                    }
                                } catch (Exception e) {
                                    // throw new RuntimeException(e);
                                }
                            } else {
                                System.out.println("Please type 'create', 'edit', or 'delete'.");
                            }
                        }
                        break;
                    case "3. view sales":
                        if (sellers.get(sellerID).getStores().size() == 0) {
                            System.out.println("you have no stores!");
                            writeAndFlush("no stores", oos);
                        } else {
                            writeAndFlush("has stores", oos);
                            try {
                                System.out.println("Type a store name to see it's sales, or 'all' to see all of your " +
                                        "store sales");
                                String storeName = (String) ois.readObject();
                                writeAndFlush(seller.viewSales(storeName), oos);
                            } catch (Exception e) {
                                // e.printStackTrace();
                            }
                        }
                        break;
                    case "4. create store":
                        String storeName = "";
                        try {
                            storeName = (String) ois.readObject();
                        } catch (Exception e1) {
                            // e1.printStackTrace();
                        }
                        seller.createStore(seller.getSellerName(), storeName);
                        synchronized (obj) {
                            sellers.set(sellerID, seller);
                        }
                        writeFile();
                        break;
                    case "5. view statistics":
                        boolean bool = true;
                        ArrayList<String> dashboard = new ArrayList<>();
                        while (bool) {
                            boolean again = true;
                            String dash = null;
                            try {
                                dash = (String) ois.readObject();
                            } catch (Exception e) {
                                // throw new RuntimeException(e);
                            }
                            if (dash.equals("Number of products bought by each customer at a specific store")) {
                                ArrayList<String> marketplaceinfo = Dashboard.readFile("marketplace.txt");
                                boolean status = false;
                                for (int i = 0; i < marketplaceinfo.size(); i++) {
                                    String[] sellerinfo = marketplaceinfo.get(i).split(";");
                                    if (sellerinfo[0].equals(seller.getSellerName())) {
                                        status = true;
                                        for (int j = 1; j < sellerinfo.length; j++) {
                                            String[] productinfo = sellerinfo[j].split(",");
                                            if (productinfo[0].contains("-")) {
                                                String[] storeinfo = sellerinfo[j].split("-");
                                                writeAndFlush(storeinfo[0], oos);
                                            } else {
                                                writeAndFlush(productinfo[0], oos);
                                            }
                                        }
                                        writeAndFlush("", oos);
                                    }
                                }
                                if (status) {
                                    String store = null;
                                    try {
                                        store = (String) ois.readObject();
                                    } catch (Exception e) {
                                        // throw new RuntimeException(e);
                                    }
                                    if (!store.equals("")) {
                                        dashboard = Dashboard.getSellerDashboard1(store, "customers.txt");
                                        if (dashboard.size() > 1) {
                                            for (int i = 0; i < dashboard.size(); i++) {
                                                writeAndFlush(dashboard.get(i), oos);
                                            }
                                            writeAndFlush("", oos);
                                        } else {
                                            writeAndFlush("No one has bought anything...", oos);
                                            writeAndFlush("", oos);
                                            again = false;
                                        }
                                    } else {
                                        again = false;
                                    }
                                } else {
                                    again = false;
                                }
                            } else if (dash.equals("Number of items sold for each product at a specific store")) {
                                ArrayList<String> marketplaceinfo = Dashboard.readFile("marketplace.txt");
                                boolean status = false;
                                for (int i = 0; i < marketplaceinfo.size(); i++) {
                                    String[] sellerinfo = marketplaceinfo.get(i).split(";");
                                    if (sellerinfo[0].equals(seller.getSellerName())) {
                                        status = true;
                                        for (int j = 1; j < sellerinfo.length; j++) {
                                            String[] productinfo = sellerinfo[j].split(",");
                                            if (productinfo[0].contains("-")) {
                                                String[] storeinfo = sellerinfo[j].split("-");
                                                writeAndFlush(storeinfo[0], oos);
                                            } else {
                                                writeAndFlush(productinfo[0], oos);
                                            }
                                        }
                                        writeAndFlush("", oos);
                                    }
                                }
                                if (status) {
                                    String store = "";
                                    try {
                                        store = (String) ois.readObject();
                                    } catch (Exception e) {
                                        // throw new RuntimeException(e);
                                    }
                                    if (!store.equals("")) {
                                        dashboard = Dashboard.getSellerDashboard2(store, seller.getSellerName(),
                                                "customers.txt", "marketplace.txt");
                                        if (dashboard.size() > 1) {
                                            for (int i = 0; i < dashboard.size(); i++) {
                                                writeAndFlush(dashboard.get(i), oos);
                                            }
                                            writeAndFlush("", oos);
                                        } else {
                                            writeAndFlush("This store has no products...", oos);
                                            writeAndFlush("", oos);
                                            again = false;
                                        }
                                    } else {
                                        again = false;
                                    }
                                } else {
                                    again = false;
                                }
                            } else {
                                writeAndFlush("", oos);
                                again = false;
                                bool = false;
                            }
                            while (again) {
                                String sort = null;
                                try {
                                    sort = (String) ois.readObject();
                                } catch (Exception e) {
                                    // throw new RuntimeException(e);
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
                                    writeAndFlush("false", oos);
                                    writeAndFlush("", oos);
                                }
                                if (again) {
                                    for (int i = 0; i < dashboard.size(); i++) {
                                        writeAndFlush(dashboard.get(i), oos);
                                    }
                                    writeAndFlush("", oos);
                                }
                            }
                        }
                        break;
                    case "6. delete a store":
                        if (sellers.get(sellerID).getStores().size() == 0) {
                            System.out.println("you have no stores!");
                            writeAndFlush("no stores", oos);
                        } else {
                            writeAndFlush("has stores", oos);
                            String deleteStore = "";
                            try {
                                deleteStore = (String) ois.readObject();
                            } catch (Exception e) {
                                // e.printStackTrace();
                            }
                            Store storeInQuestion = null;
                            ArrayList<Store> currStores = sellers.get(sellerID).getStores();
                            for (int i = 0; i < currStores.size(); i++) {
                                if (currStores.get(i).getStoreName().equals(deleteStore)) {
                                    storeInQuestion = currStores.get(i);
                                    break;
                                }
                            }
                            if (storeInQuestion == null) {
                                writeAndFlush("not store", oos);
                                break;
                            }
                            writeAndFlush("is store", oos);

                            currStores.remove(storeInQuestion);
                            seller.setStores(currStores);
                            synchronized (obj) {
                                sellers.set(sellerID, seller);
                            }
                            System.out.println("Store deleted from marketplace");
                        }
                        break;
                    case "7. view customer shopping carts":
                        ArrayList<ArrayList<Object>> cusCart = cart.getAllCarts();
                        String output = "";
                        for (int i = 0; i < cusCart.size(); i++) {
                            output += (String) cusCart.get(i).get(0) + ": ;;";
                            for (int j = 1; j < cusCart.get(i).size(); j++) {
                                output += "   " + ((Product) cusCart.get(i).get(j)).toString() + ";;";
                            }
                        }
                        writeAndFlush(output, oos);
                        break;
                    case "8. import stores from a CSV":
                        String fileImport = "";
                        try {
                            fileImport = (String) ois.readObject();
                        } catch (Exception e1) {
                            // e1.printStackTrace();
                        }
                        ArrayList<Store> importedStores;
                        try {
                            importedStores = seller.importCSV(fileImport);
                        } catch (Exception e) {
                            // writeAndFlush("error", oos);
                            break;
                        }
                        writeAndFlush("no error", oos);

                        for (int i = 0; i < importedStores.size(); i++) {
                            seller.addStore(importedStores.get(i));
                        }
                        synchronized (obj) {
                            sellers.set(sellerID, seller);
                        }
                        System.out.println("Imported!");
                        break;
                    case "9. export stores as a CSV":
                        System.out.println("Enter the name of the file you want your stores to be exported to.");
                        try {
                            if (seller.exportCSV((String) ois.readObject())) {
                                System.out.println("Exported!");
                            } else {
                                System.out.println("there was a problem!");
                            }
                        } catch (Exception e) {
                            // e.printStackTrace();
                        }
                        break;
                    case "10. delete account":
                        synchronized (obj) {
                            sellers.remove(sellerID);
                        }
                        System.out.println("Account deleted, stores ejected, rejected and taken care of. Goodbye.");
                        MarketPlace.writeFile();
                        return;
                    default:
                        synchronized (obj) {
                            sellers.set(sellerID, seller);
                        }
                        System.out.println("Goodbye!");
                        MarketPlace.writeFile();
                        return;
                }
            } while (true);
        }
    }

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(6969);
        System.out.println("waiting for users to connect");
        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("got a connection");
            MarketPlace server = new MarketPlace(socket);
            try {
                new Thread(server).start();
            } catch (Exception e) {
                int poop = 0;
            }

        }
    }

    /**
     * Constructor; runs parseFile to parse the file
     *
     * @return none
     *         @param, none
     */
    public MarketPlace(Socket socket) {
        this.socket = socket;
        if (!sellersInitialized) {
            parseFile();
            synchronized (obj) {
                sellersInitialized = true;
            }
        }
    }

    public static Store getStoreIndex(int index) {
        int i = 1;
        for (Seller s: sellers) {
            for (Store st: s.getStores()) {
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
     * Parses file by splitting the line in the marketplace file into their
     * respective products, stores, and sellers
     *
     * @return none
     *         @param, none
     */
    public void parseFile() {
        // ArrayList<Seller> thisSellers = new ArrayList<Seller>();
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
                            // System.out.print(productsAndDesc.length);
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
            // e.printStackTrace();
        }
    }

    /**
     * Returns an object array of the search results aka all the products that match
     * the description
     *
     * @param, keyword A String word that the customer inputs to search for
     *
     * @return An HashSet type Object
     */
    public void search(ObjectOutputStream oostream, ObjectInputStream oistream, Customer customer) {
        Product curr;
        try {
            // ObjectOutputStream oos = new ObjectOutputStream(new
            // DataOutputStream(socket.getOutputStream()));
            String keyword = (String) oistream.readObject();
            if (keyword != null) {
                HashSet<Object> searchResult = new HashSet<Object>();
                for (Seller seller: sellers) {
                    for (Store store: seller.getStores()) {
                        for (Product product: store.getProductList()) {
                            if (product.getProductName().toLowerCase().contains(keyword.toLowerCase()) ||
                                    product.getDescription().toLowerCase().equalsIgnoreCase(keyword.toLowerCase())) {
                                searchResult.add(product);
                            }
                        }
                    }
                }
                writeAndFlush(searchResult, oostream);
                if (!searchResult.isEmpty()) {
                    keyword = (String) oistream.readObject(); // get index
                    if (keyword != null) {
                        for (Object p: searchResult) {
                            curr = (Product) p;
                            if (curr.getProductName().equals(keyword)) {
                                writeAndFlush(curr, oostream); // send product
                                break;
                            }
                        }
                        whatToDoWithProductReply(customer, oistream); // receive product
                    }
                }
            } else {
                return;
            }
        } catch (Exception e) {
            // e.printStackTrace();
            return;
        }
    }

    public void whatToDoWithProductReply(Customer customer, ObjectInputStream oistream) {
        try {
            Product inQuestion = (Product) oistream.readObject();
            if (inQuestion != null) {
                customer.addToCart(customer.getUsername(), inQuestion);
            }
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }

    /**
     * Returns an ArrayList of strings that contains the lines from the file
     *
     * @return An ArrayList of Strings
     *         @param, fileName A string of the file to be read
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
        } catch (Exception e) {
            // e.printStackTrace();
        }

        return tempList;
    }

    /**
     * Adds a new user to the file containing all users of the type seller/customer
     *
     * @param user String of the user's username
     * @param pass String of the user's password
     *             @param, fileName A String of the file to add the user's info to
     */
    private static void addUserPass(String filename, String user, String pass) {
        try {
            File f = new File(filename);
            FileOutputStream fos = new FileOutputStream(f, true);
            PrintWriter pw = new PrintWriter(fos);

            pw.println(user + "," + pass);

            pw.close();
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }

    /**
     * Writes to the marketplace file of their respective products, stores, and
     * sellers
     *
     * @return none
     *         @param, none
     */
    public synchronized static void writeFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File("marketplace.txt"),
                false))) {
            for (Seller seller: sellers) {
                String lineString = "";
                lineString += seller.getSellerName();
                for (Store store: seller.getStores()) {
                    lineString += ";" + store.getStoreName();
                    for (Product product: store.getProductList()) {
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
            bw.close();
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }

    public void decrementQuantity(Product[] products) {
        // ArrayList<Product> output = new ArrayList<>();
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
                            Product thing = new Product(products[j].getProductName(),
                                    products[j].getDescription(),
                                    products[j].getQuantity() - 1, products[j].getPrice(),
                                    products[j].getStoreName());
                            currProductList.add(z, thing);
                            currStore.setProductList(currProductList);
                            // output.add(thing);
                        }
                    }
                    currSeller.setStore(y, currStore);
                }
                sellers.remove(x);
                sellers.add(x, currSeller);

            }
        }
        // return output;
    }

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

    private void writeAndFlush(Object sendThing, ObjectOutputStream oostream) {
        try {
            oostream.writeObject(sendThing);
            oostream.flush();
        } catch (Exception e) {
            return;
        }
    }

    public void removeSellerDuplicates() {
        for (int i = 0; i < sellers.size(); i++) {
            for (int j = 0; j < sellers.size(); j++) {
                if ((i != j) && (sellers.get(i).getSellerName().equals(sellers.get(j).getSellerName()))) {
                    sellers.remove(j);
                    System.out.println("Duplicate seller removed!");
                }
            }
        }
    }
}