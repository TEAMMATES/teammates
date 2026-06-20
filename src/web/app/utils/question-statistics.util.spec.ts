import {
  RankOptionsQuestionStatistics,
  RankRecipientsQuestionStatistics,
  Response,
} from '../../types/question-statistics.model';
import {
  FeedbackQuestionType,
  FeedbackRankOptionsQuestionDetails,
  FeedbackRankOptionsResponseDetails,
  FeedbackRankRecipientsResponseDetails,
  QuestionRecipientType,
} from '../../types/api-output';
import {
  calculateRankOptionsQuestionStatistics,
  calculateRankRecipientsQuestionStatistics,
} from './question-statistics.util';
import rankOptionQuestionResponses from '../components/question-types/question-statistics/test-data/rank-option-question-responses';

describe('Question Statistics Utility Functions', () => {
  describe('calculateRankOptionsQuestionStatistics', () => {
    it('should calculate statistics correctly', () => {
      const question: FeedbackRankOptionsQuestionDetails = {
        questionType: FeedbackQuestionType.RANK_OPTIONS,
        questionText: 'Rank these options',
        options: ['optionA', 'optionB', 'optionC', 'optionD'],
        minOptionsToBeRanked: 1,
        maxOptionsToBeRanked: 4,
        areDuplicatesAllowed: false,
      };
      const responses = rankOptionQuestionResponses.responses;
      const stats = calculateRankOptionsQuestionStatistics(question, responses);

      const expectedRankReceivedPerOption: Record<string, number[]> = {
        optionA: [1, 2, 4],
        optionB: [2, 3, 3],
        optionC: [1, 2, 3],
        optionD: [1, 4, 4],
      };

      const expectedRankPerOption: Record<string, number> = {
        optionA: 2,
        optionB: 3,
        optionC: 1,
        optionD: 4,
      };

      expect(stats.ranksReceivedPerOption).toEqual(expectedRankReceivedPerOption);
      expect(stats.rankPerOption).toEqual(expectedRankPerOption);
    });

    it('should calculate statistics correctly if there are equal ranks', () => {
      const question: FeedbackRankOptionsQuestionDetails = {
        questionType: FeedbackQuestionType.RANK_OPTIONS,
        questionText: 'Rank these options',
        options: ['optionA', 'optionB', 'optionC', 'optionD'],
        minOptionsToBeRanked: 1,
        maxOptionsToBeRanked: 4,
        areDuplicatesAllowed: false,
      };
      const responses: Response<FeedbackRankOptionsResponseDetails>[] = rankOptionQuestionResponses.responsesSameRank;

      const stats: RankOptionsQuestionStatistics = calculateRankOptionsQuestionStatistics(question, responses);

      const expectedRankReceivedPerOption: Record<string, number[]> = {
        optionA: [1, 2, 4],
        optionB: [1, 2, 3],
        optionC: [1, 2, 3],
        optionD: [3, 4, 4],
      };

      const expectedRankPerOption: Record<string, number> = {
        optionA: 3,
        optionB: 1,
        optionC: 1,
        optionD: 4,
      };

      expect(stats.ranksReceivedPerOption).toEqual(expectedRankReceivedPerOption);
      expect(stats.rankPerOption).toEqual(expectedRankPerOption);
    });
  });

  describe('calculateRankRecipientsQuestionStatistics', () => {
    it('should not calculate team rank if recipient type is invalid', () => {
      const testResponses: Response<FeedbackRankRecipientsResponseDetails>[] = [
        {
          giver: 'alice',
          giverTeam: 'Team 1',
          giverSection: 'Tutorial 1',
          recipient: 'bob',
          recipientTeam: 'Team 1',
          recipientSection: 'Tutorial 1',
          responseDetails: {
            answer: 1,
            questionType: FeedbackQuestionType.RANK_RECIPIENTS,
          },
        },
        {
          giver: 'bob',
          giverTeam: 'Team 1',
          giverSection: 'Tutorial 1',
          recipient: 'alice',
          recipientTeam: 'Team 1',
          recipientSection: 'Tutorial 1',
          responseDetails: {
            answer: 2,
            questionType: FeedbackQuestionType.RANK_RECIPIENTS,
          },
        },
        {
          giver: 'charlie',
          giverTeam: 'Team 2',
          giverSection: 'Tutorial 1',
          recipient: 'delta',
          recipientTeam: 'Team 2',
          recipientSection: 'Tutorial 1',
          responseDetails: {
            answer: 2,
            questionType: FeedbackQuestionType.RANK_RECIPIENTS,
          },
        },
        {
          giver: 'delta',
          giverTeam: 'Team 2',
          giverSection: 'Tutorial 1',
          recipient: 'charlie',
          recipientTeam: 'Team 2',
          recipientSection: 'Tutorial 1',
          responseDetails: {
            answer: 1,
            questionType: FeedbackQuestionType.RANK_RECIPIENTS,
          },
        },
        {
          giver: 'charlie',
          giverTeam: 'Team 2',
          giverSection: 'Tutorial 1',
          recipient: 'charlie',
          recipientTeam: 'Team 2',
          recipientSection: 'Tutorial 1',
          responseDetails: {
            answer: 3,
            questionType: FeedbackQuestionType.RANK_RECIPIENTS,
          },
        },
        {
          giver: 'delta',
          giverTeam: 'Team 2',
          giverSection: 'Tutorial 1',
          recipient: 'delta',
          recipientTeam: 'Team 2',
          recipientSection: 'Tutorial 1',
          responseDetails: {
            answer: 1,
            questionType: FeedbackQuestionType.RANK_RECIPIENTS,
          },
        },
        {
          giver: 'elliot',
          giverTeam: 'Team 2',
          giverSection: 'Tutorial 1',
          recipient: 'charlie',
          recipientTeam: 'Team 2',
          recipientSection: 'Tutorial 1',
          responseDetails: {
            answer: 1,
            questionType: FeedbackQuestionType.RANK_RECIPIENTS,
          },
        },
      ];

      // ranks inside teams are meaningless when ranking across teams
      const stats: RankRecipientsQuestionStatistics = calculateRankRecipientsQuestionStatistics(
        testResponses,
        QuestionRecipientType.TEAMS,
      );

      expect(stats.rankPerOptionInTeam).toMatchObject({});
    });

    it('should rank correctly within team when recipient type is valid', () => {
      const testResponses: Response<FeedbackRankRecipientsResponseDetails>[] = [
        {
          giver: 'alice',
          giverTeam: 'Team 1',
          giverSection: 'Tutorial 1',
          recipient: 'bob',
          recipientTeam: 'Team 1',
          recipientSection: 'Tutorial 1',
          responseDetails: {
            answer: 1,
            questionType: FeedbackQuestionType.RANK_RECIPIENTS,
          },
        },
        {
          giver: 'bob',
          giverTeam: 'Team 1',
          giverSection: 'Tutorial 1',
          recipient: 'alice',
          recipientTeam: 'Team 1',
          recipientSection: 'Tutorial 1',
          responseDetails: {
            answer: 2,
            questionType: FeedbackQuestionType.RANK_RECIPIENTS,
          },
        },
        {
          giver: 'charlie',
          giverTeam: 'Team 2',
          giverSection: 'Tutorial 1',
          recipient: 'delta',
          recipientTeam: 'Team 2',
          recipientSection: 'Tutorial 1',
          responseDetails: {
            answer: 2,
            questionType: FeedbackQuestionType.RANK_RECIPIENTS,
          },
        },
        {
          giver: 'delta',
          giverTeam: 'Team 2',
          giverSection: 'Tutorial 1',
          recipient: 'charlie',
          recipientTeam: 'Team 2',
          recipientSection: 'Tutorial 1',
          responseDetails: {
            answer: 1,
            questionType: FeedbackQuestionType.RANK_RECIPIENTS,
          },
        },
        {
          giver: 'charlie',
          giverTeam: 'Team 2',
          giverSection: 'Tutorial 1',
          recipient: 'charlie',
          recipientTeam: 'Team 2',
          recipientSection: 'Tutorial 1',
          responseDetails: {
            answer: 3,
            questionType: FeedbackQuestionType.RANK_RECIPIENTS,
          },
        },
        {
          giver: 'delta',
          giverTeam: 'Team 2',
          giverSection: 'Tutorial 1',
          recipient: 'delta',
          recipientTeam: 'Team 2',
          recipientSection: 'Tutorial 1',
          responseDetails: {
            answer: 1,
            questionType: FeedbackQuestionType.RANK_RECIPIENTS,
          },
        },
        {
          giver: 'elliot',
          giverTeam: 'Team 2',
          giverSection: 'Tutorial 1',
          recipient: 'charlie',
          recipientTeam: 'Team 2',
          recipientSection: 'Tutorial 1',
          responseDetails: {
            answer: 1,
            questionType: FeedbackQuestionType.RANK_RECIPIENTS,
          },
        },
      ];

      const stats: RankRecipientsQuestionStatistics = calculateRankRecipientsQuestionStatistics(
        testResponses,
        QuestionRecipientType.OWN_TEAM_MEMBERS,
      );

      const bob = 'bob';
      const charlie = 'charlie';
      const delta = 'delta';

      expect(stats.rankPerOption[delta]).toBe(2);
      expect(stats.rankPerOptionInTeam[bob]).toBe(1);
      expect(stats.rankPerOptionInTeam[charlie]).toBe(2);
      expect(stats.rankPerOptionInTeam[delta]).toBe(1);
      expect(stats.rankPerOptionInTeamExcludeSelf[delta]).toBe(2);
    });
  });
});
