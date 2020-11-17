import java.nio.ByteBuffer;

/**
 * Created by Martin Sjölund and Filip Bark on 2016-10-04.
 */
public class PduPJoin extends Pdu {

    private int identityLength;
    private int timeStamp;
    private String identity;

    public PduPJoin(byte[] inByteArray){

        byteArray = inByteArray;
        if (byteArray.length % 4 == 0) {
            readPdu();
            testPdu();
        }else {
            byteArray = null;
        }
    }

    private void readPdu(){
        try {
            code = byteArray[0];

            identityLength = byteArray[1];

            //time stamp
            byte[] time = new byte[4];
            for (int i = 0; i < 4; i++) {
                time[i] = byteArray[4 + i];
            }
            timeStamp = ByteBuffer.wrap(time).getInt();

            //identity
            byte[] name = new byte[identityLength];
            for (int i = 0; i < identityLength; i++) {
                name[i] = byteArray[8 + i];
            }
            identity = new String(name);
        }catch (NullPointerException | ArrayIndexOutOfBoundsException
                | NegativeArraySizeException e){
            byteArray = null;
        }
    }

    private void testPdu(){
        if (byteArray[2] != 0 && byteArray[3] != 0){
            byteArray = null;
        }
    }

    @Override
    public void print(){
        System.out.println(identity + " joined session");
    }

    public int getIdentityLength() {
        return identityLength;
    }

    public int getTimeStamp() {
        return timeStamp;
    }

    public String getIdentity() {
        return identity;
    }
}
