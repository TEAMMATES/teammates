import {
  ConstsumOptionsQuestionStatistics,
  ConstsumRecipientsQuestionStatistics,
  ContributionQuestionStatistics,
  McqQuestionStatistics,
  MsqQuestionStatistics,
  NumScaleQuestionStatistics,
  RankOptionsQuestionStatistics,
  RankRecipientsQuestionStatistics,
  Response,
  RubricPerRecipientStats,
  RubricQuestionStatistics,
} from '../../types/question-statistics.model';
import {
  FeedbackConstantSumQuestionDetails,
  FeedbackConstantSumResponseDetails,
  FeedbackContributionResponseDetails,
  FeedbackMcqQuestionDetails,
  FeedbackMcqResponseDetails,
  FeedbackMsqQuestionDetails,
  FeedbackMsqResponseDetails,
  FeedbackNumericalScaleResponseDetails,
  FeedbackParticipantType,
  FeedbackQuestionType,
  FeedbackRankOptionsQuestionDetails,
  FeedbackRankOptionsResponseDetails,
  FeedbackRankRecipientsResponseDetails,
  FeedbackRubricQuestionDetails,
  FeedbackRubricResponseDetails,
} from '../../types/api-output';
import {
  calculateConstsumOptionsQuestionStatistics,
  calculateConstsumRecipientsQuestionStatistics,
  calculateContributionQuestionStatistics,
  calculateMcqQuestionStatistics,
  calculateMsqQuestionStatistics,
  calculateNumScaleQuestionStatistics,
  calculateRankOptionsQuestionStatistics,
  calculateRankRecipientsQuestionStatistics,
  calculateRubricQuestionStatistics,
} from './question-statistics.util';
import constsumOptionQuestionResponses from '../components/question-types/question-statistics/test-data/constsumOptionQuestionResponses.json';
import mcqQuestionResponses from '../components/question-types/question-statistics/test-data/mcqQuestionResponses.json';
import msqQuestionResponses from '../components/question-types/question-statistics/test-data/msqQuestionResponses.json';
import numScaleQuestionResponses from '../components/question-types/question-statistics/test-data/numScaleQuestionResponses.json';
import rankOptionQuestionResponses from '../components/question-types/question-statistics/test-data/rankOptionQuestionResponses.json';
import rubricQuestionResponses from '../components/question-types/question-statistics/test-data/rubricQuestionResponses.json';
import { CONTRIBUTION_POINT_NOT_SUBMITTED } from '../../types/feedback-response-details';

