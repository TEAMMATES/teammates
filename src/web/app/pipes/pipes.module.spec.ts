import { Pipes } from './pipes.module';
import { ResponseStatusPipe } from './sessionResponseStatus.pipe';
import { SubmissionStatusPipe } from './sessionSubmissionStatus.pipe';

describe('Pipes', () => {
  let pipes: Pipes;
  let responseStatusPipe: ResponseStatusPipe;
  let submissionStatusPipe: SubmissionStatusPipe;

  beforeEach(() => {
    pipes = new Pipes;
    responseStatusPipe = new ResponseStatusPipe();
    submissionStatusPipe = new SubmissionStatusPipe();
  });

  /**
   * Tests for response status pipe.
   */
  it('should be instantiated', () => {
    expect(responseStatusPipe).toBeTruthy();
  });

  it('should return Published when session is published', () => {
    expect(responseStatusPipe.transform(true)).toBe('Published');
  });

  it('should return Not Published when session is not published', () => {
    expect(responseStatusPipe.transform(false)).toBe('Not Published');
  });

  /**
   * Tests for submission status pipe.
   */
  it('should be instantiated', () => {
    expect(submissionStatusPipe).toBeTruthy();
  });

  it('should return Submitted if session is open and feedback is submitted', () => {
    expect(submissionStatusPipe.transform(true, false, true)).toBe('Submitted');
  });

  it('should return Pending if session is open and feedback is not submitted', () => {
    expect(submissionStatusPipe.transform(true, false, false)).toBe('Pending');
  });

  it('should return Awaiting if session is waiting to open', () => {
    expect(submissionStatusPipe.transform(false, true, false)).toBe('Awaiting');
  });

  it('should return Closed if session is not open and feedback is submitted', () => {
    expect(submissionStatusPipe.transform(false, false, true)).toBe('Closed');
  });

  it('should return Closed if session is not open and feedback is not submitted', () => {
    expect(submissionStatusPipe.transform(false, false, false)).toBe('Closed');
  });
});
