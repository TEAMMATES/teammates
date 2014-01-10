module('instructorStudentList.js');

function testFilterCourse(){};
test('filterCourse()', function(){
	//Initialize by checking the options box and show email
	if(!$("#option_check").is(":checked")){
		$("#option_check").click();
	}
	if(!$("#show_email").is(":checked")){
		$("#show_email").click();
	}
	
	//Manually reset view first
	$(".student_row").show();
	$(".student_email").show();
	$(".backgroundBlock").show();
	
	//Deselect 1 course, and select back using Select All
	$("#course_check-0").click();
	equal($("#course-0").is(':hidden'), true, "Course not selected is hidden");
	equal($("#course_all").is(":checked"), false, "Select all check should be removed");
	equal($("#team_check-0-0").is(':hidden'), true, "Team in non-selected course should be hidden");
	equal($("#team_check-0-1").is(':hidden'), true, "Team in non-selected course should be hidden");
	equal($("#team_check-0-0").is(':checked'), false, "Hidden team_check should not have checked prop");
	equal($("#team_check-0-1").is(':checked'), false, "Hidden team_check should not have checked prop");
	
	equal($("[id^=student_email-c0]:hidden").length, 2, "Student in course 0 email should be hidden");
	equal($("[id^=student_email-c1]:visible").length, 10, "Student in course 1 email should be visible");

	
	$("#course_all").click();
	equal($("#course-0").is(':visible'), true, "All course should be visible");
	equal($("#course-1").is(':visible'), true, "All course should be visible");
	equal($("#course_check-0").is(":checked"), true, "Course 0 checkbox should be re-selected");
	equal($("#team_check-0-0").is(':visible'), true, "Team in selected course should be visible");
	equal($("#team_check-0-1").is(':visible'), true, "Team in selected course should be visible");
	equal($("#team_check-0-0").is(':checked'), true, "All team_check should be checked after course check");
	equal($("#team_check-0-1").is(':checked'), true, "All team_check should be checked after course check");
	
	equal($("[id^=student_email-c0]:visible").length, 2, "Student in course 0 email should be visible again");
	equal($("[id^=student_email-c1]:visible").length, 9, "Student in course 1 email (minus duplicate) should be visible");
	
	
	//Deselect 1 course, and select back using the specific course check
	$("#course_check-1").click();
	equal($("#course-1").is(':hidden'), true, "Course not selected is hidden");
	equal($("#course_all").is(":checked"), false, "Select all check should be removed");
	equal($("#team_check-1-0").is(':hidden'), true, "Team in non-selected course should be hidden");
	equal($("#team_check-1-1").is(':hidden'), true, "Team in non-selected course should be hidden");
	equal($("#team_check-1-2").is(':hidden'), true, "Team in non-selected course should be hidden");
	equal($("#team_check-1-0").is(':checked'), false, "Hidden team_check should not have checked prop");
	equal($("#team_check-1-1").is(':checked'), false, "Hidden team_check should not have checked prop");
	equal($("#team_check-1-3").is(':checked'), false, "Hidden team_check should not have checked prop");
	
	equal($("[id^=student_email-c0]:visible").length, 2, "Student in course 0 email should be visible");
	equal($("[id^=student_email-c1]:hidden").length, 10, "Student in course 1 email should be hidden");
	
	$("#course_check-1").click();
	equal($("#course-1").is(':visible'), true, "Course selected is visible");
	equal($("#course_all").is(":checked"), true, "Select all check should be re-selected");
	equal($("#team_check-1-0").is(':visible'), true, "Team in selected course should be visible");
	equal($("#team_check-1-1").is(':visible'), true, "Team in selected course should be visible");
	equal($("#team_check-1-2").is(':visible'), true, "Team in selected course should be visible");
	equal($("#team_check-1-0").is(':checked'), true, "All team_check should be checked after course check");
	equal($("#team_check-1-1").is(':checked'), true, "All team_check should be checked after course check");
	equal($("#team_check-1-2").is(':checked'), true, "All team_check should be checked after course check");
	
	equal($("[id^=student_email-c0]:visible").length, 2, "Student in course 0 email should be visible");
	equal($("[id^=student_email-c1]:visible").length, 9, "Student in course 1 email (minus duplicate) should be visible again");
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
	$(".student_row").show();
	$(".student_email").show();
	$(".backgroundBlock").show();
	
	//Deselect 2 team, and select back using Select All
	$("#team_check-0-0").click();
	$("#team_check-1-1").click();
	equal($("#studentteam-c0\\.0").is(':hidden'), true, "Team not selected is hidden");
	equal($("#studentteam-c1\\.1").is(':hidden'), true, "Team not selected is hidden");
	equal($("#team_all").is(":checked"), false, "Select all check should be removed");
	
	equal($("#student_email-c0\\.0").is(':hidden'), true, "[Team 1]Alice Betsy's email hidden");
	equal($("#student_email-c0\\.1").is(':visible'), true, "[Team 2]Benny Charles's email visible");
	equal($("#student_email-c1\\.0").is(':visible'), true, "[Team 1]Alice Betsy's email visible");
	equal($("#student_email-c1\\.1").is(':hidden'), true, "[Team 1]Duplicate Benny Charles's email should be hidden");
	equal($("#student_email-c1\\.2").is(':visible'), true, "[Team 1]Charlie D's email visible");
	equal($("#student_email-c1\\.3").is(':visible'), true, "[Team 1]Denny Charlés's email visible");
	equal($("#student_email-c1\\.4").is(':hidden'), true, "[Team 2]Emma F's email hidden");
	equal($("#student_email-c1\\.5").is(':hidden'), true, "[Team 2]Frank Gatsby's email hidden");
	equal($("#student_email-c1\\.6").is(':hidden'), true, "[Team 2]Gabriel Hobb's email hidden");
	equal($("#student_email-c1\\.7").is(':visible'), true, "[Team 3]Hans Iker's email visible");
	equal($("#student_email-c1\\.8").is(':visible'), true, "[Team 3]Ian Jacobsson's email visible");
	equal($("#student_email-c1\\.9").is(':visible'), true, "[Team 3]James K's email visible");
	
	$("#team_all").click();
	equal($("#studentteam-c0\\.0").is(':visible'), true, "All teams should be visible");
	equal($("#studentteam-c0\\.1").is(':visible'), true, "All teams should be visible");
	equal($("#studentteam-c1\\.0").is(':visible'), true, "All teams should be visible");
	equal($("#studentteam-c1\\.1").is(':visible'), true, "All teams should be visible");
	equal($("#studentteam-c1\\.2").is(':visible'), true, "All teams should be visible");
	equal($("#team_check-0-0").is(":checked"), true, "Course 0 Team 0 checkbox should be re-selected");
	equal($("#team_check-1-1").is(":checked"), true, "Course 1 Team 1 checkbox should be re-selected");
	
	equal($("[id^=student_email]:visible").length, 11, "All emails (minus duplicate) should be visible again");
	
	//Deselect 1 team, and select back using the specific course check
	$("#team_check-1-2").click();
	equal($("#studentteam-c1\\.2").is(':hidden'), true, "Team not selected is hidden");
	equal($("#team_all").is(":checked"), false, "Select all check should be removed");
	
	equal($("#student_email-c0\\.0").is(':visible'), true, "[Team 1]Alice Betsy's email visible");
	equal($("#student_email-c0\\.1").is(':visible'), true, "[Team 2]Benny Charles's email visible");
	equal($("#student_email-c1\\.0").is(':visible'), true, "[Team 1]Alice Betsy's email visible");
	equal($("#student_email-c1\\.1").is(':hidden'), true, "[Team 1]Duplicate Benny Charles's email should be hidden");
	equal($("#student_email-c1\\.2").is(':visible'), true, "[Team 1]Charlie D's email visible");
	equal($("#student_email-c1\\.3").is(':visible'), true, "[Team 1]Denny Charlés's email visible");
	equal($("#student_email-c1\\.4").is(':visible'), true, "[Team 2]Emma F's email visible");
	equal($("#student_email-c1\\.5").is(':visible'), true, "[Team 2]Frank Gatsby's email visible");
	equal($("#student_email-c1\\.6").is(':visible'), true, "[Team 2]Gabriel Hobb's email visible");
	equal($("#student_email-c1\\.7").is(':hidden'), true, "[Team 3]Hans Iker's email hidden");
	equal($("#student_email-c1\\.8").is(':hidden'), true, "[Team 3]Ian Jacobsson's email hidden");
	equal($("#student_email-c1\\.9").is(':hidden'), true, "[Team 3]James K's email hidden");
	
	$("#team_check-1-2").click();
	equal($("#studentteam-c1\\.2").is(':visible'), true, "Team selected is visible");
	equal($("#team_check-1-2").is(':checked'), true, "Course 1 Team 1 checkbox should be re-selected");
	
	equal($("[id^=student_email]:visible").length, 11, "All emails (minus 1 duplicate) should be visible again");
});

