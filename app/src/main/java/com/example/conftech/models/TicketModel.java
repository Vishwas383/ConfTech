package com.example.conftech.models;

public class TicketModel {

    String ticketId,userId,eventId;

    public TicketModel(String ticketId, String userId, String eventId) {
        this.ticketId = ticketId;
        this.userId = userId;
        this.eventId = eventId;
    }

    public TicketModel() {
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
}