describe('Question Statistics Utility Functions', () => {
  describe('calculateConstsumOptionsQuestionStatistics', () => {
    it('should calculate statistics correctly', () => {
      const question: FeedbackConstantSumQuestionDetails = {
        constSumOptions: ['optionA', 'optionB', 'optionC'],
      } as any;
      const responses: Response<FeedbackConstantSumResponseDetails>[] = JSON.parse(
        JSON.stringify(constsumOptionQuestionResponses.responses),
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
      const responses: Response<FeedbackConstantSumResponseDetails>[] = [
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
            questionType: FeedbackQuestionType.CONSTSUM,
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
            questionType: FeedbackQuestionType.CONSTSUM,
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
            questionType: FeedbackQuestionType.CONSTSUM,
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
            questionType: FeedbackQuestionType.CONSTSUM,
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
            questionType: FeedbackQuestionType.CONSTSUM,
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
            questionType: FeedbackQuestionType.CONSTSUM,
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
            questionType: FeedbackQuestionType.CONSTSUM,
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
            questionType: FeedbackQuestionType.CONSTSUM,
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
            questionType: FeedbackQuestionType.CONSTSUM,
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
            questionType: FeedbackQuestionType.CONSTSUM,
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
            questionType: FeedbackQuestionType.CONSTSUM,
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
            questionType: FeedbackQuestionType.CONSTSUM,
          },
        },
      ];

      const stats: ConstsumRecipientsQuestionStatistics = calculateConstsumRecipientsQuestionStatistics(
        responses,
        FeedbackParticipantType.NONE,
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

  describe('calculateMcqQuestionStatistics', () => {
    it('should calculate statistics correctly', () => {
      const question: FeedbackMcqQuestionDetails = {
        mcqChoices: ['optionA', 'optionB', 'optionC'],
        otherEnabled: false,
        hasAssignedWeights: true,
        mcqWeights: [1, 2, 3],
      } as any;
      const responses: Response<FeedbackMcqResponseDetails>[] =
        mcqQuestionResponses.responsesNoOther as Response<FeedbackMcqResponseDetails>[];

      const stats: McqQuestionStatistics = calculateMcqQuestionStatistics(question, responses);

      const expectedAnswerFrequency: Record<string, number> = {
        optionA: 2,
        optionB: 1,
        optionC: 0,
      };
      const expectedPercentagePerOption: Record<string, number> = {
        optionA: 66.67,
        optionB: 33.33,
        optionC: 0,
      };
      const expectedWeightPerOption: Record<string, number> = {
        optionA: 1,
        optionB: 2,
        optionC: 3,
      };
      const expectedWeightedPrecentagePerOption: Record<string, number> = {
        optionA: 50,
        optionB: 50,
        optionC: 0,
      };

      expect(stats.answerFrequency).toEqual(expectedAnswerFrequency);
      expect(stats.percentagePerOption).toEqual(expectedPercentagePerOption);
      expect(stats.weightPerOption).toEqual(expectedWeightPerOption);
      expect(stats.weightedPercentagePerOption).toEqual(expectedWeightedPrecentagePerOption);
    });

    it('should calculate statistics correctly when other is enabled', () => {
      const question: FeedbackMcqQuestionDetails = {
        mcqChoices: ['optionA', 'optionB', 'optionC'],
        otherEnabled: true,
        hasAssignedWeights: true,
        mcqWeights: [1, 2, 3],
        mcqOtherWeight: 4,
      } as any;
      const responses: Response<FeedbackMcqResponseDetails>[] =
        mcqQuestionResponses.responsesWithOther as Response<FeedbackMcqResponseDetails>[];

      const stats: McqQuestionStatistics = calculateMcqQuestionStatistics(question, responses);

      const expectedAnswerFrequency: Record<string, number> = {
        optionA: 1,
        optionB: 1,
        optionC: 0,
        Other: 1,
      };
      const expectedPercentagePerOption: Record<string, number> = {
        optionA: 33.33,
        optionB: 33.33,
        optionC: 0,
        Other: 33.33,
      };
      const expectedWeightPerOption: Record<string, number> = {
        optionA: 1,
        optionB: 2,
        optionC: 3,
        Other: 4,
      };
      const expectedWeightedPrecentagePerOption: Record<string, number> = {
        optionA: 14.29,
        optionB: 28.57,
        optionC: 0,
        Other: 57.14,
      };

      expect(stats.answerFrequency).toEqual(expectedAnswerFrequency);
      expect(stats.percentagePerOption).toEqual(expectedPercentagePerOption);
      expect(stats.weightPerOption).toEqual(expectedWeightPerOption);
      expect(stats.weightedPercentagePerOption).toEqual(expectedWeightedPrecentagePerOption);
    });

    it('should calculate statistics correctly when there are no assigned weights', () => {
      const question: FeedbackMcqQuestionDetails = {
        mcqChoices: ['optionA', 'optionB', 'optionC'],
        otherEnabled: false,
        hasAssignedWeights: false,
      } as any;
      const responses: Response<FeedbackMcqResponseDetails>[] =
        mcqQuestionResponses.responsesNoOther as Response<FeedbackMcqResponseDetails>[];

      const stats: McqQuestionStatistics = calculateMcqQuestionStatistics(question, responses);

      const expectedAnswerFrequency: Record<string, number> = {
        optionA: 2,
        optionB: 1,
        optionC: 0,
      };
      const expectedPercentagePerOption: Record<string, number> = {
        optionA: 66.67,
        optionB: 33.33,
        optionC: 0,
      };
      const expectedPerRecipientResponses: Record<string, any> = {};

      expect(stats.answerFrequency).toEqual(expectedAnswerFrequency);
      expect(stats.percentagePerOption).toEqual(expectedPercentagePerOption);
      expect(stats.perRecipientResponses).toEqual(expectedPerRecipientResponses);
    });

    it('should calculate statistics correctly when other is enabled and no assigned weights', () => {
      const question: FeedbackMcqQuestionDetails = {
        mcqChoices: ['optionA', 'optionB', 'optionC'],
        otherEnabled: true,
        hasAssignedWeights: false,
      } as any;
      const responses: Response<FeedbackMcqResponseDetails>[] =
        mcqQuestionResponses.responsesWithOther as Response<FeedbackMcqResponseDetails>[];

      const stats: McqQuestionStatistics = calculateMcqQuestionStatistics(question, responses);

      const expectedAnswerFrequency: Record<string, number> = {
        optionA: 1,
        optionB: 1,
        optionC: 0,
        Other: 1,
      };
      const expectedPercentagePerOption: Record<string, number> = {
        optionA: 33.33,
        optionB: 33.33,
        optionC: 0,
        Other: 33.33,
      };
      const expectedPerRecipientResponses: Record<string, any> = {};

      expect(stats.answerFrequency).toEqual(expectedAnswerFrequency);
      expect(stats.percentagePerOption).toEqual(expectedPercentagePerOption);
      expect(stats.perRecipientResponses).toEqual(expectedPerRecipientResponses);
    });
  });

  describe('calculateMsqQuestionStatistics', () => {
    it('should calculate statistics correctly', () => {
      const question: FeedbackMsqQuestionDetails = {
        msqChoices: ['optionA', 'optionB', 'optionC'],
        otherEnabled: false,
        hasAssignedWeights: true,
        msqWeights: [1, 2, 3],
      } as any;
      const responses: Response<FeedbackMsqResponseDetails>[] =
        msqQuestionResponses.responsesNoOther as Response<FeedbackMsqResponseDetails>[];

      const stats: MsqQuestionStatistics = calculateMsqQuestionStatistics(question, responses);

      const expectedAnswerFrequency: Record<string, number> = {
        optionA: 2,
        optionB: 1,
        optionC: 0,
      };
      const expectedPercentagePerOption: Record<string, number> = {
        optionA: 66.67,
        optionB: 33.33,
        optionC: 0,
      };
      const expectedWeightPerOption: Record<string, number> = {
        optionA: 1,
        optionB: 2,
        optionC: 3,
      };
      const expectedWeightedPrecentagePerOption: Record<string, number> = {
        optionA: 50,
        optionB: 50,
        optionC: 0,
      };

      expect(stats.answerFrequency).toEqual(expectedAnswerFrequency);
      expect(stats.percentagePerOption).toEqual(expectedPercentagePerOption);
      expect(stats.weightPerOption).toEqual(expectedWeightPerOption);
      expect(stats.weightedPercentagePerOption).toEqual(expectedWeightedPrecentagePerOption);
    });

    it('should calculate statistics correctly when there are no weights', () => {
      const question: FeedbackMsqQuestionDetails = {
        msqChoices: ['optionA', 'optionB', 'optionC'],
        otherEnabled: false,
        hasAssignedWeights: false,
      } as any;
      const responses: Response<FeedbackMsqResponseDetails>[] =
        msqQuestionResponses.responsesNoOther as Response<FeedbackMsqResponseDetails>[];

      const stats: MsqQuestionStatistics = calculateMsqQuestionStatistics(question, responses);

      const expectedAnswerFrequency: Record<string, number> = {
        optionA: 2,
        optionB: 1,
        optionC: 0,
      };
      const expectedPercentagePerOption: Record<string, number> = {
        optionA: 66.67,
        optionB: 33.33,
        optionC: 0,
      };
      const expectedPerRecipientResponses: Record<string, any> = {};

      expect(stats.answerFrequency).toEqual(expectedAnswerFrequency);
      expect(stats.percentagePerOption).toEqual(expectedPercentagePerOption);
      expect(stats.perRecipientResponses).toEqual(expectedPerRecipientResponses);
    });
  });

  describe('calculateNumScaleQuestionStatistics', () => {
    it('should calculate statistics correctly', () => {
      const responses: Response<FeedbackNumericalScaleResponseDetails>[] =
        numScaleQuestionResponses.responses as Response<FeedbackNumericalScaleResponseDetails>[];

      const stats: NumScaleQuestionStatistics = calculateNumScaleQuestionStatistics(responses);

      const team = 'Instructors';
      const recipient = 'Instructor';
      expect(stats.teamToRecipientToScores[team][recipient].min).toEqual(1);
      expect(stats.teamToRecipientToScores[team][recipient].max).toEqual(5);
      expect(stats.teamToRecipientToScores[team][recipient].average).toEqual(2.67);
      expect(stats.teamToRecipientToScores[team][recipient].averageExcludingSelf).toEqual(2.67);
    });

    it('should calculate statistics correctly if responses are zero', () => {
      const responses: Response<FeedbackNumericalScaleResponseDetails>[] =
        numScaleQuestionResponses.responsesAtZero as Response<FeedbackNumericalScaleResponseDetails>[];

      const stats: NumScaleQuestionStatistics = calculateNumScaleQuestionStatistics(responses);

      const team = 'Instructors';
      const recipient = 'Instructor';
      expect(stats.teamToRecipientToScores[team][recipient].min).toEqual(0);
      expect(stats.teamToRecipientToScores[team][recipient].max).toEqual(0);
      expect(stats.teamToRecipientToScores[team][recipient].average).toEqual(0);
      expect(stats.teamToRecipientToScores[team][recipient].averageExcludingSelf).toEqual(0);
    });

    it('should calculate statistics correctly if self-response exists', () => {
      const responses: Response<FeedbackNumericalScaleResponseDetails>[] =
        numScaleQuestionResponses.responsesWithSelf as Response<FeedbackNumericalScaleResponseDetails>[];

      const stats: NumScaleQuestionStatistics = calculateNumScaleQuestionStatistics(responses);

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
        options: ['optionA', 'optionB', 'optionC', 'optionD'],
      } as any;
      const responses: Response<FeedbackRankOptionsResponseDetails>[] =
        rankOptionQuestionResponses.responses as Response<FeedbackRankOptionsResponseDetails>[];

      const stats: RankOptionsQuestionStatistics = calculateRankOptionsQuestionStatistics(question, responses);

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
        options: ['optionA', 'optionB', 'optionC', 'optionD'],
      } as any;
      const responses: Response<FeedbackRankOptionsResponseDetails>[] =
        rankOptionQuestionResponses.responsesSameRank as Response<FeedbackRankOptionsResponseDetails>[];

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
        FeedbackParticipantType.TEAMS,
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
        FeedbackParticipantType.OWN_TEAM_MEMBERS,
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

  describe('calculateRubricQuestionStatistics', () => {
    it('should calculate responses correctly', () => {
      const question: FeedbackRubricQuestionDetails = {
        rubricSubQuestions: ['Question1', 'Question2', 'Question3'],
        rubricChoices: ['Yes', 'No'],
        hasAssignedWeights: true,
        rubricWeightsForEachCell: [
          [0.2, 0.8],
          [0.1, 0.9],
          [0.4, 0.6],
        ],
      } as any;
      const responses: Response<FeedbackRubricResponseDetails>[] =
        rubricQuestionResponses.responses as Response<FeedbackRubricResponseDetails>[];

      const stats: RubricQuestionStatistics = calculateRubricQuestionStatistics(question, responses, false);

      const expectedPercentages: number[][] = [
        [75, 25],
        [50, 50],
        [100, 0],
      ];

      const expectedPercentagesExceptSelf: number[][] = [
        [100, 0],
        [50, 50],
        [100, 0],
      ];

      const expectedWeightAverage: number[] = [0.35, 0.5, 0.4];

      const expectedWeightAverageExcludeSelf: number[] = [0.2, 0.5, 0.4];

      expect(stats.percentages).toEqual(expectedPercentages);
      expect(stats.percentagesExcludeSelf).toEqual(expectedPercentagesExceptSelf);
      expect(stats.subQuestionWeightAverage).toEqual(expectedWeightAverage);
      expect(stats.subQuestionWeightAverageExcludeSelf).toEqual(expectedWeightAverageExcludeSelf);
    });

    it('should calculate responses correctly when there are no weights', () => {
      const question: FeedbackRubricQuestionDetails = {
        rubricSubQuestions: ['Question1', 'Question2', 'Question3'],
        rubricChoices: ['Yes', 'No'],
        hasAssignedWeights: false,
      } as any;
      const responses: Response<FeedbackRubricResponseDetails>[] =
        rubricQuestionResponses.responses as Response<FeedbackRubricResponseDetails>[];

      const stats: RubricQuestionStatistics = calculateRubricQuestionStatistics(question, responses, false);

      const expectedPercentages: number[][] = [
        [75, 25],
        [50, 50],
        [100, 0],
      ];

      const expectedPercentagesExceptSelf: number[][] = [
        [100, 0],
        [50, 50],
        [100, 0],
      ];

      const expectedPerRecpientStatsMap: Record<string, RubricPerRecipientStats> = {};

      expect(stats.percentages).toEqual(expectedPercentages);
      expect(stats.percentagesExcludeSelf).toEqual(expectedPercentagesExceptSelf);
      expect(stats.perRecipientStatsMap).toEqual(expectedPerRecpientStatsMap);
    });
  });

  describe('calculateContributionQuestionStatistics', () => {
    it('should return empty stats when statistics string is empty', () => {
      const responses: Response<FeedbackContributionResponseDetails>[] = [];
      const statistics = '';
      const isStudent = false;

      const stats: ContributionQuestionStatistics = calculateContributionQuestionStatistics(
        responses,
        statistics,
        isStudent,
      );

      expect(stats.emailToTeamName).toEqual({});
      expect(stats.emailToName).toEqual({});
      expect(stats.emailToDiff).toEqual({});
      expect(stats.questionOverallStatistics?.results).toEqual({});
    });

    it('should return empty stats when statistics string is null or undefined', () => {
      const responses: Response<FeedbackContributionResponseDetails>[] = [];

      const statsWithNull: ContributionQuestionStatistics = calculateContributionQuestionStatistics(
        responses,
        null as any,
        false,
      );

      expect(statsWithNull.emailToTeamName).toEqual({});
      expect(statsWithNull.emailToName).toEqual({});
      expect(statsWithNull.emailToDiff).toEqual({});
    });

    it('should calculate statistics correctly for student view', () => {
      const responses: Response<FeedbackContributionResponseDetails>[] = [];
      const statistics = JSON.stringify({
        results: {
          'student@example.com': {
            claimed: 25,
            perceived: 30,
            claimedOthers: {
              'peer1@example.com': 20,
              'peer2@example.com': 55,
            },
            perceivedOthers: [35, 35],
          },
        },
      });
      const isStudent = true;

      const stats: ContributionQuestionStatistics = calculateContributionQuestionStatistics(
        responses,
        statistics,
        isStudent,
      );

      expect(stats.questionStatisticsForStudent?.claimed).toBe(25);
      expect(stats.questionStatisticsForStudent?.perceived).toBe(30);
      expect(stats.questionStatisticsForStudent?.claimedOthers).toEqual({
        'peer1@example.com': 20,
        'peer2@example.com': 55,
      });
      expect(stats.questionStatisticsForStudent?.claimedOthersValues).toEqual([55, 20]);
      expect(stats.emailToTeamName).toEqual({});
      expect(stats.emailToName).toEqual({});
    });

    it('should calculate statistics correctly for instructor view with positive differences', () => {
      const responses: Response<FeedbackContributionResponseDetails>[] = [
        {
          giver: 'Alice',
          giverTeam: 'Team A',
          giverEmail: 'alice@example.com',
          giverSection: '',
          recipient: 'Bob',
          recipientTeam: 'Team B',
          recipientEmail: 'bob@example.com',
          recipientSection: '',
          responseDetails: {
            answer: 25,
            questionType: FeedbackQuestionType.CONTRIB,
          },
        },
        {
          giver: 'Charlie',
          giverTeam: 'Team A',
          giverEmail: 'charlie@example.com',
          giverSection: '',
          recipient: 'Bob',
          recipientTeam: 'Team B',
          recipientEmail: 'bob@example.com',
          recipientSection: '',
          responseDetails: {
            answer: 30,
            questionType: FeedbackQuestionType.CONTRIB,
          },
        },
      ];
      const statistics = JSON.stringify({
        results: {
          'bob@example.com': {
            claimed: 20,
            perceived: 35,
            claimedOthers: {},
            perceivedOthers: [],
          },
        },
      });
      const isStudent = false;

      const stats: ContributionQuestionStatistics = calculateContributionQuestionStatistics(
        responses,
        statistics,
        isStudent,
      );

      expect(stats.emailToTeamName).toEqual({
        'bob@example.com': 'Team B',
      });
      expect(stats.emailToName).toEqual({
        'bob@example.com': 'Bob',
      });
      expect(stats.emailToDiff).toEqual({
        'bob@example.com': 15, // 35 - 20
      });
      expect(stats.questionOverallStatistics?.results).toEqual({
        'bob@example.com': {
          claimed: 20,
          perceived: 35,
          claimedOthers: {},
          perceivedOthers: [],
        },
      });
    });

    it('should calculate statistics correctly for instructor view with negative claimed values', () => {
      const responses: Response<FeedbackContributionResponseDetails>[] = [
        {
          giver: 'Alice',
          giverTeam: 'Team A',
          giverEmail: 'alice@example.com',
          giverSection: '',
          recipient: 'Bob',
          recipientTeam: 'Team B',
          recipientEmail: 'bob@example.com',
          recipientSection: '',
          responseDetails: {
            answer: 25,
            questionType: FeedbackQuestionType.CONTRIB,
          },
        },
      ];
      const statistics = JSON.stringify({
        results: {
          'bob@example.com': {
            claimed: -1,
            perceived: 35,
            claimedOthers: {},
            perceivedOthers: [],
          },
        },
      });
      const isStudent = false;

      const stats: ContributionQuestionStatistics = calculateContributionQuestionStatistics(
        responses,
        statistics,
        isStudent,
      );

      expect(stats.emailToDiff['bob@example.com']).toBe(CONTRIBUTION_POINT_NOT_SUBMITTED); // CONTRIBUTION_POINT_NOT_SUBMITTED
    });

    it('should calculate statistics correctly for instructor view with negative perceived values', () => {
      const responses: Response<FeedbackContributionResponseDetails>[] = [
        {
          giver: 'Alice',
          giverTeam: 'Team A',
          giverEmail: 'alice@example.com',
          giverSection: '',
          recipient: 'Bob',
          recipientTeam: 'Team B',
          recipientEmail: 'bob@example.com',
          recipientSection: '',
          responseDetails: {
            answer: 25,
            questionType: FeedbackQuestionType.CONTRIB,
          },
        },
      ];
      const statistics = JSON.stringify({
        results: {
          'bob@example.com': {
            claimed: 25,
            perceived: -1,
            claimedOthers: {},
            perceivedOthers: [],
          },
        },
      });
      const isStudent = false;

      const stats: ContributionQuestionStatistics = calculateContributionQuestionStatistics(
        responses,
        statistics,
        isStudent,
      );

      expect(stats.emailToDiff['bob@example.com']).toBe(CONTRIBUTION_POINT_NOT_SUBMITTED);
    });

    it('should handle responses without recipientEmail for instructor view', () => {
      const responses: Response<FeedbackContributionResponseDetails>[] = [
        {
          giver: 'Alice',
          giverTeam: 'Team A',
          giverEmail: 'alice@example.com',
          giverSection: '',
          recipient: 'Bob',
          recipientTeam: 'Team B',
          recipientEmail: undefined,
          recipientSection: '',
          responseDetails: {
            answer: 25,
            questionType: FeedbackQuestionType.CONTRIB,
          },
        },
      ];
      const statistics = JSON.stringify({
        results: {},
      });
      const isStudent = false;

      const stats: ContributionQuestionStatistics = calculateContributionQuestionStatistics(
        responses,
        statistics,
        isStudent,
      );

      expect(stats.emailToTeamName).toEqual({});
      expect(stats.emailToName).toEqual({});
      expect(stats.emailToDiff).toEqual({});
    });

    it('should calculate statistics correctly for multiple recipients in instructor view', () => {
      const responses: Response<FeedbackContributionResponseDetails>[] = [
        {
          giver: 'Alice',
          giverTeam: 'Team A',
          giverEmail: 'alice@example.com',
          giverSection: '',
          recipient: 'Bob',
          recipientTeam: 'Team B',
          recipientEmail: 'bob@example.com',
          recipientSection: '',
          responseDetails: {
            answer: 25,
            questionType: FeedbackQuestionType.CONTRIB,
          },
        },
        {
          giver: 'Alice',
          giverTeam: 'Team A',
          giverEmail: 'alice@example.com',
          giverSection: '',
          recipient: 'Charlie',
          recipientTeam: 'Team B',
          recipientEmail: 'charlie@example.com',
          recipientSection: '',
          responseDetails: {
            answer: 30,
            questionType: FeedbackQuestionType.CONTRIB,
          },
        },
      ];
      const statistics = JSON.stringify({
        results: {
          'bob@example.com': {
            claimed: 20,
            perceived: 35,
            claimedOthers: {},
            perceivedOthers: [],
          },
          'charlie@example.com': {
            claimed: 30,
            perceived: 40,
            claimedOthers: {},
            perceivedOthers: [],
          },
        },
      });
      const isStudent = false;

      const stats: ContributionQuestionStatistics = calculateContributionQuestionStatistics(
        responses,
        statistics,
        isStudent,
      );

      expect(stats.emailToTeamName).toEqual({
        'bob@example.com': 'Team B',
        'charlie@example.com': 'Team B',
      });
      expect(stats.emailToName).toEqual({
        'bob@example.com': 'Bob',
        'charlie@example.com': 'Charlie',
      });
      expect(stats.emailToDiff).toEqual({
        'bob@example.com': 15, // 35 - 20
        'charlie@example.com': 10, // 40 - 30
      });
    });

    it('should calculate statistics correctly for student view with multiple peers', () => {
      const responses: Response<FeedbackContributionResponseDetails>[] = [];
      const statistics = JSON.stringify({
        results: {
          'student@example.com': {
            claimed: 30,
            perceived: 25,
            claimedOthers: {
              'peer1@example.com': 15,
              'peer2@example.com': 35,
              'peer3@example.com': 20,
            },
            perceivedOthers: [20, 40, 15],
          },
        },
      });
      const isStudent = true;

      const stats: ContributionQuestionStatistics = calculateContributionQuestionStatistics(
        responses,
        statistics,
        isStudent,
      );

      expect(stats.questionStatisticsForStudent?.claimed).toBe(30);
      expect(stats.questionStatisticsForStudent?.perceived).toBe(25);
      // Should be sorted in descending order
      expect(stats.questionStatisticsForStudent?.claimedOthersValues).toEqual([35, 20, 15]);
    });

    it('should preserve team information for multiple recipients in instructor view', () => {
      const responses: Response<FeedbackContributionResponseDetails>[] = [
        {
          giver: 'Alice',
          giverTeam: 'Team A',
          giverEmail: 'alice@example.com',
          giverSection: '',
          recipient: 'Bob',
          recipientTeam: 'Team A',
          recipientEmail: 'bob@example.com',
          recipientSection: '',
          responseDetails: {
            answer: 25,
            questionType: FeedbackQuestionType.CONTRIB,
          },
        },
        {
          giver: 'Alice',
          giverTeam: 'Team A',
          giverEmail: 'alice@example.com',
          giverSection: '',
          recipient: 'Charlie',
          recipientTeam: 'Team B',
          recipientEmail: 'charlie@example.com',
          recipientSection: '',
          responseDetails: {
            answer: 30,
            questionType: FeedbackQuestionType.CONTRIB,
          },
        },
      ];
      const statistics = JSON.stringify({
        results: {
          'bob@example.com': {
            claimed: 20,
            perceived: 35,
            claimedOthers: {},
            perceivedOthers: [],
          },
          'charlie@example.com': {
            claimed: 30,
            perceived: 40,
            claimedOthers: {},
            perceivedOthers: [],
          },
        },
      });
      const isStudent = false;

      const stats: ContributionQuestionStatistics = calculateContributionQuestionStatistics(
        responses,
        statistics,
        isStudent,
      );

      expect(stats.emailToTeamName['bob@example.com']).toBe('Team A');
      expect(stats.emailToTeamName['charlie@example.com']).toBe('Team B');
      expect(stats.emailToName['bob@example.com']).toBe('Bob');
      expect(stats.emailToName['charlie@example.com']).toBe('Charlie');
    });
  });
});
