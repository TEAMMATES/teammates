package teammates.test.util;

import teammates.test.driver.TestProperties;

public class Url extends teammates.common.util.Url {

    public Url(String url) {
        super(url);
    }
    
    @Override
    protected String getAppUrl() {
        return TestProperties.inst().TEAMMATES_URL;
    }
    
}
