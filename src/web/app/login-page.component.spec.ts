import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { AngularFireModule } from '@angular/fire/compat';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { NgxCaptchaModule } from 'ngx-captcha';
import { AuthService } from '../services/auth.service';
import { StatusMessageService } from '../services/status-message.service';
import SpyInstance = jest.SpyInstance;
import { throwError } from 'rxjs';
import { AjaxLoadingModule } from './components/ajax-loading/ajax-loading.module';
import { LoadingSpinnerModule } from './components/loading-spinner/loading-spinner.module';
import { LoginPageComponent } from './login-page.component';

describe('LoginPageComponent', () => {
  let component: LoginPageComponent;
  let fixture: ComponentFixture<LoginPageComponent>;
  let authService: AuthService;
  let statusMessageService: StatusMessageService;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [LoginPageComponent],
      imports: [
        ReactiveFormsModule,
        RouterTestingModule,
        HttpClientTestingModule,
        LoadingSpinnerModule,
        NgxCaptchaModule,
        AjaxLoadingModule,
        AngularFireModule.initializeApp({}),
      ],
      providers: [
        AuthService,
        StatusMessageService,
      ],
    })
        .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LoginPageComponent);
    component = fixture.componentInstance;
    authService = TestBed.inject(AuthService);
    statusMessageService = TestBed.inject(StatusMessageService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show error toast if login with Google is unsuccessful', () => {
    jest.spyOn(authService, 'isLogInWithEmailLink').mockResolvedValue(true);
    window.localStorage.setItem('emailForSignIn', 'abc@gmail.com');
    jest.spyOn(authService, 'logInWithEmailLink').mockRejectedValue(new Error());
    const spy: SpyInstance = jest.spyOn(statusMessageService, 'showErrorToast')
        .mockImplementation((args: string) => {
          expect(args).toEqual('Login with email is unsuccessful. Please try again.');
        });
    fixture.detectChanges();
    component.ngOnInit();
    expect(spy).toBeCalled();
  });

  it('should show error toast if login with email is unsuccessful', () => {
    jest.spyOn(authService, 'isLogInWithEmailLink').mockResolvedValue(false);
    jest.spyOn(authService, 'getRedirectResult').mockRejectedValue(new Error());
    const spy: SpyInstance = jest.spyOn(statusMessageService, 'showErrorToast')
        .mockImplementation((args: string) => {
          expect(args).toEqual('Login with Google is unsuccessful. Please try again.');
        });
    fixture.detectChanges();
    component.ngOnInit();
    expect(spy).toBeCalled();
  });

  it('should show error toast if cannot redirect to Google login page', () => {
    jest.spyOn(authService, 'logInWithRedirect').mockRejectedValue(new Error());
    const spy: SpyInstance = jest.spyOn(statusMessageService, 'showErrorToast')
        .mockImplementation((args: string) => {
          expect(args).toEqual('Cannot redirect to Google login page. Please try again.');
        });
    fixture.detectChanges();
    component.logInWithGoogle();
    expect(spy).toBeCalled();
  });

  it('should show error toast if login form is invalid', () => {
    component.captchaResponse = '';
    component.formLogin.setValue({
      email: '',
      recaptcha: '',
    })
    const spy: SpyInstance = jest.spyOn(statusMessageService, 'showErrorToast')
        .mockImplementation((args: string) => {
          expect(args).toEqual('Please enter a valid email address and click the reCAPTCHA before submitting.');
        });
    fixture.detectChanges();
    component.logInWithEmail(component.formLogin);
    expect(spy).toBeCalled();
  });

  it('should show error toast if cannot send login email', () => {
    jest.spyOn(authService, 'sendLoginEmail').mockReturnValue(throwError({
      error: {
        message: 'This is the error message.',
      },
    }));
    component.captchaResponse = 'captchaResponse';
    component.formLogin.setValue({
      email: 'abc@gmail.com',
      recaptcha: 'recaptcha',
    })
    const spy: SpyInstance = jest.spyOn(statusMessageService, 'showErrorToast')
        .mockImplementation((args: string) => {
          expect(args).toEqual('This is the error message.');
        });
    fixture.detectChanges();
    component.logInWithEmail(component.formLogin);
    expect(spy).toBeCalled();
  });
});
