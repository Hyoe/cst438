package com.cst438.app;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.*;
import java.sql.*;
import javax.servlet.http.*;
import com.google.appengine.api.utils.SystemProperty;

public class TestServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)throws IOException {

        if (req.getParameter("function").equals("getTaco")) {

            String url = null;

            resp.setContentType("text/html");
            resp.getWriter().println("get Taco function<br>");

            // Load the class that provides the new "jdbc:google:mysql://" prefix.
            try {
                Class.forName("com.mysql.jdbc.GoogleDriver");
                //format for url is........appname:mysqlinstancename/dbname?user=username&password=passsword
                url = "jdbc:google:mysql://cst438mysql:mysqlinstance/cst438db?user=root&password=banana438";
                Connection conn = DriverManager.getConnection(url);
                ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM users WHERE username = " + "dd");

                while (rs.next()) {
                    String id = rs.getString("id");
                    String value = rs.getString("value");

                    resp.getWriter().println("id: " + id + " value: " + value);

                }

                conn.close();

            } catch (Exception e) {
                resp.getWriter().println(e.toString());
            }
        }

        if (req.getParameter("function").equals("doRegister")) {
            String url = null;

            String getUsername = req.getParameter("username");

            resp.setContentType("text/html");
            //http://cst438-1139.appspot.com/test?function=doRegister&username=banana

            // Load the class that provides the new "jdbc:google:mysql://" prefix.
            try {
                Class.forName("com.mysql.jdbc.GoogleDriver");
                //format for url is........appname:mysqlinstancename/dbname?user=username&password=passsword
                url = "jdbc:google:mysql://cst438mysql:mysqlinstance/cst438db?user=root&password=banana438";
                Connection conn = DriverManager.getConnection(url);
                String query = "SELECT username FROM users WHERE username = '" + getUsername + "'";
                ResultSet rs = conn.createStatement().executeQuery(query);

                String username = null;
                while (rs.next()) {
                    username = rs.getString("username");
                }

                if (username != null) {
                    //echo out json that says username is taken
                    resp.getWriter().println("{\"status\":\"usernameTaken\"}");
                    //{"firstName":"John", "lastName":"Doe"}
                } else {
                    
                    //register the user
                    //resp.getWriter().println("username is available");

                    String getPassword = req.getParameter("password");
                    String getFirstName = req.getParameter("firstName");
                    String getLastName = req.getParameter("lastName");
                    String getType = req.getParameter("type");

                    query = "INSERT INTO `users` ( `username` , `fname` , `lname` , `type` , `pw` ) VALUES ( ?, ?, ?, ?, ? )";

                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setString(1, getUsername);
                    stmt.setString(2, getFirstName);
                    stmt.setString(3, getLastName);
                    stmt.setString(4, getType);
                    stmt.setString(5, getPassword);
                    int success = 2;
                    success = stmt.executeUpdate();
                    
                    if (success == 1) {
                        //echo out sucessful registration
                        resp.getWriter().println("{\"status\":\"successfulRegistration\"}");
                        //if type == truck, then insert blank profile into truck database table
                        if (getType.equals("1")) {
                            query = "INSERT INTO `trucks` ( `username`, `truck_name`, `website`, `description`) VALUES (?, ?, ?, ?)";
                            stmt = conn.prepareStatement(query);
                            stmt.setString(1, getUsername);
                            stmt.setString(2, "");
                            stmt.setString(3, "");
                            stmt.setString(4, "");
                            success = 2;
                            success = stmt.executeUpdate();
                        }
                    } else if (success == 0) {
                        resp.getWriter().println("{\"status\":\"databaseError\"}");
                    }
                }

                conn.close();

            } catch (Exception e) {
                resp.getWriter().println(e.toString());
            }
        }

