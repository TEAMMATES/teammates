import {
    toggleExcludingSelfResultsForRubricStatistics,
} from '../common/instructorFeedbackResults';

QUnit.module('instructorFeedbackResults.js');

QUnit.test('toggleExcludingSelfResultsForRubricStatistics(checkbox)', (assert) => {
    // Tests that the toggling of checkbox for `excluding self option` in Rubric question works properly
    const className = '.excluding-self-check';

    $(className).prop("checked", false);
    toggleExcludingSelfResultsForRubricStatistics(className);
    var isExcludingSelfTableVisible = $('.table-body-excluding-self').is(':visible');
    var isIncludingSelfTableVisible = $('.table-body-including-self').is(':visible');

    assert.equal(isExcludingSelfTableVisible, false, "Not visible");
    assert.equal(isIncludingSelfTableVisible, true, "Visible");

    $(className).prop("checked", true);
    toggleExcludingSelfResultsForRubricStatistics(className);
    isExcludingSelfTableVisible = $('.table-body-excluding-self').is(':visible');
    isIncludingSelfTableVisible = $('.table-body-including-self').is(':visible');

    assert.equal(isExcludingSelfTableVisible, true, "Visible");
    assert.equal(isIncludingSelfTableVisible, false, "Not visible");

});

