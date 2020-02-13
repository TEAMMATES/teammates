public class Diy {

    private HashMap<String, HashMap<Integer, boolean>> coverage;
    private String[] functions = [];


    public Diy() {

        coverage = HashMap <>();
        for (int i = 0; i < functions.length; i++) {
            coverage.put(functions[i], new HashMap<Integer, boolean>());
        }
        // Somehow build coverage hash maps
    }


    public setReachedId(String functionName, int Id) {
        coverage.get(functionName).put(Id, true)
    }

}