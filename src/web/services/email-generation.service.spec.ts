import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { EmailGenerationService } from './email-generation.service';

describe('EmailGenerationService', () => {
  let service: EmailGenerationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
      ],
    });
    service = TestBed.inject(EmailGenerationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
