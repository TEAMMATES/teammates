package teammates.testing.lib;

import teammates.api.Common;


public class ImportTestData {

	public static void main(String args[]) throws Exception{
		System.out.println("====[START of Importing test data]====");
		long start = System.currentTimeMillis();
		setupPageVerificationData();
		System.out.println("Finished importing data in "+(System.currentTimeMillis()-start)+" ms");
		System.out.println("====[END of Importing test data]====");	
	}

	private static void setupPageVerificationData() throws Exception{
		System.out.println("Importing data for page verification ...");
		String jsonString = Common.readFile(Common.TEST_DATA_FOLDER+"/DataForHtmlVerification.json");
		BackDoor.deleteCoordinators(jsonString);
		System.out.println(BackDoor.persistNewDataBundle(jsonString));

	}
}