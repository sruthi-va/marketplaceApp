import java.io.*;
import java.util.*;

/**
 * Since object output stream writes headings in all the time,
 * made our own method to not do that, so we can use it multiple times
 *
 * @author Catherine Park
 * @version December 8th, 2022
 */
class MyObjectOutputStream extends ObjectOutputStream {

    public MyObjectOutputStream() throws IOException {
        super();
    }

    public MyObjectOutputStream(OutputStream o) throws IOException {
        super(o);
    }

    @Override
    // method we need to override
    public void writeStreamHeader() throws IOException {
        return;
    }
}