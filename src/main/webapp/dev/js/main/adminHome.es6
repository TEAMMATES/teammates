import {
    Const,
} from '../common/const.es6';

import {
    makeCsrfTokenParam,
} from '../common/crypto.es6';

import {
    encodeHtmlString,
} from '../common/sanitizer.es6';

import {
    assertDefined,
    assert,
} from '../common/assert.es6';

/**
 * Represents the result of an AJAX request made to add an instructor.
 * Encapsulates the instructor for which an add was attempted and the AJAX response (responseData).
 * The instructor can be either an actual Instructor or an InstructorError (which may arise
 * from a parse error when Instructor.createFromString is used), but never null.
 * The responseData is null exactly when the instructor is an InstructorError.
 *
 */
class InstructorAjaxResult {
    constructor(instructor, responseData) {
        assertDefined(instructor,
                'InstructorAjaxResult cannot be constructed without instructor being defined.');
        assert(!instructor.isError() || responseData === null,
                'Response data must be null if instantiating with an error.');
        this.instructor = instructor;
        this.responseData = responseData;
    }
    /* eslint-disable class-methods-use-this */
    isError() {
        return this.instructor.isError();
    }
    /* eslint-enable class-methods-use-this */
    isAddFailed() {
        return !this.isError() && !this.responseData.isInstructorAddingResultForAjax;
    }
    /**
     * If The AJAX request never happened because the instructor itself is erroneous,
     * return the error message encapsulated in the InstructorError.
     * Otherwise, return the AJAX status message.
     */
    getStatusMessage() {
        if (this.isError()) {
            return this.instructor.getErrorMessage();
        }
        return this.responseData.statusForAjax;
    }
}

/**
 * Stands in for an Instructor whenever an error occurs.
 * The isError() method returns true here (but false in the Instructor class).
 * In the event that a string was used to construct the Instructor
 * but the construction failed, that string can be passed in to the InstructorError constructor.
 */
class InstructorError {
    constructor(message, originalString) {
        this.message = message;
        this.originalString = originalString;
    }
    toString() {
        return this.originalString;
    }
    /* eslint-disable class-methods-use-this */
    isError() {
        return true;
    }
    /* eslint-enable class-methods-use-this */
    getErrorMessage() {
        return this.message;
    }
}

/**
 * Represents an instructor.
 * Contains the shortname, name, email and institution fields.
 * Static "constructor" functions are used since constructors cannot be overloaded in ES6.
 */
class Instructor {
    /**
     * Takes in several instructor attributes and constructs an instructor.
     */
    static create(shortName, name, email, institution) {
        const instructor = new Instructor();
        instructor.shortName = shortName;
        instructor.name = name;
        instructor.email = email;
        instructor.institution = institution;
        return instructor;
    }
    /**
     * Takes in a string in either of the following formats:
     *  NAME | EMAIL | INSTITUTION
     *  NAME\tEMAIL\tINSTITUTION
     * If the string is in the correct format, parses it and constructs an Instructor.
     * If the format is wrong, constructs and returns a InstructorError instead.
     */
    static createFromString(str) {
        const regexStringForPipeSeparator = '(?: *\\| *)';
        const regexStringForTabSeparator = '\\t+';
        const regexStringForSeparator = `(?:${regexStringForTabSeparator}|${regexStringForPipeSeparator})`;
        const regexStringForFields = '([^|\\t]+?)';

        const instructorMatchRegex = new RegExp(`^${[
            regexStringForFields, // name
            regexStringForSeparator,
            regexStringForFields, // email
            regexStringForSeparator,
            regexStringForFields, // institution
        ].join('')}$`);

        const instructorData = str.match(instructorMatchRegex);
        if (instructorData === null) {
            return new InstructorError(Const.StatusMessages.INSTRUCTOR_DETAILS_LENGTH_INVALID, str);
        }

        const shortName = instructorData[1];
        const name = instructorData[1];
        const email = instructorData[2];
        const institution = instructorData[3];
        return Instructor.create(shortName, name, email, institution);
    }
    toString() {
        return `${this.name} | ${this.email} | ${this.institution}`;
    }
    getParamString() {
        return $.param({
            instructorshortname: this.shortName,
            instructorname: this.name,
            instructoremail: this.email,
            instructorinstitution: this.institution,
        });
    }
    /* eslint-disable class-methods-use-this */
    isError() {
        return false;
    }
    /* eslint-enable class-methods-use-this */
    static allFromString(multipleInstructorsString) {
        return multipleInstructorsString
            .split('\n')
            .map(singleInstructorString => Instructor.createFromString(singleInstructorString));
    }
    static allToString(instructors) {
        return instructors
            .map(instructor => instructor.toString())
            .join('\n');
    }
}

/**
 * Generates HTML text for a row containing instructor's information
 * and status of the action.
 *
 * @param {String} shortName
 * @param {String} name
 * @param {String} email
 * @param {String} institution
 * @param {bool} isSuccess is a flag to show the action is successful or not.
 * The color and status of the row is affected by its value.
 * @param {String} status
 * @returns {String} a HTML row of action result table
 */
function createRowForResultTable(shortName, name, email, institution, isSuccess, status) {
    return `
    <tr class="${isSuccess ? 'success' : 'danger'}">
        <td>${encodeHtmlString(shortName)}</td>
        <td>${encodeHtmlString(name)}</td>
        <td>${encodeHtmlString(email)}</td>
        <td>${encodeHtmlString(institution)}</td>
        <td>${isSuccess ? 'Success' : 'Fail'}</td>
        <td>${status}</td>
    </tr>
    `;
}

