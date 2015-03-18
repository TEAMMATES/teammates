function isInCommentsPage(){
	return $(location).attr('href').indexOf('instructorCommentsPage') != -1;
}

var addCommentHandler = function(e) {
    var submitButton = $(this);
    var cancelButton = $(this).next("input[value='Cancel']");
    var formObject = $(this).parent().parent();
    var addFormRow = $(this).parent().parent().parent();
    var panelHeading = $(this).parent().parent().parent().parent()
    	.parent().parent().parent().parent().parent().parent().prev();
    var formData = formObject.serialize();
    var responseCommentId = addFormRow.parent().attr('id');
    var numberOfComments = addFormRow.parent().find('li').length;
    
    e.preventDefault();
    
    $.ajax({
        type : 'POST',
        url :   submitButton.attr('href') + "?" + formData,
        beforeSend : function() {
            formObject.find("textarea").prop("disabled", true);
            submitButton.html("<img src='/images/ajax-loader.gif'/>");
            submitButton.prop("disabled", true);
            cancelButton.prop("disabled", true);
        },
        error : function() {
            formObject.find("textarea").prop("disabled", false);
            submitButton.prop("disabled", false);
            cancelButton.prop("disabled", false);
            setFormErrorMessage(submitButton, "Failed to save comment. Please try again.");
            submitButton.text("Add");
        },
        success : function(data) {
            setTimeout(function(){
                if (!data.isError) {
                    if(isInCommentsPage()){
                    	panelHeading.click();
                    } else {
	                    // Inject new comment row
	                    addFormRow.parent().attr("class", "list-group");
	                    addFormRow.before(generateNewCommentRow(data, responseCommentId, numberOfComments));
	                    var newCommentRow = addFormRow.prev();
	                    newCommentRow.find("form[class*='responseCommentEditForm'] > div > a[id*='button_save_comment_for_edit']").click(editCommentHandler);
	                    newCommentRow.find("form[class*='responseCommentDeleteForm'] > a").click(deleteCommentHandler);
	                    registerResponseCommentCheckboxEvent();
	                    newCommentRow.find("[data-toggle='tooltip']").tooltip({html: true});
	                    
	                    // Reset add comment form
	                    formObject.find("textarea").prop("disabled", false);
	                    formObject.find("textarea").val("");
	                    submitButton.text("Add");
	                    submitButton.prop("disabled", false);
	                    cancelButton.prop("disabled", false);
	                    removeFormErrorMessage(submitButton);
	                    addFormRow.prev().find("div[id^=plainCommentText]").css("margin-left","15px");
	                    addFormRow.prev().show();
	                    addFormRow.hide();
                    }
                } else {
                    formObject.find("textarea").prop("disabled", false);
                    setFormErrorMessage(submitButton, data.errorMessage);
                    submitButton.text("Add");
                    submitButton.prop("disabled", false);
                    cancelButton.prop("disabled", false);
                }
            },500);
        }
    });
};

