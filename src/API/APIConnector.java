package API;

import GUI.Controller;
import com.fasterxml.jackson.core.io.CharacterEscapes;
import okhttp3.*;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class APIConnector {
    private final Controller controller;
    private String applicationURL;
    private String repositoryShortName;
    private String APIauthorization;
    private int rootID;
    private int latestCreatedObjectID;
    HashMap<Request, Response> requestResponseHashMap = new HashMap<>();

    public APIConnector(Controller controller, String applicationURL, String repositoryShortName, int rootID) {
        this.applicationURL = applicationURL;
        this.repositoryShortName = repositoryShortName;
        this.rootID = rootID;
        this.controller = controller;
    }

    public void encodeCredentialsToBase64(String username, String password){
        String encodedStringPrefix  = "Basic ";
        String delimiter = ":";
        String encodeableString = username+delimiter+password;

        this.APIauthorization = encodedStringPrefix + Base64.getEncoder().encodeToString(encodeableString.getBytes());
    }

    public void createRootObject(APIContentCreator apiContentCreator, String objectType, String name, String purpose, String summary){
        createObject(apiContentCreator, rootID, objectType, name, purpose, summary);
        rootID = latestCreatedObjectID;
    }

    public int createNodeObject(APIContentCreator apiContentCreator, String objectType, String name, String purpose, String summary){
        return createObject(apiContentCreator, rootID, objectType, name, purpose, summary);
    }

    public int createObject(APIContentCreator apiContentCreator, int parentObjectID, String objectType, String name, String purpose, String summary) {
        OkHttpClient client = new OkHttpClient();
        client.newBuilder().connectTimeout(2, TimeUnit.SECONDS)
                            .writeTimeout(15, TimeUnit.SECONDS);

        Request request = createRequest(parentObjectID, objectType, name, purpose, summary);

        try (Response response = client.newCall(request).execute()) {
            requestResponseHashMap.put(request, response);
            if (response.code() == 201) {
                latestCreatedObjectID = getCreatedObjectID(response);
                apiContentCreator.addObjectToObjectMap(latestCreatedObjectID, parentObjectID);
                controller.statusTxtField.setText("Created object '" + latestCreatedObjectID + "' : '" + name + "'");
                return latestCreatedObjectID;
            } /* else if response.code() == ?? {
                   // Handling if object has been created, but body text wasn't applied.
                }*/
        } catch (IOException e) {
            controller.statusTxtField.setText("Failed trying to create: '" + name + "'");
            System.out.println("Failed: '" + name + "'");

            int count = 0;
            while (retryFailedRequests(request) == 0 && count++ < 25);
        }
        return 0;
    }

    public Request createRequest(int parentObjectID, String objectType, String name, String purpose, String summary) {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, makeObjectContent(objectType, name, purpose, summary));
        return new Request.Builder()
                .url(applicationURL + "/api/repositories/" + repositoryShortName + "/objects/" + parentObjectID + "/create")
                .post(body)
                .addHeader("authorization", APIauthorization)
                .addHeader("content-type", "application/json")
                .addHeader("cache-control", "no-cache")
                .build();
    }

    private byte[] makeObjectContent(String objectType, String name, String summary, String purpose) {
        String JsonContent = "{\r\n  " +
                "\"id\": 0,\r\n  " +
                "\"name\": \"" + name + "\",\r\n  " +
                "\"purpose\": \""+ purpose + "\",\r\n  " +
                "\"summary\": \"" + summary +"\",\r\n  " +
                "\"type\": \"" + objectType + "\"\r\n}";
        return JsonContent.getBytes();
    }

    private int getCreatedObjectID(Response response) {
        int indexOfObjectID = new String("/api/repositories/"+ repositoryShortName +"/objects/").length();
        String objectID = response.headers().toMultimap().get("location").get(0).substring(indexOfObjectID);
        return Integer.parseInt(objectID);
    }

    public HashMap<Request, Response> getRequestResponseHashMap() {
        return requestResponseHashMap;
    }

    public int retryFailedRequests(Request request) {
        OkHttpClient client = new OkHttpClient();
        client.newBuilder().connectTimeout(2, TimeUnit.SECONDS);

        try (Response response = client.newCall(request).execute()) {
            requestResponseHashMap.put(request, response);
            if (response.code() == 201) {
                controller.statusTxtField.setText("Cleaning up");
                System.out.println("Done some housekeeping and succeeded creating: " + getCreatedObjectID(response));
                return getCreatedObjectID(response);
            } else {
                System.out.println(response);
            }
        } catch (IOException e) {
            controller.statusTxtField.setText("Failed when trying to clean up. Trying harder...");
            System.out.println("Failed reattempt. Retrying...");
        }
        return 0;
    }
}