/**
 * Disables the Add Instructor form.
 */
function disableAddInstructorForm() {
    $('.addInstructorBtn').each(function () {
        $(this).html("<img src='/images/ajax-loader.gif'/>");
    });
    $('.addInstructorFormControl').each(function () {
        $(this).prop('disabled', true);
    });
}

/**
 * Enables the Add Instructor form.
 */
function enableAddInstructorForm() {
    $('.addInstructorBtn').each(function () {
        $(this).html('Add Instructor');
    });
    $('.addInstructorFormControl').each(function () {
        $(this).prop('disabled', false);
    });
}

/**
 * Is called as part of the callback to the AJAX call used to add instructors.
 * Updates the table that displays whether adding the instructor was successful.
 * In the event of an error, preserves the text entered into the forms so that a
 * retry can be attempted.
 */
function updateInstructorAddStatus(ajaxResults) {
    for (let i = 0; i < ajaxResults.length; i += 1) {
        const ajaxResult = ajaxResults[i];

        const shortName = ajaxResult.isError() ? '-' : ajaxResult.responseData.instructorShortName;
        const name = ajaxResult.isError() ? '-' : ajaxResult.responseData.instructorName;
        const email = ajaxResult.isError() ? '-' : ajaxResult.responseData.instructorEmail;
        const institution = ajaxResult.isError() ? '-' : ajaxResult.responseData.instructorInstitution;
        const isSuccess = !ajaxResult.isError() && !ajaxResult.isAddFailed();
        const status = ajaxResult.getStatusMessage();

        const rowText = createRowForResultTable(shortName, name, email, institution, isSuccess, status);
        $('#addInstructorResultTable tbody').append(rowText);
        const panelHeader = `<strong>Result (${i + 1}/${ajaxResults.length})</strong>`;
        $('#addInstructorResultPanel div.panel-heading').html(panelHeader);
    }
}

/**
 * Takes in a list of instructor objects and a postprocessing function.
 * Adds the instructors one by one recursively, in the following manner:
 *  Base Case -      If there are no instructors left to add, the postProcess function is called.
 *  Recursive Case - Add the first instructor, and make a recursive call to add the remaining
 *                   instructors in the AJAX callback function.
 * This is done as it is the simplest solution that sidesteps race conditions and
 * does not involve busy waiting in the main thread.
 */
function addInstructors(instructors, postProcess) {
    const ajaxResults = [];

    /* eslint-disable no-shadow */
    const addInstructorsHelper = (instructors) => {
        if (instructors.length === 0) {
            postProcess(ajaxResults);
            enableAddInstructorForm();
            return;
        }

        const firstInstructor = instructors[0];
        const remainingInstructors = instructors.slice(1);

        if (firstInstructor.isError()) {
            ajaxResults.push(new InstructorAjaxResult(firstInstructor, null));
            addInstructorsHelper(remainingInstructors);
            return;
        }

        $.ajax({
            type: 'POST',
            url: `/admin/adminInstructorAccountAdd?${makeCsrfTokenParam()}&${firstInstructor.getParamString()}`,
            beforeSend: disableAddInstructorForm,
            error() {
                const ajaxError = new InstructorError('Cannot send Ajax Request!');
                ajaxResults.push(new InstructorAjaxResult(ajaxError, null));
                addInstructorsHelper(remainingInstructors);
            },
            success(data) {
                ajaxResults.push(new InstructorAjaxResult(firstInstructor, data));
                addInstructorsHelper(remainingInstructors);
            },
        });
    };
    /* eslint-enable no-shadow */

    addInstructorsHelper(instructors, postProcess);
}

function addInstructorFromFirstFormByAjax() {
    const $instructorsAddTextArea = $('#addInstructorDetailsSingleLine');

    const instructors = Instructor.allFromString($instructorsAddTextArea.val());
    const postProcess = (ajaxResults) => {
        const failedInstructors = ajaxResults
            .filter(ajaxResult => ajaxResult.isError() || ajaxResult.isAddFailed())
            .map(ajaxResult => ajaxResult.instructor);
        const failedInstructorsString = Instructor.allToString(failedInstructors);
        $instructorsAddTextArea.val(failedInstructorsString);
        updateInstructorAddStatus(ajaxResults);
    };

    $('#addInstructorResultPanel').show(); // show the hidden panel
    $('#addInstructorResultTable tbody').html(''); // clear table
    $('#addInstructorDetailsSingleLine').val(''); // clear input form
    $('#addInstructorResultPanel div.panel-heading').html('<strong>Result</strong>'); // clear panel header

    addInstructors(instructors, postProcess);
}

function addInstructorFromSecondFormByAjax() {
    $('#addInstructorResultPanel').show(); // show the hidden panel
    $('#addInstructorResultTable tbody').html(''); // clear table
    $('#addInstructorResultPanel div.panel-heading').html('<strong>Result</strong>'); // clear panel header

    const instructorToAdd = Instructor.create(
            $('#instructorShortName').val(),
            $('#instructorName').val(),
            $('#instructorEmail').val(),
            $('#instructorInstitution').val());
    const postProcess = ajaxResults => updateInstructorAddStatus(ajaxResults);

    addInstructors([instructorToAdd], postProcess);
}

$(document).ready(() => {
    $('#btnAddInstructorDetailsSingleLineForm').on('click', () => {
        addInstructorFromFirstFormByAjax();
    });

    $('#btnAddInstructor').on('click', () => {
        addInstructorFromSecondFormByAjax();
    });
});

export {
    Instructor,
    InstructorError,
    createRowForResultTable,
};
