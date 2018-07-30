/* global tinymce:false */

import {
    showModalConfirmation,
} from './bootboxWrapper';

import {
    richTextEditorBuilder,
} from './richTextEditor';

import {
    BootstrapContextualColors as StatusType,
} from './const';

function deleteCommentRow(deleteButton) {
    const deletedCommentRow = deleteButton.closest('li');
    deletedCommentRow.closest('div').find('span.errorMessage').remove();
    deletedCommentRow.closest('div').hide();
    deletedCommentRow.remove();
}

function showErrorMessage(deleteButton, errorMessage) {
    deleteButton.closest('ul').after(`<span id="errorMessage" class="pull-right ">${errorMessage}</span>`);
    deleteButton.html('<span class="glyphicon glyphicon-trash glyphicon-primary"></span>');
}

function deleteCommentRowAndShowAddCommentButton(submitButton) {
    const commentId = `-${submitButton.data('qnindex')}-${submitButton.data('responseindex')}`;
    deleteCommentRow(submitButton);
    $(`#button_add_comment${commentId}`).closest('div').show();
}

const deleteCommentHandlerForFeedbackParticipant = (e) => {
    const deleteButton = $(e.currentTarget);
    e.preventDefault();

    showModalConfirmation('Confirm deletion', 'Are you sure you want to remove this comment?', () => {
        const divObject = deleteButton.parent();
        const formData = divObject.find('input').serialize();
        $.ajax({
            type: 'POST',
            url: `${deleteButton.attr('href')}?${formData}`,
            beforeSend() {
                deleteButton.html('<img src="/images/ajax-loader.gif"/>');
            },
            error() {
                showErrorMessage(deleteButton, 'Failed to delete comment. Please try again.');
            },
            success(data) {
                if (data.isError) {
                    showErrorMessage(deleteButton, data.errorMessage);
                } else {
                    deleteCommentRowAndShowAddCommentButton(deleteButton);
                }
            },
        });
    }, null, null, null, StatusType.WARNING);
};

function showResponseCommentAddFormForFeedbackParticipant(qnIndex, responseIndex) {
    const id = `-${qnIndex}-${responseIndex}`;

    $(`#button_add_comment${id}`).closest('div').hide();
    $(`#showResponseCommentAddForm${id}`).closest('div').show();
    $(`#showResponseCommentAddForm${id}`).show();

    const responseCommentAddFormId = `responseCommentAddForm${id}`;
    const responseCommentEditor = tinymce.get(responseCommentAddFormId);
    if (responseCommentEditor === null) {
        richTextEditorBuilder.initEditor(`#${responseCommentAddFormId}`, {
            inline: true,
        });
    } else {
        responseCommentEditor.setContent('');
    }
    $(`#responseCommentAddForm${id}`).focus();
}

function hideResponseCommentAddFormForFeedbackParticipant(qnIndex, responseIndex) {
    const id = `-${qnIndex}-${responseIndex}`;
    $(`#button_add_comment${id}`).closest('div').show();
    $(`#responseCommentTable${id}`).closest('div').hide();
}

function showResponseCommentEditFormForFeedbackParticipant(qnIndex, responseIndex) {
    const id = `-${qnIndex}-${responseIndex}`;
    const $commentBar = $(`#plainCommentText${id}`).parent().find(`#commentBar${id}`);
    $commentBar.hide();
    $(`#plainCommentText${id}`).hide();
    $(`#responseCommentEditForm${id} > div > textarea`).val($(`#plainCommentText${id}`).text());
    $(`#responseCommentEditForm${id}`).show();
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
    $(`#responseCommentEditForm${id} > div > textarea`).focus();
}

function hideResponseCommentEditFormForFeedbackParticipant(qnIndex, responseIndex) {
    const id = `-${qnIndex}-${responseIndex}`;
    const $commentBar = $(`#plainCommentText${id}`).parent().find(`#commentBar${id}`);
    $commentBar.show();
    $(`#plainCommentText${id}`).show();
    $(`#responseCommentEditForm${id}`).hide();
    tinymce.get(`responsecommenttext${id}`).setContent($(`#plainCommentText${id}`).text());
}

function registerResponseCommentsEventForFeedbackPage() {
    $('body').on('click',
            'div[class*="responseCommentDeleteForm"] > a[id^="commentdelete"]', deleteCommentHandlerForFeedbackParticipant);
    const clickHandlerMap = new Map();
    clickHandlerMap.set(
            '.show-frc-add-form', [showResponseCommentAddFormForFeedbackParticipant,
                ['qnindex', 'responseindex']]);
    clickHandlerMap.set(
            '.show-frc-edit-form', [showResponseCommentEditFormForFeedbackParticipant,
                ['qnindex', 'responseindex']]);
    clickHandlerMap.set(
            '.hide-frc-add-form', [hideResponseCommentAddFormForFeedbackParticipant,
                ['qnindex', 'responseindex']]);
    clickHandlerMap.set(
            '.hide-frc-edit-form', [hideResponseCommentEditFormForFeedbackParticipant,
                ['qnindex', 'responseindex']]);

    /* eslint-disable no-restricted-syntax */
    for (const [className, clickHandlerAndParams] of clickHandlerMap) {
        $(document).on('click', className, (e) => {
            const ev = $(e.currentTarget);
            const clickHandler = clickHandlerAndParams[0];
            const params = clickHandlerAndParams[1].map(paramName => ev.data(paramName));
            clickHandler(params[0], params[1]);
        });
    }
    /* eslint-enable no-restricted-syntax */
}

export {
    registerResponseCommentsEventForFeedbackPage,
};
