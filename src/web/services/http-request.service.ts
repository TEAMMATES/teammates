import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';

/**
 * Handles HTTP requests.
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
  buildParams(paramsMap: any): HttpParams {
    let params: HttpParams = new HttpParams();
    for (const key of Object.keys(paramsMap)) {
      params = params.append(key, paramsMap[key]);
    }
    return params;
  }

  /**
   * Executes GET request.
   */
  get(endpoint: string, paramsMap: { [key: string]: string } = {}): Observable<any> {
    const params: HttpParams = this.buildParams(paramsMap);
    const withCredentials: boolean = this.withCredentials;
    const headers: HttpHeaders = this.getXsrfHeader();
    return this.httpClient.get(`${this.backendUrl}/webapi${endpoint}`, { params, headers, withCredentials });
  }

  /**
   * Executes POST request.
   */
  post(endpoint: string, paramsMap: { [key: string]: string } = {}, body: any = null): Observable<any> {
    const params: HttpParams = this.buildParams(paramsMap);
    const withCredentials: boolean = this.withCredentials;
    const headers: HttpHeaders = this.getXsrfHeader();
    return this.httpClient.post(`${this.backendUrl}/webapi${endpoint}`, body, { params, headers, withCredentials });
  }

  /**
   * Executes PUT request.
   */
  put(endpoint: string, paramsMap: { [key: string]: string } = {}, body: any = null): Observable<any> {
    const params: HttpParams = this.buildParams(paramsMap);
    const withCredentials: boolean = this.withCredentials;
    const headers: HttpHeaders = this.getXsrfHeader();
    return this.httpClient.put(`${this.backendUrl}/webapi${endpoint}`, body, { params, headers, withCredentials });
  }

  /**
   * Executes DELETE request.
   */
  delete(endpoint: string, paramsMap: { [key: string]: string } = {}): Observable<any> {
    const params: HttpParams = this.buildParams(paramsMap);
    const withCredentials: boolean = this.withCredentials;
    const headers: HttpHeaders = this.getXsrfHeader();
    return this.httpClient.delete(`${this.backendUrl}/webapi${endpoint}`, { params, headers, withCredentials });
  }

  private getXsrfHeader(): HttpHeaders {
    if (!document.cookie) {
      return new HttpHeaders();
    }
    const xsrfTokenCookie: string[] = document.cookie.split('; ').filter((c: string) => c.startsWith('XSRF-TOKEN'));
    if (xsrfTokenCookie.length) {
      return new HttpHeaders({
        'X-XSRF-TOKEN': xsrfTokenCookie[0].replace('XSRF-TOKEN=', ''),
      });
    }
    return new HttpHeaders();
  }

}
