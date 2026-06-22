import { AbstractFeedbackQuestionDetails } from './abstract-feedback-question-details';
import {
  FeedbackNumericalScaleQuestionDetails,
  FeedbackQuestionType,
  NumScaleRecipientRow,
  QuestionOutput,
} from '../api-output';
import { QuestionStatisticsTypeChecker } from '../question-statistics-impl/question-statistics-caster';

/**
 * Concrete implementation of {@link FeedbackNumericalScaleQuestionDetails}.
 */
export class FeedbackNumericalScaleQuestionDetailsImpl
  extends AbstractFeedbackQuestionDetails
  implements FeedbackNumericalScaleQuestionDetails
{
  minScale = 1;
  maxScale = 5;
  step = 0.5;
  questionText = '';
  questionType: FeedbackQuestionType = FeedbackQuestionType.NUMSCALE;

  constructor(apiOutput: FeedbackNumericalScaleQuestionDetails) {
    super();
    this.minScale = apiOutput.minScale;
    this.maxScale = apiOutput.maxScale;
    this.step = apiOutput.step;
    this.questionText = apiOutput.questionText;
  }

  getQuestionCsvStats(question: QuestionOutput): string[][] {
    const stats = question.questionStatistics;
    if (!QuestionStatisticsTypeChecker.isNumscale(stats) || stats.rows.length === 0) {
      return [];
    }

    const showExcludeSelf = stats.rows.some((row) => row.averageExcludingSelf != null);
    const header: string[] = ['Team', 'Recipient', 'Recipient Email', 'Average', 'Minimum', 'Maximum'];
    if (showExcludeSelf) {
      header.push('Average excluding self response');
    }

    const sortedRows = [...stats.rows].sort((a: NumScaleRecipientRow, b: NumScaleRecipientRow) => {
      const teamCmp = a.recipientTeam.localeCompare(b.recipientTeam);
      return teamCmp === 0 ? a.recipientName.localeCompare(b.recipientName) : teamCmp;
    });

    const dataRows: string[][] = sortedRows.map((row: NumScaleRecipientRow) => {
      const currRow: string[] = [
        row.recipientTeam,
        row.recipientName,
        row.recipientEmail ?? '',
        String(row.average ?? ''),
        String(row.min ?? ''),
        String(row.max ?? ''),
      ];
      if (showExcludeSelf) {
        currRow.push(String(row.averageExcludingSelf ?? ''));
      }
      return currRow;
    });

    return [header, ...dataRows];
  }

  isParticipantCommentsOnResponsesAllowed(): boolean {
    return false;
  }

  isInstructorCommentsOnResponsesAllowed(): boolean {
    return true;
  }
}
