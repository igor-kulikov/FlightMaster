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
    unique (ITINERARY_ID, OUTBOUND_LEG_ID, INBOUND_LEG_ID)
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

--------------- create views
create or replace view SKY_SCANNER.V_LEGS as
select l.LEG_ID, '[' || po.TYPE || '] ' || po.NAME as PLACE, l.DEPARTURE_DT,
    '[' || pd.TYPE || '] ' || pd.NAME as DESTNATION, ARRIVAL_DT,
	DURATION_MM, stops.stops, carriers.carriers
from SKY_SCANNER.LEGS l
left outer join SKY_SCANNER.REF_PLACES po on po.PLACE_ID = l.ORIGIN_STATION_PLACE_ID
left outer join SKY_SCANNER.REF_PLACES pd on pd.PLACE_ID = l.DESTINATION_STATION_PLACE_ID
left outer join (
	select ls.LEG_ID, string_agg(p.NAME, '; ') as stops from SKY_SCANNER.LEG_STOPS ls
	join SKY_SCANNER.REF_PLACES p on p.PLACE_ID = ls.STOP_PLACE_ID
	group by ls.LEG_ID) stops on stops.LEG_ID = l.LEG_ID
left outer join (
	select lc.LEG_ID, string_agg(c.NAME, '; ') as carriers from SKY_SCANNER.LEG_CARRIERS lc
	join SKY_SCANNER.REF_CARRIERS c on c.CARRIER_ID = lc.CARRIER_ID
	group by lc.LEG_ID) carriers on carriers.LEG_ID = l.LEG_ID
;

create or replace view SKY_SCANNER.V_PRICE_OPTIONS as
select it.ITINERARY_ID, it.OUTBOUND_LEG_ID, it.INBOUND_LEG_ID,
	po.PRICE_OPTION_ID, po.PRICE, po.QUOTE_AGE_MM, agents.agents,
	leg1.PLACE PLACE_FROM, leg1.DEPARTURE_DT, leg1.PLACE PLACE_TO, leg1.ARRIVAL_DT, leg1.STOPS STOPS, leg1.CARRIERS CARRIERS, leg1.DURATION_MM,
	leg2.PLACE BACK_PLACE_FROM, leg2.DEPARTURE_DT BACK_DEPARTURE_DT, leg2.PLACE BACK_PLACE_TO, leg2.ARRIVAL_DT BACK_ARRIVAL_DT, leg2.STOPS BACK_STOPS, leg2.CARRIERS BACK_CARRIERS, leg1.DURATION_MM BACK_DURATION_MM,
	po.DEEPLINK_URL from SKY_SCANNER.ITINERARIES it
join SKY_SCANNER.PRICE_OPTIONS po on po.ITINERARY_ID = it.ITINERARY_ID
left outer join SKY_SCANNER.V_LEGS leg1 on leg1.LEG_ID = it.OUTBOUND_LEG_ID
left outer join SKY_SCANNER.V_LEGS leg2 on leg2.LEG_ID = it.INBOUND_LEG_ID
left outer join (
	select pa.PRICE_OPTION_ID, string_agg(a.NAME, '; ') as agents from SKY_SCANNER.PRICE_OPTION_AGENTS pa
	join SKY_SCANNER.REF_AGENTS a on a.AGENT_ID = pa.AGENT_ID
	group by pa.PRICE_OPTION_ID) agents on agents.PRICE_OPTION_ID = po.PRICE_OPTION_ID
;

create or replace view SKY_SCANNER.V_DIRECT_LEGS as
select l.LEG_ID from SKY_SCANNER.LEGS l
left outer join SKY_SCANNER.LEG_STOPS ls
  on ls.LEG_ID = l.LEG_ID
where ls.LEG_ID is null;

create or replace view SKY_SCANNER.V_DIRECT_ITINERARIES as
select it.ITINERARY_ID from SKY_SCANNER.ITINERARIES it
join SKY_SCANNER.V_DIRECT_LEGS ol on ol.LEG_ID = it.OUTBOUND_LEG_ID
left outer join SKY_SCANNER.V_DIRECT_LEGS il on il.LEG_ID = it.INBOUND_LEG_ID;


create or replace view SKY_SCANNER.V_BEST_PRICE_OPTIONS as
select po.ITINERARY_ID, po.PRICE_OPTION_ID from SKY_SCANNER.PRICE_OPTIONS po
join (
	select ITINERARY_ID, PRICE_OPTION_ID,
		dense_rank() over(partition by ITINERARY_ID ORDER BY PRICE ASC) as rn
	from SKY_SCANNER.PRICE_OPTIONS
	group by ITINERARY_ID, PRICE_OPTION_ID, PRICE) t
on t.ITINERARY_ID = po.ITINERARY_ID and t.PRICE_OPTION_ID = po.PRICE_OPTION_ID
	and t.rn = 1;

create or replace view SKY_SCANNER.V_DIRECT_BEST_PRICE_OPTIONS as
select bpo.ITINERARY_ID, bpo.PRICE_OPTION_ID from SKY_SCANNER.V_DIRECT_ITINERARIES di
join SKY_SCANNER.V_BEST_PRICE_OPTIONS bpo
	on bpo.ITINERARY_ID = di.ITINERARY_ID;

