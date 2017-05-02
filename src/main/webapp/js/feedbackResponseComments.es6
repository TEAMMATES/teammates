/* global tinyMCE:false,
          destroyEditor:false,
          BootboxWrapper:false,
          richTextEditorBuilder:false,
          tinymce:false,
          toggleChevron:false,
          StatusType:false
 */

function isInCommentsPage() {
    return $(window.location).attr('href').indexOf('instructorCommentsPage') !== -1;
}

function removeFormErrorMessage(submitButton) {
    if (submitButton.next().next().attr('id') === 'errorMessage') {
        submitButton.next().next().remove();
    }
}

function setFormErrorMessage(submitButton, msg) {
    if (submitButton.next().next().attr('id') === 'errorMessage') {
        submitButton.next().next().text(msg);
    } else {
        submitButton.next().after(`<span id="errorMessage" class="pull-right "> ${msg}</span>`);
    }
}

/**
 * Clears the animation queue of the panel before collapsing/expanding the panel.
 */
function toggleCollapsiblePanel(collapsiblePanel) {
    // clearQueue to clear the animation queue to prevent animation build up
    collapsiblePanel.clearQueue();
    collapsiblePanel.collapse('toggle');
}

function updateBadgeForPendingComments(numberOfPendingComments) {
    if (numberOfPendingComments === 0) {
        $('.badge').closest('.btn-group').hide();
    } else {
        $('.badge').closest('.btn-group').show();
    }
    $('.badge').text(numberOfPendingComments);
    $('.badge').parent().attr('data-original-title', `Send email notification to ${numberOfPendingComments
                               } recipient(s) of comments pending notification`);
}

function loadFeedbackResponseComments(user, courseId, fsName, fsIndx, clickedElement, isClicked) {
    $('.tooltip').hide();
    const $clickedElement = $(clickedElement);
    const $collapsiblePanel = $clickedElement.siblings('.collapse');
    const panelBody = $clickedElement.parent().find('div[class^="panel-body"]');
    const fsNameForUrl = encodeURIComponent(fsName);
    const url = `/page/instructorFeedbackResponseCommentsLoad?user=${user
               }&courseid=${courseId}&fsname=${fsNameForUrl}&fsindex=${fsIndx}`;

    // If the content is already loaded, toggle the chevron and exit.
    if ($clickedElement.hasClass('loaded') && isClicked) {
        toggleCollapsiblePanel($collapsiblePanel);
        toggleChevron(clickedElement);

        return;
    }

    $clickedElement.find('div[class^="placeholder-img-loading"]').html("<img src='/images/ajax-loader.gif'/>");

    panelBody.load(url, (response, status) => {
        if (status === 'success') {
            updateBadgeForPendingComments(parseInt(panelBody.children(':first').text(), 10));
            panelBody.children(':first').remove();

            $clickedElement.addClass('loaded');
        } else {
            panelBody.find('div[class^="placeholder-error-msg"]').removeClass('hidden');
        }

        if (isClicked) {
            toggleCollapsiblePanel($collapsiblePanel);
            toggleChevron(clickedElement);
        }

        $clickedElement.find('div[class^="placeholder-img-loading"]').html('');
    });
}

/**
 * Reload feedback response comments.
 * @param formObject the form object where the action is triggered
 * @param panelHeading the heading of the feedback session panel
 */
function reloadFeedbackResponseComments(formObject, panelHeading) {
    const user = formObject.find("[name='user']").val();
    const courseId = formObject.find("[name='courseid']").val();
    const fsName = formObject.find("[name='fsname']").val();
    const fsIndx = formObject.find("[name='fsindex']").val();

    loadFeedbackResponseComments(user, courseId, fsName, fsIndx, panelHeading, false);
}

