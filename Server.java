// "localhost" is the host name, and '375' is the port number.
import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Server {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(375);
        Socket socket;
        try {
            socket = serverSocket.accept();

        } catch (ConnectException ce) {
            JOptionPane.showMessageDialog(null, "Connection Not Established!",
                    "Not Connected!", JOptionPane.ERROR_MESSAGE);
        }
        JOptionPane.showMessageDialog(null, "Connection Established!", "Connected!",
                JOptionPane.INFORMATION_MESSAGE);

        try {
            int reply;
            do {
                String search = null;
                search = JOptionPane.showInputDialog(null, "Enter your search text!",
                        "Enter Search", JOptionPane.QUESTION_MESSAGE).toLowerCase();
                if (!(search.equals(null))) {
                    ArrayList<String> found = new ArrayList<>();
                    BufferedReader br = new BufferedReader(new FileReader(new File ("searchDatabase.txt")));
                    String line;
                    String lowerLine;
                    String title;
                    while ((line = br.readLine()) != null) {
                        lowerLine = line.toLowerCase();
                        if (lowerLine.contains(search)) {
                            title = line.substring(line.indexOf(";") + 1);
                            title = title.substring(0, title.indexOf(";"));
                            found.add(title);
                        }

                    }
                    br.close();
                    if (found.size() > 0) {
                        String foundList[] = new String[found.size()];
                        for (int i = 0; i < found.size(); i++) {
                            foundList[i] = found.get(i);
                        }
                        String userChoice = (String) JOptionPane.showInputDialog(null,
                                "What is your choice?", "Choice?", JOptionPane.QUESTION_MESSAGE, null,
                                foundList, foundList[0]);
                        BufferedReader newReader = new BufferedReader(new FileReader(new File ("searchDatabase.txt")));
                        String lineFinder;
                        String desc = "";
                        while ((lineFinder = newReader.readLine()) != null) {
                            if (lineFinder.contains(userChoice)) {
                                desc = lineFinder;
                                desc = desc.substring(desc.indexOf(";") + 1);
                                desc = desc.substring(desc.indexOf(";") + 1);
                            }
                        }
                        JOptionPane.showMessageDialog(null, desc, "Description",
                                JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null,
                                "Search did not return any results!", "Search Not Found!",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
                reply = JOptionPane.showConfirmDialog(null, "Would you like to search again?",
                        "Search Again?", JOptionPane.YES_NO_OPTION);
            } while (reply == 0);
            JOptionPane.showMessageDialog(null, "Thank you for using the application!",
                    "Farewell", JOptionPane.PLAIN_MESSAGE);
        } catch (NullPointerException npe) {
            JOptionPane.showMessageDialog(null, "The application was cancelled!",
                    "Application Cancelled!", JOptionPane.ERROR_MESSAGE);
        }
    }
}