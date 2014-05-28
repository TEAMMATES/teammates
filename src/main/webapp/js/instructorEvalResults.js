$(document).ready(function(){
	toggleSort($("#button_sortteamname"),1);
});


/**
 * Shows the desired evaluation report based on the id.
 * This is for the evaluation results page.
 * @param id
 * 		One of:
 * 		<ul>
 * 		<li>instructorEvaluationSummaryTable</li>
 * 		<li>instructorEvaluationDetailedReviewerTable</li>
 * 		<li>instructorEvaluationDetailedRevieweeTable</li>
 * 		</ul>
 */
function showReport(id){
    $(".evaluation_result").attr("style", "display: none;");
    $("#"+id).attr("style", "display: block;");
}


/**
 * Opens new window from link.
 * Javascript is used here so that the newly opened window will be able to
 * access this parent window.
 * @param obj
 * @returns {Boolean}
 */
function openChildWindow(link){
    window.open(link, "childWindow");
    return false;
}