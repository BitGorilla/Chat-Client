import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

/**
 * Course: 5DV167
 * @Author Martin Sjölund, Filip Bark
 * @version 1.0 2016-10-12
 */

/**
 * Class that establishes connection to a name or chat server.
 *
 * Also handles chat commands
 */
public class ChatClient {

    private String nickName;

    private PDUInputStream inStream;
    private PDUOutputStream outStream;
    private Socket socket;
    private boolean done = false;

    /**
     * Constructor for this class. Start name or chat server depending on input
     * @param nick Your nickname used in chat
     * @param choice if you want to connect to name or chat server
     * @param host address to server
     * @param port port to server
     * @throws Exception if connection fails
     */

    public ChatClient(String nick, String choice,
                      String host, int port)throws Exception{

        nickName = nick;

        if (choice.equals("cs")){
            connectToCS(port, host);
        }
        else if (choice.equals("ns")){
            connectToNS(port, host);
        }
    }

    /**
     * Connection setup for name server. Sends a the PDU GetList to server then
     * recieves and prints a list of servers
     *
     * @param port port to server
     * @param host address to server
     * @throws Exception if connection fails
     */

    private void connectToNS(int port, String host)throws Exception{
        boolean gotSList = false;
        socket = new Socket(host, port);
        outStream = new PDUOutputStream(socket.getOutputStream());
        inStream = new PDUInputStream(socket.getInputStream());

        PduGetList getList = new PduGetList();
        outStream.writeToServer(getList.getByteArray());

        while(!gotSList) {
            if (!inStream.streamIsEmpty()) {
                Pdu inPDU = inStream.readPdu();
                socket.close();
                inPDU.print();
                gotSList = true;
            }
        }

        Scanner s = new Scanner(System.in);
        System.out.println("ip-address: ");
        host = s.nextLine();
        checkInput(host);
        System.out.println("Port: ");
        port = s.nextInt();
        checkInput(String.valueOf(port));

        connectToCS(port, host);
    }

    /**
     * Establishes connection to chat server and sends a join PDU.
     * @param port port to server
     * @param host address to server
     * @throws Exception if connection fails
     */
    private void connectToCS(int port, String host)throws Exception{
        socket = new Socket(host, port);
        inStream = new PDUInputStream(socket.getInputStream());
        outStream = new PDUOutputStream(socket.getOutputStream());

        PduJoin join = new PduJoin(nickName);
        outStream.writeToServer(join.getByteArray());

        chatCS();
    }

    /**
     * Chat method för chat server. Contains Two threads; one for sending and
     * one for receiving PDU:s
     */
    private void chatCS(){

        //read
        new Thread(){
            @Override
            public void run(){
                try {

                    while (!done) {

                        if(!inStream.streamIsEmpty()){
                            Pdu inPDU = inStream.readPdu();
                            handlePduIn(inPDU);
                        }
                    }
                    socket.close();
                    Thread.currentThread().interrupt();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();

        //send
        new Thread(){
            @Override
            public void run(){
                try {

                    String input;

                    do {

                        Scanner s = new Scanner(System.in);
                        input = s.nextLine();
                        inputHandler(input);
                    } while (!done);

                    Thread.currentThread().interrupt();
                }catch (Exception e){
                    try {
                        socket.close();
                    }catch (IOException io){
                        io.printStackTrace();
                    }
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * Handles the PDU:s that come from inputStream.
     * @param pdu the PDU sent from inputstream. Null if corrupt.
     */

    private void handlePduIn(Pdu pdu) {
        int code;
        if (pdu != null) {
            code = pdu.code;

            switch (code) {

                //sList
                case 4:
                //Mess
                case 10:
                //pJoin
                case 16:
                //pLeave
                case 17:
                //participants
                case 19:
                    pdu.print();
                    break;

                //quit
                case 11:
                    done = true;
                    System.out.println("Server har avslutat");

                default:
                    System.out.println("Corrupt PDU");
            }
        }
    }

    /**
     * Handles input from user.
     * @param input input string from user in chat
     * @throws Exception mess throws exception
     */
    private void inputHandler(String input) throws Exception{

        switch (input) {

            case "/quit":
                done = true;
                break;

            default:
                PduMess mess = new PduMess(input);
                outStream.writeToServer(mess.getByteArray());
                break;
        }
    }

    /**
     * Checks if port or host input is within valid limits
     * @param input port or host-string
     * @throws Exception if port or host are not within limits
     */
    private void checkInput(String input)throws Exception{

        if (input.getBytes("UTF-8").length > 255){
            throw new IOException("Message too long");

        }
        try {
            if (Integer.parseInt(input) < 0 || Integer.parseInt(input) > 65535) {
                throw new IOException("Port not valid");
            }
        }catch (NumberFormatException e){
            //nothing should happen
        }
    }


    /**
     * initiates ChatClient
     * @param args nickname, server choice, host and port
     * @throws Exception connection to servers fail
     */
    public static void main(String args[])throws Exception{

        ChatClient client = new ChatClient(args[0], args[1], args[2],
                                            Integer.parseInt(args[3]));

    }
}
