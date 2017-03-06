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

QUnit.test('updatePageWithNewLogsFromAjax(response, selector)', function(assert) {
    var fixture = $('#qunit-fixture');
    var requiredElements = '<table id="logsTable"> <tbody> </tbody> </table>';
    fixture.append(requiredElements);
    var selector = '#logsTable tbody';
    var logContainer = $(selector);

    var responseWithNoLogs = { logs: [] };
    updatePageWithNewLogsFromAjax(responseWithNoLogs, selector);
    assert.equal(logContainer.children().length, 0, 'no entries should be added if ajax response contains no entries');

    var responseWithLogs = { logs: [
                                    { logInfoAsHtml: '<tr><td>entry 1</td></tr>' },
                                    { logInfoAsHtml: '<tr><td>entry 2</td></tr>' }
    ] };
    updatePageWithNewLogsFromAjax(responseWithLogs, selector);
    assert.equal(logContainer.children().length, 2, 'Log entries should be added');
    assert.equal(logContainer.children().eq(0).html(), '<td>entry 1</td>');
    assert.equal(logContainer.children().eq(1).html(), '<td>entry 2</td>');
});
