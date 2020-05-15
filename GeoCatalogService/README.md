# GeoCatalogMicroservice

Geo Catalog Service provides access to Places that SkyScanner supports:
- continent
- country
- city
- airport

REST API end-points:
- GET '/getChild/{place}' - return list of Airport located in specified place, e.g. country, city