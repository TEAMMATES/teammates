import { Location } from '@angular/common';
import { Component } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { Router, provideRouter } from '@angular/router';
import { NavigationService } from './navigation.service';
import { StatusMessageService } from './status-message.service';

@Component({ template: '' })
class DummyComponent {}

describe('NavigationService', () => {
  let service: NavigationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideRouter([{ path: 'dummy', component: DummyComponent }])],
    });
    service = TestBed.inject(NavigationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should return an encoded param string upon calling encodeParams', () => {
    expect(service.encodeParams({ courseId: '#123?123' })).toEqual('?courseId=%23123%3F123');
  });

  it('navigateBack: should navigate to the previous page in history', () => {
    const location = TestBed.inject(Location);
    const backSpy = vi.spyOn(location, 'back').mockImplementation(() => {});

    service.navigateBack();

    expect(backSpy).toHaveBeenCalled();
  });

  it('navigateBackWithSuccessMessage: should show the success message only after navigation completes', async () => {
    const location = TestBed.inject(Location);
    const router = TestBed.inject(Router);
    const statusMessageService = TestBed.inject(StatusMessageService);
    vi.spyOn(location, 'back').mockImplementation(() => {});
    const toastSpy = vi.spyOn(statusMessageService, 'showSuccessToast').mockImplementation(() => {});

    service.navigateBackWithSuccessMessage('Success!');
    expect(toastSpy).not.toHaveBeenCalled();

    await router.navigateByUrl('/dummy');
    expect(toastSpy).toHaveBeenCalledWith('Success!');
  });
});
