import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { environment } from '../environments/environment';
import firebase from 'firebase/compat/app';
import * as firebaseui from 'firebaseui';

/**
 * Login page component.
 */
@Component({
  selector: 'tm-login-page',
  templateUrl: './login-page.component.html',
  styleUrls: ['./login-page.component.scss'],
})
export class LoginPageComponent implements OnInit {

  private ui: firebaseui.auth.AuthUI = new firebaseui.auth.AuthUI(firebase.auth());

  constructor(private route: ActivatedRoute) {}

  ngOnInit(): void {

    let nextUrl: string = `${environment.frontendUrl}/web/front/home`;
    this.route.queryParams.subscribe((queryParams: any) => {
      nextUrl = queryParams.nextUrl;
    });

    const uiConfig: firebaseui.auth.Config = {
      signInOptions: [
        {
          provider: firebase.auth.GoogleAuthProvider.PROVIDER_ID,
          scopes: ['https://www.googleapis.com/auth/userinfo.email'],
        },
        {
          provider: firebase.auth.EmailAuthProvider.PROVIDER_ID,
          signInMethod: firebase.auth.EmailAuthProvider.EMAIL_LINK_SIGN_IN_METHOD,
        },
      ],
      callbacks: {
        signInSuccessWithAuthResult: function(authResult, redirectUrl) {
          console.log('successCallback', authResult, redirectUrl);
          window.location.href = `${environment.backendUrl}/oauth2callback?email=${authResult.user.email}`
              + `&nextUrl=${nextUrl}`;
          return false;
        },
        signInFailure: function(error) {
          console.warn('errorCallback', error);
        },
        uiShown: function() {
          console.log('UI shown');
        }
      },
    };

    this.ui.start('#firebaseui-auth-container', uiConfig);
  }

}