var editCommentHandler = function(e) {
    var submitButton = $(this);
    var cancelButton = $(this).next("input[value='Cancel']");
    var formObject = $(this).parent().parent();
    var displayedText = $(this).parent().parent().prev();
    var commentBar = displayedText.parent().find("div[id^=commentBar]");
    var panelHeading = $(this).parent().parent().parent().parent()
		.parent().parent().parent().parent().parent().parent().prev();
    var formData = formObject.serialize();
    
    e.preventDefault();
    
    $.ajax({
        type : 'POST',
        url :   submitButton.attr('href') + "?" + formData,
        beforeSend : function() {
            formObject.find("textarea").prop("disabled", true);
            submitButton.html("<img src='/images/ajax-loader.gif'/>");
            submitButton.prop("disabled", true);
            cancelButton.prop("disabled", true);
        },
        error : function() {
            formObject.find("textarea").prop("disabled", false);
            setFormErrorMessage(submitButton, "Failed to save changes. Please try again.");
            submitButton.text("Save");
            submitButton.prop("disabled", false);
            cancelButton.prop("disabled", false);
        },
        success : function(data) {
            setTimeout(function(){
                if (!data.isError) {
                    if(isInCommentsPage()){
                        panelHeading.click();
                    } else {
	                    // Update editted comment
	                    displayedText.html(data.comment.commentText.value);
	                    updateVisibilityOptionsForResponseComment(formObject, data);
	                    commentBar.show();
	                    
	                    // Reset edit comment form
	                    formObject.find("textarea").prop("disabled", false);
	                    formObject.find("textarea").val(data.comment.commentText.value);
	                    submitButton.text("Save");
	                    submitButton.prop("disabled", false);
	                    cancelButton.prop("disabled", false);
	                    removeFormErrorMessage(submitButton);
	                    formObject.hide();
	                    displayedText.show();
                    }
                } else {
                    formObject.find("textarea").prop("disabled", false);
                    setFormErrorMessage(submitButton, data.errorMessage);
                    submitButton.text("Save");
                    submitButton.prop("disabled", false);
                    cancelButton.prop("disabled", false);
                }
            },500);
        }
    });
};

var deleteCommentHandler = function(e) {
    var submitButton = $(this);
    var formObject = $(this).parent();
    var deletedCommentRow = $(this).parent().parent().parent();
    var formData = formObject.serialize();
    var editForm = submitButton.parent().next().next().next();
    var frCommentList = submitButton.parent().parent().parent().parent();
    var panelHeading = $(this).parent().parent().parent().parent()
		.parent().parent().parent().parent().parent().parent().prev();
    
    e.preventDefault();
    
    $.ajax({
        type : 'POST',
        url :   submitButton.attr('href') + "?" + formData,
        beforeSend : function() {
            submitButton.html("<img src='/images/ajax-loader.gif'/>");
        },
        error : function() {
            if (editForm.is(':visible')) {
                setFormErrorMessage(editForm.find("div > a"), "Failed to delete comment. Please try again.");
            } else if (frCommentList.parent().find("div.delete_error_msg").length == 0) {
                frCommentList.after("<div class=\"delete_error_msg alert alert-danger\">Failed to delete comment. Please try again.</div>");
            }
            submitButton.html("<span class=\"glyphicon glyphicon-trash glyphicon-primary\"></span>");
        },
        success : function(data) {
            setTimeout(function(){
                if (!data.isError) {
                    if(isInCommentsPage()){
                        panelHeading.click();
                    } else {
	                    var numberOfItemInFrCommentList = deletedCommentRow.parent().children('li');
	                    if(numberOfItemInFrCommentList.length <= 2){
	                        deletedCommentRow.parent().hide();
	                    }
	                    if(frCommentList.find("li").length <= 1){
	                        frCommentList.hide();
	                    }
	                    deletedCommentRow.remove();
	                    frCommentList.parent().find("div.delete_error_msg").remove();
                    }
                } else {
                    if (editForm.is(':visible')) {
                        setFormErrorMessage(editForm.find("div > a"), data.errorMessage);
                    } else if (frCommentList.parent().find("div.delete_error_msg").length == 0) {
                        frCommentList.after("<div class=\"delete_error_msg alert alert-danger\">" + data.errorMessage + "</div>");
                    }
                    submitButton.html("<span class=\"glyphicon glyphicon-trash glyphicon-primary\"></span>");
                }
            },500);
        }
    });
};

function registerResponseCommentsEvent(){
    $("form[class*='responseCommentAddForm'] > div > a[id^='button_save_comment_for_add']").click(addCommentHandler);
    $("form[class*='responseCommentEditForm'] > div > a[id^='button_save_comment_for_edit']").click(editCommentHandler);
    $("form[class*='responseCommentDeleteForm'] > a[id^='commentdelete']").click(deleteCommentHandler);
    
    String.prototype.contains = function(substr) { return this.indexOf(substr) != -1; };
    
    registerResponseCommentCheckboxEvent();
    
    $("div[id^=plainCommentText]").css("margin-left","15px");
}

