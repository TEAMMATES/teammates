import {
    showModalConfirmation,
} from './bootboxWrapper';

import {
    BootstrapContextualColors,
} from './const';

import {
    disallowNonNumericEntries,
} from './ui';

function getRubricChoiceElem(questionNum, col) {
    return $(`#rubricChoice-${questionNum}-${col}`);
}

function getRubricWeightElem(questionNum, col) {
    return $(`#rubricWeight-${questionNum}-${col}`);
}

function getRubricDescElem(questionNum, row, col) {
    return $(`#rubricDesc-${questionNum}-${row}-${col}`);
}

function swapRubricCol(questionNum, firstColIndex, secondColIndex) {
    const numberOfRows = parseInt($(`#rubricNumRows-${questionNum}`).val(), 10);
    const CHOICE = 'RUBRIC_CHOICE';
    const WEIGHT = 'RUBRIC_WEIGHT';
    const DESC = 'RUBRIC_DESC';
    const elemSelector = (type, col, row = 0) => {
        if (type === CHOICE) {
            return getRubricChoiceElem(questionNum, col);
        } else if (type === WEIGHT) {
            return getRubricWeightElem(questionNum, col);
        } else if (type === DESC) {
            return getRubricDescElem(questionNum, row, col);
        }

        return null;
    };

    const swapValues = (type, row = 0) => {
        const $a = elemSelector(type, firstColIndex, row);
        const $b = elemSelector(type, secondColIndex, row);
        const temp = $a.val();
        $a.val($b.val());
        $b.val(temp);
    };

    // swap rubric choices
    swapValues(CHOICE);

    // swap rubric weights
    swapValues(WEIGHT);

    // swap options filled
    for (let row = 0; row < numberOfRows; row += 1) {
        swapValues(DESC, row);
    }
}

function moveRubricColIfPossible(questionNum, firstColIndex, isMoveLeft) {
    if ($(`#rubricEditTable-${questionNum}`).length === 0
            || $(`.rubricCol-${questionNum}-${firstColIndex}`).length === 0
            || typeof isMoveLeft !== 'boolean') {
        // question and column should exist, isMoveLeft must be boolean
        return;
    }

    const $swapCell = $(`#rubric-options-row-${questionNum} .rubricCol-${questionNum}-${firstColIndex}`);
    const rubricCellSelector = `td[class*='rubricCol-${questionNum}']`;

    if ((isMoveLeft && $swapCell.prev(rubricCellSelector).length === 0)
        || (!isMoveLeft && $swapCell.next(rubricCellSelector).length === 0)) {
        // trying to swap left most or right most column
        return;
    }

    let secondColIndex;

    if (isMoveLeft) {
        secondColIndex = $swapCell.prev(rubricCellSelector).attr('data-col');
    } else {
        secondColIndex = $swapCell.next(rubricCellSelector).attr('data-col');
    }

    swapRubricCol(questionNum, firstColIndex, secondColIndex);

    const $form = $(`#form_editquestion-${questionNum}`);

    if ($form.attr('editstatus') === 'hasResponses') {
        $form.attr('editstatus', 'mustDeleteResponses');
    }
}

function disableCornerMoveRubricColumnButtons(questionNum) {
    const $optionColumns = $(`#rubric-options-row-${questionNum} td[class*='rubricCol-']`);

    const disableMoveLeftOfFirstCol = () => {
        const $leftmostCol = $optionColumns.first();
        const leftmostColIndex = $leftmostCol.attr('data-col');
        const $leftmostColLeftBtn = $leftmostCol.find(`#rubric-move-col-left-${questionNum}-${leftmostColIndex}`);

        $leftmostColLeftBtn.prop('disabled', true);
    };

    const disableMoveRightOfLastCol = () => {
        const $rightmostCol = $optionColumns.last();
        const rightmostColIndex = $rightmostCol.attr('data-col');
        const $rightmostColRightBtn = $rightmostCol.find(`#rubric-move-col-right-${questionNum}-${rightmostColIndex}`);

        $rightmostColRightBtn.prop('disabled', true);
    };

    const enableMoveRightOfSecondLastCol = () => {
        const $secondlastCol = $optionColumns.last().prev();
        const secondlastColIndex = $secondlastCol.attr('data-col');
        const $secondlastColRightBtn = $secondlastCol.find(`#rubric-move-col-right-${questionNum}-${secondlastColIndex}`);

        $secondlastColRightBtn.prop('disabled', false);
    };

    if ($optionColumns.length < 2) {
        disableMoveLeftOfFirstCol(questionNum);
        disableMoveRightOfLastCol(questionNum);
        return;
    }

    disableMoveLeftOfFirstCol(questionNum);
    disableMoveRightOfLastCol(questionNum);
    enableMoveRightOfSecondLastCol(questionNum);
}

