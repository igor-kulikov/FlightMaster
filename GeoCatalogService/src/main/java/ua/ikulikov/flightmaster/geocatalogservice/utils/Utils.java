package ua.ikulikov.flightmaster.geocatalogservice.utils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import one.util.streamex.StreamEx;
import org.springframework.core.io.ClassPathResource;
import ua.ikulikov.flightmaster.geocatalogservice.ApplicationException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

public class Utils {

    private Utils() {
        // this is utility class, no instantiation is allowed
    }

    public static String loadFile(String fileName) throws ApplicationException {
        ClassPathResource resource = new ClassPathResource(fileName);

        try {
            File file = resource.getFile();
            List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
            String str = StreamEx.of(lines)
                    .append("\n")
                    .joining();
            return str;
        }
        catch(IOException e) {
            throw  new ApplicationException("An error occurred reading from the file " + resource.getFilename());
        }
    }

    public static<T> T deserializeJson(String filePath, Class<T> type) throws ApplicationException {
        T response;
        ObjectMapper objectMapper = new ObjectMapper();
        String json = Utils.loadFile(filePath);

        try {
            response = objectMapper.readValue(json, type);
        }
        catch (JsonParseException | JsonMappingException e) {
            throw new ApplicationException("Can't parse json... ", e);
        }
        catch (IOException e) {
            throw new ApplicationException("An error occurred while processing... ", e);
        }
        return response;
    }
}