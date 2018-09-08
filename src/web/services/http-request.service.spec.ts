import { HttpClient, HttpParams } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { environment } from '../environments/environment';
import { HttpRequestService } from './http-request.service';

describe('HttpRequestService', () => {
  const backendUrl: string = environment.backendUrl;
  const withCredentials: boolean = environment.withCredentials;

  let spyHttpClient: jasmine.SpyObj<HttpClient>;
  let service: HttpRequestService;

  beforeEach(() => {
    spyHttpClient = jasmine.createSpyObj('HttpClient', ['get', 'post', 'put', 'delete']);
    TestBed.configureTestingModule({
      providers: [
        { provide: HttpClient, useValue: spyHttpClient },
      ],
    });
    service = TestBed.get(HttpRequestService);
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

  it('should execute GET with JSON response type by default', () => {
    service.get('/url');
    expect(spyHttpClient.get).toHaveBeenCalledWith(`${backendUrl}/webapi/url`, {
      withCredentials,
      params: jasmine.any(Object),
      responseType: 'json',
    });
  });

  it('should execute GET with specified response type if specified', () => {
    service.get('/url', {}, 'text');
    expect(spyHttpClient.get).toHaveBeenCalledWith(`${backendUrl}/webapi/url`, {
      withCredentials,
      params: jasmine.any(Object),
      responseType: 'text',
    });
  });

  it('should execute POST with null body by default', () => {
    service.post('/url');
    expect(spyHttpClient.post).toHaveBeenCalledWith(`${backendUrl}/webapi/url`, null, {
      withCredentials,
      params: jasmine.any(Object),
    });
  });

  it('should execute POST with specified body if specified', () => {
    service.post('/url', 'body');
    expect(spyHttpClient.post).toHaveBeenCalledWith(`${backendUrl}/webapi/url`, 'body', {
      withCredentials,
      params: jasmine.any(Object),
    });
  });

  it('should execute POST with params', () => {
    service.post('/url', null, { key: 'value' });
    expect(spyHttpClient.post).toHaveBeenCalledWith(`${backendUrl}/webapi/url`, null, {
      withCredentials,
      params: jasmine.any(Object),
    });
  });

  it('should execute PUT with null body by default', () => {
    service.put('/url');
    expect(spyHttpClient.put).toHaveBeenCalledWith(`${backendUrl}/webapi/url`, null, {
      withCredentials,
      params: jasmine.any(Object),
    });
  });

  it('should execute PUT with specified body if specified', () => {
    service.put('/url', 'body');
    expect(spyHttpClient.put).toHaveBeenCalledWith(`${backendUrl}/webapi/url`, 'body', {
      withCredentials,
      params: jasmine.any(Object),
    });
  });

  it('should execute PUT with params', () => {
    service.put('/url', null, { key: 'value' });
    expect(spyHttpClient.put).toHaveBeenCalledWith(`${backendUrl}/webapi/url`, null, {
      withCredentials,
      params: jasmine.any(Object),
    });
  });

  it('should execute DELETE', () => {
    service.delete('/url');
    expect(spyHttpClient.delete).toHaveBeenCalledWith(`${backendUrl}/webapi/url`, {
      withCredentials,
      params: jasmine.any(Object),
    });
  });

  it('should execute DELETE with params', () => {
    service.delete('/url', { key: 'value' });
    expect(spyHttpClient.delete).toHaveBeenCalledWith(`${backendUrl}/webapi/url`, {
      withCredentials,
      params: jasmine.any(Object),
    });
  });

});
