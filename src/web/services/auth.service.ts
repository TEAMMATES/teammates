import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';
import { HttpRequestService } from './http-request.service';

/**
 * Handles user authentication.
 */
@Injectable({
  providedIn: 'root',
})
export class AuthService {

  private frontendUrl: string = environment.frontendUrl;

  constructor(private httpRequestService: HttpRequestService) {}

  /**
   * Gets the user authentication information.
   */
  getAuthUser(user?: string): Observable<any> {
    const params: { [key: string]: string } = { frontendUrl: this.frontendUrl };
    if (user) {
      params.user = user;
    }
    return this.httpRequestService.get('/auth', params);
  }

}
