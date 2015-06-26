package lunaparking3;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

public class ParkingValidation {
	public static void main(String [] args) {
		PreparedStatement pstmt = null;
		try{
			
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection myConn = DriverManager.getConnection("jdbc:mysql://184.154.132.242:3306/yarogrou_lp_data_dev", "yarogrou", "IqTNCTsZDN");//database information
			Statement myStatement = myConn.createStatement();
			Statement myStatement2 = myConn.createStatement();
			Statement myStatement_test = myConn.createStatement();
			ResultSet myRs = myStatement.executeQuery("SELECT * FROM tbl_data_temp");   //get rows from table tbl_ref_broom_rules
			ResultSetMetaData metaData = myRs.getMetaData();
			//fetch the row from DB
			String ans = null;
			while(myRs.next()){
				String ruleID = myRs.getString(1);
				String ruleSegNum = myRs.getString(3);
				int signTypeflg = 1;
				String signperiodseg = myRs.getString(5);
				int monthyearflg=0;
				int dayweekflg=1;
				int timedayflg=1;
				int parkinginterval=0;
				String strDesc = myRs.getString(5);
//				System.out.println(strDesc);	
				String signtype=myRs.getString(2);
				if(signtype.contains("Broom")){
					ans=checkBroom(signperiodseg);
				}
				else if(signtype.contains("Interval")){
					ans=checkInterval(signperiodseg);
				}else{
					continue;
				}
				
				System.out.println(ruleID);
				System.out.println(ans);
				
				String sqlInsert = "INSERT INTO tbl_ref_parking_rules_validation(rule_id,rule_segment_num,sign_type_flg,sign_period_seg,month_of_year_flg,day_of_week_flg,time_of_day_flg,parking_interval,rule_desc) VALUES ('"+ruleID+"', '"+ruleSegNum+"','"+signTypeflg+"','"+ans+"','"+monthyearflg+"','"+dayweekflg+"','"+timedayflg+"','"+parkinginterval+"','"+strDesc+"');";  //Insert the newly created short_desc and original desc into table short_rule_test
				try{
				myStatement2.executeUpdate(sqlInsert);
				}catch(Exception E){
					System.out.println("insert failed"+E.getMessage());
				}

			}

				
		}catch (Exception exc){
			exc.printStackTrace();
		}

	}
	
	public static String checkBroom(String signperiodseg){

		
		signperiodseg = signperiodseg.replaceAll("TO","-");
		
		int startIndex=0;
		int endIndex=signperiodseg.length();
		
		if(signperiodseg.contains("SEDE")){
					 endIndex = signperiodseg.indexOf("SEDE");
				 }   //could not simply stop getting the string when meet a '<' or '>', because many Combo Type have multiple '<' or '>'

				 if(signperiodseg.contains("SEE") && endIndex == signperiodseg.length()){
					endIndex = signperiodseg.indexOf("SEE");
				}
				if(signperiodseg.contains("RIDER") && endIndex == signperiodseg.length()){  //should not cut
					endIndex = signperiodseg.indexOf("RIDER");
				}
				if(signperiodseg.contains("SUPERSEDES")){
					endIndex = signperiodseg.indexOf("SUPERSEDES");
				}
				if(signperiodseg.contains("(SUPERSEDE")){
					endIndex = signperiodseg.indexOf("(SUPERSEDE");
				}
				if(signperiodseg.contains("SUPRSEDES")){
					endIndex = signperiodseg.indexOf("SUPRSEDES");
				}
				if(signperiodseg.contains(" SU ")){
					endIndex = signperiodseg.indexOf(" SU ");
				}
				if(signperiodseg.contains("DO ") && endIndex == signperiodseg.length()){
					endIndex = signperiodseg.indexOf("DO ");
				}
				if (signperiodseg.contains("REV ") && endIndex == signperiodseg.length()) {
					endIndex = signperiodseg.indexOf("REV ");
				}
				
				if(signperiodseg.contains("(USE ") && endIndex == signperiodseg.length()){
					endIndex = signperiodseg.indexOf("(USE ");
				}
				if(signperiodseg.contains("CORRECTED") && endIndex == signperiodseg.length()){
					endIndex = signperiodseg.indexOf("CORRECTED");
				}
				
				signperiodseg = signperiodseg.substring(startIndex, endIndex);
		
		String[] parts =  signperiodseg.split("\\s+");
		int tmp=parts.length;
		for(int i = 0; i < parts.length; i++){
			
			
			if(parts[0].equals("1") || parts[i].contains("HOUR") || parts[i].contains("METERED") || parts[i].contains("PARKING") || parts[i].contains("NO")){
				parts[0]="";
				parts[i]="";
			}
			if(parts[i].equals("-3AM")){
				parts[i]="3AM";
			}
			
			if(parts[i].equals("-6AM")){
				parts[i]="6AM";
			}
			
			if(parts[i].equals("-7AM")){
				parts[i]="7AM";
			}
			
			if(parts[i].equals("-8AM")){
				parts[i]="8AM";
			}
			
			if(parts[i].contains("NIGHT") || parts[i].contains("REGULATION") || parts[i].contains("(HALF") || parts[i].contains("MOON") || parts[i].contains("&") || parts[i].contains("/") || parts[i].contains("STAR") || parts[i].contains("SYMBOLS)") || parts[i].contains("SYMBOL)") || parts[i].contains("NO") || parts[i].contains("PARKING") || parts[i].contains("-(SANITATION") || parts[i].contains("SANITATION") || parts[i].contains("BROOM") || parts[i].equals("SP-403C") || parts[i].equals("DATED") || parts[i].equals("6-17-92)")){
				if(parts[i].equals("MIDNIGHT")){
					parts[i]="MIDNIGHT";
				}
				if(parts[i].equals("MIDNIGHT-3AM")){
					parts[i]="MIDNIGHT-3AM";
				}
				if(parts[i].equals("SYMBOL)11:30AM-1PM")){
					parts[i]="11:30AM-1PM";
				}
				
				else {parts[i]="";}
			}
			
			if(parts[i].contains("<--->") || parts[i].contains("<----->") || parts[i].contains("<-->") || parts[i].contains("<->") || parts[i].contains("-->") || parts[i].contains("<---->")){
							if(parts[i].equals("FRI<--->")){
								parts[i]="FRI";
							}
							if(parts[i].equals("FRI<--->SUPERSEDES")){
								parts[i]="FRI";
							}
							else{
							parts[i]="";
							}
						}
			
			
			
			}
			String newSpseg = new String();
			for(int j = 0; j < tmp; j++){
				newSpseg += (parts[j] + " ");
			}
			return newSpseg;

				
	}
	
