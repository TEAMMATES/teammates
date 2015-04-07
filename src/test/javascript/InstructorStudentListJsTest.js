module('instructorStudentList.js');

function testFilterSection(){};
test('filterSection()', function(){
    
    if(!$("#show_email").is(":checked")){
        $("#show_email").click();
    }

    //Manually reset view first
    $("tr[id^='student-c']").show();
    $("#emails").show();
    $(".div[id^='course-']").show();

    // Deselect 1 section and select back usin Select All
    $("#section_check-1-0").click();
    equal($("#studentsection-c1\\.0").is(':hidden'), true, "Section not selected is hidden");
    equal($("#section_all").is(":checked"), false, "Select all check should be removed");

    equal($("#student_email-c0\\.0").is(':visible'), true, "[Team 1]Alice Betsy's email visible");
    equal($("#student_email-c0\\.1").is(':visible'), true, "[Team 2]Hugh Ivanov's email visible");
    equal($("#student_email-c0\\.2").is(':visible'), true, "[Team 3]Benny Charles's email visible");
    equal($("#student_email-c1\\.0").is(':hidden'), true, "[Team 1]Duplicate Benny Charles's email should be hidden");
    equal($("#student_email-c1\\.1").is(':hidden'), true, "[Team 1]Carlos Santanna's email hidden");
    equal($("#student_email-c1\\.2").is(':hidden'), true, "[Team 1]Charlie D's email hidden");
    equal($("#student_email-c1\\.3").is(':hidden'), true, "[Team 2]Denny Charlés's email hidden");
    equal($("#student_email-c1\\.4").is(':hidden'), true, "[Team 2]Emma F's email hidden");
    equal($("#student_email-c1\\.5").is(':hidden'), true, "[Team 2]Frank Gatsby's email hidden");
    equal($("#student_email-c1\\.6").is(':hidden'), true, "[Team 3]Gabriel Hobb's email hidden");
    equal($("#student_email-c1\\.7").is(':hidden'), true, "[Team 3]Hans Iker's email hidden");
    equal($("#student_email-c1\\.8").is(':hidden'), true, "[Team 3]Ian Jacobsson's email hidden");
    equal($("#student_email-c1\\.9").is(':hidden'), true, "[Team 3]James K's email hidden");

    $("#section_all").click();
    equal($("#studentsection-c0\\.0").is(':visible'), true, "All sections should be visible");
    equal($("#studentsection-c0\\.1").is(':visible'), true, "All sections should be visible");
    equal($("#studentsection-c1\\.0").is(':visible'), true, "All sections should be visible");
    equal($("#section_check-0-0").is(':checked'), true, "Course 2 Section A should be re-selected");
    equal($("#section_check-0-1").is(':checked'), true, "Course 2 Section B should be re-selected");
    equal($("#section_check-1-0").is(':checked'), true, "Course 3 Section C should be re-selected");

    equal($("[id^=student_email]:visible").length, 12, "All emails (minus duplicate) should be visible again");

     // Deselect 1 section and select back usin Select All
    $("#section_check-0-1").click();
    equal($("#studentsection-c0\\.1").is(':hidden'), true, "Section not selected is hidden");
    equal($("#section_all").is(":checked"), false, "Select all check should be removed");

    equal($("#student_email-c0\\.0").is(':visible'), true, "[Team 1]Alice Betsy's email visible");
    equal($("#student_email-c0\\.1").is(':hidden'), true, "[Team 2]Hugh Ivanov's email hidden");
    equal($("#student_email-c0\\.2").is(':hidden'), true, "[Team 3]Benny Charles's email hidden");
    equal($("#student_email-c1\\.0").is(':visible'), true, "[Team 1]Duplicate Benny Charles's email should be visible");
    equal($("#student_email-c1\\.1").is(':visible'), true, "[Team 1]Carlos Santanna's email visible");
    equal($("#student_email-c1\\.2").is(':visible'), true, "[Team 1]Charlie D's email visible");
    equal($("#student_email-c1\\.3").is(':visible'), true, "[Team 2]Denny Charlés's email visible");
    equal($("#student_email-c1\\.4").is(':visible'), true, "[Team 2]Emma F's email visible");
    equal($("#student_email-c1\\.5").is(':visible'), true, "[Team 2]Frank Gatsby's email visible");
    equal($("#student_email-c1\\.6").is(':visible'), true, "[Team 3]Gabriel Hobb's email visible");
    equal($("#student_email-c1\\.7").is(':visible'), true, "[Team 3]Hans Iker's email visible");
    equal($("#student_email-c1\\.8").is(':visible'), true, "[Team 3]Ian Jacobsson's email visible");
    equal($("#student_email-c1\\.9").is(':visible'), true, "[Team 3]James K's email visible");

    $("#section_check-0-1").click();
    equal($("#studentsection-c0\\.0").is(':visible'), true, "All sections should be visible");
    equal($("#studentsection-c0\\.1").is(':visible'), true, "All sections should be visible");
    equal($("#studentsection-c1\\.0").is(':visible'), true, "All sections should be visible");
    equal($("#section_check-0-0").is(':checked'), true, "Course 2 Section A should be re-selected");
    equal($("#section_check-0-1").is(':checked'), true, "Course 2 Section B should be re-selected");
    equal($("#section_check-1-0").is(':checked'), true, "Course 3 Section C should be re-selected");

    equal($("[id^=student_email]:visible").length, 12, "All emails (minus duplicate) should be visible again");
});