function removeUnwantedVisibilityOptions(commentId) {
    const commentIds = commentId.split('-');
    const addFormId = `showResponseCommentAddForm-${commentIds.splice(0, commentIds.length).join('-')}`;
    const checkboxesInInAddForm = $(`#${addFormId}`).find('tr').find('input.visibilityCheckbox');
    const valuesOfCheckbox = [];
    for (let i = 0; i < checkboxesInInAddForm.length; i += 1) {
        valuesOfCheckbox.push($(checkboxesInInAddForm[i]).val());
    }
    if (valuesOfCheckbox.indexOf('GIVER') === -1) {
        $(`#response-giver-${commentId}`).remove();
    }
    if (valuesOfCheckbox.indexOf('RECEIVER') === -1) {
        $(`#response-recipient-${commentId}`).remove();
    }
    if (valuesOfCheckbox.indexOf('OWN_TEAM_MEMBERS') === -1) {
        $(`#response-giver-team-${commentId}`).remove();
    }
    if (valuesOfCheckbox.indexOf('RECEIVER_TEAM_MEMBERS') === -1) {
        $(`#response-recipient-team-${commentId}`).remove();
    }
    if (valuesOfCheckbox.indexOf('STUDENTS') === -1) {
        $(`#response-students-${commentId}`).remove();
    }
    if (valuesOfCheckbox.indexOf('INSTRUCTORS') === -1) {
        $(`#response-instructors-${commentId}`).remove();
    }
}

function updateVisibilityOptionsForResponseComment(formObject, data) {
    formObject.find("input[class*='answerCheckbox'][value='GIVER']")
              .prop('checked', data.comment.showCommentTo.indexOf('GIVER') !== -1);
    formObject.find("input[class*='giverCheckbox'][value='GIVER']")
              .prop('checked', data.comment.showGiverNameTo.indexOf('GIVER') !== -1);
    formObject.find("input[class*='answerCheckbox'][value='RECEIVER']")
              .prop('checked', data.comment.showCommentTo.indexOf('RECEIVER') !== -1);
    formObject.find("input[class*='giverCheckbox'][value='RECEIVER']")
              .prop('checked', data.comment.showGiverNameTo.indexOf('RECEIVER') !== -1);
    formObject.find("input[class*='answerCheckbox'][value='OWN_TEAM_MEMBERS']")
              .prop('checked', data.comment.showCommentTo.indexOf('OWN_TEAM_MEMBERS') !== -1);
    formObject.find("input[class*='giverCheckbox'][value='OWN_TEAM_MEMBERS']")
              .prop('checked', data.comment.showGiverNameTo.indexOf('OWN_TEAM_MEMBERS') !== -1);
    formObject.find("input[class*='answerCheckbox'][value='RECEIVER_TEAM_MEMBERS']")
              .prop('checked', data.comment.showCommentTo.indexOf('RECEIVER_TEAM_MEMBERS') !== -1);
    formObject.find("input[class*='giverCheckbox'][value='RECEIVER_TEAM_MEMBERS']")
              .prop('checked', data.comment.showGiverNameTo.indexOf('RECEIVER_TEAM_MEMBERS') !== -1);
    formObject.find("input[class*='answerCheckbox'][value='STUDENTS']")
              .prop('checked', data.comment.showCommentTo.indexOf('STUDENTS') !== -1);
    formObject.find("input[class*='giverCheckbox'][value='STUDENTS']")
              .prop('checked', data.comment.showGiverNameTo.indexOf('STUDENTS') !== -1);
    formObject.find("input[class*='answerCheckbox'][value='INSTRUCTORS']")
              .prop('checked', data.comment.showCommentTo.indexOf('INSTRUCTORS') !== -1);
    formObject.find("input[class*='giverCheckbox'][value='INSTRUCTORS']")
              .prop('checked', data.comment.showGiverNameTo.indexOf('INSTRUCTORS') !== -1);
}

function deleteCommentRow(submitButton) {
    const deletedCommentRow = submitButton.closest('li');
    const frCommentList = submitButton.closest('.comments');

    const numberOfItemInFrCommentList = deletedCommentRow.parent().children('li');
    if (numberOfItemInFrCommentList.length <= 2) {
        deletedCommentRow.parent().hide();
    }
    if (frCommentList.find('li').length <= 1) {
        frCommentList.hide();
    }
    deletedCommentRow.remove();
    frCommentList.parent().find('div.delete_error_msg').remove();
}

