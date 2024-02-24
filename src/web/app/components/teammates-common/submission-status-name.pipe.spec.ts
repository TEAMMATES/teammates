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

  it('transform with no deadlines correctly', () => {
    jest.useFakeTimers().setSystemTime(new Date('2020-01-01').getTime());
    const hasNoDeadlines = {
      studentDeadlines: {},
      instructorDeadlines: {},
    };
    const hasNoOngoingDeadlines = {
      studentDeadlines: { nonOngoingExtension: new Date('2019-01-01').valueOf() },
      instructorDeadlines: {},
    };
    expect(pipe.transform(FeedbackSessionSubmissionStatus.VISIBLE_NOT_OPEN, hasNoDeadlines)).toEqual(
      pipe.transform(FeedbackSessionSubmissionStatus.VISIBLE_NOT_OPEN),
    );
    expect(pipe.transform(FeedbackSessionSubmissionStatus.OPEN, hasNoOngoingDeadlines)).toEqual(
      pipe.transform(FeedbackSessionSubmissionStatus.OPEN),
    );
  });

  it('transform with deadlines correctly', () => {
    jest.useFakeTimers().setSystemTime(new Date('2020-01-01').getTime());
    const hasOngoingDeadlines = {
      studentDeadlines: { ongoingExtension: new Date('2021-01-01').valueOf() },
      instructorDeadlines: { nonOngoingExtension: new Date('2019-01-01').valueOf() },
    };
    const extensionMessage = '(Ext. ongoing)';

    const notVisibleWithExtension = pipe.transform(FeedbackSessionSubmissionStatus.NOT_VISIBLE, hasOngoingDeadlines);
    expect(notVisibleWithExtension.endsWith(extensionMessage)).toBeTruthy();

    const visibleWithExtension = pipe.transform(FeedbackSessionSubmissionStatus.VISIBLE_NOT_OPEN, hasOngoingDeadlines);
    expect(visibleWithExtension.endsWith(extensionMessage)).toBeTruthy();

    const openWithExtension = pipe.transform(FeedbackSessionSubmissionStatus.OPEN, hasOngoingDeadlines);
    expect(openWithExtension.endsWith(extensionMessage)).toBeTruthy();

    const gracePeriodWithExtension = pipe.transform(FeedbackSessionSubmissionStatus.GRACE_PERIOD, hasOngoingDeadlines);
    expect(gracePeriodWithExtension.endsWith(extensionMessage)).toBeTruthy();

    const closedWithExtension = pipe.transform(FeedbackSessionSubmissionStatus.CLOSED, hasOngoingDeadlines);
    expect(closedWithExtension.endsWith(extensionMessage)).toBeTruthy();
  });
});
