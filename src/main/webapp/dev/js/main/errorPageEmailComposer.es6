import {
    destroyEditor,
    richTextEditorBuilder,
} from '../common/richTextEditor.es6';

import {
    clearStatusMessages,
    setStatusMessage,
    setStatusMessageToForm,
} from '../common/statusMessage.es6';

$(document).ready(() => {
    $('#error-feedback-form').on('submit', (event) => {
	    event.preventDefault();
    	const $form = $(event.target);
    	// Use Ajax to submit form data
        $.ajax({
            url: `/page/errorFeedbackSubmit`,
            type: 'POST',
            data: $form.serialize(),
            beforeSend() {
                clearStatusMessages();
            },
            success(result) {
                setStatusMessageToForm(result.statusMessagesToUser[0].text, result.statusMessagesToUser[0].color.toLowerCase(), $form);
                $form.children().not('#statusMessagesToUser').hide();
            },
        });
    });
});
