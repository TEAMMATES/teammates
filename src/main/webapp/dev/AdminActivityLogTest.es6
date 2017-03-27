/* global toggleReference:false getOlderLogEntriesByAjax:false convertLogTimestampToAdminTimezone:false */

QUnit.module('AdminActivityLog.js');

QUnit.test('toggleReference correctly changes display of query reference', (assert) => {
    const requiredElements = '<span id="referenceText"> Show Reference</span>'
                            + '<span class="glyphicon glyphicon-chevron-down" id="detailButton"></span>'
                            + '<div id="filterReference"> </div>';
    createRequiredElements(requiredElements);
    const $filterReferenceDiv = $('#filterReference');
    const $detailButton = $('#detailButton');
    const $referenceLink = $('#referenceText');
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

const $ajaxImplementation = $.ajax;

/**
 * Replaces $.ajax with a stub that executes the success callback synchronously with a simulated response.
 *
 * @param {Object} simulatedResponse to use with success callback
 */
function replaceAjaxWithStub(simulatedResponse) {
    $.ajax = function (opts) {
        const successCallback = opts.success;
        successCallback(simulatedResponse);
    };
}

function restoreAjaxImplementation() {
    $.ajax = $ajaxImplementation;
}

QUnit.test('getOlderLogEntriesByAjax correctly modifies page when receiving new log entries', (assert) => {
    const requiredElements = '<form id="ajaxLoaderDataForm"> <input type="hidden" name="searchTimeOffset" value=""> </form> '
        + '<table id="logsTable"> <tbody> </tbody> </table>'
        + '<button id="button_older">Search More</button>';
    createRequiredElements(requiredElements);
    const selector = '#logsTable tbody';
    const logContainer = $(selector);

    const responseWithNoLogs = {
        isError: false,
        logs: [],
    };
    replaceAjaxWithStub(responseWithNoLogs);
    const sampleTimestamp = 1489130289711;
    getOlderLogEntriesByAjax(sampleTimestamp);
    assert.equal(logContainer.children().length, 0, 'no entries should be added if ajax response contains no entries');

    const responseWithLogs = {
        isError: false,
        logs: [
            { logInfoAsHtml: '<tr><td>entry 1</td></tr>' },
            { logInfoAsHtml: '<tr><td>entry 2</td></tr>' },
        ],
    };
    replaceAjaxWithStub(responseWithLogs);
    getOlderLogEntriesByAjax(sampleTimestamp);
    assert.equal(logContainer.children().length, 2, 'Log entries should be added');
    assert.equal(logContainer.children().eq(0).html(), '<td>entry 1</td>');
    assert.equal(logContainer.children().eq(1).html(), '<td>entry 2</td>');

    restoreAjaxImplementation();
});

QUnit.test('convertLogTimestampToAdminTimezone', (assert) => {
    const requiredElements = '<table id="logsTable"><tbody><tr><td><a>10-03-2017 15:18:09</a>'
        + '<p class="localTime"></p></tr></tbody></table>';
    createRequiredElements(requiredElements);
    const response = {
        isError: false,
        logLocalTime: '10-03-2017 07:18:09',
    };
    replaceAjaxWithStub(response);
    const $logEntry = $('#logsTable tbody tr a');
    const $logTimestampCell = $logEntry.parent();
    const sampleTimestamp = 1489130289711;
    convertLogTimestampToAdminTimezone(sampleTimestamp, 'teammates.admin.id', 'Admin', $logEntry);

    assert.equal($logTimestampCell.html(), '10-03-2017 15:18:09<mark><br>10-03-2017 07:18:09</mark>',
            'Received timestamp correctly displayed');
    restoreAjaxImplementation();
});

/**
 * Creates DOM elements needed for testing under #qunit-fixture.
 *
 * @param {String} html the elements to be added
 */
function createRequiredElements(html) {
    const fixture = $('#qunit-fixture');
    fixture.append(html);
}
