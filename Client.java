import javax.swing.*;
import javax.swing.text.DefaultEditorKit.InsertBreakAction;

import java.io.*;
import java.net.*;
import java.util.*;

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
        while (run) {
            try {
                int cancel = JOptionPane.showOptionDialog(null, "Welcome to bEtsy!", "Welcome",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.INFORMATION_MESSAGE, null, null, null);
                if (cancel == JOptionPane.CLOSED_OPTION) {
                    return;
                }

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                choose = (String) JOptionPane.showInputDialog(null, "Are you a customer or seller?",
                        "Choice?", JOptionPane.QUESTION_MESSAGE,
                        null, userChoice, userChoice[0]);
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
                                "Please Enter X and Y Values", JOptionPane.OK_CANCEL_OPTION);
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
                        cancel = JOptionPane.showOptionDialog(null, "Successfully logged in!", "Success",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.INFORMATION_MESSAGE, null, null, null);
                        if (cancel == JOptionPane.CLOSED_OPTION) {
                            return;
                        }
                        break;
                    } else {
                        Object[] options1 = {"Create New Account", "Try Again"};
                        cancel = JOptionPane.showOptionDialog(null, 
                            "Not a valid username or password! Create a new account or try again?", 
                            "Error", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, 
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
                    //Customer customer = new Customer(userName, password); //HELP PLEASE
                    runCustomer = true;

                    String results = reader.readLine(); // server return a string of the drop down options (1. view
                                                        // store,2. search,3. purchase...) etc
                    // separate by commas
                    while (runCustomer) {
                        String[] dropdown = results.split(",", 0);
                        String reply = (String) JOptionPane.showInputDialog(null, "What is your choice?",
                                "Choice?", JOptionPane.QUESTION_MESSAGE,
                                null, dropdown, dropdown[0]);
                        writer.write(reply); // on server side, read in the line, and have ifs for the corresponding
                                             // drop down choice
                        writer.newLine();
                        writer.flush();

                        if (reply.equals("1. view store")) {
                            String storeList = reader.readLine(); // server return a string of all the store nanes
                                                                  // separated by commas

                            String[] chooseStore = storeList.split(",", 0);
                            String viewStore = (String) JOptionPane.showInputDialog(null, "Which store do you " +
                                            "want to view?",
                                    "View Stores?", JOptionPane.QUESTION_MESSAGE,
                                    null, chooseStore, chooseStore[0]);
                            writer.write(viewStore);
                            writer.newLine();
                            writer.flush();

                            String productList = reader.readLine();
                            String[] storeProducts = productList.split(",", 0);
                            String chooseProduct = (String) JOptionPane.showInputDialog(null, "Click on " +
                                            "the product you want to buy.",
                                    "Store's Product List", JOptionPane.QUESTION_MESSAGE,
                                    null, storeProducts, storeProducts[0]);

                            if (chooseProduct == null) {
                                runCustomer = true;
                            } else {
                                writer.write(chooseProduct);
                                writer.newLine();
                                writer.flush();
                            }

                        }

                        if (reply.equals("2. search")) {
                            //searchGUI(socket, writer, reader, customer, userName);
                        }

                        if (reply.equals("3. purchase")) {
                            int display = JOptionPane.showOptionDialog(null, "Your items have been bought!",
                                    "Shopping Cart",
                                    JOptionPane.DEFAULT_OPTION,
                                    JOptionPane.INFORMATION_MESSAGE, null, null, null);
                            if (display == JOptionPane.CLOSED_OPTION) {
                                return;
                            }

                        }
                        
                        if (reply.equals("4. edit cart")) {
                            //display cart, ask user which item they want to delete using a textfield
                        }

                    }
                    //*/
                }
                if (choose.equals("Seller")) {
                    runSeller = true;
                    while (runSeller) {

                    }
                }


            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Thanks for visiting bEtsy!", "Goodbye",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
    }

    public static void searchGUI(Socket socket, BufferedWriter writer, BufferedReader reader, Customer customer, String userName) {
        try {
            ObjectInputStream ois = new ObjectInputStream(new DataInputStream(socket.getInputStream()));
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
                chosenProduct = (String) JOptionPane.showInputDialog(null, "Select Desired Product", "MarketPlace",
                        JOptionPane.PLAIN_MESSAGE, null, resultString.toArray(new String[0]), null);
                writer.write(chosenProduct);
                writer.newLine();
                writer.flush();
                Product product = (Product) ois.readObject();
                whatToDoWithProduct(customer, product, userName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * What to do with selected product
     *
     * @param scanner A scanner so that the method can acess the user interface
     * @return none
     */
    public static void whatToDoWithProduct(Customer customer, Product product, String userName) {
        int choice = JOptionPane.showConfirmDialog(null, 
            "Add " + product.getProductName() + " to cart? yes or no?", 
            "MarketPlace", JOptionPane.YES_NO_OPTION);
        if (choice == 0) {
            customer.addToCart(userName, product);
            JOptionPane.showMessageDialog(null, "Item added to cart", "MarketPlace", JOptionPane.INFORMATION_MESSAGE);
        } else{
            JOptionPane.showMessageDialog(null, "Sad", "MarketPlace", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
