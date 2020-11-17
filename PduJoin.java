/**
 * Created by Martin Sjölund and Filip Bark on 2016-10-04.
 */
public class PduJoin extends Pdu{

    public PduJoin(String identity) throws Exception{
        code = 12;

        builder.append((byte)code);

        int IDLength = identity.getBytes("UTF-8").length;
        builder.append((byte)IDLength).pad();

        builder.append(identity.getBytes()).pad();

        byteArray = builder.toByteArray();
    }

    @Override
    public void print(){
        System.out.println("Join");
    }
}