function showErrorMessage(errorMessage, submitButton) {
    const editForm = submitButton.parent().next().next().next();
    const frCommentList = submitButton.closest('.comments');

    if (editForm.is(':visible')) {
        setFormErrorMessage(editForm.find('div > a'), errorMessage);
    } else if (frCommentList.parent().find('div.delete_error_msg').length === 0) {
        frCommentList.after(`<div class="delete_error_msg alert alert-danger">${errorMessage}</div>`);
    }
    submitButton.html('<span class="glyphicon glyphicon-trash glyphicon-primary"></span>');
}

const addCommentHandler = (e) => {
    const submitButton = $(e.currentTarget);
    const cancelButton = $(e.currentTarget).next("input[value='Cancel']");
    const formObject = $(e.currentTarget).closest('form');
    const addFormRow = formObject.closest("li[id^='showResponseCommentAddForm']");
    const panelHeading = $(e.currentTarget).parents("[id^='panel_display-']").find('.panel-heading').first();

    const responseCommentTableId = addFormRow.parent().attr('id');
    const responseCommentId = responseCommentTableId.substring('responseCommentTable-'.length);
    const numberOfComments = addFormRow.parent().find('li').length;
    const commentId = `${responseCommentId}-${numberOfComments}`;

    e.preventDefault();

    const editor = tinyMCE.get(`responseCommentAddForm-${responseCommentId}`);
    formObject.find('input[name=responsecommenttext]').val(editor.getContent());

    const formData = formObject.serialize();

    $.ajax({
        type: 'POST',
        url: `${submitButton.attr('href')}?${formData}&commentid=${commentId}`,
        beforeSend() {
            formObject.find('textarea').prop('disabled', true);
            submitButton.html("<img src='/images/ajax-loader.gif'/>");
            submitButton.prop('disabled', true);
            cancelButton.prop('disabled', true);
        },
        error() {
            formObject.find('textarea').prop('disabled', false);
            submitButton.prop('disabled', false);
            cancelButton.prop('disabled', false);
            setFormErrorMessage(submitButton, 'Failed to save comment. Please try again.');
            submitButton.text('Add');
        },
        success(data) {
            if (data.isError) {
                formObject.find('textarea').prop('disabled', false);
                setFormErrorMessage(submitButton, data.errorMessage);
                submitButton.text('Add');
                submitButton.prop('disabled', false);
                cancelButton.prop('disabled', false);
            } else if (isInCommentsPage()) {
                destroyEditor(`responseCommentAddForm-${responseCommentId}`);
                reloadFeedbackResponseComments(formObject, panelHeading);
            } else {
                // Inject new comment row
                addFormRow.parent().attr('class', 'list-group');
                addFormRow.before(data);
                removeUnwantedVisibilityOptions(commentId);

                // Reset add comment form
                formObject.find('textarea').prop('disabled', false);
                formObject.find('textarea').val('');
                submitButton.text('Add');
                submitButton.prop('disabled', false);
                cancelButton.prop('disabled', false);
                removeFormErrorMessage(submitButton);
                addFormRow.prev().show();
                addFormRow.hide();
                destroyEditor(`responseCommentAddForm-${responseCommentId}`);
            }
        },
    });
};

