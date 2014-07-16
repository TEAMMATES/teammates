var COMMENT_TEXT = "commenttext";
var COMMENT_EDITTYPE = "commentedittype";
var DISPLAY_COMMENT_BLANK = "Please enter a valid comment. The comment can't be empty.";

$(document).ready(function(){
	
	$("div[id^=plainCommentText]").css("margin-left","15px");
	
	$('a[id^="visibility-options-trigger"]').click(function(){
    	var visibilityOptions = $(this).parent().next();
		if(visibilityOptions.is(':visible')){
			visibilityOptions.hide();
			$(this).html('<span class="glyphicon glyphicon-eye-close"></span> Show Visibility Options');
		} else {
			visibilityOptions.show();
			$(this).html('<span class="glyphicon glyphicon-eye-close"></span> Hide Visibility Options');
		}
	});
    
    $("input[type=checkbox]").click(function(){
    	var table = $(this).parent().parent().parent().parent();
    	var form = table.parent().parent().parent();
    	var visibilityOptions = [];
    	table.find('.answerCheckbox:checked').each(function () {
			visibilityOptions.push($(this).val());
	    });
    	form.find("input[name='showcommentsto']").val(visibilityOptions.toString());
	    
	    visibilityOptions = [];
	    table.find('.giverCheckbox:checked').each(function () {
			visibilityOptions.push($(this).val());
	    });
	    form.find("input[name='showgiverto']").val(visibilityOptions.toString());
	    
	    visibilityOptions = [];
	    table.find('.recipientCheckbox:checked').each(function () {
			visibilityOptions.push($(this).val());
	    });
	    form.find("input[name='showrecipientto']").val(visibilityOptions.toString());
    });
});

/**
 * To be loaded when instructorStudentRecords page is loaded
 * Contains key bindings, text area adjustment and auto-opening
 * of comment box if the request parameter asks for it
 */
function readyStudentRecordsPage(){
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

    //Open the comment box if so desired by the request
    if (showCommentBox == "yes"){
        $("#button_add_comment").click();
    }
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
        $(window).scrollTop(0);
        return false;
    }
}

function isBlank(str) {
    return (!str || /^\s*$/.test(str));
}

/**
 * Show the comment box, focus comment text area and hide "Add Comment link"
 */
function showAddCommentBox(){
    $('#comment_box').show();
    $('#commentText').focus();
}

function hideAddCommentBox(){
    $('#comment_box').hide();
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
	$('#'+'commentBar'+commentIdx).hide();
	$('#'+'plainCommentText'+commentIdx).hide();
	$("div[id='commentTextEdit"+commentIdx+"']").show();
	$("textarea[id='commentText"+commentIdx+"']").val($("#plainCommentText"+commentIdx).text());
    $("textarea[id='commentText"+commentIdx+"']").focus();
}

function disableComment(commentIdx){
	$('#'+'commentBar'+commentIdx).show();
	$('#'+'plainCommentText'+commentIdx).show();
	$("div[id='commentTextEdit"+commentIdx+"']").hide();
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
