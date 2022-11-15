import org.junit.Test;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

/**
 * This program contains test cases for the logging in part of the MarketPlace.java
 * and the search method in MarketPlace.java
 *
 * @author Jennifer Wang
 * @version November 14, 2022
 */
public class testMain {

    private final PrintStream originalOutput = System.out;
    private final InputStream originalSysin = System.in;

    @SuppressWarnings("FieldCanBeLocal")
    private ByteArrayInputStream testIn;

    @SuppressWarnings("FieldCanBeLocal")
    private ByteArrayOutputStream testOut;

    @Before
    public void outputStart() {
        testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));
    }

    @After
    public void restoreInputAndOutput() {
        System.setIn(originalSysin);
        System.setOut(originalOutput);
    }

    private String getOutput() {
        return testOut.toString();
    }

    @SuppressWarnings("SameParameterValue")
    private void receiveInput(String str) {
        testIn = new ByteArrayInputStream(str.getBytes());
        System.setIn(testIn);
    }

    @Test(timeout = 1000)
    //test logging in with existing account, test pass
    public void testMarketPlaceOutput() {
        String input = "1\ntestUser\ntestPassword";
        String expected = "Welcome to the Marketplace! Please login.\n" +
                "1. Customer or 2. Seller? (Type 1-2)\n" +
                "Username: \n" + "Password: \n" + "Logged in!\n" +
                "1: Midnights by user Taylor Swift\n" +
                "2: Folklore by user Taylor Swift\n" +
                "3: Drivers License by user Olivia Rodrigo\n" +
                "4: D2 by user Agust D\n" +
                "5: Agust D by user Agust D\n" +
                "6: Hellevator by user Stray kids\n" +
        "Do you want to: 1. view store, 2. search, 3. purchase, 4. edit cart, 5. view cart, 6. view statistics, 7. delete account, or 8. logout?\n" +
                "(Type 1-8)\n";

        receiveInput(input);
        try {
            MarketPlace.main(new String[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String actual = getOutput();
        actual = actual.replace("\r\n", "\n");

        Assert.assertEquals("Verify that the output matches!", expected, actual);
    }



    @Test(timeout = 1000)
    //test search for marketplace, test fail
    public void testListStore() {
        MarketPlace marketPlace = new MarketPlace();
        String input = "ready for it?";
        String expected = "There's nothing on this bro :((\n";

        receiveInput(input);
        try {
            marketPlace.search(input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String actual = getOutput();
        actual = actual.replace("\r\n", "\n");

        Assert.assertEquals("Verify that the output matches!", expected, actual);
    }

}


