drop schema if exists GEO_CATALOG cascade;
create schema GEO_CATALOG;

create table GEO_CATALOG.REF_GEOCATALOG (
    ID smallserial primary key,
    CONTINENT text not null,
    COUNTRY_CODE text not null,
    COUNTRY_NAME text not null,
    CITY_CODE text not null,
    CITY_NAME text not null,
    CITY_POS text not null,
    AIRPORT_CODE text not null,
    AIRPORT_NAME text not null,
    AIRPORT_POS text not null,
    CREATE_DATE timestamp,
    MODIFY_DATE timestamp,
    UNIQUE(AIRPORT_CODE));