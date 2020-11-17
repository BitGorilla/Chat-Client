import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Course: 5DV167
 * @Author Martin Sjölund, Filip Bark
 * @version 1.0 2016-10-12
 */

/**
 * Class writing a byteArray via output stream
 */
public class PDUOutputStream {

    DataOutputStream stream;

    public PDUOutputStream(OutputStream out){

        stream = new DataOutputStream(out);
    }

    public void writeToServer(byte[] byteArray){

        int length = byteArray.length;

        try {
            stream.write(byteArray, 0, length);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
