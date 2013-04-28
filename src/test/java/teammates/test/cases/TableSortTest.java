package teammates.test.cases;

import static org.testng.AssertJUnit.assertEquals;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import java.io.File;

import org.openqa.selenium.By;

import teammates.test.driver.BrowserInstance;
import teammates.test.driver.BrowserInstancePool;

public class TableSortTest extends BaseTestCase {
	private static BrowserInstance bi;
	
	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
	
		bi = BrowserInstancePool.getBrowserInstance();		
	}
	
	@AfterClass
	public static void classTearDown() throws Exception {
		printTestClassFooter();
		BrowserInstancePool.release(bi);

	}
	
	private String getPath() throws Exception{
		String workingDirectory = new File(".").getCanonicalPath();
		return "file:///"+workingDirectory+"/src/test/resources/pages/tableSort.html";
	}
	
	@Test
	public void testTableSorting() throws Exception{
		//Sort testing Functions	
		bi.goToUrl(getPath());
		
		testTableSortingName();
		testTableSortingDate();
		testTableSortingID();
		testTableSortingPoint();
		testTableSortingDiff();
	}
	
	@Test
	public void testTableSortingID() throws Exception{
		bi.clickTableSortByIdButton();
		int column = 0;

		//Ascending
		assertStringWithRowColumn("-13.5",1,column);
		assertStringWithRowColumn("-2",2,column);
		assertStringWithRowColumn("-1.3",3,column);
		assertStringWithRowColumn("-0.001",4,column);
		assertStringWithRowColumn("0",5,column);
		assertStringWithRowColumn("1",6,column);
		assertStringWithRowColumn("2",7,column);
		assertStringWithRowColumn("3",8,column);
		assertStringWithRowColumn("10.01",9,column);
		assertStringWithRowColumn("10.3",10,column);
		assertStringWithRowColumn("10.35",11,column);
		assertStringWithRowColumn("10.7",12,column);
		assertStringWithRowColumn("15",13,column);
		assertStringWithRowColumn("24",14,column);
		assertStringWithRowColumn("33",15,column);

		
		bi.clickTableSortByIdButton();
		
		//Descending
		assertStringWithRowColumn("-13.5",15,column);
		assertStringWithRowColumn("-2",14,column);
		assertStringWithRowColumn("-1.3",13,column);
		assertStringWithRowColumn("-0.001",12,column);
		assertStringWithRowColumn("0",11,column);
		assertStringWithRowColumn("1",10,column);
		assertStringWithRowColumn("2",9,column);
		assertStringWithRowColumn("3",8,column);
		assertStringWithRowColumn("10.01",7,column);
		assertStringWithRowColumn("10.3",6,column);
		assertStringWithRowColumn("10.35",5,column);
		assertStringWithRowColumn("10.7",4,column);
		assertStringWithRowColumn("15",3,column);
		assertStringWithRowColumn("24",2,column);
		assertStringWithRowColumn("33",1,column);
		
	}
	
	@Test
	public void testTableSortingName() throws Exception{
		bi.clickTableSortByNameButton();
		
		int column = 1;
		
		//Ascending
		assertStringWithRowColumn("Ang Ji Kai",1,column);
		assertStringWithRowColumn("Chin Yong Wei",2,column);
		assertStringWithRowColumn("Chong Kok Wei",3,column);
		assertStringWithRowColumn("Hou GuoChen",4,column);
		assertStringWithRowColumn("Le Minh Khue",5,column);
		assertStringWithRowColumn("Loke Yan Hao",6,column);
		assertStringWithRowColumn("Luk Ming Kit",7,column);
		assertStringWithRowColumn("Phan Thi Quynh Trang",8,column);
		assertStringWithRowColumn("Shawn Teo Chee Yong",9,column);
		assertStringWithRowColumn("Shum Chee How",10,column);
		assertStringWithRowColumn("Sim ShengMing, Eugene",11,column);
		assertStringWithRowColumn("Tan Guo Wei",12,column);
		assertStringWithRowColumn("Teo Yock Swee Terence",13,column);
		assertStringWithRowColumn("Yen Zi Shyun",14,column);
		assertStringWithRowColumn("Zhang HaoQiang",15,column);
		
		
		
		bi.clickTableSortByNameButton();
		//Descending
		assertStringWithRowColumn("Ang Ji Kai",15,column);
		assertStringWithRowColumn("Chin Yong Wei",14,column);
		assertStringWithRowColumn("Chong Kok Wei",13,column);
		assertStringWithRowColumn("Hou GuoChen",12,column);
		assertStringWithRowColumn("Le Minh Khue",11,column);
		assertStringWithRowColumn("Loke Yan Hao",10,column);
		assertStringWithRowColumn("Luk Ming Kit",9,column);
		assertStringWithRowColumn("Phan Thi Quynh Trang",8,column);
		assertStringWithRowColumn("Shawn Teo Chee Yong",7,column);
		assertStringWithRowColumn("Shum Chee How",6,column);
		assertStringWithRowColumn("Sim ShengMing, Eugene",5,column);
		assertStringWithRowColumn("Tan Guo Wei",4,column);
		assertStringWithRowColumn("Teo Yock Swee Terence",3,column);
		assertStringWithRowColumn("Yen Zi Shyun",2,column);
		assertStringWithRowColumn("Zhang HaoQiang",1,column);
	}
	
	@Test
	public void testTableSortingDate() throws Exception{
		bi.clickTableSortByDateButton();
		
		int column = 2;
		
		//Ascending
		assertStringWithRowColumn("04/05/10",1,column);
		assertStringWithRowColumn("21/08/10",2,column);
		assertStringWithRowColumn("06/04/11",3,column);
		assertStringWithRowColumn("14/05/11",4,column);
		assertStringWithRowColumn("12/12/11",5,column);
		assertStringWithRowColumn("01/01/12",6,column);
		assertStringWithRowColumn("02/01/12",7,column);
		assertStringWithRowColumn("01/02/12",8,column);
		assertStringWithRowColumn("03/02/12",9,column);
		assertStringWithRowColumn("05/03/12",10,column);
		assertStringWithRowColumn("10/05/12",11,column);
		assertStringWithRowColumn("25/07/12",12,column);
		assertStringWithRowColumn("17/09/12",13,column);
		assertStringWithRowColumn("01/01/13",14,column);
		assertStringWithRowColumn("05/06/13",15,column);
		
		bi.clickTableSortByDateButton();
		
		//Descending
		assertStringWithRowColumn("04/05/10",15,column);
		assertStringWithRowColumn("21/08/10",14,column);
		assertStringWithRowColumn("06/04/11",13,column);
		assertStringWithRowColumn("14/05/11",12,column);
		assertStringWithRowColumn("12/12/11",11,column);
		assertStringWithRowColumn("01/01/12",10,column);
		assertStringWithRowColumn("02/01/12",9,column);
		assertStringWithRowColumn("01/02/12",8,column);
		assertStringWithRowColumn("03/02/12",7,column);
		assertStringWithRowColumn("05/03/12",6,column);
		assertStringWithRowColumn("10/05/12",5,column);
		assertStringWithRowColumn("25/07/12",4,column);
		assertStringWithRowColumn("17/09/12",3,column);
		assertStringWithRowColumn("01/01/13",2,column);
		assertStringWithRowColumn("05/06/13",1,column);
	}
	
	@Test
	public void testTableSortingDiff() throws Exception{
		bi.click(By.id("button_sortDiff"));
		
		int column = 4;
		
		//Ascending
		assertStringWithRowColumn("-99%",1,column);
		assertStringWithRowColumn("-20%",2,column);
		assertStringWithRowColumn("-10%",3,column);
		assertStringWithRowColumn("-2%",4,column);
		assertStringWithRowColumn("-1%",5,column);
		assertStringWithRowColumn("0%",6,column);
		assertStringWithRowColumn("+1%",7,column);
		assertStringWithRowColumn("+2%",8,column);
		assertStringWithRowColumn("+3%",9,column);
		assertStringWithRowColumn("+5%",10,column);
		assertStringWithRowColumn("+25%",11,column);
		assertStringWithRowColumn("+30%",12,column);
		assertStringWithRowColumn("+99%",13,column);
		assertStringWithRowColumn("N/A",14,column);
		assertStringWithRowColumn("N/A",15,column);
		
		bi.click(By.id("button_sortDiff"));
	
		//Descending
		assertStringWithRowColumn("-99%",15,column);
		assertStringWithRowColumn("-20%",14,column);
		assertStringWithRowColumn("-10%",13,column);
		assertStringWithRowColumn("-2%",12,column);
		assertStringWithRowColumn("-1%",11,column);
		assertStringWithRowColumn("0%",10,column);
		assertStringWithRowColumn("+1%",9,column);
		assertStringWithRowColumn("+2%",8,column);
		assertStringWithRowColumn("+3%",7,column);
		assertStringWithRowColumn("+5%",6,column);
		assertStringWithRowColumn("+25%",5,column);
		assertStringWithRowColumn("+30%",4,column);
		assertStringWithRowColumn("+99%",3,column);
		assertStringWithRowColumn("N/A",2,column);
		assertStringWithRowColumn("N/A",1,column);
	}
	
	@Test
	public void testTableSortingPoint() throws Exception{
		bi.click(By.id("button_sortPoint"));
		
		int column = 3;
		
		//Ascending
		assertStringWithRowColumn("E -99%",1,column);
		assertStringWithRowColumn("E -21%",2,column);
		assertStringWithRowColumn("E -10%",3,column);
		assertStringWithRowColumn("E -4%",4,column);
		assertStringWithRowColumn("E -2%",5,column);
		assertStringWithRowColumn("E 0%",6,column);
		assertStringWithRowColumn("E 0%",7,column);
		assertStringWithRowColumn("E +5%",8,column);
		assertStringWithRowColumn("E +20%",9,column);
		assertStringWithRowColumn("E +20%",10,column);
		assertStringWithRowColumn("E +99%",11,column);
		assertStringWithRowColumn("N/S",12,column);
		assertStringWithRowColumn("N/S",13,column);
		assertStringWithRowColumn("N/S",14,column);
		assertStringWithRowColumn("N/A",15,column);
		
		bi.click(By.id("button_sortPoint"));
		
		//Descending
		assertStringWithRowColumn("E -99%",15,column);
		assertStringWithRowColumn("E -21%",14,column);
		assertStringWithRowColumn("E -10%",13,column);
		assertStringWithRowColumn("E -4%",12,column);
		assertStringWithRowColumn("E -2%",11,column);
		assertStringWithRowColumn("E 0%",10,column);
		assertStringWithRowColumn("E 0%",9,column);
		assertStringWithRowColumn("E +5%",8,column);
		assertStringWithRowColumn("E +20%",7,column);
		assertStringWithRowColumn("E +20%",6,column);
		assertStringWithRowColumn("E +99%",5,column);
		assertStringWithRowColumn("N/S",4,column);
		assertStringWithRowColumn("N/S",3,column);
		assertStringWithRowColumn("N/S",2,column);
		assertStringWithRowColumn("N/A",1,column);
	}
	
	private void assertStringWithRowColumn(String text,int row,int column){
		assertEquals(text,bi.getCellFromDataTable(row,column));
	}
}
