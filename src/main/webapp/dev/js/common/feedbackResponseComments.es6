/* global tinymce:false */

import {
    showModalConfirmation,
} from './bootboxWrapper.es6';

import {
    StatusType,
} from './const.es6';

import {
    destroyEditor,
    richTextEditorBuilder,
} from './richTextEditor.es6';

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

    const responseCommentTableId = addFormRow.parent().attr('id');
    const responseCommentId = responseCommentTableId.substring('responseCommentTable-'.length);
    const numberOfComments = addFormRow.parent().find('li').length;
    const commentId = `${responseCommentId}-${numberOfComments}`;

    e.preventDefault();

    const editor = tinymce.get(`responseCommentAddForm-${responseCommentId}`);
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

    e.preventDefault();

    const commentTextId = formObject.find('div[id^="responsecommenttext-"]').attr('id');
    formObject.find('input[name=responsecommenttext]').val(tinymce.get(commentTextId).getContent());

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
            } else {
                // Update editted comment
                displayedText.html(data.comment.commentText.value);
                updateVisibilityOptionsForResponseComment(formObject, data);
                commentBar.find('span[class="text-muted"]').first().text(data.editedCommentDetails);
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

    showModalConfirmation('Confirm deletion', 'Are you sure you want to remove this comment?', () => {
        const formObject = submitButton.parent();
        const formData = formObject.serialize();

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
                } else {
                    deleteCommentRow(submitButton);
                }
            },
        });
    }, null, null, null, StatusType.WARNING);
};

function enableHoverToDisplayEditOptions() {
    // show on hover for comment
    $('body').on('mouseenter', '.comments > .list-group-item', function () {
        $('div[id|="commentBar"] a[type="button"]', this).show();
    });

    $('body').on('mouseleave', '.comments > .list-group-item', function () {
        $('div[id|="commentBar"] a[type="button"]', this).hide();
    });
}

function showResponseCommentAddForm(recipientIndex, giverIndex, qnIndex, sectionIndex) {
    const id = `${sectionIndex !== undefined ? `-${sectionIndex}` : ''}-${recipientIndex}-${giverIndex}-${qnIndex}`;

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
        });
        /* eslint-enable camelcase */
    }

    $(`#responseCommentAddForm${id}`).focus();
}

function hideResponseCommentAddForm(recipientIndex, giverIndex, qnIndex, sectionIndex) {
    const id = `${sectionIndex !== undefined ? `-${sectionIndex}` : ''}-${recipientIndex}-${giverIndex}-${qnIndex}`;

    if ($(`#responseCommentTable${id} > li`).length <= 1) {
        $(`#responseCommentTable${id}`).css('margin-top', '0');
        $(`#responseCommentTable${id}`).hide();
    }
    $(`#showResponseCommentAddForm${id}`).hide();
    removeFormErrorMessage($(`#button_save_comment_for_add${id}`));
}

function showResponseCommentEditForm(recipientIndex, giverIndex, qnIndex, commentIndex, sectionIndex, viewType) {
    let id;

    if (`${sectionIndex}` !== 'undefined') {
        id = `-${sectionIndex}-${recipientIndex}-${giverIndex}-${qnIndex}-${commentIndex}`;
    } else if (`${viewType}` !== 'undefined') {
        id = `-${viewType}-${recipientIndex}-${giverIndex}-${qnIndex}-${commentIndex}`;
    } else {
        id = `-${recipientIndex}-${giverIndex}-${qnIndex}-${commentIndex}`;
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
        });
        /* eslint-enable camelcase */
    }
}

