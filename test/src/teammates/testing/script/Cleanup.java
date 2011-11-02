package teammates.testing.script;

import java.io.IOException;

import teammates.testing.lib.TMAPI;
import teammates.testing.object.Scenario;

/**
 * Clean up the remote's Teammates server's data
 * 
 * @author nvquanghuy
 * 
 */
public class Cleanup {
	public static void main(String args[]) throws IOException {
		TMAPI.cleanup();
		TMAPI.updateBumpRatio();

	}
}
