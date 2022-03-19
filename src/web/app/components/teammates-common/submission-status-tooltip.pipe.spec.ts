import { FeedbackSessionSubmissionStatus } from '../../../types/api-output';
import { SubmissionStatusTooltipPipe } from './submission-status-tooltip.pipe';

describe('SubmissionStatusTooltipPipe', () => {
  it('create an instance', () => {
    const pipe: SubmissionStatusTooltipPipe = new SubmissionStatusTooltipPipe();
    expect(pipe).toBeTruthy();
  });

  it('transform with no deadlines correctly', () => {
    jest.useFakeTimers().setSystemTime(new Date('2020-01-01').getTime());
    const hasNoDeadlines = {
      studentDeadlines: {},
      instructorDeadlines: {},
    };
    const hasNoOngoingDeadlines = {
      studentDeadlines: { nonOngoingExtension1: new Date('2019-01-01').valueOf() },
      instructorDeadlines: { nonOngoingExtension2: new Date('2019-02-01').valueOf() },
    };
    const pipe: SubmissionStatusTooltipPipe = new SubmissionStatusTooltipPipe();

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
      studentDeadlines: { ongoingDeadline: new Date('2021-01-01').valueOf() },
      instructorDeadlines: { nonOngoingDeadline: new Date('2019-01-01').valueOf() },
    };

    const pipe: SubmissionStatusTooltipPipe = new SubmissionStatusTooltipPipe();
    const extensionMessage = 'with current ongoing session extensions.';

    const notVisibileWithExtension = pipe.transform(FeedbackSessionSubmissionStatus.NOT_VISIBLE, hasOngoingDeadlines);
    expect(notVisibileWithExtension.substring(
      notVisibileWithExtension.length - extensionMessage.length, notVisibileWithExtension.length),
    ).toEqual(extensionMessage);

    const visibileWithExtension = pipe.transform(FeedbackSessionSubmissionStatus.VISIBLE_NOT_OPEN, hasOngoingDeadlines);
    expect(visibileWithExtension.substring(
      visibileWithExtension.length - extensionMessage.length, visibileWithExtension.length),
    ).toEqual(extensionMessage);

    const openWithExtension = pipe.transform(FeedbackSessionSubmissionStatus.OPEN, hasOngoingDeadlines);
    expect(openWithExtension.substring(
      openWithExtension.length - extensionMessage.length, openWithExtension.length),
    ).toEqual(extensionMessage);

    const gracePeriodWithExtension = pipe.transform(FeedbackSessionSubmissionStatus.GRACE_PERIOD, hasOngoingDeadlines);
    expect(gracePeriodWithExtension.substring(
      gracePeriodWithExtension.length - extensionMessage.length, gracePeriodWithExtension.length),
    ).toEqual(extensionMessage);

    const closedWithExtension = pipe.transform(FeedbackSessionSubmissionStatus.CLOSED, hasOngoingDeadlines);
    expect(closedWithExtension.substring(
      closedWithExtension.length - extensionMessage.length, closedWithExtension.length),
    ).toEqual(extensionMessage);
  });
});
