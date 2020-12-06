package teammates.e2e.pageobjects;

/**
 * Page Object Model for home page.
 */
public class HomePage extends AppPage {

    public HomePage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getTitle().contains("TEAMMATES");
    }

    @Override
    public void waitForPageToLoad() {
        // The load state of this page cannot be determined using Angular testability
        // as there is a setInterval operation in the page (for the testimonial container),
        // which causes Angular testability to never stabilize as per their specification.
        //
        // Since this page is a static page, we can fall back to the old way of
        // just checking the document.readyState variable.
        browser.waitForPageReadyState();
    }

}
