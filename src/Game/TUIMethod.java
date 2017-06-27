package Game;


import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TUIMethod {
    private URL serverURL;
    private HttpURLConnection serverConnection = null;
    private Gson json = new Gson();
    private String serverIPAddress;
    private String serverPort;

    public TUIMethod(String serverIPAddress, String serverPort) {
        this.serverIPAddress = serverIPAddress;
        this.serverPort = serverPort;
    }

    public String StartConnection() {
        try {
            //Create connection
            serverURL = new URL("http://" + serverIPAddress + ":" + serverPort + "/Server/REST");
            serverConnection = (HttpURLConnection) serverURL.openConnection();
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
            serverConnection = (HttpURLConnection) url.openConnection();
            serverConnection.setRequestMethod("GET");
            // find matches
            if (serverConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // recovery json
                BufferedReader in = new BufferedReader(new InputStreamReader(serverConnection.getInputStream()));
                String inputLine;
                StringBuffer result = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    result.append(inputLine);
                }
                in.close();
                // recovery list of games
                List<String> listOfMatches = json.fromJson(result.toString(), List.class);
                String response = "";
                // print on terminal the list of matches
                for (String name: listOfMatches) {
                    response += "- " + name + "\n";
                }
                return response;
            }
            // no matches in list
            else {
                return "empty";
            }
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
            serverConnection = (HttpURLConnection) url.openConnection();
            serverConnection.setRequestMethod("GET");
            // find matches
            if (serverConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // recovery json
                BufferedReader in = new BufferedReader(new InputStreamReader(serverConnection.getInputStream()));
                String inputLine;
                StringBuffer result = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    result.append(inputLine);
                }
                in.close();
                // recovery list of games
                Game game = json.fromJson(result.toString(), Game.class);
                String response = "Game name: " + game.getName() + ",\nMap side size: " + game.getSizeSide() + ",\nIn-game players: \n";
                for (int i = 0; i < game.getInGamePlayers().size(); i ++) {
                    response += "  - " + game.getInGamePlayers().get(i) + "\n";
                }
                response += "Max score: " + game.getMaxScore() + " .";
                return response;
            }
            // no match found
            else {
                return "empty";
            }
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
            serverConnection = (HttpURLConnection) url.openConnection();
            serverConnection.setRequestMethod("POST");
            serverConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            serverConnection.setRequestProperty("Content-Length", Integer.toString(postData.length()));
            serverConnection.setDoOutput(true);
            serverConnection.getOutputStream().write(postDataBytes);

            // receive token from call
            if (serverConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // recovery json
                BufferedReader in = new BufferedReader(new InputStreamReader(serverConnection.getInputStream()));
                String inputLine;
                StringBuffer result = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    result.append(inputLine);
                }
                in.close();
                // receive the token if the request is end well
                return json.fromJson(result.toString(), String.class);
            }
            // error in the creation of the match
            else {
                return "fail";
            }
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
            serverConnection = (HttpURLConnection) url.openConnection();
            serverConnection.setRequestMethod("PUT");

            // receive on ok from the server
            if (serverConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // recovery json
                BufferedReader in = new BufferedReader(new InputStreamReader(serverConnection.getInputStream()));
                String inputLine;
                StringBuffer result = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    result.append(inputLine);
                }
                in.close();
                // receive the token if the request is end well
                return json.fromJson(result.toString(), String.class);
            }
            // error in the adding of the player
            else {
                return "empty";
            }
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
            serverConnection = (HttpURLConnection) url.openConnection();
            serverConnection.setRequestMethod("DELETE");
            // find matches
            if (serverConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // recovery json
                BufferedReader in = new BufferedReader(new InputStreamReader(serverConnection.getInputStream()));
                String inputLine;
                StringBuffer result = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    result.append(inputLine);
                }
                in.close();
                return json.fromJson(result.toString(), String.class);
            }
            // no match found
            else {
                return "empty";
            }
        }
        catch (Exception e) {
            return "error";
        }
    }
}
