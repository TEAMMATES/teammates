/**
 * Holds Handsontable settings, reference and other information for the spreadsheet interface.
 */
/* global Handsontable:false */
import {
    prepareInstructorPages,
} from '../common/instructor';

import {
    BootstrapContextualColors,
    ParamsNames,
    Const,
} from '../common/const';

import {
    getUpdatedHeaderString,
    getUserDataRows,
    ajaxDataToHandsontableData,
    spreadsheetDataRowsToHandsontableData,
    getSpreadsheetLength,
    toggleStudentsPanel,
    showPasteModalBox,
    showEnrollSuccessModalBox,
    showEnrollFailureModalBox,
} from '../common/instructorEnroll';

import {
    appendNewStatusMessage,
    clearStatusMessages,
} from '../common/statusMessage';

const dataContainer = document.getElementById('existingDataSpreadsheet');
let dataHandsontable = new Handsontable(dataContainer, {
    readOnly: true,
    height: 400,
    autoWrapRow: true,
    preventOverflow: 'horizontal',
    manualColumnResize: true,
    manualRowResize: true,
    rowHeaders: true,
    colHeaders: ['Section', 'Team', 'Name', 'Email', 'Comments'],
    columnSorting: true,
    sortIndicator: true,
    minRows: 20,
    maxCols: 5,
    stretchH: 'all',
});

const enrollContainer = document.getElementById('enrollSpreadsheet');
const enrollHandsontable = new Handsontable(enrollContainer, {
    className: 'enroll-handsontable',
    height: 500,
    autoWrapRow: true,
    preventOverflow: 'horizontal',
    manualColumnResize: true,
    manualRowResize: true,
    manualColumnMove: true,
    rowHeaders: true,
    colHeaders: ['Section', 'Team', 'Name', 'Email', 'Comments'],
    columnSorting: true,
    sortIndicator: true,
    minRows: 20,
    maxCols: 5,
    maxRows: 100,
    stretchH: 'all',
    minSpareRows: 1,
    contextMenu: [
        'row_above',
        'row_below',
        'remove_row',
        'undo',
        'redo',
        {
            key: 'paste',
            name: 'Paste',
            callback: showPasteModalBox,
        },
        'make_read_only',
        'alignment',
    ],
});

const enrollErrorMessagesMap = new Map();
const enrollNewStudentsMessagesMap = new Map();
const enrollModifiedStudentsMessagesMap = new Map();
const enrollUnmodifiedStudentsMessagesMap = new Map();

/**
 * Function to update the enrollHandsontable cell settings according to a custom renderer.
 */
function updateEnrollHandsontableCellSettings(targetRenderer) {
    enrollHandsontable.updateSettings({
        cells: () => {
            const cellProperties = {};
            cellProperties.renderer = targetRenderer; // uses function directly
            return cellProperties;
        },
    });
}

/**
 * Custom renderer to reset any cell styles and tooltips in the current Handsontable instance.
 */
/* eslint-disable prefer-rest-params */
function resetDefaultViewRenderer(instance, td) {
    Handsontable.renderers.TextRenderer.apply(this, arguments);
    $(td).tooltip('destroy');
    td.style.background = '#FFFFFF';
}

/**
 * Custom renderer to update the rows of the Handsontable instance to the respective cell styles and tooltips.
 */
function statusMessageRowsRenderer(instance, td, row) {
    Handsontable.renderers.TextRenderer.apply(this, arguments);
    let text = '';

    if (enrollErrorMessagesMap.has(row)) {
        td.style.background = '#ff6666';
        text = enrollErrorMessagesMap.get(row);
    } else if (enrollNewStudentsMessagesMap.has(row)) {
        td.style.background = '#7CFC00';
        text = enrollNewStudentsMessagesMap.get(row);
    } else if (enrollModifiedStudentsMessagesMap.has(row)) {
        td.style.background = '#ffc20e';
        text = enrollModifiedStudentsMessagesMap.get(row);
    } else if (enrollUnmodifiedStudentsMessagesMap.get(row)) {
        td.style.background = '#ADD8E6';
        text = enrollUnmodifiedStudentsMessagesMap.get(row);
    } else {
        td.style.background = '#FFFFFF';
        return;
    }
    $(td).tooltip({
        trigger: 'hover active',
        title: text,
        placement: 'auto',
        container: 'body',
        template: '<div class="tooltip" role="tooltip"><div class="tooltip-arrow">'
        + '</div><div class="tooltip-inner"></div></div>',
    });
}

