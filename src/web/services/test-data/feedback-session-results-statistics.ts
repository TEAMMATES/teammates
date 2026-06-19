import type { FeedbackMcqQuestionDetails, FeedbackMcqResponseDetails, SessionResults } from '../../types/api-output';
import {
  FeedbackQuestionType,
  FeedbackVisibilityType,
  NumberOfEntitiesToGiveFeedbackToSetting,
  QuestionGiverType,
  QuestionRecipientType,
} from '../../types/api-output';

const feedbackSessionResultsStatistics: SessionResults = {
  questions: [
    {
      feedbackQuestion: {
        feedbackQuestionId: `agR0ZXN0chYLEhBGZWVkYmFja1F1ZXN0aW9uGBYM`,
        questionNumber: 1,
        questionBrief: `What is the best selling point of your product?`,
        questionDetails: {
          hasAssignedWeights: false,
          mcqWeights: [],
          mcqOtherWeight: 0,
          mcqChoices: [`Price`, `Quality`],
          otherEnabled: false,
          generateOptionsFor: QuestionRecipientType.NONE,
          questionType: FeedbackQuestionType.MCQ,
          questionText: `What is the best selling point of your product?`,
        } as unknown as FeedbackMcqQuestionDetails,
        questionType: FeedbackQuestionType.MCQ,
        giverType: QuestionGiverType.STUDENTS,
        recipientType: QuestionRecipientType.SELF,
        numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.CUSTOM,
        customNumberOfEntitiesToGiveFeedbackTo: 1,
        showResponsesTo: [FeedbackVisibilityType.INSTRUCTORS],
        showGiverNameTo: [FeedbackVisibilityType.INSTRUCTORS],
        showRecipientNameTo: [FeedbackVisibilityType.INSTRUCTORS],
        questionDescription: ``,
      },
      questionStatistics: undefined,
      allResponses: [
        {
          isMissingResponse: false,
          responseId: `agR0ZXN0chYLEhBGZWVkYmFja1F1ZXN0aW9uGBYM%student1InCourse1@gmail.tmt%student1InCourse1@gmail.tmt`,
          giver: `student1 In Course1</td></div>'"`,
          userIdForModeration: `c35dc7b8-ee63-48b3-93dc-d781803f9a03`,
          giverTeam: `Team 1.1</td></div>'"`,
          giverEmail: `student1InCourse1@gmail.tmt`,
          giverSection: `Section 1`,
          recipient: `student1 In Course1</td></div>'"`,
          recipientTeam: `Team 1.1</td></div>'"`,
          recipientEmail: `student1InCourse1@gmail.tmt`,
          recipientSection: `Section 1`,
          responseDetails: {
            answer: `Quality`,
            isOther: false,
            otherFieldContent: ``,
            questionType: FeedbackQuestionType.MCQ,
          } as FeedbackMcqResponseDetails,
          instructorComments: [],
        },
      ],
    },
  ],
};

export default feedbackSessionResultsStatistics;
