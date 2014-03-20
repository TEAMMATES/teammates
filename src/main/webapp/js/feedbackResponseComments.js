var addCount = 0;

$(document).ready(function(){
	var addCommentHandler = function(e) {
		var submitButton = $(this);
		var formObject = $(this).parent();
		var addFormRow = $(this).parent().parent().parent();
		var formData = formObject.serialize();
		
		e.preventDefault();
		
		$.ajax({
			type : 'POST',
			url : 	submitButton.attr('href') + "?" + formData,
			beforeSend : function() {
				formObject.find("textarea").prop("disabled", true);
				submitButton.attr("class", "floatright");
				submitButton.html("<img src='/images/ajax-loader.gif'/>");
			},
			error : function() {
				formObject.find("textarea").prop("disabled", false);
				setFormErrorMessage(submitButton, "Failed to save comment. Please try again.");
			},
			success : function(data) {
				setTimeout(function(){
					if (!data.isError) {
						// Inject new comment row
						addFormRow.prev().before(generateNewCommentRow(data));
						var newCommentRow = addFormRow.prev().prev().prev();
						newCommentRow.next().find("form[class*='responseCommentEditForm'] > a").click(editCommentHandler);
						newCommentRow.find("form[class*='responseCommentDeleteForm'] > a").click(deleteCommentHandler);
						addCount++;
						
						// Reset add comment form
						formObject.find("textarea").prop("disabled", false);
						formObject.find("textarea").val("");
						submitButton.addClass("button");
						submitButton.text("Submit Comment");
						removeFormErrorMessage(submitButton);
						addFormRow.prev().show();
						addFormRow.hide();
					} else {
						formObject.find("textarea").prop("disabled", false);
						setFormErrorMessage(submitButton, data.errorMessage);
					}
				},500);
			}
		});
	};
	$("form[class*='responseCommentAddForm'] > a").click(addCommentHandler);
	
	var editCommentHandler = function(e) {
		var submitButton = $(this);
		var formObject = $(this).parent();
		var editFormRow = $(this).parent().parent().parent();
		var editedCommentRow = $(this).parent().parent().parent().prev();
		var formData = formObject.serialize();
		
		e.preventDefault();
		
		$.ajax({
			type : 'POST',
			url : 	submitButton.attr('href') + "?" + formData,
			beforeSend : function() {
				formObject.find("textarea").prop("disabled", true);
				submitButton.attr("class", "floatright");
				submitButton.html("<img src='/images/ajax-loader.gif'/>");
			},
			error : function() {
				formObject.find("textarea").prop("disabled", false);
				setFormErrorMessage(submitButton, "Failed to save changes. Please try again.");
			},
			success : function(data) {
				setTimeout(function(){
					if (!data.isError) {
						// Update editted comment
						editedCommentRow.find("[class='feedbackResponseCommentText']").text(data.comment.commentText.value);
						
						// Reset edit comment form
						formObject.find("textarea").prop("disabled", false);
						formObject.find("textarea").val(data.comment.commentText.value);
						submitButton.addClass("button");
						submitButton.text("Submit Comment");
						removeFormErrorMessage(submitButton);
						editFormRow.prev().show();
						editFormRow.hide();
					} else {
						formObject.find("textarea").prop("disabled", false);
						setFormErrorMessage(submitButton, data.errorMessage);
					}
				},500);
			}
		});
	};
	$("form[class*='responseCommentEditForm'] > a").click(editCommentHandler);
	
	var deleteCommentHandler = function(e) {
		var submitButton = $(this);
		var formObject = $(this).parent();
		var deletedCommentRow = $(this).parent().parent().parent();
		var formData = formObject.serialize();
		
		e.preventDefault();
		
		$.ajax({
			type : 'POST',
			url : 	submitButton.attr('href') + "?" + formData,
			beforeSend : function() {
				submitButton.html("<img src='/images/ajax-loader.gif'/>");
			},
			error : function() {
				if (submitButton.parent().parent().parent().next().is(':visible')) {
					submitButton.parent().parent().parent().next().find("#deleteErrorMessage").text("Failed to delete comment. Please try again.");
				} else {
					submitButton.parent().parent().parent().after("<tr><td colspan=\"5\"><span id=\"deleteErrorMessage\">Failed to delete comment. Please try again.</span></td></tr>");
					submitButton.parent().parent().parent().next().children().children().attr("class", "color_red floatright");
				}
				submitButton.html("<a href=\"/page/instructorFeedbackResponseCommentDelete\" class=\"color_red pad_right\">Delete</a>");
			},
			success : function(data) {
				setTimeout(function(){
					if (!data.isError) {
						deletedCommentRow.next().remove();
						deletedCommentRow.remove();
					} else {
						if (submitButton.parent().parent().parent().next().is(':visible')) {
							submitButton.parent().parent().parent().next().find("#deleteErrorMessage").text(data.errorMessage);
						} else {
							submitButton.parent().parent().parent().after("<tr><td colspan=\"5\"><span id=\"deleteErrorMessage\">" + data.errorMessage + "</span></td></tr>");
							submitButton.parent().parent().parent().next().children().children().attr("class", "color_red floatright");
						}
					}
				},500);
			}
		});
	};
	$("form[class*='responseCommentDeleteForm'] > a").click(deleteCommentHandler);
});

