module("Coordinator View Submission Results");


//test sendGetSubmissionListRequest()

//test processGetSubmissionListResponse()

/***
 * unit function: claimedPointsToString(points, pointsBumpRatio)
 */
test('claimedPointsToString()', function(){
	var actual;
	var expected;
	
	actual = claimedPointsToString(90, 1.111111);
	expected = 100;
	equal(actual, expected, "claimedPointsToString(90, 1.111111) expected: 100");
	
	actual = claimedPointsToString(-999, 1.111111);
	expected = "N/A";
	equal(actual, expected, "claimedPointsToString(-999, 1.111111) expected: N/A");
	
	actual = claimedPointsToString(-101, 1.111111);
	expected = "NOT SURE";
	equal(actual, expected, "claimedPointsToString(-101, 1.111111) expected: NOT SURE");
	
});


/***
 * unit function: getDifference(average, claimedPoints)
 */
test('getDifference()', function(){
	var actual;
	var expected;
	var average;
	var claimedPoints;
	
	//case 1: no difference
	average = 100;
	claimedPoints = 100;
	expected = 0;
	actual = getDifference(average, claimedPoints);
	equal(actual, expected, "getDifference(100, 100) expected: 0");
	
	//case 2: negative difference
	average = 100;
	claimedPoints = 130;
	expected = -30;
	actual = getDifference(average, claimedPoints);
	equal(actual, expected, "getDifference(100, 130) expected: -30");
	
	//case 3: positive difference
	average = 100;
	claimedPoints = 80;
	expected = 20;
	actual = getDifference(average, claimedPoints);
	equal(actual, expected, "getDifference(100, 80) expected: 20");
	
	//case 4: decimal rounded
	average = 88.88;
	claimedPoints = 130;
	expected = -41;
	actual = getDifference(average, claimedPoints);
	equal(actual, expected, "getDifference(88.88, 130) expected: -41");
	
	//case 5: average == NaN
	average = "N/A";
	claimedPoints = 130;
	expected = "N/A";
	actual = getDifference(average, claimedPoints);
	equal(actual, expected, "getDifference(N/A, 130) expected: N/A");
	
	//case 6: claimedPoints == NaN
	average = 100;
	claimedPoints = "Not Sure";
	expected = "N/A";
	actual = getDifference(average, claimedPoints);
	equal(actual, expected, "getDifference(100, null) expected: N/A");
	
});


/**
 * integrated function: compileSubmissionSummaryList()
 * */
test('complieSubmissionSumaryList()', function(){
	
	testCompileSubmissionSummaryList(data.submissionPoints1, 1);
	testCompileSubmissionSummaryList(data.submissionPoints2, 2);
	testCompileSubmissionSummaryList(data.submissionPoints3, 3);
	testCompileSubmissionSummaryList(data.submissionPoints4, 4);
	testCompileSubmissionSummaryList(data.submissionPoints5, 5);
	testCompileSubmissionSummaryList(data.submissionPoints6, 6);
	testCompileSubmissionSummaryList(data.submissionPoints7, 7);
	testCompileSubmissionSummaryList(data.submissionPoints8, 8);
	testCompileSubmissionSummaryList(data.submissionPoints9, 9);
	testCompileSubmissionSummaryList(data.submissionPoints10, 10);
	testCompileSubmissionSummaryList(data.submissionPoints11, 11);
	testCompileSubmissionSummaryList(data.submissionPoints12, 12);
	testCompileSubmissionSummaryList(data.submissionPoints13, 13);
	testCompileSubmissionSummaryList(data.submissionPoints14, 14);
	testCompileSubmissionSummaryList(data.submissionPoints15, 15);
	testCompileSubmissionSummaryList(data.submissionPoints16, 16);
	testCompileSubmissionSummaryList(data.submissionPoints17, 17);
	testCompileSubmissionSummaryList(data.submissionPoints18, 18);
	testCompileSubmissionSummaryList(data.submissionPoints19, 19);
	testCompileSubmissionSummaryList(data.submissionPoints20, 20);
	testCompileSubmissionSummaryList(data.submissionPoints21, 21);
	testCompileSubmissionSummaryList(data.submissionPoints22, 22);
	testCompileSubmissionSummaryList(data.submissionPoints23, 23);
	testCompileSubmissionSummaryList(data.submissionPoints24, 24);
	testCompileSubmissionSummaryList(data.submissionPoints25, 25);
	testCompileSubmissionSummaryList(data.submissionPoints26, 26);
	testCompileSubmissionSummaryList(data.submissionPoints27, 27);
	testCompileSubmissionSummaryList(data.submissionPoints28, 28);
	testCompileSubmissionSummaryList(data.submissionPoints0, 0);
});

