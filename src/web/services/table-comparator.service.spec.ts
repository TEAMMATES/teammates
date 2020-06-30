import { TestBed } from '@angular/core/testing';
import { SortBy, SortOrder } from '../types/sort-properties';
import { TableComparatorService } from './table-comparator.service';

describe('SortableService', () => {
  let service: TableComparatorService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        TableComparatorService,
      ],
    });
    service = TestBed.inject(TableComparatorService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should compare lexicographically correctly', () => {
    expect(service.compareLexicographically('Team 2' , 'Team 11', SortOrder.ASC)).toEqual(1);
    expect(service.compareLexicographically('Team 2' , 'Team 11', SortOrder.DESC)).toEqual(-1);
  });

  it('should compare naturally correctly', () => {
    expect(service.compareNaturally('Team 2' , 'Team 11', SortOrder.ASC)).toEqual(-1);
    expect(service.compareNaturally('Team 2' , 'Team 11', SortOrder.DESC)).toEqual(1);
  });

  it('should call correct method of comparison depending on element to sort by', () => {
    expect(service.compare(SortBy.SECTION_NAME, SortOrder.ASC, 'Team 2' , 'Team 11')).toEqual(-1);
    expect(service.compare(SortBy.STUDENT_NAME, SortOrder.ASC, 'Team 2' , 'Team 11')).toEqual(1);
  });
});
