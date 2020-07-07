package rosslewis1501830cm3113cw19;

/**
 * CM3113 Session 2018-19 Starting point for Coursework
 * Client.java is client that connects to server to order tickets
 */
import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client implements Runnable {

    private int thePortNo;
    private boolean connected = false;
    private ClientGUI gui;
    private Socket socket;
    private BufferedReader theInput;
    private PrintWriter theOutput;

    public Client(int portNo) {
        thePortNo = portNo;
    }

    public Client(int portNo, ClientGUI g) {
        thePortNo = portNo;
        gui = g;
    }

    public void sendTicketRequest(String name, int num) {
        String message = "ORDER," + num + "," + name;
        while(!this.connected){
            try {
                Thread.sleep(50L);
            } catch (InterruptedException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (this.connected) {        
            gui.updateText("Send order for " + num + " from " + name);

            theOutput.println(message);
            theOutput.flush();
        } else {
            System.out.println("Not connected. Cant send: " + message);
        }
    }

    public void stopClient() throws IOException {
        theOutput.println("BYE");
        theOutput.close();
        theInput.close();
        connected = false;
    }

    public void run() {
        try {
            socket = new Socket("localhost", thePortNo);
            connected = true;
            //System.out.println("connected");
            theInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            theOutput = new PrintWriter(socket.getOutputStream(), true /* auto flush */);

            while (connected) {
                String message = theInput.readLine();
                if (message != null) {
                    processMessage(message);
                }
            }
            System.out.println("Before socket close()");
            socket.close();
        } catch (Exception e) {
            System.out.println("Oh no " + e.toString());
            System.exit(0);
        }
    }

    public void processMessage(String message) {
        String bits[] = message.split(",");
        if (bits[0].toUpperCase().equals("CONFIRM")) {
            gui.updateText("Confirmed Ticket Ref:" + bits[1]
                    + " Number of tickets: " + bits[2]
                    + " Customer: " + bits[3]
            );
            System.out.println(message);
            this.connected = false;
        } else if (bits[0].toUpperCase().equals("UPDATE")) {
            gui.updateText(message);
            System.out.println(message);
        } else if (bits[0].toUpperCase().equals("EVENT")) {
            gui.updateEventDetails(bits[1], bits[2]);
            System.out.println(message);
        } else if (bits[0].toUpperCase().equals("SOLDOUT")) {
            gui.updateText("Event is sold out");
            System.out.println(message);
        } else {
            gui.updateText(message);
            System.out.println(message);
        }
    }

    public boolean isConnected() {
        return connected;
    }
}
