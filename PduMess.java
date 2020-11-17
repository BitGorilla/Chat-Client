import java.nio.ByteBuffer;

/**
 * Course: 5DV167
 * @Author Martin Sjölund, Filip Bark
 * @version 1.0 2016-10-12
 */

/**
 * Class handles message PDU.
 */
public class PduMess extends Pdu {

    private int identityLength;
    private int messageLength;
    private String inMessage;
    private String identity;
    private int timeStamp;

    //read
    public PduMess(byte[] inByteArray) {

        byteArray = inByteArray;
        if (byteArray.length % 4 == 0){
            readByteArray();
            testMess();
        }else {
            byteArray = null;
        }
    }

    //send
    public PduMess(String message) throws Exception{
        code = 10;
        short length = (short)message.getBytes("UTF-8").length;

        builder.append((byte) code).pad();

        //message length
        builder.appendShort(length).pad();

        //timestamp
        builder.append((byte)0).pad();

        //Message
        builder.append(message.getBytes()).pad();

        byteArray = builder.toByteArray();

        //Checksum
        byteArray[3] = Checksum.computeChecksum(byteArray);
    }


    private void readByteArray(){
        try {
            code = byteArray[0];

            identityLength = (int) byteArray[2];

            //message length
            byte[] length = new byte[2];
            for (int i = 0; i < 2; i++) {
                length[i] = byteArray[4 + i];
            }
            messageLength = ByteBuffer.wrap(length).getShort();

            //time stamp
            byte[] time = new byte[4];
            for (int i = 0; i < 4; i++) {
                time[i] = byteArray[8 + i];
            }
            timeStamp = ByteBuffer.wrap(time).get() & 0xff;

            int marker = 12;
            //message
            byte[] message = new byte[messageLength];
            for (int i = 0; i < messageLength; i++) {
                message[i] = byteArray[marker + i];
            }
            inMessage = new String(message);
            marker += messageLength + getPadLength(messageLength);

            //identity
            byte[] name = new byte[identityLength];
            for (int i = 0; i < identityLength; i++) {
                name[i] = byteArray[marker + i];
            }
            identity = new String(name);

        }catch (NullPointerException | ArrayIndexOutOfBoundsException
                | NegativeArraySizeException e){
            byteArray = null;
        }
    }

    private void testMess(){

        try {

            if (Checksum.computeChecksum(byteArray) != 0) {
                byteArray = null;
            } else if (byteArray[1] != 0 || byteArray[6] != 0
                                        || byteArray[7] != 0) {
                byteArray = null;
            }
        }catch(NullPointerException e) {
            byteArray = null;
        }

    }

    @Override
    public void print(){
        System.out.println(identity+": "+ inMessage);
    }

    public int getIdentityLength() {
        return identityLength;
    }

    public int getMessageLength() {
        return messageLength;
    }

    public String getInMessage() {
        return inMessage;
    }

    public String getIdentity() {
        return identity;
    }

    public int getTimeStamp() {
        return timeStamp;
    }

    /**
     *
     * @param nameLength name length of the server meassured
     * @return the number of zero's that is padded after server name
     */
    private int getPadLength(int nameLength){

        if (nameLength % 4 == 0){
            return 4;
        }
        else {
            return 4 - (nameLength % 4);
        }

    }
}