function registerResponseCommentCheckboxEvent(){
	$("input[type=checkbox]").click(function(e){
		var table = $(this).parent().parent().parent().parent();
		var form = table.parent().parent().parent();
		var visibilityOptions = [];
		var _target = $(e.target);
		
		if (_target.prop("class").contains("answerCheckbox") && !_target.prop("checked")) {
			_target.parent().parent().find("input[class*=giverCheckbox]").prop("checked", false);
			_target.parent().parent().find("input[class*=recipientCheckbox]").prop("checked", false);
		}
		if ((_target.prop("class").contains("giverCheckbox") || 
				_target.prop("class").contains("recipientCheckbox")) && _target.prop("checked")) {
			_target.parent().parent().find("input[class*=answerCheckbox]").prop("checked", true);
		}
		
		table.find('.answerCheckbox:checked').each(function () {
			visibilityOptions.push($(this).val());
	    });
		form.find("input[name='showresponsecommentsto']").val(visibilityOptions.toString());
	    
	    visibilityOptions = [];
	    table.find('.giverCheckbox:checked').each(function () {
			visibilityOptions.push($(this).val());
	    });
	    form.find("input[name='showresponsegiverto']").val(visibilityOptions.toString());
    });
}

function updateVisibilityOptionsForResponseComment(formObject, data) {
	formObject.find("input[class*='answerCheckbox'][value='GIVER']").prop("checked", (data.comment.showCommentTo.indexOf("GIVER") !== -1));
    formObject.find("input[class*='giverCheckbox'][value='GIVER']").prop("checked", (data.comment.showGiverNameTo.indexOf("GIVER") !== -1));
    formObject.find("input[class*='answerCheckbox'][value='RECEIVER']").prop("checked", (data.comment.showCommentTo.indexOf("RECEIVER") !== -1));
    formObject.find("input[class*='giverCheckbox'][value='RECEIVER']").prop("checked", (data.comment.showGiverNameTo.indexOf("RECEIVER") !== -1));
    formObject.find("input[class*='answerCheckbox'][value='OWN_TEAM_MEMBERS']").prop("checked", (data.comment.showCommentTo.indexOf("OWN_TEAM_MEMBERS") !== -1));
    formObject.find("input[class*='giverCheckbox'][value='OWN_TEAM_MEMBERS']").prop("checked", (data.comment.showGiverNameTo.indexOf("OWN_TEAM_MEMBERS") !== -1));
    formObject.find("input[class*='answerCheckbox'][value='RECEIVER_TEAM_MEMBERS']").prop("checked", (data.comment.showCommentTo.indexOf("RECEIVER_TEAM_MEMBERS") !== -1));
    formObject.find("input[class*='giverCheckbox'][value='RECEIVER_TEAM_MEMBERS']").prop("checked", (data.comment.showGiverNameTo.indexOf("RECEIVER_TEAM_MEMBERS") !== -1));
    formObject.find("input[class*='answerCheckbox'][value='STUDENTS']").prop("checked", (data.comment.showCommentTo.indexOf("STUDENTS") !== -1));
    formObject.find("input[class*='giverCheckbox'][value='STUDENTS']").prop("checked", (data.comment.showGiverNameTo.indexOf("STUDENTS") !== -1));
    formObject.find("input[class*='answerCheckbox'][value='INSTRUCTORS']").prop("checked", (data.comment.showCommentTo.indexOf("INSTRUCTORS") !== -1));
    formObject.find("input[class*='giverCheckbox'][value='INSTRUCTORS']").prop("checked", (data.comment.showGiverNameTo.indexOf("INSTRUCTORS") !== -1));
}

function enableHoverToDisplayEditOptions(){
	//show on hover for comment
	  $('.comments > .list-group-item').hover(
	     function(){
		  $("a[type='button']", this).show();
	  }, function(){
		  $("a[type='button']", this).hide();
	  });
}

