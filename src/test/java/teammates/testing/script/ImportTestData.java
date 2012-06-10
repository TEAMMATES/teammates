package teammates.testing.script;

import teammates.api.Common;
import teammates.testing.lib.TMAPI;


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
		String jsonString = Common.readFile(Common.TEST_DATA_FOLDER+"/page_verificationNew.json");
		TMAPI.deleteCoordinators(jsonString);
		System.out.println(TMAPI.persistNewDataBundle(jsonString));

	}
}