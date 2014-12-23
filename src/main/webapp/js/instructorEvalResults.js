$(document).ready(function(){
	toggleSort($("#button_sortteamname"),1);
	
	//auto select the html table when modal is shown
    $('#evalResultsHtmlWindow').on('shown.bs.modal', function (e) {
		selectElementContents( document.getElementById('summaryModalTable') );
    });
});


function submitFormAjax() {

	var formObject = $("#csvToHtmlForm");
	var formData = formObject.serialize();
	var content = $('#summaryModalTable');
	var ajaxStatus = $('#ajaxStatus');
	
	$.ajax({
        type : 'POST',
        url :   "/page/instructorEvalResultsPage?" + formData,
        beforeSend : function() {
        	content.html("<img src='/images/ajax-loader.gif'/>");
        },
        error : function() {
        	ajaxStatus.html("Failed to load results table. Please try again.");
            content.html("<button class=\"btn btn-info\" onclick=\"submitFormAjax()\"> retry</button>");     	
        },
        success : function(data) {
            setTimeout(function(){
                if (!data.isError) {
                	var table = data.summaryReportHtmlTableAsString;                	             	
                	content.html("<small>" + table + "</small>");
                } else {
                    ajaxStatus.html(data.errorMessage);
                    content.html("<button class=\"btn btn-info\" onclick=\"submitFormAjax()\"> retry</button>");   
                }
            	               
                $("#statusMessage").html(data.statusForAjax);

            },500);
        }
    });
}

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