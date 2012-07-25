/*
 * coordEval.js
 * 
 *  isEvaluationNameLengthValid(name) -> used in coordEval.jsp
 *  isEvaluationNameValid(name) -> used in coordEval.jsp
 *  convertDateFromDDMMYYYYToMMDDYYYY(dateString) -> used in coordEval.jsp
 *  isAddEvaluationScheduleValid(start, startTime, deadline, deadlineTime) -> used in coordEval.jsp
 *  isEditEvaluationScheduleValid(start, startTime, deadline, deadlineTime, timeZone, activated, status) -> used in coordEval.jsp
 *  checkAddEvaluation(form) -> used in coordEval.jsp
 *  checkEditEvaluation(form) -> used in coordEval.jsp
 *  selectDefaultTimeOptions() -> used in coordEval.jsp
 *  formatDigit(num) -> used in coordEval.jsp
 *  convertDateToDDMMYYYY(date) -> used in coordEval.jsp
 *  convertDateToHHMM(date) -> used in coordEval.jsp
 *  
 */

module('coordEval.js');

test('isEvaluationNameLengthValid(name)', function(){
	equal(isEvaluationNameLengthValid("Evaluation 1"), true, "Less than 22 characters");
	equal(isEvaluationNameLengthValid("This evaluation has a very long name"), false, "Invalid evaluation name");
});


test('isEvaluationNameValid(name)', function(){
	equal(isEvaluationNameValid("Eval\Test"), true, "Eval\Test");
	equal(isEvaluationNameValid("EvalTest"), true, "EvalTest");
	equal(isEvaluationNameValid("12345"), true, "12345");
	
	equal(isEvaluationNameValid("Eval'Test"), false, "Eval'Test");
	equal(isEvaluationNameValid("Eval/Test"), false, "Eval/Test");
	equal(isEvaluationNameValid("Eval-Test"), false, "EvalTest");
	equal(isEvaluationNameValid("Eval_Test"), false, "EvalTest");
	equal(isEvaluationNameValid("Eval$Test"), false, "EvalTest");
	equal(isEvaluationNameValid("Eval%Test"), false, "EvalTest");
});


test('convertDateFromDDMMYYYYToMMDDYYYY(dateString)', function(){
	equal(convertDateFromDDMMYYYYToMMDDYYYY("21/07/2012"), "07/21/2012", "Conversion of date works");
});


test('isAddEvaluationScheduleValid(start, startTime, deadline, deadlineTime)', function(){
	var now = new Date();
	var correctstart = "31/12/" + (now.getFullYear());
	var correctend = "31/12/" + (now.getFullYear() + 1);
	var wrongstart = "31/12/" + (now.getFullYear() - 2);
	var wrongend = "31/12/" + (now.getFullYear() - 3);
	var starttime = "24";
	var endtime = "22";
	
	
	equal(isAddEvaluationScheduleValid(correctstart, starttime, correctend, endtime), true, "Normal start and end" + correctstart + " " + correctend);
	equal(isAddEvaluationScheduleValid(wrongstart, starttime, correctend, endtime), false, "Start time before current date");
	equal(isAddEvaluationScheduleValid(correctstart, starttime, wrongend, endtime), false, "End time before start time");
	equal(isAddEvaluationScheduleValid(wrongstart, starttime, wrongend, endtime), false, "Start time before current date");
	equal(isAddEvaluationScheduleValid(correctstart, starttime, correctstart, starttime), false, "start and end at same time");
});


test('isEditEvaluationScheduleValid(start, startTime, deadline, deadlineTime, timeZone, activated, status)', function(){
	var now = new Date();
	var correctstart = "31/12/" + (now.getFullYear());
	var correctend = "31/12/" + (now.getFullYear() + 1);
	var wrongstart = "31/12/" + (now.getFullYear() - 2);
	var wrongend = "31/12/" + (now.getFullYear() - 3);
	var starttime = "24";
	var endtime = "22";
	
	equal(isAddEvaluationScheduleValid(correctstart, starttime, correctend, endtime, "", "", ""), true, "Normal start and end");
	equal(isAddEvaluationScheduleValid(wrongstart, starttime, correctend, endtime, "", "", ""), false, "Start time before current date");
	equal(isAddEvaluationScheduleValid(correctstart, starttime, wrongend, endtime, "", "", ""), false, "End time before start time");
	equal(isAddEvaluationScheduleValid(wrongstart, starttime, correctend, endtime, "", "AWAITING", ""), false, "Status awaiting");
	equal(isAddEvaluationScheduleValid(wrongstart, starttime, correctend, endtime, "", "AWAITING", ""), false, "Status not awaiting");
});


