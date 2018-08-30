package teammates.test.pageobjects;

/**
 * This is used as the page type when we want to navigate to a Url without
 * bothering about the exact type of the page.
 */
public class GenericAppPage extends AppPage {

    public GenericAppPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return true;
    }

}