function testFilterTeam(){};
test('filterTeam()', function(){
    //Initialize by checking the options box and show email
    if(!$("#option_check").is(":checked")){
        $("#option_check").click();
    }
    if(!$("#show_email").is(":checked")){
        $("#show_email").click();
    }
    
    //Manually reset view first
    $("tr[id^='student-c']").show();
    $("#emails").show();
    $(".div[id^='course-']").show();
    
    //Deselect 2 team, and select back using Select All
    $("#team_check-0-0-0").click();
    $("#team_check-1-0-0").click();
    equal($("#studentteam-c0\\.0\\.0").is(':hidden'), true, "Team not selected is hidden");
    equal($("#studentteam-c1\\.0\\.0").is(':hidden'), true, "Team not selected is hidden");
    equal($("#team_all").is(":checked"), false, "Select all check should be removed");
    
    equal($("#student_email-c0\\.0").is(':hidden'), true, "[Team 1]Alice Betsy's email hidden");
    equal($("#student_email-c0\\.1").is(':visible'), true, "[Team 2]Hugh Ivanov's email visible");
    equal($("#student_email-c0\\.2").is(':visible'), true, "[Team 3]Benny Charles's email visible");
    equal($("#student_email-c1\\.0").is(':hidden'), true, "[Team 1]Duplicate Benny Charles's email should be hidden");
    equal($("#student_email-c1\\.1").is(':hidden'), true, "[Team 1]Carlos Santanna's email visible");
    equal($("#student_email-c1\\.2").is(':hidden'), true, "[Team 1]Charlie D's email visible");
    equal($("#student_email-c1\\.3").is(':visible'), true, "[Team 2]Denny Charlés's email visible");
    equal($("#student_email-c1\\.4").is(':visible'), true, "[Team 2]Emma F's email hidden");
    equal($("#student_email-c1\\.5").is(':visible'), true, "[Team 2]Frank Gatsby's email hidden");
    equal($("#student_email-c1\\.6").is(':visible'), true, "[Team 3]Gabriel Hobb's email hidden");
    equal($("#student_email-c1\\.7").is(':visible'), true, "[Team 3]Hans Iker's email visible");
    equal($("#student_email-c1\\.8").is(':visible'), true, "[Team 3]Ian Jacobsson's email visible");
    equal($("#student_email-c1\\.9").is(':visible'), true, "[Team 3]James K's email visible");
    
    $("#team_all").click();
    equal($("#studentteam-c0\\.0\\.0").is(':visible'), true, "All teams should be visible");
    equal($("#studentteam-c0\\.1\\.0").is(':visible'), true, "All teams should be visible");
    equal($("#studentteam-c0\\.1\\.1").is(':visible'), true, "All teams should be visible");
    equal($("#studentteam-c1\\.0\\.0").is(':visible'), true, "All teams should be visible");
    equal($("#studentteam-c1\\.0\\.1").is(':visible'), true, "All teams should be visible");
    equal($("#studentteam-c1\\.0\\.2").is(':visible'), true, "All teams should be visible");
    equal($("#team_check-0-0-0").is(":checked"), true, "Course 2 Section A Team 1 checkbox should be re-selected");
    equal($("#team_check-0-1-0").is(":checked"), true, "Course 2 Section B Team 2 checkbox should be re-selected");
    equal($("#team_check-0-1-1").is(":checked"), true, "Course 2 Section B Team 3 checkbox should be re-selected");
    equal($("#team_check-1-0-0").is(":checked"), true, "Course 3 Section C Team 1 checkbox should be re-selected");
    equal($("#team_check-1-0-1").is(":checked"), true, "Course 3 Section C Team 2 checkbox should be re-selected");
    equal($("#team_check-1-0-2").is(":checked"), true, "Course 3 Section C Team 3 checkbox should be re-selected");

    equal($("[id^=student_email]:visible").length, 12, "All emails (minus duplicate) should be visible again");
    
    //Deselect 1 team, and select back using the specific course check
    $("#team_check-1-0-2").click();
    equal($("#studentteam-c1\\.0\\.2").is(':hidden'), true, "Team not selected is hidden");
    equal($("#team_all").is(":checked"), false, "Select all check should be removed");
    
    equal($("#student_email-c0\\.0").is(':visible'), true, "[Team 1]Alice Betsy's email visible");
    equal($("#student_email-c0\\.1").is(':visible'), true, "[Team 2]Hugh Ivanov's email visible");
    equal($("#student_email-c0\\.2").is(':visible'), true, "[Team 3]Benny Charles's email visible");
    equal($("#student_email-c1\\.0").is(':hidden'), true, "[Team 1]Duplicate Benny Charles's email should be hidden");
    equal($("#student_email-c1\\.1").is(':visible'), true, "[Team 1]Carlos Santanna's email visible");
    equal($("#student_email-c1\\.2").is(':visible'), true, "[Team 1]Charlie D's email visible");
    equal($("#student_email-c1\\.3").is(':visible'), true, "[Team 2]Denny Charlés's email visible");
    equal($("#student_email-c1\\.4").is(':visible'), true, "[Team 2]Emma F's email visible");
    equal($("#student_email-c1\\.5").is(':visible'), true, "[Team 2]Frank Gatsby's email visible");
    equal($("#student_email-c1\\.6").is(':hidden'), true, "[Team 3]Gabriel Hobb's email hidden");
    equal($("#student_email-c1\\.7").is(':hidden'), true, "[Team 3]Hans Iker's email hidden");
    equal($("#student_email-c1\\.8").is(':hidden'), true, "[Team 3]Ian Jacobsson's email hidden");
    equal($("#student_email-c1\\.9").is(':hidden'), true, "[Team 3]James K's email hidden");
    
    $("#team_check-1-0-2").click();
    equal($("#studentteam-c1\\.0\\.2").is(':visible'), true, "Team selected is visible");
    equal($("#team_check-1-0-2").is(':checked'), true, "Course 1 Team 1 checkbox should be re-selected");
    
    equal($("[id^=student_email]:visible").length, 12, "All emails (minus 1 duplicate) should be visible again");
});

function testFilterEmails(){};
test('filterEmails()', function(){
    //The method has been tested by UI test, and testFilter(Course|Team|Name) above.
    expect(0);
});

function testToggleDeleteStudentConfirmation(){};
test('toggleDeleteStudentConfirmation(courseId, studentName)', function(){
    //gives a popup, can't be tested
    expect(0);
});