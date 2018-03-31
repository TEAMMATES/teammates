package teammates.test.cases.browsertests;

import org.openqa.selenium.By;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.test.pageobjects.AppPage;

/**
 * Verifies that the table sorting functions work.
 */
public class TableSortTest extends BaseUiTestCase {
    private AppPage page;

    @Override
    protected void prepareTestData() {
        // no test data used in this test
    }

    @BeforeClass
    public void classSetup() {
        loginAdmin();
        page = AppPage.getNewPageInstance(browser).navigateTo(createUrl(Const.ViewURIs.TABLE_SORT));
    }

    @Test
    public void testTableSortingId() {
        verifySortingOrder(By.id("button_sortid"),

                "-13.5",
                "-2",
                "-1.3",
                "-0.001",
                "0",
                "1",
                "2",
                "3",
                "10.01",
                "10.3",
                "10.35",
                "10.7",
                "15",
                "24",
                "33");

    }

    @Test
    public void testTableSortingName() {

        verifySortingOrder(By.id("button_sortname"),

                "Ang Ji Kai",
                "Chin Yong Wei",
                "Chong Kok Wei",
                "Hou GuoChen",
                "Le Minh Khue",
                "Le Minh Khue",
                "Le Minh Khue",
                "Le Minh Khue",
                "Loke Yan Hao",
                "Luk Ming Kit",
                "Phan Thi Quynh Trang",
                "Shum Chee How",
                "Tan Guo Wei",
                "Teo Yock Swee Terence",
                "Zhang HaoQiang");

    }

    @Test
    public void testTableSortingDate() {

        verifySortingOrder(By.id("button_sortdate"),

                "04 May 2010",
                "21 August 2010",
                "06 April 2011",
                "14 May 2011",
                "12 December 2011",
                "01 January 2012",
                "02 January 2012",
                "01 February 2012",
                "03 February 2012",
                "05 March 2012",
                "10 May 2012",
                "25 July 2012",
                "17 September 2012",
                "01 January 2013",
                "05 June 2013");
    }

    @Test
    public void testTableSortingDiff() {

        verifySortingOrder(By.id("button_sortDiff"),

                "-99%",
                "-20%",
                "-10%",
                "-2%",
                "-1%",
                "0%",
                "+1%",
                "+2%",
                "+3%",
                "+5%",
                "+25%",
                "+30%",
                "+99%",
                "N/A",
                "N/A");

    }

    @Test
    public void testTableSortingPoint() {
        verifySortingOrder(By.id("button_sortPoint"),

                "E -99%",
                "E -21%",
                "E -10%",
                "E -4%",
                "E -2%",
                "E 0%",
                "E 0%",
                "E +5%",
                "E +20%",
                "E +20%",
                "E +99%",
                "N/S",
                "N/S",
                "N/S",
                "N/A");
    }

    @Test
    public void testTableSortingPointNumber() {
        verifySortingOrder(By.id("button_sortPointNumber"),

                "-1.667",
                "-1.51",
                "-1",
                "-0.5",
                "-0.4",
                "-0.1",
                "0",
                "0.2",
                "0.333",
                "0.45",
                "0.9",
                "1",
                "1.1",
                "1.333",
                "1.45");
    }

    @Test
    public void testStableSort() {
        page.click(By.id("button_sortid"));
        page.click(By.id("button_sortid"));
        page.click(By.id("button_sortname"));

        String[] idList = {
                "15",
                "0",
                "-13.5",
                "2",
                "-2",
                "-1.3",
                "10.01",
                "24",
                "1",
                "10.7",
                "3",
                "33",
                "10.35",
                "-0.001",
                "10.3"
        };

        StringBuilder searchString = new StringBuilder();
        for (String id : idList) {
            searchString.append(id).append("{*}");
        }
        page.verifyContains(searchString.toString());

        page.click(By.id("button_sortname"));
        String[] reversedIdList = {
                "10.3",
                "-0.001",
                "10.35",
                "33",
                "3",
                "10.7",
                "1",
                "-2",
                "-1.3",
                "10.01",
                "24",
                "2",
                "-13.5",
                "0",
                "15"
        };

        searchString = new StringBuilder();
        for (String id : reversedIdList) {
            searchString.append(id).append("{*}");
        }
        page.verifyContains(searchString.toString());
    }

    private void verifySortingOrder(By sortIcon, String... values) {
        //check if the rows match the given order of values
        page.click(sortIcon);
        StringBuilder searchString = new StringBuilder();
        for (String value : values) {
            searchString.append(value).append("{*}");
        }
        page.verifyContains(searchString.toString());

        //click the sort icon again and check for the reverse order
        page.click(sortIcon);
        searchString = new StringBuilder();
        for (int i = values.length; i > 0; i--) {
            searchString.append(values[i - 1]).append("{*}");
        }
        page.verifyContains(searchString.toString());
    }

}
