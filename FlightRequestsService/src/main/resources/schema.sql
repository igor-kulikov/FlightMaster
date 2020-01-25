drop schema if exists FLIGHT_REQUEST_SERVICE cascade;
create schema FLIGHT_REQUEST_SERVICE;

create table FLIGHT_REQUEST_SERVICE.FLIGHT_REQUESTS (
    REQUEST_ID bigserial primary key,
    COUNTRY text not null default 'UA',
    CURRENCY text not null default 'USD',
    LOCALE text not null default 'en-US',
    ORIGIN_PLACE text not null,
    DESTINATION_PLACE text not null,
    OUTBOUND_DATE text not null,
    INBOUND_DATE text,      -- Use empty string for oneway trip
--  CABIN_CLASS text,       -- Can be 'Economy', 'PremiumEconomy', 'Business', 'First'
    ADULTS int not null,
    CHILDREN int not null,
    INFANTS int not null default 0,
--  INCLUDE_CARRIERS,       -- Only return results from those carriers. Comma-separated list of carrier ids
--  EXCLUDE_CARRIERS,       -- Filter out results from those carriers. Comma-separated list of carrier ids
    GROUP_PRICING boolean default true,     -- If set to true, prices will be obtained for the whole passenger group
                                            -- and if set to false it will be obtained for one adult.
    SERIAL_POLL_PERIOD int not null default 0,           -- days number for shifting poll starting from OUTBOUND_DATE
    ENABLED_FLAG boolean not null default true);