QUnit.module('datepicker.js');

QUnit.test('getMinDateForEndDate(startDate)', function(assert) {
    assert.strictEqual(getMinDateForEndDate(new Date('02/04/12')), new Date('02/04/12'),
                     'Return startDate input argument');
});

QUnit.test('getMaxDateForStartDate(endDate)', function(assert) {
    assert.strictEqual(getMaxDateForStartDate(new Date('02/04/12')), new Date('02/04/12'),
                     'Return endDate input argument');
});

QUnit.test('getMaxDateForVisibleDate(startDate, publishDate)', function(assert) {
    assert.strictEqual(getMaxDateForVisibleDate(new Date('02/04/12'), null), new Date('02/04/12'),
                     'NULL value test');
    assert.strictEqual(getMaxDateForVisibleDate(new Date('02/04/12'), undefined), new Date('02/04/12'),
                     'Undefined value test');
    assert.strictEqual(getMaxDateForVisibleDate(new Date('02/04/12'), new Date('01/04/12')),
                     newDate('01/04/12'), 'Test for startDate > publishDate');
    assert.strictEqual(getMaxDateForVisibleDate(new Date('01/04/12'), new Date('02/04/12')),
                     newDate('01/04/12'), 'Test for startDate < publishDate');
    assert.strictEqual(getMaxDateForVisibleDate(new Date('02/04/12'), new Date('02/04/12')),
                     newDate('02 Apr 2012, 23:59'), 'Test for startDate = publishDate');
});

QUnit.test('getMinDateForPublishDate(visibleDate)', function(assert) {
    assert.strictEqual(getMinDateForPublishDate(new Date('02/04/12')), new Date('02/04/12'),
                     'Return visibleDate input argument');
});