const editCommentHandler = (e) => {
    const submitButton = $(e.currentTarget);
    const cancelButton = $(e.currentTarget).next("input[value='Cancel']");
    const formObject = $(e.currentTarget).closest('form');
    const displayedText = formObject.siblings("div[id^='plainCommentText']").first();
    const commentBar = displayedText.parent().find('div[id^=commentBar]');
    const panelHeading = $(e.currentTarget).parents("[id^='panel_display-']").find('.panel-heading').first();

    e.preventDefault();

    const commentTextId = formObject.find('div[id^="responsecommenttext-"]').attr('id');
    formObject.find('input[name=responsecommenttext]').val(tinyMCE.get(commentTextId).getContent());

    const formData = formObject.serialize();

    $.ajax({
        type: 'POST',
        url: `${submitButton.attr('href')}?${formData}`,
        beforeSend() {
            formObject.find('textarea').prop('disabled', true);
            submitButton.html("<img src='/images/ajax-loader.gif'/>");
            submitButton.prop('disabled', true);
            cancelButton.prop('disabled', true);
        },
        error() {
            formObject.find('textarea').prop('disabled', false);
            setFormErrorMessage(submitButton, 'Failed to save changes. Please try again.');
            submitButton.text('Save');
            submitButton.prop('disabled', false);
            cancelButton.prop('disabled', false);
        },
        success(data) {
            if (data.isError) {
                formObject.find('textarea').prop('disabled', false);
                setFormErrorMessage(submitButton, data.errorMessage);
                submitButton.text('Save');
                submitButton.prop('disabled', false);
                cancelButton.prop('disabled', false);
            } else if (isInCommentsPage()) {
                destroyEditor(commentTextId);
                reloadFeedbackResponseComments(formObject, panelHeading);
            } else {
                // Update editted comment
                displayedText.html(data.comment.commentText.value);
                updateVisibilityOptionsForResponseComment(formObject, data);
                commentBar.show();

                // Reset edit comment form
                formObject.find('textarea').prop('disabled', false);
                formObject.find('textarea').val(data.comment.commentText.value);
                submitButton.text('Save');
                submitButton.prop('disabled', false);
                cancelButton.prop('disabled', false);
                removeFormErrorMessage(submitButton);
                formObject.hide();
                displayedText.show();
                destroyEditor(commentTextId);
            }
        },
    });
};

const deleteCommentHandler = (e) => {
    const submitButton = $(e.currentTarget);
    e.preventDefault();

    BootboxWrapper.showModalConfirmation('Confirm deletion', 'Are you sure you want to remove this comment?', () => {
        const formObject = submitButton.parent();
        const formData = formObject.serialize();
        const panelHeading = submitButton.parents("[id^='panel_display-']").find('.panel-heading').first();

        $.ajax({
            type: 'POST',
            url: `${submitButton.attr('href')}?${formData}`,
            beforeSend() {
                submitButton.html("<img src='/images/ajax-loader.gif'/>");
            },
            error() {
                showErrorMessage('Failed to delete comment. Please try again.', submitButton);
            },
            success(data) {
                if (data.isError) {
                    showErrorMessage(data.errorMessage, submitButton);
                } else if (isInCommentsPage()) {
                    reloadFeedbackResponseComments(formObject, panelHeading);
                } else {
                    deleteCommentRow(submitButton);
                }
            },
        });
    }, null, BootboxWrapper.DEFAULT_OK_TEXT, BootboxWrapper.DEFAULT_CANCEL_TEXT, StatusType.WARNING);
};

function registerResponseCommentsEvent() {
    $('body').on('click', 'form[class*="responseCommentAddForm"] > div > a[id^="button_save_comment_for_add"]',
                 addCommentHandler);
    $('body').on('click', 'form[class*="responseCommentEditForm"] > div > a[id^="button_save_comment_for_edit"]',
                 editCommentHandler);
    $('body').on('click', 'form[class*="responseCommentDeleteForm"] > a[id^="commentdelete"]', deleteCommentHandler);
}

function registerResponseCommentCheckboxEvent() {
    $('body').on('click', 'ul[id^="responseCommentTable"] * input[type=checkbox]', (e) => {
        const table = $(e.currentTarget).closest('table');
        const form = table.closest('form');
        let visibilityOptions = [];
        const target = $(e.target);
        const visibilityOptionsRow = target.closest('tr');

        if (target.prop('class').includes('answerCheckbox') && !target.prop('checked')) {
            visibilityOptionsRow.find('input[class*=giverCheckbox]').prop('checked', false);
            visibilityOptionsRow.find('input[class*=recipientCheckbox]').prop('checked', false);
        }
        if ((target.prop('class').includes('giverCheckbox') || target.prop('class').includes('recipientCheckbox'))
                && target.prop('checked')) {
            visibilityOptionsRow.find('input[class*=answerCheckbox]').prop('checked', true);
        }

        table.find('.answerCheckbox:checked').each(function () {
            visibilityOptions.push($(this).val());
        });
        form.find("input[name='showresponsecommentsto']").val(visibilityOptions.join(', '));

        visibilityOptions = [];
        table.find('.giverCheckbox:checked').each(function () {
            visibilityOptions.push($(this).val());
        });
        form.find("input[name='showresponsegiverto']").val(visibilityOptions.join(', '));
    });
}

