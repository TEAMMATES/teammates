import { SubmissionStatusNamePipe } from './submission-status-name.pipe';
import { FeedbackSessionSubmissionStatus } from '../../../types/api-output';

describe('SubmissionStatusNamePipe', () => {
  let pipe: SubmissionStatusNamePipe;

  beforeEach(() => {
    pipe = new SubmissionStatusNamePipe();
  });

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('transform with deadlines correctly', () => {
    const notVisibleWithExtension = pipe.transform(FeedbackSessionSubmissionStatus.NOT_VISIBLE);
    expect(notVisibleWithExtension).toEqual('Awaiting');

    const visibleWithExtension = pipe.transform(FeedbackSessionSubmissionStatus.VISIBLE_NOT_OPEN);
    expect(visibleWithExtension).toEqual('Awaiting');

    const openWithExtension = pipe.transform(FeedbackSessionSubmissionStatus.OPEN);
    expect(openWithExtension).toEqual('Open');

    const gracePeriodWithExtension = pipe.transform(FeedbackSessionSubmissionStatus.GRACE_PERIOD);
    expect(gracePeriodWithExtension).toEqual('Open (grace period)');

    const closedWithExtension = pipe.transform(FeedbackSessionSubmissionStatus.CLOSED);
    expect(closedWithExtension).toEqual('Closed');
  });
});
