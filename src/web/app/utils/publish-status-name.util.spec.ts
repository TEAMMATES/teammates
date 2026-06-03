import { publishStatusNameToString } from './publish-status-name.util';
import { FeedbackSessionPublishStatus } from '../../types/api-output';

describe('publishStatusNameToString', () => {
  it('should return "Published" for PUBLISHED status', () => {
    expect(publishStatusNameToString(FeedbackSessionPublishStatus.PUBLISHED)).toBe('Published');
  });

  it('should return "Not Published" for NOT_PUBLISHED status', () => {
    expect(publishStatusNameToString(FeedbackSessionPublishStatus.NOT_PUBLISHED)).toBe('Not Published');
  });

  it('should return "Unknown" when state is undefined', () => {
    expect(publishStatusNameToString(undefined)).toBe('Unknown');
  });

  it('should return "Unknown" when state is not provided', () => {
    expect(publishStatusNameToString()).toBe('Unknown');
  });
});
