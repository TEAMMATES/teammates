import { PublishStatusTooltipPipe } from './publish-status-tooltip.pipe';
import { FeedbackSessionPublishStatus } from '../../../types/api-output';

describe('PublishStatusTooltipPipe', () => {
  let pipe: PublishStatusTooltipPipe;

  beforeEach(() => {
    pipe = new PublishStatusTooltipPipe();
  });

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should return the correct tooltip for PUBLISHED status', () => {
    const status = FeedbackSessionPublishStatus.PUBLISHED;
    const expectedTooltip = 'Respondents can view responses received, as per the visibility settings of each question.';
    const result = pipe.transform(status);
    expect(result).toBe(expectedTooltip);
  });

  it('should return the correct tooltip for NOT_PUBLISHED status', () => {
    const status = FeedbackSessionPublishStatus.NOT_PUBLISHED;
    const expectedTooltip = 'Respondents cannot view responses received.';
    const result = pipe.transform(status);
    expect(result).toBe(expectedTooltip);
  });

  it('should return "Unknown" for an undefined status', () => {
    const status = undefined as any; // Casting to 'any' to simulate an undefined value
    const expectedTooltip = 'Unknown';
    const result = pipe.transform(status);
    expect(result).toBe(expectedTooltip);
  });

  it('should return "Unknown" for an invalid status', () => {
    const status = 'INVALID_STATUS' as any; // Casting to 'any' to simulate an invalid value
    const expectedTooltip = 'Unknown';
    const result = pipe.transform(status);
    expect(result).toBe(expectedTooltip);
  });
});