enrollHandsontable.addHook('beforeColumnSort', () => {
    updateEnrollHandsontableCellSettings(resetDefaultViewRenderer);
});

/**
 * Updates the student data from the spreadsheet when the user clicks "Enroll Students" button.
 * Pushes the output data into the textarea (used for form submission).
 */
function updateDataDump() {
    const spreadsheetData = enrollHandsontable.getData();
    const dataPushToTextarea = getUpdatedHeaderString(enrollHandsontable.getColHeader());
    const userDataRows = getUserDataRows(spreadsheetData);
    $('#enrollstudents').text(userDataRows === ''
            ? '' : dataPushToTextarea + userDataRows); // only pushes header string if userDataRows is not empty
}

/**
 * Loads existing student data into the spreadsheet interface.
 */
function loadExistingStudentsData(studentsData) {
    dataHandsontable.loadData(ajaxDataToHandsontableData(
            studentsData, dataHandsontable.getColHeader()));
}

/**
 * Gets list of student data through an AJAX request.
 * @returns {Promise} the state of the result from the AJAX request
 */
function getAjaxStudentList(ajaxPreloadImage) {
    return new Promise((resolve, reject) => {
        const $spreadsheetForm = $('#student-spreadsheet-form');
        $.ajax({
            type: 'POST',
            url: '/page/instructorCourseEnrollAjaxPage',
            cache: false,
            data: {
                courseid: $spreadsheetForm.children(`input[name="${ParamsNames.COURSE_ID}"]`).val(),
            },
            beforeSend() {
                ajaxPreloadImage.show();
            },
        }).done(resolve)
                .fail(reject);
    });
}

/**
 * Gets enroll status through an AJAX request.
 * @returns {Promise} the state of the result from the AJAX request
 */
function getAjaxEnrollStatus(ajaxLoaderImage, enrollStudentsButton) {
    return new Promise((resolve, reject) => {
        const $spreadsheetForm = $('#student-spreadsheet-form');
        $.ajax({
            type: 'POST',
            url: '/page/instructorCourseEnrollAjaxSave',
            cache: false,
            data: {
                courseid: $spreadsheetForm.children(`input[name='${ParamsNames.COURSE_ID}']`).val(),
                enrollstudents: $spreadsheetForm.find('#enrollstudents').val(),
            },
            beforeSend() {
                ajaxLoaderImage.show();
                enrollStudentsButton.hide();
            },
        }).done(resolve)
                .fail(reject);
    });
}

/**
 * Handles how the panels are displayed, including rendering the spreadsheet interface.
 */
function adjustStudentsPanelView(panelHeading, panelCollapse, handsontableInstance) {
    toggleStudentsPanel(panelHeading, panelCollapse);
    // needed as the view is buggy after collapsing the panel
    handsontableInstance.render();
}

/**
 * Adds all error messages returned from the backend into a map.
 */
function addEnrollErrorMessages(dataEnrollErrorMap) {
    enrollHandsontable.getData().forEach((row, index) => {
        const currRowLine = row.join('|');
        if (dataEnrollErrorMap.has(currRowLine)) {
            enrollErrorMessagesMap.set(index,
                    dataEnrollErrorMap.get(currRowLine));
        }
    });
}

/**
 * Adds all successful messages returned from the backend into the respective maps.
 */
