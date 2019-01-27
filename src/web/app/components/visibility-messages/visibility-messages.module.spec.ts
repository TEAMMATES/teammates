import { VisibilityMessagesModule } from './visibility-messages.module';

describe('VisibilityMessagesModule', () => {
  let visibilityMessagesModule: VisibilityMessagesModule;

  beforeEach(() => {
    visibilityMessagesModule = new VisibilityMessagesModule();
  });

  it('should create an instance', () => {
    expect(visibilityMessagesModule).toBeTruthy();
  });
});
