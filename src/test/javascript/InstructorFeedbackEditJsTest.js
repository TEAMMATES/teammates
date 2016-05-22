QUnit.module('instructorFeedbackEdit.js');

QUnit.test('getQuestionIdSuffix(questionNumber)', function(assert) {
    var max = 9007199254740991; // Number.MAX_SAFE_INTEGER
    var min = -max;

    var expected = [max, 1, 2, -1]; // -1 is used for new questions
    var unexpected = [0, -2, -3, min];

    // Tests the correct suffix is returned with a hyphen concatenated when necessary
    for (var i = 0; i < expected.length; i++) {
        assert.strictEqual(getQuestionIdSuffix(expected[i]), '-' + expected[i]);
    }
    for (var j = 0; j < unexpected.length; j++) {
        assert.strictEqual(getQuestionIdSuffix(unexpected[j]), '');
    }
});
