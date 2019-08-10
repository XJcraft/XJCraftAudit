package org.jim.bukkit.audit.util;

import com.google.common.base.Charsets;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {

    public static String get(String url) throws IOException {
        HttpURLConnection connection = createUrlConnection(new URL(url));

        // LOGGER.debug("Reading data from " + url);

        InputStream inputStream = null;
        try {
            inputStream = connection.getInputStream();
            String result = IOUtils.toString(inputStream, Charsets.UTF_8);
            // LOGGER.debug("Successful read, server response was " +
            // connection.getResponseCode());
            // LOGGER.debug("Response: " + result);
            return result;
        } catch (IOException e) {
            IOUtils.closeQuietly(inputStream);
            inputStream = connection.getErrorStream();

            if (inputStream != null) {
                // LOGGER.debug("Reading error page from " + url);
                String result = IOUtils.toString(inputStream, Charsets.UTF_8);
                // LOGGER.debug("Successful read, server response was " +
                // connection.getResponseCode());
                // LOGGER.debug("Response: " + result);
                return result;
            } else {
                // LOGGER.debug("Request failed", e);
                throw e;
            }
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    protected static HttpURLConnection createUrlConnection(URL url)
            throws IOException {
        // Validate.notNull(url);
        // LOGGER.debug("Opening connection to " + url);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);
        connection.setUseCaches(false);
        return connection;
    }

}
