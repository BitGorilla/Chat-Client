/**
 * Course: 5DV167
 * @Author Martin Sjölund, Filip Bark
 * @version 1.0 2016-10-12
 */

/**
 *  Abstract class, contains OP-code, byteArray and a abstract print-method
 */
public abstract class Pdu {

    protected int code;
    protected byte[] byteArray;
    protected ByteSequenceBuilder builder = new ByteSequenceBuilder();


    protected byte[] getByteArray(){
        return byteArray;
    }

    abstract public void print();

}
