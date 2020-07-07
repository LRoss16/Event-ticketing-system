package rosslewis1501830cm3113cw19;

import java.util.HashMap;

/**
 * CM3113 Session 2018-19 Starting point for Coursework TicketedEvent.java
 * encapsulated properties of an event that has tickets allocated to customers
 */

//as there could be multiple client guis running at once the methods are synchronized so that only will be able to access them at a time 
public class TicketedEvent {

    private String eventName;
    private int capacity;
    private int numberTicketsRemaining;
    private boolean theEventIsOpen;
    private HashMap<String, Ticket> tickets;

    public TicketedEvent(String name, int cap) {
        this.eventName = name;
        this.capacity = cap;
        this.numberTicketsRemaining = cap;
        this.theEventIsOpen = true;
        tickets = new HashMap();
    }

    synchronized public String getEventName() {
        return eventName;
    }

    synchronized public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    synchronized public int getCapacity() {
        return capacity;
    }

    synchronized public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    synchronized public int getNumberTicketsRemaining() {
        return numberTicketsRemaining;
    }

    synchronized public HashMap<String, Ticket> getTickets() {
        return tickets;
    }

    synchronized public void addTicket(String contact, Ticket ticket) {
        this.tickets.put(contact, ticket);

    }

    synchronized public Ticket getTicket(String contact, int number) {
        Ticket t = new Ticket(contact, number);
        this.tickets.put(contact, t);
        this.numberTicketsRemaining -= number;
        return t;
    }

    synchronized public boolean isOpen() {
        return theEventIsOpen;
    }
}
