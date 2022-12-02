import javax.swing.*;
import javax.swing.colorchooser.ColorChooserComponentFactory;
import javax.swing.text.DefaultEditorKit.InsertBreakAction;

import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;

public class Client {
    public static void main(String[] args) throws IOException {
        boolean run = true;
        boolean runCustomer = false;
        boolean runSeller = false;
        Socket socket = new Socket("localhost", 6969);
        String userName = "";
        String password = "";
        String userChoice[] = { "Customer", "Seller" };
        String choose = "";

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | 
            IllegalAccessException | UnsupportedLookAndFeelException ex) {
            int i = 0;
        }

        ImageIcon icon = new ImageIcon("betsy.png");

        while (run) {
            try {
                int cancel = JOptionPane.showOptionDialog(null, "Welcome to bEtsy!",
                        "Welcome", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, icon, null,
                        null);
                if (cancel == JOptionPane.CLOSED_OPTION) {
                    return;
                }

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                ObjectOutputStream oos = new ObjectOutputStream(new DataOutputStream(socket.getOutputStream()));
                ObjectInputStream ois = new ObjectInputStream(new DataInputStream(socket.getInputStream()));
                choose = (String) JOptionPane.showInputDialog(null,
                        "Are you a customer or seller?","Choice?", JOptionPane.QUESTION_MESSAGE,
                        icon, userChoice, userChoice[0]);
                if (choose == null) {
                    run = false;
                }
                writer.write(choose);
                writer.newLine();
                writer.flush();
                while (true) {
                    do {
                        JTextField userField = new JTextField(15);
                        JTextField passField = new JTextField(15);

                        JPanel myPanel = new JPanel();
                        myPanel.add(new JLabel("Username:"));
                        myPanel.add(userField);
                        myPanel.add(Box.createHorizontalStrut(15)); // a spacer
                        myPanel.add(new JLabel("Password:"));
                        myPanel.add(passField);

                        int result = JOptionPane.showConfirmDialog(null, myPanel, 
                                "Please Enter Username and Password", JOptionPane.OK_CANCEL_OPTION);
                        if (result == JOptionPane.OK_OPTION) {
                            userName = userField.getText();
                            password = passField.getText();
                            System.out.println("username " + userField.getText());
                            System.out.println("password " + passField.getText());
                        }
                    } while (userName.isEmpty() || password.isEmpty());
                    
                    if (userName == null || password == null) {
                        run = false;
                    } 

                    writer.write(String.format("%s;;%s", userName, password));
                    writer.newLine();
                    writer.flush();

                    String isValid = reader.readLine();
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
                        Object[] options1 = {"Create New Account", "Try Again"};
                        cancel = JOptionPane.showOptionDialog(null, 
                            "Not a valid username or password! Create a new account or try again?", 
                            "Error", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, icon, 
                            options1, null);
                        if (cancel == JOptionPane.CLOSED_OPTION) {
                            writer.write("quit");
                            writer.newLine();
                            writer.flush();
                            return;
                        } else if (cancel == JOptionPane.YES_OPTION) {
                            writer.write("newAccount");
                            writer.newLine();
                            writer.flush();
                            String validUsername = reader.readLine();
                            if (validUsername.equals("false")) {
                                cancel = JOptionPane.showOptionDialog(null, 
                                    "Username already exists! Try logging in again.", "Error",
                                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, 
                                    null, null);
                                if (cancel == JOptionPane.CLOSED_OPTION) {
                                    return;
                                }
                            }
                        } else if (cancel == JOptionPane.NO_OPTION) {
                            writer.write("tryAgain");
                            writer.newLine();
                            writer.flush();
                        } else {
                            break;
                        }
                    }
                }

                if (choose.equals("Customer")) {
                    runCustomer = true;

                    String results = reader.readLine(); // DONE!! server return a string of the drop down options (1.
                                                        // view store,2. search,3. purchase...) etc DONE!!
                    // separate by commas
                    while (runCustomer) {
                        String[] dropdown = results.split(",", 0);
                        String reply = (String) JOptionPane.showInputDialog(null,
                                "What is your choice?", "Choice?", JOptionPane.QUESTION_MESSAGE,
                                icon, dropdown, dropdown[0]);
                        writer.write(reply); // DONE !! on server side, read in the line, and have ifs for the
                                            // corresponding drop down choice DONE!!
                        writer.newLine();
                        writer.flush();

                        switch (reply) {
                            case "1. view store":
                                // Added a string sent to server so that it prompts the corresponding action
                                writer.write(reply);
                                writer.newLine();
                                writer.flush();
                                // 

                                String[] chooseStore = (String[]) ois.readObject();
                                String viewStore = (String) JOptionPane.showInputDialog(null,
                                        "Which store do you want to view?",
                                        "View Stores?", JOptionPane.QUESTION_MESSAGE,
                                        icon, chooseStore, chooseStore[0]);
                                writer.write(viewStore);
                                writer.newLine();
                                writer.flush();

                                String[] storeProducts = (String[]) ois.readObject();
                                String chooseProduct = (String) JOptionPane.showInputDialog(null,
                                        "Click on the product you want to buy.","Store's Product List",
                                        JOptionPane.QUESTION_MESSAGE,icon, storeProducts, storeProducts[0]);
                                if (chooseProduct == null) {
                                    runCustomer = true;
                                } else {
                                    whatToDoWithProduct(null, userName, oos);
                                }
                                break;
                            case "2. search":
                                // Added a string sent to server so that it prompts the corresponding action
                                writer.write(reply);
                                writer.newLine();
                                writer.flush();
                                searchGUI(socket, writer, reader, userName, ois, oos);
                                break;
                            case "3. purchase":                                // Added a string sent to server so that it prompts the corresponding action
                                writer.write(reply);
                                writer.newLine();
                                writer.flush();
                                // 
                                String purchaseResult = reader.readLine();
                                if (purchaseResult.contains("out of stock")) {
                                } else {
                                    int display = JOptionPane.showOptionDialog(null,
                                        purchaseResult, "Shopping Cart",
                                        JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, icon,
                                        null, null);
                                    if (display == JOptionPane.CLOSED_OPTION) {
                                        return;
                                    }
                                }
                                break;
                            case "4. edit cart":
                                // Added a string sent to server so that it prompts the corresponding action
                                writer.write(reply);
                                writer.newLine();
                                writer.flush();
                                // 

                                String[] displayCart = (String[]) ois.readObject();
                                String deleteItem = (String) JOptionPane.showInputDialog(null,
                                        "Click on the product you want to remove to cart.",
                                        "Your Shopping Cart", JOptionPane.QUESTION_MESSAGE,icon, displayCart,
                                        displayCart[0]);
                                if (deleteItem == null) {
                                    runCustomer = true;
                                } else {
                                    writer.write(deleteItem);
                                    writer.newLine();
                                    writer.flush();
                                }
                                break;
                            case "5. view cart":
                                // Added a string sent to server so that it prompts the corresponding action
                                writer.write(reply);
                                writer.newLine();
                                writer.flush();
                                //
                                
                                String shoppingcart = reader.readLine();
                                if (shoppingcart != null) {
                                    shoppingcart = "your cart is empty!";
                                } 
                                JOptionPane.showMessageDialog(null, shoppingcart, "Shopping Cart",
                                        JOptionPane.INFORMATION_MESSAGE);
                                break;
                            case "6. view statistics":
                                // Added a string sent to server so that it prompts the corresponding action
                                writer.write(reply);
                                writer.newLine();
                                writer.flush();
                                //

                                boolean bool = true;
                                ArrayList<String> dashboard = new ArrayList<>();
                                String[] options = {"Number of products sold by each store",
                                        "Your purchased items by store", "Nothing"};
                                String[] sorts = {"Alphabetically(A-Z)", "Alphabetically(Z-A)", "Quantity(high-low)",
                                        "Quantity(low-high)", "Back"};
                                while (bool) {
                                    boolean again = true;
                                    String title = (String) JOptionPane.showInputDialog(null,
                                            "What would you like to see?",
                                            "Customer Dashboard",
                                            JOptionPane.QUESTION_MESSAGE, null, options, null);
                                    if (title != null) {
                                        writer.write(title);
                                    } else {
                                        writer.write("");
                                    }
                                    writer.newLine();
                                    writer.flush();

                                    for (int i = 0; i < dashboard.size(); ) {
                                        dashboard.remove(i);
                                    }
                                    String line = reader.readLine();
                                    while (!line.isBlank()) {
                                        dashboard.add(line);
                                        line = reader.readLine();
                                    }
                                    if (dashboard.isEmpty()) {
                                        bool = false;
                                        again = false;
                                    } else if (dashboard.size() == 1) {
                                        JOptionPane.showMessageDialog(null, dashboard.get(0),
                                                "Customer Dashboard",
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
                                                "Customer Dashboard",
                                                JOptionPane.INFORMATION_MESSAGE, null, sorts, null);
                                        if (sort != null) {
                                            writer.write(sort);
                                        } else {
                                            writer.write("");
                                        }
                                        writer.newLine();
                                        writer.flush();

                                        for (int i = 0; i < dashboard.size(); ) {
                                            dashboard.remove(i);
                                        }
                                        String line2 = reader.readLine();
                                        while (!line2.isBlank()) {
                                            dashboard.add(line2);
                                            line2 = reader.readLine();
                                        }
                                        if (dashboard.size() == 1) {
                                            again = false;
                                        }
                                    }
                                }
                                break;

                            case "7. delete account":
                                // Added a string sent to server so that it prompts the corresponding action
                                writer.write(reply);
                                writer.newLine();
                                writer.flush();
                                //

                                JOptionPane.showMessageDialog(null, "Your account has been deleted.",
                                        "bEtsy",JOptionPane.INFORMATION_MESSAGE);
                                runCustomer = false;
                                return;
                            
                            case "8. logout":
                                // Added a string sent to server so that it prompts the corresponding action
                                writer.write(reply);
                                writer.newLine();
                                writer.flush();
                                //

                                JOptionPane.showMessageDialog(null, "Goodbye!",
                                        "bEtsy",JOptionPane.INFORMATION_MESSAGE);
                                runCustomer = false;
                                run = false;
                                return;
                            default:
                                return;
                        }
                    }

                }
                if (choose.equals("Seller")) {
                    runSeller = true;
                    while (runSeller) {
                        String results = reader.readLine(); // DONE!! server return a string of the drop down options
                        // (1. view store,2. search,3. purchase...) etc DONE!!
                        String[] dropdown = results.split(",", 0);
                        String reply = (String) JOptionPane.showInputDialog(null,
                                "What is your choice?",
                        "Choice?", JOptionPane.QUESTION_MESSAGE,
                        icon, dropdown, dropdown[0]);
                        writer.write(reply); // DONE!! on server side, read in the line, and have ifs for the
                                            // corresponding drop down choice DONE!!
                        writer.newLine();
                        writer.flush();
                        
                        if (reply.equals("1. list your stores")) {
                            // Added a string sent to server so that it prompts the corresponding action
                            writer.write(reply);
                            writer.newLine();
                            writer.flush();
                            //

                            if (reader.readLine().equals("no stores")) {
                                JOptionPane.showMessageDialog(null, "There are no stores", "Stores",
                                        JOptionPane.ERROR_MESSAGE);
                            } else {
                                Store[] stores = (Store[]) ois.readObject();
                                int k = 1;

                                for (Store s : stores) {
                                    ArrayList<Product> currProducts = s.getProductList();
                                    System.out.println(k + ": " + s.toString());
                                    for (int i = 0; i < currProducts.size(); i++) {
                                        System.out.println("   - " + currProducts.get(i).toString() +
                                                ", " + currProducts.get(i).getQuantity() + " left in stock.");
                                    }
                                    k++;
                                }
                                // TODO make this a gui
                            }
                        }

                        if (reply.equals("2. edit stores")) {
                            // Added a string sent to server so that it prompts the corresponding action
                            writer.write(reply);
                            writer.newLine();
                            writer.flush();
                            //


                            if (reader.readLine().equals("no stores")) {
                                JOptionPane.showMessageDialog(null, "There are no stores", "Stores",
                                        JOptionPane.ERROR_MESSAGE);
                            } else {
                                Store[] stores = (Store[]) ois.readObject();

                                int poop = 1;
                                for (Store s : stores) {
                                    ArrayList<Product> currProducts = s.getProductList();
                                    System.out.println(poop + ": " + s.toString());
                                    for (int i = 0; i < currProducts.size(); i++) {
                                        System.out.println("   - " + currProducts.get(i).toString());
                                    }
                                    poop++;
                                }
                                // TODO display stores in a gui

                                Store chosenStore = null; // TODO this should be set to what the user chooses
                                
                                oos.writeObject(chosenStore);
                                oos.flush();


                                boolean valid = false;
                                while (!valid) {
                                    // TODO gui
                                    String[] strArr = {"create", "edit", "delete products"};
                                    String todo = (String) JOptionPane.showInputDialog(null,
                                "What is your choice?",
                        "Choice?", JOptionPane.QUESTION_MESSAGE,
                        icon, strArr, dropdown[0]);
                                    //System.out.println("Do you want to create, edit, or delete products?");
                                    writer.write(todo);
                                    writer.newLine();
                                    writer.flush(); 
                                    
                                    if (todo.equalsIgnoreCase("create")) {
                                        valid = true;

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
                                                
                                        String name = prodName.getText();
                                        String desc = prodDesc.getText();
                                        String quant = prodQuant.getText();
                                        String price = prodPrice.getText();
                                            
                                        System.out.printf("%s, %s, %s, %s\n", name, desc, quant, price);

                                        Product product = new Product(name, desc, Integer.parseInt(quant), 
                                            Double.parseDouble(price), chosenStore.getStoreName());

                                        oos.writeObject(product);
                                        oos.flush();
                                    } else if (todo.equalsIgnoreCase("edit")) {
                                        valid = true;
                                        Product[] products = (Product[]) ois.readObject();
                                        String[] names = new String[products.length];
                                        Product currProduct;
                                        int currProductIndex = -1;
                                        int i = 0;
                                        for (Product p : products) {
                                            names[i] = p.getProductName();
                                            i++;
                                        }

                                        // TODO PRODUCT CHOOSE GUI HERE
                                        String chosenName = null;

                                        i = 0;
                                        for (Product p : products) {
                                            if (p.getProductName().equals(chosenName)) { // TODO chosen name gui
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
                                                
                                        String name = prodName.getText();
                                        String desc = prodDesc.getText();
                                        String quant = prodQuant.getText();
                                        String price = prodPrice.getText();
                                            
                                        System.out.printf("%s, %s, %s, %s\n", name, desc, quant, price);

                                        Product product = new Product(name, desc, Integer.parseInt(quant), 
                                            Double.parseDouble(price), chosenStore.getStoreName());

                                        oos.writeObject(product);
                                        oos.flush();
                                        writer.write(currProductIndex);
                                        writer.newLine();
                                        writer.flush();
                                    } else if (todo.equalsIgnoreCase("delete")) {
                                        Product[] products = (Product[]) ois.readObject();
                                        String[] names = new String[products.length];
                                        int currProductIndex = -1;
                                        int i = 0;
                                        for (Product p : products) {
                                            names[i] = p.getProductName();
                                            i++;
                                        }

                                        // TODO PRODUCT CHOOSE GUI HERE
                                        String chosenName = null;

                                        i = 0;
                                        for (Product p : products) {
                                            if (p.getProductName().equals(chosenName)) { // TODO chosen name gui
                                                currProductIndex = i;
                                            }
                                            i++;
                                        }

                                        writer.write(currProductIndex);
                                        writer.newLine();
                                        writer.flush();
                                    } else {
                                        System.out.println("Please type 'create', 'edit', or 'delete'.");
                                    }
                                }

                            }
                        }

                        if (reply.equals("3. view sales")) {
                            // Added a string sent to server so that it prompts the corresponding action
                            writer.write(reply);
                            writer.newLine();
                            writer.flush();
                            //
                                                        if (reader.readLine().equals("no stores")) {
                                JOptionPane.showMessageDialog(null, "There are no stores", "Stores",
                                        JOptionPane.ERROR_MESSAGE);
                            } else { 
                                System.out.println("Type a store name to see it's sales, or 'all' to see all of your " +
                                    "store sales"); // TODO gui
                                String input = ""; // value from GUI
                                writer.write(input);
                                writer.newLine();
                                writer.flush();
                                

                                String sellerHistory = reader.readLine(); // TODO display gui
                            }
                        }

                        if (reply.equals("4. create store")) {
                            boolean repeat = true;
                            while (repeat) {
                                System.out.println("What would you like this store to be named?"); // TODO GUI
                                String storeName = ""; // value from GUI
                                if (storeName.equalsIgnoreCase("") || storeName.equals(null)) {
                                    System.out.println("Please enter a valid name!");
                                } else {
                                    repeat = false;
                                    writer.write(storeName);
                                    writer.newLine();
                                    writer.flush();
                                    System.out.println("Store created!"); // TODO
                                }
                            }
                        }

                        if (reply.equals("5. view statistics")) {
                            boolean bool = true;
                            ArrayList<String> dashboard = new ArrayList<>();
                            String[] options = {"Number of products bought by each customer at a specific store", "Number of items sold for each product at a specific store", "Nothing"};
                            String[] sorts = {"Alphabetically(A-Z)", "Alphabetically(Z-A)", "Quantity(high-low)", "Quantity(low-high)", "Back"};
                            while (bool) {
                                boolean again = true;
                                boolean storeStatus = false;
                                String title = (String) JOptionPane.showInputDialog(null, "What would you like to see?",
                                        "Seller Dashboard",
                                        JOptionPane.QUESTION_MESSAGE, null, options, null);
                                if (title != null) {
                                    if (title.equals("Number of products bought by each customer at a specific store") || title.equals("Number of items sold for each product at a specific store")) {
                                        writer.write(title);
                                        writer.newLine();
                                        writer.flush();
                                        String store = JOptionPane.showInputDialog(null, "Please enter one of your stores:",
                                                "Seller Dashboard",
                                                JOptionPane.PLAIN_MESSAGE);
                                        if (store != null) {
                                            writer.write(store);
                                        } else {
                                            writer.write("false");
                                        }
                                        writer.newLine();
                                        writer.flush();
                                        String status = reader.readLine();
                                        if (status.equals("true")) {
                                            storeStatus = true;
                                        } else {
                                            JOptionPane.showMessageDialog(null, "You don't have a store under that name...", "Seller Dashboard",
                                                    JOptionPane.ERROR_MESSAGE);
                                        }
                                    } else if (title.equals("Nothing")) {
                                        writer.write(title);
                                        writer.newLine();
                                        writer.flush();
                                        bool = false;
                                    }
                                } else {
                                    writer.write("");
                                    writer.newLine();
                                    writer.flush();
                                    bool = false;
                                }

                                if (storeStatus) {
                                    for (int i = 0; i < dashboard.size(); ) {
                                        dashboard.remove(i);
                                    }
                                    String line = reader.readLine();
                                    while (!line.isBlank()) {
                                        dashboard.add(line);
                                        line = reader.readLine();
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
                                            writer.write(sort);
                                        } else {
                                            writer.write("");
                                        }
                                        writer.newLine();
                                        writer.newLine();
                                        writer.flush();

                                        for (int i = 0; i < dashboard.size(); ) {
                                            dashboard.remove(i);
                                        }
                                        String line2 = reader.readLine();
                                        while (!line2.isBlank()) {
                                            dashboard.add(line2);
                                            line2 = reader.readLine();
                                        }
                                        if (dashboard.size() == 1) {
                                            again = false;
                                        }
                                    }
                                }
                            }
                        }

                        if (reply.equals("6. delete a store")) {
                            boolean valid = false;
                            do {
                                String input = ""; // TODO get GUI input from user (which store)
                                if (input.equalsIgnoreCase("") || input.equals(null)) {
                                    JOptionPane.showMessageDialog(null, "Not a store", "Stores",
                                            JOptionPane.ERROR_MESSAGE);
                                } else {
                                    valid = true;
                                    writer.write(input);
                                    writer.newLine();
                                    writer.flush();
                                    if (reader.readLine().equals("is store")) {
                                        JOptionPane.showMessageDialog(null, "Stores deleted!", "Stores",
                                                JOptionPane.INFORMATION_MESSAGE);
                                    } else {
                                        JOptionPane.showMessageDialog(null, "Still not a store", "Stores",
                                                JOptionPane.ERROR_MESSAGE);
                                    }
                                } 
                            } while (!valid);
                        }

                        if (reply.equals("7. import stores from a CSV")) {
                            boolean valid = false;
                            do {
                                String input = ""; // TODO get GUI input from user (enter filename)
                                if (input.equalsIgnoreCase("") || input.equals(null)) {
                                    JOptionPane.showMessageDialog(null, "Not a name", "Stores",
                                            JOptionPane.ERROR_MESSAGE);
                                } else {
                                    valid = true;
                                    writer.write(input);
                                    writer.newLine();
                                    writer.flush();
                                    if (reader.readLine().equals("error")) {
                                        System.out.println("aur naur something's wrong with the file"); // TODO
                                    } else {
                                        System.out.println("we good"); // TODO
                                    }
                                }
                            } while (!valid);
                        }

                        if (reply.equals("8. export stores as a CSV")) {
                            boolean valid = false;
                            do { 
                                String input = ""; // TODO user input from gui (enter file to write to)
                                if (input.equalsIgnoreCase("") || input.equals(null)) {
                                    JOptionPane.showMessageDialog(null, "Not a file name idiot", "Stores",
                                            JOptionPane.ERROR_MESSAGE);
                                } else {
                                    valid = true;
                                    writer.write(input);
                                    writer.newLine();
                                    writer.flush();
                                }
                            } while (!valid);
                        }

                        if (reply.equals("9. delete account")) {
                            // Added a string sent to server so that it prompts the corresponding action
                            writer.write(reply);
                            writer.newLine();
                            writer.flush();
                            //
                            
                            JOptionPane.showMessageDialog(null, "Your account has been deleted.",
                                    "bEtsy",
                        JOptionPane.INFORMATION_MESSAGE);
                        runSeller = false;

                        }

                        if (reply.equals("10. log out?")) {
                            // Added a string sent to server so that it prompts the corresponding action
                            writer.write(reply);
                            writer.newLine();
                            writer.flush();
                            //
                            
                            runSeller = false;
                            run = false;
                            JOptionPane.showMessageDialog(null, "Thanks for visiting bEtsy!", "Goodbye",
                                JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                }


            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Thanks for visiting bEtsy!", "Goodbye",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
    }

    public static void searchGUI(Socket socket, BufferedWriter writer, BufferedReader reader, 
        String userName, ObjectInputStream ois, ObjectOutputStream oos) {
        try {
            String chosenProduct;
            int continueQues = 0;
            String searchWord = JOptionPane.showInputDialog(null,
                    "Enter Search", "Search Bar", JOptionPane.QUESTION_MESSAGE); // enter search
            writer.write(searchWord); // sends message
            writer.newLine();
            writer.flush(); // ensure data is sent to the server
            ArrayList<Object> results = (ArrayList<Object>) ois.readObject();
            ArrayList<String> resultString = new ArrayList<>();
            for (Object p : results) {
                Product curr = (Product) p;
                resultString.add(curr.getProductName());
            }
            if (results.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "Error: There's nothing on this bro :((", "Search Bar", JOptionPane.ERROR_MESSAGE);
            } else {
                chosenProduct = (String) JOptionPane.showInputDialog(null,
                        "Select Desired Product", "MarketPlace",
                        JOptionPane.PLAIN_MESSAGE, null, resultString.toArray(new String[0]),
                        null);
                writer.write(chosenProduct);
                writer.newLine();
                writer.flush();
                Product product = (Product) ois.readObject();
                // fix this
                whatToDoWithProduct(product, userName, oos);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * What to do with selected product
     *
     * @param, scanner A scanner so that the method can acess the user interface
     * @return none
     */
    public static void whatToDoWithProduct(Product product, String userName, ObjectOutputStream oos) {
        try {
            int choice = JOptionPane.showConfirmDialog(null, 
                "Add " + product.getProductName() + " to cart? yes or no?", 
                "MarketPlace", JOptionPane.YES_NO_OPTION);
            if (choice == 0) {
                oos.writeObject(product);
                oos.flush();
                // fix this
                // customer.addToCart(userName, product); 
                JOptionPane.showMessageDialog(null, "Item added to cart", "MarketPlace",
                        JOptionPane.INFORMATION_MESSAGE);
            } else{
                oos.writeObject(null);
                oos.flush();
                JOptionPane.showMessageDialog(null, "Sad", "MarketPlace",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
                e.printStackTrace();
                return;
            }
    }
}
