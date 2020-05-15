# SkyScannerMicroservice

Service interacts with SkyScanner public API

REST API end-points:
 - GET '/show/{flightRequestId}' - displays results of polling flight request
  (only direct and cheapest offers)

Communication:
 - consume FlightRequestMicroservice commands from RabbitMQ queue 'skyscanner-requests' to poll specified flight request.
 - send events to FlightRequestMicroservice to RabbitMQ queue 'skyscanner-events' 
   on changing of the specified flight request polling status.