	public static String checkInterval(String signperiodseg){
		
signperiodseg = signperiodseg.replaceAll("TO","-");
		
		int startIndex=0;
		int endIndex=signperiodseg.length();
		
		if(signperiodseg.contains("SEDE")){
					 endIndex = signperiodseg.indexOf("SEDE");
				 }   //could not simply stop getting the string when meet a '<' or '>', because many Combo Type have multiple '<' or '>'

				 if(signperiodseg.contains("SEE") && endIndex == signperiodseg.length()){
					endIndex = signperiodseg.indexOf("SEE");
				}
				if(signperiodseg.contains("RIDER") && endIndex == signperiodseg.length()){  //should not cut
					endIndex = signperiodseg.indexOf("RIDER");
				}
				if(signperiodseg.contains("SUPERSEDES")){
					endIndex = signperiodseg.indexOf("SUPERSEDES");
				}
				if(signperiodseg.contains("(SUPERSEDE")){
					endIndex = signperiodseg.indexOf("(SUPERSEDE");
				}
				if(signperiodseg.contains("SUPRSEDES")){
					endIndex = signperiodseg.indexOf("SUPRSEDES");
				}
				if(signperiodseg.contains(" SU ")){
					endIndex = signperiodseg.indexOf(" SU ");
				}
				if(signperiodseg.contains("DO ") && endIndex == signperiodseg.length()){
					endIndex = signperiodseg.indexOf("DO ");
				}
				if (signperiodseg.contains("REV ") && endIndex == signperiodseg.length()) {
					endIndex = signperiodseg.indexOf("REV ");
				}
				
				if(signperiodseg.contains("(USE ") && endIndex == signperiodseg.length()){
					endIndex = signperiodseg.indexOf("(USE ");
				}
				if(signperiodseg.contains("CORRECTED") && endIndex == signperiodseg.length()){
					endIndex = signperiodseg.indexOf("CORRECTED");
				}
				
				signperiodseg = signperiodseg.substring(startIndex, endIndex);
		
		String[] parts =  signperiodseg.split("\\s+");
		int tmp=parts.length;
		for(int i = 0; i < parts.length; i++){
			if(parts[0].equals("1") || parts[i].contains("HMP") || parts[i].contains("HOUR") || parts[i].contains("METERED") || parts[i].contains("PARKING") || parts[i].contains("HR") || parts[i].contains("MUNI-METER") || parts[0].equals("2")){
				parts[0]="";
				parts[i]="";
			}
			
			if(parts[i].contains("<--->") || parts[i].contains("<----->") || parts[i].contains("<-->") || parts[i].contains("<->") || parts[i].contains("-->") || parts[i].contains("<---->")){
				parts[i]="";
			}
		}
		
		String newSpseg = new String();
		for(int j = 0; j < tmp; j++){
			newSpseg += (parts[j] + " ");
		}
		return newSpseg;
	}
		
}
