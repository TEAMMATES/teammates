QUnit.module('datepicker.js');

QUnit.test('getMinDateForEndDate(startDate)', function(assert) {
    assert.deepEqual(getMinDateForEndDate(newDate('02/04/12')), newDate('02/04/12'),
                     'Return startDate input argument');
});

QUnit.test('getMaxDateForStartDate(endDate)', function(assert) {
    assert.deepEqual(getMaxDateForStartDate(newDate('02/04/12')), newDate('02/04/12'),
                     'Return endDate input argument');
});

QUnit.test('getMaxDateForVisibleDate(startDate, publishDate)', function(assert) {
    assert.deepEqual(getMaxDateForVisibleDate(newDate('02/04/12'), null), newDate('02/04/12'),
                     'NULL value test');
    assert.deepEqual(getMaxDateForVisibleDate(newDate('02/04/12'), undefined), newDate('02/04/12'),
                     'Undefined value test');
    assert.deepEqual(getMaxDateForVisibleDate(newDate('02/04/12'), newDate('01/04/12')),
                     newDate('01/04/12'), 'Test for startDate > publishDate');
    assert.deepEqual(getMaxDateForVisibleDate(newDate('01/04/12'), newDate('02/04/12')),
                     newDate('01/04/12'), 'Test for startDate < publishDate');
    assert.deepEqual(getMaxDateForVisibleDate(newDate('02/04/12'), newDate('02/04/12')),
                     newDate('02 Apr 2012, 23:59'), 'Test for startDate = publishDate');
});

QUnit.test('getMinDateForPublishDate(visibleDate)', function(assert) {
    assert.deepEqual(getMinDateForPublishDate(newDate('02/04/12')), newDate('02/04/12'),
                     'Return visibleDate input argument');
});
