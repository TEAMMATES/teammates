import { Component, OnInit, ViewChild } from '@angular/core';
import { GoogleAuthProvider } from '@angular/fire/auth';
import { AngularFireAuth } from '@angular/fire/compat/auth';
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
  private frontendUrl: string = environment.frontendUrl;

  isLoginPage: boolean = true;
  isSignInWithEmail: boolean = false;
  isSignInLinkEmailSent: boolean = false;
  isTroubleGettingEmail: boolean = false;

  isSigningInWithGoogle: boolean = false;
  isSigningInWithEmail: boolean = false;
  isPageLoading: boolean = false;

  constructor(private afAuth: AngularFireAuth,
              private authService: AuthService,
              private formBuilder: FormBuilder,
              private statusMessageService: StatusMessageService) {}

  ngOnInit(): void {
    this.isPageLoading = true;

    this.formLogin = this.formBuilder.group({
      email: ['', Validators.required],
      recaptcha: [''],
    });

    this.afAuth.isSignInWithEmailLink(window.location.href).then((isEmailLink) => {
      if (isEmailLink) {
        const email = window.localStorage.getItem('emailForSignIn');
        if (email) {
          this.afAuth.signInWithEmailLink(email, window.location.href)
              .then((authResult) => {
                window.localStorage.removeItem('emailForSignIn');
                window.location.href = `${this.backendUrl}/oauth2callback${window.location.search}`
                    + `&email=${authResult.user!.email}`;
              })
              .catch((error) => {
                this.isPageLoading = false;
                let errorMsg;
                switch (error.code) {
                  case 'auth/invalid-action-code':
                    errorMsg = 'Login link is malformed, expired, or has already been used.';
                    break;
                  default:
                    errorMsg = error.message;
                }
                this.statusMessageService.showErrorToast(errorMsg);
              });
        } else {
          this.isPageLoading = false;
          this.statusMessageService.showErrorToast('Kindly login using the same device.');
        }
      } else {
        this.afAuth.getRedirectResult()
            .then((authResult) => {
              if (authResult.user) {
                this.isPageLoading = false;
                window.location.href = `${this.backendUrl}/oauth2callback${window.location.search}`
                    + `&email=${authResult.user!.email}`;
              } else {
                this.isPageLoading = false;
              }
            })
            .catch((error) => {
              this.isPageLoading = false;
              this.statusMessageService.showErrorToast(error.code + error.message);
            });
      }
    });
  }

  signInWithGoogle(): void {
    this.isSigningInWithGoogle = true;
    const googleProvider = new GoogleAuthProvider();
    googleProvider.addScope('https://www.googleapis.com/auth/userinfo.email');
    this.afAuth.signInWithRedirect(googleProvider)
        .then(() => {
          this.isSigningInWithGoogle = false;
        })
        .catch((error) => {
          this.isSigningInWithGoogle = false;
          this.statusMessageService.showErrorToast(error.code + error.message);
        });
  }

  /**
   * Sends the login link to the specified email address.
   */
  signInWithEmail(loginForm: FormGroup): void {
    if (!this.captchaSiteKey) {
      this.captchaResponse = '';
    }

    if (!this.formLogin.valid || this.captchaResponse === undefined) {
      this.statusMessageService.showErrorToast(
          'Please enter a valid email address and click the reCAPTCHA before submitting.');
      return;
    }

    this.isSigningInWithEmail = true;

    this.authService.sendLoginEmail({
      userEmail: loginForm.controls.email.value,
      continueUrl: `${this.frontendUrl}/web/login${window.location.search}`,
      captchaResponse: this.captchaResponse,
    }).pipe(finalize(() => {
      this.isSigningInWithEmail = false;
    })).subscribe((resp: SendLoginEmailResponse) => {
      if (resp.isEmailSent) {
        window.localStorage.setItem('emailForSignIn', loginForm.controls.email.value);
        this.isSignInLinkEmailSent = true;
        this.isSignInWithEmail = false;
      } else {
        this.statusMessageService.showErrorToast(resp.message);
        this.resetFormGroups();
      }
    }, (response: ErrorMessageOutput) => {
      this.statusMessageService.showErrorToast(response.error.message);
      this.resetFormGroups();
    });
  }

  /**
   * Resets the email and reCAPTCHA input fields in the form.
   */
  resetFormGroups(): void {
    (this.formLogin = this.formBuilder.group({
      email: ['', Validators.required],
      recaptcha: [''],
    }));

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
   * Handles successful completion recaptcha challenge.
   *
   * @param captchaResponse User's captcha response token
   */
  handleSuccess(captchaResponse: string): void {
    this.captchaSuccess = true;
    this.captchaResponse = captchaResponse;
  }

  resendEmail(): void {
    this.isSignInWithEmail = true;
    this.isTroubleGettingEmail = false;
  }

}
