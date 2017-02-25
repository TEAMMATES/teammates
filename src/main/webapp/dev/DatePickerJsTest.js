QUnit.module('datepicker.js');

QUnit.test('getMinDateForEndDate(startDate)', function(assert) {
    assert.deepEqual(getMinDateForEndDate(new Date('02/25/2014')), new Date('02/25/2014'), 'Return startDate');
});

QUnit.test('getMaxDateForStartDate(endDate)', function(assert) {
    assert.deepEqual(getMaxDateForStartDate(new Date('02/04/2014')), new Date('02/04/2014'), 'Return endDate');
});

QUnit.test('getMinDateForPublishDate(visibleDate)', function(assert) {
    assert.deepEqual(getMinDateForPublishDate(new Date('10/13/2014')), new Date('10/13/2014'), 'Return visibleDate');
});
QUnit.test('getMaxDateForVisibleDate(startDate,publishDate)', function(assert) {
    assert.deepEqual(getMaxDateForVisibleDate(new Date('08/25/2014'), null), new Date('08/25/2014'),
            'When publishDate is null, Return minDate = startDate');
});

QUnit.test('getMaxDateForVisibleDate(startDate,publishDate)', function(assert) {
    assert.deepEqual(getMaxDateForVisibleDate(new Date('10/13/2014'), new Date('08/25/2014')), new Date('08/25/2014'),
            'When startDate > publishDate, Return minDate = publishDate');
});

QUnit.test('getMaxDateForVisibleDate(startDate,publishDate)', function(assert) {
    assert.deepEqual(getMaxDateForVisibleDate(new Date('08/25/2014'), new Date('06/08/2014')), new Date('08/25/2014'),
            'And in every other case, Return minDate = startDate');
});