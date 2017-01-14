QUnit.module('datepicker.js');


QUnit.test('getMinDateForEndDate(startDate)', function(assert) {
    assert.deepEqual(getMinDateForEndDate(newDate("02 Apr 2012, 23:59")), newDate("02 Apr 2012, 23:59"), 'Return startDate input argument');
});

QUnit.test('getMaxDateForStartDate(endDate)', function(assert) {
    assert.deepEqual(getMaxDateForStartDate(newDate("02 Apr 2012, 23:59")), newDate("02 Apr 2012, 23:59"), 'Return endDate input argument');
});

QUnit.test('getMaxDateForVisibleDate(startDate, publishDate)', function(assert) {
    assert.deepEqual(getMaxDateForVisibleDate(newDate("02 Apr 2012, 23:59"), null), newDate("02 Apr 2012, 23:59"), 'NULL value test');
    assert.deepEqual(getMaxDateForVisibleDate(newDate("02 Apr 2012, 23:59"), ""), newDate("02 Apr 2012, 23:59"), 'Undefined value test');
    assert.deepEqual(getMaxDateForVisibleDate(newDate("02 Apr 2012, 23:59"), newDate("02 Apr 2012, 23:58")), newDate("02 Apr 2012, 23:58"), 'Test for startDate > publishDate');
    assert.deepEqual(getMaxDateForVisibleDate(newDate("02 Apr 2012, 23:58"), newDate("02 Apr 2012, 23:59")), newDate("02 Apr 2012, 23:58"), 'Test for startDate < publishDate');
    assert.deepEqual(getMaxDateForVisibleDate(newDate("02 Apr 2012, 23:59"), newDate("02 Apr 2012, 23:59")), newDate("02 Apr 2012, 23:59"), 'Test for startDate = publishDate');
});

QUnit.test('getMinDateForPublishDate(visibleDate)', function(assert) {
    assert.deepEqual(getMinDateForPublishDate(newDate("02 Apr 2012, 23:59")), newDate("02 Apr 2012, 23:59"), 'Return visibleDate input argument');
});