function addRubricRow(questionNum) {
    const questionId = `#form_editquestion-${questionNum}`;

    const numberOfRows = parseInt($(`#rubricNumRows-${questionNum}`).val(), 10);
    const numberOfCols = parseInt($(`#rubricNumCols-${questionNum}`).val(), 10);

    const newRowNumber = numberOfRows + 1;

    let rubricRowBodyFragments = '';
    // Create numberOfCols of <td>'s
    for (let cols = 0; cols < numberOfCols; cols += 1) {
        if (!$(`.rubricCol-${questionNum}-${cols}`).length) {
            continue;
        }
        const rubricRowFragment =
            `<td class="align-center rubricCol-${questionNum}-${cols}">
                <textarea class="form-control" rows="3" id="rubricDesc-${questionNum}-${newRowNumber - 1}-${cols}"
                        name="rubricDesc-${newRowNumber - 1}-${cols}"></textarea>
            </td>`;
        rubricRowBodyFragments += rubricRowFragment;
    }

    // Create new rubric row
    const newRubricRow =
        `<tr id="rubricRow-${questionNum}-${newRowNumber - 1}">
            <td>
                <div class="col-sm-12 input-group">
                    <span class="input-group-addon btn btn-default rubricRemoveSubQuestionLink-${questionNum}"
                            id="rubricRemoveSubQuestionLink-${questionNum}-${newRowNumber - 1}"
                            onclick="removeRubricRow(${newRowNumber - 1}, ${questionNum})"
                            onmouseover="highlightRubricRow(${newRowNumber - 1}, ${questionNum}, true)"
                            onmouseout="highlightRubricRow(${newRowNumber - 1}, ${questionNum}, false)">
                        <span class="glyphicon glyphicon-remove"></span>
                    </span>
                    <textarea class="form-control" rows="3" id="rubricSubQn-${questionNum}-${newRowNumber - 1}"
                            name="rubricSubQn-${newRowNumber - 1}" required=""></textarea>
                </div>
            </td>
            ${rubricRowBodyFragments}
        </tr>`;

    // Row to insert new row after
    const $secondLastRow = $(`#rubricEditTable-${questionNum} tbody tr:nth-last-child(2)`);
    $(newRubricRow).insertAfter($secondLastRow);

    // Increment
    $(`#rubricNumRows-${questionNum}`).val(newRowNumber);

    if ($(questionId).attr('editStatus') === 'hasResponses') {
        $(questionId).attr('editStatus', 'mustDeleteResponses');
    }
}

