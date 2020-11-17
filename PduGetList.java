/**
 * Created by Martin Sjölund and Filip Bark on 2016-10-04.
 */
public class PduGetList extends Pdu {

    public PduGetList(){
        code = 3;
        builder.append((byte)3).pad();
        byteArray = builder.toByteArray();
    }

    @Override
    public void print(){
        System.out.println("GetList");
    }
}
