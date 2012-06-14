var xmlhttp = new getXMLObject();

/**---------------------------------------------------------added 26 Mar 2012------------------------------------*/

/*
 * Student view evaluation results
 */
function printEvaluationResultStudentForm(summaryList, submissionList, start,
		deadline) {

	logSummaryList(summaryList);
	var claimedPoints = summaryList[0].claimedPoints;
	var perceivedPoints = summaryList[0].average;

	if (isNaN(perceivedPoints))
		perceivedPoints = "N/A";

	var output = "<br />" + "<div>" + "<h1>Evaluation Results</h1>" + "</div>"
			+ "<table class=\"result_studentform\">" + "<tr>"
			+ "<td width=\"20%\">"
			+ STUDENT
			+ "</td>"
			+ "<td width=\"30%\">"
			+ summaryList[0].toStudentName
			+ "</td>"
			+ "<td width=\"20%\">"
			+ COURSE
			+ "</td>"
			+ "<td width=\"30%\">"
			+ submissionList[0].courseID
			+ "</td>"
			+ "</tr>"
			+ "<tr>"
			+ "<td>"
			+ TEAM
			+ "</td>"
			+ "<td>"
			+ encodeCharForPrint(submissionList[0].teamName)
			+ "</td>"
			+ "<td>"
			+ EVALUATION
			+ "</td>"
			+ "<td>"
			+ submissionList[0].evaluationName
			+ "</td>"
			+ "</tr>"
			+ "<tr>"
			+ "<td>"
			+ "<span onmouseover=\"ddrivetip('Claimed contribution is what you claimed you contributed to the project')\" onmouseout=\"hideddrivetip()\">"
			+ CLAIMED
			+ "</span>"
			+ "</td>"
			+ "<td>"
			+ displayEvaluationPoints(claimedPoints)
			+ "</td>"
			+ "<td>"
			+ OPENING
			+ "</td>"
			+ "<td>"
			+ start
			+ "</td>"
			+ "</tr>"
			+ "<tr>"
			+ "<td>"
			+ "<span onmouseover=\"ddrivetip('Perceived contribution is the average of what the other team members think you contributed to the project')\" onmouseout=\"hideddrivetip()\">"
			+ PERCEIVED
			+ "</span></td>"
			+ "<td>"
			+ displayEvaluationPoints(perceivedPoints)
			+ "</td>"
			+ "<td>"
			+ CLOSING
			+ "</td>"
			+ "<td>"
			+ deadline
			+ "</td>"
			+ "</tr>"
			+ "</table>"
			+ "<table class = \"result_table\" id = \"\">"
			+ "<tr class = \"result_subheader\">"
			+ "<td>"
			+ FEEDBACK_FROM
			+ "</td>" + "</tr>";

	submissionList.splice(0, 1);

	var targetStudent = summaryList[0].toStudent;
	var ctr = 0;
	var submissionListLength = submissionList.length;
	for (var loop = 0; loop < submissionListLength; loop++) {
		if (submissionList[loop].toStudent == targetStudent
				&& submissionList[loop].fromStudent != targetStudent) {
			if (encodeChar(submissionList[loop].commentsToStudent) == "") {
				output = output + "<tr><td>" + NA + "</td></tr>";
			} else {
				output = output
						+ "<tr><td id=\"com"
						+ ctr
						+ "\">"
						+ encodeCharForPrint(submissionList[loop].commentsToStudent)
						+ "</td></tr>";
			}
		}
	}

	output = output
			+ "</tr></table><br /><br />"
			+ "<input type=\"button\" class=\"button\" id=\"button_back\" onclick=\"displayEvaluationsTab();\" value=\"Back\" />"
			+ "<br /><br />";

	document.getElementById(DIV_EVALUATION_RESULTS).innerHTML = output;
}

/**
 * Function to check whether the evaluation submission edit/submit form has
 * been fully filled (no unfilled textarea and dropdown box)
 * @returns {Boolean}
 */
function checkEvaluationForm(){
	points = $("select");
	comments = $("textarea");
	for(var i=0; i<points.length; i++){
		if(points[i].value==''){
			setStatusMessage("Please give contribution scale to everyone",true);
			return false;
		}
	}
	for(var i=0; i<comments.length; i++){
		if(comments[i].value==''){
			setStatusMessage("Please fill in all fields",true);
			return false;
		}
	}
	return true;
}

window.onload = function() {
	initializetooltip();
};

// DynamicDrive JS mouse-hover
document.onmousemove = positiontip;