create or replace view SKY_SCANNER.V_LEG_DETAILS as
select l.LEG_ID, '[' || po.TYPE || '] ' || po.NAME as PLACE, l.DEPARTURE_DT,
    '[' || pd.TYPE || '] ' || pd.NAME as DESTNATION, ARRIVAL_DT,
	DURATION_MM, stops.stops, carriers.carriers
from SKY_SCANNER.LEGS l
left outer join SKY_SCANNER.REF_PLACES po on po.PLACE_ID = l.ORIGIN_STATION_PLACE_ID
left outer join SKY_SCANNER.REF_PLACES pd on pd.PLACE_ID = l.DESTINATION_STATION_PLACE_ID
left outer join (
	select ls.LEG_ID, string_agg(p.NAME, '; ') as stops from SKY_SCANNER.LEG_STOPS ls
	join SKY_SCANNER.REF_PLACES p on p.PLACE_ID = ls.STOP_PLACE_ID
	group by ls.LEG_ID) stops on stops.LEG_ID = l.LEG_ID
left outer join (
	select lc.LEG_ID, string_agg(c.NAME, '; ') as carriers from SKY_SCANNER.LEG_CARRIERS lc
	join SKY_SCANNER.REF_CARRIERS c on c.CARRIER_ID = lc.CARRIER_ID
	group by lc.LEG_ID) carriers on carriers.LEG_ID = l.LEG_ID
;

create or replace view SKY_SCANNER.V_DIRECT_BEST_PRICE_DETAILS as
select it.ITINERARY_ID, it.OUTBOUND_LEG_ID, it.INBOUND_LEG_ID,
	po.PRICE_OPTION_ID, po.PRICE, po.QUOTE_AGE_MM, agents.agents,
	leg1.PLACE PLACE_FROM, leg1.DEPARTURE_DT, leg2.PLACE PLACE_TO, leg1.ARRIVAL_DT, leg1.STOPS STOPS, leg1.CARRIERS CARRIERS, leg1.DURATION_MM,
	leg2.PLACE BACK_PLACE_FROM, leg2.DEPARTURE_DT BACK_DEPARTURE_DT, leg2.PLACE BACK_PLACE_TO, leg2.ARRIVAL_DT BACK_ARRIVAL_DT, leg2.STOPS BACK_STOPS, leg2.CARRIERS BACK_CARRIERS, leg1.DURATION_MM BACK_DURATION_MM,
	po.DEEPLINK_URL from SKY_SCANNER.V_DIRECT_BEST_PRICE_OPTIONS dbpo
join SKY_SCANNER.ITINERARIES it on it.ITINERARY_ID = dbpo.ITINERARY_ID
join SKY_SCANNER.PRICE_OPTIONS po on po.ITINERARY_ID = dbpo.ITINERARY_ID
	and po.PRICE_OPTION_ID = dbpo.PRICE_OPTION_ID
left outer join SKY_SCANNER.V_LEG_DETAILS leg1 on leg1.LEG_ID = it.OUTBOUND_LEG_ID
left outer join SKY_SCANNER.V_LEG_DETAILS leg2 on leg2.LEG_ID = it.INBOUND_LEG_ID
left outer join (
	select pa.PRICE_OPTION_ID, string_agg(a.NAME, '; ') as agents from SKY_SCANNER.PRICE_OPTION_AGENTS pa
	join SKY_SCANNER.REF_AGENTS a on a.AGENT_ID = pa.AGENT_ID
	group by pa.PRICE_OPTION_ID) agents on agents.PRICE_OPTION_ID = po.PRICE_OPTION_ID
;

create or replace view SKY_SCANNER.FLIGHT_DATA_AGGR as
select p.POLL_ID, p.POLL_ID_EXT, p.OUTBOUND_AIRPORT, p.INBOUND_AIRPORT, p.OUTBOUND_DATE, p.INBOUND_DATE,
	dbpo.DEPARTURE_DT, dbpo.ARRIVAL_DT, dbpo.DURATION_MM, dbpo.CARRIERS,
	dbpo.BACK_DEPARTURE_DT, dbpo.BACK_ARRIVAL_DT, dbpo.BACK_DURATION_MM, dbpo.BACK_CARRIERS,
	dbpo.AGENTS, dbpo.PRICE, dbpo.DEEPLINK_URL, dbpo.QUOTE_AGE_MM, p.POLL_STATUS_DT as POLL_COMPLETE_DT
from SKY_SCANNER.FLIGHT_REQUEST_POLLS p
join SKY_SCANNER.POLL_ITINERARIES pi on pi.POLL_ID = p.POLL_ID
join SKY_SCANNER.V_DIRECT_BEST_PRICE_DETAILS dbpo on dbpo.ITINERARY_ID = pi.ITINERARY_ID
where p.POLL_STATUS = 'SUCCESS'
order by p.POLL_ID_EXT, p.OUTBOUND_AIRPORT, p.INBOUND_AIRPORT, p.OUTBOUND_DATE, p.INBOUND_DATE,
	p.POLL_ID desc;