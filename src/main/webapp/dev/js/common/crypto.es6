/**
 * Returns the value of a cookie given its name.
 * Returns null if the cookie is not set.
 */
function getCookie(cookieNameToFind) {
    const cookies = document.cookie.split('; ').map(s => s.split('='));

    for (let i = 0; i < cookies.length; i += 1) {
        const cookieName = cookies[i][0];
        const cookieValue = cookies[i][1];

        // the cookie was found in the ith iteration
        if (cookieName === cookieNameToFind) {
            return cookieValue;
        }
    }

    // the cookie was not found
    return null;
}

function makeCsrfTokenParam() {
    const tokenParamName = 'token';
    const tokenCookieName = 'token';
    const tokenCookieValue = getCookie(tokenCookieName);

    return `${tokenParamName}=${tokenCookieValue}`;
}

/*
exported makeCsrfTokenParam
*/
