import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by Martin Sjölund and Filip Bark on 2016-10-04.
 */
public class PduParticipants extends Pdu {

    private int noOfIdentities;
    private short length;
    private List<String> participants = new ArrayList<String>();

    public PduParticipants(byte[] inByteArray){
        byteArray = inByteArray;
        if (byteArray.length % 4 == 0){
            readPdu();
        }else{
            byteArray = null;
        }
    }

    private void readPdu(){
        try {
            code = byteArray[0];

            noOfIdentities = byteArray[1];

            byte[] l = new byte[2];
            for (int i = 0; i < 2; i++) {
                l[i] = byteArray[2 + i];
            }
            length = ByteBuffer.wrap(l).getShort();

            byte[] array = new byte[length];
            for (int i = 0; i < length; i++) {
                array[i] = byteArray[4 + i];
            }

            String nameString = new String(array);
            String[] parts = nameString.split("\0");

            for (int i = 0; i < noOfIdentities; i++) {
                participants.add(parts[i]);
            }
        }catch (NullPointerException | ArrayIndexOutOfBoundsException
                | NegativeArraySizeException e){
            byteArray = null;
        }
    }

    public int getNoOfIdentities() {
        return noOfIdentities;
    }

    public short getLength() {
        return length;
    }

    public List<String> getParticipants() {
        return participants;
    }

    @Override
    public void print(){
        if (noOfIdentities != 0) {
            System.out.println("Participants:");
            for (int i = 0; i < noOfIdentities; i++) {
                System.out.println(participants.get(i));
            }
            System.out.println("\n");
        }else {
            System.out.println("There are no other participants");
        }
    }
}
