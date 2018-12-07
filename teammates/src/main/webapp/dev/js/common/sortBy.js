import {
    isDate,
    isNumber,
} from './helper';

/**
 * Returns false if the parameter is either one of null or undefined.
 * Returns true otherwise.
 *
 * @param varToCheck
 */
function isDefined(varToCheck) {
    return varToCheck !== null && varToCheck !== undefined;
}

/**
 * To get point value from a formatted string
 *
 * @param s
 *     A table cell (td tag) that contains the formatted string
 * @param ditchZero
 *     Whether 0% should be treated as lower than -90 or not
 * @returns
 */
function getPointValue(s, ditchZero) {
    let s0 = s;
    const baseValue = 100;

    if (s0.indexOf('/') !== -1) {
        // magic expressions below as these cases will only be compared with
        // case E +(-)X% (0 <= X <= 100)
        if (s0.indexOf('S') !== -1) {
            return (2 * baseValue) + 1; // Case N/S (feedback contribution not sure)
        }

        return (2 * baseValue) + 2; // Case N/A
    }

    if (s0 === '0%') { // Case 0%
        if (ditchZero) {
            return 0;
        }
        return baseValue;
    }

    s0 = s0.replace('E', '').replace('%', ''); // Case E +(-)X%

    if (s0 === '') {
        return baseValue; // Case E
    }

    return baseValue + parseFloat(s0); // Other typical cases
}

/**
 * Sorting comparison functions.
 */
class Comparators {
    /**
     * The base comparator (ascending)
     * @returns 1 if x comes after y, -1 if x comes before y, 0 if they are the same
     */
    static sortBase(x, y) {
        // Text sorting
        if (x < y) {
            return -1;
        }
        return x > y ? 1 : 0;
    }

    /**
     * Comparator for numbers (integer, double) (ascending)
     * @returns +ve if x > y, -ve if x < y and 0 otherwise
     */
    static sortNum(x, y) {
        return x - y;
    }

    /**
     * Comparator for date. Allows for the same format as isDate()
     * @returns 1 if Date x is after y, 0 if same and -1 if before
     */
    static sortDate(x, y) {
        const x0 = Date.parse(x);
        const y0 = Date.parse(y);
        if (x0 > y0) {
            return 1;
        }
        return x0 < y0 ? -1 : 0;
    }

    /**
     * Comparator to sort strings in format: E([+-]x%) | N/A | N/S | 0% with
     * possibly a tag that surrounds it.
     */
    static sortByPoints(a, b) {
        const a0 = getPointValue(a, true);
        const b0 = getPointValue(b, true);
        if (isNumber(a0) && isNumber(b0)) {
            return Comparators.sortNum(a0, b0);
        }
        return Comparators.sortBase(a0, b0);
    }

    /**
     * Comparator to sort strings in format: [+-]x% | N/A with possibly a tag that
     * surrounds it.
     */
    static sortByDiff(a, b) {
        const a0 = getPointValue(a, false);
        const b0 = getPointValue(b, false);
        if (isNumber(a0) && isNumber(b0)) {
            return Comparators.sortNum(a0, b0);
        }
        return Comparators.sortBase(a0, b0);
    }

    static getDefaultComparator(columnType) {
        let defaultComparator;

        if (columnType === 1) {
            defaultComparator = Comparators.sortNum;
        } else if (columnType === 2) {
            defaultComparator = Comparators.sortDate;
        } else {
            defaultComparator = Comparators.sortBase;
        }

        return defaultComparator;
    }
}

/**
 * Functions that pull data out of a table cell.
 */
class Extractors {
    static textExtractor($tableCell) {
        return $tableCell.text();
    }

    static tooltipExtractor($tableCell) {
        return $tableCell.find('span').attr('data-original-title');
    }

    static dateStampExtractor($tableCell) {
        return $tableCell.data('dateStamp');
    }

    static getDefaultExtractor() {
        return Extractors.textExtractor;
    }
}

class TableButtonHelpers {
    /**
     * Given a button, get the innermost table enclosing it.
     */
    static getEnclosingTable($button) {
        return $($button.parents('table')[0]);
    }

    /**
     * Given a button and an index idx,
     * find the button's column position in the table
     * where the columns are treated as idx-indexed.
     */
    static getColumnPositionOfButton($button, idx) {
        return $button.parent().children().index($button) + idx;
    }

    /**
     * Given a table, clear all the sort states.
     */
    static clearAllSortStates($table) {
        $table.find('.icon-sort').attr('class', 'icon-sort unsorted'); // clear the icons
        $table.find('.button-sort-ascending')
                .removeClass('button-sort-ascending')
                .addClass('button-sort-none');
        $table.find('.button-sort-descending')
                .removeClass('button-sort-descending')
                .addClass('button-sort-none');
    }

