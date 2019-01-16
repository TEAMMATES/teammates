import { TeammatesCommonModule } from './teammates-common.module';

describe('TeammatesCommonModule', () => {
  let teammatesCommonModule: TeammatesCommonModule;

  beforeEach(() => {
    teammatesCommonModule = new TeammatesCommonModule();
  });

  it('should create an instance', () => {
    expect(teammatesCommonModule).toBeTruthy();
  });
});
