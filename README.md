# FlightMaster 

Microservices based application sourcing SkyScanner public API for flights pricing data as per user requests.

## Components
 - `FlightRequestService` - Service managing user requests for flight
 - `FlightPollService` - Service manages polls as per user requests for flight
 - `GeoCatalogService` - Service provides access to Places that SkyScanner supports
 - `SkyScannerService`
 - `EurekaService` - Service Registration and Discovery