    /**
     * Given a button in table, set its state to sorted ascending.
     * Clear all other button states.
     */
    static setButtonToSortedAscending($button) {
        this.clearAllSortStates(this.getEnclosingTable($button));
        $button.addClass('button-sort-ascending');
        $button.find('.icon-sort').attr('class', 'icon-sort sorted-ascending'); // set the icon to ascending
    }

    /**
     * Given a button in table, set its state to sorted descending.
     * Clear all other button states.
     */
    static setButtonToSortedDescending($button) {
        this.clearAllSortStates(this.getEnclosingTable($button));
        $button.addClass('button-sort-descending');
        $button.find('.icon-sort').attr('class', 'icon-sort sorted-descending'); // set the icon to descending
    }
}

// http://stackoverflow.com/questions/7558182/sort-a-table-fast-by-its-first-column-with-javascript-or-jquery
/**
 * Sorts a table based on certain column and comparator
 *
 * @param $table
 *     A jQuery object representing the table.
 * @param colIdx
 *     The column index (1-based) as key for the sort
 * @param comparatorOrNull
 *     Function to compare two elements.
 *     May be null.
 * @param extractorOrNull
 *     Function to pull out data from a table cell for comparison.
 *     May be null.
 * @param shouldSortAscending
 *     If this is true, sort in ascending order.
 *     Otherwise, sort in descending order
 */
function sortTable($table, colIdx, comparatorOrNull, extractorOrNull, shouldSortAscending) {
    let columnType = 0;
    let store = [];
    const $rowList = $table.find('> tbody > tr');

    // Iterate through column's contents to decide which comparator to use
    for (let i = 0; i < $rowList.length; i += 1) {
        if ($rowList[i].cells[colIdx - 1] === undefined) {
            continue;
        }

        const extractor = isDefined(extractorOrNull) ? extractorOrNull : Extractors.getDefaultExtractor();

        // $.trim trims leading/trailing whitespaces
        // $rowList[i].cells[colIdx - 1] is where we get the table cell from
        const textToCompare = $.trim(extractor($($rowList[i].cells[colIdx - 1])));

        // Store rows together with the innerText to compare
        store.push([textToCompare, $rowList[i], i]);

        if ((columnType === 0 || columnType === 1) && isNumber(textToCompare)) {
            columnType = 1;
        } else if ((columnType === 0 || columnType === 2) && isDate(textToCompare)) {
            columnType = 2;
        } else {
            columnType = 3;
        }
    }

    const comparator = isDefined(comparatorOrNull) ? comparatorOrNull : Comparators.getDefaultComparator(columnType);

    store.sort((x, y) => {
        const compareResult = shouldSortAscending ? comparator(x[0].toUpperCase(), y[0].toUpperCase())
                : comparator(y[0].toUpperCase(), x[0].toUpperCase());
        if (compareResult === 0) {
            return x[2] - y[2];
        }
        return compareResult;
    });

    // Must rewrap because .get() does not return a jQuery wrapped DOM node
    // and hence does not have the .children() function
    let $tbody = $($table.get(0)).children('tbody');

    if ($tbody.size < 1) {
        $tbody = $table;
    }

    // Must push to target tbody else it will generate a new tbody for the table
    for (let j = 0; j < store.length; j += 1) {
        $tbody.get(0).appendChild(store[j][1]);
    }

    store = null;
}

/**
 * Sorts a table
 * @param sortButtonClicked
 *     The jQuery object representing the sort button that was clicked.
 * @param comparatorStringOrNull
 *     String representing the function to compare 2 elements.
 *     May be null.
 * @param extractorStringOrNull
 *     String representing the function to pull out data from a table cell for comparison.
 *     May be null.
 */
function toggleSort($button, comparatorStringOrNull, extractorStringOrNull) {
    const isSortedAscending = $button.hasClass('button-sort-ascending');

    const $table = TableButtonHelpers.getEnclosingTable($button);
    const colIdx = TableButtonHelpers.getColumnPositionOfButton($button, 1);
    const comparatorOrNull = isDefined(comparatorStringOrNull) ? Comparators[comparatorStringOrNull] : null;
    const extractorOrNull = isDefined(extractorStringOrNull) ? Extractors[extractorStringOrNull] : null;
    const shouldSortAscending = !isSortedAscending;

    sortTable($table, colIdx, comparatorOrNull, extractorOrNull, shouldSortAscending);

    // update the button and icon states
    if (shouldSortAscending) {
        TableButtonHelpers.setButtonToSortedAscending($button);
    } else {
        TableButtonHelpers.setButtonToSortedDescending($button);
    }
}

export {
    Comparators, // for test
    getPointValue, // for test
    toggleSort,
};