function addEnrollSuccessMessages(dataEnrollNewStudentsMap, dataEnrollModifiedStudentsMap,
        dataEnrollUnmodifiedStudentsMap) {
    const emailColumnIndex = 3;
    enrollHandsontable.getData().forEach((row, index) => {
        const currRowEmail = row[emailColumnIndex];
        if (dataEnrollNewStudentsMap.has(currRowEmail)) {
            enrollNewStudentsMessagesMap.set(index,
                    dataEnrollNewStudentsMap.get(currRowEmail));
        } else if (dataEnrollModifiedStudentsMap.has(currRowEmail)) {
            enrollModifiedStudentsMessagesMap.set(index,
                    dataEnrollModifiedStudentsMap.get(currRowEmail));
        } else if (dataEnrollUnmodifiedStudentsMap.has(currRowEmail)) {
            enrollUnmodifiedStudentsMessagesMap.set(index,
                    dataEnrollUnmodifiedStudentsMap.get(currRowEmail));
        }
    });
}

/**
 * Handles the main logic for success/failed enroll case
 */
function processAjaxSaveAction(data) {
    if (data.statusMessagesToUser.length === 1
            && data.statusMessagesToUser[0].color === 'SUCCESS') {
        addEnrollSuccessMessages(new Map(Object.entries(data.enrollNewStudentsLines)),
                new Map(Object.entries(data.enrollModifiedStudentsLines)),
                new Map(Object.entries(data.enrollUnmodifiedStudentsLines)));
        updateEnrollHandsontableCellSettings(statusMessageRowsRenderer);
        showEnrollSuccessModalBox();
    } else if (data.statusMessagesToUser.length === 1
            && data.statusMessagesToUser[0].text === Const.StatusMessages.ENROLL_LINE_EMPTY) {
        appendNewStatusMessage(Const.StatusMessages.ENROLL_LINE_EMPTY,
                BootstrapContextualColors[data.statusMessagesToUser[0].color]);
    } else if (data.statusMessagesToUser.length === 1
            && data.statusMessagesToUser[0].text === Const.StatusMessages.QUOTA_PER_ENROLLMENT_EXCEED) {
        appendNewStatusMessage(Const.StatusMessages.QUOTA_PER_ENROLLMENT_EXCEED,
                BootstrapContextualColors[data.statusMessagesToUser[0].color]);
    } else {
        addEnrollErrorMessages(new Map(Object.entries(data.enrollErrorLines)));
        updateEnrollHandsontableCellSettings(statusMessageRowsRenderer);
        showEnrollFailureModalBox();
    }
}

/**
 * Triggers an AJAX request to retrieve the enroll status.
 * Does the necessary post processing after the state of the AJAX request is returned.
 */
function triggerAndProcessAjaxSaveAction(event) {
    const enrollStudentsButton = $(event.currentTarget);
    const ajaxLoaderImage = enrollStudentsButton.parent().children('.ajax-loader-image');

    getAjaxEnrollStatus(ajaxLoaderImage, enrollStudentsButton)
            .then((data) => {
                ajaxLoaderImage.hide();
                enrollStudentsButton.show();
                clearStatusMessages();
                updateEnrollHandsontableCellSettings(resetDefaultViewRenderer);
                processAjaxSaveAction(data);
            }).catch(() => {
                ajaxLoaderImage.hide();
                enrollStudentsButton.show();
                clearStatusMessages();
                appendNewStatusMessage('Failed to enroll students. Check your internet connectivity.',
                        BootstrapContextualColors.DANGER);
            });
}

/**
 * Hides the panel and reset dataHandsontable. Used after the "Enroll students" is clicked.
 */
function resetDataHandsontable(panelHeading, panelCollapse) {
    const toggleChevron = panelHeading.parent().find('.glyphicon-chevron-down, .glyphicon-chevron-up');
    if (panelCollapse.hasClass('in')) { // panel is shown
        panelCollapse.collapse('hide');
        toggleChevron.addClass('glyphicon-chevron-down').removeClass('glyphicon-chevron-up');
    }
    dataHandsontable = new Handsontable(dataContainer, {
        readOnly: true,
        height: 400,
        autoWrapRow: true,
        preventOverflow: 'horizontal',
        manualColumnResize: true,
        manualRowResize: true,
        rowHeaders: true,
        colHeaders: ['Section', 'Team', 'Name', 'Email', 'Comments'],
        columnSorting: true,
        sortIndicator: true,
        minRows: 20,
        maxCols: 5,
        stretchH: 'all',
    });
    dataHandsontable.render();
}