function addRubricCol(questionNum) {
    const questionId = `#form_editquestion-${questionNum}`;

    const numberOfRows = parseInt($(`#rubricNumRows-${questionNum}`).val(), 10);
    const numberOfCols = parseInt($(`#rubricNumCols-${questionNum}`).val(), 10);

    const newColNumber = numberOfCols + 1;

    // Insert header <th>
    const rubricHeaderFragment =
        `<th class="rubricCol-${questionNum}-${newColNumber - 1}">
            <input type="text" class="col-sm-12 form-control" value=""
                    id="rubricChoice-${questionNum}-${newColNumber - 1}"
                    name="rubricChoice-${newColNumber - 1}">
        </th>`;

    // Insert after last <th>
    const lastTh = $(`#rubricEditTable-${questionNum}`).find('tr:first').children().last();
    $(rubricHeaderFragment).insertAfter(lastTh);

    // Insert weight <th>
    const rubricWeightFragment =
        `<th class="rubricCol-${questionNum}-${newColNumber - 1}">
            <input type="number" class="form-control nonDestructive" value="0"
                    id="rubricWeight-${questionNum}-${newColNumber - 1}"
                    name="rubricWeight-${newColNumber - 1}" step="0.01">
        </th>`;

    // Insert after last <th>
    const lastWeightCell = $(`#rubricWeights-${questionNum} th:last`);
    $(rubricWeightFragment).insertAfter(lastWeightCell);

    disallowNonNumericEntries($(`#rubricWeight-${questionNum}-${newColNumber - 1}`), true, true);

    // Create numberOfRows of <td>'s
    for (let rows = 0; rows < numberOfRows; rows += 1) {
        if (!$(`#rubricRow-${questionNum}-${rows}`).length) {
            continue;
        }
        // Insert body <td>'s
        const rubricRowFragment =
            `<td class="align-center rubricCol-${questionNum}-${newColNumber - 1}">
                <textarea class="form-control" rows="3" id="rubricDesc-${questionNum}-${rows}-${newColNumber - 1}"
                        name="rubricDesc-${rows}-${newColNumber - 1}"></textarea>
            </td>`;

        // Insert after previous <td>
        const lastTd = $(`#rubricRow-${questionNum}-${rows} td:last`);
        $(rubricRowFragment).insertAfter(lastTd);
    }

    // Add options row at the end
    const optionsRow =
        `<td class="align-center rubricCol-${questionNum}-${newColNumber - 1}" data-col="${newColNumber - 1}">
            <div class="btn-group">
                <button type="button" class="btn btn-default" id="rubric-move-col-left-${questionNum}-${newColNumber - 1}"
                        data-toggle="tooltip" data-placement="top" title="Move column left">
                    <span class="glyphicon glyphicon-arrow-left"></span>
                </button>
                <button type="button" class="btn btn-default" id="rubricRemoveChoiceLink-${questionNum}-${newColNumber - 1}"
                        onclick="removeRubricCol(${newColNumber - 1}, ${questionNum})"
                        onmouseover="highlightRubricCol(${newColNumber - 1}, ${questionNum}, true)"
                        onmouseout="highlightRubricCol(${newColNumber - 1}, ${questionNum}, false)">
                    <span class="glyphicon glyphicon-remove"></span>
                </button>
                <button type="button" class="btn btn-default" id="rubric-move-col-right-${questionNum}-${newColNumber - 1}"
                        data-toggle="tooltip" data-placement="top" title="Move column right">
                    <span class="glyphicon glyphicon-arrow-right"></span>
                </button>
            </div>
        </td>`;

    const $lastTd = $(`#rubric-options-row-${questionNum} td:last`);
    $(optionsRow).insertAfter($lastTd);

    // Initialize tooltips and set click event handlers for move column buttons
    const $newColMoveLeftBtn = $(`#rubric-move-col-left-${questionNum}-${newColNumber - 1}`);
    const $newColMoveRightBtn = $(`#rubric-move-col-right-${questionNum}-${newColNumber - 1}`);

    $newColMoveLeftBtn.tooltip({ container: 'body' });
    $newColMoveRightBtn.tooltip({ container: 'body' });

    $newColMoveLeftBtn.click(() => {
        moveRubricColIfPossible(questionNum, newColNumber - 1, true);
    });

    $newColMoveRightBtn.click(() => {
        moveRubricColIfPossible(questionNum, newColNumber - 1, false);
    });

    // Increment
    $(`#rubricNumCols-${questionNum}`).val(newColNumber);

    if ($(questionId).attr('editStatus') === 'hasResponses') {
        $(questionId).attr('editStatus', 'mustDeleteResponses');
    }

    disableCornerMoveRubricColumnButtons(questionNum);
}

function removeRubricRow(index, questionNum) {
    const questionId = `#form_editquestion-${questionNum}`;

    const $thisRow = $(`#rubricRow-${questionNum}-${index}`);

    // count number of table rows from table body
    const numberOfRows = $thisRow.parent().children('tr').length - 1; // exclude options row

    const delStr = numberOfRows <= 1 ? 'clear' : 'delete';
    const messageText = `Are you sure you want to ${delStr} the row?`;
    const okCallback = function () {
        if (numberOfRows <= 1) {
            $thisRow.find('textarea').val('');
        } else {
            $thisRow.remove();

            if ($(questionId).attr('editStatus') === 'hasResponses') {
                $(questionId).attr('editStatus', 'mustDeleteResponses');
            }
        }
    };
    showModalConfirmation('Confirm Deletion', messageText, okCallback, null, null, null, BootstrapContextualColors.WARNING);
}

