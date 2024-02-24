import { HttpClient, HttpHeaders, HttpParams, HttpUrlEncodingCodec } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { MasqueradeModeService } from './masquerade-mode.service';
import { environment } from '../environments/environment';

/**
 * This encoder was created to support encoding and decoding of plus (+) signs.
 *
 * Angular will ignore the encoding for plus signs. Refer to:
 * https://github.com/angular/angular/blob/9.0.0/packages/common/http/src/params.ts#L34
 */
class CustomEncoder extends HttpUrlEncodingCodec {

  override encodeValue(value: string): string {
    return this.standardEncoding(value);
  }

  standardEncoding(v: string): string {
    return encodeURIComponent(v)
        .replace(/%40/gi, '@')
        .replace(/%3A/gi, ':')
        .replace(/%24/gi, '$')
        .replace(/%2C/gi, ',')
        .replace(/%3B/gi, ';')
        .replace(/%3D/gi, '=')
        .replace(/%3F/gi, '?')
        .replace(/%2F/gi, '/');
  }
}

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
  private version: string = environment.version;

  constructor(private httpClient: HttpClient, private masqueradeModeService: MasqueradeModeService) {}

  /**
   * Builds an HttpParams object from a standard key-value mapping.
   *
   * <p>Add the current masquerading user info to the params also.
   */
  buildParams(paramsMap: Record<string, string>): HttpParams {
    let params: HttpParams = new HttpParams({ encoder: new CustomEncoder() });
    for (const key of Object.keys(paramsMap)) {
      if (paramsMap[key]) {
        params = params.append(key, paramsMap[key]);
      }
    }

    if (this.masqueradeModeService.isInMasqueradingMode() && params.keys().indexOf('user') === -1) {
      params = params.append('user', this.masqueradeModeService.getMasqueradeUser());
    }
    return params;
  }

  /**
   * Executes GET request.
   */
  get(endpoint: string, paramsMap: Record<string, string> = {},
      responseType: any = 'json' as 'text'): Observable<any> {
    const params: HttpParams = this.buildParams(paramsMap);
    const withCredentials: boolean = this.withCredentials;
    const headers: HttpHeaders = this.getHeaders(false);
    return this.httpClient.get(
        `${this.backendUrl}${endpoint}`,
        { params, headers, responseType, withCredentials },
    );
  }

  /**
   * Executes POST request.
   */
  post(endpoint: string, paramsMap: Record<string, string> = {}, body: any = null): Observable<any> {
    const params: HttpParams = this.buildParams(paramsMap);
    const withCredentials: boolean = this.withCredentials;
    const headers: HttpHeaders = this.getHeaders(true);
    return this.httpClient.post(
        `${this.backendUrl}${endpoint}`, body,
        { params, headers, withCredentials },
    );
  }

  /**
   * Executes PUT request.
   */
  put(endpoint: string, paramsMap: Record<string, string> = {}, body: any = null): Observable<any> {
    const params: HttpParams = this.buildParams(paramsMap);
    const withCredentials: boolean = this.withCredentials;
    const headers: HttpHeaders = this.getHeaders(true);
    return this.httpClient.put(
        `${this.backendUrl}${endpoint}`,
        body,
        { params, headers, withCredentials },
    );
  }

  /**
   * Executes DELETE request.
   */
  delete(endpoint: string, paramsMap: Record<string, string> = {}): Observable<any> {
    const params: HttpParams = this.buildParams(paramsMap);
    const withCredentials: boolean = this.withCredentials;
    const headers: HttpHeaders = this.getHeaders(true);
    return this.httpClient.delete(
        `${this.backendUrl}${endpoint}`,
        { params, headers, withCredentials },
    );
  }

  private getHeaders(withCsrfHeader: boolean): HttpHeaders {
    const headers: Record<string, string> = {
      'X-WEB-VERSION': this.version,
      'ngsw-bypass': 'true',
    };
    if (withCsrfHeader && document.cookie) {
      const csrfTokenCookie: string[] = document.cookie.split('; ').filter((c: string) => c.startsWith('CSRF-TOKEN'));
      if (csrfTokenCookie.length) {
        headers['X-CSRF-TOKEN'] = csrfTokenCookie[0].replace('CSRF-TOKEN=', '');
      }
    }
    return new HttpHeaders(headers);
  }

}
