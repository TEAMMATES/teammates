
module('common.js');

test('isNumber(num)', function(){
    equal(isNumber("-0.001"),true,"Negative double");
    equal(isNumber("12.056"),true,"Positive double");
    equal(isNumber("100356"),true,"Positive integer");
    equal(isNumber("-237"),true,"Negative integer");
    equal(isNumber("ABCDE"),false,"Letters");
    equal(isNumber("$12.57"),false,"With Dollar Sign");
    equal(isNumber("12A5"),false,"Letter in Numbers");
    equal(isNumber("0"),true,"zero");
    equal(isNumber("   124    "),true,"With Spacing");
    equal(isNumber("   12   4    "),false,"With Spacing between");
});

/**
 * Test the whether the passed object is an actual date
 * with an accepted format
 * 
 * Allowed formats : http://dygraphs.com/date-formats.html
 * 
 * TEAMMATES currently follows the RFC2822 / IETF date syntax 
 * e.g. 02 Apr 2012, 23:59
 */
test('isDate(date)', function(){
    equal(isDate("12432567"),false,"Numbers");
    equal(isDate("0/0/0"),true,"0/0/0 - valid date on Firefox, invalid on Chrome");
    equal(isDate("12/2/13"),true,"12/2/13 - valid format");
    equal(isDate("12/02/2013"),true,"12/02/2013 - valid format (mm/dd/yyyy)");
    equal(isDate("28/12/2013"),true,"28/12/2013 - valid format (dd/mm/yyyy)");
    equal(isDate("12/12/13"),true,"12/02/13 - valid format");
    equal(isDate("2013-12-12"),true,"2013-12-12 - valid format");
    equal(isDate("28-12-2013"),false,"28-12-2013 - invalid format (dd-mm-yyyy)");
    equal(isDate("2013-12-28"),true,"2013-12-28 - valid format (yyyy-mm-dd)");
    equal(isDate("01 03 2003"),true,"01 03 2003 - valid format");
    equal(isDate("A1/B3/C003"),false,"A1/B3/C003 - invalid date");
    equal(isDate("Abcdef"),false,"Invalid Date string");
    equal(isDate("02 Apr 2012, 23:59"),true,"Valid Date string with time");
    equal(isDate("02 Apr 2012"),true,"Valid Date string without time");
    equal(isDate("    12/12/01"),true,"With Spacing in front");
    equal(isDate("12 12 01       "),true,"With Spacing behind");
    equal(isDate("            12-12-01       "),false,"With Spacing," +
            " invalid on Firefox and valid on Chrome");
    equal(isDate("a12-12-2001"),false,"a12-12-2001 - not in proper format");
    equal(isDate("    a      12 12 2001"),false,"    a      12 12 2001 - not in proper format");
    equal(isDate("12/12/2001   a  "),false,"12/12/2001   a  - not in proper format");
});


test('scrollToTop()', function(){
    // N/A, trivial function
    expect(0);
});


test('sortBase(x, y)', function(){
    equal(sortBase("abc","abc"),0,"Same text");
    equal(sortBase("ABC","abc"),-1,"Bigger text");
    equal(sortBase("abc","ABC"),1,"Smaller text");
    equal(sortBase("abc","efg"),-1,"Different text");
    equal(sortBase("ABC","efg"),-1,"Bigger text");
    equal(sortBase("abc","EFG"),1,"Smaller text");
});

test('sortNum(x, y)', function(){
    equal(sortNum("1","2"),-1,"x=1, y=2");
    equal(sortNum("-10","2"),-12,"x=-10, y=2");
    equal(sortNum("3","-1"),4,"x=3, y=-1");
    equal(sortNum("0.1","0.1"),0,"x=0.1, y=0.1");
    equal(sortNum("-0.1","0.1"),-0.2,"x=-0.1, y=0.1");
    equal(sortNum("0.1","-0.1"),0.2,"x=-0.1, y=-0.1");
});

test('sortDate(x, y)', function(){
    equal(sortDate("25 April 1999","23 April 1999"),1,"25 April 1999 - 23 April 1999");
    equal(sortDate("25 April 1999 2:00","25 April 1999 1:59"),1,"25 April 1999 2:00PM - 25 April 1999 1:59PM");
    equal(sortDate("25 April 1999 2:00","25 April 1999 2:00"),0,"25 April 1999 2:00PM - 25 April 1999 2:00PM");
    equal(sortDate("25 April 1999 2:00","25 April 1999 2:01"),-1,"25 April 1999 2:00PM - 25 April 1999 2:01PM");
});

