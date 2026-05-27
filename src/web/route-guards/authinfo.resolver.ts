import { inject, Injectable } from '@angular/core';
import { AuthInfo } from '../types/api-output';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { AuthService } from '../services/auth.service';

/**
 * Resolvers for fetching authentication information of the user.
 */
@Injectable({
  providedIn: 'root',
})
export class AuthInfoResolver implements Resolve<AuthInfo> {
  resolve(_: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    const authService = inject(AuthService);

    return authService.getAuthUser(state.url);
  }
}
