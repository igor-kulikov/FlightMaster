# SkyScannerService

Service interacts with SkyScanner public API

End-points:
- POST '/poll' - poll SkyScanner API as per given flight request
- GET '/show/{flightRequestId}' - displays results of polling flight request
  (only direct and cheapest offers)