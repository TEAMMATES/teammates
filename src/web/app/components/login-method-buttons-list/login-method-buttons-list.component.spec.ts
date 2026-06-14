import { By } from '@angular/platform-browser';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LoginMethodButtonsListComponent } from './login-method-buttons-list.component';
import { LoginMethod } from '../../../types/api-output';
import { LoginMethodButtonBaseComponent } from '../login-method-buttons/login-method-button-base/login-method-button-base.component';
import { environment } from '../../../environments/environment';

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

  it('should provide nextUrl to rendered login buttons', () => {
    component.nextUrl = '/web/student/home';
    component.supportedLoginMethods = new Set([LoginMethod.GOOGLE]);
    fixture.detectChanges();

    const buttonBase = fixture.debugElement.query(By.directive(LoginMethodButtonBaseComponent))
      .componentInstance as LoginMethodButtonBaseComponent;
    const getCompleteLoginUrl = buttonBase as unknown as { getCompleteLoginUrl: () => string };

    expect(getCompleteLoginUrl.getCompleteLoginUrl()).toBe(
      `${environment.backendUrl}/login?nextUrl=%2Fweb%2Fstudent%2Fhome&method=google`,
    );
  });

  it('should support login method sets', () => {
    component.supportedLoginMethods = new Set([LoginMethod.DEV_SERVER]);

    expect(component.isSupported(LoginMethod.DEV_SERVER)).toBeTruthy();
    expect(component.isSupported(LoginMethod.GOOGLE)).toBeFalsy();
  });
});
