import { TestBed } from '@angular/core/testing';

import { SimpleModalService } from './simple-modal.service';

describe('SimpleModalService', () => {
  let service: SimpleModalService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SimpleModalService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
