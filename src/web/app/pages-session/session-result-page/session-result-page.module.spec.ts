import { SessionResultPageModule } from './session-result-page.module';

describe('SessionResultPageModule', () => {
  let sessionResultPageModule: SessionResultPageModule;

  beforeEach(() => {
    sessionResultPageModule = new SessionResultPageModule();
  });

  it('should create an instance', () => {
    expect(sessionResultPageModule).toBeTruthy();
  });
});
