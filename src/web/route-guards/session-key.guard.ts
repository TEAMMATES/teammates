import { Injectable, inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable, of, switchMap, tap, map } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { FeedbackSessionsService } from '../services/feedback-sessions.service';
import { NavigationService } from '../services/navigation.service';
import { SessionKeyAccessDecision, SessionKeyAccess } from '../types/api-output';

type SessionKeyType = 'SUBMISSION' | 'RESULTS';

@Injectable({
  providedIn: 'root',
})
export class SessionKeyGuard implements CanActivate {
  private readonly feedbackSessionsService = inject(FeedbackSessionsService);
  private readonly authService = inject(AuthService);
  private readonly navigationService = inject(NavigationService);
  private readonly router = inject(Router);

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> {
    const feedbackSessionId = route.paramMap.get('feedbackSessionId');
    const key = route.queryParamMap.get('key');
    const previewAs = route.queryParamMap.get('previewAs');
    const moderatedPerson = route.queryParamMap.get('moderatedPerson');
    const type = route.data['sessionKeyType'] as SessionKeyType | undefined;

    if (previewAs || moderatedPerson) {
      return of(true);
    }

    if (!feedbackSessionId || !type || !key) {
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
              return of(this.router.createUrlTree(['/web/login'], { queryParams: { nextUrl: state.url } }));
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
