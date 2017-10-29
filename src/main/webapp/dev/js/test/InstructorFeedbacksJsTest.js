import {
    extractQuestionNumFromEditFormId,
} from '../main/instructorFeedbackEdit';

QUnit.module('instructorFeedbacks.js');

QUnit.test('extractQuestionNumFromEditFormId(id)', (assert) => {
    // Tests that extracting question number from form is correct.
    for (let i = 1; i < 1000; i += 1) {
        const id = `form_editquestion-${i}`;
        assert.equal(extractQuestionNumFromEditFormId(id), i);
    }
});
