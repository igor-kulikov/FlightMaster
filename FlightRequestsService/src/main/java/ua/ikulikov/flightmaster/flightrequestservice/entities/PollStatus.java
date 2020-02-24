package ua.ikulikov.flightmaster.flightrequestservice.entities;

public enum PollStatus {
    NEW,
    IN_PROGRESS,
    SUCCESS,
    FAILED_SKYSCANNER,
    FAILED_BAD_REQUEST
}
