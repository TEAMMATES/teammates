import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot } from '@angular/router';
import { firstValueFrom, of } from 'rxjs';
import { Mock, vi } from 'vitest';
import { AuthService } from '../services/auth.service';
import { FeedbackSessionsService } from '../services/feedback-sessions.service';
import { NavigationService } from '../services/navigation.service';
import { SessionKeyAccessDecision } from '../types/api-output';
import { SessionKeyType } from '../types/api-request';
import { SessionKeyGuard } from './session-key.guard';

const mockState = (url: string): RouterStateSnapshot => ({ url }) as RouterStateSnapshot;

const mockRoute = (
  type: SessionKeyType = SessionKeyType.SUBMISSION,
  key = 'session-key',
  fsid = 'session-id',
  previewAs = '',
  moderatedPerson = '',
): ActivatedRouteSnapshot =>
  ({
    data: { sessionKeyType: type },
    paramMap: new Map([['feedbackSessionId', fsid]]) as never,
    queryParamMap: new Map([
      ['key', key],
      ['previewAs', previewAs],
      ['moderatedPerson', moderatedPerson],
    ]) as never,
  }) as unknown as ActivatedRouteSnapshot;

describe('SessionKeyGuard', () => {
  let guard: SessionKeyGuard;
  let spyFeedbackSessionsService: { checkSessionKeyAccess: Mock };
  let spyAuthService: { getAuthUser: Mock };
  let spyNavigationService: { navigateWithErrorMessage: Mock; navigateByURL: Mock };
  let spyRouter: { createUrlTree: Mock };

  beforeEach(() => {
    spyFeedbackSessionsService = {
      checkSessionKeyAccess: vi.fn(),
    };
    spyAuthService = {
      getAuthUser: vi.fn(),
    };
    spyNavigationService = {
      navigateWithErrorMessage: vi.fn(),
      navigateByURL: vi.fn(),
    };
    spyRouter = {
      createUrlTree: vi.fn().mockReturnValue('login-tree'),
    };

    TestBed.configureTestingModule({
      providers: [
        SessionKeyGuard,
        { provide: FeedbackSessionsService, useValue: spyFeedbackSessionsService },
        { provide: AuthService, useValue: spyAuthService },
        { provide: NavigationService, useValue: spyNavigationService },
        { provide: Router, useValue: spyRouter },
      ],
    });

    guard = TestBed.inject(SessionKeyGuard);
  });

  it('should allow access when backend preflight allows access', async () => {
    spyFeedbackSessionsService.checkSessionKeyAccess.mockReturnValue(
      of({ decision: SessionKeyAccessDecision.ALLOW_UNREGISTERED, message: '' }),
    );

    const result = await firstValueFrom(
      guard.canActivate(mockRoute(), mockState('/web/student/sessions/session-id/submission')),
    );

    expect(result).toBe(true);
  });

  it('should redirect to the logged in submission page when access is allowed and user is signed in', async () => {
    spyFeedbackSessionsService.checkSessionKeyAccess.mockReturnValue(
      of({ decision: SessionKeyAccessDecision.ALLOW_SIGNED_IN, message: '' }),
    );
    spyAuthService.getAuthUser.mockReturnValue(
      of({
        loginUrl: '/',
        masquerade: false,
        user: {
          accountId: 'account-id',
          accountEmail: 'test@example.com',
        },
      }),
    );

    const result = await firstValueFrom(
      guard.canActivate(
        mockRoute(SessionKeyType.SUBMISSION),
        mockState('/web/sessions/session-id/submission?key=session-key'),
      ),
    );

    expect(spyNavigationService.navigateByURL).toHaveBeenCalledWith('/web/student/sessions/session-id/submission');
    expect(spyNavigationService.navigateWithErrorMessage).not.toHaveBeenCalled();
    expect(result).toBe(false);
  });

  it('should redirect to error route when key is invalid', async () => {
    spyFeedbackSessionsService.checkSessionKeyAccess.mockReturnValue(
      of({ decision: SessionKeyAccessDecision.INVALID_KEY, message: 'This session link is invalid.' }),
    );

    const result = await firstValueFrom(
      guard.canActivate(mockRoute(), mockState('/web/student/sessions/session-id/submission')),
    );

    expect(spyNavigationService.navigateWithErrorMessage).toHaveBeenCalledWith(
      '/web/front',
      'This session link is invalid.',
    );
    expect(result).toBe(false);
  });

  it('should redirect to home with a fallback error message when no message is provided', async () => {
    spyFeedbackSessionsService.checkSessionKeyAccess.mockReturnValue(
      of({ decision: SessionKeyAccessDecision.INVALID_KEY, message: null }),
    );

    const result = await firstValueFrom(
      guard.canActivate(mockRoute(), mockState('/web/student/sessions/session-id/submission')),
    );

    expect(spyNavigationService.navigateWithErrorMessage).toHaveBeenCalledWith(
      '/web/front',
      'This session link is invalid.',
    );
    expect(result).toBe(false);
  });

  it('should allow sign-in redirect when backend requires auth', async () => {
    spyFeedbackSessionsService.checkSessionKeyAccess.mockReturnValue(
      of({ decision: SessionKeyAccessDecision.SIGN_IN_REQUIRED, message: null }),
    );

    const result = await firstValueFrom(
      guard.canActivate(mockRoute(), mockState('/web/student/sessions/session-id/submission')),
    );

    expect(spyRouter.createUrlTree).toHaveBeenCalledWith(['/web/login'], {
      queryParams: { nextUrl: '/web/student/sessions/session-id/submission' },
    });
    expect(result).toBe('login-tree');
  });

  it('should allow preview routes without a key when the user is signed in', async () => {
    const result = await firstValueFrom(
      guard.canActivate(
        mockRoute(SessionKeyType.SUBMISSION, '', 'session-id', 'student-id'),
        mockState('/web/sessions/session-id/submission?previewAs=student-id'),
      ),
    );

    expect(spyFeedbackSessionsService.checkSessionKeyAccess).not.toHaveBeenCalled();
    expect(spyAuthService.getAuthUser).not.toHaveBeenCalled();
    expect(result).toBe(true);
  });

  it('should allow moderation routes without a key', async () => {
    const result = await firstValueFrom(
      guard.canActivate(
        mockRoute(SessionKeyType.SUBMISSION, '', 'session-id', '', 'student-id'),
        mockState('/web/sessions/session-id/submission?moderatedPerson=student-id'),
      ),
    );

    expect(spyFeedbackSessionsService.checkSessionKeyAccess).not.toHaveBeenCalled();
    expect(spyAuthService.getAuthUser).not.toHaveBeenCalled();
    expect(result).toBe(true);
  });
});
