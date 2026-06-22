import { FeedbackMcqMsqCourseWideStatistics } from '../api-output';
import { AbstractFeedbackQuestionDetails } from './abstract-feedback-question-details';

/**
 * Abstract class for MCQ/MSQ question detail.
 */
export abstract class AbstractFeedbackMcqMsqQuestionDetails extends AbstractFeedbackQuestionDetails {
  protected getQuestionCsvStatsFrom(stats: FeedbackMcqMsqCourseWideStatistics): string[][] {
    const statsRows: string[][] = [];

    if (stats.hasWeights) {
      statsRows.push(['Choice', 'Weight', 'Response Count', 'Percentage (%)', 'Weighted Percentage (%)']);
    } else {
      statsRows.push(['Choice', 'Response Count', 'Percentage (%)']);
    }

    for (const row of stats.rows) {
      if (stats.hasWeights) {
        statsRows.push([
          row.option,
          String(row.weight ?? '-'),
          String(row.count),
          String(row.percentage),
          String(row.weightedPercentage ?? '-'),
        ]);
      } else {
        statsRows.push([row.option, String(row.count), String(row.percentage)]);
      }
    }

    if (!stats.hasWeights || stats.perRecipientRows.length === 0) {
      return statsRows;
    }

    const optionLabels = stats.rows.map((r) => r.option);

    statsRows.push([], ['Per Recipient Statistics']);
    statsRows.push([
      'Team',
      'Recipient Name',
      ...stats.rows.map((r) => `${r.option} [${r.weight}]`),
      'Total',
      'Average',
    ]);

    for (const row of stats.perRecipientRows) {
      statsRows.push([
        row.recipientTeam,
        row.recipientName,
        ...optionLabels.map((label) => String(row.responseCountPerOption[label] ?? 0)),
        String(row.total),
        String(row.average),
      ]);
    }

    return statsRows;
  }
}
