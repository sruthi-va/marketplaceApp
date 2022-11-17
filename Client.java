import javax.swing.*;
import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) throws IOException {
        boolean run = true;
        boolean runCustomer = false;
        boolean runSeller = false;
        Socket socket = new Socket("localhost", 4242);
        String userName = "";
        String password = "";
        String userChoice[] = {"Customer", "Seller"};
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
                PrintWriter writer = new PrintWriter(socket.getOutputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                choose = (String) JOptionPane.showInputDialog(null, "Are you a customer or seller?",
                        "Choice?", JOptionPane.QUESTION_MESSAGE,
                        null, userChoice, userChoice[0]);
                if (choose == null) {
                    run = false;
                }
                if (choose.equals("Customer")) {
                    runCustomer = true;
                    while (runCustomer) {
                        writer.write(choose);
                        writer.println();
                        writer.flush();

                        String results = reader.readLine(); //server return a string of the drop down options (1. view store,2. search,3. purchase...) etc
                        //separate by commas

                        String[] dropdown = results.split(",", 0);
                        String reply = (String) JOptionPane.showInputDialog(null, "What is your choice?",
                                "Choice?", JOptionPane.QUESTION_MESSAGE,
                                null, dropdown, dropdown[0]);
                        writer.write(reply); //on server side, read in the line, and have ifs for the corresponding drop down choice
                        writer.println();
                        writer.flush();

                        if (reply.equals("1. view store")) {
                            String storeList = reader.readLine();
                        
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
}
