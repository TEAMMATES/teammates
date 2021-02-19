package teammates;

import java.util.ArrayList;
import java.util.List;

public class Globals {

    private static Globals globalsInstance = new Globals();

    public static Globals getInstance() {
        return globalsInstance;
    }

    private List<Boolean> equalList = new ArrayList<>();

    private List<Boolean> getSessionResultsForUserList = new ArrayList<>();

    private List<Boolean> isResponseVisibleForUserList = new ArrayList<>();

    private List<Boolean> recipientsOfQuestionList = new ArrayList<>();

    private Globals() {
        for (int i = 0; i < 50; i++) {
            equalList.add(false);
        }

        for (int i = 0; i < 25; i++) {
            getSessionResultsForUserList.add(false);
        }

        for (int i = 0; i < 13; i++) {
            isResponseVisibleForUserList.add(false);
        }

        for (int i = 0; i < 43; i++) {
            recipientsOfQuestionList.add(false);
        }

    }

    public List<Boolean> getEqualList() {
        return equalList;
    }
    public List<Boolean> getGetSessionResultsForUserList() {
        return getSessionResultsForUserList;
    }
    public List<Boolean> getRecipientsOfQuestionList() {
        return recipientsOfQuestionList;
    }

    public void setEqualList(List<Boolean> equalList) {
        this.equalList = equalList;
    }

    public void setGetSessionResultsForUserList(List<Boolean> getSessionResultsForUserList) {
        this.getSessionResultsForUserList = getSessionResultsForUserList;
    }

    public void setRecipientsOfQuestionList(List<Boolean> list) {
        this.recipientsOfQuestionList = list;
    }


    public List<Boolean> getIsResponseVisibleForUserList() {return isResponseVisibleForUserList;}

    public void setIsResponseVisibleForUserList(List<Boolean> isResponseVisibleForUserList) {this.isResponseVisibleForUserList = isResponseVisibleForUserList;
    }
}
