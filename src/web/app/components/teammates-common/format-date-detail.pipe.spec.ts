import { TestBed } from '@angular/core/testing';
import { FormatDateDetailPipe } from './format-date-detail.pipe';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

describe('FormatDateDetailPipe', () => {
  let pipe: FormatDateDetailPipe;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [FormatDateDetailPipe, provideHttpClient(), provideHttpClientTesting()],
    });

    pipe = TestBed.inject(FormatDateDetailPipe);
  });

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });
});
