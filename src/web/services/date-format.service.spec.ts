import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { DateFormatService } from './date-format.service';
import { TimezoneService } from './timezone.service';

describe('DateFormatService', () => {
  let util: DateFormatService;
  let timezoneService: TimezoneService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });

    util = TestBed.inject(DateFormatService);
    timezoneService = TestBed.inject(TimezoneService);
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('should create an instance', () => {
    expect(util).toBeTruthy();
  });

  it('formatDateBrief should call formatToString with "D MMM h:mm A" format', () => {
    vi.spyOn(timezoneService, 'formatToString').mockReturnValue('1 Jun 3:00 PM');

    const result = util.formatDateBrief(1748772000000, 'Asia/Singapore');

    expect(timezoneService.formatToString).toHaveBeenCalledWith(1748772000000, 'Asia/Singapore', 'D MMM h:mm A');
    expect(result).toBe('1 Jun 3:00 PM');
  });

  it('formatDateDetail should call formatToString with "ddd, DD MMM YYYY, hh:mm A z" format', () => {
    vi.spyOn(timezoneService, 'formatToString').mockReturnValue('Mon, 01 Jun 2026, 03:00 PM SGT');

    const result = util.formatDateDetailed(1748772000000, 'Asia/Singapore');

    expect(timezoneService.formatToString).toHaveBeenCalledWith(
      1748772000000,
      'Asia/Singapore',
      'ddd, DD MMM YYYY, hh:mm A z',
    );
    expect(result).toBe('Mon, 01 Jun 2026, 03:00 PM SGT');
  });
});
