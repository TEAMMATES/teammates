import { DeadlineExtensionHelper } from './deadline-extension-helper';

describe('DeadlineExtensionHelper', () => {
  it('should detect ongoing extensions correctly', () => {
    const timeNow = Date.now();
    const fixedLengthOfTime = 1000000;

    const ongoingExtension: Record<string, number> = { ongoingExtension1: timeNow + fixedLengthOfTime };
    const notOngoingExtension1: Record<string, number> = { notOngoingExtension1: timeNow - fixedLengthOfTime };
    const notOngoingExtension2: Record<string, number> = { notOngoingExtension2: timeNow };
    const hasOngingDeadlines: Record<string, number> = {
        ...ongoingExtension, ...notOngoingExtension1, ...notOngoingExtension2,
    };
    const hasNoOngoingDeadlines: Record<string, number> = {
        ...notOngoingExtension1, ...notOngoingExtension2,
    };

    expect(DeadlineExtensionHelper.hasOngoingExtension({
        studentDeadlines: hasOngingDeadlines,
        instructorDeadlines: hasNoOngoingDeadlines,
    })).toBeTruthy();

    expect(DeadlineExtensionHelper.hasOngoingExtension({
        studentDeadlines: hasNoOngoingDeadlines,
        instructorDeadlines: hasOngingDeadlines,
    })).toBeTruthy();

    expect(DeadlineExtensionHelper.hasOngoingExtension({
        studentDeadlines: hasOngingDeadlines,
        instructorDeadlines: hasOngingDeadlines,
    })).toBeTruthy();

    expect(DeadlineExtensionHelper.hasOngoingExtension({
        studentDeadlines: hasNoOngoingDeadlines,
        instructorDeadlines: hasNoOngoingDeadlines,
    })).toBeFalsy();
  });
});
