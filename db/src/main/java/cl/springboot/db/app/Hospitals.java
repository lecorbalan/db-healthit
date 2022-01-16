package cl.springboot.db.app;

import java.sql.*;

public class Hospitals {
    final String JDBC_DRIVER = "org.h2.Driver";
    final String DB_URL = "jdbc:h2:mem:HEALTHIT";
    final String USER = "sa";
    final String PASS = "";	
	private String region;
	private String region_code;
	private String period;
	private String pct_hospitals_mu;
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getRegion_code() {
		return region_code;
	}
	public void setRegion_code(String region_code) {
		this.region_code = region_code;
	}
	public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
	}
	public String getPct_hospitals_mu() {
		return pct_hospitals_mu;
	}
	public void setPct_hospitals_mu(String pct_hospitals_mu) {
		this.pct_hospitals_mu = pct_hospitals_mu;
	}
	@Override
	public String toString() {

    	Connection conn = null;
    	Statement stmt = null;
    	 try {
             Class.forName(JDBC_DRIVER);
             conn = DriverManager.getConnection(DB_URL, USER, PASS);
             stmt = conn.createStatement();
             String sql2 = "INSERT INTO US_HOSPITALS VALUES ('" + region +"', '" + region_code + "', '" + period + "', '" + pct_hospitals_mu + "')";
             stmt.executeUpdate(sql2);
             
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
		
		return "Hospitals [region=" + region + ", region_code=" + region_code + ", period=" + period
				+ ", pct_hospitals_mu=" + pct_hospitals_mu + "]";
	}
	
}
