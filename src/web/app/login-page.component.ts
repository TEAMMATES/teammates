import { Component, OnInit } from '@angular/core';
import { AngularFireAuth } from '@angular/fire/compat/auth';
import { ActivatedRoute } from '@angular/router';
import { FirebaseUISignInFailure, FirebaseUISignInSuccessWithAuthResult } from 'firebaseui-angular';
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

  private nextUrl = '';
  private backendUrl: string = environment.backendUrl;

  constructor(private route: ActivatedRoute, private afAuth: AngularFireAuth) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.nextUrl = queryParams.nextUrl;
    });
    this.afAuth.authState.subscribe(d => console.log(d));
  }

  successCallback(data: FirebaseUISignInSuccessWithAuthResult): void {
    console.log('successCallback', data);
    window.location.href = `${this.backendUrl}/oauth2callback?email=${data.authResult.user!.email}`
        + `&nextUrl=${this.nextUrl}`;
  }

  errorCallback(data: FirebaseUISignInFailure): void {
    console.warn('errorCallback', data);
  }

  uiShownCallback(): void {
    console.log('UI shown');
  }

}
