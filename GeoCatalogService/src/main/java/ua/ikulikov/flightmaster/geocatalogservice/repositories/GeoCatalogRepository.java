package ua.ikulikov.flightmaster.geocatalogservice.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ua.ikulikov.flightmaster.geocatalogservice.entities.GeoCatalog;
import ua.ikulikov.flightmaster.geocatalogservice.entities.GeoPlaceAirport;
import ua.ikulikov.flightmaster.geocatalogservice.entities.GeoPlaceCity;
import ua.ikulikov.flightmaster.geocatalogservice.entities.GeoPlaceContinent;
import ua.ikulikov.flightmaster.geocatalogservice.entities.GeoPlaceCountry;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
public class GeoCatalogRepository {
    private NamedParameterJdbcTemplate template;

    @Autowired
    public GeoCatalogRepository(DataSource dataSource) {
        template = new NamedParameterJdbcTemplate(dataSource);
    }

    public void saveGeoCatalog(GeoCatalog geoCatalog) {
        String sqlQuery =
                "insert into REF_GEOCATALOG as c" +
                        " (CONTINENT, COUNTRY_CODE, COUNTRY_NAME," +
                        "   CITY_CODE, CITY_NAME, CITY_POS," +
                        "   AIRPORT_CODE, AIRPORT_NAME, AIRPORT_POS," +
                        "   CREATE_DATE, MODIFY_DATE)" +
                        " values (:CONTINENT, :COUNTRY_CODE, :COUNTRY_NAME," +
                        "   :CITY_CODE, :CITY_NAME, :CITY_POS," +
                        "   :AIRPORT_CODE, :AIRPORT_NAME, :AIRPORT_POS," +
                        "   :NOW, :NOW)" +
                        " on conflict (AIRPORT_CODE) do update" +
                        " set CONTINENT = :CONTINENT," +
                        "   COUNTRY_CODE = :COUNTRY_CODE," +
                        "   COUNTRY_NAME = :COUNTRY_NAME," +
                        "   CITY_CODE = :CITY_CODE," +
                        "   CITY_NAME = :CITY_NAME," +
                        "   CITY_POS = :CITY_POS," +
                        "   AIRPORT_NAME = :AIRPORT_NAME," +
                        "   AIRPORT_POS = :AIRPORT_POS," +
                        "   MODIFY_DATE = :NOW" +
                        " where c.CONTINENT <> :CONTINENT or" +
                        "   c.COUNTRY_CODE <> :COUNTRY_CODE or" +
                        "   c.COUNTRY_NAME <> :COUNTRY_NAME or" +
                        "   c.CITY_CODE <> :CITY_CODE or" +
                        "   c.CITY_NAME <> :CITY_NAME or" +
                        "   c.CITY_POS <> :CITY_POS or" +
                        "   c.AIRPORT_NAME <> :AIRPORT_NAME or" +
                        "   c.AIRPORT_POS <> :AIRPORT_POS";


        List<Map<String, Object>> paramsList = new ArrayList<>();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        for (GeoPlaceContinent continent : geoCatalog.getContinents()) {
            for (GeoPlaceCountry country : continent.getCountries()) {
                for (GeoPlaceCity city : country.getCities()) {
                    for (GeoPlaceAirport airport : city.getAirports()) {
                        Map<String, Object> param = new MapSqlParameterSource("CONTINENT", continent.getName())
                                .addValue("COUNTRY_CODE", country.getId())
                                .addValue("COUNTRY_NAME", country.getName())
                                .addValue("CITY_CODE", city.getId())
                                .addValue("CITY_NAME", city.getName().replace("'", "''"))
                                .addValue("CITY_POS", city.getLocation())
                                .addValue("AIRPORT_CODE", airport.getId())
                                .addValue("AIRPORT_NAME", airport.getName().replace("'", "''"))
                                .addValue("AIRPORT_POS", airport.getLocation())
                                .addValue("NOW", now)
                                .getValues();
                        paramsList.add(param);
                    }
                }
            }
        }

        Map<String, Object>[] paramsArr = new Map[paramsList.size()];
        paramsArr = paramsList.toArray(paramsArr);

        template.batchUpdate(sqlQuery, paramsArr);
    }

    public Set<String> getChildAirports(String place) {
        String sqlQuery =
                "select AIRPORT_CODE from REF_GEOCATALOG " +
                "where CONTINENT = :PLACE "  +
                "   or COUNTRY_CODE = :PLACE " +
                "   or CITY_CODE = :PLACE " +
                "   or AIRPORT_CODE = :PLACE";

        SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("PLACE", place);
        return new HashSet<>(template.queryForList(sqlQuery, namedParameters, String.class));
    }
}