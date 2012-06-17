package teammates.testing.testcases;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Properties;

import org.junit.Test;

import teammates.Config;

public class ConfigTest {
	
	private String buildFile = System.getProperty("user.dir")+"\\src\\main\\webapp\\WEB-INF\\classes\\"+"build.properties";
	
	@Test
	public void testGetProperties() throws IOException{
		Properties properties = Config.getProperties(buildFile);
		assertTrue(null != properties.get("app.url"));
	}
	
	@Test
	public void testInst(){
		Config.inst(buildFile);
		//cannot test Config.inst() as the build.properties files is not in the
		//  folder being used for testing.
	}

}
