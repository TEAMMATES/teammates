import { sendRemindersToStudents } from './instructor.es6';

function bindRemindButton() {
    $('#remindModal .remind-particular-button').on('click', (event) => {
        const $remindButton = $(event.target);
        const $form = $remindButton.parents('form:first');
        const action = $form.attr('action');
        const formData = $form.serialize();
        const url = `${action}&${formData}`;
        sendRemindersToStudents(url);
    });
}

function prepareRemindModal() {
    $('#remindModal').on('show.bs.modal', (event) => {
        const button = $(event.relatedTarget); // Button that triggered the modal
        const actionlink = button.data('actionlink');

        $.ajax({
            type: 'POST',
            cache: false,
            url: actionlink,
            beforeSend() {
                $('#studentList').html('<img class="margin-center-horizontal" src="/images/ajax-loader.gif"/>');
                $('#remindModal .remind-particular-button').prop('disabled', true).prop('value', 'Loading...');
            },
            error() {
                $('#studentList').html('Error retrieving student list. Please close the dialog window and try again.');
            },
            success(data) {
                setTimeout(() => {
                    $('#studentList').html(data);
                    $('#remindModal .remind-particular-button').prop('disabled', false).prop('value', 'Remind');
                }, 500);
            },
        });
    });
    bindRemindButton();
}

export {
    prepareRemindModal,
};
