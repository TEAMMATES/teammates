import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { HttpRequestService } from './http-request.service';
import { TimezoneService } from './timezone.service';
import createSpyFromClass from '../test-helpers/create-spy-from-class';
import { ResourceEndpoints } from '../types/api-const';
import { Milliseconds } from '../types/datetime-const';

// This test does not check the timezone database used is the latest
// Only check that the version number is returned, and some sample values for timezone offset

describe('TimezoneService', () => {
  let spyHttpRequestService: any;
  let service: TimezoneService;

  beforeEach(() => {
    spyHttpRequestService = createSpyFromClass(HttpRequestService);
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

  it('should resolve local date time to the same epoch time in different timezones', () => {
    const gmtMinus8Time: number = service.resolveLocalDateTime(
        { year: 2020, month: 10, day: 7 },
        { hour: 17, minute: 0 }, 'Etc/GMT-8');
    const gmtPlus8Time: number = service.resolveLocalDateTime(
        { year: 2020, month: 10, day: 7 },
        { hour: 1, minute: 0 }, 'Etc/GMT+8');
    const gmtTime: number = service.resolveLocalDateTime(
        { year: 2020, month: 10, day: 7 },
        { hour: 9, minute: 0 }, 'Etc/GMT');
    const sgTime: number = service.resolveLocalDateTime(
        { year: 2020, month: 10, day: 7 },
        { hour: 17, minute: 0 }, 'Asia/Singapore');

    expect(gmtTime).toEqual(gmtPlus8Time);
    expect(gmtTime).toEqual(gmtMinus8Time);
    expect(gmtTime).toEqual(sgTime);
  });

  it('should resolve local date time: DST springing forward', () => {
    // For this test case the date used will be March 8, 2020 in US/Central timezone,
    // where clock sprang forward from 1.59AM to 3.00AM.

    const baseEpochTime: number = 1583650800000;

    let usTime: number = service.resolveLocalDateTime(
        { year: 2020, month: 3, day: 8 },
        { hour: 1, minute: 0 }, 'US/Central');
    let utcTime: number = service.resolveLocalDateTime(
        { year: 2020, month: 3, day: 8 },
        { hour: 7, minute: 0 }, 'UTC');

    // First make sure that in normal situation, date/time can be resolved in different timezones.
    expect(usTime).toEqual(utcTime);
    expect(usTime).toEqual(baseEpochTime);

    usTime = service.resolveLocalDateTime(
        { year: 2020, month: 3, day: 8 },
        { hour: 2, minute: 0 }, 'US/Central');

    // Here, the clock has sprung forward and the time does not actually exist in the timezone.
    // It can be resolved to either the next or previous available hour.
    expect(usTime === baseEpochTime + Milliseconds.IN_ONE_HOUR || usTime === baseEpochTime).toBeTruthy();

    usTime = service.resolveLocalDateTime(
        { year: 2020, month: 3, day: 8 },
        { hour: 3, minute: 0 }, 'US/Central');

    // The time here is a legitimate time, but the difference with 1.00AM is just one hour
    // as the 2.00-2.59AM hour mark does not exist.
    expect(usTime).toEqual(baseEpochTime + Milliseconds.IN_ONE_HOUR);

    usTime = service.resolveLocalDateTime(
        { year: 2020, month: 3, day: 8 },
        { hour: 4, minute: 0 }, 'US/Central');

    // After the clock has sprung forward, time should be calculated as per normal.
    expect(usTime).toEqual(baseEpochTime + 2 * Milliseconds.IN_ONE_HOUR);

    utcTime = service.resolveLocalDateTime(
        { year: 2020, month: 3, day: 8 },
        { hour: 9, minute: 0 }, 'UTC');

    // This confirms that the time difference with UTC has been adjusted.
    expect(usTime).toEqual(utcTime);
  });

  it('should resolve local date time: DST springing backward', () => {
    // For this test case the date used will be November 1, 2020 in US/Central timezone,
    // where clock sprang backward from 1.59AM to 1.00AM.

    const baseEpochTime: number = 1604206800000;

    let usTime: number = service.resolveLocalDateTime(
        { year: 2020, month: 11, day: 1 },
        { hour: 0, minute: 0 }, 'US/Central');
    let utcTime: number = service.resolveLocalDateTime(
        { year: 2020, month: 11, day: 1 },
        { hour: 5, minute: 0 }, 'UTC');

    // First make sure that in normal situation, date/time can be resolved in different timezones.
    expect(usTime).toEqual(utcTime);
    expect(usTime).toEqual(baseEpochTime);

    usTime = service.resolveLocalDateTime(
        { year: 2020, month: 11, day: 1 },
        { hour: 1, minute: 0 }, 'US/Central');

    // Here, the clock has sprung backward and the time exists in duplicate.
    // It can be resolved to either the earlier or the latter hour.
    expect(usTime === baseEpochTime + Milliseconds.IN_ONE_HOUR
      || usTime === baseEpochTime + 2 * Milliseconds.IN_ONE_HOUR)
      .toBeTruthy();

    usTime = service.resolveLocalDateTime(
        { year: 2020, month: 11, day: 1 },
        { hour: 2, minute: 0 }, 'US/Central');

    // After the clock has sprung backward, time should be calculated as per normal.
    expect(usTime).toEqual(baseEpochTime + 3 * Milliseconds.IN_ONE_HOUR);

    utcTime = service.resolveLocalDateTime(
        { year: 2020, month: 11, day: 1 },
        { hour: 8, minute: 0 }, 'UTC');

    // This confirms that the time difference with UTC has been adjusted.
    expect(usTime).toEqual(utcTime);
  });
});
