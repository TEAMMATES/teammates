import { FeedbackConstantSumOptionsResponseDetails, FeedbackQuestionType } from '../../../../../types/api-output';
import { Response } from '../../../../../types/question-statistics.model';

export const constsumOptionQuestionResponses = {
  responses: [
    {
      giver: 'Alice',
      giverTeam: 'Team 1',
      giverEmail: 'alice@gmail.com',
      giverSection: '',
      recipient: 'Alice',
      recipientTeam: 'Team 1',
      recipientEmail: 'alice@gmail.com',
      recipientSection: '',
      responseDetails: {
        answers: [50, 50, 0],
        questionType: FeedbackQuestionType.CONSTSUM_OPTIONS,
      },
    },
    {
      giver: 'Bob',
      giverTeam: 'Team 2',
      giverEmail: 'bob@gmail.com',
      giverSection: '',
      recipient: 'Bob',
      recipientTeam: 'Team 2',
      recipientEmail: 'bob@gmail.com',
      recipientSection: '',
      responseDetails: {
        answers: [30, 70, 0],
        questionType: FeedbackQuestionType.CONSTSUM_OPTIONS,
      },
    },
    {
      giver: 'Charles',
      giverTeam: 'Team 1',
      giverEmail: 'charles@gmail.com',
      giverSection: '',
      recipient: 'Charles',
      recipientTeam: 'Team 1',
      recipientEmail: 'charles@gmail.com',
      recipientSection: '',
      responseDetails: {
        answers: [10, 90, 0],
        questionType: FeedbackQuestionType.CONSTSUM_OPTIONS,
      },
    },
  ] as Response<FeedbackConstantSumOptionsResponseDetails>[],
} satisfies {
  responses: Response<FeedbackConstantSumOptionsResponseDetails>[];
};

export default constsumOptionQuestionResponses;