function testFilterName(){};
test('filterName(key)', function(){
	
	//Initialize by checking the options box and show email
	if(!$("#option_check").is(":checked")){
		$("#option_check").click();
	}
	if(!$("#show_email").is(":checked")){
		$("#show_email").click();
	}
	
	//Manually reset view first
	$(".student_row").show();
	$(".student_email").show();
	$(".backgroundBlock").show();
	
	//Search 1 student name that exists in 2 course
	filterName("ben");
	filterEmails();
	equal($("#student-c0\\.0").is(':hidden'), true, "Alice Betsy hidden");
	equal($("#student-c0\\.1").is(':visible'), true, "Benny Charles visible");
	equal($("#student-c1\\.0").is(':hidden'), true, "Alice Betsy hidden");
	equal($("#student-c1\\.1").is(':visible'), true, "Benny Charles visible");
	equal($("#student-c1\\.2").is(':hidden'), true, "Charlie D hidden");
	equal($("#student-c1\\.3").is(':hidden'), true, "Denny Charlés hidden");
	equal($("#student-c1\\.4").is(':hidden'), true, "Emma F hidden");
	equal($("#student-c1\\.5").is(':hidden'), true, "Frank Gatsby hidden");
	equal($("#student-c1\\.6").is(':hidden'), true, "Gabriel Hobb hidden");
	equal($("#student-c1\\.7").is(':hidden'), true, "Hans Iker hidden");
	equal($("#student-c1\\.8").is(':hidden'), true, "Ian Jacobsson hidden");
	equal($("#student-c1\\.9").is(':hidden'), true, "James K hidden");
	
	equal($("#student_email-c0\\.0").is(':hidden'), true, "Alice Betsy's email hidden");
	equal($("#student_email-c0\\.1").is(':visible'), true, "Benny Charles's email visible");
	equal($("#student_email-c1\\.0").is(':hidden'), true, "Alice Betsy's email hidden");
	equal($("#student_email-c1\\.1").is(':hidden'), true, "The second Benny Charles's email should be hidden");
	equal($("#student_email-c1\\.2").is(':hidden'), true, "Charlie D's email hidden");
	equal($("#student_email-c1\\.3").is(':hidden'), true, "Denny Charlés's email hidden");
	equal($("#student_email-c1\\.4").is(':hidden'), true, "Emma F's email hidden");
	equal($("#student_email-c1\\.5").is(':hidden'), true, "Frank Gatsby's email hidden");
	equal($("#student_email-c1\\.6").is(':hidden'), true, "Gabriel Hobb's email hidden");
	equal($("#student_email-c1\\.7").is(':hidden'), true, "Hans Iker's email hidden");
	equal($("#student_email-c1\\.8").is(':hidden'), true, "Ian Jacobsson's email hidden");
	equal($("#student_email-c1\\.9").is(':hidden'), true, "James K's email hidden");
	
	//Search 1 student name that only exist in 1 of the course
	
	//Manually reset view first
	$(".student_row").show();
	$(".student_email").show();
	filterName("James");
	filterEmails();
	equal($("#course-0").is(':hidden'), true, "Course with no student visible is hidden");
	
	equal($("#student-c1\\.0").is(':hidden'), true, "Alice Betsy hidden");
	equal($("#student-c1\\.1").is(':hidden'), true, "Benny Charles hidden");
	equal($("#student-c1\\.2").is(':hidden'), true, "Charlie D hidden");
	equal($("#student-c1\\.3").is(':hidden'), true, "Denny Charlés hidden");
	equal($("#student-c1\\.4").is(':hidden'), true, "Emma F hidden");
	equal($("#student-c1\\.5").is(':hidden'), true, "Frank Gatsby hidden");
	equal($("#student-c1\\.6").is(':hidden'), true, "Gabriel Hobb hidden");
	equal($("#student-c1\\.7").is(':hidden'), true, "Hans Iker hidden");
	equal($("#student-c1\\.8").is(':hidden'), true, "Ian Jacobsson hidden");
	equal($("#student-c1\\.9").is(':visible'), true, "James K visible");
	
	equal($("#student_email-c1\\.0").is(':hidden'), true, "Alice Betsy's email hidden");
	equal($("#student_email-c1\\.1").is(':hidden'), true, "Benny Charles's email hidden");
	equal($("#student_email-c1\\.2").is(':hidden'), true, "Charlie D's email hidden");
	equal($("#student_email-c1\\.3").is(':hidden'), true, "Denny Charlés's email hidden");
	equal($("#student_email-c1\\.4").is(':hidden'), true, "Emma F's email hidden");
	equal($("#student_email-c1\\.5").is(':hidden'), true, "Frank Gatsby's email hidden");
	equal($("#student_email-c1\\.6").is(':hidden'), true, "Gabriel Hobb's email hidden");
	equal($("#student_email-c1\\.7").is(':hidden'), true, "Hans Iker's email hidden");
	equal($("#student_email-c1\\.8").is(':hidden'), true, "Ian Jacobsson's email hidden");
	equal($("#student_email-c1\\.9").is(':visible'), true, "James K's email visible");
});

