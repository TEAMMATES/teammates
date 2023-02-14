# Documentation of Work:

## Onboarding

## Part 1: Complexity Measurement
We used `lizard` to inspect the complexity of the repository. The results were collected to google [sheets](https://docs.google.com/spreadsheets/d/1pyIgCouwoHa9f4Q0DBhj_9gXWa7iI7MTI_X0eoj8KbU/edit#gid=1684196533). From the analyzed methods, the following ten methods were deemed sufficiently complex:

| Cyclomatic Complexity | LOC | Method                                                                           |
|-----------------------|-----|----------------------------------------------------------------------------------|
|                    21 |  40 | BasicFeedbackSubmissionAction::getRecipientSection                               |
|                    19 |  76 | FeedbackSessionsDb::updateFeedbackSession                                        |
|                    19 |  60 | FeedbackResponsesLogic::isFeedbackParticipantNameVisibleToUser                   |
|                    18 |  59 | FeedbackMcqQuestionDetails::validateQuestionDetails                              |
|                    17 |  88 | GetFeedbackSessionLogsAction::execute                                            |
|                    17 |  68 | SessionResultsData::buildSingleResponseForStudent                                |
|                    17 |  52 | FeedbackResponseCommentsLogic::isFeedbackParticipantNameVisibleToUser            |
|                    16 |  82 | FeedbackRankRecipientsResponseDetails::getUpdateOptionsForRankRecipientQuestions |
|                    15 |  50 | SessionResultsData::initForStudent                                               |
|                    15 |  41 | FeedbackMsqQuestionDetails::shouldChangesRequireResponseDeletion                 |

Out of these, the following five methods were chosen for manual complexity calculation:

| Cyclomatic Complexity | LOC | Method                                                                           | Manualy Calculate Complexity |
|-----------------------|-----|----------------------------------------------------------------------------------|------------------------------|
|                    17 |  68 | SessionResultsData::buildSingleResponseForStudent                                | Name: calc, Name2: calc2     |
|                    17 |  52 | FeedbackResponseCommentsLogic::isFeedbackParticipantNameVisibleToUser            |                              |
|                    16 |  82 | FeedbackRankRecipientsResponseDetails::getUpdateOptionsForRankRecipientQuestions |                              |
|                    15 |  50 | SessionResultsData::initForStudent                                               |                              |
|                    15 |  41 | FeedbackMsqQuestionDetails::shouldChangesRequireResponseDeletion                 |                              |
