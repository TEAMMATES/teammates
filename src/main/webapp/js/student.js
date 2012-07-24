/* 
 * This Javascript file is included in all student pages. Functions here 
 * should be common to the student pages.
 */



//Initial load-up
//-----------------------------------------------------------------------------
window.onload = function() {
	initializetooltip();
};

// DynamicDrive JS mouse-hover
document.onmousemove = positiontip;

//-----------------------------------------------------------------------------





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