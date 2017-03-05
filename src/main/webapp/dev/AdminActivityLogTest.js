'use strict';
QUnit.module('AdminActivityLog.js');

QUnit.test('toggleReference correctly changes display of query reference', function(assert) {
    var fixture = $('#qunit-fixture');
    var requiredElements = '<span id="referenceText"> Show Reference</span>'
                            + '<span class="glyphicon glyphicon-chevron-down" id="detailButton"></span>'
                            + '<div id="filterReference"> </div>';
    fixture.append(requiredElements);
    var $filterReferenceDiv = $('#filterReference');
    var $detailButton = $('#detailButton');
    var $referenceLink = $('#referenceText');
    $filterReferenceDiv.hide();

    // temporarily disable animations so that we don't have to wait till it finishes to check visibility
    $.fx.off = true;

    // show reference
    toggleReference();
    assert.ok($filterReferenceDiv.is(':visible'), 'filterReference div should be visible');
    assert.equal($detailButton.attr('class'), 'glyphicon glyphicon-chevron-up');
    assert.equal($referenceLink.text(), 'Hide Reference', 'Reference links text should change appropriately');

    // hide reference
    toggleReference();
    assert.notOk($filterReferenceDiv.is(':visible'), 'filterReference div should be hidden');
    assert.equal($detailButton.attr('class'), 'glyphicon glyphicon-chevron-down');
    assert.equal($referenceLink.text(), 'Show Reference', 'Reference links text should change appropriately');

    $.fx.off = false;
});
