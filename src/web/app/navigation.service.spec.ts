import { TestBed, inject } from '@angular/core/testing';
import { Router } from '@angular/router';
import { NavigationService } from './navigation.service';

class MockWindow {
  /**
   * Stub method.
   */
  public open(url: string): string {
    return url;
  }
}

describe('NavigationService', () => {
  let spyRouter: jasmine.SpyObj<Router>;
  let spyWindow: jasmine.SpyObj<MockWindow>;

  beforeEach(() => {
    spyRouter = jasmine.createSpyObj('Router', {
      navigateByUrl: {
        then: (fn: () => void): void => {
          fn();
        },
      },
    });
    spyWindow = jasmine.createSpyObj('MockWindow', ['open']);

    TestBed.configureTestingModule({
      providers: [
        NavigationService,
      ],
    });
  });

  it('should be created', inject([NavigationService], (service: NavigationService) => {
    expect(service).toBeTruthy();
  }));

  it('should activate router when no event is fired', inject([NavigationService], (service: NavigationService) => {
    service.navigateTo(spyRouter, 'url', {}, spyWindow);
    expect(spyRouter.navigateByUrl).toHaveBeenCalledWith('url');
    expect(spyWindow.open).not.toHaveBeenCalled();
  }));

  it('should activate window when CTRL key is fired', inject([NavigationService], (service: NavigationService) => {
    service.navigateTo(spyRouter, 'url', { ctrlKey: true }, spyWindow);
    expect(spyWindow.open).toHaveBeenCalledWith('url');
    expect(spyRouter.navigateByUrl).not.toHaveBeenCalled();
  }));

  it('should activate window when CMD key is fired', inject([NavigationService], (service: NavigationService) => {
    service.navigateTo(spyRouter, 'url', { metaKey: true }, spyWindow);
    expect(spyWindow.open).toHaveBeenCalledWith('url');
    expect(spyRouter.navigateByUrl).not.toHaveBeenCalled();
  }));

  it('should activate window when SHIFT key is fired', inject([NavigationService], (service: NavigationService) => {
    service.navigateTo(spyRouter, 'url', { shiftKey: true }, spyWindow);
    expect(spyWindow.open).toHaveBeenCalledWith('url');
    expect(spyRouter.navigateByUrl).not.toHaveBeenCalled();
  }));

});
