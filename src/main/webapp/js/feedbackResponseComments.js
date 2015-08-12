function isInCommentsPage() {
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
    var commentId = responseCommentId.substring(21) + '-' + numberOfComments;
    
    e.preventDefault();
    
    $.ajax({
        type: 'POST',
        url: submitButton.attr('href') + "?" + formData + "&commentid=" + commentId,
        beforeSend: function() {
            formObject.find("textarea").prop("disabled", true);
            submitButton.html("<img src='/images/ajax-loader.gif'/>");
            submitButton.prop("disabled", true);
            cancelButton.prop("disabled", true);
        },
        error: function() {
            formObject.find("textarea").prop("disabled", false);
            submitButton.prop("disabled", false);
            cancelButton.prop("disabled", false);
            setFormErrorMessage(submitButton, "Failed to save comment. Please try again.");
            submitButton.text("Add");
        },
        success: function(data) {
            if (!data.isError) {
                if (isInCommentsPage()) {
                    panelHeading.click();
                } else {
                    // Inject new comment row
                    addFormRow.parent().attr("class", "list-group");
                    addFormRow.before(data);
                    removeUnwantedVisibilityOptions(commentId);
                    var newCommentRow = addFormRow.prev();
                    newCommentRow.find("form[class*='responseCommentEditForm'] > div > a[id*='button_save_comment_for_edit']").click(editCommentHandler);
                    newCommentRow.find("form[class*='responseCommentDeleteForm'] > a").click(deleteCommentHandler);
                    registerResponseCommentCheckboxEvent();

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
        type: 'POST',
        url: submitButton.attr('href') + "?" + formData,
        beforeSend: function() {
            formObject.find("textarea").prop("disabled", true);
            submitButton.html("<img src='/images/ajax-loader.gif'/>");
            submitButton.prop("disabled", true);
            cancelButton.prop("disabled", true);
        },
        error: function() {
            formObject.find("textarea").prop("disabled", false);
            setFormErrorMessage(submitButton, "Failed to save changes. Please try again.");
            submitButton.text("Save");
            submitButton.prop("disabled", false);
            cancelButton.prop("disabled", false);
        },
        success: function(data) {
            if (!data.isError) {
                if(isInCommentsPage()) {
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
        type: 'POST',
        url: submitButton.attr('href') + "?" + formData,
        beforeSend: function() {
            submitButton.html("<img src='/images/ajax-loader.gif'/>");
        },
        error: function() {
            if (editForm.is(':visible')) {
                setFormErrorMessage(editForm.find("div > a"), "Failed to delete comment. Please try again.");
            } else if (frCommentList.parent().find("div.delete_error_msg").length == 0) {
                frCommentList.after("<div class=\"delete_error_msg alert alert-danger\">Failed to delete comment. Please try again.</div>");
            }
            submitButton.html("<span class=\"glyphicon glyphicon-trash glyphicon-primary\"></span>");
        },
        success: function(data) {
            if (!data.isError) {
                if (isInCommentsPage()) {
                    panelHeading.click();
                } else {
                    var numberOfItemInFrCommentList = deletedCommentRow.parent().children('li');
                    if (numberOfItemInFrCommentList.length <= 2) {
                        deletedCommentRow.parent().hide();
                    }
                    if (frCommentList.find("li").length <= 1) {
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
        }
    });
};

function registerResponseCommentsEvent() {
    $("form[class*='responseCommentAddForm'] > div > a[id^='button_save_comment_for_add']").click(addCommentHandler);
    $("form[class*='responseCommentEditForm'] > div > a[id^='button_save_comment_for_edit']").click(editCommentHandler);
    $("form[class*='responseCommentDeleteForm'] > a[id^='commentdelete']").click(deleteCommentHandler);
    
    registerResponseCommentCheckboxEvent();
    
    $("div[id^=plainCommentText]").css("margin-left","15px");
}

function registerResponseCommentCheckboxEvent() {
    $("input[type=checkbox]").click(function(e) {
        var table = $(this).parent().parent().parent().parent();
        var form = table.parent().parent().parent();
        var visibilityOptions = [];
        var _target = $(e.target);
        
        if (_target.prop("class").includes("answerCheckbox") && !_target.prop("checked")) {
            _target.parent().parent().find("input[class*=giverCheckbox]").prop("checked", false);
            _target.parent().parent().find("input[class*=recipientCheckbox]").prop("checked", false);
        }
        if ((_target.prop("class").includes("giverCheckbox") || 
             _target.prop("class").includes("recipientCheckbox")) &&
             _target.prop("checked")) {
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

function enableHoverToDisplayEditOptions() {
    //show on hover for comment
    $('.comments > .list-group-item').hover(function() {
        $('div[id|="commentBar"] a[type="button"]', this).show();
    }, function() {
        $('div[id|="commentBar"] a[type="button"]', this).hide();
    });
}

$(document).ready(registerResponseCommentsEvent);

function removeUnwantedVisibilityOptions(commentId) {
    var addFormId = "showResponseCommentAddForm-" + commentId.split('-').splice(0, 3).join('-');
    var checkboxesInInAddForm = $('#' + addFormId).find('tr').find("input.visibilityCheckbox");
    var valuesOfCheckbox = [];
    for (var i = 0; i < checkboxesInInAddForm.length; i++) {
        valuesOfCheckbox.push($(checkboxesInInAddForm[i]).val());
    }
    if (valuesOfCheckbox.indexOf('GIVER') == -1) {
        $("#response-giver-" + commentId).remove();
    }
    if (valuesOfCheckbox.indexOf('RECEIVER') == -1) {
        $("#response-recipient-" + commentId).remove();
    }
    if (valuesOfCheckbox.indexOf('OWN_TEAM_MEMBERS') == -1) {
        $("#response-giver-team-" + commentId).remove();
    }
    if (valuesOfCheckbox.indexOf('RECEIVER_TEAM_MEMBERS') == -1) {
        $("#response-recipient-team-" + commentId).remove();
    }
    if (valuesOfCheckbox.indexOf('STUDENTS') == -1) {
        $("#response-students-" + commentId).remove();
    }
    if (valuesOfCheckbox.indexOf('INSTRUCTORS') == -1) {
        $("#response-instructors-" + commentId).remove();
    }
}

function removeFormErrorMessage(submitButton) {
    if (submitButton.next().next().attr("id") == "errorMessage") {
        submitButton.next().next().remove();
    }
}

function setFormErrorMessage(submitButton, msg) {
    if (submitButton.next().next().attr("id") == "errorMessage") {
        submitButton.next().next().text(msg);
    } else {
        submitButton.next().after("<span id=\"errorMessage\" class=\"pull-right \"> " + msg + "</span>");
    }
}

function showResponseCommentAddForm(recipientIndex, giverIndex, qnIndx) {
    var id = "-" + recipientIndex + "-" + giverIndex + "-" + qnIndx;
    $("#responseCommentTable" + id).show();
    if ($("#responseCommentTable" + id + " > li").length <= 1) {
        $("#responseCommentTable" + id).css('margin-top', '15px');
    }
    $("#showResponseCommentAddForm" + id).show();
    $("#responseCommentAddForm" + id).focus();
}

function hideResponseCommentAddForm(recipientIndex, giverIndex, qnIndx) {
    var id = "-" + recipientIndex + "-" + giverIndex + "-" + qnIndx;
    if ($("#responseCommentTable" + id + " > li").length <= 1) {
        $("#responseCommentTable" + id).css('margin-top', '0');
        $("#responseCommentTable" + id).hide();
    }
    $("#showResponseCommentAddForm" + id).hide();
    removeFormErrorMessage($("#button_save_comment_for_add" + id));
}

function showResponseCommentEditForm(recipientIndex, giverIndex, qnIndex, commentIndex) {
    var id;
    if (giverIndex || qnIndex || commentIndex) {
        id = "-" + recipientIndex + "-" + giverIndex + "-" + qnIndex + "-" + commentIndex;
    } else {
        id = "-" + recipientIndex;
    }
    var commentBar = $("#plainCommentText" + id).parent().find("#commentBar" + id);
    commentBar.hide();
    $("#plainCommentText" + id).hide();
    $("#responseCommentEditForm" + id + " > div > textarea").val($("#plainCommentText" + id).text());
    $("#responseCommentEditForm" + id).show();
    $("#responseCommentEditForm" + id + " > div > textarea").focus();
}

function toggleVisibilityEditForm(sessionIdx, questionIdx, responseIdx, commentIndex) {
    var id;
    if (questionIdx || responseIdx || commentIndex) {
        if (commentIndex) {
            id = "-" + sessionIdx + "-" + questionIdx + "-" + responseIdx + "-" + commentIndex;
        } else {
            id = "-" + sessionIdx + "-" + questionIdx + "-" + responseIdx;
        }
    } else {
        id = "-" + sessionIdx;
    }
    var visibilityEditForm = $("#visibility-options" + id);
    if (visibilityEditForm.is(':visible')) {
        visibilityEditForm.hide();
        $("#frComment-visibility-options-trigger" + id).html('<span class="glyphicon glyphicon-eye-close"></span> Show Visibility Options');
        
    } else {
        visibilityEditForm.show();
        $("#frComment-visibility-options-trigger" + id).html('<span class="glyphicon glyphicon-eye-close"></span> Hide Visibility Options');
    }
}

function hideResponseCommentEditForm(recipientIndex, giverIndex, qnIndex, commentIndex) {
    var id;
    if (giverIndex || qnIndex || commentIndex) {
        id = "-" + recipientIndex + "-" + giverIndex + "-" + qnIndex + "-" + commentIndex;
    } else {
        id = "-" + recipientIndex;
    }
    var commentBar = $("#plainCommentText" + id).parent().find("#commentBar" + id);
    commentBar.show();
    $("#plainCommentText" + id).show();
    $("#responseCommentEditForm" + id).hide();
    removeFormErrorMessage($("#button_save_comment_for_edit" + id));
}

function showNewlyAddedResponseCommentEditForm(addedIndex) {
    $("#responseCommentRow-" + addedIndex).hide();
    if ($("#responseCommentEditForm-" + addedIndex).prev().is(':visible')) {
        $("#responseCommentEditForm-" + addedIndex).prev().remove();
    }
    $("#responseCommentEditForm-" + addedIndex).show();
}

function loadFeedbackResponseComments(user, courseId, fsName, fsIndx, sender) {
    $(".tooltip").hide();
    var panelBody = $(sender).parent().find('div[class^="panel-body"]');
    var fsNameForUrl = encodeURIComponent(fsName);
    var url = "/page/instructorFeedbackResponseCommentsLoad?user=" + user + "&courseid=" + courseId + "&fsname=" + fsNameForUrl + "&fsindex=" + fsIndx;
    $(sender).find('div[class^="placeholder-img-loading"]').html("<img src='/images/ajax-loader.gif'/>");
    panelBody.load(url, function( response, status, xhr ) {
        if (status == "success") {
            panelBody.removeClass('hidden');
            updateBadgeForPendingComments(panelBody.children(":first").text());
            panelBody.children(":first").remove();
            registerResponseCommentsEvent();
            registerCheckboxEventForVisibilityOptions();
            enableHoverToDisplayEditOptions();
      } else {
          panelBody.find('div[class^="placeholder-error-msg"]').removeClass('hidden');
          panelBody.removeClass('hidden');
      }
      $(sender).find('div[class^="placeholder-img-loading"]').html("");
    });
}

function updateBadgeForPendingComments(numberOfPendingComments) {
    if (numberOfPendingComments == 0) {
        $('.badge').parent().parent().hide();
    } else {
        $('.badge').parent().parent().show();
    }
    $('.badge').text(numberOfPendingComments);
    $('.badge').parent().attr('data-original-title', 'Send email notification to ' + numberOfPendingComments + ' recipient(s) of comments pending notification');
}

function registerCheckboxEventForVisibilityOptions() {
    $("input[type=checkbox]").click(function(e) {
        var table = $(this).parent().parent().parent().parent();
        var form = table.parent().parent().parent();
        var visibilityOptions = [];
        var _target = $(e.target);
        
        if (_target.prop("class").includes("answerCheckbox") && !_target.prop("checked")) {
            _target.parent().parent().find("input[class*=giverCheckbox]").prop("checked", false);
            _target.parent().parent().find("input[class*=recipientCheckbox]").prop("checked", false);
        }
        if ((_target.prop("class").includes("giverCheckbox") || 
             _target.prop("class").includes("recipientCheckbox")) &&
             _target.prop("checked")) {
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