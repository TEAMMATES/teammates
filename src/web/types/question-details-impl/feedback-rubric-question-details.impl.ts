import { AbstractFeedbackQuestionDetails } from './abstract-feedback-question-details';
import { StringHelper } from '../../services/string-helper';
import {
  FeedbackQuestionType,
  FeedbackRubricQuestionDetails,
  FeedbackRubricResponseDetails,
  QuestionOutput,
} from '../api-output';
import { NO_VALUE } from '../feedback-response-details';
import { Response, RubricPerRecipientStats } from '../question-statistics.model';
import { calculateRubricQuestionStatistics } from '../../app/utils/question-statistics.util';

/**
 * Concrete implementation of {@link FeedbackRubricQuestionDetails}.
 */
export class FeedbackRubricQuestionDetailsImpl
  extends AbstractFeedbackQuestionDetails
  implements FeedbackRubricQuestionDetails
{
  hasAssignedWeights = false;
  rubricChoices: string[] = [];
  rubricSubQuestions: string[] = [];
  rubricWeightsForEachCell: number[][] = [];
  rubricDescriptions: string[][] = [];
  questionText = '';
  questionType: FeedbackQuestionType = FeedbackQuestionType.RUBRIC;

  constructor(apiOutput: FeedbackRubricQuestionDetails) {
    super();
    this.hasAssignedWeights = apiOutput.hasAssignedWeights;
    this.rubricChoices = apiOutput.rubricChoices;
    this.rubricSubQuestions = apiOutput.rubricSubQuestions;
    this.rubricWeightsForEachCell = apiOutput.rubricWeightsForEachCell;
    this.rubricDescriptions = apiOutput.rubricDescriptions;
    this.questionText = apiOutput.questionText;
  }

  override getQuestionCsvHeaders(): string[] {
    return ['Sub Question', 'Choice Value', 'Choice Number'];
  }

  override getMissingResponseCsvAnswers(): string[][] {
    return [['All Sub-Questions', 'No Response']];
  }

  getQuestionCsvStats(question: QuestionOutput): string[][] {
    const statsRows: string[][] = [];

    const questionDetails = question.feedbackQuestion.questionDetails as FeedbackRubricQuestionDetails;
    const responses = question.allResponses
      // Missing response is meaningless for statistics
      .filter((response) => !response.isMissingResponse) as unknown as Response<FeedbackRubricResponseDetails>[];

    if (responses.length === 0) {
      // skip stats for no response
      return [];
    }

    const statsCalculation = calculateRubricQuestionStatistics(questionDetails, responses, false);

    const header: string[] = ['', ...statsCalculation.choices];
    if (statsCalculation.hasWeights) {
      header.push('Average');
    }
    statsRows.push(header);

    statsCalculation.subQuestions.forEach((subQuestion: string, questionIndex: number) => {
      const currRow: string[] = [
        `${StringHelper.integerToLowerCaseAlphabeticalIndex(questionIndex + 1)}) ${subQuestion}`,
        ...statsCalculation.choices.map((_: string, choiceIndex: number) => {
          return `${statsCalculation.percentages[questionIndex][choiceIndex]}% \
(${statsCalculation.answers[questionIndex][choiceIndex]}) \
${
  statsCalculation.hasWeights ? `[${this.getDisplayWeight(statsCalculation.weights[questionIndex][choiceIndex])}]` : ''
}`;
        }),
      ];
      if (statsCalculation.hasWeights) {
        currRow.push(this.getDisplayWeight(statsCalculation.subQuestionWeightAverage[questionIndex]));
      }
      statsRows.push(currRow);
    });

    if (!statsCalculation.hasWeights) {
      return statsRows;
    }

    // generate per recipient stats
    statsRows.push([], ['Per Recipient Statistics (Per Criterion)']);

    statsRows.push([
      'Team',
      'Recipient Name',
      'Recipient Email',
      'Sub Question',
      ...statsCalculation.choices,
      'Total',
      'Average',
    ]);

    Object.values(statsCalculation.perRecipientStatsMap)
      .sort(
        (a: RubricPerRecipientStats, b: RubricPerRecipientStats) =>
          a.recipientTeam.localeCompare(b.recipientTeam) || a.recipientName.localeCompare(b.recipientName),
      )
      .forEach((perRecipientStats: RubricPerRecipientStats) => {
        this.rubricSubQuestions.forEach((subQuestion: string, questionIndex: number) => {
          statsRows.push([
            perRecipientStats.recipientTeam,
            perRecipientStats.recipientName,
            perRecipientStats.recipientEmail ?? '',
            `${StringHelper.integerToLowerCaseAlphabeticalIndex(questionIndex + 1)}) ${subQuestion}`,
            ...statsCalculation.choices.map((_: string, choiceIndex: number) => {
              return `${perRecipientStats.percentages[questionIndex][choiceIndex]}% \
(${perRecipientStats.answers[questionIndex][choiceIndex]}) \
[${this.getDisplayWeight(statsCalculation.weights[questionIndex][choiceIndex])}]`;
            }),
            this.getDisplayWeight(perRecipientStats.subQuestionTotalChosenWeight[questionIndex]),
            this.getDisplayWeight(perRecipientStats.subQuestionWeightAverage[questionIndex]),
          ]);
        });
      });

    // generate overall recipient stats
    statsRows.push([], ['Per Recipient Statistics (Overall)']);

    statsRows.push([
      'Team',
      'Recipient Name',
      'Recipient Email',
      ...statsCalculation.choices,
      'Total',
      'Average',
      'Per Criterion Average',
    ]);

    Object.values(statsCalculation.perRecipientStatsMap)
      .sort(
        (a: RubricPerRecipientStats, b: RubricPerRecipientStats) =>
          a.recipientTeam.localeCompare(b.recipientTeam) || a.recipientName.localeCompare(b.recipientName),
      )
      .forEach((perRecipientStats: RubricPerRecipientStats) => {
        const perCriterionAverage: string = perRecipientStats.subQuestionWeightAverage
          .map((val: number) => this.getDisplayWeight(val))
          .toString();
        statsRows.push([
          perRecipientStats.recipientTeam,
          perRecipientStats.recipientName,
          perRecipientStats.recipientEmail ?? '',
          ...statsCalculation.choices.map((_: string, choiceIndex: number) => {
            return `${perRecipientStats.percentagesAverage[choiceIndex]}% \
(${perRecipientStats.answersSum[choiceIndex]}) \
[${this.getDisplayWeight(perRecipientStats.weightsAverage[choiceIndex])}]`;
          }),
          this.getDisplayWeight(perRecipientStats.overallWeightedSum),
          this.getDisplayWeight(perRecipientStats.overallWeightAverage),
          perCriterionAverage,
        ]);
      });

    return statsRows;
  }

  isParticipantCommentsOnResponsesAllowed(): boolean {
    return false;
  }

  isInstructorCommentsOnResponsesAllowed(): boolean {
    return true;
  }

  private getDisplayWeight(weight: number): string {
    return weight === null || weight === NO_VALUE ? '-' : String(weight);
  }
}
