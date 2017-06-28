package Server;

import Game.Game;
import com.google.gson.Gson;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

// The Java class will be hosted at the URI path "/REST"
@Path("/REST")
public class RESTMaster {

    private Gson json = new Gson();

    @GET
    // without path check if server is on
    public Response ImAlive() {

        return Response.ok("Server is on!").build();
    }

    @GET
    @Path("getGamesNames")
    @Produces({"application/json"})
    // get list of all match playing in this moment
    public Response GetGamesNames() {

        String[] arrayOfGamesName = RESTMethod.GetInstance().GetGamesNames();
        if (arrayOfGamesName != null) {
            return Response.ok(json.toJson(arrayOfGamesName)).build();
        } else {
            return Response.status(Response.Status.NO_CONTENT).build();
        }
    }

    @Path("createGame")
    @POST
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    // create a new game
    public Response CreateNewGame(@FormParam("gameName") String gameName, @FormParam("sizeSide") Integer sizeSide, @FormParam("maxScore") Integer maxScore, @FormParam("IPAddress") String IPAddress, @FormParam("portAddress") Integer portAddress, @FormParam("playerName") String playerName){

        Game game = RESTMethod.GetInstance().CreateNewGame(gameName, sizeSide, maxScore, IPAddress, portAddress, playerName);
        if (game != null) {
            // return the token to the player after a game is created
            return Response.ok(json.toJson(game)).build();
        }
        else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @Path("gameDetails/{gameName}")
    @GET
    @Produces("application/json")
    // get details of a match
    public Response GetDetailsOfGame(@PathParam("gameName") String gameName){

        Game game = RESTMethod.GetInstance().GetGameDetails(gameName);
        if (game != null) {
            return Response.ok(json.toJson(game)).build();
        }
        else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @Path("removePlayer/{gameName}/{playerName}/{IpAddress}/{portAddress}")
    @DELETE
    @Produces("application/json")
    // delete a player
    public Response RemovePlayer(@PathParam("gameName") String gameName, @PathParam("playerName") String playerName,  @PathParam("IpAddress") String IPAddress,  @PathParam("portAddress") Integer portAddress) {

        boolean response = RESTMethod.GetInstance().RemovePlayerInGame(playerName, gameName, IPAddress, portAddress);
        if (response) {
            return Response.ok(json.toJson("ok")).build();
        }
        else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @Path("addPlayer/{gameName}/{playerName}/{IpAddress}/{portAddress}")
    @PUT
    @Produces("application/json")
    // add player to game
    public Response AddPlayer(@PathParam("gameName") String gameName, @PathParam("playerName") String playerName,  @PathParam("IpAddress") String IPAddress,  @PathParam("portAddress") Integer portAddress) {

        Game game = RESTMethod.GetInstance().AddPlayerInGame(playerName, gameName, IPAddress, portAddress);
        if (game != null) {
            return Response.ok(json.toJson(game)).build();
        }
        else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }
}
