QUnit.module('datepicker.js');

QUnit.test('getMinDateForPublishDate(startDate)', function(assert) {
    assert.deepEqual(getMinDateForPublishDate(new Date('10/13/2014')), new Date('10/13/2014'),
            'returns startDate(parameter) itself');
});

QUnit.test('getMaxDateForVisibleDate(startDate)', function(assert) {
    assert.deepEqual(getMaxDateForVisibleDate(new Date('10/13/2014')), new Date('10/13/2014'),
            'returns startDate(parameter) itself');
});
