import { McqMsqQuestionStatistics, McqMsqPerRecipientStatistics } from '../question-statistics.model';
import { AbstractFeedbackQuestionDetails } from './abstract-feedback-question-details';

/**
 * Abstract class for MCQ/MSQ question detail.
 */
export abstract class AbstractFeedbackMcqMsqQuestionDetails extends AbstractFeedbackQuestionDetails {
  protected getQuestionCsvStatsFrom(
    statsCalculation: McqMsqQuestionStatistics,
    hasAssignedWeights: boolean,
  ): string[][] {
    const statsRows: string[][] = [];

    const getDisplayValue = (val: number | null | undefined): string => {
      return val === null || val === undefined ? '-' : String(val);
    };

    if (hasAssignedWeights) {
      statsRows.push(['Choice', 'Weight', 'Response Count', 'Percentage (%)', 'Weighted Percentage (%)']);
    } else {
      statsRows.push(['Choice', 'Response Count', 'Percentage (%)']);
    }

    Object.keys(statsCalculation.answerFrequency)
      .sort()
      .forEach((answer: string) => {
        if (hasAssignedWeights) {
          statsRows.push([
            answer,
            getDisplayValue(statsCalculation.weightPerOption[answer]),
            String(statsCalculation.answerFrequency[answer]),
            String(statsCalculation.percentagePerOption[answer]),
            getDisplayValue(statsCalculation.weightedPercentagePerOption[answer]),
          ]);
        } else {
          statsRows.push([
            answer,
            String(statsCalculation.answerFrequency[answer]),
            String(statsCalculation.percentagePerOption[answer]),
          ]);
        }
      });

    if (!hasAssignedWeights) {
      return statsRows;
    }

    // generate per recipient stats
    statsRows.push([], ['Per Recipient Statistics']);

    statsRows.push([
      'Team',
      'Recipient Name',
      ...Object.keys(statsCalculation.weightPerOption).map(
        (choice: string) => `${choice} [${getDisplayValue(statsCalculation.weightPerOption[choice])}]`,
      ),
      'Total',
      'Average',
    ]);

    Object.keys(statsCalculation.perRecipientResponses)
      .sort()
      .forEach((recipient: string) => {
        const recipientResponses: McqMsqPerRecipientStatistics = statsCalculation.perRecipientResponses[recipient];
        statsRows.push([
          recipientResponses.recipientTeam,
          recipientResponses.recipient,
          ...Object.keys(statsCalculation.weightPerOption).map((choice: string) =>
            String(recipientResponses.responses[choice]),
          ),
          getDisplayValue(recipientResponses.total),
          getDisplayValue(recipientResponses.average),
        ]);
      });

    return statsRows;
  }
}
