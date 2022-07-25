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
  private afAuth: AngularFireAuth | undefined;

  constructor(private httpRequestService: HttpRequestService, private injector: Injector) {
    if (environment.enableFirebaseAuth) {
      this.afAuth = <AngularFireAuth> this.injector.get(AngularFireAuth);
    }
  }

  /**
   * Gets the user authentication information.
   */
  getAuthUser(user?: string, nextUrl?: string): Observable<AuthInfo> {
    const params: Record<string, string> = { frontendUrl: this.frontendUrl };
    if (user) {
      params.user = user;
    }
    if (nextUrl) {
      params.nextUrl = nextUrl;
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
   * Sends login email.
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

  isSignInWithEmailLink(url: string): Promise<boolean> {
    return this.afAuth?.isSignInWithEmailLink(url) || Promise.resolve(false);
  }

  signInWithEmailLink(email: string, emailLink: string): Promise<firebase.auth.UserCredential> {
    return this.afAuth?.signInWithEmailLink(email, emailLink) || new Promise(() => {});
  }

  getRedirectResult(): Promise<firebase.auth.UserCredential> {
    return this.afAuth?.getRedirectResult() || new Promise(() => {});
  }

  signInWithRedirect(provider: AuthProvider): Promise<void> {
    return this.afAuth?.signInWithRedirect(provider) || Promise.resolve();
  }

  signOut(): Promise<void> {
    return this.afAuth?.signOut() || Promise.resolve();
  }

}
