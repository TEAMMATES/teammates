import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ResourceEndpoints } from '../types/api-const';
import { HttpRequestService } from './http-request.service';
import { TimezoneService } from './timezone.service';

// This test does not check the timezone database used is the latest
// Only check that the version number is returned, and some sample values for timezone offset

describe('TimezoneService', () => {
  let spyHttpRequestService: any;
  let service: TimezoneService;

  beforeEach(() => {
    spyHttpRequestService = {
      get: jest.fn(),
      post: jest.fn(),
      put: jest.fn(),
      delete: jest.fn(),
    };
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
      ],
      providers: [
        { provide: HttpRequestService, useValue: spyHttpRequestService },
      ],
    });
    service = TestBed.inject(TimezoneService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should return non-empty version', () => {
    expect(service.getTzVersion()).toBeTruthy();
  });

  it('should call GET when retrieving timezones', () => {
    service.getTimeZone();
    expect(spyHttpRequestService.get).toHaveBeenCalledWith(ResourceEndpoints.TIMEZONE);
  });

  it('should return timezone offsets', () => {
    const tzOffsets: Record<string, number> = service.getTzOffsets();
    expect(tzOffsets['Etc/GMT-8']).toEqual(8 * 60);
    expect(tzOffsets['Etc/GMT+5']).toEqual(-5 * 60);
    expect(tzOffsets['Etc/GMT-11']).toEqual(11 * 60);
    expect(tzOffsets['Etc/GMT+0']).toEqual(-0);
  });
});