        if (req.getParameter("function").equals("doLogin")) {
            String url = null;

            String getUsername = req.getParameter("username");
            String getPassword = req.getParameter("password");

            resp.setContentType("text/html");
            //http://cst438-1139.appspot.com/test?function=doRegister&username=banana

            // Load the class that provides the new "jdbc:google:mysql://" prefix.
            try {
                Class.forName("com.mysql.jdbc.GoogleDriver");
                //format for url is........appname:mysqlinstancename/dbname?user=username&password=passsword
                url = "jdbc:google:mysql://cst438mysql:mysqlinstance/cst438db?user=root&password=banana438";
                Connection conn = DriverManager.getConnection(url);
                String query = "SELECT * FROM users WHERE username = '" + getUsername + "' AND pw = '" + getPassword + "'";
                ResultSet rs = conn.createStatement().executeQuery(query);

                String username = null;
                String firstName = null;
                String lastName = null;
                String userType = null;
                while (rs.next()) {
                    username = rs.getString("username");
                    firstName = rs.getString("fname");
                    lastName = rs.getString("lname");
                    userType = rs.getString("type");
                }

                if (username != null) {
                    //username/password combination works

                    //fix the json
                    resp.getWriter().println("{\"status\":\"goodLogin\", \"username\":\"" + username + "\", \"firstName\":\"" + firstName + "\", \"lastName\":\"" + lastName + "\", \"userType\":\"" + userType + "\"}");
                    //{"firstName":"John", "lastName":"Doe"}
                } else {
                    resp.getWriter().println("{\"status\":\"incorrectUsernamePassword\"}");
                    
                }

                conn.close();

            } catch (Exception e) {
                resp.getWriter().println(e.toString());
            }
        }

        if (req.getParameter("function").equals("getTruckProfile")) {
            String url = null;

            String getUsername = req.getParameter("username");
            resp.setContentType("text/html");

            //http://cst438-1139.appspot.com/test?function=getTruckProfile&username=truck

            try {
                Class.forName("com.mysql.jdbc.GoogleDriver");
                //format for url is........appname:mysqlinstancename/dbname?user=username&password=passsword
                url = "jdbc:google:mysql://cst438mysql:mysqlinstance/cst438db?user=root&password=banana438";
                Connection conn = DriverManager.getConnection(url);
                String query = "SELECT * FROM trucks WHERE username = '" + getUsername + "'";
                ResultSet rs = conn.createStatement().executeQuery(query);

                String username = null;
                String truckName = null;
                String website = null;
                String description = null;
                while (rs.next()) {
                    username = rs.getString("username");
                    truckName = rs.getString("truck_name");
                    website = rs.getString("website");
                    description = rs.getString("description");
                }

                if (username != null) {
                    //correct username

                    //fix the json
                    resp.getWriter().println("{\"status\":\"goodProfile\", \"username\":\"" + username + "\", \"truckName\":\"" + truckName + "\", \"website\":\"" + website + "\", \"description\":\"" + description + "\"}");
                    //{"firstName":"John", "lastName":"Doe"}
                } else {
                    resp.getWriter().println("{\"status\":\"invalidUsername\"}");
                    
                }

                conn.close();

            } catch (Exception e) {
                resp.getWriter().println(e.toString());
            }
            
        }

