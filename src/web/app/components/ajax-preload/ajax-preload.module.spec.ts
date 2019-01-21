import { AjaxPreloadModule } from './ajax-preload.module';

describe('AjaxPreloadModule', () => {
  let ajaxPreloadModule: AjaxPreloadModule;

  beforeEach(() => {
    ajaxPreloadModule = new AjaxPreloadModule();
  });

  it('should create an instance', () => {
    expect(ajaxPreloadModule).toBeTruthy();
  });
});
