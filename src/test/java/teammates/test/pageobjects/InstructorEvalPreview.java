package teammates.test.pageobjects;


public class InstructorEvalPreview extends AppPage {

    public InstructorEvalPreview(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains(
                "Previewing Evaluation as");
    }

}
