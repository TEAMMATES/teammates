function roundToThreeDp(num) {
    return parseFloat(num.toFixed(3));
}

function updateNumScalePossibleValues(questionNum) {
    const min = parseInt($(`#minScaleBox-${questionNum}`).val(), 10);
    let max = parseInt($(`#maxScaleBox-${questionNum}`).val(), 10);
    let step = parseFloat($(`#stepBox-${questionNum}`).val());

    if (max <= min) {
        max = min + 1;
        $(`#maxScaleBox-${questionNum}`).val(max);
    }

    step = roundToThreeDp(step);
    if (step === 0) {
        step = 0.001;
    }

    const $stepBox = $(`#stepBox-${questionNum}`);
    $stepBox.val(Number.isNaN(step) ? '' : step);

    const possibleValuesCount = Math.floor(roundToThreeDp((max - min) / step)) + 1;
    const largestValueInRange = min + (possibleValuesCount - 1) * step;
    const $numScalePossibleValues = $(`#numScalePossibleValues-${questionNum}`);
    let possibleValuesString;
    if (roundToThreeDp(largestValueInRange) !== max) {
        $numScalePossibleValues.css('color', 'red');

        if (Number.isNaN(min) || Number.isNaN(max) || Number.isNaN(step)) {
            possibleValuesString = '[Please enter valid numbers for all the options.]';
        } else {
            possibleValuesString = `[The interval ${min.toString()} - ${max.toString()}`
                    + ' is not divisible by the specified increment.]';
        }

        $numScalePossibleValues.text(possibleValuesString);
        return false;
    }
    $numScalePossibleValues.css('color', 'black');
    possibleValuesString = '[Based on the above settings, acceptable responses are: ';

    // step is 3 d.p. at most, so round it after * 1000.
    if (possibleValuesCount > 6) {
        possibleValuesString +=
            `${min.toString()}, ${(Math.round((min + step) * 1000) / 1000).toString()},
            ${(Math.round((min + 2 * step) * 1000) / 1000).toString()}, ...,
            ${(Math.round((max - 2 * step) * 1000) / 1000).toString()},
            ${(Math.round((max - step) * 1000) / 1000).toString()}, ${max.toString()}`;
    } else {
        possibleValuesString += min.toString();
        let cur = min + step;
        while (max - cur >= -1e-9) {
            possibleValuesString += `, ${(Math.round(cur * 1000) / 1000).toString()}`;
            cur += step;
        }
    }

    possibleValuesString += ']';
    $numScalePossibleValues.text(possibleValuesString);
    return true;
}

export {
    roundToThreeDp, // for test
    updateNumScalePossibleValues,
};
