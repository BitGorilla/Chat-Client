import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Course: 5DV167
 * @Author Martin Sjölund, Filip Bark
 * @version 1.0 2016-10-12
 */

/**
 * Reads bytes from inputstream
 */
public class PDUInputStream {

    private List<Byte> byteList = new ArrayList<>();
    private byte[] byteArray;
    private DataInputStream dis;
    private InputStream stream;

    public PDUInputStream(InputStream inputStream) throws IOException {

        dis = new DataInputStream(inputStream);
        this.stream = inputStream;
    }

    /**
     * @return The next PDU in the stream.
     * @throws java.io.IOException  If the stream closed with an error.
     * @throws  java.io.EOFException If the stream closed without an error.
     */
    public Pdu readPdu() throws IOException, EOFException {
        int code = 0;
        boolean hasRead = true;

        try {
            if (!streamIsEmpty()) {

                int PDULength;
                code = (int) dis.readByte();

                switch (code) {

                    //quit
                    case 11:
                        byteList.add((byte) 11);
                        for (int i = 1; i < 4; i++) {
                            byteList.add(dis.readByte());
                        }
                        break;

                    //pJoin
                    case 16:
                        PDULength = 8;
                        byteList.add((byte) 16);

                        for (int i = 1; i < PDULength; i++) {
                            byteList.add(dis.readByte());
                            if (i == 1) {
                                int l = (int) byteList.get(i);
                                PDULength += l + getPadLength(l);
                            }
                        }
                        break;

                    //pLeave
                    case 17:
                        PDULength = 8;
                        byteList.add((byte) 17);
                        for (int i = 1; i < PDULength; i++) {
                            byteList.add(dis.readByte());
                            if (i == 1) {
                                int l = (int) byteList.get(i);
                                PDULength += l + getPadLength(l);
                            }
                        }
                        break;

                    //mess
                    case 10:
                        byte[] messLength = new byte[2];
                        PDULength = 12;
                        byteList.add((byte) 10);
                        for (int i = 1; i < PDULength; i++) {
                            byteList.add(dis.readByte());

                            if (i == 2) {
                                int l = (int) byteList.get(i);
                                PDULength += l + getPadLength(l);
                            }
                            if (i == 4) {
                                messLength[0] = byteList.get(i);
                            }
                            if (i == 5) {
                                messLength[1] = byteList.get(i);
                                short l = ByteBuffer.wrap(messLength).getShort();
                                PDULength += l + getPadLength(l);
                            }

                        }
                        break;

                    //participants
                    case 19:
                        byte[] length = new byte[2];
                        PDULength = 4;
                        byteList.add((byte) 19);
                        for (int i = 1; i < PDULength; i++) {
                            byteList.add(dis.readByte());
                            if (i == 2) {
                                length[0] = byteList.get(i);
                            }
                            if (i == 3) {
                                length[1] = byteList.get(i);
                                short l = ByteBuffer.wrap(length).getShort();
                                PDULength += l + getPadLength(l);
                            }
                        }
                        break;

                    //sList
                    case 4:
                        PDULength = 12;
                        byteList.add((byte) 4);
                        for (int i = 1; i < PDULength; i++) {
                            byteList.add(dis.readByte());
                            if (i == 11 || (i+1) % PDULength == 0 && i != 1) {
                                int l = (int) byteList.get(i);
                                PDULength += l + getPadLength(l) + 8;
                            }
                        }
                        break;

                    default:
                        hasRead = false;
                        break;
                }
            }
        }catch (EOFException e){
            //Stream closed without error
        }

        if (hasRead) {
            try {
                byteArray = new byte[byteList.size()];
                for (int i = 0; i < byteList.size(); i++) {
                    byteArray[i] = byteList.get(i);
                }
                byteList.clear();

            } catch (NullPointerException e) {
                byteArray = null;
            }
        }
        return whichPdu(code);
    }

    private Pdu whichPdu(int code) {

        try {
            switch (code) {

                case 4:
                    try {
                        PduSList sList = new PduSList(byteArray);
                        if (sList.getByteArray() != null){
                            return sList;
                        }else {
                            return null;
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                case 10:
                    PduMess mess = new PduMess(byteArray);
                    if (mess.getByteArray() != null){
                        return mess;
                    }else {
                        return null;
                    }

                case 11:
                    PduQuit quit = new PduQuit(byteArray);

                    if (quit.getByteArray() != null){
                        return quit;
                    }else {
                        return null;
                    }

                case 16:
                    PduPJoin pJoin = new PduPJoin(byteArray);
                    if (pJoin.getByteArray() != null){
                        return pJoin;
                    }else {
                        return null;
                    }

                case 17:
                    PduPLeave pLeave = new PduPLeave(byteArray);

                    if (pLeave.getByteArray() != null){
                        return pLeave;
                    }else {
                        return null;
                    }

                case 19:
                    PduParticipants participants=new PduParticipants(byteArray);
                    if (participants.getByteArray() != null){
                        return participants;
                    }else {
                        return null;
                    }

                default:
                    return null;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    public boolean streamIsEmpty() throws IOException{
        try {
            return stream.available() == 0;
        }catch (IOException e){
            return true;
        }
    }

    /**
     *
     * @param length name length of the server meassured
     * @return the number of zero's that is padded after server name
     */
    private int getPadLength(int length){

        if (length % 4 == 0){
            return 4;
        }
        else {
            return 4 - (length % 4);
        }

    }
}