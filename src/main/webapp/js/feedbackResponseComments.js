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
				setAddErrorMessage(submitButton, "Failed to save comment. Please try again.");
			},
			success : function(data) {
				setTimeout(function(){
					if (!data.isError) {
						// Display saved comment
						addFormRow.prev().before(
								"<tr><td class=\"feedbackResponseCommentText\">" + data.comment.commentText.value + "</td>"
								+ "<td class=\"feedbackResponseCommentGiver\">" + data.comment.giverEmail + "</td>"
								+ "<td class=\"feedbackResponseCommentTime\">" + data.comment.createdAt + "</td>"
								// TODO change to ajax edit/delete link
								+ "<td colspan=\"2\" style=\"font-style: italic;\">refresh to edit/delete</td></tr>"
						);
						
						// Reset add comment form
						formObject.find("textarea").prop("disabled", false);
						formObject.find("textarea").val("");
						submitButton.addClass("button");
						submitButton.text("Submit Comment");
						removeAddFormErrorMessage(submitButton);
						addFormRow.prev().show();
						addFormRow.hide();
					} else {
						formObject.find("textarea").prop("disabled", false);
						setAddErrorMessage(submitButton, data.errorMessage);
					}
				},500);
			}
		});
	};
	$("form[class*='responseCommentAddForm'] > a").click(addCommentHandler);
});

function removeAddFormErrorMessage(submitButton) {
	if (submitButton.prev().attr("class") == "color_red") {
		submitButton.prev().remove();
	}
}

function setAddErrorMessage(submitButton, msg){
	if (submitButton.prev().attr("class") == "color_red") {
		submitButton.prev().text(msg);
	} else {
		submitButton.before("<span>" + msg + "</span>");
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
	$("#responseCommentEditForm"+id).show();
}