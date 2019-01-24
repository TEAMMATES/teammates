import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';

/**
 * Handles HTTP requests to the application back-end.
 *
 * This service is not appropriate for requesting static asset files.
 */
@Injectable({
  providedIn: 'root',
})
export class HttpRequestService {

  private backendUrl: string = environment.backendUrl;
  private withCredentials: boolean = environment.withCredentials;

  constructor(private httpClient: HttpClient) {}

  /**
   * Builds an HttpParams object from a standard key-value mapping.
   */
  buildParams(paramsMap: { [key: string]: string }): HttpParams {
    let params: HttpParams = new HttpParams();
    for (const key of Object.keys(paramsMap)) {
      if (paramsMap[key]) {
        params = params.append(key, paramsMap[key]);
      }
    }
    return params;
  }

  /**
   * Executes GET request.
   */
  get(endpoint: string, paramsMap: { [key: string]: string } = {},
      responseType: any = 'json' as 'text'): Observable<any> {
    const params: HttpParams = this.buildParams(paramsMap);
    const withCredentials: boolean = this.withCredentials;
    return this.httpClient.get(`${this.backendUrl}/webapi${endpoint}`, { params, responseType, withCredentials });
  }

  /**
   * Executes POST request.
   */
  post(endpoint: string, paramsMap: { [key: string]: string } = {}, body: any = null): Observable<any> {
    const params: HttpParams = this.buildParams(paramsMap);
    const withCredentials: boolean = this.withCredentials;
    const headers: HttpHeaders = this.getCsrfHeader();
    return this.httpClient.post(`${this.backendUrl}/webapi${endpoint}`, body, { params, headers, withCredentials });
  }

  /**
   * Executes PUT request.
   */
  put(endpoint: string, paramsMap: { [key: string]: string } = {}, body: any = null): Observable<any> {
    const params: HttpParams = this.buildParams(paramsMap);
    const withCredentials: boolean = this.withCredentials;
    const headers: HttpHeaders = this.getCsrfHeader();
    return this.httpClient.put(`${this.backendUrl}/webapi${endpoint}`, body, { params, headers, withCredentials });
  }

  /**
   * Executes DELETE request.
   */
  delete(endpoint: string, paramsMap: { [key: string]: string } = {}): Observable<any> {
    const params: HttpParams = this.buildParams(paramsMap);
    const withCredentials: boolean = this.withCredentials;
    const headers: HttpHeaders = this.getCsrfHeader();
    return this.httpClient.delete(`${this.backendUrl}/webapi${endpoint}`, { params, headers, withCredentials });
  }

  private getCsrfHeader(): HttpHeaders {
    if (!document.cookie) {
      return new HttpHeaders();
    }
    const csrfTokenCookie: string[] = document.cookie.split('; ').filter((c: string) => c.startsWith('CSRF-TOKEN'));
    if (csrfTokenCookie.length) {
      return new HttpHeaders({
        'X-CSRF-TOKEN': csrfTokenCookie[0].replace('CSRF-TOKEN=', ''),
      });
    }
    return new HttpHeaders();
  }

}
