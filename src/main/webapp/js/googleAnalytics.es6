/* eslint-disable no-underscore-dangle */ // The variable name is determined by external library (googleAnalytics)
const _gaq = _gaq || []; // eslint-disable-line no-use-before-define
_gaq.push(['_setAccount', 'UA-37652587-1']);
_gaq.push(['_trackPageview']);

(function () {
    const ga = document.createElement('script');
    ga.type = 'text/javascript';
    ga.async = true;
    // Always use the ssl version, if not test will fail as local testing uses non HTTPs by default
    ga.src = 'https://ssl.google-analytics.com/ga.js';
    const scripts = document.getElementsByTagName('script');
    for (let i = 0; i < scripts.length; i += 1) {
        const s = scripts.item(i);
        if (s.src.endsWith('googleAnalytics.js')) {
            s.parentNode.insertBefore(ga, s);
            break;
        }
    }
}());
