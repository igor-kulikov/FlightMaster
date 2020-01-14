drop schema if exists SKY_SCANNER cascade;
create schema SKY_SCANNER;

------------------------------------------------------------------
create table SKY_SCANNER.FLIGHT_REQUEST_POLLS (
    POLL_ID bigserial primary key,
    POLL_ID_EXT bigint not null,
    COUNTRY text not null,
    CURRENCY text not null,
    LOCALE text not null,
    OUTBOUND_AIRPORT text not null,
    INBOUND_AIRPORT text,
    OUTBOUND_DATE date not null,
    INBOUND_DATE date,
--  CABIN_CLASS text,       -- Can be 'Economy', 'PremiumEconomy', 'Business', 'First'
    ADULTS int not null,
    CHILDREN int not null,
    INFANTS int not null,
--  INCLUDE_CARRIERS,       -- Only return results from those carriers. Comma-separated list of carrier ids
--  EXCLUDE_CARRIERS,       -- Filter out results from those carriers. Comma-separated list of carrier ids
    GROUP_PRICING boolean,       -- If set to true, prices will be obtained for the whole passenger group
                                                -- and if set to false it will be obtained for one adult.
    REQUEST_RECEIVED_DT timestamp not null,
    POLL_STATUS text,
    POLL_STATUS_DT timestamp
--  RAW_RESPONSE json not null
);

------------------------------------------------------------------
create table SKY_SCANNER.REF_AGENTS (
    AGENT_ID int not null primary key,
    NAME text not null,
    TYPE text not null,
    STATUS text not null
);

create table SKY_SCANNER.REF_CARRIERS (
    CARRIER_ID int not null primary key,
    NAME text not null
);

create table SKY_SCANNER.REF_PLACES (
    PLACE_ID int not null primary key,
    CODE text not null,
    TYPE text not null,
    NAME text not null
);

------------------------------------------------------------------
create table SKY_SCANNER.LEGS (
    LEG_ID text primary key,
    JOURNEY_MODE text not null,
    ORIGIN_STATION_PLACE_ID int not null,
    DEPARTURE_DT timestamp,
    DESTINATION_STATION_PLACE_ID int not null,
    ARRIVAL_DT timestamp,
    DURATION_MM int,
    foreign key (ORIGIN_STATION_PLACE_ID) references SKY_SCANNER.REF_PLACES,
    foreign key (DESTINATION_STATION_PLACE_ID) references SKY_SCANNER.REF_PLACES
);

create table SKY_SCANNER.ITINERARIES (
    ITINERARY_ID serial primary key,
    OUTBOUND_LEG_ID text not null,
    INBOUND_LEG_ID text,
    foreign key (OUTBOUND_LEG_ID) references SKY_SCANNER.LEGS,
    foreign key (INBOUND_LEG_ID) references SKY_SCANNER.LEGS,
    unique (OUTBOUND_LEG_ID, INBOUND_LEG_ID)
);

create table SKY_SCANNER.POLL_ITINERARIES (
    POLL_ID int not null,
    ITINERARY_ID int not null,
    foreign key (POLL_ID) references SKY_SCANNER.FLIGHT_REQUEST_POLLS,
    foreign key (ITINERARY_ID) references SKY_SCANNER.ITINERARIES
);

create table SKY_SCANNER.LEG_CARRIERS (
    LEG_ID text not null,
    CARRIER_ID int not null,
    foreign key (LEG_ID) references SKY_SCANNER.LEGS,
    foreign key (CARRIER_ID) references SKY_SCANNER.REF_CARRIERS,
    unique (LEG_ID, CARRIER_ID)
);

create table SKY_SCANNER.LEG_STOPS (
    LEG_ID text not null,
    STOP_PLACE_ID int not null,
    foreign key (LEG_ID) references SKY_SCANNER.LEGS,
    foreign key (STOP_PLACE_ID) references SKY_SCANNER.REF_PLACES,
    unique (LEG_ID, STOP_PLACE_ID)
);

create table SKY_SCANNER.PRICE_OPTIONS (
    PRICE_OPTION_ID serial primary key,
    ITINERARY_ID int not null,
    PRICE int not null,
    QUOTE_AGE_MM int not null,
    DEEPLINK_URL VARCHAR(4000) not null,
    foreign key (ITINERARY_ID) references SKY_SCANNER.ITINERARIES,
    unique (ITINERARY_ID, PRICE, QUOTE_AGE_MM, DEEPLINK_URL)
);

create table SKY_SCANNER.PRICE_OPTION_AGENTS (
    PRICE_OPTION_ID int not null,
    AGENT_ID int not null,
    foreign key (PRICE_OPTION_ID) references SKY_SCANNER.PRICE_OPTIONS,
    foreign key (AGENT_ID) references SKY_SCANNER.REF_AGENTS,
    unique (PRICE_OPTION_ID, AGENT_ID)
);