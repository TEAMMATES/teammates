QUnit.module('datepicker.js');

QUnit.test('getMinDateForPublishDate(visibleDate)', function(assert) {
    assert.deepEqual(getMinDateForPublishDate(new Date('10/13/2014')), new Date('10/13/2014'), 
            'returns visibleDate(parameter) itself');
});

QUnit.test('getMaxDateForVisibleDate(startDate, publishDate)', function(assert) {
    assert.deepEqual(getMaxDateForVisibleDate(null, null), null,
            'When startDate and publishDate are null, returns null');
    assert.deepEqual(getMaxDateForVisibleDate(new Date('08/25/2014'), null), new Date('08/25/2014'),
            'When publishDate is null, returns startDate');
    assert.deepEqual(getMaxDateForVisibleDate(null, new Date('08/25/2014')), null,
            'When startDate is null, returns null');
    assert.deepEqual(getMaxDateForVisibleDate(new Date('08/25/2014'), undefined), new Date('08/25/2014'),
            'When publishDate is undefined, returns startDate');
    assert.deepEqual(getMaxDateForVisibleDate(new Date('10/13/2014'), new Date('08/25/2014')), new Date('08/25/2014'),
            'When startDate > publishDate, returns publishDate');
    assert.deepEqual(getMaxDateForVisibleDate(new Date('08/25/2014'), new Date('08/25/2014')), new Date('08/25/2014'),
            'When startDate = publishDate, returns startDate');
    assert.deepEqual(getMaxDateForVisibleDate(new Date('08/25/2014'), new Date('08/26/2014')), new Date('08/25/2014'),
            'When startDate < publishDate, returns startDate');
});
