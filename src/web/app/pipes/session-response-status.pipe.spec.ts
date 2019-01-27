import { ResponseStatusPipe } from './session-response-status.pipe';

describe('ResponseStatusPipe', () => {
  let responseStatusPipe: ResponseStatusPipe;

  beforeEach(() => {
    responseStatusPipe = new ResponseStatusPipe();
  });

  it('should be instantiated', () => {
    expect(responseStatusPipe).toBeTruthy();
  });

  it('should return Published when session is published', () => {
    expect(responseStatusPipe.transform(true)).toBe('Published');
  });

  it('should return Not Published when session is not published', () => {
    expect(responseStatusPipe.transform(false)).toBe('Not Published');
  });
});
