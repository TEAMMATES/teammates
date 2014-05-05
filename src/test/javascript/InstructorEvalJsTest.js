
module('instructorEval.js');

test('isEvaluationNameLengthValid(name)', function(){
    equal(isEvaluationNameLengthValid('Evaluation 1'), true, "Normal input");
    equal(isEvaluationNameLengthValid(generateRandomString(EVAL_NAME_MAX_LENGTH)), true, "Maximum number of characters");
    equal(isEvaluationNameLengthValid(generateRandomString(EVAL_NAME_MAX_LENGTH + 1)), false, "Exceed maximum number of characters");
});


test('isEvaluationNameValid(name)', function(){
    equal(isEvaluationNameValid("Eval Test  123"), true, "Eval Test  123");
    equal(isEvaluationNameValid("  "), true, "  ");
    equal(isEvaluationNameValid(""), true, "");

    equal(isEvaluationNameValid("Eval'Test"), false, "Eval'Test");
    equal(isEvaluationNameValid("Eval/Test"), false, "Eval/Test");
    equal(isEvaluationNameValid("Eval-Test"), false, "Eval-Test");
    equal(isEvaluationNameValid("Eval_Test"), false, "Eval_Test");
    equal(isEvaluationNameValid("Eval$Test"), false, "Eval$Test");
    equal(isEvaluationNameValid("Eval%Test"), false, "Eval%Test");
    equal(isEvaluationNameValid("Eval*Test"), false, "Eval*Test");
    equal(isEvaluationNameValid("Eval#Test"), false, "Eval#Test");
    equal(isEvaluationNameValid("Eval&Test"), false, "Eval&Test");
    equal(isEvaluationNameValid("Eval@Test"), false, "Eval@Test");
    equal(isEvaluationNameValid("Eval^Test"), false, "Eval^Test");
    equal(isEvaluationNameValid("Eval\\Test"), false, "Eval\\Test");
    equal(isEvaluationNameValid("Eval\"Test"), false, "Eval\"Test");
});


test('convertDateFromDDMMYYYYToMMDDYYYY(dateString)', function(){
    equal(convertDateFromDDMMYYYYToMMDDYYYY("21/07/2012"), "07/21/2012", "Conversion of date");
});


test('isAddEvaluationScheduleValid(start, startTime, deadline, deadlineTime)', function(){
    var now = new Date();
    var currentYear = "31/12/" + (now.getFullYear());
    var oneYearLater = "31/12/" + (now.getFullYear() + 1);
    var oneYearBefore = "31/12/" + (now.getFullYear() - 1);
    var starttime = "24";
    var endtime = "22";
    
    
    equal(isAddEvaluationScheduleValid(currentYear, starttime, oneYearLater, endtime), true, "Normal start and end");
    equal(isAddEvaluationScheduleValid(oneYearBefore, starttime, oneYearLater, endtime), false, "Start time before current date");
    equal(isAddEvaluationScheduleValid(currentYear, starttime, oneYearBefore, endtime), false, "End time before start time");
    equal(isAddEvaluationScheduleValid(currentYear, starttime, currentYear, starttime), false, "start and end at same day, same time");
    equal(isAddEvaluationScheduleValid(currentYear, starttime, currentYear, endtime), false, "start and end at same day");
});


