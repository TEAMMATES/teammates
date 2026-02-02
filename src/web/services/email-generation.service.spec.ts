import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { EmailGenerationService } from './email-generation.service';

describe('EmailGenerationService', () => {
  let service: EmailGenerationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });
    service = TestBed.inject(EmailGenerationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
