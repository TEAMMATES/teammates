import { TestBed } from '@angular/core/testing';

import { StatisticsCalculatorService } from './statistics-calculator.service';

describe('StatisticsCalculatorService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: StatisticsCalculatorService = TestBed.get(StatisticsCalculatorService);
    expect(service).toBeTruthy();
  });
});
