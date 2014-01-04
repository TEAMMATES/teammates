var COMMENT_TEXT = "commenttext";
var COMMENT_EDITTYPE = "commentedittype";
var DISPLAY_COMMENT_BLANK = "Please enter a valid comment. The comment can't be empty.";

/**
 * To be loaded when instructorStudentRecords page is loaded
 * Contains key bindings and text area adjustment
 */
function readyStudentRecordsPage(){
	
	initializenavbar();
	
	//Bind form submission to check for blank comment field
	$('form.form_comment').submit(function(){
		return checkComment(this);		
	});
	
	//Adjust size of each text area, except the new comment area
	$('textarea').each(function(){
		if(!$(this).attr("placeholder")){
			textAreaAdjust(this);
		}
	});
}

/**
 * Do the comment edit form submission
 * Currently done this way because the link is placed on a different column
 */
function submitCommentForm(commentIdx){
	$('#form_commentedit-'+commentIdx).submit();
	return false;
}

/**
 * Check the submitted comment text field of the form
 * Blanks are not allowed.
 */
function checkComment(form){
	var formTextField = $(form).find('[name='+COMMENT_TEXT+']').val();
	if (isBlank(formTextField)) {
		setStatusMessage(DISPLAY_COMMENT_BLANK,true);
		return false;
	}
}

function isBlank(str) {
    return (!str || /^\s*$/.test(str));
}

/**
 * Show the comment box and hide "Add Comment link"
 */
function showAddCommentBox(){
	$('#comment_box').show();
	$('#comment_link').hide();
}

/**
 * Enable the comment form indicated by index,
 * disables the others
 */
function enableEdit(commentIdx, maxComments){
	var i = 0;
	while (i < maxComments) {
		if (commentIdx == i) {
			enableComment(i);
		} else {
			disableComment(i);
		}
		i++;
	}
	
	return false;
}

function enableComment(commentIdx){
	$("textarea[id='commentText"+commentIdx+"']").removeAttr("disabled", "disabled");
	$('#'+'commentsave-'+commentIdx).show();
	$('#'+'commentedit-'+commentIdx).hide();
}

function disableComment(commentIdx){
	$("textarea[id='commentText"+commentIdx+"']").attr("disabled", "disabled");
	$('#'+'commentsave-'+commentIdx).hide();
	$('#'+'commentedit-'+commentIdx).show();
}

function textAreaAdjust(o) {
    o.style.height = "1px";
    o.style.height = (o.scrollHeight+5)+"px";
}

/**
 * Pops up confirmation dialog whether to delete specified comment
 * @param comment index
 * @returns
 */
function deleteComment(commentIdx){
	if (confirm("Are you sure you want to delete this comment?")){
		document.getElementById(COMMENT_EDITTYPE+'-'+commentIdx).value="delete";
		return submitCommentForm(commentIdx);
	} else {
		return false;
	}
}
