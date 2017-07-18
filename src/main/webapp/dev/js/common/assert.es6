class AssertionFailedError extends Error {
}

function assert(condition, message) {
    const msg = message === undefined || message === null ? 'Assertion Failed' : message;
    if (!condition) {
        throw new AssertionFailedError(msg);
    }
}

function assertDefined(expr, message) {
    assert(expr !== undefined && expr != null, message);
}

export {
    AssertionFailedError,
    assert,
    assertDefined,
};
