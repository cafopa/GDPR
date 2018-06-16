package JSON;

import GDPR.GDPR;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class JsonConverter {
    GDPR gdpr;

    public JsonConverter(GDPR gdpr) {
        this.gdpr = gdpr;
    }

    public void readJsonFrom(File file) {
        /* This function is reading the log file and converts the content into the GDPR object with Jackson
         *  ObjectMapper. */
        try {
            ObjectMapper mapper = new ObjectMapper();
            gdpr = mapper.readValue(file, GDPR.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeJsonTo(File file) {
        try(OutputStream fos = new FileOutputStream(file)) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            mapper.writeValue(file, gdpr);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
