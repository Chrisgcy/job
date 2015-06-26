package LunaDB;

import java.sql.*;


public class GetShortRules {
	public static void main(String [] args) {
		PreparedStatement pstmt = null;
		try{
			
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection myConn = DriverManager.getConnection("jdbc:mysql://184.154.132.242:3306/yarogrou_lp_data_dev", "yarogrou", "IqTNCTsZDN");//database information
			Statement myStatement = myConn.createStatement();
			Statement myStatement2 = myConn.createStatement();
			Statement myStatement_test = myConn.createStatement();
			ResultSet myRs = myStatement.executeQuery("select * from tbl_ref_broom_rules");   //get rows from table tbl_ref_broom_rules
			ResultSetMetaData metaData = myRs.getMetaData();
			//fetch the row from DB
			while(myRs.next()){

				String strDesc = myRs.getString(3);              //START FROM INDEX 1; get the value of column 3
//				System.out.println(strDesc);
				strDesc = strDesc.replaceAll("'", "''");
				// split each row by whitespace, and then arrange these pieces into String Array.
				String [] parts = strDesc.split("\\s+");   //split value of column 3 by one or more whitespace and store into a String array.
				
				int endIndex = parts.length - 1;
				for(int i = 0; i < parts.length - 1; i++){
					
					if(parts[i].equals("NIGHT") || parts[i].contains("REGULATION") || parts[i].contains("MOON") || parts[i].contains("STAR") || parts[i].contains("&")){
						parts[i] = "";
					}
					if((parts[i].contains("BROOM")|| parts[i] == "SANITATION" || parts[i] == "SYMBOL" || parts[i].contains("(")
							|| parts[i].contains(")"))){
						if((parts[i].contains("AM") || parts[i].contains("PM")       //in case of the damn sentence like "....SYMBOL)8:00AM-9:00AM..." no white space between
								|| (parts[i].contains("MON") || parts[i].contains("TUE")|| parts[i].contains( "WED") || parts[i].contains("THU") 
										|| parts[i].contains("FRI") || parts[i].contains( "SAT") || parts[i].contains("SUN") || parts[i].contains("MIDNIGHT")))){
							String [] subPart = parts[i].split("\\)");
							parts[i] = subPart[1];
							
						}
						else{
						parts[i] ="";
						}
						
					}

					if((parts[i].contains("MON") || parts[i].contains("TUE")|| parts[i].contains( "WED") || parts[i].contains("THU") || parts[i].contains("FRI") || parts[i].contains( "SAT") || parts[i].contains("SUN") 
						|| (parts[i].contains("SUNDAY") || parts[i].contains("AM") || parts[i].contains("PM"))
							) && (!(parts[i+1].contains ("MON")) && !(parts[i+1].contains("TUE")) && !(parts[i+1].contains("WED")) && !(parts[i+1].contains("THU")) && !(parts[i+1].contains("FRI")) 
							&& !(parts[i+1].contains( "SAT")) && !(parts[i+1].contains("SUN")) && !(parts[i+1].contains("SUN")) && !(parts[i+1].contains( "&")) && !(parts[i+1].contains( "AM"))
							&& !(parts[i+1].contains( "PM")) && !(parts[i+1] == "-") && !(parts[i+1].contains("TO")) && !(parts[i+1].contains("EXCEPT")))){
						if(parts[i].contains("<-")){
						int markIndex = parts[i].indexOf('<');
						parts[i] = parts[i].substring(0,markIndex);
						}
						
						if(parts[i-1].contains("EXCEPT") && (parts[i].contains("SUNDAY") || parts[i].contains("SUN"))){   //convert "EXCEPT SUN" into "MON TUE WED THU FRI SAT"
							parts[i - 1] = "MON TUE WED THU FRI";                           
							parts[i] = "SAT";
							 
							
						}
						endIndex = i;    //endIndex indicates the end point of this string, we don't need the part after this point
						break;
					}
					
				}
				String newStrDesc = new String();
				for(int j = 0; j <= endIndex; j++){
				     newStrDesc += (parts[j] + " ");
				}
//				System.out.print(newStrDesc);
//				System.out.println(" "+ strDesc);
				String sqlInsert = "INSERT INTO tbl_broom_short_rule_test VALUES ('"+newStrDesc+"', '"+strDesc+"');";  //Insert the newly created short_desc and original desc into table short_rule_test
				try{
				myStatement2.executeUpdate(sqlInsert);
				}catch(Exception E){
					System.out.println("insert failed");
				}

			}

				
		}catch (Exception exc){
			exc.printStackTrace();
		}

}
}
