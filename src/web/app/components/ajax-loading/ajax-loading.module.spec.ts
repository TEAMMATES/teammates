import { AjaxLoadingModule } from './ajax-loading.module';

describe('AjaxLoadingModule', () => {
  let ajaxLoadingModule: AjaxLoadingModule;

  beforeEach(() => {
    ajaxLoadingModule = new AjaxLoadingModule();
  });

  it('should create an instance', () => {
    expect(ajaxLoadingModule).toBeTruthy();
  });
});
