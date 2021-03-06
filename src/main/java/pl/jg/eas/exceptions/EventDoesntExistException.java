package pl.jg.eas.exceptions;

public class EventDoesntExistException extends RuntimeException {

    private final Long eventId;

    public EventDoesntExistException(Long eventId) {
        this.eventId = eventId;
    }

    @Override
    public String getMessage() {
        return String.format("Event identified by %d doesn't exist.", eventId);
    }

    public Long getEventId() {
        return eventId;
    }

    @Override
    public String toString() {
        return "EventDoesntExistException{" +
                "eventId=" + eventId +
                '}';
    }
}
