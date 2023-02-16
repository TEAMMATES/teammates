# Complexity analysis

### Questions answered for the 10 selected high complexity functions
1. What are your results? Did everyone get the same result? Is there something that is unclear? If you have a tool, is its result the same as yours?
2. Are the functions/methods with high CC also very long in terms of LOC?
3. What is the purpose of these functions? Is it related to the high CC? 
4. If your programming language uses exceptions: 

- Are they taken into account by the tool? 
- If you think of an exception as another possible branch (to the catch block or the end of the function), how is the CC affected? 

5. Is the documentation of the function clear about the different possible outcomes induced by different branches taken?
___________________

### Function 1: `BasicFeedbackSubmissionAction::getRecipientSection`
1. This function's complexity was not calculated by hand.
2. The number of LOC is not significantly high.
3. The purpose of this function is to get a section of a recipient of a feedback action. Depending on the value of `giverType`, i.e. the type of user that is giving feedback, and the value of `recipientType`, the outcome of the function differs. Because there are many different combinations of these values, the complexity is high.
4. There are no exceptions. However, the function includes assertions that seem to be included in the count performed by `lizard`.
5. The documentation in the code does not contain any information of the outcomes.


### Function 2: `FeedbackSessionsDb::updateFeedbackSession`
1.
2.
3.
4.
5.


### Function 3: `FeedbackResponsesLogic::isFeedbackParticipantNameVisibleToUser`
1.
2.
3.
4.
5.

### Function 4: `FeedbackMcqQuestionDetails::validateQuestionDetails`
1.
2.
3.
4.
5.

### Function 5: `GetFeedbackSessionLogsAction::execute`
1.
2.
3.
4.
5.

### Function 6: `SessionResultsData::buildSingleResponseForStudent`
1.
2.
3.
4.
5.


### Function 7: `FeedbackResponseCommentsLogic::isFeedbackParticipantNameVisibleToUser`
1.
2.
3.
4.
5.


### Function 8: `FeedbackRankRecipientsResponseDetails::getUpdateOptionsForRankRecipientQuestions`
1.
2.
3.
4.
5.

### Function 9: `SessionResultsData::initForStudent`
1.
2.
3.
4.
5.

### Function 10: `FeedbackMsqQuestionDetails::shouldChangesRequireResponseDeletion`
1.
2.
3.
4.
5.