# FlightRequestService

Service managing user requests for flights.

Flexible search configuration of flight locations is allowed:
- airport - specified airport
- country - any airport in specified country
- continent - any airport in specified continent.

Search configuration of serial poll with shifting period is allowed.

End-points:
- GET '/' - get all flight requests
- GET '/enabled' - get enabled flight requests
- POST '/add' - add new flight request
- DELETE '/delete/{flightRequestId}' - delete flight request
- PUT '/enable/{flightRequestId}' - enable flight request with specified flightRequestId
- PUT '/disable/{flightRequestId}' - disable flight request with specified flightRequestId