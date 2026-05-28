import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { firstValueFrom, of } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { AuthGuard } from './auth.guard';
import createSpyFromClass from '../test-helpers/create-spy-from-class';
import { AuthInfo } from '../types/api-output';

describe('AuthGuard', () => {
  let guard: AuthGuard;
  let spyAuthService: any;

  const mockState = (url: string): RouterStateSnapshot => ({ url }) as RouterStateSnapshot;

  beforeEach(() => {
    spyAuthService = createSpyFromClass(AuthService);
    TestBed.configureTestingModule({
      providers: [AuthGuard, { provide: AuthService, useValue: spyAuthService }],
    });
    guard = TestBed.inject(AuthGuard);
  });

  it('should return true if user is authenticated', async () => {
    const authInfo: AuthInfo = {
      loginUrl: '/login',
      masquerade: false,
      user: {
        id: 'test',
        accountId: 'acc_test',
        isAdmin: false,
        isInstructor: false,
        isStudent: true,
        isMaintainer: false,
      },
    };
    spyAuthService.getAuthUser.mockReturnValue(of(authInfo));
    const result = await firstValueFrom(guard.canActivate({} as ActivatedRouteSnapshot, mockState('/web')));
    expect(result).toBe(true);
  });

  it('should return false and redirect if user is not authenticated', async () => {
    const authInfo: AuthInfo = { loginUrl: '/login', masquerade: false };
    spyAuthService.getAuthUser.mockReturnValue(of(authInfo));
    // Patch window.location.href
    const orig = window.location;
    // @ts-ignore
    delete window.location;
    // @ts-ignore
    window.location = { href: '' };
    const result = await firstValueFrom(guard.canActivate({} as ActivatedRouteSnapshot, mockState('/web')));
    expect(result).toBe(false);
    // @ts-ignore
    window.location = orig;
  });

  it('canActivateChild delegates to canActivate', async () => {
    const authInfo: AuthInfo = {
      loginUrl: '/login',
      masquerade: false,
      user: {
        id: 'test',
        accountId: 'acc_test',
        isAdmin: false,
        isInstructor: false,
        isStudent: true,
        isMaintainer: false,
      },
    };
    spyAuthService.getAuthUser.mockReturnValue(of(authInfo));
    const result = await firstValueFrom(guard.canActivateChild({} as ActivatedRouteSnapshot, mockState('/web')));
    expect(result).toBe(true);
  });
});
