import { HttpClient, HttpParams } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';
import { HttpRequestService } from './http-request.service';

describe('HttpRequestService', () => {
  const backendUrl: string = environment.backendUrl;
  const withCredentials: boolean = environment.withCredentials;

  let spyHttpClient: any;
  let service: HttpRequestService;

  beforeEach(() => {
    spyHttpClient = {
      get: jest.fn(),
      post: jest.fn(),
      put: jest.fn(),
      delete: jest.fn(),
    };
    spyHttpClient.get.mockReturnValue(new Observable<ArrayBuffer>());
    TestBed.configureTestingModule({
      providers: [
        { provide: HttpClient, useValue: spyHttpClient },
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
    });
    expect(httpParams.keys().length).toBe(3);
    expect(httpParams.get('key1')).toBe('value1');
    expect(httpParams.get('key2')).toBe('value2');
    expect(httpParams.get('key3')).toBe('value3');
  });

  it('should execute GET', () => {
    service.get('/url');
    expect(spyHttpClient.get).toHaveBeenCalledWith(`${backendUrl}/url`, {
      withCredentials,
      headers: expect.any(Object),
      params: expect.any(Object), responseType: 'json' as 'text',
    });
  });

  it('should execute POST with null body and empty params by default', () => {
    service.post('/url');
    expect(spyHttpClient.post).toHaveBeenCalledWith(`${backendUrl}/url`, null, {
      withCredentials,
      headers: expect.any(Object),
      params: expect.any(Object),
    });
  });

  it('should execute POST with params if specified', () => {
    service.post('/url', { key: 'value' });
    expect(spyHttpClient.post).toHaveBeenCalledWith(`${backendUrl}/url`, null, {
      withCredentials,
      headers: expect.any(Object),
      params: expect.any(Object),
    });
  });

  it('should execute POST with body if specified', () => {
    service.post('/url', {}, 'body');
    expect(spyHttpClient.post).toHaveBeenCalledWith(`${backendUrl}/url`, 'body', {
      withCredentials,
      headers: expect.any(Object),
      params: expect.any(Object),
    });
  });

  it('should execute PUT with null body and empty params by default', () => {
    service.put('/url');
    expect(spyHttpClient.put).toHaveBeenCalledWith(`${backendUrl}/url`, null, {
      withCredentials,
      headers: expect.any(Object),
      params: expect.any(Object),
    });
  });

  it('should execute PUT with params if specified', () => {
    service.put('/url', { key: 'value' });
    expect(spyHttpClient.put).toHaveBeenCalledWith(`${backendUrl}/url`, null, {
      withCredentials,
      headers: expect.any(Object),
      params: expect.any(Object),
    });
  });

  it('should execute PUT with body if specified', () => {
    service.put('/url', {}, 'body');
    expect(spyHttpClient.put).toHaveBeenCalledWith(`${backendUrl}/url`, 'body', {
      withCredentials,
      headers: expect.any(Object),
      params: expect.any(Object),
    });
  });

  it('should execute DELETE', () => {
    service.delete('/url');
    expect(spyHttpClient.delete).toHaveBeenCalledWith(`${backendUrl}/url`, {
      withCredentials,
      headers: expect.any(Object),
      params: expect.any(Object),
    });
  });

  it('should execute DELETE with params', () => {
    service.delete('/url', { key: 'value' });
    expect(spyHttpClient.delete).toHaveBeenCalledWith(`${backendUrl}/url`, {
      withCredentials,
      headers: expect.any(Object),
      params: expect.any(Object),
    });
  });

});