function enableTooltip(){
	$(function() { 
	    $("[data-toggle='tooltip']").tooltip({html: true, container: 'body'}); 
	});
}

$(document).ready(registerResponseCommentsEvent);

function generateNewCommentRow(data, responseCommentId, numberOfComments) {
	var addedCommentId = responseCommentId.substring(21) + '-' + numberOfComments;
	var addedCommendFuncStr = addedCommentId.split('-').join(',');
	var commentDate = new Date(data.comment.createdAt);
	var commentDateStr = commentDate.toString();
	var thisYear = commentDate.getFullYear();
	var indexOfYear = commentDateStr.indexOf(thisYear, 0);
	var formattedDate = commentDateStr.substring(0, indexOfYear - 1);
	var formattedTime = commentDateStr.substring(indexOfYear + 5, indexOfYear + 14);
	var commentTime = formattedDate + " " + formattedTime + " UTC " + thisYear;
	
	var classNameForRow = isInCommentsPage()? "list-group-item list-group-item-warning giver_display-by-you":
			"list-group-item list-group-item-warning";
	
    var newRow =
    // Comment Row
	"<li class=\"" + classNameForRow + "\" id=\"responseCommentRow-" + addedCommentId + "\">"
	+ "<div id=\"commentBar-" + addedCommentId + "\">"
    + "<span class=\"text-muted\">From: <b>you</b> [" + commentTime + "]</span>"
	// Delete form
    + "<form class=\"responseCommentDeleteForm pull-right\">"
    + 		"<a href=\"/page/instructorFeedbackResponseCommentDelete\" type=\"button\" id=\"commentdelete-" + data.comment.feedbackResponseCommentId + "\" class=\"btn btn-default btn-xs icon-button\"" 
    +    		" data-toggle=\"tooltip\" data-placement=\"top\" title=\"Delete this comment\">" 
    +    		"<span class=\"glyphicon glyphicon-trash glyphicon-primary\"></span>"
    +    	"</a>"
    +   "<input type=\"hidden\" name=\"" + FEEDBACK_RESPONSE_ID + "\" value=\"" + data.comment.feedbackResponseId + "\">"
    + 	"<input type=\"hidden\" name=\"" + FEEDBACK_RESPONSE_COMMENT_ID + "\" value=\"" + data.comment.feedbackResponseCommentId + "\">"
    + 	"<input type=\"hidden\" name=\"" + COURSE_ID + "\" value=\"" + data.comment.courseId + "\">"
    + 	"<input type=\"hidden\" name=\"" + FEEDBACK_SESSION_NAME + "\" value=\"" + data.comment.feedbackSessionName + "\">"
    + 	"<input type=\"hidden\" name=\"" + USER_ID + "\" value=\"" + data.account.googleId + "\">"
    + "</form>"
    + "<a type=\"button\" id=\"commentedit-" + addedCommentId + "\" class=\"btn btn-default btn-xs icon-button pull-right\""
    + 		" onclick=\"showResponseCommentEditForm(" + addedCommendFuncStr + ")\""
    + 		" data-toggle=\"tooltip\" data-placement=\"top\" title=\"Edit this comment\">"
    + 	"<span class=\"glyphicon glyphicon-pencil glyphicon-primary\"></span>"
    + "</a>"
    + "</div>"
    // Display Saved Comment
    + "<div id=\"plainCommentText-" + addedCommentId + "\">" + data.comment.commentText.value + "</div>"
    // Edit form
    + "<form style=\"display:none;\" id=\"responseCommentEditForm-" + addedCommentId + "\" class=\"responseCommentEditForm\">"
	+    "<div class=\"form-group form-inline\">"
	+	    "<div class=\"form-group text-muted\">"
	+	        "You may change comment's visibility using the visibility options on the right hand side."
	+	    "</div>"
	+       "<a id=\"frComment-visibility-options-trigger-" + addedCommentId + "\" class=\"btn btn-sm btn-info pull-right\" onclick=\"toggleVisibilityEditForm(" + addedCommendFuncStr + ")\"><span class=\"glyphicon glyphicon-eye-close\"></span> Show Visibility Options</a>"
	+    "</div>"
	+    "<div id=\"visibility-options-" + addedCommentId + "\" class=\"panel panel-default\" style=\"display: none;\">"
    +       "<div class=\"panel-heading\">Visibility Options</div>"
    +       generateNewCommentVisibilityTable(data, addedCommentId)
    +   "</div>"
    + 	"<div class=\"form-group\">"
    + 		"<textarea class=\"form-control\" rows=\"3\" placeholder=\"Your comment about this response\""
    + 			" name=\"" + FEEDBACK_RESPONSE_COMMENT_TEXT + "\""
    + 			" id=\"" + FEEDBACK_RESPONSE_COMMENT_TEXT + "\"-" + addedCommentId + "\">" + data.comment.commentText.value + "</textarea>"
    +	 "</div>"
    + 	 "<div class=\"col-sm-offset-5\">"
    + 		"<a href=\"/page/instructorFeedbackResponseCommentEdit\" type=\"button\" class=\"btn btn-primary\" id=\"button_save_comment_for_edit-" + addedCommentId + "\">"
    + 			"Save"
    + 		"</a><span> </span>"
    +    	"<input type=\"button\" class=\"btn btn-default\" value=\"Cancel\" onclick=\"return hideResponseCommentEditForm(" + addedCommendFuncStr + ");\">"
    + 	 "</div>"
    +   "<input type=\"hidden\" name=\"" + FEEDBACK_RESPONSE_ID + "\" value=\"" + data.comment.feedbackResponseId + "\">"
    + 	 "<input type=\"hidden\" name=\"" + FEEDBACK_RESPONSE_COMMENT_ID + "\" value=\"" + data.comment.feedbackResponseCommentId + "\">"
    + 	 "<input type=\"hidden\" name=\"" + COURSE_ID + "\" value=\"" + data.comment.courseId + "\">"
    + 	 "<input type=\"hidden\" name=\"" + FEEDBACK_SESSION_NAME + "\" value=\"" + data.comment.feedbackSessionName + "\">"
    + 	 "<input type=\"hidden\" name=\"" + USER_ID + "\" value=\"" + data.account.googleId + "\">"
    +    "<input type=\"hidden\" name=\"showresponsecommentsto\" value=\"" + data.comment.showCommentTo.join(",") + "\">"
    +    "<input type=\"hidden\" name=\"showresponsegiverto\" value=\"" + data.comment.showGiverNameTo.join(",") + "\">"
    + "</form>"
    + "</li>";
    return newRow;
}

