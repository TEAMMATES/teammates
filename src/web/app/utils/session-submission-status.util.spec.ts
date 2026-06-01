import { sessionSubmissionStatusDisplay } from './session-submission-status.util';

describe('sessionSubmissionStatusDisplay', () => {
  it('should return "Awaiting" when isWaitingToOpen is true', () => {
    expect(sessionSubmissionStatusDisplay(false, true, false)).toBe('Awaiting');
  });

  it('should return "Closed" when not opened and not waiting', () => {
    expect(sessionSubmissionStatusDisplay(false, false, false)).toBe('Closed');
  });

  it('should return "Pending" when opened and not submitted', () => {
    expect(sessionSubmissionStatusDisplay(true, false, false)).toBe('Pending');
  });

  it('should return "Submitted" when opened and submitted', () => {
    expect(sessionSubmissionStatusDisplay(true, false, true)).toBe('Submitted');
  });

  it('should return "Pending (with Extension)" when opened, not submitted, and has extension', () => {
    expect(sessionSubmissionStatusDisplay(true, false, false, true)).toBe('Pending (with Extension)');
  });

  it('should return "Submitted (with Extension)" when opened, submitted, and has extension', () => {
    expect(sessionSubmissionStatusDisplay(true, false, true, true)).toBe('Submitted (with Extension)');
  });

  it('should prioritize isWaitingToOpen over isOpened', () => {
    expect(sessionSubmissionStatusDisplay(true, true, false)).toBe('Awaiting');
  });
});
