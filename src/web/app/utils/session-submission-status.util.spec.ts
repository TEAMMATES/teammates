import { sessionSubmissionStatusDisplay } from './session-submission-status.util';

describe('sessionSubmissionStatusDisplay', () => {
  it('should return "Closed" when not opened', () => {
    expect(sessionSubmissionStatusDisplay(false, false)).toBe('Closed');
  });

  it('should return "Pending" when opened and not submitted', () => {
    expect(sessionSubmissionStatusDisplay(true, false)).toBe('Pending');
  });

  it('should return "Submitted" when opened and submitted', () => {
    expect(sessionSubmissionStatusDisplay(true, true)).toBe('Submitted');
  });

  it('should return "Pending (with Extension)" when opened, not submitted, and has extension', () => {
    expect(sessionSubmissionStatusDisplay(true, false, true)).toBe('Pending (with Extension)');
  });

  it('should return "Submitted (with Extension)" when opened, submitted, and has extension', () => {
    expect(sessionSubmissionStatusDisplay(true, true, true)).toBe('Submitted (with Extension)');
  });
});
