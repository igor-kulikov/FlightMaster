# FlightMaster 

Microservices based application sourcing SkyScanner public API for flights pricing data as per user requests.

## Components
 - `FlightRequestService` - Service managing user requests for flight
 - `GeoCatalogService` - Service provides access to Places that SkyScanner supports
 - `SkyScannerService` - Service interracts with SkyScanner public API
 - `EurekaService` - Service Registration and Discovery
 - `ZuulService` - Routing