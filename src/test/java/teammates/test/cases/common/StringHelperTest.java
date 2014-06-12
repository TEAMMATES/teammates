package teammates.test.cases.common;

import static org.testng.AssertJUnit.assertEquals;

import java.util.ArrayList;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.test.cases.BaseTestCase;

public class StringHelperTest extends BaseTestCase {

    @Test
    public void testGenerateStringOfLength() {
        
        assertEquals(5, StringHelper.generateStringOfLength(5).length());
        assertEquals(0, StringHelper.generateStringOfLength(0).length());
    }

    @Test
    public void testIsWhiteSpace() {
        
        assertEquals(true, StringHelper.isWhiteSpace(""));
        assertEquals(true, StringHelper.isWhiteSpace("       "));
        assertEquals(true, StringHelper.isWhiteSpace("\t\n\t"));
        assertEquals(true, StringHelper.isWhiteSpace(Const.EOL));
        assertEquals(true, StringHelper.isWhiteSpace(Const.EOL + "   "));
    }
    
    @Test
    public void testIsMatching() {
        assertEquals(true, StringHelper.isMatching("\u00E0", "à"));
        assertEquals(true, StringHelper.isMatching("\u0061\u0300", "à"));
        assertEquals(false, StringHelper.isMatching("Héllo", "Hello"));
    }

    @Test 
    public void testToStringForStringLists(){
        ArrayList<String> strings = new ArrayList<String>();
        assertEquals("", StringHelper.toString(strings, ""));
        assertEquals("", StringHelper.toString(strings, "<br>"));
        
        strings.add("aaa");
        assertEquals("aaa", StringHelper.toString(strings, ""));
        assertEquals("aaa", StringHelper.toString(strings, "\n"));
        assertEquals("aaa", StringHelper.toString(strings, "<br>"));
        
        strings.add("bbb");
        assertEquals("aaabbb", StringHelper.toString(strings, ""));
        assertEquals("aaa\nbbb", StringHelper.toString(strings, "\n"));
        assertEquals("aaa<br>bbb", StringHelper.toString(strings, "<br>"));
    }

    @Test
    public void testKeyEncryption() {
        String msg = "Test decryption";
        String decrptedMsg;
        
        decrptedMsg = StringHelper.decrypt(StringHelper.encrypt(msg));
        assertEquals(msg, decrptedMsg);
    }

}
