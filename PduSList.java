import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Course: 5DV167
 * @Author Martin Sjölund, Filip Bark
 * @version 1.0 2016-10-12
 */

/**
 * Class for server list PDU. Translates byte array to printable attributes
 */
public class PduSList extends Pdu{

    private int noOfServers;
    private int serverNameLength;

    private List<InetAddress> addressList = new ArrayList<>();
    private List<Integer> portList = new ArrayList<>();
    private List<Integer> clientNumberList = new ArrayList<>();
    private List<String> serverNameList = new ArrayList<>();

    public PduSList(byte[] inByteArray) throws Exception{

        byteArray = inByteArray;
        if (byteArray.length % 4 == 0){
            buildByteArray();
            testPdu();
        }else {
            byteArray = null;
        }

    }

    private void buildByteArray(){
        try {
            code = byteArray[0];

            byte[] number = new byte[2];
            for (int i = 0; i < 2; i++) {

                number[i] = byteArray[2 + i];
            }
            noOfServers = ByteBuffer.wrap(number).getShort();

            //marker keeping track
            int marker = 4;
            //add servers to list
            for (int i = 0; i < noOfServers; i++) {

                //add address to list
                byte[] address = new byte[4];
                for (int j = 0; j < 4; j++) {
                    address[j] = byteArray[j + marker];
                }
                addressList.add(InetAddress.getByAddress(address));
                marker += 4;

                //add port number to list
                byte[] portNumber = new byte[2];
                for (int j = 0; j < 2; j++) {

                    portNumber[j] = byteArray[j + marker];
                }
                int port = ByteBuffer.wrap(portNumber).getShort();
                portList.add(port);
                marker += 2;

                //add number of clients connected to server to list;
                clientNumberList.add((int) byteArray[marker]);
                marker++;

                //add server name length to Integer
                serverNameLength = (int) byteArray[marker];
                marker++;

                //add server name to list
                byte[] name = new byte[serverNameLength];
                for (int j = 0; j < serverNameLength; j++) {

                    name[j] = byteArray[j + marker];
                }
                String nameString = new String(name);
                serverNameList.add(nameString);

                //add server name-length and pad to marker
                marker += serverNameLength + getPadLength(serverNameLength);
            }
        }catch (NullPointerException | ArrayIndexOutOfBoundsException
                | NegativeArraySizeException | UnknownHostException e){
            byteArray = null;
        }
    }

    private void testPdu(){
        try {
            if (byteArray[1] != 0) {
                byteArray = null;
            }
        }catch (NullPointerException e){
            byteArray = null;
        }
    }

    public int getNoOfServers(){
        return noOfServers;
    }

    public List getAddressList(){
        return addressList;
    }

    public List getPortList(){
        return portList;
    }

    public List getClientNumberList(){
        return clientNumberList;
    }

    public List getServerNameList(){
        return serverNameList;
    }

    public int getServerNameLength(){
        return serverNameLength;
    }

    @Override
    public void print(){

        for (int i= 0; i < noOfServers; i++){

            System.out.println(serverNameList.get(i)+":\n"
                    +portList.get(i)+"\n"+ getAddressList().get(i));
        }
    }

    /**
     *
     * @param nameLength name length of the server measured
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
