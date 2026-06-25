import { Injectable, inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, RouterStateSnapshot } from '@angular/router';
import { Observable, of, switchMap, tap, map } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { FeedbackSessionsService } from '../services/feedback-sessions.service';
import { NavigationService } from '../services/navigation.service';
import { environment } from '../environments/environment';
import { SessionKeyAccessDecision, SessionKeyAccess } from '../types/api-output';

type SessionKeyType = 'SUBMISSION' | 'RESULTS';

@Injectable({
  providedIn: 'root',
})
export class SessionKeyGuard implements CanActivate {
  private readonly feedbackSessionsService = inject(FeedbackSessionsService);
  private readonly authService = inject(AuthService);
  private readonly navigationService = inject(NavigationService);
  private readonly backendUrl: string = environment.backendUrl;

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
    const feedbackSessionId = route.paramMap.get('feedbackSessionId');
    const key = route.queryParamMap.get('key');
    const type = route.data['sessionKeyType'] as SessionKeyType | undefined;

    if (!feedbackSessionId || !key || !type) {
      this.navigationService.navigateWithErrorMessage('/web/front', 'This session link is invalid.');
      return of(false);
    }

    return this.feedbackSessionsService
      .checkSessionKeyAccess({
        feedbackSessionId,
        key,
        type,
      })
      .pipe(
        switchMap((resp: SessionKeyAccess) => {
          switch (resp.decision) {
            case SessionKeyAccessDecision.ALLOW_UNREGISTERED:
              return of(true);
            case SessionKeyAccessDecision.ALLOW_SIGNED_IN:
              return this.authService.getAuthUser(state.url).pipe(
                map(() => {
                  const targetRoute =
                    type === 'SUBMISSION'
                      ? `/web/student/sessions/${feedbackSessionId}/submission`
                      : `/web/student/sessions/${feedbackSessionId}/result`;
                  this.navigationService.navigateByURL(targetRoute);
                  return false;
                }),
              );
            case SessionKeyAccessDecision.SIGN_IN_REQUIRED:
              return this.authService.getAuthUser(state.url).pipe(
                map((authInfo) => {
                  globalThis.location.href = `${this.backendUrl}${authInfo.loginUrl}`;
                  return false;
                }),
              );
            case SessionKeyAccessDecision.SIGN_IN_WITH_ANOTHER_ACCOUNT:
            case SessionKeyAccessDecision.INVALID_KEY:
            default:
              return of(false).pipe(
                tap(() => {
                  this.navigationService.navigateWithErrorMessage(
                    '/web/front',
                    resp.message ?? 'This session link is invalid.',
                  );
                }),
              );
          }
        }),
      );
  }
}
