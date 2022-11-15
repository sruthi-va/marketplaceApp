import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

/**
 * A program that runs the actual marketplace,
 * where sellers and customers can interact with stores and do certain actions according to their respective roles and
 * permissions.
 * <p>
 * Purdue University -- CS18000 -- Fall 2022 -- Proj 4
 *
 * @author Catherine Park, Zander Carpenter, Jennifer Wang, Sruthi Vadakuppa, Vanshika Balaji
 * @version Nov 4, 2022
 */
public class MarketPlace {
    private static ArrayList<Seller> sellers = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        MarketPlace marketPlace = new MarketPlace();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the Marketplace! Please login.");
        Customer customer = null;
        Seller seller = null;
        int id = 0;
        String line;
        while (true) {
            System.out.println("1. Customer or 2. Seller? (Type 1-2)");
            line = scanner.nextLine();
            try {
                id = Integer.parseInt(line);
                if (id != 1 && id != 2) {
                    throw new Exception();
                }
                break;
            } catch (Exception e) {
                System.out.println("wtf man enter 1 or 2");
            }
        }
        System.out.println("Username: ");
        String user = scanner.nextLine();
        System.out.println("Password: ");
        String pass = scanner.nextLine();
        if (id == 1) {
            ArrayList<String> usernames = readFile("customers.txt");
            boolean usernameFound = false;
            for (int i = 0; i < usernames.size(); i++) {
                String[] splitLine = usernames.get(i).split(",");
                if (user.equals(splitLine[0])) {
                    usernameFound = true;
                    if (pass.equals(splitLine[1])) {
                        System.out.println("Logged in!");
                    } else {
                        String password = "";
                        while (!password.equals(splitLine[1])) { // this logic might be wonky but i'll have to redo a lot of stuff to get it to work properly and i will do that later
                            System.out.println("Wrong password L. try again");
                            System.out.println("Password: ");
                            password = scanner.nextLine();
                        }
                        System.out.println("Logged in!");
                    }
                    break;
                }
            }
            if (!usernameFound) {
                addUserPass("customers.txt", user, pass);
                System.out.println("New account created!");
            }
            customer = new Customer(user, pass);
            do {
                homescreen();
                System.out.println("Do you want to: 1. view store, 2. search, 3. purchase, 4. edit cart, 5. view cart, 6. view statistics, 7. delete account, or 8. logout?");
                System.out.println("(Type 1-8)");
                line = scanner.nextLine();
                switch (line) {
                    case "1":
                        Store store = null;
                        do {
                            try {
                                System.out.println("Which store? (Type a number)");
                                line = scanner.nextLine();
                                store = stores.get(Integer.parseInt(line) - 1);
                                if (store == null) {
                                    System.out.println("Please enter a valid store you dummy");
                                }
                            } catch (Exception e) {
                                System.out.println("Please enter a valid store you dummy");
                            }
                        } while (store == null);
                        boolean valid = false;
                        do {
                            store.listAllProducts();
                            System.out.println("Would you like to add a product into the " +
                                    "shopping cart, or go back? (Type 'add' or 'go back')");
                            line = scanner.nextLine();
                            switch (line) {
                                case "add":
                                    while (true) {
                                        try {
                                            System.out.println("Which product? (Type product number please)");
                                            line = scanner.nextLine();
                                            customer.addToCart(user, store.getProductList().get(Integer.parseInt(line) - 1));
                                            break;
                                        } catch (Exception e) {
                                            System.out.println("bruh");
                                        }
                                    }
                                    break;
                                case "go back":
                                    //homescreen();
                                    valid = true;
                                    break;
                                default:
                                    System.out.println("Enter valid option! (add / go back)");
                                    break;
                            }
                        } while (!valid);
                        break;
                    case "2":
                        HashSet<Object> searchResult;
                        do {
                            System.out.println("Enter the product name you'd like to search:");
                            searchResult = marketPlace.search(scanner.nextLine());
                        } while (searchResult == null);
                        for (Object o : searchResult) {
                            System.out.println(o.toString());
                        }
                        marketPlace.runSearch(scanner, customer, user);
                        break;
                    case "3":
                        Product[] list = customer.getCustomerCart().getProducts(user);
                        if (list.length > 0) {
                            //Product[] modList = marketPlace.updateProductQuantities(list);
                            customer.purchaseCart(list);
                            marketPlace.decrementQuantity(list);
                            System.out.println("Cart has been bought!");
                        } else {
                            System.out.println("Your cart is empty! Go buy some stuff");
                        }
                        break;
                    case "4":
                        Product[] cart = customer.getCustomerCart().getProducts(user);
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
                                    customer.deleteFromCart(user, item);
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
                    case "5":
                        Product[] toPrint = customer.getCustomerCart().getProducts(user);
                        if (toPrint.length == 0) {
                            System.out.println("your cart is empty!");
                        } else {
                            System.out.println("Current cart items:");
                            for (int i = 0; i < toPrint.length; i++) {
                                System.out.println(toPrint[i]);
                            }
                        }
                        break;
                    case "6":
                        int input = 0;
                        ArrayList<String> dashboard = new ArrayList<>();
                        while (input != 3) {
                            do {
                                System.out.println("What would you like to see? (Type 1-3)");
                                System.out.println("1. Number of products sold by each store");
                                System.out.println("2. Your purchased items by store");
                                System.out.println("3. Return to main menu");
                                try {
                                    input = scanner.nextInt();
                                    scanner.nextLine();
                                    if (input < 1 || input > 3) {
                                        System.out.println("Please enter a valid number!");
                                    }
                                } catch (Exception e) {
                                    System.out.println("Please enter a valid number!");
                                    scanner.nextLine();
                                    input = 0;
                                }
                            } while (input < 1 || input > 3);
                            if (input != 3) {
                                if (input == 1) {
                                    dashboard = Dashboard.getCustomerDashboard1("customers.txt",
                                            "marketplace.txt");
                                } else {
                                    dashboard = Dashboard.getCustomerDashboard2(customer.getUsername(),
                                            "customers.txt");
                                }
                                Dashboard.printDashboard(dashboard);
                                System.out.println("---------------------------");
                                int input2 = 0;
                                if (dashboard.size() > 2) {
                                    while (input2 != 5) {
                                        do {
                                            System.out.println("Would you like to sort? (Type 1-5)");
                                            System.out.println("1. Sort Alphabetically (A-Z)");
                                            System.out.println("2. Sort Alphabetically (Z-A)");
                                            System.out.println("3. Sort by quantity (high-low)");
                                            System.out.println("4. Sort by quantity (low-high)");
                                            System.out.println("5. Done");
                                            try {
                                                input2 = scanner.nextInt();
                                                scanner.nextLine();
                                                if (input2 < 1 || input2 > 5) {
                                                    System.out.println("Please enter a valid number!");
                                                }
                                            } catch (Exception e) {
                                                System.out.println("Please enter a valid number!");
                                                scanner.nextLine();
                                                input2 = 0;
                                            }
                                        } while (input2 < 1 || input2 > 5);
                                        switch (input2) {
                                            case 1:
                                                Dashboard.printDashboard(Dashboard.sortAlphabetically(dashboard, true));
                                                System.out.println("---------------------------");
                                                break;
                                            case 2:
                                                Dashboard.printDashboard(Dashboard.sortAlphabetically(dashboard, false));
                                                System.out.println("---------------------------");
                                                break;
                                            case 3:
                                                Dashboard.printDashboard(Dashboard.sortQuantity(dashboard, true));
                                                System.out.println("---------------------------");
                                                break;
                                            case 4:
                                                Dashboard.printDashboard(Dashboard.sortQuantity(dashboard, false));
                                                System.out.println("---------------------------");
                                                break;
                                            case 5:
                                                break;
                                        }
                                    }
                                }
                            }
                        }
                        break;
                    case "7":
                        System.out.println("Are you sure? (yes or no)");
                        if (scanner.nextLine().contains("yes")) {
                            customer.deleteAccount(user);
                            System.out.println("Account deleted, ejected, and rejected. Goodbye");
                        } else {
                            break;
                        }
                        return;
                    case "8":
                        customer.getCustomerCart().writeFile();
                        System.out.println("Goodbye!");
                        writeFile();
                        return;
                    default:
                        System.out.println("Enter a valid command :{");
                } // here
            } while (true);
        } else if (id == 2) {
            ArrayList<String> usernames = readFile("sellers.txt");
            boolean usernameFound = false;
            for (int i = 0; i < usernames.size(); i++) {
                String[] splitLine = usernames.get(i).split(",");
                if (user.equals(splitLine[0])) {
                    usernameFound = true;
                    if (pass.equals(splitLine[1])) {
                        System.out.println("Logged in!");
                    } else {
                        String password = "";
                        while (!password.equals(splitLine[1])) { // this logic might be wonky but i'll have to redo a lot of stuff to get it to work properly and i will do that later
                            System.out.println("Wrong password L. try again");
                            System.out.println("Password: ");
                            password = scanner.nextLine();
                        }
                        System.out.println("Logged in!");
                    }
                    break;
                }
            }
            seller = new Seller(new ArrayList<>(), user);
            if (!usernameFound) {
                addUserPass("sellers.txt", user, pass);
                System.out.println("New account created!");
            } else {
                for (Seller s : sellers) {
                    if (user.equals(s.getSellerName())) {
                        seller = sellers.get(sellers.indexOf(s));
                    }
                }
            }
            do {
                System.out.println("Do you want to 1. list your stores, 2. edit stores, 3. view sales, 4. create store,");
                System.out.println("5. view statistics, 6. delete a store, 7. import stores from a CSV,");
                System.out.println("8. export stores as a CSV, 9. delete account, or 10. log out?");
                System.out.println("(Type 1-10)");
                line = scanner.nextLine();
                switch (line) {
                    case "1":
                        int k = 1;
                        if (seller.getStores().size() == 0) {
                            System.out.println("no stores");
                        } else {
                            for (Store s : seller.getStores()) {
                                ArrayList<Product> currProducts = s.getProductList();
                                System.out.println(k + ": " + s.toString());
                                for (int i = 0; i < currProducts.size(); i++) {
                                    System.out.println("   - " + currProducts.get(i).toString() + 
                                        ", " + currProducts.get(i).getQuantity() + " left in stock.");
                                }
                                k++;
                            }
                        }
                        break;
                    case "2":
                        boolean continuing = false;
                        if (seller.getStores().size() == 0) {  
                            System.out.println("you have no stores!");
                        } else {
                            do {
                                int poop = 1;
                                for (Store s : seller.getStores()) {
                                    ArrayList<Product> currProducts = s.getProductList();
                                    System.out.println(poop + ": " + s.toString());
                                    for (int i = 0; i < currProducts.size(); i++) {
                                        System.out.println("   - " + currProducts.get(i).toString());
                                    }
                                    poop++;
                                }
                                System.out.println("Which store do you want to edit? (Enter number)");
                                Store currentStore = null;
                                while (currentStore == null) {
                                    try {
                                        int storeID = Integer.parseInt(scanner.nextLine());
                                        currentStore = seller.getStores().get(storeID - 1);
                                    } catch (Exception e) {
                                        System.out.println("Not a valid store name");
                                        System.out.println("Which store do you want to edit? (Enter number)");
                                    }
                                    
                                    /*
                                    for (int i = 0; i < seller.getStores().size(); i++) {
                                        if (seller.getStores().get(i).getStoreName().equalsIgnoreCase(storeName)) {
                                            currentStore = seller.getStores().get(i);
                                        }
                                    }*/
                                }
                                
                                boolean valid = false;
                                while (!valid) {
                                    System.out.println("Do you want to create, edit, or delete products?");
                                    String todo = scanner.nextLine();
                                    if (todo.equalsIgnoreCase("create")) {
                                        valid = true;
                                        System.out.println("What will this product be named?");
                                        String productName = scanner.nextLine();
                                        System.out.println("What is this product's description?");
                                        String description = scanner.nextLine();
                                        System.out.println("How many of this product are you selling?");
                                        int quantity = Integer.valueOf(scanner.nextLine());
                                        System.out.println("How much will this product be sold for?");
                                        double price = Double.valueOf(scanner.nextLine());
                                        marketPlace.products.add(new Product(productName, description, quantity, price, currentStore.getStoreName()));
                                        currentStore.createProduct(productName, description, quantity, price);
                                    } else if (todo.equalsIgnoreCase("edit")) {
                                        valid = true;
                                        System.out.println("Which item would you like to edit?");
                                        line = scanner.nextLine();
                                        int currProductIndex = -1;
                                        Product currProduct = null;
                                        for (Product p : marketPlace.products) {
                                            if (p.getProductName().equals(line)) {
                                                currProduct = p;
                                                currProductIndex = marketPlace.products.indexOf(p);
                                            }
                                        }
                                        System.out.println("What would you like to edit? Enter a number.");
                                        System.out.println("1. Product Name\n2. Product Description\n3. Quantity\n4. Price");
                                        int change = Integer.valueOf(scanner.nextLine());
                                        if (change == 1) {
                                            System.out.println("What is the new product name?");
                                            line = scanner.nextLine();
                                            currProduct.setProductName(line);
                                            marketPlace.products.remove(currProductIndex);
                                            marketPlace.products.add(currProduct);
                                        } else if (change == 2) {
                                            System.out.println("What is the new product description?");
                                            line = scanner.nextLine();
                                            currProduct.setDescription(line);
                                            marketPlace.products.remove(currProductIndex);
                                            marketPlace.products.add(currProduct);
                                        } else if (change == 3) {
                                            System.out.println("What is the new product quantity?");
                                            line = scanner.nextLine();
                                            currProduct.setQuantity(Integer.valueOf(line));
                                            marketPlace.products.remove(currProductIndex);
                                            marketPlace.products.add(currProduct);
                                        } else if (change == 4) {
                                            System.out.println("What is the new product price?");
                                            line = scanner.nextLine();
                                            currProduct.setPrice(Double.valueOf(line));
                                            marketPlace.products.remove(currProductIndex);
                                            marketPlace.products.add(currProduct);
                                        }
                                        marketPlace.updateProducts(currProduct);
                                    } else if (todo.equalsIgnoreCase("delete")) {
                                        valid = true;
                                        System.out.println("What is the name of the product you want to delete?");
                                        line = scanner.nextLine();
                                        boolean found = false;
                                        for (int i = 0; i < seller.getStores().size(); i++) {
                                            for (int j = 0; j < seller.getStores().get(i).getProductList().size(); j++) {
                                                if (seller.getStores().get(i).getProductList().get(j).getProductName().equalsIgnoreCase(line)) {
                                                    seller.getStores().get(i).getProductList().remove(j);
                                                    found = true;
                                                }
                                            }
                                        }
                                        if (!found) {
                                            System.out.println("Cannot find item!x");
                                        }
                                    } else {
                                        System.out.println("Please type 'create', 'edit', or 'delete'.");
                                    }
                                    marketPlace.updateSeller(seller);
                                    
                                }
                                System.out.println("Would you like to do something else?");
                                if (scanner.nextLine().toLowerCase().startsWith("y")) {
                                    continuing = true;
                                } else {
                                    continuing = false;
                                }
                            } while (continuing);
                        }
                        break;
                    case "3":
                        if (seller.getStores().size() == 0) {  
                            System.out.println("you have no stores!");
                            break;
                        }
                        try {
                            System.out.println("Type a store name to see it's sales, or 'all' to see all of your " +
                                    "store sales");
                            String storeName = scanner.nextLine();
                            seller.viewSales(storeName);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "4":
                        String storeName = "";
                        boolean repeat = true;
                        while (repeat) {
                            System.out.println("What would you like this store to be named?");
                            storeName = scanner.nextLine();
                            if (storeName.equalsIgnoreCase("") || storeName.equals(null)) {
                                System.out.println("Please enter a valid name!");
                            } else {
                                repeat = false;
                                Store tempStore = seller.createStore(seller.getSellerName(), storeName);
                                stores.add(tempStore);
                                System.out.println("Store created!");
                                marketPlace.updateSeller(seller);
                            }
                        }
                        break;
                    case "5":
                        if (seller.getStores().size() == 0) {  
                            System.out.println("you have no stores!");
                            break;
                        }
                        int input = 0;
                        ArrayList<String> dashboard = new ArrayList<>();
                        while (input != 3) {
                            do {
                                System.out.println("What would you like to see? (Type 1-3)");
                                System.out.println("1. Number of products bought by each customer at a specific store");
                                System.out.println("2. Number of items sold for each product at a specific store");
                                System.out.println("3. Return to main menu");
                                try {
                                    input = scanner.nextInt();
                                    scanner.nextLine();
                                    if (input < 1 || input > 3) {
                                        System.out.println("Please enter a valid number!");
                                    }
                                } catch (Exception e) {
                                    System.out.println("Please enter a valid number!");
                                    scanner.nextLine();
                                    input = 0;
                                }
                            } while (input < 1 || input > 3);
                            if (input != 3) {
                                String store = "";
                                boolean bool = false;
                                int input2 = 0;
                                while (!bool) {
                                    System.out.println("Please enter one of your stores:");
                                    store = scanner.nextLine();
                                    ArrayList<String> marketplaceinfo = Dashboard.readFile("marketplace.txt");
                                    for (int i = 0; i < marketplaceinfo.size(); i++) {
                                        String[] sellerinfo = marketplaceinfo.get(i).split(";");
                                        if (sellerinfo[0].equals(seller.getSellerName())) {
                                            for (int j = 1; j < sellerinfo.length; j++) {
                                                String[] storeinfo = sellerinfo[j].split("-");
                                                if (storeinfo[0].equalsIgnoreCase(store)) {
                                                    bool = true;
                                                }
                                            }
                                        }
                                    }
                                    if (!bool) {
                                        System.out.println("You don't have a store under that name...");
                                        boolean error;
                                        do {
                                            System.out.println("Try again? (type yes or no)");
                                            String again = scanner.nextLine();
                                            if (again.equalsIgnoreCase("yes")) {
                                                bool = false;
                                                error = false;
                                            } else if (again.equalsIgnoreCase("no")) {
                                                bool = true;
                                                input = 0;
                                                input2 = 5;
                                                error = false;
                                            } else {
                                                error = true;
                                                System.out.println("Invalid input, please enter yes or no");
                                            }
                                        } while (error);
                                    }
                                }
                                if (input == 1) {
                                    dashboard = Dashboard.getSellerDashboard1(store, "customers.txt");
                                } else if (input == 2) {
                                    dashboard = Dashboard.getSellerDashboard2(store, seller.getSellerName(),
                                            "customers.txt", "marketplace.txt");
                                }
                                Dashboard.printDashboard(dashboard);
                                System.out.println("---------------------------");
                                if (dashboard.size() > 2) {
                                    while (input2 != 5) {
                                        do {
                                            System.out.println("Would you like to sort? (Type 1-5)");
                                            System.out.println("1. Sort Alphabetically (A-Z)");
                                            System.out.println("2. Sort Alphabetically (Z-A)");
                                            System.out.println("3. Sort by quantity (high-low)");
                                            System.out.println("4. Sort by quantity (low-high)");
                                            System.out.println("5. Done");
                                            try {
                                                input2 = scanner.nextInt();
                                                scanner.nextLine();
                                                if (input2 < 1 || input2 > 5) {
                                                    System.out.println("Please enter a valid number!");
                                                }
                                            } catch (Exception e) {
                                                System.out.println("Please enter a valid number!");
                                                scanner.nextLine();
                                                input2 = 0;
                                            }
                                        } while (input2 < 1 || input2 > 5);
                                        switch (input2) {
                                            case 1:
                                                Dashboard.printDashboard(Dashboard.sortAlphabetically(dashboard, true));
                                                System.out.println("---------------------------");
                                                break;
                                            case 2:
                                                Dashboard.printDashboard(Dashboard.sortAlphabetically(dashboard, false));
                                                System.out.println("---------------------------");
                                                break;
                                            case 3:
                                                Dashboard.printDashboard(Dashboard.sortQuantity(dashboard, true));
                                                System.out.println("---------------------------");
                                                break;
                                            case 4:
                                                Dashboard.printDashboard(Dashboard.sortQuantity(dashboard, false));
                                                System.out.println("---------------------------");
                                                break;
                                            case 5:
                                                break;
                                        }
                                    }
                                }
                            }
                        }
                        break;
                    case "6":
                        if (seller.getStores().size() == 0) {  
                            System.out.println("you have no stores!");
                            break;
                        }
                        System.out.println("Sorry to hear that, which store?");
                        String deleteStore = scanner.nextLine();
                        Store storeInQuestion = null;
                        for (Store s : stores) {
                            if (s.getStoreName().equals(deleteStore)) {
                                storeInQuestion = s;
                                break;
                            }
                        }
                        stores.remove(storeInQuestion);
                        for (Seller s : sellers) {
                            if (s.getSellerName().equals(seller.getSellerName())) {
                                ArrayList<Store> temp = seller.getStores();
                                temp.remove(storeInQuestion);
                                System.out.println("Store deleted from marketplace");
                                s.setStores(temp);
                                break;
                            }
                        }
                        marketPlace.updateSeller(seller);
                        break;
                    case "7":
                        System.out.println("Enter the name of the file you want to import.");
                        String fileImport = scanner.nextLine();
                        ArrayList<Store> importedStores = seller.importCSV(fileImport);
                        for (int i = 0; i < importedStores.size(); i++) {
                            stores.add(importedStores.get(i));
                            seller.addStore(importedStores.get(i));
                        }
                        marketPlace.updateSeller(seller);
                        System.out.println("Imported!");
                        break;
                    case "8":
                        System.out.println("Enter the name of the file you want your stores to be exported to.");
                        if (seller.exportCSV(scanner.nextLine())) {
                            System.out.println("Exported!");
                        } else {
                            System.out.println("there was a problem!");
                        }
                        
                        break;
                    case "9":
                        for (int i = 0; i < sellers.size(); i++) {
                            if (sellers.get(i).getSellerName().equals(user)) {
                                sellers.remove(sellers.get(i));
                            }
                        }
                        System.out.println("Account deleted, stores ejected, rejected and taken care of. Goodbye.");
                        return;
                    case "10":
                        marketPlace.updateSeller(seller);
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

    /**
     * Constructor; runs parseFile to parse the file
     *
     * @return none
     * @param, none
     */
    public MarketPlace() {
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
        for (Store s : stores) {
            System.out.println(i + ": " + s.toString());
            i++;
        }
        if (stores.isEmpty()) {
            System.out.println("The Market is quiet for once. No life breathes");
        }
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
                                Integer.parseInt(productsAndDesc[2]), Double.parseDouble(productsAndDesc[3]), productsAndDesc[4]));
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
     * @param keyword A String word that the customer inputs to search for
     * @return An HashSet type Object
     */
    public HashSet<Object> search(String keyword) {
        HashSet<Object> searchResult = new HashSet<Object>();
        /*
        for (Product p : products) {
            if (p.getProductName().toLowerCase().contains(keyword.toLowerCase())
                    || p.getDescription().toLowerCase().equalsIgnoreCase(keyword.toLowerCase())) {
                searchResult.add(p);
            }
        }
        // for (Store s : stores) {
        //     if (s.getStoreName().toLowerCase().contains(keyword.toLowerCase())) {
        //         searchResult.add(s);
        //     }
        // }
        // for (Seller s : sellers) {
        //     if (s.getSellerName().toLowerCase().contains(keyword.toLowerCase())) {
        //         searchResult.add(s);
        //     }
        // }
        if (searchResult.isEmpty()) {
            System.out.println("There's nothing on this bro :((");
        }
        */
        return searchResult;
    }

    /**
     * Runs a search on repeat in case the customer wishes to add multiple things into their cart
     *
     * @param scanner A scanner so that the method can acess the user interface
     * @return none
     */
    public void runSearch(Scanner scanner, Customer customer, String user) {
        while (true) {
            System.out.println("Select an item or go back? (either \"select an item\" or \"go back\")");
            String line = scanner.nextLine();
            switch (line) {
                case "select an item":
                    System.out.println("Which item? Enter the product name");
                    line = scanner.nextLine();
                    for (Object o : search(line)) {
                        if (o instanceof Product) {
                            Product p = (Product) o;
                            System.out.printf("Add %s to cart? yes or no?\n", p.getProductName());
                            line = scanner.nextLine();
                            switch (line) {
                                case "yes":
                                    customer.addToCart(user, p);
                                    //System.out.println("Added to cart");
                                    break;
                                case "no":
                                    break;
                                default:
                                    System.out.println("Enter valid answer please, either \"select an item\" or \"go back\"");
                                    break;
                            }
                        }
                    }
                case "go back":
                    //homescreen();
                    return;
            }
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
     * @param none
     */
    public static void writeFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File("marketplace.txt"), false))) {
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
            for (int i = 0; i < this.products.size(); i++) {
                if (this.products.get(i).equals(products[j])) {
                    this.products.remove(i);
                    Product productM = new Product(products[j].getProductName(), products[j].getDescription(),
                            products[j].getQuantity() - 1, products[j].getPrice(), products[j].getStoreName());
                    this.products.add(i, productM);
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
                        if (currProductList.get(z).equals(products[j])) {
                            currProductList.remove(currStore.getProductList().get(z));
                            currProductList.add(z, new Product(products[j].getProductName(), products[j].getDescription(),
                                products[j].getQuantity() - 1, products[j].getPrice(), products[j].getStoreName()));
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
    }

    public Product[] updateProductQuantities(Product[] products) {
        Product[] output = new Product[products.length];
        //ArrayList<Product> output = new ArrayList<>();
        for (int j = 0; j < products.length; j++) {
            for (int i = 0; i < this.products.size(); i++) {
                if (this.products.get(i).equals(products[j])) {
                    output[j] = this.products.get(i);
                    break;
                }
            }
        }
        return output;
    }

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
    }
}
