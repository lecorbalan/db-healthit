package cl.springboot.db.app;

import java.sql.*;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

@SpringBootApplication
public class DbApplication {

	private static final String POSTS_API_URL = "https://www.healthit.gov/data/open-api?source=Meaningful-Use-Acceleration-Scorecard.csv";
	
	public static void main( String[] args ) throws IOException, InterruptedException {
		SpringApplication.run(DbApplication.class, args);
		
	}
	
	@Bean
	CommandLineRunner runner() {
	    final String JDBC_DRIVER = "org.h2.Driver";
	    final String DB_URL = "jdbc:h2:mem:HEALTHIT";
	    final String USER = "sa";
	    final String PASS = "";
	    return args -> {
	    	Connection conn = null;
	    	Statement stmt = null;
	    	 try {
	             //Register JDBC driver
	             Class.forName(JDBC_DRIVER);

	             //Open a connection
	             System.out.println("Connecting to a selected database...");
	             conn = DriverManager.getConnection(DB_URL, USER, PASS);
	             System.out.println("Connected database successfully...");

	             //Execute a query
	             System.out.println("Creating table in given database...");
	             stmt = conn.createStatement();

	             String sql = "CREATE TABLE US_HOSPITALS " +
	                     "(REGION VARCHAR(255), " +
	                     " REGION_CODE VARCHAR(255), " +
	                     " PERIOD VARCHAR(255), " +
	                     " PCT_HOSPITALS_MU VARCHAR(255))";

	             stmt.executeUpdate(sql);
	             System.out.println("Created table US_HOSPITALS in HEALTHIT database...");
	             
	         } catch (SQLException se) {
	             //Handle errors for JDBC
	             se.printStackTrace();
	         } catch (Exception e) {
	             //Handle errors for Class.forName
	             e.printStackTrace();
	         } finally {
	             //finally block used to close resources
	             try {
	                 if (stmt!=null)
	                     conn.close();
	             } catch (SQLException se) {
	             } // do nothing
	             try {
	                 if (conn!= null)
	                     conn.close();
	             } catch (SQLException se) {
	                 se.printStackTrace();
	             } // end finally try
	         } // end try
	    	 
	    	 
	     	HttpClient client= HttpClient.newHttpClient();
	    	HttpRequest request = HttpRequest.newBuilder()
	    			.GET()
	    			.header("accept", "application/json")
	    			.uri(URI.create(POSTS_API_URL))
	    			.build();
	    	HttpResponse<String> response = client.send(request, BodyHandlers.ofString());   			

	    	ObjectMapper mapper = new ObjectMapper();
	    	mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	    			
	    	List<Hospitals> hospitals = mapper.readValue(response.body(), new TypeReference<List<Hospitals>>() {});
	    	
    	    String[] itemsArray = new String[hospitals.size()];
	        for(int i=0; i<hospitals.size(); i++) {
	        	Hospitals hosp = hospitals.get(i);
	        	itemsArray[i]=hosp.toString();
	        }
	        
	        //Finally perform the solicited query:
	        //Print out, by state and in descending order, the PERCENTAGE (*) of eligible and critical access
	        //hospitals that have demonstrated Meaningful Use of CEHRT in the year 2014.
	        //i.e: ""SELECT * FROM US_HOSPITALS where PERIOD='2014' order by PCT_HOSPITALS_MU DESC,REGION"
	        //(*) Note: The PERCENTAGE is given in the API source in decimals numbers, in the range [0..1], where 0 is 0% and 1 is 100%

	    	 try {
	             Class.forName(JDBC_DRIVER);
	             conn = DriverManager.getConnection(DB_URL, USER, PASS);
	             stmt = conn.createStatement();
	             String sql3 = "SELECT * FROM US_HOSPITALS where PERIOD='2014' order by PCT_HOSPITALS_MU DESC,REGION";
	             ResultSet rs = stmt.executeQuery(sql3);
	             while (rs.next()) {
	            	 //(*) Note: The PERCENTAGE is given from the API Endpoint in float numbers, in the range [0..1], where 0 is 0% and 1 is 100%
	            	 //Because of that the following conversion from the H2 database for display the output in percentages between 0% and 100%.  
	            	 String all = rs.getString(1) + ", " + rs.getString(2) + ", " + rs.getString(3) + ", " + Integer.toString((int) (Float.parseFloat(rs.getString(4))*100)) + "%";
	            	 
	            	 //This line shows the raw data for the PERCENTAGE i.e. in the range [0..1]
	            	 //String all = rs.getString(1) + ", " + rs.getString(2) + ", " + rs.getString(3) + ", " + rs.getString(4);
	            	 
	            	 System.out.println(all);
	             }

	             
	         } catch (SQLException se) {
	             se.printStackTrace();
	         } catch (Exception e) {
	             e.printStackTrace();
	         } finally {
	             try {
	                 if (stmt!=null)
	                     conn.close();
	             } catch (SQLException se) {
	             } 
	             try {
	                 if (conn!= null)
	                     conn.close();
	             } catch (SQLException se) {
	                 se.printStackTrace();
	             }
	         }

	        //
	         System.out.println("Goodbye!");
	    	
	    };
	}
		
	

}
