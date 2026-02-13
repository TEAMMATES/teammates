import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { inject, TestBed } from '@angular/core/testing';
import { FormatDateBriefPipe } from './format-date-brief.pipe';
import { TimezoneService } from '../../../services/timezone.service';

describe('FormatDateBriefPipe', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });
  });

  it('create an instance', inject([TimezoneService], (timezoneService: TimezoneService) => {
    const pipe: FormatDateBriefPipe = new FormatDateBriefPipe(timezoneService);
    expect(pipe).toBeTruthy();
  }));
});
