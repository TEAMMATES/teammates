import {
  ConstsumOptionsQuestionStatistics,
  ConstsumRecipientsQuestionStatistics,
  RankOptionsQuestionStatistics,
  RankRecipientsQuestionStatistics,
  Response,
} from '../../types/question-statistics.model';
import {
  FeedbackConstantSumOptionsQuestionDetails,
  FeedbackConstantSumOptionsResponseDetails,
  FeedbackConstantSumRecipientsResponseDetails,
  FeedbackQuestionType,
  FeedbackRankOptionsQuestionDetails,
  FeedbackRankOptionsResponseDetails,
  FeedbackRankRecipientsResponseDetails,
  QuestionRecipientType,
} from '../../types/api-output';
import {
  calculateConstsumOptionsQuestionStatistics,
  calculateConstsumRecipientsQuestionStatistics,
  calculateNumScaleQuestionStatistics,
  calculateRankOptionsQuestionStatistics,
  calculateRankRecipientsQuestionStatistics,
} from './question-statistics.util';
import constsumOptionQuestionResponses from '../components/question-types/question-statistics/test-data/constsum-option-question-responses';
import numScaleQuestionResponses from '../components/question-types/question-statistics/test-data/num-scale-question-responses';
import rankOptionQuestionResponses from '../components/question-types/question-statistics/test-data/rank-option-question-responses';