        if (req.getParameter("function").equals("postTruckProfile")) {
            String url = null;

            String getUsername = req.getParameter("username");
            String getTruckName = req.getParameter("truckName");
            String getWebsite = req.getParameter("website");
            String getDescription = req.getParameter("description");
            resp.setContentType("text/html");

            //http://cst438-1139.appspot.com/test?function=postTruckProfile&username=truck

            try {
                Class.forName("com.mysql.jdbc.GoogleDriver");
                //format for url is........appname:mysqlinstancename/dbname?user=username&password=passsword
                url = "jdbc:google:mysql://cst438mysql:mysqlinstance/cst438db?user=root&password=banana438";
                Connection conn = DriverManager.getConnection(url);
                String query = "UPDATE `trucks` SET `truck_name` = ?, `website` =  ?, `description` =  ? WHERE  `username` =  '" + getUsername + "'";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, getTruckName);
                stmt.setString(2, getWebsite);
                stmt.setString(3, getDescription);
                int success = 2;
                success = stmt.executeUpdate();

                if (success == 1) {
                    //echo out sucessful registration
                    resp.getWriter().println("{\"status\":\"successfulUpdate\"}");
                } else if (success == 0) {
                    resp.getWriter().println("{\"status\":\"databaseError\"}");
                }

                conn.close();

            } catch (Exception e) {
                resp.getWriter().println(e.toString());
            }
            
        }

        if (req.getParameter("function").equals("getFavorites")) {
            String url = null;

            String getUsername = req.getParameter("username");
            resp.setContentType("text/html");

            //http://cst438-1139.appspot.com/test?function=getTruckProfile&username=truck

            try {
                Class.forName("com.mysql.jdbc.GoogleDriver");
                //format for url is........appname:mysqlinstancename/dbname?user=username&password=passsword
                url = "jdbc:google:mysql://cst438mysql:mysqlinstance/cst438db?user=root&password=banana438";
                Connection conn = DriverManager.getConnection(url);
                //SELECT * FROM  `favorites` WHERE `username` = 'test'
                String query = "SELECT * FROM favorites WHERE username = '" + getUsername + "'";
                ResultSet rs = conn.createStatement().executeQuery(query);

                int i = 0;
                String truckName = null;

                String s = "{\"favorites\":[";

                //resp.getWriter().println("\"favorites\":[");

                while (rs.next()) {

                    truckName = rs.getString("truckname");
                    //resp.getWriter().println("{\"truckname\":\"" + truckName + "\"},");
                    s += "{\"truckname\":\"" + truckName + "\"},";
                    
                }
                s = s.replaceAll(",$", "");

                s+= "]}";

                resp.getWriter().println(s);

                conn.close();

            } catch (Exception e) {
                resp.getWriter().println(e.toString());
            }
            
        }

        if (req.getParameter("function").equals("getTrucks")) {
            String url = null;

            resp.setContentType("text/html");

            //http://cst438-1139.appspot.com/test?function=getTruckProfile&username=truck

            try {
                Class.forName("com.mysql.jdbc.GoogleDriver");
                //format for url is........appname:mysqlinstancename/dbname?user=username&password=passsword
                url = "jdbc:google:mysql://cst438mysql:mysqlinstance/cst438db?user=root&password=banana438";
                Connection conn = DriverManager.getConnection(url);
                //SELECT * FROM  `favorites` WHERE `username` = 'test'
                String query = "SELECT * FROM trucklocation";
                ResultSet rs = conn.createStatement().executeQuery(query);

                int i = 0;
                String truckName = null;
                String latitude = null;
                String longitude = null;

                String s = "{\"truckLocations\":[";

                //resp.getWriter().println("\"favorites\":[");

                while (rs.next()) {

                    truckName = rs.getString("truck_name");
                    latitude = rs.getString("latitude");
                    longitude = rs.getString("longitude");
                    //resp.getWriter().println("{\"truckname\":\"" + truckName + "\"},");
                    s += "{\"truckname\":\"" + truckName + "\", \"latitude\":\"" + latitude + "\", \"longitude\":\"" + longitude + "\"},";
                    
                }
                s = s.replaceAll(",$", "");

                s+= "]}";

                resp.getWriter().println(s);

                conn.close();

            } catch (Exception e) {
                resp.getWriter().println(e.toString());
            }
            
        }

        if (req.getParameter("function").equals("postLocation")) {
            String url = null;

            String getUsername = req.getParameter("username");
            String getLatitude = req.getParameter("latitude");
            String getLongitude = req.getParameter("longitude");
            resp.setContentType("text/html");

            //http://cst438-1139.appspot.com/test?function=getTruckProfile&username=truck

            try {
                Class.forName("com.mysql.jdbc.GoogleDriver");
                //format for url is........appname:mysqlinstancename/dbname?user=username&password=passsword
                url = "jdbc:google:mysql://cst438mysql:mysqlinstance/cst438db?user=root&password=banana438";
                Connection conn = DriverManager.getConnection(url);
                //UPDATE `trucklocation` SET `latitude` = '33.815456', `longitude` = '-117.923586' WHERE `truck_name` = 'Fat Burger'
                String query = "SELECT * FROM  `trucks` WHERE `username` = '" + getUsername + "'";
                ResultSet rs = conn.createStatement().executeQuery(query);

                int i = 0;
                String truckName = null;

                while (rs.next()) {
                    truckName = rs.getString("truck_name");
                }

                query = "SELECT * FROM `trucklocation` WHERE `truck_name` = '" + truckName + "'";
                rs = conn.createStatement().executeQuery(query);
                
                while (rs.next()) {
                    i++;
                }

                int success = 2;

                if (i > 0) {
                    query = "UPDATE `trucklocation` SET `latitude` = ?, `longitude` = ? WHERE `truck_name` = ?";

                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setString(1, getLatitude);
                    stmt.setString(2, getLongitude);
                    stmt.setString(3, truckName);
                    success = stmt.executeUpdate();
                    
                } else {

                    query = "INSERT INTO `trucklocation` (`truck_name`, `latitude`, `longitude`) VALUES (?, ?, ?)";

                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setString(1, truckName);
                    stmt.setString(2, getLatitude);
                    stmt.setString(3, getLongitude);
                    success = stmt.executeUpdate();

                }
    
                if (success == 1) {
                    //echo out sucessful registration
                    resp.getWriter().println("{\"status\":\"successfulUpdate\"}");
                } else if (success == 0) {
                    resp.getWriter().println("{\"status\":\"databaseError\"}");
                }

                conn.close();

            } catch (Exception e) {
                resp.getWriter().println(e.toString());
            }
            
        }

        if (req.getParameter("function").equals("editFavorites")) {
            String url = null;

            String getUsername = req.getParameter("username");
            String getAction = req.getParameter("action");
            String getValue = req.getParameter("value");
            resp.setContentType("text/html");

            //http://cst438-1139.appspot.com/test?function=getTruckProfile&username=truck

            try {
                Class.forName("com.mysql.jdbc.GoogleDriver");
                //format for url is........appname:mysqlinstancename/dbname?user=username&password=passsword
                url = "jdbc:google:mysql://cst438mysql:mysqlinstance/cst438db?user=root&password=banana438";
                Connection conn = DriverManager.getConnection(url);
                //UPDATE `trucklocation` SET `latitude` = '33.815456', `longitude` = '-117.923586' WHERE `truck_name` = 'Fat Burger'
                int success = 2;


                if (getAction.equals("add")) {
                    String query = "INSERT INTO `favorites` (`username` ,`truckname`)VALUES (?, ?)";

                    /*

                    INSERT INTO `favorites` (`username` ,`truckname`)VALUES (?, ?);

                    */
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setString(1, getUsername);
                    stmt.setString(2, getValue);
                    success = stmt.executeUpdate();

                    resp.getWriter().println("inside of  INSERT");
                    
                } else {
                    /*
                    DELETE FROM `favorites` WHERE `username` = ? AND `truckname` = ? LIMIT 1
                    */
                    String query = "DELETE FROM `favorites` WHERE `username` = ? AND `truckname` = ? LIMIT 1";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setString(1, getUsername);
                    stmt.setString(2, getValue);
                    success = stmt.executeUpdate();

                }
                
                if (success == 1) {
                    //echo out sucessful registration
                    resp.getWriter().println("{\"status\":\"successfulUpdate\"}");
                } else if (success == 0) {
                    resp.getWriter().println("{\"status\":\"databaseError\"}");
                }

                conn.close();

            } catch (Exception e) {
                resp.getWriter().println(e.toString());
            }
            
        }
    }
}