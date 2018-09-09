import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';

/**
 * Handles user authentication.
 */
@Injectable({
  providedIn: 'root',
})
export class AuthService {

  private backendUrl: string = environment.backendUrl;
  private frontendUrl: string = environment.frontendUrl;
  private withCredentials: boolean = environment.withCredentials;

  constructor(private httpClient: HttpClient) {}

  /**
   * Gets the user authentication information.
   */
  getAuthUser(): Observable<any> {
    const params: HttpParams = new HttpParams().set('frontendUrl', this.frontendUrl);
    return this.httpClient.get(`${this.backendUrl}/auth`, { params, withCredentials: this.withCredentials });
  }

}