function toggleVisibilityAddForm(recipientIndex, giverIndex, qnIndex, sectionIndex) {
    const id = `${sectionIndex !== undefined ? `-${sectionIndex}` : ''}-${recipientIndex}-${giverIndex}-${qnIndex}`;

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

function toggleVisibilityEditForm(recipientIndex, giverIndex, qnIndex, commentIndex, sectionIndex, viewType) {
    let id;

    if (`${sectionIndex}` !== 'undefined') {
        id = `-${sectionIndex}-${recipientIndex}-${giverIndex}-${qnIndex}-${commentIndex}`;
    } else if (`${viewType}` !== 'undefined') {
        id = `-${viewType}-${recipientIndex}-${giverIndex}-${qnIndex}-${commentIndex}`;
    } else {
        id = `-${recipientIndex}-${giverIndex}-${qnIndex}-${commentIndex}`;
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

function hideResponseCommentEditForm(recipientIndex, giverIndex, qnIndex, commentIndex, sectionIndex, viewType) {
    let id;

    if (`${sectionIndex}` !== 'undefined') {
        id = `-${sectionIndex}-${recipientIndex}-${giverIndex}-${qnIndex}-${commentIndex}`;
    } else if (`${viewType}` !== 'undefined') {
        id = `-${viewType}-${recipientIndex}-${giverIndex}-${qnIndex}-${commentIndex}`;
    } else {
        id = `-${recipientIndex}-${giverIndex}-${qnIndex}-${commentIndex}`;
    }

    const commentBar = $(`#plainCommentText${id}`).parent().find(`#commentBar${id}`);
    commentBar.show();
    $(`#plainCommentText${id}`).show();
    $(`#responseCommentEditForm${id}`).hide();
    removeFormErrorMessage($(`#button_save_comment_for_edit${id}`));
}

function registerResponseCommentsEvent() {
    $('body').on('click', 'form[class*="responseCommentAddForm"] > div > a[id^="button_save_comment_for_add"]',
                 addCommentHandler);
    $('body').on('click', 'form[class*="responseCommentEditForm"] > div > a[id^="button_save_comment_for_edit"]',
                 editCommentHandler);
    $('body').on('click', 'form[class*="responseCommentDeleteForm"] > a[id^="commentdelete"]', deleteCommentHandler);

    const clickHandlerMap = new Map();
    clickHandlerMap.set(
            '.show-frc-add-form', [showResponseCommentAddForm,
                    ['recipientindex', 'giverindex', 'qnindex', 'sectionindex']]);
    clickHandlerMap.set(
            '.show-frc-edit-form', [showResponseCommentEditForm,
                    ['recipientindex', 'giverindex', 'qnindex', 'frcindex', 'sectionindex', 'viewtype']]);
    clickHandlerMap.set(
            '.hide-frc-add-form', [hideResponseCommentAddForm,
                    ['recipientindex', 'giverindex', 'qnindex', 'sectionindex']]);
    clickHandlerMap.set(
            '.hide-frc-edit-form', [hideResponseCommentEditForm,
                    ['recipientindex', 'giverindex', 'qnindex', 'frcindex', 'sectionindex', 'viewtype']]);
    clickHandlerMap.set(
            '.toggle-visib-add-form', [toggleVisibilityAddForm,
                    ['recipientindex', 'giverindex', 'qnindex', 'sectionindex']]);
    clickHandlerMap.set(
            '.toggle-visib-edit-form', [toggleVisibilityEditForm,
                    ['recipientindex', 'giverindex', 'qnindex', 'frcindex', 'sectionindex', 'viewtype']]);

    /* eslint-disable no-restricted-syntax */
    for (const [className, clickHandlerAndParams] of clickHandlerMap) {
        $(document).on('click', className, (e) => {
            const ev = $(e.currentTarget);
            const clickHandler = clickHandlerAndParams[0];
            const params = clickHandlerAndParams[1].map(paramName => ev.data(paramName));
            clickHandler(params[0], params[1], params[2], params[3], params[4], params[5]);
        });
    }
    /* eslint-enable no-restricted-syntax */
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

export {
    enableHoverToDisplayEditOptions,
    registerResponseCommentCheckboxEvent,
    registerResponseCommentsEvent,
};
