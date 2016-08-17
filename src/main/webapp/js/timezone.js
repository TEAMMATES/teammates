var TimeZone = {
    /**
     * Generate time zone <option>s using time zone IDs from Moment-Timezone library
     * and appends it under the specified element.
     */
    prepareTimeZoneInput: function($selectElement) {
        moment.tz.names().forEach(function(name) {
            var o = document.createElement('option');
            o.text = name;
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