function generateNewCommentVisibilityTable(data, addedCommentId) {
	var tableStr = "<table class=\"table text-center\" style=\"color:#000;\">"
    +   "<tbody>"
    +       "<tr><th class=\"text-center\">User/Group</th><th class=\"text-center\">Can see your comment</th><th class=\"text-center\">Can see your name</th></tr>";
	var addFormId = "showResponseCommentAddForm-" + addedCommentId.split('-').splice(0, 3).join('-');
	var checkboxesInInAddForm = $('#' + addFormId).find('tr').find("input.visibilityCheckbox");
	var valuesOfCheckbox = [];
	for (var i = 0; i < checkboxesInInAddForm.length; i++) {
		valuesOfCheckbox.push($(checkboxesInInAddForm[i]).val());
	}
	if (valuesOfCheckbox.indexOf('GIVER') != -1) {
		tableStr += "<tr id=\"response-giver-" + addedCommentId + "\"><td class=\"text-left\"><div data-toggle=\"tooltip\" data-placement=\"top\" title=\"\" data-original-title=\"Control what response giver can view\">Response Giver</div></td>"
	    +   "<td><input class=\"visibilityCheckbox answerCheckbox centered\" name=\"receiverLeaderCheckbox\" type=\"checkbox\" value=\"GIVER\" " + ((data.comment.showCommentTo.indexOf("GIVER") === -1) ? "" : "checked=\"checked\"") + "></td>"
	    +   "<td><input class=\"visibilityCheckbox giverCheckbox\" type=\"checkbox\" value=\"GIVER\" " + ((data.comment.showGiverNameTo.indexOf("GIVER") === -1) ? "" : "checked=\"checked\"") + "></td></tr>";
	}
	if (valuesOfCheckbox.indexOf('RECEIVER') != -1) {
		tableStr += "<tr id=\"response-recipient-" + addedCommentId + "\"><td class=\"text-left\"><div data-toggle=\"tooltip\" data-placement=\"top\" title=\"\" data-original-title=\"Control what response recipient(s) can view\">Response Recipient(s)</div></td>"
	    +    "<td><input class=\"visibilityCheckbox answerCheckbox centered\" name=\"receiverLeaderCheckbox\" type=\"checkbox\" value=\"RECEIVER\" " + ((data.comment.showCommentTo.indexOf("RECEIVER") === -1) ? "" : "checked=\"checked\"") + "></td>"
	    +    "<td><input class=\"visibilityCheckbox giverCheckbox\" type=\"checkbox\" value=\"RECEIVER\" " + ((data.comment.showGiverNameTo.indexOf("RECEIVER") === -1) ? "" : "checked=\"checked\"") + "></td></tr>";
	}
	if (valuesOfCheckbox.indexOf('OWN_TEAM_MEMBERS') != -1) {
		tableStr += "<tr id=\"response-giver-team-" + addedCommentId + "\"><td class=\"text-left\"><div data-toggle=\"tooltip\" data-placement=\"top\" title=\"\" data-original-title=\"Control what team members of response giver can view\">Response Giver's Team Members</div></td>"
	    +    "<td><input class=\"visibilityCheckbox answerCheckbox\" type=\"checkbox\" value=\"OWN_TEAM_MEMBERS\" " + ((data.comment.showCommentTo.indexOf("OWN_TEAM_MEMBERS") === -1) ? "" : "checked=\"checked\"") + "></td>"
	    +    "<td><input class=\"visibilityCheckbox giverCheckbox\" type=\"checkbox\" value=\"OWN_TEAM_MEMBERS\" " + ((data.comment.showGiverNameTo.indexOf("OWN_TEAM_MEMBERS") === -1) ? "" : "checked=\"checked\"") + "></td></tr>";
	}
	if (valuesOfCheckbox.indexOf('RECEIVER_TEAM_MEMBERS') != -1) {
		tableStr += "<tr id=\"response-recipient-team-" + addedCommentId + "\"><td class=\"text-left\"><div data-toggle=\"tooltip\" data-placement=\"top\" title=\"\" data-original-title=\"Control what team members of response recipient(s) can view\">Response Recipient's Team Members</div></td>"
	    +    "<td><input class=\"visibilityCheckbox answerCheckbox\" type=\"checkbox\" value=\"RECEIVER_TEAM_MEMBERS\" " + ((data.comment.showCommentTo.indexOf("RECEIVER_TEAM_MEMBERS") === -1) ? "" : "checked=\"checked\"") + "></td>"
	    +    "<td><input class=\"visibilityCheckbox giverCheckbox\" type=\"checkbox\" value=\"RECEIVER_TEAM_MEMBERS\" " + ((data.comment.showGiverNameTo.indexOf("RECEIVER_TEAM_MEMBERS") === -1) ? "" : "checked=\"checked\"") + "></td></tr>";
	}
	if (valuesOfCheckbox.indexOf('STUDENTS') != -1) {
		tableStr += "<tr id=\"response-instructors-" + addedCommentId + "\"><td class=\"text-left\"><div data-toggle=\"tooltip\" data-placement=\"top\" title=\"\" data-original-title=\"Control what other students in this course can view\">Other students in this course</div></td>"
	    +    "<td><input class=\"visibilityCheckbox answerCheckbox\" type=\"checkbox\" value=\"STUDENTS\" " + ((data.comment.showCommentTo.indexOf("STUDENTS") === -1) ? "" : "checked=\"checked\"") + "></td>"
	    +    "<td><input class=\"visibilityCheckbox giverCheckbox\" type=\"checkbox\" value=\"STUDENTS\" " + ((data.comment.showGiverNameTo.indexOf("STUDENTS") === -1) ? "" : "checked=\"checked\"") + "></td></tr>";
	}
	if (valuesOfCheckbox.indexOf('INSTRUCTORS') != -1) {
		tableStr += "<tr id=\"response-instructors-" + addedCommentId + "\"><td class=\"text-left\"><div data-toggle=\"tooltip\" data-placement=\"top\" title=\"\" data-original-title=\"Control what instructors can view\">Instructors</div></td>"
	    +    "<td><input class=\"visibilityCheckbox answerCheckbox\" type=\"checkbox\" value=\"INSTRUCTORS\" " + ((data.comment.showCommentTo.indexOf("INSTRUCTORS") === -1) ? "" : "checked=\"checked\"") + "></td>"
	    +    "<td><input class=\"visibilityCheckbox giverCheckbox\" type=\"checkbox\" value=\"INSTRUCTORS\" " + ((data.comment.showGiverNameTo.indexOf("INSTRUCTORS") === -1) ? "" : "checked=\"checked\"") + "></td></tr>";
	}
	tableStr += "</tbody></table>";
	
	return tableStr;
}

