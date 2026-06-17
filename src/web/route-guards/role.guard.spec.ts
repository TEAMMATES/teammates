import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { firstValueFrom, of } from 'rxjs';
import { Mock, vi } from 'vitest';
import { AuthService } from '../services/auth.service';
import { NavigationService } from '../services/navigation.service';
import { AuthInfo } from '../types/api-output';
import { RoleGuard } from './role.guard';

const mockState = (url: string): RouterStateSnapshot => ({ url }) as RouterStateSnapshot;

const mockRoute = (role: string | undefined, parent: ActivatedRouteSnapshot | null = null): ActivatedRouteSnapshot =>
  ({ data: role === undefined ? {} : { role }, parent }) as unknown as ActivatedRouteSnapshot;

const authInfoFor = (role: 'student' | 'instructor' | 'admin' | 'maintainer' | null): AuthInfo => {
  if (!role) {
    return { loginUrl: '/login', masquerade: false };
  }
  return {
    loginUrl: '/login',
    masquerade: false,
    user: {
      id: `test_${role}`,
      accountId: `acc_${role}`,
      email: 'user@teammates.tmt',
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
  let spyNavigationService: Partial<NavigationService>;

  beforeEach(() => {
    spyAuthService = {
      getAuthUser: vi.fn(),
    };
    spyNavigationService = {};

    TestBed.configureTestingModule({
      providers: [
        RoleGuard,
        { provide: AuthService, useValue: spyAuthService },
        { provide: NavigationService, useValue: spyNavigationService },
      ],
    });

    guard = TestBed.inject(RoleGuard);
  });

  describe('canActivate', () => {
    it('should redirect to login when user is not authenticated', async () => {
      spyAuthService.getAuthUser.mockReturnValue(of(authInfoFor(null)));

      const result = await firstValueFrom(guard.canActivate(mockRoute('admin'), mockState('/web/admin')));

      expect(result.toString()).toBe('/web/login/%2Fweb%2Fadmin');
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

    it('should redirect to unauthorized warning page when user has wrong role', async () => {
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
