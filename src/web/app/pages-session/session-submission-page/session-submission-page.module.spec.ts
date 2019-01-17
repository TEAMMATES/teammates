import { SessionSubmissionPageModule } from './session-submission-page.module';

describe('SessionSubmissionPageModule', () => {
  let sessionSubmissionPageModule: SessionSubmissionPageModule;

  beforeEach(() => {
    sessionSubmissionPageModule = new SessionSubmissionPageModule();
  });

  it('should create an instance', () => {
    expect(sessionSubmissionPageModule).toBeTruthy();
  });
});
