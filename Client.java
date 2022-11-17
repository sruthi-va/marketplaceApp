import javax.swing.*;
import java.io.*;
import java.net.*;
public class Client {
    public static void main(String[] args) throws IOException {
        boolean run = true;
        //ocket socket = new Socket("localhost", 4242);
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

                choose = (String) JOptionPane.showInputDialog(null, "Are you a customer or seller?",
                        "Choice?", JOptionPane.QUESTION_MESSAGE,
                        null, userChoice, userChoice[0]);
                if (choose == null) {
                    run = false;
                }


            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "", "Welcome",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
    }
}
