package teammates;

import java.util.ArrayList;
import java.util.List;

public class Globals {

    private static Globals globalsInstance = new Globals();

    public static Globals getInstance() {
        return globalsInstance;
    }

    private List<Boolean> equalList = new ArrayList<>();

    private Globals() {
        for (int i = 0; i < 50; i++) {
            equalList.add(false);
        }
    }

    public List<Boolean> getEqualList() {
        return equalList;
    }

    public void setEqualList(List<Boolean> equalList) {
        this.equalList = equalList;
    }
}
