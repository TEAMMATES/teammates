import { HttpClientTestingModule } from '@angular/common/http/testing';
import { inject, TestBed } from '@angular/core/testing';
import { TimezoneService } from '../../../services/timezone.service';
import { FormatDateDetailPipe } from './format-date-detail.pipe';

describe('FormatDateDetailPipe', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
  });

  it('create an instance', inject([TimezoneService], (timezoneService: TimezoneService) => {
    const pipe: FormatDateDetailPipe = new FormatDateDetailPipe(timezoneService);
    expect(pipe).toBeTruthy();
  }));
});
