package teammates.test.pageobjects;

public class StudentEvalResultsPage extends AppPage {

    public StudentEvalResultsPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("<h1>Evaluation Results</h1>");
    }
    

}
