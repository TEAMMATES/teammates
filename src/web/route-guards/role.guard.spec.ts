import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, provideRouter, RouterStateSnapshot } from '@angular/router';
import { firstValueFrom, of } from 'rxjs';
import { Mock, vi } from 'vitest';
import { AuthService } from '../services/auth.service';
import { AuthInfo } from '../types/api-output';
import { RoleGuard, UserRole } from './role.guard';

const mockState = (url: string): RouterStateSnapshot => ({ url }) as RouterStateSnapshot;

const mockRoute = (role: string | undefined, parent: ActivatedRouteSnapshot | null = null): ActivatedRouteSnapshot =>
  ({ data: role === undefined ? {} : { role }, parent }) as unknown as ActivatedRouteSnapshot;

const authInfoFor = (role: 'student' | 'instructor' | 'admin' | 'maintainer' | null): AuthInfo => {
  if (!role) {
    return { masquerade: false };
  }
  return {
    masquerade: false,
    user: {
      accountId: `acc_${role}`,
      accountEmail: 'user@teammates.tmt',
      isStudent: role === 'student',
      isInstructor: role === 'instructor',
      isAdmin: role === 'admin',
      isMaintainer: role === 'maintainer',
    },
  };
};

describe('RoleGuard', () => {
  let guard: RoleGuard;
  let spyAuthService: { getAuthUser: Mock };

  beforeEach(() => {
    spyAuthService = {
      getAuthUser: vi.fn(),
    };

    TestBed.configureTestingModule({
      providers: [
        provideRouter([]),
        RoleGuard,
        { provide: AuthService, useValue: spyAuthService },
      ],
    });

    guard = TestBed.inject(RoleGuard);
    vi.spyOn(guard as object as { redirectToLogin(): boolean }, 'redirectToLogin').mockImplementation(() => false);
  });

  describe('canActivate', () => {
    it('should return false when user is not authenticated', async () => {
      spyAuthService.getAuthUser.mockReturnValue(of(authInfoFor(null)));

      const result = await firstValueFrom(guard.canActivate(mockRoute('admin'), mockState('/web/admin')));

      expect(result).toBe(false);
    });

    it('should return true for a student accessing a student route', async () => {
      spyAuthService.getAuthUser.mockReturnValue(of(authInfoFor('student')));

      const result = await firstValueFrom(guard.canActivate(mockRoute('student'), mockState('/web/student')));

      expect(result).toBe(true);
    });

    it('should return true for an instructor accessing an instructor route', async () => {
      spyAuthService.getAuthUser.mockReturnValue(of(authInfoFor('instructor')));

      const result = await firstValueFrom(guard.canActivate(mockRoute('instructor'), mockState('/web/instructor')));

      expect(result).toBe(true);
    });

    it('should return true for an admin accessing an admin route', async () => {
      spyAuthService.getAuthUser.mockReturnValue(of(authInfoFor('admin')));

      const result = await firstValueFrom(guard.canActivate(mockRoute('admin'), mockState('/web/admin')));

      expect(result).toBe(true);
    });

    it('should return true for a maintainer accessing a maintainer route', async () => {
      spyAuthService.getAuthUser.mockReturnValue(of(authInfoFor('maintainer')));

      const result = await firstValueFrom(guard.canActivate(mockRoute('maintainer'), mockState('/web/maintainer')));

      expect(result).toBe(true);
    });

    it.each([
      ['admin', UserRole.STUDENT],
      ['admin', UserRole.INSTRUCTOR],
      ['maintainer', UserRole.STUDENT],
      ['maintainer', UserRole.INSTRUCTOR],
    ] as const)(
      'should return true for a %s accessing a %s route without the matching role',
      async (userRole: 'admin' | 'maintainer', routeRole: UserRole) => {
        spyAuthService.getAuthUser.mockReturnValue(of(authInfoFor(userRole)));

        const result = await firstValueFrom(guard.canActivate(mockRoute(routeRole), mockState(`/web/${routeRole}`)));

        expect(result).toBe(true);
      },
    );

    it('should return false and redirect to unauthorized warning page when user has wrong role', async () => {
      spyAuthService.getAuthUser.mockReturnValue(of(authInfoFor('student')));

      const result = await firstValueFrom(guard.canActivate(mockRoute('admin'), mockState('/web/admin')));

      expect(result.toString()).toContain('/web/unauthorized?role=admin');
    });

    it('should return true when route has no role requirement', async () => {
      spyAuthService.getAuthUser.mockReturnValue(of(authInfoFor('student')));

      const result = await firstValueFrom(guard.canActivate(mockRoute(undefined), mockState('/web/unauthorized')));

      expect(result).toBe(true);
    });
  });

  describe('canActivateChild', () => {
    it('should use the role from the parent route when child has no role', async () => {
      spyAuthService.getAuthUser.mockReturnValue(of(authInfoFor('admin')));
      const parentRoute = mockRoute('admin');
      const childRoute = mockRoute(undefined, parentRoute);

      const result = await firstValueFrom(guard.canActivateChild(childRoute, mockState('/web/admin/home')));

      expect(result).toBe(true);
    });

    it('should walk multiple levels up to find the role', async () => {
      spyAuthService.getAuthUser.mockReturnValue(of(authInfoFor('instructor')));
      const grandparentRoute = mockRoute('instructor');
      const parentRoute = mockRoute(undefined, grandparentRoute);
      const childRoute = mockRoute(undefined, parentRoute);

      const result = await firstValueFrom(
        guard.canActivateChild(childRoute, mockState('/web/instructor/courses/details')),
      );

      expect(result).toBe(true);
    });

    it('should deny access when ancestor role does not match user role', async () => {
      spyAuthService.getAuthUser.mockReturnValue(of(authInfoFor('student')));
      const parentRoute = mockRoute('admin');
      const childRoute = mockRoute(undefined, parentRoute);

      const result = await firstValueFrom(guard.canActivateChild(childRoute, mockState('/web/admin/home')));

      expect(result.toString()).toContain('/web/unauthorized?role=admin');
    });

    it('should return true when no ancestor has a role requirement', async () => {
      spyAuthService.getAuthUser.mockReturnValue(of(authInfoFor('student')));
      const parentRoute = mockRoute(undefined);
      const childRoute = mockRoute(undefined, parentRoute);

      const result = await firstValueFrom(guard.canActivateChild(childRoute, mockState('/web/somepage')));

      expect(result).toBe(true);
    });
  });
});
