package Game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class TUIMethod {
    private URL serverURL;
    private String serverIPAddress;
    private String serverPort;

    public TUIMethod(String serverIPAddress, String serverPort) {
        this.serverIPAddress = serverIPAddress;
        this.serverPort = serverPort;
        this.serverURL = null;
    }

    public String StartConnection() {
        try {
            //Create connection
            serverURL = new URL("http://" + serverIPAddress + ":" + serverPort + "/Server/REST");
            HttpURLConnection serverConnection = (HttpURLConnection) serverURL.openConnection();
            serverConnection.setRequestMethod("GET");
            // try to recover the response code from the server to know if is on
            serverConnection.getResponseCode();
            return "ok";
        }
        catch (Exception e) {
            return "Connection with server failed...";
        }
    }

    // Writes on terminal the list of current matches
    public String GetListOfCurrentMatches() {
        try {
            // server request
            URL url = new URL(serverURL + "/getGamesNames");
            HttpURLConnection serverConnection = (HttpURLConnection) url.openConnection();
            serverConnection.setRequestMethod("GET");
            // find matches
            return GetRequest(serverConnection);
        }
        catch (Exception e) {
            return "error";
        }
    }

    // Writes on terminal the details of a match
    public String GetDetailsOfMatch(String matchName) {
        try {
            // server request
            URL url = new URL(serverURL + "/gameDetails/" + matchName);
            HttpURLConnection serverConnection = (HttpURLConnection) url.openConnection();
            serverConnection.setRequestMethod("GET");
            // find matches
            return GetRequest(serverConnection);
        }
        catch (Exception e) {
            return "error";
        }
    }

    // Create a new match
    public String CreateANewGame(String playerName, String matchName, String sideSize, String maxScore, String IPAddress, String portAddress) {
        try {
            Map<String,Object> params = new LinkedHashMap<>();
            params.put("gameName", matchName);
            params.put("sizeSide", sideSize);
            params.put("maxScore", maxScore);
            params.put("IPAddress", IPAddress);
            params.put("portAddress", portAddress);
            params.put("playerName", playerName);
            StringBuilder postData = new StringBuilder();
            for (Map.Entry<String,Object> param : params.entrySet()) {
                if (postData.length() != 0) postData.append('&');
                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            }
            byte[] postDataBytes = postData.toString().getBytes("UTF-8");
            // server post
            URL url = new URL(serverURL + "/createGame");
            HttpURLConnection serverConnection = (HttpURLConnection) url.openConnection();
            serverConnection.setRequestMethod("POST");
            serverConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            serverConnection.setRequestProperty("Content-Length", Integer.toString(postData.length()));
            serverConnection.setDoOutput(true);
            serverConnection.getOutputStream().write(postDataBytes);

            // receive game from call
            return GetRequest(serverConnection);
        }
        catch (Exception e) {
            return "error";
        }
    }

    // Add player to a created match
    public String AddPlayerInGame(String playerName, String matchName, String IPAddress, String portAddress) {
        try {
            // server post
            URL url = new URL(serverURL + "/addPlayer/" + matchName + "/" + playerName + "/" + IPAddress + "/" + portAddress);
            HttpURLConnection serverConnection = (HttpURLConnection) url.openConnection();
            serverConnection.setRequestMethod("PUT");

            // receive on ok from the server
            return GetRequest(serverConnection);
        }
        catch (Exception e) {
            return "error";
        }
    }

    // Remove a player from a created match
    public String RemovePlayerFromMatch(String playerName, String matchName, String IPAddress, String portAddress) {
        try {
            // server request
            URL url = new URL(serverURL + "/removePlayer/" + matchName + "/" + playerName + "/" + IPAddress + "/" + portAddress);
            HttpURLConnection serverConnection = (HttpURLConnection) url.openConnection();
            serverConnection.setRequestMethod("DELETE");
            // find matches
            return GetRequest(serverConnection);
        }
        catch (Exception e) {
            return "error";
        }
    }

    private String GetRequest(HttpURLConnection serverConnection) {
        try {
            if (serverConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // recovery json
                BufferedReader in = new BufferedReader(new InputStreamReader(serverConnection.getInputStream()));
                String inputLine;
                StringBuffer result = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    result.append(inputLine);
                }
                in.close();
                return result.toString();
            }
            // no match found
            else {
                return "empty";
            }
        } catch (Exception e) {
            return "error";
        }
    }
}