function removeRubricCol(index, questionNum) {
    const questionId = `#form_editquestion-${questionNum}`;

    const $thisCol = $(`.rubricCol-${questionNum}-${index}`);

    // count number of table columns from table body
    const numberOfCols = $thisCol.first().parent().children().length - 1;

    const delStr = numberOfCols <= 1 ? 'clear' : 'delete';
    const messageText = `Are you sure you want to ${delStr} the column?`;
    const okCallback = function () {
        if (numberOfCols <= 1) {
            $thisCol.find('input[id^="rubricChoice"], textarea').val('');
            $thisCol.find('input[id^="rubricWeight"]').val(0);
        } else {
            $thisCol.remove();
            disableCornerMoveRubricColumnButtons(questionNum);

            if ($(questionId).attr('editStatus') === 'hasResponses') {
                $(questionId).attr('editStatus', 'mustDeleteResponses');
            }
        }
    };
    showModalConfirmation('Confirm Deletion', messageText, okCallback, null, null, null, BootstrapContextualColors.WARNING);
}

function highlightRubricRow(index, questionNum, highlight) {
    const $rubricRow = $(`#rubricRow-${questionNum}-${index}`);

    if (highlight) {
        $rubricRow.find('td').addClass('cell-selected-negative');
    } else {
        $rubricRow.find('td').removeClass('cell-selected-negative');
    }
}

function highlightRubricCol(index, questionNum, highlight) {
    const $rubricCol = $(`.rubricCol-${questionNum}-${index}`);

    if (highlight) {
        $rubricCol.addClass('cell-selected-negative');
    } else {
        $rubricCol.removeClass('cell-selected-negative');
    }
}

/**
 * Shows the weight row if the "Choices are weighted" checkbox is checked, otherwise
 * hides the weight row.
 *
 * @param checkbox the "Choices are weighted" checkbox
 */
function toggleAssignWeightsRow($checkbox) {
    const $weightsRow = $checkbox.closest('form').find('tr[id^="rubricWeights"]');
    const $weightsRowFirstCell = $weightsRow.find('th').first();

    if ($checkbox.prop('checked')) {
        $weightsRow.show();
        $weightsRowFirstCell.html('Weights <span class="glyphicon glyphicon-arrow-right"></span>');
    } else {
        $weightsRow.hide();
    }
}

/**
 * Attaches event handlers to "Choices are weighted" checkboxes to toggle the visibility of
 * the input boxes for rubric weights.
 */
function bindAssignWeightsCheckboxes() {
    $('body').on('click', 'input[id^="rubricAssignWeights"]', function () {
        const $checkbox = $(this);

        toggleAssignWeightsRow($checkbox);
    });
}

/**
 * Attaches click event handlers move rubric column buttons to
 * all rubric questions. To be called in $(document).ready().
 */
function bindMoveRubricColButtons() {
    $('table[id^="rubricEditTable-"]').each(function () {
        const questionNum = $(this).closest('form').data('qnnumber');

        $(`#rubric-options-row-${questionNum} td[class*="rubricCol-${questionNum}"]`).each(function () {
            const colNum = $(this).attr('data-col');

            $(`#rubric-move-col-left-${questionNum}-${colNum}`).click(() => {
                moveRubricColIfPossible(questionNum, colNum, true);
            });

            $(`#rubric-move-col-right-${questionNum}-${colNum}`).click(() => {
                moveRubricColIfPossible(questionNum, colNum, false);
            });
        });
    });
}

/**
 * @param questionNum
 *            the question number of the feedback question
 * @returns {Boolean} true if the weights are assigned by the user, otherwise false
 */
function hasAssignedWeights(questionNum) {
    return $(`#rubricAssignWeights-${questionNum}`).prop('checked');
}

export {
    addRubricCol,
    addRubricRow,
    bindAssignWeightsCheckboxes,
    bindMoveRubricColButtons,
    disableCornerMoveRubricColumnButtons,
    hasAssignedWeights,
    highlightRubricCol,
    highlightRubricRow,
    removeRubricCol,
    removeRubricRow,
    toggleAssignWeightsRow,
};