function removeFormErrorMessage(submitButton) {
    if (submitButton.next().next().attr("id") == "errorMessage") {
        submitButton.next().next().remove();
    }
}

function setFormErrorMessage(submitButton, msg){
    if (submitButton.next().next().attr("id") == "errorMessage") {
        submitButton.next().next().text(msg);
    } else {
        submitButton.next().after("<span id=\"errorMessage\" class=\"pull-right \"> " + msg + "</span>");
    }
}

function showResponseCommentAddForm(recipientIndex, giverIndex, qnIndx) {
    var id = "-"+recipientIndex+"-"+giverIndex+"-"+qnIndx;
    $("#responseCommentTable"+id).show();
    if($("#responseCommentTable"+ id + " > li").length <= 1){
    	$("#responseCommentTable"+id).css('margin-top', '15px');
    }
    $("#showResponseCommentAddForm"+id).show();
    $("#responseCommentAddForm"+id).focus();
}

function hideResponseCommentAddForm(recipientIndex, giverIndex, qnIndx) {
    var id = "-"+recipientIndex+"-"+giverIndex+"-"+qnIndx;
    if($("#responseCommentTable"+ id + " > li").length <= 1){
    	$("#responseCommentTable"+id).css('margin-top', '0');
    	$("#responseCommentTable"+id).hide();
    }
    $("#showResponseCommentAddForm"+id).hide();
    removeFormErrorMessage($("#button_save_comment_for_add" + id));
}

