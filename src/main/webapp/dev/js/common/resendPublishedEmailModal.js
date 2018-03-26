import {
    resendPublishedEmailToStudents,
} from './instructor';

function bindSelectAllStudentCheckboxEvent() {
    $('#resend-published-email-checkall').on('click', (event) => {
        const $studentTable = $('#studentEmailList');
        const $selectAllCheckbox = $(event.currentTarget);
        const $studentCheckboxes = $studentTable.find('input[type="checkbox"]').not($selectAllCheckbox);
        $studentCheckboxes.prop('checked', $selectAllCheckbox.is(':checked'));
    });
}

function prepareResendPublishedEmailModal() {
    $('#resendPublishedEmailModal').on('show.bs.modal', (event) => {
        const button = $(event.relatedTarget); // Button that triggered the modal
        const actionlink = button.data('actionlink');

        $.ajax({
            type: 'POST',
            cache: false,
            url: actionlink,
            beforeSend() {
                $('#studentEmailList').html('<img class="margin-center-horizontal" src="/images/ajax-loader.gif"/>');
                $('#resendPublishedEmailModal .resend-published-email-particular-button')
                        .prop('disabled', true).prop('value', 'Loading...');
            },
            error() {
                $('#studentEmailList').html('Error retrieving student list. Please close the dialog window and try again.');
            },
            success(data) {
                setTimeout(() => {
                    $('#studentEmailList').html(data);
                    $('#resendPublishedEmailModal .resend-published-email-particular-button')
                            .prop('disabled', false).prop('value', 'Send');
                    bindSelectAllStudentCheckboxEvent();
                }, 500);
            },
        });
    });
    $('#resendPublishedEmailModal .resend-published-email-particular-button').on('click', (event) => {
        const $emailButton = $(event.currentTarget);
        const $form = $emailButton.parents('form:first');
        const action = $form.attr('action');
        const formData = $form.serialize();
        const url = `${action}&${formData}`;
        resendPublishedEmailToStudents(url);
    });
}

export {
    prepareResendPublishedEmailModal,
};
