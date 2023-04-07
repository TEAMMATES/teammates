import { Injectable, Injector } from '@angular/core';
import { AngularFireAuth } from '@angular/fire/compat/auth';
import { AuthProvider } from 'firebase/auth';
import firebase from 'firebase/compat/app';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';
import { ResourceEndpoints } from '../types/api-const';
import { AuthInfo, SendLoginEmailResponse, RegkeyValidity } from '../types/api-output';
import { Intent } from '../types/api-request';
import { HttpRequestService } from './http-request.service';

/**
 * Handles user authentication.
 */
@Injectable({
  providedIn: 'root',
})
export class AuthService {

  private frontendUrl: string = environment.frontendUrl;
  private afAuth?: AngularFireAuth;

  constructor(private httpRequestService: HttpRequestService, private injector: Injector) {
    if (environment.firebaseConfig?.projectId) {
      this.afAuth = <AngularFireAuth> this.injector.get(AngularFireAuth);
    }
  }

  /**
   * Gets the user authentication information.
   */
  getAuthUser(user?: string, nextUrl?: string): Observable<AuthInfo> {
    const params: Record<string, string> = { frontendUrl: this.frontendUrl };
    if (user) {
      params['user'] = user;
    }
    if (nextUrl) {
      params['nextUrl'] = nextUrl;
    }
    return this.httpRequestService.get(ResourceEndpoints.AUTH, params);
  }

  /**
   * Gets the validity of the given registration key for user.
   */
  getAuthRegkeyValidity(key: string, intent: Intent): Observable<RegkeyValidity> {
    const params: Record<string, string> = { key, intent };
    return this.httpRequestService.get(ResourceEndpoints.AUTH_REGKEY, params);
  }

  /**
   * Sends login email to the specified user.
   */
  sendLoginEmail(queryParam: {
    userEmail: string,
    continueUrl: string,
    captchaResponse: string,
  }): Observable<SendLoginEmailResponse> {
    const paramMap: Record<string, string> = {
      useremail: queryParam.userEmail,
      continueurl: queryParam.continueUrl,
      captcharesponse: queryParam.captchaResponse,
    };

    return this.httpRequestService.post(ResourceEndpoints.LOGIN_EMAIL, paramMap);
  }

  /**
   * Wrapper method for AngularFireAuth.isSignInWithEmailLink().
   *
   * @see https://firebase.google.com/docs/reference/js/v8/firebase.auth.Auth#issigninwithemaillink.
   */
  isLogInWithEmailLink(url: string): Promise<boolean> {
    return this.afAuth?.isSignInWithEmailLink(url) || Promise.resolve(false);
  }

  /**
   * Wrapper method for AngularFireAuth.signInWithEmailLink().
   *
   * @see https://firebase.google.com/docs/reference/js/v8/firebase.auth.Auth#signinwithemaillink
   */
  logInWithEmailLink(email: string, emailLink: string): Promise<firebase.auth.UserCredential> {
    return this.afAuth?.signInWithEmailLink(email, emailLink) || new Promise(() => {});
  }

  /**
   * Wrapper method for AngularFireAuth.getRedirectResult().
   *
   * @see https://firebase.google.com/docs/reference/js/v8/firebase.auth.Auth#getredirectresult
   */
  getRedirectResult(): Promise<firebase.auth.UserCredential> {
    return this.afAuth?.getRedirectResult() || new Promise(() => {});
  }

  /**
   * Wrapper method for AngularFireAuth.signInWithRedirect().
   *
   * @see https://firebase.google.com/docs/reference/js/v8/firebase.auth.Auth#signinwithredirect
   */
  logInWithRedirect(provider: AuthProvider): Promise<void> {
    return this.afAuth?.signInWithRedirect(provider) || Promise.resolve();
  }

  /**
   * Wrapper method for AngularFireAuth.signOut()
   *
   * @see https://firebase.google.com/docs/reference/js/v8/firebase.auth.Auth#signout
   */
  logout(): Promise<void> {
    return this.afAuth?.signOut() || Promise.resolve();
  }

}
