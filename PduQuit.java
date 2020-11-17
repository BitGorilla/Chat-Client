/**
 * Created by Martin Sjölund and Filip Bark on 2016-10-04.
 */
public class PduQuit extends Pdu{

    public PduQuit(){
        code = 11;
        builder.append((byte)code).pad();
        byteArray = builder.toByteArray();
    }

    public PduQuit(byte[] inByteArray){

        byteArray = inByteArray;
        if (byteArray.length % 4 == 0){
            readBytes();
            testPdu();
        }else {
            byteArray = null;
        }
    }

    private void readBytes(){
        try {

            code = byteArray[0];

        }catch (NullPointerException | ArrayIndexOutOfBoundsException
                                    | NegativeArraySizeException e){
            byteArray = null;
        }
    }
    private void testPdu(){
        try {
            if (byteArray[1] != 0 || byteArray[2] != 0 || byteArray[3] != 0) {
                byteArray = null;
            }
        }catch (NullPointerException e){
            byteArray = null;
        }
    }

    @Override
    public void print(){
        System.out.println("Quit");
    }
}
