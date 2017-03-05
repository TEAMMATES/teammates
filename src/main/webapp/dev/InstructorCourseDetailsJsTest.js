QUnit.module('instructorCourseDetails.js');

QUnit.test('toggleSendRegistrationKey(courseID, email)', function(assert) {
    assert.expect(0);
});

QUnit.test('toggleSendRegistrationKeysConfirmation(courseID)', function(assert) {
    // gives a popup, can't be tested
    assert.expect(0);
});

QUnit.test('attachEventToSendInviteLink()', function(assert) {
    assert.expect(1);

    $('.course-student-remind-link').click();
    $('.bootbox .modal-btn-ok').click();

    var statusMessage = $('#statusMessagesToUser .alert-success').html();
    var studentEmail = $('.course-student-remind-link').parent().siblings('td[id|="studentemail"]').html().trim();

    assert.strictEqual(statusMessage, 'An email has been sent to ' + studentEmail, 'Mail sent to correct student');
});
