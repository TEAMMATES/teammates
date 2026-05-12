import { TestBed } from '@angular/core/testing';
import { FormatDateBriefPipe } from './format-date-brief.pipe';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

describe('FormatDateBriefPipe', () => {
  let pipe: FormatDateBriefPipe;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [FormatDateBriefPipe, provideHttpClient(), provideHttpClientTesting()],
    });

    pipe = TestBed.inject(FormatDateBriefPipe);
  });

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });
});
