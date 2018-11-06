import { HttpClient } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import * as timezoneData from '../assets/data/timezone.json';
import { TimezoneService } from './timezone.service';

// This test does not check the timezone database used is the latest
// Only check that the version number is returned, and some sample values for timezone offset

describe('TimezoneService', () => {
  let spyHttpClient: jasmine.SpyObj<HttpClient>;
  let service: TimezoneService;

  beforeEach(() => {
    spyHttpClient = jasmine.createSpyObj('HttpClient', {
      get: of(timezoneData.default),
    });
    TestBed.configureTestingModule({
      providers: [
        { provide: HttpClient, useValue: spyHttpClient },
      ],
    });
    service = TestBed.get(TimezoneService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should return non-empty version', () => {
    expect(service.getTzVersion()).toBeTruthy();
  });

  it('should return timezone offsets', () => {
    const tzOffsets: { [key: string]: number } = service.getTzOffsets();
    expect(tzOffsets['Asia/Singapore']).toEqual(8 * 60);
    expect(tzOffsets['America/New_York']).toEqual(-5 * 60);
    expect(tzOffsets['Australia/Sydney']).toEqual(11 * 60);
    expect(tzOffsets['Europe/London']).toEqual(-0);
  });
});