test('checkAddEvaluation(form)', function(){
	var now = new Date();
	var correctstart = "31/12/" + (now.getFullYear());
	var correctend = "31/12/" + (now.getFullYear() + 1);
	var wrongstart = "31/12/" + (now.getFullYear() - 2);
	var wrongend = "31/12/" + (now.getFullYear() - 3);
	
	var form = new Object();
	form.courseid = new Object();
	form.courseid.value = "CS2103";
	form.evaluationname = new Object();
	form.evaluationname.value = "Evaluation Name";
	form.commentsstatus = new Object();
	form.commentsstatus.value = "Comments";
	form.start = new Object();
	form.start.value = correctstart;
	form.starttime = new Object();
	form.starttime.value = 24;
	form.deadline = new Object();
	form.deadline.value = correctend;
	form.deadlinetime = new Object();
	form.deadlinetime.value = 22;
	form.timezone = new Object();
	form.timezone.value = "+8";
	form.graceperiod = new Object();
	form.graceperiod.value = 10;
	form.instr = new Object();
	form.instr.value = "instruct";
	
	equal(checkAddEvaluation(form), true, "All fields are correct");
	
	form.courseid.value = "";
	form.starttime.value = "";
	equal(checkAddEvaluation(form), false, "Empty fields");
	
	form.courseid.value = "CS2103";
	form.starttime.value = 0;
	form.evaluationname.value = "Super Extreme long evaluation name";
	equal(checkAddEvaluation(form), false, "Evaluation name not valid");
	
	form.evaluationname.value = "!@#$%^& name";
	equal(checkAddEvaluation(form), false, "Evaluation name not valid");
	
	form.evaluationname.value = "Evaluation name";
	form.start.value = wrongstart;
	form.deadline.value = correctend;
	equal(checkAddEvaluation(form), false, "Schedule not valid");
	
	form.start.value = correctstart;
	form.deadline.value = wrongend;
	equal(checkAddEvaluation(form), false, "Schedule not valid");
});


test('checkEditEvaluation(form)', function(){
	var now = new Date();
	var correctstart = "31/12/" + (now.getFullYear());
	var correctend = "31/12/" + (now.getFullYear() + 1);
	var wrongstart = "31/12/" + (now.getFullYear() - 2);
	var wrongend = "31/12/" + (now.getFullYear() - 3);
	
	var form = new Object();
	form.courseid = new Object();
	form.courseid.value = "CS2103";
	form.evaluationname = new Object();
	form.evaluationname.value = "Evaluation Name";
	form.commentsstatus = new Object();
	form.commentsstatus.value = "Comments";
	form.start = new Object();
	form.start.value = correctstart;
	form.starttime = new Object();
	form.starttime.value = 24;
	form.deadline = new Object();
	form.deadline.value = correctend;
	form.deadlinetime = new Object();
	form.deadlinetime.value = 22;
	form.timezone = new Object();
	form.timezone.value = "+8";
	form.graceperiod = new Object();
	form.graceperiod.value = 10;
	form.instr = new Object();
	form.instr.value = "instruct";
	
	equal(checkEditEvaluation(form), true, "All fields are correct");
	
	form.courseid.value = "";
	form.starttime.value = "";
	equal(checkEditEvaluation(form), false, "Empty fields");
	
	form.courseid.value = "CS2103";
	form.starttime.value = 0;
	form.evaluationname.value = "Super Extreme long evaluation name";
	equal(checkEditEvaluation(form), false, "Evaluation name not valid");
	
	form.evaluationname.value = "!@#$%^& name";
	equal(checkEditEvaluation(form), false, "Evaluation name not valid");
	
	form.evaluationname.value = "Evaluation name";
	form.start.value = wrongstart;
	form.deadline.value = correctend;
	equal(checkEditEvaluation(form), false, "Schedule not valid");
	
	form.start.value = correctstart;
	form.deadline.value = wrongend;
	equal(checkEditEvaluation(form), false, "Schedule not valid");
});


test('selectDefaultTimeOptions()', function(){
	//N/A, uses the current date and elements in the page
	expect(0);
});


test('formatDigit(num)', function(){
	// Tested in convertDateToDDMMYYYY(date)
	expect(0);
});


test('convertDateToDDMMYYYY(date)', function(){
	var testdate1 = new Date(2012, 6, 21, 14, 18, 0);	
	equal(convertDateToDDMMYYYY(testdate1), "21/07/2012", "Date converted correctly");
});


test('convertDateToHHMM(date)', function(){
	var testdate1 = new Date(2012, 6, 21, 14, 18, 0);	
	equal(convertDateToHHMM(testdate1), "1418", "Date converted correctly");
});
