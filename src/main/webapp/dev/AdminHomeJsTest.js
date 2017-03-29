'use strict';

QUnit.module('AdminHome.js');

QUnit.assert.contains = function(context, toIdentify, message) {
    var actual = context.indexOf(toIdentify) > -1;
    this.pushResult({
        result: actual,
        actual: actual,
        expected: toIdentify,
        message: message
    });
};

QUnit.test('createRowForResultTable(shortName, name, email, institution, isSuccess, status)', function(assert) {
    var boolIndex = 4;
    var successClass = 'success';
    var failureClass = 'danger';
    function testCreateRowForResultTable(isSuccess) {
        var testProperties = ['test', 'testName', 'testMail', 'testInstitution', isSuccess, 'testStatus'];
        var result = createRowForResultTable.apply(null, testProperties);
        var expected = testProperties.slice();  // deep clone testProperties
        expected[boolIndex] = isSuccess ? successClass : failureClass;
        expected.forEach(function(property) {
            assert.contains(result, property, 'should contain ' + property);
        });
    }
    [true, false].forEach(testCreateRowForResultTable);
});

var addInstructorDetailsSingleLine = '<textarea id="addInstructorDetailsSingleLine"></textarea>';
var addInstructorResultTable = '<table id="addInstructorResultTable"><tbody></tbody></table>';

QUnit.module('addInstructorAjax', {
    afterEach: function() {
        paramsCounter = 0;
        paramsList = [];
        isInputFromFirstPanel = false;
    }
});
QUnit.test('test when paramsCounter >= paramsList.length', function(assert) {
    assert.expect(2);

    var implementation = enableAddInstructorForm;
    enableAddInstructorForm = function() {
        assert.ok(true, 'enableAddInstructorForm is called');
    };
    addInstructorAjax(true, null);
    enableAddInstructorForm = implementation;
    assert.equal(paramsCounter, 1, 'counter is incremented');
});
QUnit.test('test when paramsCounter < paramsList.length', function(assert) {
    assert.expect(1);

    paramsList = [0, 1];

    var implementation = addInstructorByAjaxRecursively;
    addInstructorByAjaxRecursively = function() {
        assert.ok(true, 'addInstructorByAjaxRecursively is called');
    };
    addInstructorAjax(true, null);
    addInstructorByAjaxRecursively = implementation;
});
QUnit.test('test addInstructorDetailsSingleLine data addition', function(assert) {
    var delimiter = '\n';
    var fixture = $('#qunit-fixture');
    fixture.append(addInstructorDetailsSingleLine);

    var testString = 'testString';
    paramsList = [0, 1];
    instructorDetailsList = [testString, testString];
    isInputFromFirstPanel = true;
    var implementation = addInstructorByAjaxRecursively;
    addInstructorByAjaxRecursively = function() {
        assert.ok(true, 'addInstructorByAjaxRecursively is called');
    };

    addInstructorAjax(true, null);

    assert.equal($('#addInstructorDetailsSingleLine').val(),
        testString + delimiter, 'data appended to addInstructorDetailsSingleLine');

    var data = {
        instructorShortName: 'testShortName',
        instructorName: 'testInstructorName',
        instructorEmail: 'testInstructorEmail',
        instructorInstitution: 'testInstructorInstitution',
        instructorAddingResultForAjax: false,
        statusForAjax: true
    };

    addInstructorAjax(false, data);
    var expected = instructorDetailsList.join(delimiter) + delimiter;
    assert.equal($('#addInstructorDetailsSingleLine').val(), expected, 'data is appended');

    data.instructorAddingResultForAjax = true;
    addInstructorAjax(false, data);
    assert.equal($('#addInstructorDetailsSingleLine').val(), expected, 'data is not appended');

    addInstructorByAjaxRecursively = implementation;
});

QUnit.module('addInstructor', {
    afterEach: function() {
        paramsCounter = 0;
        paramsList = [];
        instructorDetailsList = [];
        isInputFromFirstPanel = false;
    }
});
QUnit.test('addInstructorFromFirstFormByAjax', function(assert) {
    assert.expect(4);
    // initialize test data
    var delimiter = '\n';
    var testData = ['test', 'test1'];

    // initialize input dom
    var fixture = $('#qunit-fixture');
    fixture.append('<textarea id="addInstructorDetailsSingleLine" type="text">' + testData.join(delimiter) + '</textarea>');

    // test state changes and logic flow
    isInputFromFirstPanel = false;
    var implementation = addInstructorByAjaxRecursively;
    addInstructorByAjaxRecursively = function() {
        assert.ok(true, 'addInstructorByAjaxRecursively is called');
    };
    addInstructorFromFirstFormByAjax();
    addInstructorByAjaxRecursively = implementation;

    assert.ok(isInputFromFirstPanel, 'isInputFromFirstPanel is set to true');

    // test state data is correctly inserted
    assert.equal(instructorDetailsList.length, testData.length, 'data appended to instructorDetailsList');
    assert.equal(paramsList.length, testData.length, 'data appended to paramsList');
});
QUnit.test('addInstructorFromSecondFormByAjax', function(assert) {
    assert.expect(8);

    // initialize fields to be used
    var fields = ['instructorShortName', 'instructorName', 'instructorEmail', 'instructorInstitution'];
    var testValues = fields.map(function(field) {
        return field + 'test';
    });

    // initialize input dom
    var fixture = $('#qunit-fixture');
    var addInstructorFields = fields.map(function(field, i) {
        var testValue = testValues[i];
        return '<input id="' + field + '" type="text" value="' + testValue + '">';
    });
    fixture.append(addInstructorFields.join());

    // test state changes and logic flow
    isInputFromFirstPanel = true;
    var implementation = addInstructorByAjaxRecursively;
    addInstructorByAjaxRecursively = function() {
        assert.ok(true, 'addInstructorByAjaxRecursively is called');
    };
    addInstructorFromSecondFormByAjax();
    addInstructorByAjaxRecursively = implementation;

    assert.notOk(isInputFromFirstPanel, 'isInputFromFirstPanel is set to false');

    // test state data is correctly inserted
    assert.equal(instructorDetailsList.length, 1, 'data appended to instructorDetailsList');
    assert.equal(paramsList.length, 1, 'data appended to paramsList');
    var firstParams = paramsList[0];
    fields.forEach(function(field, i) {
        var fieldValue = testValues[i];
        assert.contains(firstParams, fieldValue, fieldValue + ' is inserted into paramsList');
    });
});
