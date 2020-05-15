# FlightRequestMicroservice

Service managing user requests for flights.
Flexible search configuration of flight locations is allowed:
- continent - any airport in specified continent.
- country   - any airport in specified country
- city      - any airport in specified city
- airport   - specified airport
Search configuration of serial poll with shifting period is allowed.

REST API end-points:
- GET '/' - get all flight requests
- GET '/enabled' - get enabled flight requests
- POST '/add' - add new flight request
- DELETE '/delete/{flightRequestId}' - delete flight request
- PUT '/enable/{flightRequestId}' - enable flight request with specified flightRequestId
- PUT '/disable/{flightRequestId}' - disable flight request with specified flightRequestId
- POST '/poll/{flightRequestId}' - process flight request with specified flightRequestId

Communication:
 - requests GeoCatalogMicroservice REST API to get list of airports located in specified city/country/continent.
 - send commands for SkyScannerMicroservice to RabbitMQ queue 'skyscanner-requests' to poll specified flight request.
 - consume SkyScannerMicroservice events from RabbitMQ queue 'skyscanner-events' 
   on changing of the specified flight request polling status.