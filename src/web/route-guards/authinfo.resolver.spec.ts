import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { of, EMPTY } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { AuthInfoResolver } from './authinfo.resolver';
import createSpyFromClass from '../test-helpers/create-spy-from-class';
import { environment } from '../environments/environment.prod';

const mockState = (url: string): RouterStateSnapshot => ({ url }) as RouterStateSnapshot;

describe('AuthInfoResolver', () => {
  let resolver: AuthInfoResolver;
  let spyAuthService: any;
  let originalMaintenance: boolean;

  beforeEach(() => {
    spyAuthService = createSpyFromClass(AuthService);
    TestBed.configureTestingModule({
      providers: [
        AuthInfoResolver,
        { provide: AuthService, useValue: spyAuthService },
      ],
    });
    resolver = TestBed.inject(AuthInfoResolver);
    originalMaintenance = environment.maintenance;
  });

  afterEach(() => {
    environment.maintenance = originalMaintenance;
  });

  it('should return EMPTY if maintenance mode is enabled', () => {
    environment.maintenance = true;
    const result = resolver.resolve({} as ActivatedRouteSnapshot, mockState('/web'));
    expect(result).toBe(EMPTY);
  });

  it('should call getAuthUser with state.url when not in maintenance', () => {
    environment.maintenance = false;
    const expected = { user: { id: 'test' } };
    spyAuthService.getAuthUser.mockReturnValue(of(expected));
    const result$ = resolver.resolve({} as ActivatedRouteSnapshot, mockState('/web/test'));
    let value: any;
    result$.subscribe((v: any) => (value = v));
    expect(spyAuthService.getAuthUser).toHaveBeenCalledWith('/web/test');
    expect(value).toEqual(expected);
  });
});