test('setStatusMessage(message,error)', function(){	
    $("body").append('<div id="statusMessage"></div>');
    var message = "Status Message";
    
    //isError = false: class = alert alert-warning
    //isError = true: class = alert alert-danger
    
    setStatusMessage(message, false);
    equal($("#statusMessage").html(), message, "Normal status message");
    ok(($("#statusMessage").attr("class") == "alert alert-warning"), "No warning");
    setStatusMessage("", false);
    equal($("#statusMessage").html(), "", "Empty status message");
    ok(($("#statusMessage").attr("class") == "alert alert-warning"), "No warning");
    setStatusMessage(message, true);
    equal($("#statusMessage").html(), message, "Normal status message");
    ok(($("#statusMessage").attr("class") == "alert alert-danger"), "No danger");
    setStatusMessage("", true);
    equal($("#statusMessage").html(), "", "Normal status message");
    ok(($("#statusMessage").attr("class") == "alert alert-danger"), "No danger");
});


test('clearStatusMessage()', function(){
    clearStatusMessage();
    equal($("#statusMessage").html(), "", "Status message cleared");
    ok(($("#statusMessage").css('background-color') == "rgba(0, 0, 0, 0)" || $("#statusMessage").css('background-color') == "transparent"), "No background");
});


test('checkEvaluationForm()', function(){
    // N/A, requires elements in the page
    expect(0);
});

test('sanitizeGoogleId(googleId)', function() {
    equal(sanitizeGoogleId("test  @Gmail.COM  "), "test", "test - valid");
    equal(sanitizeGoogleId("  user@hotmail.com  "), "user@hotmail.com", "user@hotmail.com - valid");
});

test('isValidGoogleId(googleId)', function() {
    equal(isValidGoogleId("  test  \t\n"), true, "test - valid");
    equal(isValidGoogleId("  charile.brown  \t\n"), true, "charile.brown - valid");
    equal(isValidGoogleId("  big-small_mini  \t\n"), true, "big-small_mini - valid");
    
    equal(isValidGoogleId(" hello@GMail.COm \t\n "), false, "hello@gmail.com - invalid");
    equal(isValidGoogleId("wrong!"), false, "wrong! - invalid");
    equal(isValidGoogleId("not*correct"), false, "not*correct - invalid");
    equal(isValidGoogleId("is/not\correct"), false, "is/not\correct - invalid");
});

test('isEmailValid(email)', function(){
    equal(isEmailValid("test@gmail.com"), true, "test@gmail.com - valid");
    equal(isEmailValid("email"), false, "email - invalid");
    equal(isEmailValid("email@email"), false, "email@email - invalid");
    equal(isEmailValid("@yahoo.com"), false, "@yahoo.com - invalid");
    equal(isEmailValid("email.com"), false, "email.com - invalid");
});
    

test('isNameValid(name)', function(){
    equal(isNameValid('	Tom Jacobs,.	\'()-\/ \\  '), true, "alphanumerics, fullstop, comma, round brackets, slashes, apostrophe, hyphen - valid");
    equal(isNameValid(generateRandomString(NAME_MAX_LENGTH)), true, "Maximum characters - valid");
    
    equal(isNameValid(""), false, "Empty name - invalid");
    equal(isNameValid(generateRandomString(NAME_MAX_LENGTH + 1)), false, "Exceed number of maximum characters - invalid");
    equal(isNameValid("Tom! Jacobs"), false, "! character - invalid");
    equal(isNameValid("Tom ^Jacobs"), false, "^ character - invalid");
    equal(isNameValid("Tom#"), false, "# character - invalid");
    equal(isNameValid("&Tom"), false, "& character - invalid");
    equal(isNameValid("J%cobs "), false, "% character - invalid");
    equal(isNameValid("Jacobs*"), false, "* character - invalid");
    equal(isNameValid("	+123	 "), false, "+ character - invalid");
    equal(isNameValid("a b c $ 1 2 3 4"), false, "$ character - invalid");
    equal(isNameValid("1@2@3  456"), false, "@ character - invalid");
    equal(isNameValid("Tom = Tom"), false, "= character - invalid");
    equal(isNameValid("Tom||Jacobs"), false, "| character - invalid");
    
});

test('roundToThreeDp(num)', function() {

    equal(roundToThreeDp(0), 0, "Zero test");    
    equal(roundToThreeDp(1), 1, "Positive integer test");    
    equal(roundToThreeDp(-1), -1, "Negative integer test");    
    equal(roundToThreeDp(1.001), 1.001, "Three dp positive number test");
    equal(roundToThreeDp(-1.001), -1.001, "Three dp negative number test");
    equal(roundToThreeDp(1.0015), 1.002, "Four dp positive number rounding up test");    
    equal(roundToThreeDp(1.0011), 1.001, "Four dp negative number rounding down test");  
    equal(roundToThreeDp(-1.0015), -1.002, "Four dp positive number rounding 'up' test");    
    equal(roundToThreeDp(-1.0011), -1.001, "Four dp negative number rounding 'down' test");    
    
});

test('sanitizeForJs(string)', function(){
    equal(sanitizeForJs(""), "", "sanitization for empty string");
    equal(sanitizeForJs("Will o' Wisp"), "Will o\\' Wisp", "sanitization for single quote");
    equal(sanitizeForJs("Will o'''''\\\\ Wisp"), "Will o\\'\\'\\'\\'\\'\\\\\\\\ Wisp", "sanitization for single quote and slash \\");
    
});