function generateNewCommentRow(data) {
	var newRow =
		// Comment Row
		"<tr id=\"responseCommentRow-" + addCount + "\">" 
			// Display Saved Comment
			+ "<td class=\"feedbackResponseCommentText\">" + data.comment.commentText.value + "</td>"
			+ "<td class=\"feedbackResponseCommentGiver\">" + data.comment.giverEmail + "</td>"
			+ "<td class=\"feedbackResponseCommentTime\">" + data.comment.createdAt + "</td>"
			+ "<td class=\"rightalign\"><a href=\"#\" class=\"color_blue\" onclick=\"showNewlyAddedResponseCommentEditForm(" + addCount + ")\">Edit</a></td>"
			
			// Delete Form
			+ "<td class=\"rightalign\"><form class=\"responseCommentDeleteForm\">"
			+ "<a href=\"/page/instructorFeedbackResponseCommentDelete\" class=\"color_red pad_right\">Delete</a>"
			+ "<input type=\"hidden\" name=\"" + FEEDBACK_RESPONSE_COMMENT_ID + "\" value=\"" + data.comment.feedbackResponseCommentId + "\">"
			+ "<input type=\"hidden\" name=\"" + COURSE_ID + "\" value=\"" + data.comment.courseId + "\">"
			+ "<input type=\"hidden\" name=\"" + FEEDBACK_SESSION_NAME + "\" value=\"" + data.comment.feedbackSessionName + "\">"
			+ "<input type=\"hidden\" name=\"" + USER_ID + "\" value=\"" + data.account.googleId + "\">"
			+ "</form></td>"
		+ "</tr>"
		
		// Edit Form
		+ "<tr id=\"responseCommentEditForm-" + addCount + "\" style=\"display: none;\">" 
			+ "<td colspan=\"5\"><form class=\"responseCommentEditForm\">"
			+ "<textarea rows=\"4\" name=\"" + FEEDBACK_RESPONSE_COMMENT_TEXT + "\">" + data.comment.commentText.value + "</textarea>"
			+ "<input type=\"hidden\" name=\"" + FEEDBACK_RESPONSE_COMMENT_ID + "\" value=\"" + data.comment.feedbackResponseCommentId + "\">"
			+ "<input type=\"hidden\" name=\"" + COURSE_ID + "\" value=\"" + data.comment.courseId + "\">"
			+ "<input type=\"hidden\" name=\"" + FEEDBACK_SESSION_NAME + "\" value=\"" + data.comment.feedbackSessionName + "\">"
			+ "<input type=\"hidden\" name=\"" + USER_ID + "\" value=\"" + data.account.googleId + "\">"
			+ "<a href=\"/page/instructorFeedbackResponseCommentEdit\" class=\"button floatright\">Save Changes</a>"
		+ "</form></td></tr>";
	return newRow;
}

function removeFormErrorMessage(submitButton) {
	if (submitButton.prev().attr("class") == "color_red") {
		submitButton.prev().remove();
	}
}

function setFormErrorMessage(submitButton, msg){
	if (submitButton.prev().attr("id") == "errorMessage") {
		submitButton.prev().text(msg);
	} else {
		submitButton.before("<span id=\"errorMessage\">" + msg + "</span>");
		submitButton.prev().addClass("color_red");
	}
	
	submitButton.addClass("button");
	submitButton.text("Submit Comment");
}

function showResponseCommentAddForm(recipientIndex, giverIndex, qnIndx) {
	var id = "-"+recipientIndex+"-"+giverIndex+"-"+qnIndx;

	$("#showResponseCommentAddFormButton"+id).hide();
	$("#responseCommentAddForm"+id).show();
	$("#responsecommenttext"+id).focus();
}

function showResponseCommentEditForm(recipientIndex, giverIndex, qnIndex, commentIndex) {
	var id = "-"+recipientIndex+"-"+giverIndex+"-"+qnIndex+"-"+commentIndex;
	
	$("#responseCommentRow"+id).hide();
	if ($("#responseCommentEditForm"+id).prev().is(':visible')) {
		$("#responseCommentEditForm"+id).prev().remove();
	}
	$("#responseCommentEditForm"+id).show();
}

function showNewlyAddedResponseCommentEditForm(addedIndex) {
	$("#responseCommentRow-"+addedIndex).hide();
	if ($("#responseCommentEditForm-"+addedIndex).prev().is(':visible')) {
		$("#responseCommentEditForm-"+addedIndex).prev().remove();
	}
	$("#responseCommentEditForm-"+addedIndex).show();
}