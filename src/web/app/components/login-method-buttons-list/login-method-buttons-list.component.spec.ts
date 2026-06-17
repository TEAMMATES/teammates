import { By } from '@angular/platform-browser';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LoginMethodButtonsListComponent } from './login-method-buttons-list.component';
import { LoginMethod } from '../../../types/api-output';
import { environment } from '../../../environments/environment';
import { GoogleLoginButtonComponent } from '../login-method-buttons/google-login-button/google-login-button.component';

describe('LoginMethodButtonsListComponent', () => {
  let component: LoginMethodButtonsListComponent;
  let fixture: ComponentFixture<LoginMethodButtonsListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({}).compileComponents();

    fixture = TestBed.createComponent(LoginMethodButtonsListComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should build login URL for selected login method', () => {
    component.nextUrl = '/web/student/home';
    const getCompleteLoginUrl = component as unknown as { getCompleteLoginUrl: (method: LoginMethod) => string };

    expect(getCompleteLoginUrl.getCompleteLoginUrl(LoginMethod.GOOGLE)).toBe(
      `${environment.backendUrl}/login?nextUrl=%2Fweb%2Fstudent%2Fhome&method=google`,
    );
  });

  it('should log in with selected login method when rendered button is clicked', () => {
    component.supportedLoginMethods = new Set([LoginMethod.GOOGLE]);
    const loginSpy = vi.spyOn(component, 'login').mockReturnValue();
    fixture.detectChanges();

    const googleLoginButton = fixture.debugElement.query(By.directive(GoogleLoginButtonComponent))
      .componentInstance as GoogleLoginButtonComponent;
    googleLoginButton.login.emit();

    expect(loginSpy).toHaveBeenCalledWith(LoginMethod.GOOGLE);
  });

  it('should support login method sets', () => {
    component.supportedLoginMethods = new Set([LoginMethod.DEV_SERVER]);

    expect(component.isSupported(LoginMethod.DEV_SERVER)).toBeTruthy();
    expect(component.isSupported(LoginMethod.GOOGLE)).toBeFalsy();
  });
});
