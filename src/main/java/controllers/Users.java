package controllers;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import server.Main;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

@Path("users/")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)

public class Users {
    @GET
    @Path("get")
    public String get() {
        System.out.println("Invoked Users.get()");
        JSONArray response = new JSONArray();
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT Name of City FROM Destinations");
            ResultSet results = ps.executeQuery();
            while (results.next() == true) {
                JSONObject row = new JSONObject();
                row.put("Name of City", results.getInt(1));
                response.add(row);
            }
            return response.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to list items.  Error code xx.\"}";
        }
    }

    @GET
    @Path("get/{UserID}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)

    public String GetUser(@PathParam("UserID") Integer UserID) {
        System.out.println("Invoked Users.GetUser() with UserID " + UserID);
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT Username FROM Users WHERE UserID = ?");
            ps.setInt(1, UserID);
            ResultSet results = ps.executeQuery();
            JSONObject response = new JSONObject();
            if (results.next() == true) {
                response.put("UserID", UserID);
                response.put("Username", results.getString(1));
            }
            return response.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to get item, please see server console for more info.\"}";
        }
    }

        @POST
        @Path("login")
        public String UsersLogin(@FormDataParam("Username") String Username, @FormDataParam("Password") String Password){

            System.out.println("Invoked loginUser() on path users/login");
            try {
                PreparedStatement ps1 = Main.db.prepareStatement("SELECT Password FROM Users WHERE Username = ?");
                ps1.setString(1, Username);
                ResultSet loginResults = ps1.executeQuery();
                if (loginResults.next() == true) {
                    String correctPassword = loginResults.getString(1);
                    if (Password.equals(correctPassword)) {
                        String Token = UUID.randomUUID().toString();
                        PreparedStatement ps2 = Main.db.prepareStatement("UPDATE Users SET Token = ? WHERE Username = ?");
                        ps2.setString(1, Token);
                        ps2.setString(2, Username);
                        ps2.executeUpdate();
                        JSONObject userDetails = new JSONObject();
                        userDetails.put("Username", Username);
                        userDetails.put("Token", Token);
                        return userDetails.toString();
                    } else {
                        return "{\"Error\": \"Incorrect password!\"}";
                    }
                } else {
                    return "{\"Error\": \"Incorrect username.\"}";
                }
            } catch (Exception exception) {
                System.out.println("Database error during /users/login: " + exception.getMessage());
                return "{\"Error\": \"Server side error!\"}";
            }
        }

        @POST
        @Path("SavedDestinations")
        public String SavedDestinations(@FormDataParam("SavedDestination1") String SavedDestination1, @FormDataParam("SavedDestination2") String SavedDestination2, @FormDataParam("SavedDestination3") String SavedDestination3, @FormDataParam("Username") String Username) {
            try {
                PreparedStatement ps = Main.db.prepareStatement("UPDATE Login SET SavedDestination1 = ?, SavedDestination2 = ?, SavedDestination3 = ? WHERE Username = ?");
                ps.setString(1, SavedDestination1);
                ps.setString(2, SavedDestination2);
                ps.setString(3, SavedDestination3);
                ps.setString(4, Username);
                ps.execute();
                return "{\"OK\": \"Saved Destination\"}";
            } catch(Exception exception) {
                System.out.println("Database error: " + exception.getMessage());
                return "{\"Error\": \"Unable to update item, please see server console for more info.\"}";
            }
        }



}