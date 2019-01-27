import { StaticPagesModule } from './static-pages.module';

describe('StaticPagesModule', () => {
  let staticPagesModule: StaticPagesModule;

  beforeEach(() => {
    staticPagesModule = new StaticPagesModule();
  });

  it('should create an instance', () => {
    expect(staticPagesModule).toBeTruthy();
  });
});
