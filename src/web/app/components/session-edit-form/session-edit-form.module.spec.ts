import { SessionEditFormModule } from './session-edit-form.module';

describe('SessionEditFormModule', () => {
  let sessionEditFormModule: SessionEditFormModule;

  beforeEach(() => {
    sessionEditFormModule = new SessionEditFormModule();
  });

  it('should create an instance', () => {
    expect(sessionEditFormModule).toBeTruthy();
  });
});
