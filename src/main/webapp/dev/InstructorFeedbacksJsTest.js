'use strict';

/* global extractQuestionNumFromEditFormId:false */

QUnit.module('instructorFeedbacks.js');

QUnit.test('extractQuestionNumFromEditFormId(id)', function (assert) {
    // Tests that extracting question number from form is correct.
    for (var i = 1; i < 1000; i += 1) {
        var id = 'form_editquestion-' + i;
        assert.equal(extractQuestionNumFromEditFormId(id), i);
    }
});