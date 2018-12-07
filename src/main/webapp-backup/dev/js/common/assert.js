class AssertionFailedError extends Error {
}

function assert(condition, message) {
    if (!condition) {
        throw new AssertionFailedError(message || 'Assertion Failed');
    }
}

function assertDefined(expr, message) {
    assert(expr !== undefined && expr !== null, message);
}

export {
    AssertionFailedError,
    assert,
    assertDefined,
};
