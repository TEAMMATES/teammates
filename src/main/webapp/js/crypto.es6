/* global getCookie:false */

function makeCsrfTokenParam() {
    const tokenParamName = 'token';
    const tokenCookieName = 'token';
    const tokenCookieValue = getCookie(tokenCookieName);

    return `${tokenParamName}=${tokenCookieValue}`;
}

/*
exported makeCsrfTokenParam
*/
