var TimeZone = {
    /**
     * Generate time zone <options> using time zone IDs from Moment-Timezone library.
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
     * and updates the relevant field.
     */
    autoDetectAndUpdateTimeZone: function($selectElement) {
        var detectedTimeZone = moment.tz.guess();
        TimeZone.updateTimeZone($selectElement, detectedTimeZone);
    },

    updateTimeZone: function($selectElement, timeZone) {
        $selectElement.val(timeZone);
    }
};
