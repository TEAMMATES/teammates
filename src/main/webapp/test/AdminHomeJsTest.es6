/* global
addInstructorAjax:false, enableAddInstructorForm:true, createRowForResultTable:false
addInstructorByAjaxRecursively:true, addInstructorFromFirstFormByAjax:false, addInstructorFromSecondFormByAjax:false
*/

QUnit.module('AdminHome.js');

QUnit.assert.contains = function (context, toIdentify, message) {
    const actual = context.indexOf(toIdentify) > -1;
    this.pushResult({
        result: actual,
        actual,
        expected: toIdentify,
        message,
    });
};

QUnit.test('createRowForResultTable(shortName, name, email, institution, isSuccess, status)', (assert) => {
    const boolIndex = 4;
    const successClass = 'success';
    const failureClass = 'danger';
    function testCreateRowForResultTable(isSuccess) {
        const testProperties = ['test', 'testName', 'testMail', 'testInstitution', isSuccess, 'testStatus'];
        const result = createRowForResultTable(...testProperties);
        const expected = testProperties.slice();  // deep clone testProperties
        expected[boolIndex] = isSuccess ? successClass : failureClass;
        expected.forEach((property) => {
            assert.contains(result, property, `should contain ${property}`);
        });
    }
    [true, false].forEach(testCreateRowForResultTable);
});

const addInstructorDetailsSingleLine = '<textarea id="addInstructorDetailsSingleLine"></textarea>';

let paramsCounter;
let paramsList;
let isInputFromFirstPanel;
let instructorDetailsList;

QUnit.module('addInstructorAjax', {
    beforeEach() {
        paramsCounter = 0;
        paramsList = [];
        isInputFromFirstPanel = false;
    },
});

QUnit.test('test when paramsCounter >= paramsList.length', (assert) => {
    assert.expect(2);

    const implementation = enableAddInstructorForm;
    enableAddInstructorForm = function () {
        assert.ok(true, 'enableAddInstructorForm is called');
    };
    addInstructorAjax(true, null);
    enableAddInstructorForm = implementation;
    assert.equal(paramsCounter, 1, 'counter is incremented');
});
QUnit.test('test when paramsCounter < paramsList.length', (assert) => {
    assert.expect(1);

    paramsList = [0, 1];

    const implementation = addInstructorByAjaxRecursively;
    addInstructorByAjaxRecursively = function () {
        assert.ok(true, 'addInstructorByAjaxRecursively is called');
    };
    addInstructorAjax(true, null);
    addInstructorByAjaxRecursively = implementation;
});
QUnit.test('test addInstructorDetailsSingleLine data addition', (assert) => {
    const delimiter = '\n';
    const fixture = $('#qunit-fixture');
    fixture.append(addInstructorDetailsSingleLine);

    const testString = 'testString';
    paramsList = [0, 1];
    instructorDetailsList = [testString, testString];
    isInputFromFirstPanel = true;
    const implementation = addInstructorByAjaxRecursively;
    addInstructorByAjaxRecursively = function () {
        assert.ok(true, 'addInstructorByAjaxRecursively is called');
    };

    addInstructorAjax(true, null);

    assert.equal($('#addInstructorDetailsSingleLine').val(),
        testString + delimiter, 'data appended to addInstructorDetailsSingleLine');

    const data = {
        instructorShortName: 'testShortName',
        instructorName: 'testInstructorName',
        instructorEmail: 'testInstructorEmail',
        instructorInstitution: 'testInstructorInstitution',
        instructorAddingResultForAjax: false,
        statusForAjax: true,
    };

    addInstructorAjax(false, data);
    const expected = instructorDetailsList.join(delimiter) + delimiter;
    assert.equal($('#addInstructorDetailsSingleLine').val(), expected, 'data is appended');

    data.instructorAddingResultForAjax = true;
    addInstructorAjax(false, data);
    assert.equal($('#addInstructorDetailsSingleLine').val(), expected, 'data is not appended');

    addInstructorByAjaxRecursively = implementation;
});

QUnit.module('addInstructor', {
    afterEach() {
        paramsCounter = 0;
        paramsList = [];
        instructorDetailsList = [];
        isInputFromFirstPanel = false;
    },
});
QUnit.test('addInstructorFromFirstFormByAjax', (assert) => {
    assert.expect(4);
    // initialize test data
    const delimiter = '\n';
    const testData = ['test', 'test1'];

    // initialize input dom
    const fixture = $('#qunit-fixture');
    fixture.append(`<textarea id="addInstructorDetailsSingleLine" type="text">${testData.join(delimiter)}</textarea>`);

    // test state changes and logic flow
    isInputFromFirstPanel = false;
    const implementation = addInstructorByAjaxRecursively;
    addInstructorByAjaxRecursively = function () {
        assert.ok(true, 'addInstructorByAjaxRecursively is called');
    };
    addInstructorFromFirstFormByAjax();
    addInstructorByAjaxRecursively = implementation;

    assert.ok(isInputFromFirstPanel, 'isInputFromFirstPanel is set to true');

    // test state data is correctly inserted
    assert.equal(instructorDetailsList.length, testData.length, 'data appended to instructorDetailsList');
    assert.equal(paramsList.length, testData.length, 'data appended to paramsList');
});
QUnit.test('addInstructorFromSecondFormByAjax', (assert) => {
    assert.expect(8);

    // initialize fields to be used
    const fields = ['instructorShortName', 'instructorName', 'instructorEmail', 'instructorInstitution'];
    const testValues = fields.map(field => `${field}test`);

    // initialize input dom
    const fixture = $('#qunit-fixture');
    const addInstructorFields = fields.map((field, i) => {
        const testValue = testValues[i];
        return `<input id="${field}" type="text" value="${testValue}">`;
    });
    fixture.append(addInstructorFields.join());

    // test state changes and logic flow
    isInputFromFirstPanel = true;
    const implementation = addInstructorByAjaxRecursively;
    addInstructorByAjaxRecursively = function () {
        assert.ok(true, 'addInstructorByAjaxRecursively is called');
    };
    addInstructorFromSecondFormByAjax();
    addInstructorByAjaxRecursively = implementation;

    assert.notOk(isInputFromFirstPanel, 'isInputFromFirstPanel is set to false');

    // test state data is correctly inserted
    assert.equal(instructorDetailsList.length, 1, 'data appended to instructorDetailsList');
    assert.equal(paramsList.length, 1, 'data appended to paramsList');
    const firstParams = paramsList[0];
    fields.forEach((field, i) => {
        const fieldValue = testValues[i];
        assert.contains(firstParams, fieldValue, `${fieldValue} is inserted into paramsList`);
    });
});
