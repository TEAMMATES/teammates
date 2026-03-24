import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, fakeAsync, flush, waitForAsync } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { StatusMessageService } from '../services/status-message.service';
import { PageComponent } from './page.component';

describe('PageComponent', () => {
  let component: PageComponent;
  let fixture: ComponentFixture<PageComponent>;
  let ngbModal: NgbModal;
  let statusMessageService: StatusMessageService;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      providers: [
        provideRouter([]),
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PageComponent);
    component = fixture.componentInstance;
    ngbModal = TestBed.inject(NgbModal);
    statusMessageService = TestBed.inject(StatusMessageService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should open provider modal for student role', () => {
    const mockModalRef = { close: jest.fn() };
    const openSpy = jest.spyOn(ngbModal, 'open').mockReturnValue(mockModalRef as any);
    (component as any).providerModal = {};

    component.openProviderModal('student');

    expect(openSpy).toHaveBeenCalledTimes(1);
    expect(openSpy).toHaveBeenCalledWith((component as any).providerModal, { centered: true });
    expect((component as any).currentRole).toBe('student');
  });

  it('should show error if login provider is chosen before selecting role', () => {
    const errorSpy = jest.spyOn(statusMessageService, 'showErrorToast');

    component.loginWithProvider('google');

    expect(errorSpy).toHaveBeenCalledWith('Role not selected');
  });

  it('should redirect student login with google provider', () => {
    const mockModalRef = { close: jest.fn() };
    (component as any).providerModalRef = mockModalRef;
    (component as any).currentRole = 'student';
    const backendUrl = (component as any).backendUrl;
    const redirectSpy = jest.spyOn(component, 'redirectTo').mockImplementation(() => undefined);

    component.loginWithProvider('google');

    expect(mockModalRef.close).toHaveBeenCalledTimes(1);
    expect(redirectSpy).toHaveBeenCalledWith(
      `${backendUrl}/login?provider=google&nextUrl=${encodeURIComponent('/web/student/home')}`,
    );
  });

  it('should redirect instructor login with entra provider', () => {
    const mockModalRef = { close: jest.fn() };
    (component as any).providerModalRef = mockModalRef;
    (component as any).currentRole = 'instructor';
    const backendUrl = (component as any).backendUrl;
    const redirectSpy = jest.spyOn(component, 'redirectTo').mockImplementation(() => undefined);

    component.loginWithProvider('entra');

    expect(mockModalRef.close).toHaveBeenCalledTimes(1);
    expect(redirectSpy).toHaveBeenCalledWith(
      `${backendUrl}/login?provider=entra&nextUrl=${encodeURIComponent('/web/instructor/home')}`,
    );
  });

  describe('provider modal button visibility', () => {
    afterEach(fakeAsync(() => {
      ngbModal.dismissAll();
      fixture.detectChanges();
      flush();
    }));

    it('should show only Google button when showGoogleLogin is true and showMsEntraLogin is false', () => {
      component.showGoogleLogin = true;
      component.showMsEntraLogin = false;
      fixture.detectChanges();

      component.openProviderModal('student');
      fixture.detectChanges();

      expect(document.querySelector('#provider-google-btn')).not.toBeNull();
      expect(document.querySelector('#provider-entra-btn')).toBeNull();
    });

    it('should show only Microsoft button when showMsEntraLogin is true and showGoogleLogin is false', () => {
      component.showGoogleLogin = false;
      component.showMsEntraLogin = true;
      fixture.detectChanges();

      component.openProviderModal('student');
      fixture.detectChanges();

      expect(document.querySelector('#provider-google-btn')).toBeNull();
      expect(document.querySelector('#provider-entra-btn')).not.toBeNull();
    });

    it('should show both buttons when showGoogleLogin and showMsEntraLogin are both true', () => {
      component.showGoogleLogin = true;
      component.showMsEntraLogin = true;
      fixture.detectChanges();

      component.openProviderModal('student');
      fixture.detectChanges();

      expect(document.querySelector('#provider-google-btn')).not.toBeNull();
      expect(document.querySelector('#provider-entra-btn')).not.toBeNull();
    });

    it('should show no provider buttons when showGoogleLogin and showMsEntraLogin are both false', () => {
      component.showGoogleLogin = false;
      component.showMsEntraLogin = false;
      fixture.detectChanges();

      component.openProviderModal('student');
      fixture.detectChanges();

      expect(document.querySelector('#provider-google-btn')).toBeNull();
      expect(document.querySelector('#provider-entra-btn')).toBeNull();
    });
  });
});
