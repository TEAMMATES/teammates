import {
    ParamsNames,
} from './const';

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
    return `${ParamsNames.SESSION_TOKEN}=${getCookie(ParamsNames.SESSION_TOKEN)}`;
}

function updateCsrfTokenInInputFields() {
    const updatedToken = getCookie(ParamsNames.SESSION_TOKEN);
    if (!updatedToken) {
        return;
    }
    $(`input[name=${ParamsNames.SESSION_TOKEN}]`).val(updatedToken);
}

export {
    makeCsrfTokenParam,
    updateCsrfTokenInInputFields,
};
