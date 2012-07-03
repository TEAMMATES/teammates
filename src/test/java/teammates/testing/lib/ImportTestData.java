package teammates.testing.lib;

import teammates.api.Common;

/** Running this class will import data in DataForHtmlVerification.json 
 *    to the app. This data is used 
 * 
 */
public class ImportTestData {

	public static void main(String args[]) throws Exception{
		persistData(Common.TEST_DATA_FOLDER+"/DataForHtmlVerification.json");
	}

	private static void persistData(String dataFile) throws Exception{
		System.out.println("====[START of Importing test data]====");
		long start = System.currentTimeMillis();
		System.out.println("Importing data for page verification ...");
		String jsonString = Common.readFile(dataFile);
		String restoreStatus = BackDoor.restoreDataBundle(jsonString);
		System.out.println(restoreStatus);
		System.out.println("Finished importing data in "+(System.currentTimeMillis()-start)+" ms");
		System.out.println("====[END of Importing test data]====");	
	}
}