import { publishStatusTooltipUtilToString } from './publish-status-tooltip.util';
import { FeedbackSessionPublishStatus } from '../../types/api-output';

describe('publishStatusTooltipUtilToString', () => {
  it('should return correct message for PUBLISHED status', () => {
    expect(publishStatusTooltipUtilToString(FeedbackSessionPublishStatus.PUBLISHED)).toBe(
      'Respondents can view responses received, as per the visibility settings of each question.',
    );
  });

  it('should return correct message for NOT_PUBLISHED status', () => {
    expect(publishStatusTooltipUtilToString(FeedbackSessionPublishStatus.NOT_PUBLISHED)).toBe(
      'Respondents cannot view responses received.',
    );
  });

  it('should return "Unknown" for an unrecognized status', () => {
    expect(publishStatusTooltipUtilToString('INVALID' as FeedbackSessionPublishStatus)).toBe('Unknown');
  });
});
