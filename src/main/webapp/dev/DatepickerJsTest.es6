/* global
triggerDatepickerOnClick:false, getMaxDateForVisibleDate:false
getMinDateForPublishDate:false
*/

QUnit.module('datepicker.js');

QUnit.test('triggerDatepickerOnClick(datepickerDivs)', (assert) => {
    assert.expect(2);

    $('#date-picker-div').datepicker();
    assert.equal($('.ui-datepicker-calendar:visible').length, 0, 'Datepicker is hidden initially');
    triggerDatepickerOnClick([$('#date-picker-div')]);
    $('#date-picker-div').click();

    assert.equal($('.ui-datepicker-calendar:visible').length, 1, 'Displays datepickers after trigger');
});

QUnit.test('getMaxDateForVisibleDate(startDate, publishDate)', (assert) => {
    assert.expect(5);

    const laterDate = new Date(2017, 3, 19, 2, 31, 0, 0);
    const earlierDate = new Date(2017, 3, 19, 2, 30, 0, 0);

    assert.equal(getMaxDateForVisibleDate(laterDate, null), laterDate,
            'Returns startDate when publishDate is null');
    assert.equal(getMaxDateForVisibleDate(laterDate, undefined), laterDate,
            'Returns startDate when publishDate is undefined');
    assert.equal(getMaxDateForVisibleDate(laterDate, earlierDate), earlierDate,
            'Returns publishDate when startDate > publishDate');
    assert.equal(getMaxDateForVisibleDate(laterDate, laterDate), laterDate,
            'Returns startDate when startDate = publishDate');
    assert.equal(getMaxDateForVisibleDate(earlierDate, laterDate), earlierDate,
            'Returns startDate when startDate < publishDate');
});

QUnit.test('getMinDateForPublishDate(visibleDate)', (assert) => {
    assert.expect(1);

    const visibleDate = new Date(2017, 3, 19, 2, 31, 0, 0);

    assert.equal(getMinDateForPublishDate(visibleDate), visibleDate, 'Returns visibleDate');
});
