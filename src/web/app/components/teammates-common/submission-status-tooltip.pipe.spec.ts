import { SubmissionStatusTooltipPipe } from './submission-status-tooltip.pipe';
import { FeedbackSessionSubmissionStatus } from '../../../types/api-output';

describe('SubmissionStatusTooltipPipe', () => {
  let pipe: SubmissionStatusTooltipPipe;

  beforeEach(() => {
    pipe = new SubmissionStatusTooltipPipe();
  });

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('transforms correctly', () => {
    const notVisibleWithExtension = pipe.transform(FeedbackSessionSubmissionStatus.NOT_VISIBLE);
    expect(notVisibleWithExtension).toEqual(
      'The feedback session is waiting to open for submissions, and is not yet visible to respondents.',
    );

    const visibleWithExtension = pipe.transform(FeedbackSessionSubmissionStatus.VISIBLE_NOT_OPEN);
    expect(visibleWithExtension).toEqual(
      'The feedback session is waiting to open for submissions, but is visible to respondents.',
    );

    const openWithExtension = pipe.transform(FeedbackSessionSubmissionStatus.OPEN);
    expect(openWithExtension).toEqual('The feedback session is open for submissions, and is visible to respondents.');

    const gracePeriodWithExtension = pipe.transform(FeedbackSessionSubmissionStatus.GRACE_PERIOD);
    expect(gracePeriodWithExtension).toEqual(
      'The feedback session is open for submissions, is in the grace period, and is visible to respondents.',
    );

    const closedWithExtension = pipe.transform(FeedbackSessionSubmissionStatus.CLOSED);
    expect(closedWithExtension).toEqual(
      'The feedback session is closed for submissions, and is visible to respondents.',
    );
  });
});
