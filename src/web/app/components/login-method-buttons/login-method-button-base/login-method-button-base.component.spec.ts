import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LoginMethodButtonBaseComponent } from './login-method-button-base.component';
import { LoginMethod } from '../../../../types/api-output';
import { environment } from '../../../../environments/environment';
import { LOGIN_METHOD_BUTTON_CONTEXT, LoginMethodButtonContext } from '../login-method-button-context';

describe('LoginMethodButtonBaseComponent', () => {
  let component: LoginMethodButtonBaseComponent;
  let fixture: ComponentFixture<LoginMethodButtonBaseComponent>;
  let mockContext: LoginMethodButtonContext;

  const createComponent = (nextUrl = 'http://example.com/next') => {
    mockContext = { nextUrl };
    TestBed.configureTestingModule({
      providers: [
        {
          provide: LOGIN_METHOD_BUTTON_CONTEXT,
          useValue: mockContext,
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(LoginMethodButtonBaseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  };

  it('should create', () => {
    createComponent();
    expect(component).toBeTruthy();
  });

  it('should redirect to the correct login URL', () => {
    component.loginMethod = LoginMethod.GOOGLE;

    const expectedUrl = environment.backendUrl + '/login?nextUrl=http%3A%2F%2Fexample.com%2Fnext&method=google';

    const completeLoginUrlSpy = vi.spyOn(
      component as unknown as { getCompleteLoginUrl: () => string },
      'getCompleteLoginUrl',
    );

    component.login();

    expect(completeLoginUrlSpy).toHaveReturnedWith(expectedUrl);
  });

  it('should handle empty nextUrl correctly', () => {
    createComponent('');

    component.loginMethod = LoginMethod.DEV_SERVER;

    const expectedUrl = environment.backendUrl + '/login?nextUrl=&method=devserver';

    const completeLoginUrlSpy = vi.spyOn(
      component as unknown as { getCompleteLoginUrl: () => string },
      'getCompleteLoginUrl',
    );

    component.login();

    expect(completeLoginUrlSpy).toHaveReturnedWith(expectedUrl);
  });
});
