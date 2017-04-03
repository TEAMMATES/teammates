'use strict';

var TimeZone = {
    /**
     * Generate time zone <option>s using time zone IDs from Moment-Timezone library
     * and appends it under the specified element.
     */
    prepareTimeZoneInput: function($selectElement) {
        function displayUtcOffset(offset) {
            if (offset === 0) {
                return 'UTC';
            }
            var hr = Math.floor(Math.abs(offset / 60));
            var min = Math.abs(offset) % 60;
            // offset is calculated as the number of minutes needed to get to UTC
            // thus the +/- sign needs to be swapped
            return 'UTC ' + (offset < 0 ? '+' : '-') + addLeadingZeroes(hr) + ':' + addLeadingZeroes(min);
        }

        function addLeadingZeroes(num) {
            return (num > 9 ? '' : '0') + num;
        }

        moment.tz.names().forEach(function(name) {
            var o = document.createElement('option');
            var date = new Date();
            var offset = moment.tz.zone(name).offset(date);
            o.text = name + ' (' + displayUtcOffset(offset) + ')';
            o.value = name;
            $selectElement.append(o);
        });
    },

    /**
     * Automatically detects the user's time zone based on the local settings
     * and updates the specified <select> field.
     */
    autoDetectAndUpdateTimeZone: function($selectElement) {
        var detectedTimeZone = moment.tz.guess();
        TimeZone.updateTimeZone($selectElement, detectedTimeZone);
    },

    /**
     * Updates the specified <select> field with the chosen time zone.
     */
    updateTimeZone: function($selectElement, timeZone) {
        if (moment.tz.names().indexOf(timeZone) !== -1) {
            $selectElement.val(timeZone);
        }
    }
};