describe('Question Statistics Utility Functions', () => {
  describe('calculateConstsumOptionsQuestionStatistics', () => {
    it('should calculate statistics correctly', () => {
      const question: FeedbackConstantSumOptionsQuestionDetails = {
        questionType: FeedbackQuestionType.CONSTSUM_OPTIONS,
        questionText: 'How do you distribute points?',
        constSumOptions: ['optionA', 'optionB', 'optionC'],
        pointsPerOption: true,
        forceUnevenDistribution: false,
        distributePointsFor: 'distribute points for',
        points: 100,
      };
      const responses: Response<FeedbackConstantSumOptionsResponseDetails>[] = structuredClone(
        constsumOptionQuestionResponses.responses,
      );

      const stats: ConstsumOptionsQuestionStatistics = calculateConstsumOptionsQuestionStatistics(question, responses);

      const expectedPointsPerOption: Record<string, number[]> = {
        optionA: [10, 30, 50],
        optionB: [50, 70, 90],
        optionC: [0, 0, 0],
      };
      const expectedTotalPointsPerOption: Record<string, number> = {
        optionA: 90,
        optionB: 210,
        optionC: 0,
      };
      const expectedAveragePointsPerOption: Record<string, number> = {
        optionA: 30,
        optionB: 70,
        optionC: 0,
      };

      expect(stats.pointsPerOption).toEqual(expectedPointsPerOption);
      expect(stats.totalPointsPerOption).toEqual(expectedTotalPointsPerOption);
      expect(stats.averagePointsPerOption).toEqual(expectedAveragePointsPerOption);
    });
  });

  describe('calculateConstsumRecipientsQuestionStatistics', () => {
    it('should calculate statistics correctly', () => {
      const responses: Response<FeedbackConstantSumRecipientsResponseDetails>[] = [
        {
          giver: 'Alice',
          giverTeam: 'Team 1',
          giverEmail: 'alice@gmail.com',
          giverSection: '',
          recipient: 'Bob',
          recipientTeam: 'Team 2',
          recipientEmail: 'bob@gmail.com',
          recipientSection: '',
          responseDetails: {
            answers: [2],
            questionType: FeedbackQuestionType.CONSTSUM_RECIPIENTS,
          },
        },
        {
          giver: 'Charles',
          giverTeam: 'Team 1',
          giverEmail: 'charles@gmail.com',
          giverSection: '',
          recipient: 'Bob',
          recipientTeam: 'Team 2',
          recipientEmail: 'bob@gmail.com',
          recipientSection: '',
          responseDetails: {
            answers: [3],
            questionType: FeedbackQuestionType.CONSTSUM_RECIPIENTS,
          },
        },
        {
          giver: 'David',
          giverTeam: 'Team 1',
          giverEmail: 'david@gmail.com',
          giverSection: '',
          recipient: 'Bob',
          recipientTeam: 'Team 2',
          recipientEmail: 'bob@gmail.com',
          recipientSection: '',
          responseDetails: {
            answers: [5],
            questionType: FeedbackQuestionType.CONSTSUM_RECIPIENTS,
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
            answers: [5],
            questionType: FeedbackQuestionType.CONSTSUM_RECIPIENTS,
          },
        },
        {
          giver: 'Alice',
          giverTeam: 'Team 1',
          giverEmail: 'alice@gmail.com',
          giverSection: '',
          recipient: 'Emma',
          recipientTeam: 'Team 2',
          recipientEmail: 'emma@gmail.com',
          recipientSection: '',
          responseDetails: {
            answers: [9],
            questionType: FeedbackQuestionType.CONSTSUM_RECIPIENTS,
          },
        },
        {
          giver: 'Charles',
          giverTeam: 'Team 1',
          giverEmail: 'charles@gmail.com',
          giverSection: '',
          recipient: 'Emma',
          recipientTeam: 'Team 2',
          recipientEmail: 'emma@gmail.com',
          recipientSection: '',
          responseDetails: {
            answers: [6],
            questionType: FeedbackQuestionType.CONSTSUM_RECIPIENTS,
          },
        },
        {
          giver: 'David',
          giverTeam: 'Team 1',
          giverEmail: 'david@gmail.com',
          giverSection: '',
          recipient: 'Emma',
          recipientTeam: 'Team 2',
          recipientEmail: 'emma@gmail.com',
          recipientSection: '',
          responseDetails: {
            answers: [4],
            questionType: FeedbackQuestionType.CONSTSUM_RECIPIENTS,
          },
        },
        {
          giver: 'Emma',
          giverTeam: 'Team 2',
          giverEmail: 'emma@gmail.com',
          giverSection: '',
          recipient: 'Emma',
          recipientTeam: 'Team 2',
          recipientEmail: 'emma@gmail.com',
          recipientSection: '',
          responseDetails: {
            answers: [7],
            questionType: FeedbackQuestionType.CONSTSUM_RECIPIENTS,
          },
        },
        {
          giver: 'Fred',
          giverTeam: 'Team 3',
          giverEmail: 'fred@gmail.com',
          giverSection: '',
          recipient: 'Fred',
          recipientTeam: 'Team 3',
          recipientEmail: 'fred@gmail.com',
          recipientSection: '',
          responseDetails: {
            answers: [2],
            questionType: FeedbackQuestionType.CONSTSUM_RECIPIENTS,
          },
        },
        {
          giver: 'Greg',
          giverTeam: 'Team 3',
          giverEmail: 'greg@gmail.com',
          giverSection: '',
          recipient: 'Henry',
          recipientTeam: 'Team 3',
          recipientEmail: 'henry@gmail.com',
          recipientSection: '',
          responseDetails: {
            answers: [5],
            questionType: FeedbackQuestionType.CONSTSUM_RECIPIENTS,
          },
        },
        {
          giver: 'Fred',
          giverTeam: 'Team 3',
          giverEmail: 'fred@gmail.com',
          giverSection: '',
          recipient: 'Greg',
          recipientTeam: 'Team 3',
          recipientEmail: 'greg@gmail.com',
          recipientSection: '',
          responseDetails: {
            answers: [7],
            questionType: FeedbackQuestionType.CONSTSUM_RECIPIENTS,
          },
        },
        {
          giver: 'Henry',
          giverTeam: 'Team 3',
          giverEmail: 'henry@gmail.com',
          giverSection: '',
          recipient: 'Greg',
          recipientTeam: 'Team 3',
          recipientEmail: 'greg@gmail.com',
          recipientSection: '',
          responseDetails: {
            answers: [9],
            questionType: FeedbackQuestionType.CONSTSUM_RECIPIENTS,
          },
        },
      ];

      const stats: ConstsumRecipientsQuestionStatistics = calculateConstsumRecipientsQuestionStatistics(
        responses,
        QuestionRecipientType.STUDENTS,
      );

      const expectedPointsPerOption: Record<string, number[]> = {
        'bob@gmail.com': [2, 3, 5, 5],
        'emma@gmail.com': [4, 6, 7, 9],
        'fred@gmail.com': [2],
        'henry@gmail.com': [5],
        'greg@gmail.com': [7, 9],
      };
      const expectedTotalPointsPerOption: Record<string, number> = {
        'bob@gmail.com': 15,
        'emma@gmail.com': 26,
        'fred@gmail.com': 2,
        'henry@gmail.com': 5,
        'greg@gmail.com': 16,
      };
      const expectedAveragePointsPerOption: Record<string, number> = {
        'bob@gmail.com': 3.75,
        'emma@gmail.com': 6.5,
        'fred@gmail.com': 2,
        'henry@gmail.com': 5,
        'greg@gmail.com': 8,
      };
      const expectedAveragePointsExcludingSelf: Record<string, number> = {
        'bob@gmail.com': 3.33,
        'emma@gmail.com': 6.33,
        'fred@gmail.com': 0,
        'henry@gmail.com': 5,
        'greg@gmail.com': 8,
      };

      expect(stats.pointsPerOption).toEqual(expectedPointsPerOption);
      expect(stats.totalPointsPerOption).toEqual(expectedTotalPointsPerOption);
      expect(stats.averagePointsPerOption).toEqual(expectedAveragePointsPerOption);
      expect(stats.averagePointsExcludingSelf).toEqual(expectedAveragePointsExcludingSelf);
    });
  });

  describe('calculateNumScaleQuestionStatistics', () => {
    it('should calculate statistics correctly', () => {
      const responses = numScaleQuestionResponses.responses;
      const stats = calculateNumScaleQuestionStatistics(responses);

      const team = 'Instructors';
      const recipient = 'Instructor';
      expect(stats.teamToRecipientToScores[team][recipient].min).toEqual(1);
      expect(stats.teamToRecipientToScores[team][recipient].max).toEqual(5);
      expect(stats.teamToRecipientToScores[team][recipient].average).toEqual(2.67);
      expect(stats.teamToRecipientToScores[team][recipient].averageExcludingSelf).toEqual(2.67);
    });

    it('should calculate statistics correctly if responses are zero', () => {
      const responses = numScaleQuestionResponses.responsesAtZero;
      const stats = calculateNumScaleQuestionStatistics(responses);

      const team = 'Instructors';
      const recipient = 'Instructor';
      expect(stats.teamToRecipientToScores[team][recipient].min).toEqual(0);
      expect(stats.teamToRecipientToScores[team][recipient].max).toEqual(0);
      expect(stats.teamToRecipientToScores[team][recipient].average).toEqual(0);
      expect(stats.teamToRecipientToScores[team][recipient].averageExcludingSelf).toEqual(0);
    });

    it('should calculate statistics correctly if self-response exists', () => {
      const responses = numScaleQuestionResponses.responsesWithSelf;
      const stats = calculateNumScaleQuestionStatistics(responses);

      const team = 'Instructors';
      const recipient = 'Instructor';
      expect(stats.teamToRecipientToScores[team][recipient].min).toEqual(2);
      expect(stats.teamToRecipientToScores[team][recipient].max).toEqual(5);
      expect(stats.teamToRecipientToScores[team][recipient].average).toEqual(3.5);
      expect(stats.teamToRecipientToScores[team][recipient].averageExcludingSelf).toEqual(3);
    });
  });

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
