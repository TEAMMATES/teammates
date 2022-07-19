import { Component, OnInit } from '@angular/core';
import { GoogleAuthProvider } from '@angular/fire/auth';
import { AngularFireAuth } from '@angular/fire/compat/auth';
import { ActivatedRoute } from '@angular/router';
import { environment } from '../environments/environment';
import { AuthService } from '../services/auth.service';
import { StatusMessageService } from '../services/status-message.service';
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

  private nextUrl: string = '';
  private backendUrl: string = environment.backendUrl;
  private frontendUrl: string = environment.frontendUrl;

  isLoginPage: boolean = true;
  isSignInWithEmail: boolean = false;
  isSignInLinkEmailSent: boolean = false;
  isTroubleGettingEmail: boolean = false;
  email: string = 'test@example.com';
  isLoading: boolean = false;

  constructor(private route: ActivatedRoute,
              private afAuth: AngularFireAuth,
              private authService: AuthService,
              private statusMessageService: StatusMessageService,
  ) {}

  ngOnInit(): void {
    this.isLoading = true;
    this.route.queryParams.subscribe((queryParams: any) => {
      this.nextUrl = queryParams.nextUrl;
      this.afAuth.isSignInWithEmailLink(window.location.href).then((isEmailLink) => {
        if (isEmailLink) {
          const email = window.localStorage.getItem('emailForSignIn');
          if (email) {
            this.afAuth.signInWithEmailLink(email, window.location.href)
                .then((authResult) => {
                  window.localStorage.removeItem('emailForSignIn');
                  window.location.href = `${this.backendUrl}/oauth2callback?email=${authResult.user!.email}`
                      + `&nextUrl=${this.nextUrl}`;
                })
                .catch((error) => {
                  this.isLoading = false;
                  this.statusMessageService.showErrorToast(error.code + error.message);
                });
          } else {
            this.isLoading = false;
            this.statusMessageService.showErrorToast('Login link expired.');
          }
        } else {
          this.afAuth.getRedirectResult()
              .then((authResult) => {
                if (authResult.user) {
                  window.location.href = `${this.backendUrl}/oauth2callback?email=${authResult.user!.email}`
                      + `&nextUrl=${this.nextUrl}`;
                }
              })
              .catch((error) => {
                this.isLoading = false;
                this.statusMessageService.showErrorToast(error.code + error.message);
              });
          this.isLoading = false;
        }
      });
    });
  }

  triggerEmailChange(newEmail: string): void {
    this.email = newEmail;
  }

  signInWithGoogle(): void {
    this.isLoading = true;
    const googleProvider = new GoogleAuthProvider();
    googleProvider.addScope('https://www.googleapis.com/auth/userinfo.email');
    this.afAuth.signInWithRedirect(googleProvider)
        .then(() => {
          this.isLoading = false;
        })
        .catch((error) => {
          this.isLoading = false;
          this.statusMessageService.showErrorToast(error.code + error.message);
        });
  }

  signInWithEmail(): void {
    this.isLoading = true;
    this.authService.sendLoginEmail(this.email, `${this.frontendUrl}/web/login${window.location.search}`)
        .subscribe(() => {
          window.localStorage.setItem('emailForSignIn', this.email);
          this.isSignInLinkEmailSent = true;
          this.isSignInWithEmail = false;
          this.isLoading = false;
        }, (error: ErrorMessageOutput) => {
          this.isLoading = false;
          this.statusMessageService.showErrorToast(error.error.message);
        });
  }

  resendEmail(): void {
    this.signInWithEmail();
    this.isTroubleGettingEmail = false;
  }

}
