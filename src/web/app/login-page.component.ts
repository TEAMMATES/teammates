import { Component, OnInit, ViewChild } from '@angular/core';
import { GoogleAuthProvider } from '@angular/fire/auth';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ReCaptcha2Component } from 'ngx-captcha';
import { finalize } from 'rxjs/operators';
import { environment } from '../environments/environment';
import { AuthService } from '../services/auth.service';
import { StatusMessageService } from '../services/status-message.service';
import { SendLoginEmailResponse } from '../types/api-output';
import { ErrorMessageOutput } from './error-message-output';

/**
 * Login page component.
 */
@Component({
  selector: 'tm-login-page',
  templateUrl: './login-page.component.html',
  styleUrls: ['./login-page.component.scss'],
})
export class LoginPageComponent implements OnInit {

  // ngx-recaptcha2 element properties
  captchaSuccess: boolean = false;
  captchaResponse?: string;
  size: 'compact' | 'normal' = 'normal';
  lang: string = 'en';

  formLogin!: FormGroup;
  readonly captchaSiteKey: string = environment.captchaSiteKey;
  @ViewChild('captchaElem') captchaElem!: ReCaptcha2Component;

  private backendUrl: string = environment.backendUrl;

  isLoginPage: boolean = true;
  isLogInWithEmail: boolean = false;
  isLoginEmailSent: boolean = false;
  isTroubleGettingEmail: boolean = false;

  isLoggingInWithGoogle: boolean = false;
  isLoggingInWithEmail: boolean = false;
  isPageLoading: boolean = false;

  constructor(private authService: AuthService,
              private formBuilder: FormBuilder,
              private statusMessageService: StatusMessageService) {}

  ngOnInit(): void {
    if (!environment.allowFirebaseLogin) {
      // Redirect to home page if Firebase login is not supported
      window.location.href = '/';
    }
    this.isPageLoading = true;

    this.formLogin = this.formBuilder.group({
      email: ['', Validators.required],
      recaptcha: [''],
    });

    this.isLoggingInWithEmail = true;
    this.isLoggingInWithGoogle = true;

    this.handleGoogleRedirection();
    this.handleEmailRedirection();
  }

  handleGoogleRedirection(): Promise<void> {
    return this.authService.getRedirectResult()
        .then((authResult) => {
          if (authResult.user) {
            // is redirection from Google login
            authResult.user!.getIdToken()
                .then((idToken) => {
                  window.location.href = `${this.backendUrl}/oauth2callback${window.location.search}`
                      + `&idToken=${idToken}`;
                })
                .catch(() => {
                  this.isPageLoading = false;
                  this.statusMessageService.showErrorToast('Login with Google is unsuccessful. Please try again.');
                });
          } else {
            // not redirection from Google login
            this.isLoggingInWithGoogle = false;
            // page should stop loading only if it is not still logging in with email
            this.isPageLoading = this.isLoggingInWithEmail;
          }
        })
        .catch(() => {
          this.isPageLoading = false;
          this.statusMessageService.showErrorToast('Login with Google is unsuccessful. Please try again.');
        });
  }

  handleEmailRedirection(): Promise<void> {
    return this.authService.isLogInWithEmailLink(window.location.href).then((isEmailLink) => {
      if (isEmailLink) {
        // is redirection from email login
        const email = window.localStorage.getItem('emailForSignIn');
        if (email) {
          this.authService.logInWithEmailLink(email, window.location.href)
              .then((authResult) => {
                window.localStorage.removeItem('emailForSignIn');
                authResult.user!.getIdToken()
                    .then((idToken) => {
                      window.location.href = `${this.backendUrl}/oauth2callback${window.location.search}`
                          + `&idToken=${idToken}`;
                    })
                    .catch(() => {
                      this.isPageLoading = false;
                      this.statusMessageService.showErrorToast('Login with email is unsuccessful. Please try again.');
                    });
              })
              .catch(() => {
                this.isPageLoading = false;
                this.statusMessageService.showErrorToast('Login with email is unsuccessful. Please try again.');
              });
        } else {
          this.isPageLoading = false;
          this.statusMessageService.showErrorToast(
              'Login link has already been used. If not, kindly login using the same device.');
        }
      } else {
        // not redirection from email login
        this.isLoggingInWithEmail = false;
        // page should stop loading only if it is not still logging in with Google
        this.isPageLoading = this.isLoggingInWithGoogle;
      }
    });
  }

  /**
   * Redirects to the Google login page.
   */
  logInWithGoogle(): Promise<void> {
    this.isLoggingInWithGoogle = true;
    const googleProvider = new GoogleAuthProvider();
    googleProvider.addScope('https://www.googleapis.com/auth/userinfo.email');
    return this.authService.logInWithRedirect(googleProvider)
        .then(() => {
          this.isLoggingInWithGoogle = false;
        })
        .catch(() => {
          this.isLoggingInWithGoogle = false;
          this.statusMessageService.showErrorToast('Cannot redirect to Google login page. Please try again.');
        });
  }

  /**
   * Sends the login email to the specified email address.
   */
  logInWithEmail(loginForm: FormGroup): void {
    if (!this.captchaSiteKey) {
      this.captchaResponse = '';
    }

    if (!this.formLogin.valid || this.captchaResponse === undefined) {
      this.statusMessageService.showErrorToast(
          'Please enter a valid email address and click the reCAPTCHA before submitting.');
      return;
    }

    this.isLoggingInWithEmail = true;

    this.authService.sendLoginEmail({
      userEmail: loginForm.controls['email'].value,
      continueUrl: `${window.location.origin}/web/login${window.location.search}`,
      captchaResponse: this.captchaResponse,
    }).pipe(finalize(() => {
      this.isLoggingInWithEmail = false;
    })).subscribe({
      next: (resp: SendLoginEmailResponse) => {
        if (resp.isEmailSent) {
          window.localStorage.setItem('emailForSignIn', loginForm.controls['email'].value);
          this.isLoginEmailSent = true;
          this.isLogInWithEmail = false;
        } else {
          this.statusMessageService.showErrorToast(resp.message);
          this.resetFormGroups();
        }
      },
      error: (response: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(response.error.message);
        this.resetFormGroups();
      },
    });
  }

  /**
   * Resets the email and reCAPTCHA input fields in the Log In with Email form.
   */
  resetFormGroups(): void {
    this.formLogin = this.formBuilder.group({
      email: ['', Validators.required],
      recaptcha: [''],
    });

    this.reloadCaptcha();
  }

  /**
   * Reloads the reCAPTCHA widget if a non-empty site key is present.
   */
  reloadCaptcha(): void {
    if (this.captchaSiteKey) {
      this.captchaElem.reloadCaptcha();
    }
  }

  /**
   * Handles successful completion reCAPTCHA challenge.
   *
   * @param captchaResponse user's reCAPTCHA response token.
   */
  handleCaptchaSuccess(captchaResponse: string): void {
    this.captchaSuccess = true;
    this.captchaResponse = captchaResponse;
  }

  /**
   * Resends the login email.
   */
  resendEmail(): void {
    this.isLogInWithEmail = true;
    this.isTroubleGettingEmail = false;
  }

}
