package lunaparking;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;




public class TableValidation {
    public static void main(String[] args) throws FileNotFoundException {
        String sql_Query = "SELECT * FROM tbl_data_parking_signs_unique_ALL_copy";
        Date date = new Date();
        PrintStream out = new PrintStream(new FileOutputStream("output("+date.toString()+").txt"));   //default directory is the project's directory
        System.setOut(out);
        try{
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection myConn = DriverManager.getConnection("jdbc:mysql://184.154.132.242:3306/yarogrou_lp_data_dev", "yarogrou", "IqTNCTsZDN");//database information
            Statement statSelect = myConn.createStatement();               //export the print out into a txt file.
            ResultSet myRs = statSelect.executeQuery(sql_Query);
            int count = 0;
            HashMap <Integer, Integer> map = new HashMap<Integer, Integer>();
            TableValidation tv = new TableValidation();
            out.print("ruleID"+","+"SignType"+","+ "seq_amount"+","+"length"+","+"Desc"+"\n");
            while(myRs.next()){
                count ++;
                String strDesc = myRs.getString(4);
                String vehType = myRs.getString(2);
                String ruleID = myRs.getString(1);
                int counttttt = 0;
                if(vehType.contains("Broom")){
                    counttttt = checkRuleSeq(strDesc);
                }else if(vehType.contains("Interval")){
                    counttttt = checkInterval(strDesc);
                }
                
                try{
                    
                    out.print(ruleID+","+vehType +"," + counttttt +","+ strDesc.length()+","+strDesc+"\n");
                    
                    
                }catch(Exception E){
                    System.out.println(E.getMessage());
                }
                
                //				out.println(ruleID+" "+vehType+" "+strDesc + " "+ count);
                
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    //check the number of sequences
    public static int checkRuleSeq(String strDesc){
        
        //		while(strDesc.indexOf("SUPERSEDES")<=0 && i < strDesc.length()){
        //			sb.append(strDesc.charAt(i));
        //			i++;
        //		}
        int firstSeqHours = -1;
        int startIndex = 0;
        int endIndex = strDesc.length();
        String [] parts =  strDesc.split("\\s+");
        
        if(isInteger(parts[0])){
            startIndex = parts[0].length();
            firstSeqHours = Integer.parseInt(parts[0]);
        }
        // when meet each of following string, cut the rest part after that.
        if(strDesc.contains("SEDE")){
            endIndex = strDesc.indexOf("SEDE");
        }   //could not simply stop getting the string when meet a '<' or '>', because many Combo Type have multiple '<' or '>'
        
        if(strDesc.contains("SEE") && endIndex == strDesc.length()){
            endIndex = strDesc.indexOf("SEE");
        }
        if(strDesc.contains("RIDER") && endIndex == strDesc.length()){  //should not cut
            endIndex = strDesc.indexOf("RIDER");
        }
        if(strDesc.contains("SUPEREDES")){
            endIndex = strDesc.indexOf("SUPEREDES");
        }
        //		if(strDesc.contains("SUPRSEDES")){
        //			endIndex = strDesc.indexOf("SUPRSEDES");
        //		}
        if(strDesc.contains(" SU ")){
            endIndex = strDesc.indexOf(" SU ");
        }
        if(strDesc.contains("DO ") && endIndex == strDesc.length()){
            endIndex = strDesc.indexOf("DO ");
        }
        if (strDesc.contains("REV ") && endIndex == strDesc.length()) {
            endIndex = strDesc.indexOf("REV ");
        }
        
        if(strDesc.contains("(USE ") && endIndex == strDesc.length()){
            endIndex = strDesc.indexOf("(USE ");
        }
        if(strDesc.contains("CORRECTED") && endIndex == strDesc.length()){
            endIndex = strDesc.indexOf("CORRECTED");
        }
        
        //if(endIndex > 0){
        strDesc = strDesc.substring(startIndex, endIndex);
        //}
        
        
        strDesc = strDesc.replaceAll("TO","-");
        strDesc = strDesc.replaceAll("to","-");
        //		String [] parts = strDesc.split("\\s+");
        strDesc = strDesc.replaceAll("=", "-");
        strDesc = strDesc.replaceAll(":", "");
        //										   (\\d+\\s*([A]*\\s*[M]*|[P]\\s*[M])|NOON|MID|MIDNIGHT)\\s*(\\-+|\\s*)\\s*(\\d+\\s*([A]*\\s*[M]*|[P]\\s*[M])|NOON|MID|MIDNIGHT)
        //Can only handle Broom Signs.
        Pattern pattern = Pattern.compile("(\\d+\\s*([A]*\\s*[M]*|[P]\\s*[M])|NOON|MID|MIDNIGHT)\\s*(\\-+|\\s*)\\s*(\\d+\\s*([A]*\\s*[M]*|[P]\\s*[M])|NOON|MID|MIDNIGHT)");
        Matcher  matcher = pattern.matcher(strDesc);
        
        
        
        int countFortime = 0;
        while (matcher.find()){
            countFortime++;
        }
        
        return countFortime;
        
    }
    
    public static int checkInterval(String strDesc){
        strDesc = strDesc.replaceAll(" AM", "AM");
        strDesc = strDesc.replaceAll(" TO ","-");
        strDesc = strDesc.replaceAll(" PM", "PM");
        //strDesc = strDesc.replaceAll("to","-");
        //strDesc = strDesc.replaceAll("=", "-");
        //strDesc = strDesc.replaceAll(":", "");
        
        String[] ss=strDesc.split(" ");
        int counter = 0;
        for (int i=0;i<ss.length;i++) {
            if(ss[i].contains("-")) {
                if(ss[i].contains("AM") || ss[i].contains("PM") || ss[i].contains("MID")||ss[i].contains("8-6")) {
                    counter++;
                }
            }
        }
        
        return counter;
        
    }
    
    public static boolean isInteger(String s){          //check if a string implies a integer.
        
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }
    
    public String parseDesc(String strDesc){
        strDesc = strDesc.replaceAll("TO","-");
        strDesc = strDesc.replaceAll("to","-");
        strDesc = strDesc.replaceAll(":00","");
        
        
        
        String [] segments = strDesc.split("\\s+");
        StringBuilder time = new StringBuilder();
        StringBuilder day  = new StringBuilder();
        StringBuilder result = new StringBuilder();
        HashMap <Integer, String> meaning = new HashMap<Integer, String>();
        for(int i = 0; i < segments.length; i++){
            if(segments[i].contains("MON")){
                day.append("MON");
            }
            if(segments[i].contains("TUE")){
                day.append("TUE");
            }
            if(segments[i].contains("WED")){
                day.append("WED");
            }
            if(segments[i].contains("THU")){
                day.append("THU");
            }
            if(segments[i].contains("FRI")){
                day.append("FRI");
            }
            if(segments[i].contains("SAT")){
                day.append("SAT");
            }
            if(segments[i].contains("SUN")){
                day.append("SUN");
            }
            if(segments[i].contains("AM") || segments[i].contains("PM") || segments[i].contains("MIDNIGHT") || segments[i].contains("NOON") ||segments[i].matches("\\d+")){
                time.append(segments[i]);
            }
            else if(segments[i].contains("-")){
                time.append(segments[i]);
            }
            
        }
        result = result.append(day).append(time);
        String res = result.toString();
        return res;
    }
}