test('isEditEvaluationScheduleValid(start, startTime, deadline, deadlineTime, timeZone, activated, status)', function(){
    var now = new Date();
    var currentYear = "31/12/" + (now.getFullYear());
    var oneYearLater = "31/12/" + (now.getFullYear() + 1);
    var oneYearBefore = "31/12/" + (now.getFullYear() - 1);
    var starttime = "24";
    var endtime = "22";
    
    equal(isEditEvaluationScheduleValid(currentYear, starttime, oneYearLater, endtime, "", "", ""), true, "Normal start and end");
    equal(isEditEvaluationScheduleValid(currentYear, starttime, currentYear, starttime, "", "", ""), false, "start and end at same day, same time");
    equal(isEditEvaluationScheduleValid(oneYearBefore, starttime, oneYearLater, endtime, "", "", ""), true, "Start time before current date");
    equal(isEditEvaluationScheduleValid(oneYearBefore, starttime, oneYearLater, endtime, "", "", "AWAITING"), false, "Start time before current date + awaiting");
    equal(isEditEvaluationScheduleValid(currentYear, starttime, currentYear, endtime, "", "", ""), false, "start and end at same day");
    equal(isEditEvaluationScheduleValid(currentYear, starttime, oneYearBefore, endtime, "", "", ""), false, "End time before start time");
    
});


test('checkAddEvaluation(form)', function(){
    var now = new Date();
    var currentYear = "31/12/" + (now.getFullYear());
    var oneYearLater = "31/12/" + (now.getFullYear() + 1);
    var oneYearBefore = "31/12/" + (now.getFullYear() - 1);
    
    var form = {
        courseid: {value: "CS2103"},
        evaluationname: {value: "Evaluation Name"},
        commentsstatus: {value: "Comments"},
        start: {value: currentYear},
        starttime: {value: 24},
        deadline: {value: oneYearLater},
        deadlinetime: {value: 22},
        timezone: {value: +8},
        graceperiod: {value: 10},
        instr: {value: "instructions"}		
    };
    
    equal(checkAddEvaluation(form), true, "All fields are correct");
    
    form.courseid.value = "";
    equal(checkAddEvaluation(form), false, "Empty fields");
    
    form.courseid.value = "CS2103";
    form.evaluationname.value = generateRandomString(EVAL_NAME_MAX_LENGTH + 1);
    equal(checkAddEvaluation(form), false, "Evaluation name length invalid");
    
    form.evaluationname.value = "!@#$%^& name";
    equal(checkAddEvaluation(form), false, "Evaluation name invalid");
    
    form.evaluationname.value = "Evaluation name";
    form.start.value = oneYearLater;
    form.deadline.value = oneYearBefore;
    equal(checkAddEvaluation(form), false, "Schedule invalid");
    
});


test('checkEditEvaluation(form)', function(){
    var now = new Date();
    var currentYear = "31/12/" + (now.getFullYear());
    var oneYearLater = "31/12/" + (now.getFullYear() + 1);
    var oneYearBefore = "31/12/" + (now.getFullYear() - 1);
    
    var form = {
        courseid: {value: "CS2103"},
        evaluationname: {value: "Evaluation Name"},
        commentsstatus: {value: "Comments"},
        start: {value: currentYear},
        starttime: {value: 24},
        deadline: {value: oneYearLater},
        deadlinetime: {value: 22},
        timezone: {value: +8},
        graceperiod: {value: 10},
        instr: {value: "instructions"}		
    };
    
    equal(checkEditEvaluation(form), true, "All fields are correct");
    
    form.courseid.value = "";
    equal(checkEditEvaluation(form), false, "Empty fields");
    
    form.courseid.value = "CS2103";
    form.evaluationname.value = generateRandomString(EVAL_NAME_MAX_LENGTH + 1);
    equal(checkEditEvaluation(form), false, "Evaluation name length invalid");
    
    form.evaluationname.value = "!@#$%^& name";
    equal(checkEditEvaluation(form), false, "Evaluation name invalid");
    
    form.evaluationname.value = "Evaluation name";
    form.start.value = oneYearLater;
    form.deadline.value = oneYearBefore;
    equal(checkEditEvaluation(form), false, "Schedule invalid");
    
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
    equal(convertDateToDDMMYYYY(testdate1), "21/07/2012", "Date conversion to DDMMYYYY");
});


test('convertDateToHHMM(date)', function(){
    var testdate1 = new Date(2012, 6, 21, 14, 18, 0);	
    equal(convertDateToHHMM(testdate1), "1418", "Date conversion to HHMM");
});

