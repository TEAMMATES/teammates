import { HttpClient } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { AuthService } from './auth.service';

describe('AuthService', () => {
  let spyHttpClient: jasmine.SpyObj<HttpClient>;

  beforeEach(() => {
    spyHttpClient = jasmine.createSpyObj('HttpClient', ['get']);
    TestBed.configureTestingModule({
      providers: [
        HttpClient,
        { provide: HttpClient, useValue: spyHttpClient },
      ],
    });
  });

  it('should be created', () => {
    const service: AuthService = TestBed.get(AuthService);
    expect(service).toBeTruthy();
  });
});
