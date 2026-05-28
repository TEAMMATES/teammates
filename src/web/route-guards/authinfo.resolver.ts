import { inject, Injectable } from '@angular/core';
import { AuthInfo } from '../types/api-output';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { environment } from '../environments/environment.prod';
import { EMPTY } from 'rxjs';

/**
 * Resolvers for fetching authentication information of the user.
 */
@Injectable({
  providedIn: 'root',
})
export class AuthInfoResolver implements Resolve<AuthInfo> {
  private authService = inject(AuthService);

  resolve(_: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    if (environment.maintenance) {
      return EMPTY;
    }
    return this.authService.getAuthUser(state.url);
  }
}
