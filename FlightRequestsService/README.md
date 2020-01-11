# FlightRequestService

Service managing user requests for flights

End-points:
- GET '/' - get all flight requests
- GET '/enabled' - get enabled flight requests
- POST '/add' - add new flight request
- DELETE '/delete/{flightRequestId}' - delete flight request
- PUT '/enable/{flightRequestId}' - enable flight request with specified flightRequestId
- PUT '/disable/{flightRequestId}' - disable flight request with specified flightRequestId