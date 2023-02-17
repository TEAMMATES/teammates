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
1. This function's complexity was not calculated by hand.
2. The number of LOC is quite high in relation to the CC, but not surprisingly high.
3. The purpose of the function is to update a feedback session, and the high complexity is a result of multiple values that are being checked if they have changed and should be updated.
4. The function throws exceptions, and these do not seem to be included as exit points. The functions do not include try/catch blocks.
5. There is no explicit documentation of the different possible outcomes of the branches, however, the code is pretty self-explanatory.


### Function 3: `FeedbackResponsesLogic::isFeedbackParticipantNameVisibleToUser`
1. This function's complexity was not one of the functions calculated by hand.
2. The function has 60 LOC which I consider to be too long. There is a lot of exit points in the function that reduces the CC. The functions almost exclusively consists of if, else, case, switch, return, break.
3. As given in the name, it’s a function that checks if the participant receiving the feedback is visible to the user.  Without have used the software, I assume there is a high complexity if things should be visible or not because of privacy and integrity.
4. There are no exceptions. However, the function includes assertions that seem to be included in the count performed by lizard.
5. There is none documentation of the function and the different outcomes.

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
1. Both of the people that worked on it got the same result: CC=16. We used lizard to calculate the CC of the function, and same result was shown. 
2. This function has 82 LOC, which is fairly large for one function. I beilieve it would make sens to try to divide the method into submethods, as the function seems to do multiple steps(4 detected). Given that there's only 3 possible exit points, the function has a high CC of 16.
3. The purpose of the function is to provide updates of responses for 'rank recipient question', such that the ranks in the responses are consistent. It's natural to have a high CC as the function provdes the updates which seem to be immutable, hence a builder has been implemented
4. The are no try catch blocks, however one assertion is made, which seems to be taken into account.
5. The documentation is fairly clear about possible outcomes induced. However, condition statements are poorly commented, but the condition itself can be understood with the names of the variables used.

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