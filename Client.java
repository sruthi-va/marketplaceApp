import javax.swing.*;
import java.io.*;
import java.net.*;
public class Client {
    public static void main(String[] args) throws IOException {
        boolean run = true;
        //Socket socket = new Socket("localhost", 4242);
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

                choose = (String) JOptionPane.showInputDialog(null, "Are you a customer or seller?",
                        "Choice?", JOptionPane.QUESTION_MESSAGE,
                        null, userChoice, userChoice[0]);
                if (choose == null) {
                    run = false;
                }
                if (choose.equals("Customer")) {

                }
                if (choose.equals("Seller")) {

                }


            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Thanks for visiting bEtsy!", "Goodbye",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
    }
}
