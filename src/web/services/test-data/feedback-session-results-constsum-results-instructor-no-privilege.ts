import type {
  FeedbackConstantSumOptionsQuestionDetails,
  FeedbackConstantSumRecipientsQuestionDetails,
  SessionResults,
} from '../../types/api-output';
import {
  FeedbackQuestionType,
  FeedbackVisibilityType,
  NumberOfEntitiesToGiveFeedbackToSetting,
  QuestionGiverType,
  QuestionRecipientType,
} from '../../types/api-output';

const feedbackSessionResultsConstsumResultsInstructorNoPrivilege: SessionResults = {
  questions: [
    {
      feedbackQuestion: {
        feedbackQuestionId: `agR0ZXN0chYLEhBGZWVkYmFja1F1ZXN0aW9uGDIM`,
        questionNumber: 2,
        questionBrief: `Split points among the teams`,
        questionDetails: {
          pointsPerOption: true,
          forceUnevenDistribution: false,
          distributePointsFor: `None`,
          points: 100,
          questionType: FeedbackQuestionType.CONSTSUM_RECIPIENTS,
          questionText: `Split points among the teams`,
        } as FeedbackConstantSumRecipientsQuestionDetails,
        questionType: FeedbackQuestionType.CONSTSUM_RECIPIENTS,
        giverType: QuestionGiverType.INSTRUCTORS,
        recipientType: QuestionRecipientType.TEAMS,
        numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.CUSTOM,
        customNumberOfEntitiesToGiveFeedbackTo: 2,
        showResponsesTo: [FeedbackVisibilityType.INSTRUCTORS],
        showGiverNameTo: [FeedbackVisibilityType.INSTRUCTORS],
        showRecipientNameTo: [FeedbackVisibilityType.INSTRUCTORS],
        questionDescription: ``,
      },
      questionStatistics: ``,
      allResponses: [],
    },
    {
      feedbackQuestion: {
        feedbackQuestionId: `agR0ZXN0chYLEhBGZWVkYmFja1F1ZXN0aW9uGDQM`,
        questionNumber: 3,
        questionBrief: `How much has each student worked?`,
        questionDetails: {
          pointsPerOption: true,
          forceUnevenDistribution: false,
          distributePointsFor: `None`,
          points: 100,
          questionType: FeedbackQuestionType.CONSTSUM_RECIPIENTS,
          questionText: `How much has each student worked?`,
        } as FeedbackConstantSumRecipientsQuestionDetails,
        questionType: FeedbackQuestionType.CONSTSUM_RECIPIENTS,
        giverType: QuestionGiverType.INSTRUCTORS,
        recipientType: QuestionRecipientType.STUDENTS,
        numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.CUSTOM,
        customNumberOfEntitiesToGiveFeedbackTo: 5,
        showResponsesTo: [FeedbackVisibilityType.INSTRUCTORS],
        showGiverNameTo: [FeedbackVisibilityType.INSTRUCTORS],
        showRecipientNameTo: [FeedbackVisibilityType.INSTRUCTORS],
        questionDescription: ``,
      },
      questionStatistics: ``,
      allResponses: [],
    },
    {
      feedbackQuestion: {
        feedbackQuestionId: `agR0ZXN0chYLEhBGZWVkYmFja1F1ZXN0aW9uGDoM`,
        questionNumber: 1,
        questionBrief: `How important are the following factors to you? Give points accordingly.`,
        questionDetails: {
          constSumOptions: [`Grades`, `Fun`],
          pointsPerOption: false,
          forceUnevenDistribution: false,
          distributePointsFor: `None`,
          points: 100,
          questionType: FeedbackQuestionType.CONSTSUM_OPTIONS,
          questionText: `How important are the following factors to you? Give points accordingly.`,
        } as FeedbackConstantSumOptionsQuestionDetails,
        questionType: FeedbackQuestionType.CONSTSUM_OPTIONS,
        giverType: QuestionGiverType.STUDENTS,
        recipientType: QuestionRecipientType.SELF,
        numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.CUSTOM,
        customNumberOfEntitiesToGiveFeedbackTo: 1,
        showResponsesTo: [FeedbackVisibilityType.INSTRUCTORS],
        showGiverNameTo: [FeedbackVisibilityType.INSTRUCTORS],
        showRecipientNameTo: [FeedbackVisibilityType.INSTRUCTORS],
        questionDescription: ``,
      },
      questionStatistics: ``,
      allResponses: [],
    },
  ],
};

export default feedbackSessionResultsConstsumResultsInstructorNoPrivilege;
