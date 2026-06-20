import { AbstractFeedbackQuestionDetails } from './abstract-feedback-question-details';
import { StringHelper } from '../../services/string-helper';
import {
  FeedbackQuestionType,
  FeedbackRubricQuestionDetails,
  FeedbackRubricStatistics,
  QuestionOutput,
  RubricPerRecipientStats,
  RubricSubQuestionRow,
} from '../api-output';
import { QuestionStatisticsTypeChecker } from '../question-statistics-impl/question-statistics-caster';

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
    const stats = question.questionStatistics;
    if (!QuestionStatisticsTypeChecker.isRubricCourseWide(stats)) {
      return [];
    }
    return this.buildCsvStats(stats);
  }

  private buildCsvStats(stats: FeedbackRubricStatistics): string[][] {
    const statsRows: string[][] = [];

    const header: string[] = ['', ...stats.choices];
    if (stats.hasWeights) {
      header.push('Average');
    }
    statsRows.push(header);

    stats.rows.forEach((row: RubricSubQuestionRow, questionIndex: number) => {
      const currRow: string[] = [
        `${StringHelper.integerToLowerCaseAlphabeticalIndex(questionIndex + 1)}) ${row.subQuestion}`,
        ...row.cells.map((cell) => {
          const weightStr = cell.weight == null ? '' : ` [${cell.weight}]`;
          return `${cell.percentage}% (${cell.count})${weightStr}`;
        }),
      ];
      if (stats.hasWeights) {
        currRow.push(this.getDisplayWeight(row.weightAverage));
      }
      statsRows.push(currRow);
    });

    if (!stats.hasWeights || stats.perRecipientStats.length === 0) {
      return statsRows;
    }

    statsRows.push(
      [],
      ['Per Recipient Statistics (Per Criterion)'],
      ['Team', 'Recipient Name', 'Recipient Email', 'Sub Question', ...stats.choices, 'Total', 'Average'],
    );

    [...stats.perRecipientStats]
      .sort((a: RubricPerRecipientStats, b: RubricPerRecipientStats) => {
        const teamCmp = a.recipientTeam.localeCompare(b.recipientTeam);
        return teamCmp === 0 ? a.recipientName.localeCompare(b.recipientName) : teamCmp;
      })
      .forEach((perRecipientStats: RubricPerRecipientStats) => {
        perRecipientStats.perCriterionRows.forEach((criterionRow, questionIndex) => {
          statsRows.push([
            perRecipientStats.recipientTeam,
            perRecipientStats.recipientName,
            perRecipientStats.recipientEmail ?? '',
            `${StringHelper.integerToLowerCaseAlphabeticalIndex(questionIndex + 1)}) ${criterionRow.subQuestion}`,
            ...criterionRow.cells.map((cell) => {
              const weightStr = cell.weight == null ? '' : ` [${cell.weight}]`;
              return `${cell.percentage}% (${cell.count})${weightStr}`;
            }),
            this.getDisplayWeight(criterionRow.total),
            this.getDisplayWeight(criterionRow.average),
          ]);
        });
      });

    statsRows.push(
      [],
      ['Per Recipient Statistics (Overall)'],
      ['Team', 'Recipient Name', 'Recipient Email', ...stats.choices, 'Total', 'Average', 'Per Criterion Average'],
    );

    [...stats.perRecipientStats]
      .sort((a: RubricPerRecipientStats, b: RubricPerRecipientStats) => {
        const teamCmp = a.recipientTeam.localeCompare(b.recipientTeam);
        return teamCmp === 0 ? a.recipientName.localeCompare(b.recipientName) : teamCmp;
      })
      .forEach((perRecipientStats: RubricPerRecipientStats) => {
        const perCriterionAverage: string = perRecipientStats.subQuestionAverages
          .map((val: number | null) => this.getDisplayWeight(val))
          .toString();
        statsRows.push([
          perRecipientStats.recipientTeam,
          perRecipientStats.recipientName,
          perRecipientStats.recipientEmail ?? '',
          ...perRecipientStats.overallCells.map((cell) => {
            const weightStr = cell.weight == null ? '' : ` [${cell.weight}]`;
            return `${cell.percentage}% (${cell.count})${weightStr}`;
          }),
          this.getDisplayWeight(perRecipientStats.overallTotal),
          this.getDisplayWeight(perRecipientStats.overallAverage),
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

  private getDisplayWeight(weight: number | null | undefined): string {
    return weight == null ? '-' : String(weight);
  }
}
