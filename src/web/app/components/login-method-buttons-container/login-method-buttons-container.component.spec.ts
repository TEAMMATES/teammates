import { By } from '@angular/platform-browser';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LoginMethodButtonsContainerComponent } from './login-method-buttons-container.component';
import { LoginMethod } from '../../../types/api-output';
import { environment } from '../../../environments/environment';
import { GoogleLoginButtonComponent } from '../login-method-buttons/google-login-button/google-login-button.component';

describe('LoginMethodButtonsContainerComponent', () => {
  let component: LoginMethodButtonsContainerComponent;
  let fixture: ComponentFixture<LoginMethodButtonsContainerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({}).compileComponents();

    fixture = TestBed.createComponent(LoginMethodButtonsContainerComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should build login URL for selected login method', () => {
    fixture.componentRef.setInput('nextUrl', '/web/student/home');
    fixture.detectChanges();
    const getCompleteLoginUrl = component as unknown as { getCompleteLoginUrl: (method: LoginMethod) => string };

    expect(getCompleteLoginUrl.getCompleteLoginUrl(LoginMethod.GOOGLE)).toBe(
      `${environment.backendUrl}/login?nextUrl=%2Fweb%2Fstudent%2Fhome&loginMethod=google`,
    );
  });

  it('should log in with selected login method when rendered button is clicked', () => {
    fixture.componentRef.setInput('supportedLoginMethods', new Set([LoginMethod.GOOGLE]));
    const loginSpy = vi.spyOn(component, 'login').mockReturnValue();
    fixture.detectChanges();

    const googleLoginButton = fixture.debugElement.query(By.directive(GoogleLoginButtonComponent))
      .componentInstance as GoogleLoginButtonComponent;
    googleLoginButton.login.emit();

    expect(loginSpy).toHaveBeenCalledWith(LoginMethod.GOOGLE);
  });

  it('should support login method sets', () => {
    fixture.componentRef.setInput('supportedLoginMethods', new Set([LoginMethod.DEV_SERVER]));
    fixture.detectChanges();

    expect(component.isSupported(LoginMethod.DEV_SERVER)).toBeTruthy();
    expect(component.isSupported(LoginMethod.GOOGLE)).toBeFalsy();
  });
});
