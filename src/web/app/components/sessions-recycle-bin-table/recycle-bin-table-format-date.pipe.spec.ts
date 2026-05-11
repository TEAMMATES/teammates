import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { RecycleBinTableFormatDatePipe } from './recycle-bin-table-format-date.pipe';

describe('RecycleBinTableFormatDatePipe', () => {
  let pipe: RecycleBinTableFormatDatePipe;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [RecycleBinTableFormatDatePipe, provideHttpClient(), provideHttpClientTesting()],
    });

    pipe = TestBed.inject(RecycleBinTableFormatDatePipe);
  });

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });
});
