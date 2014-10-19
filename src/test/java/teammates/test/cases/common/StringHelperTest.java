package teammates.test.cases.common;

import static org.testng.AssertJUnit.assertEquals;

import java.util.ArrayList;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.Sanitizer;
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
    public void testIsAnyMatching(){
        //this method is used in header row processing in StudentAttributesFactory: locateColumnIndexes
        //so use this to test the various header field regex expressions here
        
        
        String[] regexArray = FieldValidator.REGEX_COLUMN_NAME;
        
        assertEquals(true, StringHelper.isAnyMatching("names", regexArray));
        assertEquals(true, StringHelper.isAnyMatching("name", regexArray));
        assertEquals(true, StringHelper.isAnyMatching("students names", regexArray));
        assertEquals(true, StringHelper.isAnyMatching("student     name", regexArray));
        assertEquals(true, StringHelper.isAnyMatching("full    names", regexArray));
        assertEquals(true, StringHelper.isAnyMatching("student   full  names", regexArray));
        
        assertEquals(false, StringHelper.isAnyMatching("namess", regexArray));
        assertEquals(false, StringHelper.isAnyMatching("nam", regexArray));
        assertEquals(false, StringHelper.isAnyMatching("studenttsnames", regexArray));
        assertEquals(false, StringHelper.isAnyMatching("studen     name", regexArray));
        assertEquals(false, StringHelper.isAnyMatching("fulll names", regexArray));
        assertEquals(false, StringHelper.isAnyMatching("studnt full  names", regexArray));
        
        regexArray = FieldValidator.REGEX_COLUMN_SECTION;
        assertEquals(true, StringHelper.isAnyMatching("   sect   ", regexArray));
        assertEquals(true, StringHelper.isAnyMatching("sect ", regexArray));
        assertEquals(true, StringHelper.isAnyMatching("sections ", regexArray));
        assertEquals(true, StringHelper.isAnyMatching("section ", regexArray));
        assertEquals(true, StringHelper.isAnyMatching("course   sections", regexArray));
        assertEquals(true, StringHelper.isAnyMatching("courses   sec", regexArray));
        
        assertEquals(false, StringHelper.isAnyMatching("sectt", regexArray));
        assertEquals(false, StringHelper.isAnyMatching("sectionss", regexArray));
        assertEquals(false, StringHelper.isAnyMatching("sct", regexArray));
        assertEquals(false, StringHelper.isAnyMatching("coursesecs", regexArray));
        assertEquals(false, StringHelper.isAnyMatching("course sectionsss", regexArray));
        assertEquals(false, StringHelper.isAnyMatching("ect", regexArray));
        
        regexArray = FieldValidator.REGEX_COLUMN_TEAM;
        assertEquals(true, StringHelper.isAnyMatching("team ", regexArray));
        assertEquals(true, StringHelper.isAnyMatching("teams", regexArray));
        assertEquals(true, StringHelper.isAnyMatching(" groups ", regexArray));
        assertEquals(true, StringHelper.isAnyMatching(" students   teams", regexArray));
        assertEquals(true, StringHelper.isAnyMatching("  student    groups ", regexArray));
        assertEquals(true, StringHelper.isAnyMatching(" courses   teams  ", regexArray));
        
        assertEquals(false, StringHelper.isAnyMatching("tea", regexArray));
        assertEquals(false, StringHelper.isAnyMatching("grop", regexArray));
        assertEquals(false, StringHelper.isAnyMatching("studen teams", regexArray));
        assertEquals(false, StringHelper.isAnyMatching("studentt groups", regexArray));
        assertEquals(false, StringHelper.isAnyMatching("coursess teamss ", regexArray));
        assertEquals(false, StringHelper.isAnyMatching("courseteams", regexArray));
        
        regexArray = FieldValidator.REGEX_COLUMN_EMAIL;
        assertEquals(true, StringHelper.isAnyMatching("emails", regexArray));
        assertEquals(true, StringHelper.isAnyMatching("email ", regexArray));
        assertEquals(true, StringHelper.isAnyMatching(" e-mails ", regexArray));
        assertEquals(true, StringHelper.isAnyMatching(" e-mail  ", regexArray));
        assertEquals(true, StringHelper.isAnyMatching("  e  mails ", regexArray));
        assertEquals(true, StringHelper.isAnyMatching(" emails   addresses   ", regexArray));
        assertEquals(true, StringHelper.isAnyMatching(" emails   address   ", regexArray));
        assertEquals(true, StringHelper.isAnyMatching(" contact   ", regexArray));
        assertEquals(true, StringHelper.isAnyMatching(" contacts   ", regexArray));
        
        assertEquals(false, StringHelper.isAnyMatching("emaill", regexArray));
        assertEquals(false, StringHelper.isAnyMatching("mali", regexArray));
        assertEquals(false, StringHelper.isAnyMatching("eemail", regexArray));
        assertEquals(false, StringHelper.isAnyMatching("emai addresses", regexArray));
        assertEquals(false, StringHelper.isAnyMatching("e-mai address", regexArray));
        assertEquals(false, StringHelper.isAnyMatching("contats", regexArray));
        
        regexArray = FieldValidator.REGEX_COLUMN_COMMENT;
        assertEquals(true, StringHelper.isAnyMatching("   comments  ", regexArray));
        assertEquals(true, StringHelper.isAnyMatching("  comment ", regexArray));
        assertEquals(true, StringHelper.isAnyMatching(" notes  ", regexArray));
        assertEquals(true, StringHelper.isAnyMatching(" note", regexArray));
        assertEquals(true, StringHelper.isAnyMatching("note", regexArray));
        
        assertEquals(false, StringHelper.isAnyMatching("", regexArray));
        assertEquals(false, StringHelper.isAnyMatching("commment", regexArray));
        assertEquals(false, StringHelper.isAnyMatching("nottes", regexArray));
        assertEquals(false, StringHelper.isAnyMatching("notess", regexArray));
        assertEquals(false, StringHelper.isAnyMatching("commentss", regexArray));

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
    
    @Test
    public void testSplitName(){
        
            
        String fullName = "singleWord";
        String[] splitName = StringHelper.splitName(fullName);
        
        assertEquals(splitName[0],"");
        assertEquals(splitName[1],"singleWord");
       
        fullName = "";
        splitName = StringHelper.splitName(fullName);
        
        assertEquals(splitName[0],"");
        assertEquals(splitName[1],"");
        
        fullName = null;
        splitName = StringHelper.splitName(fullName);
        
        assertEquals(splitName,null);
        
        
        fullName = "two words";
        splitName = StringHelper.splitName(fullName);
        
        assertEquals(splitName[0],"two");
        assertEquals(splitName[1],"words");
        
        fullName = "now three words";
        splitName = StringHelper.splitName(fullName);
        
        assertEquals(splitName[0],"now three");
        assertEquals(splitName[1],"words");
        
        
        fullName = "what if four words";
        splitName = StringHelper.splitName(fullName);
        
        assertEquals(splitName[0],"what if four");
        assertEquals(splitName[1],"words");
        
        fullName = "first name firstName {last Name}";
        splitName = StringHelper.splitName(fullName);
        
        assertEquals(splitName[0],"first name firstName");
        assertEquals(splitName[1],"last Name");
        
    }
    
    @Test 
    public void testRemoveExtraSpace(){
        
       String str = "";
       assertEquals("",StringHelper.removeExtraSpace(str));
       
       str = null;
       assertEquals(null,StringHelper.removeExtraSpace(str));
       
       str = "a    a";
       assertEquals("a a",StringHelper.removeExtraSpace(str));
       
       str = "  a    a   ";
       assertEquals("a a",StringHelper.removeExtraSpace(str));
       
       str = "    ";
       assertEquals("",StringHelper.removeExtraSpace(str));
       
       str = " a      b       c       d      ";
       assertEquals("a b c d",StringHelper.removeExtraSpace(str));
    }
    
    @Test
    public void testRecoverFromSanitizedText(){        
        String str = null;
        assertEquals(null,StringHelper.recoverFromSanitizedText(str));
        
        str = "";
        assertEquals("",StringHelper.recoverFromSanitizedText(str));
        
        str = Sanitizer.sanitizeForHtml("<text><div> 'param' &&& \\//\\");
        assertEquals("<text><div> 'param' &&& \\//\\",StringHelper.recoverFromSanitizedText(str));
    }

}