function testAllFilter(){};
test('applyFilters()', function(){
	//Initialize by checking the options box and show email
	if(!$("#option_check").is(":checked")){
		$("#option_check").click();
	}
	if(!$("#show_email").is(":checked")){
		$("#show_email").click();
	}
	
	//Manually reset view first
	$(".student_row").show();
	$(".student_email").show();
	$(".backgroundBlock").show();
	
	//Search for Ben, except in [Team 1] [course3]
	$("#searchbox").val("ben"); //Set searchbox value to ben
	$("#team_check-1-0").click(); //Deselect [Team 1] in [course3]
	//applyFilters() will be done automatically
	
	equal($("#student-c0\\.1").is(':visible'), true, "Benny Charles visible");
	equal($("[id^=student-c]:visible").length, 1, "Only 1 Benny Charles should be visible");
	
	equal($("#student_email-c0\\.1").is(':visible'), true, "Benny Charles' email visible");
	equal($("[id^=student_email-c]:visible").length, 1, "Only 1 Benny Charles' email should be visible");
	
	//Further filter the search above, remove [course2]
	$("#course_check-0").click(); //Deselect [course2]
	
	equal($("[id^=student-c]:visible").length, 0, "No student should be visible");
	equal($("[id^=student_email-c]:visible").length, 0, "No email should be visible");
	equal($(".backgroundBlock:visible").length, 0, "No course should be visible");
	
	//Reset the filter by selecting all course
	$("#course_all").click();
	equal($("#student-c0\\.1").is(':visible'), true, "Benny Charles visible");
	equal($("#student-c1\\.1").is(':visible'), true, "Another Benny Charles visible");
	equal($("[id^=student-c]:visible").length, 2, "2 Benny Charles should be visible");
	equal($("#student_email-c0\\.1").is(':visible'), true, "Benny Charles' email visible");
	equal($("#student_email-c1\\.1").is(':hidden'), true, "Another Benny Charles' email should be hidden");
	equal($("[id^=student_email-c]:visible").length, 1, "Only 1 Benny Charles' emails should be visible");
	equal($(".backgroundBlock:visible").length, 2, "All courses should be visible");
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
