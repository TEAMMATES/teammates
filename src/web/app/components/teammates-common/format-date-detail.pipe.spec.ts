import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { inject, TestBed } from '@angular/core/testing';
import { FormatDateDetailPipe } from './format-date-detail.pipe';
import { TimezoneService } from '../../../services/timezone.service';

describe('FormatDateDetailPipe', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });
  });

  it('create an instance', inject([TimezoneService], (timezoneService: TimezoneService) => {
    const pipe: FormatDateDetailPipe = new FormatDateDetailPipe(timezoneService);
    expect(pipe).toBeTruthy();
  }));
});
