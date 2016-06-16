/**
 * Common functions used by the various question*.js
 */

function getQuestionIdSuffix(questionNum) {
    var isValidQuestionNumber = questionNum > 0 || questionNum === NEW_QUESTION;
    
    var idSuffix = isValidQuestionNumber ? '-' + questionNum : '';
    return idSuffix;
}