function testCompileSubmissionSummaryList(testData, index){
	var submissionList = prepareSubmissionList(testData);
	var expected = prepareSummaryList(testData);
	var actual = compileSubmissionSummaryList(submissionList);
	deepEqual(actual, expected, "test submissionPoints" + index);
}



/**----------------------------------PREPARE TEST DATA----------------------------------**/
function prepareSubmissionList(dataSet){

	var studentList = data.students;
	
	var submissionList = new Array();
	var i;
	for(i = 0; i < dataSet.length; i++){
		var student = studentList[i];
		var dataList = dataSet[i].split("; ");

		//submission points
		var originalPoints = dataList[0].substr(10).split(", ");
		var bumpRatio = dataList[7].substr(11);
		
		var j;
		var sum = 0;
		var teamSize = originalPoints.length;
		for(j = 0; j < teamSize; j++){
			var studentIndex = Math.floor(i/teamSize) * teamSize + j;
			
			var fromStudentName = student.name;
			var toStudentName = studentList[studentIndex].name;
			var fromStudent = student.email;
			var toStudent = studentList[studentIndex].email;
			var courseID = data.course.id;
			var evaluationName = data.evaluation.name;
			var teamName = student.team;
			var justification = "this is justification.";
			var commentsToStudent = "this is comments to student";
			var points = parseInt(originalPoints[j]);
			var pointsBumpRatio = parseFloat(bumpRatio);//TODO: add attribute in JSON
			var fromStudentComments = "this is fromStudent comments";
			var toStudentComments = "this is toStudent comments";
			
			submissionList[i*teamSize + j] = {
				fromStudentName : fromStudentName,
				toStudentName : toStudentName,
				fromStudent : fromStudent,
				toStudent : toStudent,
				courseID : courseID,
				evaluationName : evaluationName,
				teamName : teamName,
				justification : justification,
				commentsToStudent : commentsToStudent,
				points : points,
				pointsBumpRatio : pointsBumpRatio,
				fromStudentComments : fromStudentComments,
				toStudentComments : toStudentComments
			};
			
			if(points != -999 && points != -101)
				sum += points;
		}
		
		//special case data processing (see test 9)		
		if(sum == 0){
			for(j = 0; j < teamSize; j++){
				if(submissionList[i*teamSize + j].points != -101 && submissionList[i*teamSize + j].points != -999)
					submissionList[i*teamSize + j].points = 100;
			}
		}
	}
	
//	deepEqual("", submissionList);
	return submissionList;
}

function prepareSummaryList(dataSet){
	var studentList = data.students;
	
	var summaryList = new Array();

	var i;
	for(i = 0; i < dataSet.length; i++){

		//read raw data
		var student = studentList[i];
		var dataList = dataSet[i].split("; ");

		var claimedCoord = stringToPoint(dataList[4].substr(14));
		var perceivedCoord = stringToPoint(dataList[5].substr(16));
		var diff = stringToPoint(dataList[6].substr(6));

		var originalPoints = dataList[0].substr(10).split(", ");
		var teamSize = originalPoints.length;
		var studentIndex = i%teamSize;
		
		//prepare data
		var toStudent = student.email;
		var toStudentName = student.name;
		var teamName = student.team;
		var average = perceivedCoord;
		var difference = diff;
		var toStudentComments = "this is toStudent comments";
		var submitted = (originalPoints[studentIndex] != -999);//TODO: get it right
		var claimedPoints = claimedCoord;
		
		summaryList[i] = {
			toStudent : toStudent,
			toStudentName : toStudentName,
			teamName : teamName,
			average : average,
			difference : difference,
			toStudentComments : toStudentComments,
			submitted : submitted,
			claimedPoints : claimedPoints
		};
		
//		deepEqual("", summaryList);
	}
	return summaryList;
}

