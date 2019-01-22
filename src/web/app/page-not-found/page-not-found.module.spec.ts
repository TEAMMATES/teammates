import { PageNotFoundModule } from './page-not-found.module';

describe('PageNotFoundModule', () => {
  let pageNotFoundModule: PageNotFoundModule;

  beforeEach(() => {
    pageNotFoundModule = new PageNotFoundModule();
  });

  it('should create an instance', () => {
    expect(pageNotFoundModule).toBeTruthy();
  });
});
