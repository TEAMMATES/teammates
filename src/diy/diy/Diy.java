package diy;
import java.util.HashMap;

public class Diy {

    private static HashMap<String, HashMap<Integer, Boolean>> coverage;

    public Diy() {
        coverage = new HashMap <String, HashMap<Integer, Boolean>>();
    }
    
    //sets the value of the specific ID of a specific functionName to true
    public static void setReachedId(String functionName, int Id) {
        coverage.get(functionName).replace(Id, true);
    } 
    
    //adds an empty <Integer, Boolean> HashMap to coverage based on functionName, then the empty 
    //Hashmap is filled with an Integer ID key ranging from 0 to numberOfIds with the value false
    public void generateFalseHashMap(String functionName, int numberOfIds){
        coverage.put(functionName, new HashMap<Integer, Boolean>());
        for (int i = 0; i < numberOfIds; i++){
            coverage.get(functionName).put(i, false);
        }
    }

}