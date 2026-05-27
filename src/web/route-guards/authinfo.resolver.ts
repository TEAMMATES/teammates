import { inject } from '@angular/core';
import { AuthInfo } from '../types/api-output';
import { ResolveFn, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { AuthService } from '../services/auth.service';

/**
 * Resolvers for fetching authentication information of the user.
 */
export const authInfoResolver: ResolveFn<AuthInfo> = (_: ActivatedRouteSnapshot, state: RouterStateSnapshot) => {
  const authService = inject(AuthService);

  return authService.getAuthUser(state.url);
};
