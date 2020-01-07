package ua.ikulikov.flightmaster.geocatalogservice.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class CustomDateTimeDeserializer extends StdDeserializer<LocalDateTime> {
    private SimpleDateFormat formatter =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    public CustomDateTimeDeserializer() {
        this(null);
    }

    public CustomDateTimeDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public LocalDateTime deserialize(JsonParser jsonparser, DeserializationContext context)
            throws IOException {
        String date = jsonparser.getText();
        try {
            Date d = formatter.parse(date);
            return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        }
        catch (ParseException e) {
            throw new RuntimeException(String.format("Can't parse date %s with pattern %s", date, formatter.toPattern()) , e);
        }
    }
}