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
1.
2.
3.
4.
5.


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
1. This function's complexity was not one of the functions calculated by hand.
2. This function has 59 LOC, but most of it is comments. It’s a fairly short function with a lot of if-statements.
3. The purpose of the function is to check that every question is entered correctly. Since there are many options that need to be checked, the complexity will be high. However, the branching is very shallow, so the function is not experienced as complex.
4. There are no exceptions or assertions in the function.
5. This function is well documented in its different paths. Almost all if-statements has a comment explaining the branching.

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