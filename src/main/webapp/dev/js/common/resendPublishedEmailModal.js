import {
    resendPublishedEmailsToStudents,
} from './instructor';

function bindSelectAllStudentCheckboxEvent() {
    $('#publish-email-checkall').on('click', (event) => {
        const $studentTable = $('#studentEmailList');
        const $selectAllCheckbox = $(event.currentTarget);
        const $studentCheckboxes = $studentTable.find('input[type="checkbox"]').not($selectAllCheckbox);
        $studentCheckboxes.prop('checked', $selectAllCheckbox.is(':checked'));
    });
}

function prepareResendPublishedEmailModal() {
    $('#publishEmailModal').on('show.bs.modal', (event) => {
        const button = $(event.relatedTarget); // Button that triggered the modal
        const actionlink = button.data('actionlink');

        $.ajax({
            type: 'POST',
            cache: false,
            url: actionlink,
            beforeSend() {
                $('#studentEmailList').html('<img class="margin-center-horizontal" src="/images/ajax-loader.gif"/>');
                $('#publishEmailModal .publish-email-particular-button').prop('disabled', true).prop('value', 'Loading...');
            },
            error() {
                $('#studentEmailList').html('Error retrieving student list. Please close the dialog window and try again.');
            },
            success(data) {
                setTimeout(() => {
                    $('#studentEmailList').html(data);
                    $('#publishEmailModal .publish-email-particular-button').prop('disabled', false).prop('value', 'Send');
                    bindSelectAllStudentCheckboxEvent();
                }, 500);
            },
        });
    });
    $('#publishEmailModal .publish-email-particular-button').on('click', (event) => {
        const $emailButton = $(event.currentTarget);
        const $form = $emailButton.parents('form:first');
        const action = $form.attr('action');
        const formData = $form.serialize();
        const url = `${action}&${formData}`;
        resendPublishedEmailsToStudents(url);
    });
}

export {
    prepareResendPublishedEmailModal,
};
