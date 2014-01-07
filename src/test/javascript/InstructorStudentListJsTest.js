module('instructorStudentList.js');

function testSearch(){};
test('search(key)', function(){
	//Search 1 student name that exists in 2 course
	searchName("ben");
	equal($("#student-0").is(':hidden'), true, "Alice Betsy hidden");
	equal($("#student-1").is(':visible'), true, "Benny Charles visible");
	equal($("#student-2").is(':hidden'), true, "Alice Betsy hidden");
	equal($("#student-3").is(':visible'), true, "Benny Charles visible");
	equal($("#student-4").is(':hidden'), true, "Charlie D hidden");
	equal($("#student-5").is(':hidden'), true, "Denny Charlés hidden");
	equal($("#student-6").is(':hidden'), true, "Emma F hidden");
	equal($("#student-7").is(':hidden'), true, "Frank Gatsby hidden");
	equal($("#student-8").is(':hidden'), true, "Gabriel Hobb hidden");
	equal($("#student-9").is(':hidden'), true, "Hans Iker hidden");
	equal($("#student-10").is(':hidden'), true, "Ian Jacobsson hidden");
	equal($("#student-11").is(':hidden'), true, "James K hidden");
	
	equal($("#student_email-0").is(':hidden'), true, "Alice Betsy's email hidden");
	equal($("#student_email-1").is(':visible'), true, "Benny Charles's email visible");
	equal($("#student_email-2").is(':hidden'), true, "Alice Betsy's email hidden");
	equal($("#student_email-3").is(':visible'), true, "Benny Charles's email visible");
	equal($("#student_email-4").is(':hidden'), true, "Charlie D's email hidden");
	equal($("#student_email-5").is(':hidden'), true, "Denny Charlés's email hidden");
	equal($("#student_email-6").is(':hidden'), true, "Emma F's email hidden");
	equal($("#student_email-7").is(':hidden'), true, "Frank Gatsby's email hidden");
	equal($("#student_email-8").is(':hidden'), true, "Gabriel Hobb's email hidden");
	equal($("#student_email-9").is(':hidden'), true, "Hans Iker's email hidden");
	equal($("#student_email-10").is(':hidden'), true, "Ian Jacobsson's email hidden");
	equal($("#student_email-11").is(':hidden'), true, "James K's email hidden");
	
	//Search 1 student name that only exist in 1 of the course
	searchName("James");
	equal($("#course-0").is(':hidden'), true, "Course with no student visible is hidden");
	
	equal($("#student-2").is(':hidden'), true, "Alice Betsy hidden");
	equal($("#student-3").is(':hidden'), true, "Benny Charles hidden");
	equal($("#student-4").is(':hidden'), true, "Charlie D hidden");
	equal($("#student-5").is(':hidden'), true, "Denny Charlés hidden");
	equal($("#student-6").is(':hidden'), true, "Emma F hidden");
	equal($("#student-7").is(':hidden'), true, "Frank Gatsby hidden");
	equal($("#student-8").is(':hidden'), true, "Gabriel Hobb hidden");
	equal($("#student-9").is(':hidden'), true, "Hans Iker hidden");
	equal($("#student-10").is(':hidden'), true, "Ian Jacobsson hidden");
	equal($("#student-11").is(':visible'), true, "James K visible");
	
	equal($("#student_email-2").is(':hidden'), true, "Alice Betsy's email hidden");
	equal($("#student_email-3").is(':hidden'), true, "Benny Charles's email hidden");
	equal($("#student_email-4").is(':hidden'), true, "Charlie D's email hidden");
	equal($("#student_email-5").is(':hidden'), true, "Denny Charlés's email hidden");
	equal($("#student_email-6").is(':hidden'), true, "Emma F's email hidden");
	equal($("#student_email-7").is(':hidden'), true, "Frank Gatsby's email hidden");
	equal($("#student_email-8").is(':hidden'), true, "Gabriel Hobb's email hidden");
	equal($("#student_email-9").is(':hidden'), true, "Hans Iker's email hidden");
	equal($("#student_email-10").is(':hidden'), true, "Ian Jacobsson's email hidden");
	equal($("#student_email-11").is(':visible'), true, "James K's email visible");
	
	//Search for 1 course
	searchName("course2");
	equal($("#course-1").is(':hidden'), true, "Hidden course3");
	
	equal($("#student-0").is(':visible'), true, "Alice Betsy visible");
	equal($("#student-1").is(':visible'), true, "Benny Charles visible");
	
	equal($("#student_email-0").is(':visible'), true, "Alice Betsy's email visible");
	equal($("#student_email-1").is(':visible'), true, "Benny Charles's email visible");
});

function testToggleDeleteStudentConfirmation(){};
test('toggleDeleteStudentConfirmation(courseId, studentName)', function(){
	//gives a popup, can't be tested
	expect(0);
});

function testHideEmails(){};
test('hideEmails()', function(){
	//The method has been tested by UI test, and part of testSearch above.
	expect(0);
});