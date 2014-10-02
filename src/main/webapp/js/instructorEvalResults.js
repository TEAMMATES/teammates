$(document).ready(function(){
	toggleSort($("#button_sortteamname"),1);
	
	//auto select the html table when modal is shown
    $('#evalResultsHtmlWindow').on('shown.bs.modal', function (e) {
		selectElementContents( document.getElementById('summaryModalTable') );
    });
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

/**
 * function that select the whole table
 * @param el
 */

function selectElementContents(el) {
    var body = document.body, range, sel;
    if (document.createRange && window.getSelection) {
        range = document.createRange();
        sel = window.getSelection();
        sel.removeAllRanges();
        try {
            range.selectNodeContents(el);
            sel.addRange(range);
        } catch (e) {
            range.selectNode(el);
            sel.addRange(range);
        }
    } else if (body.createTextRange) {
        range = body.createTextRange();
        range.moveToElementText(el);
        range.select();
    }
}