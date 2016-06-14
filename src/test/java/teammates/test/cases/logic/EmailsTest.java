package teammates.test.cases.logic;

import java.util.ArrayList;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.util.EmailWrapper;
import teammates.logic.core.Emails;
import teammates.test.cases.BaseComponentTestCase;

public class EmailsTest extends BaseComponentTestCase {
    
    @BeforeClass
    public static void classSetUp() {
        printTestClassHeader();
    }

    @Test
    public void testNoExceptionThrownWhenNoMessagesToSend() {
        new Emails().sendEmails(new ArrayList<EmailWrapper>());
    }
    
    @AfterClass
    public static void classTearDown() {
        printTestClassFooter();
    }
    
}
