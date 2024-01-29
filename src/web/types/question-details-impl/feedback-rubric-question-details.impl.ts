import { AbstractFeedbackQuestionDetails } from './abstract-feedback-question-details';
import {
  PerRecipientStats,
  RubricQuestionStatisticsCalculation,
} from '../../app/components/question-types/question-statistics/question-statistics-calculation/rubric-question-statistics-calculation';
import { StringHelper } from '../../services/string-helper';
import {
  FeedbackQuestionType,
  FeedbackRubricQuestionDetails, QuestionOutput,
} from '../api-output';
import { NO_VALUE } from '../feedback-response-details';

/**
 * Concrete implementation of {@link FeedbackRubricQuestionDetails}.
 */
export class FeedbackRubricQuestionDetailsImpl extends AbstractFeedbackQuestionDetails
    implements FeedbackRubricQuestionDetails {

  hasAssignedWeights: boolean = false;
  rubricChoices: string[] = [];
  rubricSubQuestions: string[] = [];
  rubricWeightsForEachCell: number[][] = [];
  rubricDescriptions: string[][] = [];
  questionText: string = '';
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

    const statsCalculation: RubricQuestionStatisticsCalculation = new RubricQuestionStatisticsCalculation(this);
    this.populateQuestionStatistics(statsCalculation, question);
    if (statsCalculation.responses.length === 0) {
      // skip stats for no response
      return [];
    }
    statsCalculation.calculateStatistics();

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
${statsCalculation.hasWeights
    ? `[${this.getDisplayWeight(statsCalculation.weights[questionIndex][choiceIndex])}]`
    : ''}`;
        }),
      ];
      if (statsCalculation.hasWeights) {
        currRow.push(String(this.getDisplayWeight(statsCalculation.subQuestionWeightAverage[questionIndex])));
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
        .sort((a: PerRecipientStats, b: PerRecipientStats) =>
            a.recipientTeam.localeCompare(b.recipientTeam) || a.recipientName.localeCompare(b.recipientName))
        .forEach((perRecipientStats: PerRecipientStats) => {
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
              String(this.getDisplayWeight(perRecipientStats.subQuestionTotalChosenWeight[questionIndex])),
              String(this.getDisplayWeight(perRecipientStats.subQuestionWeightAverage[questionIndex])),
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
      .sort((a: PerRecipientStats, b: PerRecipientStats) =>
        a.recipientTeam.localeCompare(b.recipientTeam) || a.recipientName.localeCompare(b.recipientName))
      .forEach((perRecipientStats: PerRecipientStats) => {
        const perCriterionAverage: string =
            perRecipientStats.subQuestionWeightAverage.map((val: number) =>
            this.getDisplayWeight(val)).toString();
        statsRows.push([
          perRecipientStats.recipientTeam,
          perRecipientStats.recipientName,
          perRecipientStats.recipientEmail ?? '',
          ...statsCalculation.choices.map((_: string, choiceIndex: number) => {
          return `${perRecipientStats.percentagesAverage[choiceIndex]}% \
(${perRecipientStats.answersSum[choiceIndex]}) \
[${this.getDisplayWeight(perRecipientStats.weightsAverage[choiceIndex])}]`;
          }),
          String(this.getDisplayWeight(perRecipientStats.overallWeightedSum)),
          String(this.getDisplayWeight(perRecipientStats.overallWeightAverage)),
          String(perCriterionAverage),
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

  private getDisplayWeight(weight: number): any {
    return weight === null || weight === NO_VALUE ? '-' : weight;
  }
}