function enableHoverToDisplayEditOptions() {
    // show on hover for comment
    $('body').on('mouseenter', '.comments > .list-group-item', function () {
        $('div[id|="commentBar"] a[type="button"]', this).show();
    });

    $('body').on('mouseleave', '.comments > .list-group-item', function () {
        $('div[id|="commentBar"] a[type="button"]', this).hide();
    });
}

function showResponseCommentAddForm(recipientIndex, giverIndex, qnIndx, opts) {
    let id;
    const isIncludeSection = opts && typeof opts.sectionIndex !== 'undefined';

    if (isIncludeSection) {
        id = `-${opts.sectionIndex}-${recipientIndex}-${giverIndex}-${qnIndx}`;
    } else {
        id = `-${recipientIndex}-${giverIndex}-${qnIndx}`;
    }

    $(`#responseCommentTable${id}`).show();
    if ($(`#responseCommentTable${id} > li`).length <= 1) {
        $(`#responseCommentTable${id}`).css('margin-top', '15px');
    }
    $(`#showResponseCommentAddForm${id}`).show();

    $(`#responseCommentAddForm${id}`).empty();

    if (typeof richTextEditorBuilder !== 'undefined') {
        /* eslint-disable camelcase */ // The property names are determined by external library (tinymce)
        richTextEditorBuilder.initEditor(`#responseCommentAddForm${id}`, {
            inline: true,
            fixed_toolbar_container: `#rich-text-toolbar-comment-container${id}`,
        });
        /* eslint-enable camelcase */
    }

    $(`#responseCommentAddForm${id}`).focus();
}

function hideResponseCommentAddForm(recipientIndex, giverIndex, qnIndx, opts) {
    let id;
    const isIncludeSection = opts && typeof opts.sectionIndex !== 'undefined';

    if (isIncludeSection) {
        id = `-${opts.sectionIndex}-${recipientIndex}-${giverIndex}-${qnIndx}`;
    } else {
        id = `-${recipientIndex}-${giverIndex}-${qnIndx}`;
    }

    if ($(`#responseCommentTable${id} > li`).length <= 1) {
        $(`#responseCommentTable${id}`).css('margin-top', '0');
        $(`#responseCommentTable${id}`).hide();
    }
    $(`#showResponseCommentAddForm${id}`).hide();
    removeFormErrorMessage($(`#button_save_comment_for_add${id}`));
}

function showResponseCommentEditForm(recipientIndex, giverIndex, qnIndex, commentIndex, opts) {
    let id;
    const isIncludeSection = opts && typeof opts.sectionIndex !== 'undefined';

    if (giverIndex || qnIndex || commentIndex) {
        if (isIncludeSection) {
            id = `-${opts.sectionIndex}-${recipientIndex}-${giverIndex}-${qnIndex}-${commentIndex}`;
        } else {
            id = `-${recipientIndex}-${giverIndex}-${qnIndex}-${commentIndex}`;
        }
    } else if (isIncludeSection) {
        id = `-${opts.sectionIndex}-${recipientIndex}`;
    } else {
        id = `-${recipientIndex}`;
    }

    const commentBar = $(`#plainCommentText${id}`).parent().find(`#commentBar${id}`);
    commentBar.hide();
    $(`#plainCommentText${id}`).hide();
    $(`#responseCommentEditForm${id} > div > textarea`).val($(`#plainCommentText${id}`).text());
    $(`#responseCommentEditForm${id}`).show();
    $(`#responseCommentEditForm${id} > div > textarea`).focus();

    if (typeof richTextEditorBuilder !== 'undefined') {
        if (tinymce.get(`responsecommenttext${id}`)) {
            return;
        }
        /* eslint-disable camelcase */ // The property names are determined by external library (tinymce)
        richTextEditorBuilder.initEditor(`#responsecommenttext${id}`, {
            inline: true,
            fixed_toolbar_container: `#rich-text-toolbar-comment-container${id}`,
        });
        /* eslint-enable camelcase */
    }
}