function showResponseCommentEditForm(recipientIndex, giverIndex, qnIndex, commentIndex) {
	var id;
	if(giverIndex || qnIndex || commentIndex){
		id = "-"+recipientIndex+"-"+giverIndex+"-"+qnIndex+"-"+commentIndex;
	} else {
		id = "-"+recipientIndex;
	}
	var commentBar = $("#plainCommentText"+id).parent().find("#commentBar"+id);
	commentBar.hide();
    $("#plainCommentText"+id).hide();
    $("#responseCommentEditForm"+id+" > div > textarea").val($("#plainCommentText"+id).text());
    $("#responseCommentEditForm"+id).show();
    $("#responseCommentEditForm"+id+" > div > textarea").focus();
}

function toggleVisibilityEditForm(sessionIdx, questionIdx, responseIdx, commentIndex) {
	var id;
	if(questionIdx || responseIdx || commentIndex){
		if(commentIndex){
			id = "-"+sessionIdx+"-"+questionIdx+"-"+responseIdx+"-"+commentIndex;
		} else {
			id = "-"+sessionIdx+"-"+questionIdx+"-"+responseIdx;
		}
	} else {
		id = "-"+sessionIdx;
	}
	var visibilityEditForm = $("#visibility-options"+id);
	if(visibilityEditForm.is(':visible')){
		visibilityEditForm.hide();
		$("#frComment-visibility-options-trigger"+id).html('<span class="glyphicon glyphicon-eye-close"></span> Show Visibility Options');
		
	} else {
		visibilityEditForm.show();
		$("#frComment-visibility-options-trigger"+id).html('<span class="glyphicon glyphicon-eye-close"></span> Hide Visibility Options');
	}
}

