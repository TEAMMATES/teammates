import type {
  FeedbackContributionCourseWideStatistics,
  FeedbackContributionQuestionDetails,
  FeedbackContributionResponseDetails,
  FeedbackTextResponseDetails,
  SessionResults,
} from '../../types/api-output';
import {
  FeedbackQuestionType,
  FeedbackQuestionResultsStatisticsView,
  FeedbackVisibilityType,
  NumberOfEntitiesToGiveFeedbackToSetting,
  QuestionGiverType,
  QuestionRecipientType,
} from '../../types/api-output';

const student3UserId = '09f330c2-8b2d-4dd4-a8e5-6ec511418ff2';
const student4UserId = 'e4589528-3337-451a-9b47-3f851f834775';

const feedbackSessionResultsContribResultsRestrictedSections: SessionResults = {
  questions: [
    {
      feedbackQuestion: {
        feedbackQuestionId: `agR0ZXN0chYLEhBGZWVkYmFja1F1ZXN0aW9uGD8M`,
        questionNumber: 1,
        questionBrief: `How much has each team member including yourself, contributed to the project?`,
        questionDetails: {
          isNotSureAllowed: true,
          questionType: FeedbackQuestionType.CONTRIB,
          questionText: `How much has each team member including yourself, contributed to the project?`,
        } as FeedbackContributionQuestionDetails,
        questionType: FeedbackQuestionType.CONTRIB,
        giverType: QuestionGiverType.STUDENTS,
        recipientType: QuestionRecipientType.OWN_TEAM_MEMBERS_INCLUDING_SELF,
        numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
        showResponsesTo: [FeedbackVisibilityType.INSTRUCTORS],
        showGiverNameTo: [FeedbackVisibilityType.INSTRUCTORS],
        showRecipientNameTo: [FeedbackVisibilityType.INSTRUCTORS],
        questionDescription: ``,
        customNumberOfEntitiesToGiveFeedbackTo: 0,
      },
      questionStatistics: {
        questionType: FeedbackQuestionType.CONTRIB,
        statisticsView: FeedbackQuestionResultsStatisticsView.COURSE_WIDE,
        rows: [
          {
            teamName: `Team 2`,
            recipientName: `student3 In Course With Sections`,
            recipientEmail: `student3InCourseWithSections@gmail.tmt`,
            claimed: 100,
            perceived: -9999,
            diff: -9999,
            ratingsReceived: [],
          },
        ],
      } as FeedbackContributionCourseWideStatistics,
      allResponses: [
        {
          isMissingResponse: false,
          responseId: `agR0ZXN0chYLEhBGZWVkYmFja1F1ZXN0aW9uGD8M%student3InCourseWithSections@gmail.tmt%student3InCourseWithSections@gmail.tmt`,
          giver: `student3 In Course With Sections`,
          userIdForModeration: student3UserId,
          giverUserId: student3UserId,
          giverTeam: `Team 2`,
          giverEmail: `student3InCourseWithSections@gmail.tmt`,
          giverSection: `Section 2`,
          recipient: `student3 In Course With Sections`,
          recipientUserId: student3UserId,
          recipientTeam: `Team 2`,
          recipientEmail: `student3InCourseWithSections@gmail.tmt`,
          recipientSection: `Section 2`,
          responseDetails: {
            answer: 100,
            questionType: FeedbackQuestionType.CONTRIB,
          } as FeedbackContributionResponseDetails,
          instructorComments: [],
        },
        {
          isMissingResponse: true,
          responseId: `agR0ZXN0chYLEhBGZWVkYmFja1F1ZXN0aW9uGD8M%student4InCourseWithSections@gmail.tmt%student4InCourseWithSections@gmail.tmt`,
          giver: `student4 In Course With Sections`,
          userIdForModeration: student4UserId,
          giverUserId: student4UserId,
          giverTeam: `Team 3`,
          giverEmail: `student4InCourseWithSections@gmail.tmt`,
          giverSection: `Section 3`,
          recipient: `student4 In Course With Sections`,
          recipientUserId: student4UserId,
          recipientTeam: `Team 3`,
          recipientEmail: `student4InCourseWithSections@gmail.tmt`,
          recipientSection: `Section 3`,
          responseDetails: {
            answer: `No Response`,
            questionType: FeedbackQuestionType.TEXT,
          } as FeedbackTextResponseDetails,
          instructorComments: [],
        },
      ],
    },
  ],
};

export default feedbackSessionResultsContribResultsRestrictedSections;
