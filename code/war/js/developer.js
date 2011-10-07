// OPERATIONS
var OPERATION_TEST_OPENEVALUATION = "test_openevaluation";
var OPERATION_TEST_CLOSEEVALUATION = "test_closeevaluation";
var OPERATION_TEST_AWAITEVALUATION = "text_awaitevaluation";


function changeEvaluationState(courseID, evaluation, state) {
	var operation;
	switch(state) {
	  case "await":
		operation = OPERATION_TEST_AWAITEVALUATION;
		break;
	  case "expire":
		operation = OPERATION_TEST_CLOSEEVALUATION;
		break;
	  case "active":
		operation = OPERATION_TEST_OPENEVALUATION;
	}

	var POST = {
		operation: operation,
		courseid: courseID,
		evaluationname: evaluation
	};
	$.post( "/teammates", POST, function(data) {
		if (data == "success") {
			$("#message").html( "The evaluation status has been changed." );
		} else {
			$("#message").html( "Something went wrong. Evaluation status couldn't be changed." );
		}
	} );
}

function doChangeEvaluationState(form, state) {
	var courseID = form.elements[0].value;
	var evaluation = form.elements[1].value;
	if(courseID == "" || evaluation == "") {
		alert("Please fill in all fields.");
	} else {
		changeEvaluationState(courseID, evaluation, state);
	}
}
