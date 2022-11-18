import javax.swing.*;
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

                do {
                    userName = JOptionPane.showInputDialog(null, "Please enter username.",
                            "Welcome", JOptionPane.QUESTION_MESSAGE);
                } while (userName.isEmpty());
                if (userName == null) {
                    run = false;
                } else {
                    do {
                        password = (JOptionPane.showInputDialog(null, "Please enter password.",
                                "Welcome", JOptionPane.QUESTION_MESSAGE));

                    } while (password.isEmpty());
                }
                if (password == null) {
                    run = false;
                }
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                choose = (String) JOptionPane.showInputDialog(null, "Are you a customer or seller?",
                        "Choice?", JOptionPane.QUESTION_MESSAGE,
                        null, userChoice, userChoice[0]);
                if (choose == null) {
                    run = false;
                }
                if (choose.equals("Customer")) {
                    runCustomer = true;
                    writer.write(choose);
                    writer.newLine();
                    writer.flush();

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
                            // continue view store GUI here
                            //display all store using a dropdown, ask user which one they want to view
                            //once user chooses a store, display another dropdown with all the items in the store
                            //ask user if they want to purchase anything.. click on the one they want to purchase to add to cart
                            //hit cancel to go back

                        }

                        if (reply.equals("2. search")) {
                            // rin's code go here

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

    public Product searchGUI(Socket socket, BufferedWriter writer, BufferedReader reader) {
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
                return (Product) ois.readObject();
            }
        } catch (Exception e) {
            e.printStackTrace();
    }
        return null;
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
}
