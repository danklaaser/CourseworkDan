package controllers;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import server.Main;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Path("users/")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)

public class Users {
    @GET
    @Path("list")
    public String UsersList() {
        System.out.println("Invoked Users.UsersList()");
        JSONArray response = new JSONArray();
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT UserID, UserName FROM Users");
            ResultSet results = ps.executeQuery();
            while (results.next() == true) {
                JSONObject row = new JSONObject();
                row.put("UserID", results.getInt(1));
                row.put("UserName", results.getString(2));
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
            PreparedStatement ps = Main.db.prepareStatement("SELECT UserName FROM Users WHERE UserID = ?");
            ps.setInt(1, UserID);
            ResultSet results = ps.executeQuery();
            JSONObject response = new JSONObject();
            if (results.next() == true) {
                response.put("UserID", UserID);
                response.put("UserName", results.getString(1));
            }
            return response.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to get item, please see server console for more info.\"}";
        }
    }

        @POST
        @Path("login")
        public String UsersLogin(@FormDataParam("UserName") String UserName, @FormDataParam("PassWord") String PassWord){

            System.out.println("Invoked loginUser() on path users/login");
            try {
                PreparedStatement ps1 = Main.db.prepareStatement("SELECT PassWord FROM Users WHERE UserName = ?");
                ps1.setString(1, UserName);
                ResultSet loginResults = ps1.executeQuery();
                if (loginResults.next() == true) {
                    String correctPassword = loginResults.getString(1);
                    if (PassWord.equals(correctPassword)) {
                        String Token = UUID.randomUUID().toString();
                        PreparedStatement ps2 = Main.db.prepareStatement("UPDATE Users SET Token = ? WHERE UserName = ?");
                        ps2.setString(1, Token);
                        ps2.setString(2, UserName);
                        ps2.executeUpdate();
                        JSONObject userDetails = new JSONObject();
                        userDetails.put("UserName", UserName);
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



}