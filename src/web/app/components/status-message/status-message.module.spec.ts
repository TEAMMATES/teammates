import { StatusMessageModule } from './status-message.module';

describe('StatusMessageModule', () => {
  let statusMessageModule: StatusMessageModule;

  beforeEach(() => {
    statusMessageModule = new StatusMessageModule();
  });

  it('should create an instance', () => {
    expect(statusMessageModule).toBeTruthy();
  });
});
