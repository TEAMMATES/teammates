import {
    toggleExcludingSelfResultsForRubricStatistics,
} from '../common/instructorFeedbackResults';

QUnit.module('instructorFeedbackResults.js');

QUnit.test('toggleExcludingSelfResultsForRubricStatistics(checkbox)', (assert) => {
    // Tests that the toggling of checkbox for `excluding self option` in Rubric question works properly
    const excludingSelfResponseCheckbox = '.excluding-self-response-checkbox';

    $(excludingSelfResponseCheckbox).prop('checked', true);
    toggleExcludingSelfResultsForRubricStatistics(excludingSelfResponseCheckbox);
    assert.ok($('.table-body-excluding-self').is(':visible'), 'table-body-excluding-self should be visible');
    assert.notOk($('.table-body-including-self').is(':visible'), 'table-body-including-self should not be visible');

    $(excludingSelfResponseCheckbox).prop('checked', false);
    toggleExcludingSelfResultsForRubricStatistics(excludingSelfResponseCheckbox);
    assert.ok($('.table-body-including-self').is(':visible'), 'table-body-including-self should be visible');
    assert.notOk($('.table-body-excluding-self').is(':visible'), 'table-body-excluding-self should not be visible');
});