function toggleVisibilityAddForm(sessionIdx, questionIdx, responseIdx, opts) {
    let id;
    const isIncludeSection = opts && typeof opts.sectionIndex !== 'undefined';

    if (questionIdx || responseIdx) {
        if (isIncludeSection) {
            id = `-${opts.sectionIndex}-${sessionIdx}-${questionIdx}-${responseIdx}`;
        } else {
            id = `-${sessionIdx}-${questionIdx}-${responseIdx}`;
        }
    } else if (isIncludeSection) {
        id = `-${opts.sectionIndex}-${sessionIdx}`;
    } else {
        id = `-${sessionIdx}`;
    }

    const visibilityEditForm = $(`#visibility-options${id}`);
    if (visibilityEditForm.is(':visible')) {
        visibilityEditForm.hide();
        $(`#frComment-visibility-options-trigger${id}`)
            .html('<span class="glyphicon glyphicon-eye-close"></span> Show Visibility Options');
    } else {
        visibilityEditForm.show();
        $(`#frComment-visibility-options-trigger${id}`)
            .html('<span class="glyphicon glyphicon-eye-close"></span> Hide Visibility Options');
    }
}

function toggleVisibilityEditForm(sessionIdx, questionIdx, responseIdx, commentIndex, opts) {
    let id;
    const isIncludeSection = opts && typeof opts.sectionIndex !== 'undefined';

    if (questionIdx || responseIdx || commentIndex) {
        if (commentIndex) {
            if (isIncludeSection) {
                id = `-${opts.sectionIndex}-${sessionIdx}-${questionIdx}-${responseIdx}-${commentIndex}`;
            } else {
                id = `-${sessionIdx}-${questionIdx}-${responseIdx}-${commentIndex}`;
            }
        } else if (isIncludeSection) {
            id = `-${opts.sectionIndex}-${sessionIdx}-${questionIdx}-${responseIdx}`;
        } else {
            id = `-${sessionIdx}-${questionIdx}-${responseIdx}`;
        }
    } else if (isIncludeSection) {
        id = `-${opts.sectionIndex}-${sessionIdx}`;
    } else {
        id = `-${sessionIdx}`;
    }

    const visibilityEditForm = $(`#visibility-options${id}`);
    if (visibilityEditForm.is(':visible')) {
        visibilityEditForm.hide();
        $(`#frComment-visibility-options-trigger${id}`)
            .html('<span class="glyphicon glyphicon-eye-close"></span> Show Visibility Options');
    } else {
        visibilityEditForm.show();
        $(`#frComment-visibility-options-trigger${id}`)
            .html('<span class="glyphicon glyphicon-eye-close"></span> Hide Visibility Options');
    }
}

function hideResponseCommentEditForm(recipientIndex, giverIndex, qnIndex, commentIndex, opts) {
    let id;
    const isIncludeSection = opts && typeof opts.sectionIndex !== 'undefined';

    if (giverIndex || qnIndex || commentIndex) {
        if (isIncludeSection) {
            id = `-${opts.sectionIndex}-${recipientIndex}-${giverIndex}-${qnIndex}-${commentIndex}`;
        } else {
            id = `-${recipientIndex}-${giverIndex}-${qnIndex}-${commentIndex}`;
        }
    } else if (isIncludeSection) {
        id = `-${opts.sectionIndex}-${recipientIndex}`;
    } else {
        id = `-${recipientIndex}`;
    }

    const commentBar = $(`#plainCommentText${id}`).parent().find(`#commentBar${id}`);
    commentBar.show();
    $(`#plainCommentText${id}`).show();
    $(`#responseCommentEditForm${id}`).hide();
    removeFormErrorMessage($(`#button_save_comment_for_edit${id}`));
}

function showNewlyAddedResponseCommentEditForm(addedIndex) {
    $(`#responseCommentRow-${addedIndex}`).hide();
    if ($(`#responseCommentEditForm-${addedIndex}`).prev().is(':visible')) {
        $(`#responseCommentEditForm-${addedIndex}`).prev().remove();
    }
    $(`#responseCommentEditForm-${addedIndex}`).show();
}
