package rosslewis1501830cm3113cw19;

import java.io.*;
import java.net.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * CM3113 Session 2018-19 Starting point for Coursework
 * ConnectionHandler.java communicates with client to perform ticket transaction
 */
public class ConnectionHandler extends Thread 
{

    private Server server;
    private Socket incoming;
    private BufferedReader theInput;
    private PrintWriter theOutput;
    private TicketedEvent theEvent;
    boolean go = true;

    /**
     * Creates a new instance of Class
     */
    public ConnectionHandler(Server serv, Socket incoming, TicketedEvent event){
        server = serv;
        this.incoming = incoming;
        this.theEvent = event;
        this.start(); //changed run() to start() so if more than one thread calls it, their run method is executed by separate threads
    }

    public void close() {
        go = false;
    }

    public void sendToClient(String s) {
        theOutput.println(s);
    }

    public void run() {
        String remoteIPAddress = incoming.getLocalAddress().getHostName()
                + ":" + incoming.getLocalPort();
        LocalDateTime bidDateTime = LocalDateTime.now();
        try {
            // set up streams for bidirectional transfer across connection socket
            theInput = new BufferedReader(new InputStreamReader(incoming.getInputStream()));
            theOutput = new PrintWriter(incoming.getOutputStream(), true /* auto flush */);
            // acknowledge connection
            sendToClient("You are connected to ticket server: "
                    + remoteIPAddress + " at date / time " + bidDateTime + " \n");

            if (theEvent.isOpen()) {
                sendToClient("EVENT" 
                        + "," + theEvent.getNumberTicketsRemaining()
                        + "," + theEvent.getEventName());
            }

            while (go) {
                if (theEvent.isOpen()) {
                    // read bid line and confirmation line
                    String line = theInput.readLine().trim();
                    System.out.println(line);
                    if (line.length() > 0) {
                        processMessage(line);
                    }
                }
                theOutput.flush();
            }
            theInput.close();
            theOutput.close();
            incoming.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void processMessage(String message) {
        String bits[] = message.split(",");
        if (bits[0].toUpperCase().equals("ORDER")) {
            int numberTicketsRequested = Integer.parseInt(bits[1]);
            String contactName = bits[2];
            if (numberTicketsRequested <= theEvent.getNumberTicketsRemaining()) {
                Ticket ticket = theEvent.getTicket(contactName, numberTicketsRequested);
                String reply = "Ticket issued to: " + contactName + " for " + numberTicketsRequested + " people "
                        + incoming.getInetAddress().getHostName()
                        + " on port " + incoming.getPort()
                        + " at " + LocalDateTime.now();
                System.out.println(reply);
                this.server.gui.updateHistory(reply);
                sendToClient("CONFIRM" 
                        + "," + ticket.getTicketRef()
                        + "," + ticket.getNumberOfPeople()
                        + "," + ticket.getContactName()
                );
                this.server.gui.setTicketsRemaining(theEvent.getNumberTicketsRemaining());
                this.server.gui.setCustomersCount(theEvent.getTickets().size());
                this.server.gui.addToList(""+ticket.getTicketRef()+" - "+ticket.getContactName()+" - "+ticket.getNumberOfPeople());
            } 
            
            else {
                sendToClient("SOLDOUT");
            }
            go = false;
        } else if (bits[0].toUpperCase().equals("ENQUIRE")) {
            sendToClient("UPDATE" 
                    + "," + theEvent.getNumberTicketsRemaining()
                    + "," + theEvent.getEventName()
            );
        } else {
            System.out.println(message);
            this.server.gui.updateHistory(message);
        }
    }
}
