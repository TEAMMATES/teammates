/* global moment:false */

import {
    showModalAlert,
} from './bootboxWrapper';

import {
    BootstrapContextualColors,
} from './const';

function isSupportedByJava(name) {
    // These short timezones are not supported by Java
    const badZones = {
        EST: true, 'GMT+0': true, 'GMT-0': true, HST: true, MST: true, ROC: true,
    };
    return !badZones[name];
}

const TimeZone = {
    /**
     * Generate time zone <option>s using time zone IDs from Moment-Timezone library
     * and appends it under the specified element.
     */
    prepareTimeZoneInput($selectElement) {
        function addLeadingZeroes(num) {
            return (num > 9 ? '' : '0') + num;
        }

        function displayUtcOffset(offset) {
            if (offset === 0) {
                return 'UTC';
            }
            const hr = Math.floor(Math.abs(offset / 60));
            const min = Math.abs(offset) % 60;
            // offset is calculated as the number of minutes needed to get to UTC
            // thus the +/- sign needs to be swapped
            return `UTC${offset < 0 ? '+' : '-'}${addLeadingZeroes(hr)}:${addLeadingZeroes(min)}`;
        }

        moment.tz.names().filter(isSupportedByJava).forEach((name) => {
            const o = document.createElement('option');
            const date = new Date();
            const offset = moment.tz.zone(name).utcOffset(date);
            o.text = `${name} (${displayUtcOffset(offset)})`;
            o.value = name;
            $selectElement.append(o);
        });
    },

    /**
     * Automatically detects the user's time zone based on the local settings
     * and updates the specified <select> field.
     */
    autoDetectAndUpdateTimeZone($selectElement) {
        const detectedTimeZone = moment.tz.guess();
        TimeZone.updateTimeZone($selectElement, detectedTimeZone);
    },

    /**
     * Updates the specified <select> field with the chosen time zone.
     * If the chosen time zone is unrecognized, the field is updated with
     * an auto-detected time zone instead and a modal alert is displayed.
     */
    updateTimeZone($selectElement, timeZone) {
        if (moment.tz.names().filter(isSupportedByJava).indexOf(timeZone) === -1) {
            const detectedTimeZone = moment.tz.guess();
            $selectElement.val(detectedTimeZone);
            showModalAlert('We are switching to geographical time zones',
                    `This session is using the fixed offset time zone ${timeZone}. TEAMMATES now uses ` +
                    'time zones based on geographical location, with support for daylight saving time. ' +
                    `We have detected that your geographical time zone is ${detectedTimeZone}. ` +
                    'Please verify that this is correct, then save the session.',
                    null, BootstrapContextualColors.WARNING);
            return;
        }
        $selectElement.val(timeZone);
    },
};

export {
    TimeZone,
};