/**
 * Expands "Existing students" panel. Spreadsheet interface would be shown after expansion.
 * An AJAX request would be called to load existing
 * students' data into the spreadsheet interface (if spreadsheet is not empty).
 * The panel will be collapsed otherwise if the spreadsheet interface is already shown.
 */
function existingStudentsPanelClickHandler(event) {
    const panelHeading = $(event.currentTarget);
    const panelCollapse = panelHeading.parent().children('.panel-collapse');

    const ajaxPreloadImage = panelHeading.find('#ajax-preload-image');
    const ajaxStatusMessage = panelHeading.find('.ajax-status-message');
    ajaxStatusMessage.hide(); // hide any status message from the previous state

    if (getSpreadsheetLength(dataHandsontable.getData()) !== 0) {
        adjustStudentsPanelView(panelHeading, panelCollapse, dataHandsontable);
        return;
    }
    // perform AJAX only if existing students' spreadsheet is empty
    getAjaxStudentList(ajaxPreloadImage)
            .then((data) => {
                ajaxPreloadImage.hide();
                if (data.students.length === 0) {
                    ajaxStatusMessage.show();
                    ajaxStatusMessage.text('No existing students in course.');
                } else {
                    loadExistingStudentsData(data.students);
                    adjustStudentsPanelView(panelHeading, panelCollapse, dataHandsontable);
                }
            }).catch(() => {
                ajaxPreloadImage.hide();
                ajaxStatusMessage.show();
                ajaxStatusMessage.text('Failed to load. Click here to retry.');
            });
}

$(document).ready(() => {
    prepareInstructorPages();
    $('#enroll-spreadsheet').on('click', (event) => {
        const panelHeading = $(event.currentTarget);
        const panelCollapse = panelHeading.parent().children('.panel-collapse');
        adjustStudentsPanelView(panelHeading, panelCollapse, enrollHandsontable);
        $('.enroll-students-spreadsheet-buttons').toggle();
    });
    $('#enroll-spreadsheet').trigger('click');

    $('#existing-data-spreadsheet').on('click', existingStudentsPanelClickHandler);

    if ($('#enrollstudents').val()) {
        const allData = $('#enrollstudents').val().split('\n'); // data in the table including column headers (string format)

        const columnHeaders = allData[0].split('|');
        enrollHandsontable.updateSettings({
            colHeaders: columnHeaders,
        });

        const spreadsheetDataRows = allData.slice(1);
        if (spreadsheetDataRows.length > 0) {
            const data = spreadsheetDataRowsToHandsontableData(spreadsheetDataRows);
            enrollHandsontable.loadData(data); // Reset all cells in the grid to contain data from the data array
        }
    }

    $('#button_add_empty_rows').click(() => {
        const emptyRowsCount = $('#number-of-rows').val();
        enrollHandsontable.alter('insert_row', null, emptyRowsCount);
    });

    $('#button_enroll').on('click', (event) => {
        enrollErrorMessagesMap.clear();
        enrollNewStudentsMessagesMap.clear();
        enrollModifiedStudentsMessagesMap.clear();
        enrollUnmodifiedStudentsMessagesMap.clear();
        updateDataDump();
        triggerAndProcessAjaxSaveAction(event);

        const existingStudentsPanelHeading =
                $('#existing-data-spreadsheet');
        const existingStudentsPanelCollapse =
                existingStudentsPanelHeading.parent().children('.panel-collapse');
        resetDataHandsontable(existingStudentsPanelHeading,
                existingStudentsPanelCollapse);
    });
});
