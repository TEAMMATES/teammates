import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { Mock, vi } from 'vitest';
import { Observable } from 'rxjs';
import { HttpRequestService } from './http-request.service';
import { MasqueradeModeService } from './masquerade-mode.service';
import { environment } from '../environments/environment';

describe('HttpRequestService', () => {
  const backendUrl: string = environment.backendUrl;
  const withCredentials: boolean = environment.withCredentials;

  let spyHttpClient: Partial<HttpClient>;
  let spyMasqueradeModeService: Partial<MasqueradeModeService>;
  let getMasqueradeHeaderSpy: Mock;
  let service: HttpRequestService;

  beforeEach(() => {
    spyHttpClient = {
      get: vi.fn().mockReturnValue(new Observable<ArrayBuffer>()),
      post: vi.fn().mockReturnValue(new Observable()),
      put: vi.fn().mockReturnValue(new Observable()),
      delete: vi.fn().mockReturnValue(new Observable<void>()),
    };
    getMasqueradeHeaderSpy = vi.fn().mockReturnValue({});
    spyMasqueradeModeService = {
      getMasqueradeHeader: getMasqueradeHeaderSpy,
    };
    TestBed.configureTestingModule({
      providers: [
        { provide: HttpClient, useValue: spyHttpClient },
        { provide: MasqueradeModeService, useValue: spyMasqueradeModeService },
      ],
    });
    service = TestBed.inject(HttpRequestService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should build empty HttpParams from empty object', () => {
    const httpParams: HttpParams = service.buildParams({});
    expect(httpParams.keys().length).toBe(0);
  });

  it('should build proper HttpParams from key-value mapping', () => {
    const httpParams: HttpParams = service.buildParams({
      key1: 'value1',
      key2: 'value2',
      key3: 'value3',
      key4: ['value4a', 'value4b'],
    });
    expect(httpParams.keys().length).toBe(4);
    expect(httpParams.get('key1')).toBe('value1');
    expect(httpParams.get('key2')).toBe('value2');
    expect(httpParams.get('key3')).toBe('value3');
    expect(httpParams.getAll('key4')).toEqual(['value4a', 'value4b']);
  });

  it('should include masquerade account ID in HttpHeaders', () => {
    getMasqueradeHeaderSpy.mockReturnValue({
      'X-Masquerade-Account-Id': 'account-123',
    });

    service.get('/url');

    const requestOptions = (spyHttpClient.get as Mock).mock.calls[0][1] as {
      headers: HttpHeaders;
      params: HttpParams;
    };
    expect(requestOptions.headers.get('X-Masquerade-Account-Id')).toBe('account-123');
    expect(requestOptions.params.has('masqueradeaccountid')).toBe(false);
  });

  it('should execute GET', () => {
    service.get('/url');
    expect(spyHttpClient.get).toHaveBeenCalledWith(`${backendUrl}/url`, {
      params: expect.any(HttpParams),
      headers: expect.any(HttpHeaders),
      withCredentials,
    });
  });

  it('should execute POST with null body and empty params by default', () => {
    service.post('/url');
    expect(spyHttpClient.post).toHaveBeenCalledWith(`${backendUrl}/url`, null, {
      params: expect.any(HttpParams),
      headers: expect.any(HttpHeaders),
      withCredentials,
    });
  });

  it('should execute POST with params if specified', () => {
    service.post('/url', { key: 'value' });
    expect(spyHttpClient.post).toHaveBeenCalledWith(`${backendUrl}/url`, null, {
      params: expect.any(HttpParams),
      headers: expect.any(HttpHeaders),
      withCredentials,
    });
  });

  it('should execute POST with body if specified', () => {
    service.post('/url', {}, 'body');
    expect(spyHttpClient.post).toHaveBeenCalledWith(`${backendUrl}/url`, 'body', {
      params: expect.any(HttpParams),
      headers: expect.any(HttpHeaders),
      withCredentials,
    });
  });

  it('should execute PUT with null body and empty params by default', () => {
    service.put('/url');
    expect(spyHttpClient.put).toHaveBeenCalledWith(`${backendUrl}/url`, null, {
      params: expect.any(HttpParams),
      headers: expect.any(HttpHeaders),
      withCredentials,
    });
  });

  it('should execute PUT with params if specified', () => {
    service.put('/url', { key: 'value' });
    expect(spyHttpClient.put).toHaveBeenCalledWith(`${backendUrl}/url`, null, {
      params: expect.any(HttpParams),
      headers: expect.any(HttpHeaders),
      withCredentials,
    });
  });

  it('should execute PUT with body if specified', () => {
    service.put('/url', {}, 'body');
    expect(spyHttpClient.put).toHaveBeenCalledWith(`${backendUrl}/url`, 'body', {
      params: expect.any(HttpParams),
      headers: expect.any(HttpHeaders),
      withCredentials,
    });
  });

  it('should execute DELETE', () => {
    service.delete('/url');
    expect(spyHttpClient.delete).toHaveBeenCalledWith(`${backendUrl}/url`, {
      params: expect.any(HttpParams),
      headers: expect.any(HttpHeaders),
      withCredentials,
    });
  });

  it('should execute DELETE with params', () => {
    service.delete('/url', { key: 'value' });
    expect(spyHttpClient.delete).toHaveBeenCalledWith(`${backendUrl}/url`, {
      params: expect.any(HttpParams),
      headers: expect.any(HttpHeaders),
      withCredentials,
    });
  });
});
