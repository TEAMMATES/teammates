import {
    toggleExcludingSelfResultsForRubricStatistics,
} from '../common/instructorFeedbackResults';

QUnit.module('instructorFeedbackResults.js');

QUnit.test('toggleExcludingSelfResultsForRubricStatistics(checkbox)', (assert) => {
    // Tests that the toggling of checkbox for `excluding self option` in Rubric question works properly
    let isExcludingSelfTableVisible;
    let isIncludingSelfTableVisible;

    const className = '.excluding-self-check';

    $(className).prop('checked', false);
    toggleExcludingSelfResultsForRubricStatistics(className);
    isExcludingSelfTableVisible = $('.table-body-excluding-self').is(':visible');
    isIncludingSelfTableVisible = $('.table-body-including-self').is(':visible');

    assert.ok(isIncludingSelfTableVisible, 'Visible');
    assert.notOk(isExcludingSelfTableVisible, 'Should not be visible');

    $(className).prop('checked', true);
    toggleExcludingSelfResultsForRubricStatistics(className);
    isExcludingSelfTableVisible = $('.table-body-excluding-self').is(':visible');
    isIncludingSelfTableVisible = $('.table-body-including-self').is(':visible');

    assert.ok(isExcludingSelfTableVisible, 'Visible');
    assert.notOk(isIncludingSelfTableVisible, 'Should not be visible');
});
