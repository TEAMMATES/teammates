package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeedbackSessionResponseStatus {
    public List<String> hasResponse; 
    public List<String> noResponse;   
    public Map<String, String> emailNameTable;
    public Map<String, String> emailTeamNameTable;
    
    public FeedbackSessionResponseStatus() {        
        hasResponse = new ArrayList<String>();
        noResponse = new ArrayList<String>();
        emailNameTable = new HashMap<String, String>();
        emailTeamNameTable = new HashMap<String, String>();
    }
    
    public List<String> getStudentsWhoDidNotRespondToAnyQuestion() {
        Collections.sort(noResponse, compareByTeamNameStudentName);
        return noResponse;
    }
    
    @SuppressWarnings("unused")
    private void ________________COMPARATORS_____________(){}
    
    // Sorts by teamName > studentName
    public Comparator<String> compareByTeamNameStudentName
        = new Comparator<String>() {
        
        @Override
        public int compare(String s1, String s2) {

            // Compare between instructor and student
            // Instructor should be at higher order compared to student
            String teamName1 = emailTeamNameTable.get(s1);
            String teamName2 = emailTeamNameTable.get(s2);
            
            boolean isTeamName1Instructor = teamName1 == null;
            boolean isTeamName2Instructor = teamName2 == null;

            if(isTeamName1Instructor && !isTeamName2Instructor){
                // Team 1 has higher sorting order when team 1 belongs instructor and team 2 belongs to student
                // -1 represents team 1 is at higher order
                return -1;               
            }else if(!isTeamName1Instructor && isTeamName2Instructor){
                // Team 2 has higher sorting order when team 2 belongs instructor and team 1 belongs to student
                // 1 represents team 2 is at higher order
                return 1;                
            }else{
                // Either team 1 & 2 both belong to instructor or student
            }
            
            // Compare on names
            String name1 = emailNameTable.get(s1);
            String name2 = emailNameTable.get(s2);
            
            if(isTeamName1Instructor && isTeamName2Instructor){
                // Both teams belong to instructor
                // Compare instructor name
                return name1.compareToIgnoreCase(name2);
            }
            
            // Both teams belong to student
            // Compare team name
            int order = teamName1.compareToIgnoreCase(teamName2);
            if(order != 0){
                return order;
            }
            
            // Both students belong to the same team
            // Compare student name
            return name1.compareToIgnoreCase(name2);
        }
    };
}

