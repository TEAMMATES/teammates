import { Component, OnInit } from '@angular/core';
import { GoogleAuthProvider } from '@angular/fire/auth';
import { AngularFireAuth } from '@angular/fire/compat/auth';
import { ActivatedRoute } from '@angular/router';
import { environment } from '../environments/environment';

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

  constructor(private route: ActivatedRoute, private afAuth: AngularFireAuth) {}

  ngOnInit(): void {
    this.isLoading = true;
    this.route.queryParams.subscribe((queryParams: any) => {
      this.nextUrl = queryParams.nextUrl;
      this.afAuth.isSignInWithEmailLink(window.location.href).then((isEmailLink) => {
        if (isEmailLink) {
          let email = window.localStorage.getItem('emailForSignIn');
          if (!email) {
            email = window.prompt('Please provide your email for confirmation');
          }
          this.afAuth.signInWithEmailLink(email!, window.location.href)
              .then((authResult) => {
                window.localStorage.removeItem('emailForSignIn');
                window.location.href = `${this.backendUrl}/oauth2callback?email=${authResult.user!.email}`
                    + `&nextUrl=${this.nextUrl}`;
              })
              .catch((error) => {
                console.error('signInWithEmailLinkError', error);
                this.isLoading = false;
              });
        } else {
          this.afAuth.getRedirectResult()
              .then((authResult) => {
                if (authResult.user) {
                  window.location.href = `${this.backendUrl}/oauth2callback?email=${authResult.user!.email}`
                      + `&nextUrl=${this.nextUrl}`;
                }
              })
              .catch((error) => {
                console.error('signInWithGoogleError', error);
                this.isLoading = false;
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
    const googleProvider = new GoogleAuthProvider();
    googleProvider.addScope('https://www.googleapis.com/auth/userinfo.email');
    this.afAuth.signInWithRedirect(googleProvider)
        .catch((error) => {
          console.error('Authentication failed:', error);
        });
  }

  signInWithEmail(): void {
    this.isLoading = true;
    const actionCodeSettings = {
      url: `${this.frontendUrl}/web/login${window.location.search}`,
      handleCodeInApp: true,
    };
    this.afAuth.sendSignInLinkToEmail(this.email, actionCodeSettings)
        .then(() => {
          window.localStorage.setItem('emailForSignIn', this.email);
          this.isSignInLinkEmailSent = true;
          this.isSignInWithEmail = false;
          this.isLoading = false;
        })
        .catch((error) => {
          console.error('sendSignInLinkToEmailError', error);
        });
  }

  resendEmail(): void {
    this.signInWithEmail();
    this.isTroubleGettingEmail = false;
  }

}
