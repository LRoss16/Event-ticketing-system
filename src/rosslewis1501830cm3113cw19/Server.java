package rosslewis1501830cm3113cw19;

import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * CM3113 Session 2018-19 Starting point for Coursework
 * Server.java monitors a ServerSocket and creates Server Threads to handle client connections
 */
public class Server extends Thread {

    private ServerSocket serversocket;
    private int thePortNo;
    private TicketedEvent theEvent;
    private boolean theStopFlag;
    private ArrayList<ConnectionHandler> connections;
    public ServerGUI gui;

    public Server(int portNo, TicketedEvent event, ServerGUI gui) {
        // share references to all the shared data
        thePortNo = portNo;
        theEvent = event;
        // create data for managing server and set of connections
        theStopFlag = false;
        connections = new ArrayList<>();
        this.gui = gui;
    }

    public void startServer() {
        this.start();
    }

    public void stopServer() throws IOException {
        theStopFlag = true;
        for(ConnectionHandler handler: connections) {
            handler.go = false;
        }
        this.gui.updateHistory("Server Stopped at time: "+LocalDateTime.now());
        this.gui.updateHistory("Event Name: "+theEvent.getEventName()+", Ticket Capacity: "+theEvent.getCapacity());
        this.gui.updateHistory("Tickets remaining: "+theEvent.getNumberTicketsRemaining());
        this.gui.updateHistory("# of Customers booked tickets: "+theEvent.getTickets().size());
        this.gui.updateHistory("---------------------------------");
        serversocket.close();
    }
    
    public int numberConnections(){
        return connections.size();
    }

    public void run() {
        try {
            serversocket = new ServerSocket(thePortNo);
            this.gui.updateHistory("Server started at time: "+LocalDateTime.now());
            while (!theStopFlag) {
                // listen for a connection request on serversocket
                // incoming is the connection socket
                Socket incoming = serversocket.accept();
                // start new thread to service client
                ConnectionHandler conn = new ConnectionHandler(this, incoming, theEvent);
                this.gui.updateHistory("Connected to:" + incoming);
                connections.add(conn);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//    
//    public static void main(String[] args) {
//        TicketedEvent event = new TicketedEvent("Test event", 1000);
//        Server server = new Server(8189, event);
//        server.start();
//    }
}
