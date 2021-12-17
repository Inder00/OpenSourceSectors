package pl.inder00.opensource.sectors.commons.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import pl.inder00.opensource.sectors.commons.concurrent.FutureCallback;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UpdateChecker {

    /**
     * Github REST API release endpoint
     */
    protected static String GITHUB_RELEASE_ENDPOINT = "https://api.github.com/repos/%s/%s/releases/latest";

    /**
     * Single threaded executor service
     */
    protected static ExecutorService singleThreadedExecutorService = Executors.newSingleThreadExecutor();

    /**
     * Checks latest version via Github REST API
     *
     * @param repoAuthor Repository author
     * @param repoName Repository name
     * @param versionCallback Version callback (string)
     */
    public static void getLatestVersion(String repoAuthor, String repoName, FutureCallback<String> versionCallback)
    {

        // Schedule request on single-threaded executor
        singleThreadedExecutorService.submit(() -> {

            // Update check endpoint url
            String updateCheckEndpoint = String.format(GITHUB_RELEASE_ENDPOINT, repoAuthor, repoName);

            try
            {

                // create connection
                URL url = new URL(updateCheckEndpoint);
                URLConnection connection = url.openConnection();
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("User-Agent", repoName);
                connection.setUseCaches(false);
                connection.setDoOutput(true);

                // connect
                connection.connect();

                // read response
                InputStreamReader inputStreamReader = new InputStreamReader((InputStream) connection.getContent());

                // parse response
                JsonObject jsonResponse = (JsonObject) JsonParser.parseReader(inputStreamReader);

                // send callback
                versionCallback.execute( jsonResponse.get("tag_name").getAsString() );

            }
            catch (Throwable e)
            {
                e.printStackTrace();
                // send callback
                versionCallback.execute( null );

            }

        });

    }

}
