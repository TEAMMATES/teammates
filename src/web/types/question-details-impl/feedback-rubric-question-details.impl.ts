// tslint:disable-next-line:max-line-length
import { PerRecipientStats, RubricQuestionStatisticsCalculation } from '../../app/components/question-types/question-statistics/question-statistics-calculation/rubric-question-statistics-calculation';
import { StringHelper } from '../../services/string-helper';
import {
  FeedbackQuestionType,
  FeedbackRubricQuestionDetails, QuestionOutput,
} from '../api-output';
import { AbstractFeedbackQuestionDetails } from './abstract-feedback-question-details';

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

  getQuestionCsvHeaders(): string[] {
    return ['Sub Question', 'Choice Value', 'Choice Number'];
  }

  getMissingResponseCsvAnswers(): string[][] {
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
          return `${ statsCalculation.percentages[questionIndex][choiceIndex] }% \
(${ statsCalculation.answers[questionIndex][choiceIndex] }) \
${ statsCalculation.hasWeights ? `[${ statsCalculation.weights[questionIndex][choiceIndex] }]` : '' }`;
        }),
      ];
      if (statsCalculation.hasWeights) {
        currRow.push(String(statsCalculation.subQuestionWeightAverage[questionIndex]));
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
      "Recipient's Email",
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
              perRecipientStats.recipientEmail ? perRecipientStats.recipientEmail : '',
              `${StringHelper.integerToLowerCaseAlphabeticalIndex(questionIndex + 1)}) ${subQuestion}`,
              ...statsCalculation.choices.map((_: string, choiceIndex: number) => {
                return `${ perRecipientStats.percentages[questionIndex][choiceIndex] }% \
(${ perRecipientStats.answers[questionIndex][choiceIndex] }) \
[${ statsCalculation.weights[questionIndex][choiceIndex] }]`;
              }),
              String(perRecipientStats.subQuestionTotalChosenWeight[questionIndex]),
              String(perRecipientStats.subQuestionWeightAverage[questionIndex]),
            ]);
          });
        });

    // generate overall recipient stats
    statsRows.push([], ['Per Recipient Statistics (Overall)']);

    statsRows.push([
      'Team',
      'Recipient Name',
      "Recipient's Email",
      'Average',
      'Breakdown',
    ]);

    Object.values(statsCalculation.perRecipientStatsMap)
      .sort((a: PerRecipientStats, b: PerRecipientStats) =>
        a.recipientTeam.localeCompare(b.recipientTeam) || a.recipientName.localeCompare(b.recipientName))
      .forEach((perRecipientStats: PerRecipientStats) => {
        statsRows.push([
          perRecipientStats.recipientTeam,
          perRecipientStats.recipientName,
          perRecipientStats.recipientEmail ? perRecipientStats.recipientEmail : '',
          String(perRecipientStats.weightAverage),
          perRecipientStats.subQuestionWeightAverage.toString(),
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
}
