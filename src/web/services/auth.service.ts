import { Injectable, Injector } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { AngularFireAuth } from '@angular/fire/compat/auth';
import { AuthProvider } from 'firebase/auth';
import firebase from 'firebase/compat/app';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';
import { ResourceEndpoints } from '../types/api-const';
import { AuthInfo, SendLoginEmailResponse, RegkeyValidity } from '../types/api-output';
import { Intent } from '../types/api-request';

/**
 * Handles user authentication.
 */
@Injectable({
    providedIn: 'root',
})
export class AuthService {

    private frontendUrl: string = environment.frontendUrl;
    private afAuth: AngularFireAuth | null = null;

    constructor(private http: HttpClient, private injector: Injector) {
        if (environment.firebaseConfig?.projectId) {
            this.afAuth = this.injector.get(AngularFireAuth);
        }
    }

    /**
     * Gets the user authentication information.
     */
    getAuthUser(user?: string, nextUrl?: string): Observable<AuthInfo> {
        let params = new HttpParams().set('frontendUrl', this.frontendUrl);
        if (user) params = params.set('user', user);
        if (nextUrl) params = params.set('nextUrl', nextUrl);

        return this.http.get<AuthInfo>(`${environment.backendUrl}${ResourceEndpoints.AUTH}`, {
            params,
            withCredentials: environment.withCredentials,
        });
    }

    /**
     * Gets the validity of the given registration key for user.
     */
    getAuthRegkeyValidity(key: string, intent: Intent): Observable<RegkeyValidity> {
        const params = new HttpParams()
            .set('key', key)
            .set('intent', intent);

        return this.http.get<RegkeyValidity>(`${environment.backendUrl}${ResourceEndpoints.AUTH_REGKEY}`, {
            params,
            withCredentials: environment.withCredentials,
        });
    }

    /**
     * Sends login email to the specified user.
     */
    sendLoginEmail(queryParam: {
        userEmail: string,
        continueUrl: string,
        captchaResponse: string,
    }): Observable<SendLoginEmailResponse> {
        const body = {
            useremail: queryParam.userEmail,
            continueurl: queryParam.continueUrl,
            captcharesponse: queryParam.captchaResponse,
        };

        return this.http.post<SendLoginEmailResponse>(
            `${environment.backendUrl}${ResourceEndpoints.LOGIN_EMAIL}`,
            body,
            { withCredentials: environment.withCredentials }
        );
    }

    /**
     * Wrapper method for AngularFireAuth.isSignInWithEmailLink().
     */
    isLogInWithEmailLink(url: string): Promise<boolean> {
        if (!this.afAuth) return Promise.resolve(false);
        return this.afAuth.isSignInWithEmailLink(url);
    }

    /**
     * Wrapper method for AngularFireAuth.signInWithEmailLink().
     */
    logInWithEmailLink(email: string, emailLink: string): Promise<firebase.auth.UserCredential> {
        if (!this.afAuth) return Promise.reject('Firebase not initialized');
        return this.afAuth.signInWithEmailLink(email, emailLink);
    }

    /**
     * Wrapper method for AngularFireAuth.getRedirectResult().
     */
    getRedirectResult(): Promise<firebase.auth.UserCredential> {
        if (!this.afAuth) return Promise.reject('Firebase not initialized');
        return this.afAuth.getRedirectResult();
    }

    /**
     * Wrapper method for AngularFireAuth.signInWithRedirect().
     */
    logInWithRedirect(provider: AuthProvider): Promise<void> {
        if (!this.afAuth) return Promise.reject('Firebase not initialized');
        return this.afAuth.signInWithRedirect(provider);
    }

    /**
     * Wrapper method for AngularFireAuth.signOut()
     */
    logout(): Promise<void> {
        if (!this.afAuth) return Promise.resolve();
        return this.afAuth.signOut();
    }

}