function stringToPoint(point){

	var equalShare = "Equal Share";
	var number = parseInt(point);
	
	if(!isNaN(number))
		return number;
	else if(point == "0%")
		return -100;
	else if(point == equalShare)
		return 100;
	else if(point.charAt(equalShare.length + 1) == "+")
		return 100 + parseInt(point.split("%")[0].substr(equalShare.length + 3));
	else if(point.charAt(equalShare.length + 1) == "-")
		return 100 - parseInt(point.split("%")[0].substr(equalShare.length + 3));
	else
		return point;
}

var data = {
	"coordinator" : {
		"username" : "teammates.coord"
	},
	"course" : {
		"id" : "CS2103-TESTING",
		"name" : "Software Engineering"
	},

	"course2" : {
		"id" : "CS1101-TESTING",
		"name" : "Programming Methodology"
	},

	"evaluation" : {
		"graceperiod" : 10,
		"instructions" : "Please please fill it in",
		"name" : "First Eval",
		"p2pcomments" : "true"
	},
	"evaluation2" : {
		"graceperiod" : 10,
		"instructions" : "Please please fill in the second evaluation",
		"name" : "Second Eval",
		"p2pcomments" : "true"
	},
	"evaluation3" : {
		"graceperiod" : 10,
		"instructions" : "Please please fill in the third evaluation",
		"name" : "Third Eval",
		"p2pcomments" : "true"
	},
	"evaluation4" : {
		"graceperiod" : 10,
		"instructions" : "Please please fill in the forth evaluation",
		"name" : "Forth Eval",
		"p2pcomments" : "true"
	},

	"teams2" : [ "Team Point", "Team Point2" ],
	"students" : [ {
		"name" : "Alice",
		"email" : "alice.tmms@gmail.com",
		"google_id" : "alice.tmms",
		"team" : "Team Point"
	}, {
		"name" : "Benny",
		"email" : "benny.tmms@gmail.com",
		"google_id" : "benny.tmms",
		"team" : "Team Point"
	}, {
		"name" : "Charlie",
		"email" : "charlie.tmms@gmail.com",
		"google_id" : "charlie.tmms",
		"team" : "Team Point"
	}, {
		"name" : "Danny",
		"email" : "danny.tmms@gmail.com",
		"google_id" : "danny.tmms",
		"team" : "Team Point"
	}, {
		"name" : "Emily",
		"email" : "emily.tmms@gmail.com",
		"team" : "Team Point2",
		"google_id" : "emily.tmms"
	}, {
		"name" : "Frank",
		"email" : "frank.tmms@gmail.com",
		"google_id" : "frank.tmms",
		"team" : "Team Point2"
	}, {
		"name" : "Gary",
		"email" : "gary.tmms@gmail.com",
		"google_id" : "gary.tmms",
		"team" : "Team Point2"
	}, {
		"name" : "Henry",
		"email" : "henry.tmms@gmail.com",
		"google_id" : "henry.tmms",
		"team" : "Team Point2"
	} ],

	"submissionPoints0" : [
			"original: 80, 70, 150, 90; normalized: Equal Share - 18%, Equal Share - 28%, Equal Share + 54%, Equal Share - 8%; claimed: Equal Share - 20%; perceived: Equal Share; claimedCoord: Equal Share - 18%; perceivedCoord: Equal Share; diff: 18; bumpratio: 1.0256411",
			"original: 90, 100, 130, 80; normalized: Equal Share - 10%, Equal Share, Equal Share + 30%, Equal Share - 20%; claimed: Equal Share; perceived: Equal Share - 15%; claimedCoord: Equal Share; perceivedCoord: Equal Share - 15%; diff: -15; bumpratio: 1.0",
			"original: 100, 90, 120, 80; normalized: Equal Share + 3%, Equal Share - 8%, Equal Share + 23%, Equal Share - 18%; claimed: Equal Share + 20%; perceived: Equal Share + 29%; claimedCoord: Equal Share + 23%; perceivedCoord: Equal Share + 29%; diff: 6; bumpratio: 1.0256411",
			"original: 110, 90, 100, 130; normalized: Equal Share + 2%, Equal Share - 16%, Equal Share - 7%, Equal Share + 21%; claimed: Equal Share + 30%; perceived: Equal Share - 13%; claimedCoord: Equal Share + 21%; perceivedCoord: Equal Share - 13%; diff: -34; bumpratio: 0.9302326",
			"original: 80, 70, 150, 90; normalized: Equal Share - 18%, Equal Share - 28%, Equal Share + 54%, Equal Share - 8%; claimed: Equal Share - 20%; perceived: Equal Share; claimedCoord: Equal Share - 18%; perceivedCoord: Equal Share; diff: 18; bumpratio: 1.0256411",
			"original: 90, 100, 130, 80; normalized: Equal Share - 10%, Equal Share, Equal Share + 30%, Equal Share - 20%; claimed: Equal Share; perceived: Equal Share - 15%; claimedCoord: Equal Share; perceivedCoord: Equal Share - 15%; diff: -15; bumpratio: 1.0",
			"original: 100, 90, 120, 80; normalized: Equal Share + 3%, Equal Share - 8%, Equal Share + 23%, Equal Share - 18%; claimed: Equal Share + 20%; perceived: Equal Share + 29%; claimedCoord: Equal Share + 23%; perceivedCoord: Equal Share + 29%; diff: 6; bumpratio: 1.0256411",
			"original: 110, 90, 100, 130; normalized: Equal Share + 2%, Equal Share - 16%, Equal Share - 7%, Equal Share + 21%; claimed: Equal Share + 30%; perceived: Equal Share - 13%; claimedCoord: Equal Share + 21%; perceivedCoord: Equal Share - 13%; diff: -34; bumpratio: 0.9302326", ],

	"submissionPoints1" : [
			"original: 120, 60; normalized: Equal Share + 33%, Equal Share - 33%; claimed: Equal Share + 20%; perceived: Equal Share - 23%; claimedCoord: Equal Share + 33%; perceivedCoord: Equal Share - 23%; diff: -56; bumpratio: 1.111111",
			"original: 40, 150; normalized: Equal Share - 58%, Equal Share + 58%; claimed: Equal Share + 50%; perceived: Equal Share + 23%; claimedCoord: Equal Share + 58%; perceivedCoord: Equal Share + 23%; diff: -35; bumpratio: 1.052632", ],

	"submissionPoints2" : [
			"original: 120, 90; normalized: Equal Share + 14%, Equal Share - 14%; claimed: Equal Share + 20%; perceived: N/A; claimedCoord: Equal Share + 14%; perceivedCoord: N/A; diff: N/A; bumpratio: 0.95238096",
			"original: -999, -999; normalized: N/A, N/A; claimed: N/A; perceived: Equal Share; claimedCoord: N/A; perceivedCoord: Equal Share; diff: N/A; bumpratio: 1.0", ],

	"submissionPoints3" : [
			"original: -999, -999; normalized: N/A, N/A; claimed: N/A; perceived: N/A; claimedCoord: N/A; perceivedCoord: N/A; diff: N/A; bumpratio: 1.0",
			"original: -999, -999; normalized: N/A, N/A; claimed: N/A; perceived: N/A; claimedCoord: N/A; perceivedCoord: N/A; diff: N/A; bumpratio: 1.0", ],

	"submissionPoints4" : [
			"original: 130, -101; normalized: Equal Share, NOT SURE; claimed: Equal Share + 30%; perceived: Equal Share; claimedCoord: Equal Share; perceivedCoord: Equal Share; diff: 0; bumpratio: 0.7692308",
			"original: 110, 70; normalized: Equal Share + 22%, Equal Share - 22%; claimed: Equal Share - 30%; perceived: N/A; claimedCoord: Equal Share - 22%; perceivedCoord: N/A; diff: N/A; bumpratio: 1.1111112", ],

	"submissionPoints5" : [
			"original: 110, -101; normalized: Equal Share, NOT SURE; claimed: Equal Share + 10%; perceived: N/A; claimedCoord: Equal Share; perceivedCoord: N/A; diff: N/A; bumpratio: 0.90909094",
			"original: -101, 160; normalized: NOT SURE, Equal Share; claimed: Equal Share + 60%; perceived: N/A; claimedCoord: Equal Share; perceivedCoord: N/A; diff: N/A; bumpratio: 0.625", ],

	"submissionPoints6" : [
			"original: -101, -101; normalized: NOT SURE, NOT SURE; claimed: NOT SURE; perceived: N/A; claimedCoord: NOT SURE; perceivedCoord: N/A; diff: N/A; bumpratio: 1.0",
			"original: -101, -101; normalized: NOT SURE, NOT SURE; claimed: NOT SURE; perceived: N/A; claimedCoord: NOT SURE; perceivedCoord: N/A; diff: N/A; bumpratio: 1.0", ],

	"submissionPoints7" : [
			"original: 100, 100; normalized: Equal Share, Equal Share; claimed: Equal Share; perceived: Equal Share; claimedCoord: Equal Share; perceivedCoord: Equal Share; diff: 0; bumpratio: 1.0",
			"original: 100, 100; normalized: Equal Share, Equal Share; claimed: Equal Share; perceived: Equal Share; claimedCoord: Equal Share; perceivedCoord: Equal Share; diff: 0; bumpratio: 1.0", ],

	"submissionPoints8" : [
			"original: 200, 200; normalized: Equal Share, Equal Share; claimed: Equal Share + 100%; perceived: Equal Share; claimedCoord: Equal Share; perceivedCoord: Equal Share; diff: 0; bumpratio: 0.5",
			"original: 200, 200; normalized: Equal Share, Equal Share; claimed: Equal Share + 100%; perceived: Equal Share; claimedCoord: Equal Share; perceivedCoord: Equal Share; diff: 0; bumpratio: 0.5", ],

	"submissionPoints9" : [
			"original: 0, 0; normalized: Equal Share, Equal Share; claimed: Equal Share; perceived: Equal Share; claimedCoord: Equal Share; perceivedCoord: Equal Share; diff: 0; bumpratio: 1.0",
			"original: 0, 0; normalized: Equal Share, Equal Share; claimed: Equal Share; perceived: Equal Share; claimedCoord: Equal Share; perceivedCoord: Equal Share; diff: 0; bumpratio: 1.0", ],

	"submissionPoints10" : [
			"original: 120, 90, 80; normalized: Equal Share + 24%, Equal Share - 7%, Equal Share - 17%; claimed: Equal Share + 20%; perceived: Equal Share - 11%; claimedCoord: Equal Share + 24%; perceivedCoord: Equal Share - 11%; diff: -35; bumpratio: 1.0344827",
			"original: 80, 140, 50; normalized: Equal Share - 11%, Equal Share + 56%, Equal Share - 44%; claimed: Equal Share + 40%; perceived: Equal Share + 27%; claimedCoord: Equal Share + 56%; perceivedCoord: Equal Share + 27%; diff: -29; bumpratio: 1.1111112",
			"original: 60, 120, 130; normalized: Equal Share - 42%, Equal Share + 16%, Equal Share + 26%; claimed: Equal Share + 30%; perceived: Equal Share - 16%; claimedCoord: Equal Share + 26%; perceivedCoord: Equal Share - 16%; diff: -42; bumpratio: 0.9677419", ],

	"submissionPoints11" : [
			"original: 120, 90, 70; normalized: Equal Share + 29%, Equal Share - 4%, Equal Share - 25%; claimed: Equal Share + 20%; perceived: Equal Share + 6%; claimedCoord: Equal Share + 29%; perceivedCoord: Equal Share + 6%; diff: -23; bumpratio: 1.0714285",
			"original: 80, 100, 70; normalized: Equal Share - 4%, Equal Share + 20%, Equal Share - 16%; claimed: Equal Share; perceived: Equal Share + 6%; claimedCoord: Equal Share + 20%; perceivedCoord: Equal Share + 6%; diff: -14; bumpratio: 1.2",
			"original: -999, -999, -999; normalized: N/A, N/A, N/A; claimed: N/A; perceived: Equal Share - 12%; claimedCoord: N/A; perceivedCoord: Equal Share - 12%; diff: N/A; bumpratio: 1.0", ],

	"submissionPoints12" : [
			"original: -999, -999, -999; normalized: N/A, N/A, N/A; claimed: N/A; perceived: Equal Share - 6%; claimedCoord: N/A; perceivedCoord: Equal Share - 6%; diff: N/A; bumpratio: 1.0",
			"original: 80, 120, 90; normalized: Equal Share - 17%, Equal Share + 24%, Equal Share - 7%; claimed: Equal Share + 20%; perceived: N/A; claimedCoord: Equal Share + 24%; perceivedCoord: N/A; diff: N/A; bumpratio: 1.0344827",
			"original: -999, -999, -999; normalized: N/A, N/A, N/A; claimed: N/A; perceived: Equal Share + 6%; claimedCoord: N/A; perceivedCoord: Equal Share + 6%; diff: N/A; bumpratio: 1.0", ],

	"submissionPoints13" : [
			"original: -999, -999, -999; normalized: N/A, N/A, N/A; claimed: N/A; perceived: N/A; claimedCoord: N/A; perceivedCoord: N/A; diff: N/A; bumpratio: 1.0",
			"original: -999, -999, -999; normalized: N/A, N/A, N/A; claimed: N/A; perceived: N/A; claimedCoord: N/A; perceivedCoord: N/A; diff: N/A; bumpratio: 1.0",
			"original: -999, -999, -999; normalized: N/A, N/A, N/A; claimed: N/A; perceived: N/A; claimedCoord: N/A; perceivedCoord: N/A; diff: N/A; bumpratio: 1.0", ],

	"submissionPoints14" : [
			"original: 130, 80, -101; normalized: Equal Share + 24%, Equal Share - 24%, NOT SURE; claimed: Equal Share + 30%; perceived: Equal Share - 8%; claimedCoord: Equal Share + 24%; perceivedCoord: Equal Share - 8%; diff: -32; bumpratio: 0.95238096",
			"original: 70, 110, 90; normalized: Equal Share - 22%, Equal Share + 22%, Equal Share; claimed: Equal Share + 10%; perceived: Equal Share - 3%; claimedCoord: Equal Share + 22%; perceivedCoord: Equal Share - 3%; diff: -25; bumpratio: 1.1111112",
			"original: 90, 100, 120; normalized: Equal Share - 13%, Equal Share - 3%, Equal Share + 16%; claimed: Equal Share + 20%; perceived: Equal Share + 11%; claimedCoord: Equal Share + 16%; perceivedCoord: Equal Share + 11%; diff: -5; bumpratio: 0.9677419", ],

	"submissionPoints15" : [
			"original: 90, 100, -101; normalized: Equal Share - 5%, Equal Share + 5%, NOT SURE; claimed: Equal Share - 10%; perceived: Equal Share - 7%; claimedCoord: Equal Share - 5%; perceivedCoord: Equal Share - 7%; diff: -2; bumpratio: 1.0526316",
			"original: 60, 80, 60; normalized: Equal Share - 10%, Equal Share + 20%, Equal Share - 10%; claimed: Equal Share - 20%; perceived: Equal Share + 12%; claimedCoord: Equal Share + 20%; perceivedCoord: Equal Share + 12%; diff: -8; bumpratio: 1.5",
			"original: 80, -101, 110; normalized: Equal Share - 16%, NOT SURE, Equal Share + 16%; claimed: Equal Share + 10%; perceived: Equal Share - 4%; claimedCoord: Equal Share + 16%; perceivedCoord: Equal Share - 4%; diff: -20; bumpratio: 1.0526316", ],

	"submissionPoints16" : [
			"original: 110, -101, 70; normalized: Equal Share + 22%, NOT SURE, Equal Share - 22%; claimed: Equal Share + 10%; perceived: Equal Share + 1%; claimedCoord: Equal Share + 22%; perceivedCoord: Equal Share + 1%; diff: -21; bumpratio: 1.1111112",
			"original: -101, 90, 80; normalized: NOT SURE, Equal Share + 6%, Equal Share - 6%; claimed: Equal Share - 10%; perceived: N/A; claimedCoord: Equal Share + 6%; perceivedCoord: N/A; diff: N/A; bumpratio: 1.1764706",
			"original: 70, -101, 90; normalized: Equal Share - 12%, NOT SURE, Equal Share + 13%; claimed: Equal Share - 10%; perceived: Equal Share - 1%; claimedCoord: Equal Share + 13%; perceivedCoord: Equal Share - 1%; diff: -14; bumpratio: 1.25", ],

	"submissionPoints17" : [
			"original: -101, -101, -101; normalized: NOT SURE, NOT SURE, NOT SURE; claimed: NOT SURE; perceived: N/A; claimedCoord: NOT SURE; perceivedCoord: N/A; diff: N/A; bumpratio: 1.0",
			"original: -101, -101, -101; normalized: NOT SURE, NOT SURE, NOT SURE; claimed: NOT SURE; perceived: N/A; claimedCoord: NOT SURE; perceivedCoord: N/A; diff: N/A; bumpratio: 1.0",
			"original: -101, -101, -101; normalized: NOT SURE, NOT SURE, NOT SURE; claimed: NOT SURE; perceived: N/A; claimedCoord: NOT SURE; perceivedCoord: N/A; diff: N/A; bumpratio: 1.0", ],

	"submissionPoints18" : [
			"original: 100, 100, 100; normalized: Equal Share, Equal Share, Equal Share; claimed: Equal Share; perceived: Equal Share; claimedCoord: Equal Share; perceivedCoord: Equal Share; diff: 0; bumpratio: 1.0",
			"original: 100, 100, 100; normalized: Equal Share, Equal Share, Equal Share; claimed: Equal Share; perceived: Equal Share; claimedCoord: Equal Share; perceivedCoord: Equal Share; diff: 0; bumpratio: 1.0",
			"original: 100, 100, 100; normalized: Equal Share, Equal Share, Equal Share; claimed: Equal Share; perceived: Equal Share; claimedCoord: Equal Share; perceivedCoord: Equal Share; diff: 0; bumpratio: 1.0", ],

	"submissionPoints19" : [
			"original: 200, 200, 200; normalized: Equal Share, Equal Share, Equal Share; claimed: Equal Share + 100%; perceived: Equal Share; claimedCoord: Equal Share; perceivedCoord: Equal Share; diff: 0; bumpratio: 0.5",
			"original: 200, 200, 200; normalized: Equal Share, Equal Share, Equal Share; claimed: Equal Share + 100%; perceived: Equal Share; claimedCoord: Equal Share; perceivedCoord: Equal Share; diff: 0; bumpratio: 0.5",
			"original: 200, 200, 200; normalized: Equal Share, Equal Share, Equal Share; claimed: Equal Share + 100%; perceived: Equal Share; claimedCoord: Equal Share; perceivedCoord: Equal Share; diff: 0; bumpratio: 0.5", ],

	"submissionPoints20" : [
			"original: 0, 0, 0; normalized: Equal Share, Equal Share, Equal Share; claimed: Equal Share; perceived: Equal Share; claimedCoord: Equal Share; perceivedCoord: Equal Share; diff: 0; bumpratio: 1.0",
			"original: 0, 0, 0; normalized: Equal Share, Equal Share, Equal Share; claimed: Equal Share; perceived: Equal Share; claimedCoord: Equal Share; perceivedCoord: Equal Share; diff: 0; bumpratio: 1.0",
			"original: 0, 0, 0; normalized: Equal Share, Equal Share, Equal Share; claimed: Equal Share; perceived: Equal Share; claimedCoord: Equal Share; perceivedCoord: Equal Share; diff: 0; bumpratio: 1.0", ],

	"submissionPoints21" : [
			"original: -101, 100; normalized: NOT SURE, Equal Share; claimed: NOT SURE; perceived: Equal Share - 5%; claimedCoord: NOT SURE; perceivedCoord: Equal Share - 5%; diff: N/A; bumpratio: 1.0",
			"original: 90, 110; normalized: Equal Share - 10%, Equal Share + 10%; claimed: Equal Share + 10%; perceived: Equal Share + 5%; claimedCoord: Equal Share + 10%; perceivedCoord: Equal Share + 5%; diff: -5; bumpratio: 1.0" ],

	"submissionPoints22" : [
			"original: -101, -101; normalized: NOT SURE, NOT SURE; claimed: NOT SURE; perceived: Equal Share; claimedCoord: NOT SURE; perceivedCoord: Equal Share; diff: N/A; bumpratio: 1.0",
			"original: 90, 110; normalized: Equal Share - 10%, Equal Share + 10%; claimed: Equal Share + 10%; perceived: N/A; claimedCoord: Equal Share + 10%; perceivedCoord: N/A; diff: N/A; bumpratio: 1.0" ],

	"submissionPoints23" : [
			"original: 0, 100; normalized: 0%, Equal Share + 100%; claimed: Equal Share - 100%; perceived: Equal Share - 38%; claimedCoord: Equal Share - 100%; perceivedCoord: Equal Share - 38%; diff: 62; bumpratio: 2.0",
			"original: 90, 110; normalized: Equal Share - 10%, Equal Share + 10%; claimed: Equal Share + 10%; perceived: Equal Share + 38%; claimedCoord: Equal Share + 10%; perceivedCoord: Equal Share + 38%; diff: 28; bumpratio: 1.0" ],

	"submissionPoints24" : [
			"original: 100, 0; normalized: Equal Share + 100%, Equal Share - 100%; claimed: Equal Share; perceived: Equal Share + 100%; claimedCoord: Equal Share + 100%; perceivedCoord: Equal Share + 100%; diff: 0; bumpratio: 2.0",
			"original: 90, 110; normalized: Equal Share - 10%, Equal Share + 10%; claimed: Equal Share + 10%; perceived: Equal Share - 100%; claimedCoord: Equal Share + 10%; perceivedCoord: Equal Share - 100%; diff: -110; bumpratio: 1.0" ],

	"submissionPoints25" : [
			"original: 0, 0; normalized: Equal Share, Equal Share; claimed: Equal Share; perceived: Equal Share - 5%; claimedCoord: Equal Share; perceivedCoord: Equal Share - 5%; diff: -5; bumpratio: 1.0",
			"original: 90, 110; normalized: Equal Share - 10%, Equal Share + 10%; claimed: Equal Share + 10%; perceived: Equal Share + 5%; claimedCoord: Equal Share + 10%; perceivedCoord: Equal Share + 5%; diff: -5; bumpratio: 1.0" ],

	"submissionPoints26" : [
			"original: -101, 100; normalized: NOT SURE, Equal Share; claimed: NOT SURE; perceived: Equal Share; claimedCoord: NOT SURE; perceivedCoord: Equal Share; diff: N/A; bumpratio: 1.0",
			"original: 100, -101; normalized: Equal Share, NOT SURE; claimed: NOT SURE; perceived: Equal Share; claimedCoord: NOT SURE; perceivedCoord: Equal Share; diff: N/A; bumpratio: 1.0" ],

	"submissionPoints27" : [
			"original: -101, 100; normalized: NOT SURE, Equal Share; claimed: NOT SURE; perceived: N/A; claimedCoord: NOT SURE; perceivedCoord: N/A; diff: N/A; bumpratio: 1.0",
			"original: -101, 100; normalized: NOT SURE, Equal Share; claimed: Equal Share; perceived: Equal Share; claimedCoord: Equal Share; perceivedCoord: Equal Share; diff: 0; bumpratio: 1.0" ],

	"submissionPoints28" : [
			"original: -999, -999; normalized: N/A, N/A; claimed: N/A; perceived: N/A; claimedCoord: N/A; perceivedCoord: N/A; diff: N/A; bumpratio: 1.0",
			"original: -999, -999; normalized: N/A, N/A; claimed: N/A; perceived: N/A; claimedCoord: N/A; perceivedCoord: N/A; diff: N/A; bumpratio: 1.0" ]
};


/**
 * just for test debug
 * 
 */
//test('test', function() {
//
//	var dataSet = data.submissionPoints1;
//	var studentList = data.students;
//
//	var summaryList = new Array();
//
//	var i;
//	for (i = 0; i < dataSet.length; i++) {
//
//		var normalizedPoints = dataList[1].substr(12).split(", ");
//
//		// student view
//		var claimed = dataList[2].substr(9);
//		var perceived = dataList[3].substr(11);
//	}
//	
//	var submissionList = prepareSubmissionList(data.submissionPoints1);
//	deepEqual("", submissionList);
//});