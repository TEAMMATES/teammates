'use strict';

/* eslint-disable no-underscore-dangle */ // The variable name is determined by external library (googleAnalytics)
var _gaq = _gaq || [];
_gaq.push(['_setAccount', 'UA-37652587-1']);
_gaq.push(['_trackPageview']);

(function() {
    var ga = document.createElement('script');
    ga.type = 'text/javascript';
    ga.async = true;
    // Always use the ssl version, if not test will fail as local testing uses non HTTPs by default
    ga.src = 'https://ssl.google-analytics.com/ga.js';
    var scripts = document.getElementsByTagName('script');
    for (var i = 0; i < scripts.length; i++) {
        var s = scripts.item(i);
        if (s.src.endsWith('googleAnalytics.js')) {
            s.parentNode.insertBefore(ga, s);
            break;
        }
    }
})();