function hideResponseCommentEditForm(recipientIndex, giverIndex, qnIndex, commentIndex) {
    var id;
    if(giverIndex || qnIndex || commentIndex){
    	id = "-"+recipientIndex+"-"+giverIndex+"-"+qnIndex+"-"+commentIndex;
    } else {
    	id = "-"+recipientIndex;
    }
    var commentBar = $("#plainCommentText"+id).parent().find("#commentBar"+id);
    commentBar.show();
    $("#plainCommentText"+id).show();
    $("#responseCommentEditForm"+id).hide();
    removeFormErrorMessage($("#button_save_comment_for_edit" + id));
}

function showNewlyAddedResponseCommentEditForm(addedIndex) {
    $("#responseCommentRow-"+addedIndex).hide();
    if ($("#responseCommentEditForm-"+addedIndex).prev().is(':visible')) {
        $("#responseCommentEditForm-"+addedIndex).prev().remove();
    }
    $("#responseCommentEditForm-"+addedIndex).show();
}

function loadFeedbackResponseComments(user, courseId, fsName, sender) {
	$(".tooltip").hide();
	var panelBody = $(sender).parent().find('div[class^="panel-body"]');
	var fsNameForUrl = fsName.split(' ').join('+');
	var url = "/page/instructorFeedbackResponseCommentsLoad?user=" + user + "&courseid=" + courseId + "&fsname=" + fsNameForUrl;
	$(sender).find('div[class^="placeholder-img-loading"]').html("<img src='/images/ajax-loader.gif'/>");
	panelBody.load(url, function( response, status, xhr ) {
	  if (status == "success") {
		  panelBody.removeClass('hidden');
		  updateBadgeForPendingComments(panelBody.children(":first").text());
		  panelBody.children(":first").remove();
		  registerResponseCommentsEvent();
		  registerCheckboxEventForVisibilityOptions();
		  enableHoverToDisplayEditOptions();
		  enableTooltip();
	  } else {
		  panelBody.find('div[class^="placeholder-error-msg"]').removeClass('hidden');
		  panelBody.removeClass('hidden');
	  }
	  $(sender).find('div[class^="placeholder-img-loading"]').html("");
	});
}

function updateBadgeForPendingComments(numberOfPendingComments){
	if(numberOfPendingComments == 0) {
		$('.badge').parent().parent().hide();
	} else {
		$('.badge').parent().parent().show();
	}
	$('.badge').text(numberOfPendingComments);
	$('.badge').parent().attr('data-original-title', 'Send email notification to ' + numberOfPendingComments + ' recipient(s) of comments pending notification');
}

function registerCheckboxEventForVisibilityOptions(){
	$("input[type=checkbox]").click(function(e){
    	var table = $(this).parent().parent().parent().parent();
    	var form = table.parent().parent().parent();
    	var visibilityOptions = [];
    	var _target = $(e.target);
    	
    	if (_target.prop("class").contains("answerCheckbox") && !_target.prop("checked")) {
    		_target.parent().parent().find("input[class*=giverCheckbox]").prop("checked", false);
    		_target.parent().parent().find("input[class*=recipientCheckbox]").prop("checked", false);
    	}
    	if ((_target.prop("class").contains("giverCheckbox") || 
    			_target.prop("class").contains("recipientCheckbox")) && _target.prop("checked")) {
    		_target.parent().parent().find("input[class*=answerCheckbox]").prop("checked", true);
    	}
    	
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
}