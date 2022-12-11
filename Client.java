import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;

/**
 * Acts as the main interface that interacts with the user through the use of simple GUIs. Through easy to understand
 * prompts, a user can easily navigate the marketplace and have access to roles unique to their identity as a customer
 * or a seller. For instance, only customers can add products to their cart and buy it, while sellers can make stores
 * or new products and view how well their store is doing by viewing statistics calculated by the dashboard. Extensive
 * testing was performed by our team as each member tested the all the actions that can be preformed by
 * customer/seller, checking to see if it displayed the right options.
 *
 *@author Catherine Park, Zander Carpenter, Jennifer Wang, Sruthi Vadakuppa,
 *        Vanshika Balaji
 * @version December 8th, 2022
 */
public class Client {
    public static ObjectOutputStream oos;
    public static ObjectInputStream ois;

    public static void main(String[] args) throws Exception {
        boolean run = true;
        boolean runCustomer = false;
        boolean runSeller = false;
        Socket socket;
        try {
            socket = new Socket("localhost", 6969);
            oos = new ObjectOutputStream(new DataOutputStream(socket.getOutputStream()));
            ois = new ObjectInputStream(new DataInputStream(socket.getInputStream()));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Cannot connect to server! :(", "Marketplace",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        String userName = "";
        String password = "";
        String[] userChoice = { "Customer", "Seller" };
        String choose = "";

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                 | UnsupportedLookAndFeelException ex) {
            //e.printStackTrace();
        }

        ImageIcon icon = new ImageIcon("betsy.png");

        while (run) {
            try {
                int cancel = JOptionPane.showOptionDialog(null, "Welcome to bEtsy!",
                        "Welcome", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, icon, null,
                        null);
                if (cancel == JOptionPane.CLOSED_OPTION) {
                    writeAndFlush("exit", oos);
                    return;
                } else {
                    writeAndFlush("we good", oos);
                }
                choose = (String) JOptionPane.showInputDialog(null,
                        "Are you a customer or seller?", "Choice?", JOptionPane.QUESTION_MESSAGE,
                        icon, userChoice, userChoice[0]);
                if (choose == null) {
                    run = false;
                    break;
                }
                writeAndFlush(choose, oos);
                while (true) {
                    do {
                        JTextField userField = new JTextField(15);
                        JTextField passField = new JTextField(15);

                        JPanel myPanel = new JPanel();
                        myPanel.setLayout(new GridLayout(2, 2));
                        myPanel.add(new JLabel("Username:"));
                        myPanel.add(userField);
                        // myPanel.add(Box.createHorizontalStrut(15)); // a spacer
                        myPanel.add(new JLabel("Password:"));
                        myPanel.add(passField);

                        int result = JOptionPane.showConfirmDialog(null, myPanel,
                                "Please Enter Username and Password", JOptionPane.OK_CANCEL_OPTION);
                        if (result == JOptionPane.OK_OPTION) {
                            userName = userField.getText();
                            password = passField.getText();
                        }
                        if (result == JOptionPane.CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION) {
                            run = false;
                            break;
                        }

                    } while (userName.isEmpty() || password.isEmpty());

                    if (userName == null || password == null) {
                        run = false;
                    }
                    writeAndFlush(String.format("%s;;%s", userName, password), oos);
                    String isValid = (String) ois.readObject();
                    if (isValid.equals("true")) {
                        cancel = JOptionPane.showOptionDialog(null, "Successfully logged in!",
                                "Success",
                                JOptionPane.DEFAULT_OPTION,
                                JOptionPane.INFORMATION_MESSAGE, icon, null, null);
                        if (cancel == JOptionPane.CLOSED_OPTION) {
                            return;
                        }
                        break;
                    } else {
                        Object[] options1 = { "Create New Account", "Try Again" };
                        cancel = JOptionPane.showOptionDialog(null,
                                "Not a valid username or password! Create a new account or try again?",
                                "Error", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, icon,
                                options1, null);
                        if (cancel == JOptionPane.CLOSED_OPTION) {
                            writeAndFlush("quit", oos);
                            return;
                        } else if (cancel == JOptionPane.YES_OPTION) {
                            writeAndFlush("newAccount", oos);
                            String validUsername = (String) ois.readObject();
                            if (validUsername.equals("false")) {
                                cancel = JOptionPane.showOptionDialog(null,
                                        "Username already exists! Try logging in again.", "Error",
                                        JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, icon,
                                        null, null);
                                if (cancel == JOptionPane.CLOSED_OPTION) {
                                    return;
                                }
                            } else {
                                cancel = JOptionPane.showOptionDialog(null,
                                        "New account created!", "Log In",
                                        JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, icon,
                                        null, null);
                                if (cancel == JOptionPane.CLOSED_OPTION) {
                                    return;
                                }
                                break;
                            }
                        } else if (cancel == JOptionPane.NO_OPTION) {
                            writeAndFlush("tryAgain", oos);
                        } else {
                            break;
                        }
                    }
                }

                if (choose.equals("Customer")) {
                    runCustomer = true;
                    String results = (String) ois.readObject(); // DONE!! server return a string of the drop down
                    // options (1.
                    // view store,2. search,3. purchase...) etc DONE!!
                    // separate by commas
                    while (runCustomer) {
                        String[] dropdown = results.split(",", 0);
                        String reply = (String) JOptionPane.showInputDialog(null,
                                "What is your choice?", "Choice?", JOptionPane.QUESTION_MESSAGE,
                                icon, dropdown, dropdown[0]);
                        writeAndFlush(reply, oos); // DONE !! on server side, read in the line, and have ifs for the
                        // corresponding drop down choice DONE!!
                        if (reply == null) {
                            reply = " ";
                        }
                        switch (reply) {
                            case "1. view store":
                                String[] chooseStore = (String[]) ois.readObject();
                                if (chooseStore == null || chooseStore.length == 0) {
                                }
                                String viewStore = (String) JOptionPane.showInputDialog(null,
                                        "Which store do you want to view?",
                                        "View Stores?", JOptionPane.QUESTION_MESSAGE,
                                        icon, chooseStore, chooseStore[0]);
                                writeAndFlush(viewStore, oos);
                                String[] storeProducts = (String[]) ois.readObject();
                                if (storeProducts.length == 0) {
                                    JOptionPane.showMessageDialog(null, "This store doesn't have anything in stock :(",
                                            "Store's Product List",
                                            JOptionPane.ERROR_MESSAGE);
                                } else {
                                    String chooseProduct = (String) JOptionPane.showInputDialog(null,
                                            "Click on a product.", "Store's Product List",
                                            JOptionPane.QUESTION_MESSAGE, icon, storeProducts, storeProducts[0]);
                                    if (chooseProduct == null) {
                                        writeAndFlush(null, oos);
                                        runCustomer = true;
                                    } else {
                                        writeAndFlush(chooseProduct, oos);
                                        whatToDoWithProduct((Product) ois.readObject(), userName, oos);
                                    }
                                }
                                break;
                            case "2. search":
                                searchGUI(socket, userName, ois, oos);
                                break;
                            case "3. purchase":
                                String empty = (String) ois.readObject();
                                if (empty.equals("has stuff")) {
                                    String purchaseResult = (String) ois.readObject();
                                    int display = JOptionPane.showOptionDialog(null,
                                            purchaseResult, "Shopping Cart",
                                            JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, icon,
                                            null, null);
                                    if (display == JOptionPane.CLOSED_OPTION) {
                                        return;
                                    }
                                } else {
                                    JOptionPane.showMessageDialog(null, "Your cart is empty!", "Shopping Cart",
                                            JOptionPane.INFORMATION_MESSAGE);
                                }
                                break;
                            case "4. edit cart":
                                Product[] displayCart = (Product[]) ois.readObject();
                                if (displayCart == null) {
                                    JOptionPane.showMessageDialog(null, "your cart is empty!", "Shopping Cart",
                                            JOptionPane.INFORMATION_MESSAGE);
                                    writeAndFlush(null, oos);
                                } else {
                                    String[] names = new String[displayCart.length];
                                    for (int i = 0; i < displayCart.length; i++) {
                                        names[i] = displayCart[i].getProductName();
                                    }

                                    Product deleteItem = (Product) JOptionPane.showInputDialog(null,
                                            "Click on the product you want to remove from cart.",
                                            "Your Shopping Cart", JOptionPane.QUESTION_MESSAGE, icon, displayCart,
                                            displayCart[0]);

                                    if (deleteItem == null) {
                                        runCustomer = true;
                                        writeAndFlush(null, oos);
                                    } else {
                                        writeAndFlush(deleteItem, oos);
                                        JOptionPane.showMessageDialog(null, deleteItem.getProductName() + " deleted!", "Shopping Cart",
                                                JOptionPane.INFORMATION_MESSAGE);
                                    }
                                }

                                break;
                            case "5. view cart":
                                String shoppingcart = (String) ois.readObject();
                                if (shoppingcart.equals("") || shoppingcart.isEmpty()) {
                                    shoppingcart = "your cart is empty!";
                                }
                                String thing = shoppingcart.replace(";;", "\n");
                                JOptionPane.showMessageDialog(null, thing, "Shopping Cart",
                                        JOptionPane.INFORMATION_MESSAGE);
                                break;
                            case "6. view statistics":
                                boolean bool = true;
                                ArrayList<String> dashboard = new ArrayList<>();
                                String[] options = { "Number of products sold by each store",
                                        "Your purchased items by store", "Nothing" };
                                String[] sorts = { "Alphabetically(A-Z)", "Alphabetically(Z-A)", "Quantity(high-low)",
                                        "Quantity(low-high)", "Back" };
                                while (bool) {
                                    boolean again = true;
                                    String title = (String) JOptionPane.showInputDialog(null,
                                            "What would you like to see?",
                                            "Customer Dashboard",
                                            JOptionPane.QUESTION_MESSAGE, null, options, null);
                                    if (title != null) {
                                        writeAndFlush(title, oos);
                                    } else {
                                        writeAndFlush("", oos);
                                    }

                                    for (int i = 0; i < dashboard.size();) {
                                        dashboard.remove(i);
                                    }
                                    String line = (String) ois.readObject();
                                    while (!line.isBlank()) {
                                        dashboard.add(line);
                                        line = (String) ois.readObject();
                                    }
                                    if (dashboard.isEmpty()) {
                                        bool = false;
                                        again = false;
                                    } else if (dashboard.size() == 1) {
                                        JOptionPane.showMessageDialog(null, dashboard.get(0),
                                                "Customer Dashboard", JOptionPane.INFORMATION_MESSAGE);
                                        again = false;
                                    }
                                    while (again) {
                                        String dashstring = "";
                                        for (int i = 0; i < dashboard.size(); i++) {
                                            dashstring += dashboard.get(i) + "\n";
                                        }
                                        dashstring += "\n" + "What would you like to sort by?";
                                        String sort = (String) JOptionPane.showInputDialog(null, dashstring,
                                                "Customer Dashboard",
                                                JOptionPane.INFORMATION_MESSAGE, null, sorts, null);
                                        if (sort != null) {
                                            writeAndFlush(sort, oos);
                                        } else {
                                            writeAndFlush("", oos);
                                        }

                                        for (int i = 0; i < dashboard.size();) {
                                            dashboard.remove(i);
                                        }
                                        String line2 = (String) ois.readObject();
                                        while (!line2.isBlank()) {
                                            dashboard.add(line2);
                                            line2 = (String) ois.readObject();
                                        }
                                        if (dashboard.size() == 1) {
                                            again = false;
                                        }
                                    }
                                }
                                break;
                            case "7. export buy history to csv file":
                                boolean valid = false;
                                do {
                                    String input = JOptionPane.showInputDialog(null,
                                            "What file do you want to export your stores to?",
                                            "Export buy history to CSV", JOptionPane.QUESTION_MESSAGE);
                                    if (input.equalsIgnoreCase("") || input.equals(null)) {
                                        JOptionPane.showMessageDialog(null, "Not a file name.", "Export buy history",
                                                JOptionPane.ERROR_MESSAGE);
                                    } else {
                                        valid = true;
                                        writeAndFlush(input, oos);
                                        String print = (String) ois.readObject();
                                        if (print.equals("Exported!")) {
                                            JOptionPane.showMessageDialog(null, print,
                                                    "bEtsy", JOptionPane.INFORMATION_MESSAGE);
                                        } else {
                                            JOptionPane.showMessageDialog(null, print,
                                                    "bEtsy", JOptionPane.ERROR_MESSAGE);
                                        }
                                    }
                                } while (!valid);
                                break;
                            case "8. delete account":
                                JOptionPane.showMessageDialog(null, "Your account has been deleted.",
                                        "bEtsy", JOptionPane.INFORMATION_MESSAGE);
                                runCustomer = false;
                                return;
                            default:
                                JOptionPane.showMessageDialog(null, "Goodbye!",
                                        "bEtsy", JOptionPane.INFORMATION_MESSAGE);
                                runCustomer = false;
                                run = false;
                                return;
                        }
                    }
                }
                if (choose.equals("Seller")) {
                    runSeller = true;
                    String results = (String) ois.readObject(); // DONE!! server return a string of the drop down
                    // options
                    // (1. view store,2. search,3. purchase...) etc DONE!!
                    while (runSeller) {
                        String[] dropdown = results.split(",", 0);
                        String reply = (String) JOptionPane.showInputDialog(null,
                                "What is your choice?",
                                "Choice?", JOptionPane.QUESTION_MESSAGE,
                                icon, dropdown, dropdown[0]);
                        writeAndFlush(reply, oos); // DONE!! on server side, read in the line, and have ifs for the
                        // corresponding drop down choice DONE!!
                        if (reply.equals("1. list your stores")) {
                            if (((String) ois.readObject()).equals("no stores")) {
                                JOptionPane.showMessageDialog(null, "There are no stores", "Stores",
                                        JOptionPane.ERROR_MESSAGE);
                            } else {
                                @SuppressWarnings("unchecked")
                                ArrayList<Store> stores = (ArrayList<Store>) ois.readObject();
                                int k = 1;
                                String output = "";

                                for (Store s : stores) {
                                    ArrayList<Product> currProducts = s.getProductList();
                                    output += k + ": " + s.toString();
                                    for (int i = 0; i < currProducts.size(); i++) {
                                        output += "\n    - " + currProducts.get(i).toString() +
                                                ", " + currProducts.get(i).getQuantity() + " left in stock.";
                                    }
                                    output += "\n";
                                    k++;
                                }
                                JOptionPane.showMessageDialog(null, output, "Stores",
                                        JOptionPane.INFORMATION_MESSAGE);
                            }
                        }

                        if (reply.equals("2. edit stores")) {
                            if (((String) ois.readObject()).equals("no stores")) {
                                JOptionPane.showMessageDialog(null, "There are no stores", "Stores",
                                        JOptionPane.ERROR_MESSAGE);
                            } else {
                                @SuppressWarnings("unchecked")
                                ArrayList<Store> stores = (ArrayList<Store>) ois.readObject();
                                String output = ""; // what is this used for?

                                int poop = 1;
                                for (Store s : stores) {
                                    ArrayList<Product> currProducts = s.getProductList();
                                    output += poop + ": " + s.toString() + "\n";
                                    for (int i = 0; i < currProducts.size(); i++) {
                                        output += "   - " + currProducts.get(i).toString() + "\n";
                                    }
                                    poop++;
                                }

                                Store chosenStore = (Store) JOptionPane.showInputDialog(null,
                                        "Which store do you want to view?",
                                        "View Store", JOptionPane.QUESTION_MESSAGE,
                                        null, stores.toArray(), stores.toArray()[0]);

                                writeAndFlush(chosenStore, oos);
                                String[] strArr = { "create product", "edit product", "delete product" };
                                String todo = (String) JOptionPane.showInputDialog(null,
                                        "What do you want to do?", "Edit Store",
                                        JOptionPane.QUESTION_MESSAGE, icon, strArr, dropdown[0]);
                                writeAndFlush(todo, oos);

                                if (todo.equalsIgnoreCase("create product")) {
                                    // valid = true;

                                    JTextField prodName = new JTextField(15);
                                    JTextField prodDesc = new JTextField(15);
                                    JTextField prodQuant = new JTextField(15);
                                    JTextField prodPrice = new JTextField(15);

                                    JPanel myPanel = new JPanel();
                                    myPanel.setLayout(new GridLayout(4, 2));
                                    myPanel.add(new JLabel("Product Name:"));
                                    myPanel.add(prodName);
                                    myPanel.add(new JLabel("Product Description:"));
                                    myPanel.add(prodDesc);
                                    myPanel.add(new JLabel("Product Quantity:"));
                                    myPanel.add(prodQuant);
                                    myPanel.add(new JLabel("Product Price:"));
                                    myPanel.add(prodPrice);

                                    int result = JOptionPane.showConfirmDialog(null, myPanel,
                                            "Enter Product Details", JOptionPane.OK_CANCEL_OPTION);
                                    // nopthing uses result? is this unfinished?
                                    String name = prodName.getText();
                                    String desc = prodDesc.getText();
                                    String quant = prodQuant.getText();
                                    String price = prodPrice.getText();

                                    Product product = new Product(name, desc, Integer.parseInt(quant),
                                            Double.parseDouble(price), chosenStore.getStoreName());
                                    writeAndFlush(product, oos);
                                    JOptionPane.showMessageDialog(null, product.getProductName() + " added to store!",
                                            "Edit Store",
                                            JOptionPane.INFORMATION_MESSAGE);
                                } else if (todo.equalsIgnoreCase("edit product")) {
                                    // valid = true;
                                    @SuppressWarnings("unchecked")
                                    ArrayList<Product> products = (ArrayList<Product>) ois.readObject();
                                    String[] names = new String[products.size()];
                                    Product currProduct; // where is this used?
                                    int currProductIndex = -1;
                                    int i = 0;
                                    for (Product p : products) {
                                        names[i] = p.getProductName();
                                        i++;
                                    }

                                    String chosenName = (String) JOptionPane.showInputDialog(null,
                                            "What product do you want to edit?",
                                            "Edit Product", JOptionPane.QUESTION_MESSAGE,
                                            null, names, names[0]);

                                    i = 0;
                                    for (Product p : products) {
                                        if (p.getProductName().equals(chosenName)) {
                                            currProduct = p;
                                            currProductIndex = i;
                                        }
                                        i++;
                                    }

                                    JTextField prodName = new JTextField(15);
                                    JTextField prodDesc = new JTextField(15);
                                    JTextField prodQuant = new JTextField(15);
                                    JTextField prodPrice = new JTextField(15);

                                    JPanel myPanel = new JPanel();
                                    myPanel.setLayout(new GridLayout(4, 2));
                                    myPanel.add(new JLabel("Product Name:"));
                                    myPanel.add(prodName);
                                    myPanel.add(new JLabel("Product Description:"));
                                    myPanel.add(prodDesc);
                                    myPanel.add(new JLabel("Product Quantity:"));
                                    myPanel.add(prodQuant);
                                    myPanel.add(new JLabel("Product Price:"));
                                    myPanel.add(prodPrice);

                                    int result = JOptionPane.showConfirmDialog(null, myPanel,
                                            "Enter Product Details", JOptionPane.OK_CANCEL_OPTION);
                                    // same thing nothing ends up using this result?
                                    String name = prodName.getText();
                                    String desc = prodDesc.getText();
                                    String quant = prodQuant.getText();
                                    String price = prodPrice.getText();

                                    Product product = new Product(name, desc, Integer.parseInt(quant),
                                            Double.parseDouble(price), chosenStore.getStoreName());

                                    writeAndFlush(product, oos);
                                    writeAndFlush("" + currProductIndex, oos);
                                    JOptionPane.showMessageDialog(null, product.getProductName() + " edited!",
                                            "Edit Store",
                                            JOptionPane.INFORMATION_MESSAGE);
                                } else if (todo.equalsIgnoreCase("delete product")) {
                                    @SuppressWarnings("unchecked")
                                    ArrayList<Product> products = (ArrayList<Product>) ois.readObject();
                                    String[] names = new String[products.size()];
                                    int currProductIndex = -1;
                                    int i = 0;
                                    for (Product p : products) {
                                        names[i] = p.getProductName();
                                        i++;
                                    }

                                    String chosenName = (String) JOptionPane.showInputDialog(null,
                                            "What product do you want to edit?",
                                            "Edit Product", JOptionPane.QUESTION_MESSAGE,
                                            null, names, names[0]);

                                    i = 0;
                                    for (Product p : products) {
                                        if (p.getProductName().equals(chosenName)) {
                                            currProductIndex = i;
                                        }
                                        i++;
                                    }

                                    writeAndFlush("" + currProductIndex, oos);
                                    JOptionPane.showMessageDialog(null, chosenName + " deleted!", "Edit Store",
                                            JOptionPane.INFORMATION_MESSAGE);
                                } else {
                                }
                            }
                        }

                        if (reply.equals("3. create store")) {
                            boolean repeat = true;
                            while (repeat) {
                                String storeName = JOptionPane.showInputDialog(null,
                                        "What would you like this store to be named?",
                                        "Create Store", JOptionPane.QUESTION_MESSAGE);
                                if (storeName.equalsIgnoreCase("") || storeName.equals(null)) {
                                    JOptionPane.showMessageDialog(null, "Please enter a valid name", "Create Store",
                                            JOptionPane.ERROR_MESSAGE);
                                } else {
                                    repeat = false;
                                    writeAndFlush(storeName, oos);
                                    JOptionPane.showMessageDialog(null, "Store Created", "Create Store",
                                            JOptionPane.INFORMATION_MESSAGE);
                                }
                            }
                        }

                        if (reply.equals("4. view statistics")) {
                            boolean bool = true;
                            ArrayList<String> dashboard = new ArrayList<>();
                            ArrayList<String> stores = new ArrayList<>();
                            String[] options = { "Number of products bought by each customer at a specific store",
                                    "Number of items sold for each product at a specific store", "Nothing" };
                            String[] sorts = { "Alphabetically(A-Z)", "Alphabetically(Z-A)",
                                    "Quantity(high-low)", "Quantity(low-high)", "Back" };
                            while (bool) {
                                boolean again = true;
                                boolean storeStatus = false;
                                String title = (String) JOptionPane.showInputDialog(null, "What would you like to see?",
                                        "Seller Dashboard",
                                        JOptionPane.QUESTION_MESSAGE, null, options, null);
                                if (title != null) {
                                    if (title.equals("Number of products bought by each customer at a specific store")
                                            || title.equals(
                                            "Number of items sold for each product at a specific store")) {
                                        writeAndFlush(title, oos);
                                        for (int i = 0; i < stores.size();) {
                                            stores.remove(i);
                                        }
                                        String line = (String) ois.readObject();
                                        while (!line.isBlank()) {
                                            stores.add(line);
                                            line = (String) ois.readObject();
                                        }
                                        String[] eachstore = stores.toArray(new String[0]);
                                        String store = "";
                                        if (!eachstore[0].equals("")) {
                                            store = (String) JOptionPane.showInputDialog(null,
                                                    "From which of your stores?",
                                                    "Seller Dashboard",
                                                    JOptionPane.QUESTION_MESSAGE, null, eachstore, null);
                                            if (store != null) {
                                                storeStatus = true;
                                                writeAndFlush(store, oos);
                                            } else {
                                                writeAndFlush("", oos);
                                            }
                                        } else {
                                            JOptionPane.showMessageDialog(null, "You don't have any stores...",
                                                    "Seller Dashboard",
                                                    JOptionPane.ERROR_MESSAGE);
                                        }
                                    } else if (title.equals("Nothing")) {
                                        writeAndFlush(title, oos);
                                        bool = false;
                                    }
                                } else {
                                    writeAndFlush("", oos);
                                    bool = false;
                                }

                                if (storeStatus) {
                                    for (int i = 0; i < dashboard.size();) {
                                        dashboard.remove(i);
                                    }
                                    String line = (String) ois.readObject();
                                    while (!line.isBlank()) {
                                        dashboard.add(line);
                                        line = (String) ois.readObject();
                                    }
                                    if (dashboard.isEmpty()) {
                                        bool = false;
                                        again = false;
                                    } else if (dashboard.size() == 1) {
                                        JOptionPane.showMessageDialog(null, dashboard.get(0), "Seller Dashboard",
                                                JOptionPane.ERROR_MESSAGE);
                                        again = false;
                                    }
                                    while (again) {
                                        String dashstring = "";
                                        for (int i = 0; i < dashboard.size(); i++) {
                                            dashstring += dashboard.get(i) + "\n";
                                        }
                                        dashstring += "\n" + "What would you like to sort by?";
                                        String sort = (String) JOptionPane.showInputDialog(null, dashstring,
                                                "Seller Dashboard",
                                                JOptionPane.INFORMATION_MESSAGE, null, sorts, null);
                                        if (sort != null) {
                                            writeAndFlush(sort, oos);
                                        } else {
                                            writeAndFlush("", oos);
                                        }

                                        for (int i = 0; i < dashboard.size();) {
                                            dashboard.remove(i);
                                        }
                                        String line2 = (String) ois.readObject();
                                        while (!line2.isBlank()) {
                                            dashboard.add(line2);
                                            line2 = (String) ois.readObject();
                                        }
                                        if (dashboard.size() == 1) {
                                            again = false;
                                        }
                                    }
                                }
                            }
                        }

                        if (reply.equals("5. delete a store")) {
                            boolean valid = false;
                            do {
                                String input = JOptionPane.showInputDialog(null, "Which store?",
                                        "Delete Store", JOptionPane.QUESTION_MESSAGE);
                                if (input.equalsIgnoreCase("") || input.equals(null)) {
                                    JOptionPane.showMessageDialog(null, "Not a store", "Stores",
                                            JOptionPane.ERROR_MESSAGE);
                                } else {
                                    valid = true;
                                    writeAndFlush(input, oos);
                                    if (((String) ois.readObject()).equals("is store")) {
                                        JOptionPane.showMessageDialog(null, "Stores deleted!", "Stores",
                                                JOptionPane.INFORMATION_MESSAGE);
                                    } else {
                                        JOptionPane.showMessageDialog(null, "Still not a store", "Stores",
                                                JOptionPane.ERROR_MESSAGE);
                                    }
                                }
                            } while (!valid);
                        }

                        if (reply.equals("6. view customer shopping carts")) {
                            String output = ((String) ois.readObject()).replace(";;", "\n");
                            JOptionPane.showMessageDialog(null, output, "Customer Carts",
                                    JOptionPane.INFORMATION_MESSAGE);

                        }

                        if (reply.equals("7. import stores from a CSV")) {
                            boolean valid = false;
                            do {
                                String input = JOptionPane.showInputDialog(null, "What file do you want to import?",
                                        "Import Stores from CSV", JOptionPane.QUESTION_MESSAGE);
                                if (input.equalsIgnoreCase("") || input.equals(null)) {
                                    JOptionPane.showMessageDialog(null, "Not a name", "Stores",
                                            JOptionPane.ERROR_MESSAGE);
                                } else {
                                    valid = true;
                                    writeAndFlush(input, oos);
                                    if (((String) ois.readObject()).equals("error")) {
                                        JOptionPane.showMessageDialog(null, "aur naur something's wrong with the file",
                                                "Import Stores from CSV", JOptionPane.ERROR_MESSAGE);
                                    } else {
                                        JOptionPane.showMessageDialog(null, "we good",
                                                "Import Stores from CSV", JOptionPane.INFORMATION_MESSAGE);
                                    }
                                }
                            } while (!valid);
                        }

                        if (reply.equals("8. export stores as a CSV")) {
                            boolean valid = false;
                            do {
                                String input = JOptionPane.showInputDialog(null,
                                        "What file do you want to export your stores to?",
                                        "Export Stores to CSV", JOptionPane.QUESTION_MESSAGE);
                                if (input.equalsIgnoreCase("") || input.equals(null)) {
                                    JOptionPane.showMessageDialog(null, "Not a file name", "Stores",
                                            JOptionPane.ERROR_MESSAGE);
                                } else {
                                    valid = true;
                                    writeAndFlush(input, oos);
                                    JOptionPane.showMessageDialog(null, "Exported!", "Stores",
                                            JOptionPane.ERROR_MESSAGE);
                                }
                            } while (!valid);
                        }

                        if (reply.equals("9. delete account")) {
                            JOptionPane.showMessageDialog(null, "Your account has been deleted.",
                                    "bEtsy", JOptionPane.INFORMATION_MESSAGE);
                            runSeller = false;
                        }

                        if (reply.equals("10. log out")) {
                            runSeller = false;
                            run = false;
                            JOptionPane.showMessageDialog(null, "Thanks for visiting bEtsy!", "Goodbye",
                                    JOptionPane.INFORMATION_MESSAGE, icon);
                        }
                    }
                }
            } catch (Exception e) {
                // e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Thanks for visiting bEtsy!", "Goodbye",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }
    }

    public static void searchGUI(Socket socket,
                                 String userName, ObjectInputStream oistream, ObjectOutputStream oostream) {
        try {
            String chosenProduct;
            String searchWord = JOptionPane.showInputDialog(null,
                    "Enter Search", "Search Bar", JOptionPane.QUESTION_MESSAGE); // enter search
            writeAndFlush(searchWord, oostream);
            if (searchWord != null) {
                @SuppressWarnings("unchecked")
                HashSet<Object> results = (HashSet<Object>) oistream.readObject();
                if (results.isEmpty()) {
                    JOptionPane.showMessageDialog(null,
                            "Error: There's nothing on this bro :((", "Search Bar", JOptionPane.ERROR_MESSAGE);
                    return;
                } else {
                    ArrayList<String> resultString = new ArrayList<>();
                    for (Object p : results) {
                        Product curr = (Product) p;
                        resultString.add(curr.getProductName());
                    }
                    chosenProduct = (String) JOptionPane.showInputDialog(null,
                            "Select Desired Product", "MarketPlace",
                            JOptionPane.PLAIN_MESSAGE, null, resultString.toArray(new String[0]),
                            null);
                    writeAndFlush(chosenProduct, oostream);
                    if (chosenProduct != null) {
                        Product product = (Product) oistream.readObject();
                        whatToDoWithProduct(product, userName, oostream);
                    }
                }
            } else {
                return;
            }
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }

    /**
     * What to do with selected product
     *
     * @return none
     *         @param, scanner A scanner so that the method can acess the user
     *         interface
     */
    public static void whatToDoWithProduct(Product product, String userName, ObjectOutputStream oostream) {
        try {
            int choice = JOptionPane.showConfirmDialog(null,
                    String.format("%s. %d in stock.\nAdd %s to cart? Yes or no?", product.toString(),
                            product.getQuantity(), product.getProductName()),
                    "MarketPlace", JOptionPane.YES_NO_OPTION);
            if (choice == 0) {
                writeAndFlush(product, oostream);
                JOptionPane.showMessageDialog(null, "Item added to cart", "MarketPlace",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                writeAndFlush(null, oostream);
                JOptionPane.showMessageDialog(null, "Sad", "MarketPlace",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            // e.printStackTrace();
            return;
        }
    }

    private static void writeAndFlush(Object sendThing, ObjectOutputStream oostream) {
        try {
            oostream.writeObject(sendThing);
            oostream.flush();
        } catch (Exception e) {
            return;
        }
    }
}
