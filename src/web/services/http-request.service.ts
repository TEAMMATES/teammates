import { HttpClient, HttpParams } from '@angular/common/http';
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
  get(endpoint: string, paramsMap: object = {}, responseType: any = 'json' as 'text'): Observable<any> {
    const params: HttpParams = this.buildParams(paramsMap);
    const withCredentials: boolean = this.withCredentials;
    return this.httpClient.get(`${this.backendUrl}/webapi${endpoint}`, { params, responseType, withCredentials });
  }

  /**
   * Executes POST request.
   */
  post(endpoint: string, body: any = null, paramsMap: object = {}): Observable<any> {
    const params: HttpParams = this.buildParams(paramsMap);
    const withCredentials: boolean = this.withCredentials;
    return this.httpClient.post(`${this.backendUrl}/webapi${endpoint}`, body, { params, withCredentials });
  }

  /**
   * Executes PUT request.
   */
  put(endpoint: string, body: any = null, paramsMap: object = {}): Observable<any> {
    const params: HttpParams = this.buildParams(paramsMap);
    const withCredentials: boolean = this.withCredentials;
    return this.httpClient.put(`${this.backendUrl}/webapi${endpoint}`, body, { params, withCredentials });
  }

  /**
   * Executes DELETE request.
   */
  delete(endpoint: string, paramsMap: object = {}): Observable<any> {
    const params: HttpParams = this.buildParams(paramsMap);
    const withCredentials: boolean = this.withCredentials;
    return this.httpClient.delete(`${this.backendUrl}/webapi${endpoint}`, { params, withCredentials });
  }

}
