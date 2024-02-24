import { TestBed } from '@angular/core/testing';
import { TableComparatorService } from './table-comparator.service';
import { InstructorPermissionRole } from '../types/api-request';
import { SortBy, SortOrder } from '../types/sort-properties';

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
    expect(service.compareLexicographically('Team 2', 'Team 11', SortOrder.ASC)).toEqual(1);
    expect(service.compareLexicographically('Team 2', 'Team 11', SortOrder.DESC)).toEqual(-1);
  });

  it('should compare naturally correctly', () => {
    expect(service.compareNaturally('Team 2', 'Team 11', SortOrder.ASC)).toEqual(-1);
    expect(service.compareNaturally('Team 2', 'Team 11', SortOrder.DESC)).toEqual(1);
  });

  it('should compare roles correctly', () => {
    expect(service.compareRoles(InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
      InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_CUSTOM, SortOrder.ASC)).toEqual(1);
    expect(service.compareRoles(InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_CUSTOM,
      InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER, SortOrder.DESC)).toEqual(1);
    expect(service.compareRoles(InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_OBSERVER,
      InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_TUTOR, SortOrder.DESC)).toEqual(-1);
    expect(service.compareRoles(InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_TUTOR,
      InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_OBSERVER, SortOrder.ASC)).toEqual(-1);
    expect(service.compareRoles(InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
      InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER, SortOrder.ASC)).toEqual(0);
    expect(service.compareRoles(InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
      InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER, SortOrder.DESC)).toEqual(-0);
  });

  it('should call correct method of comparison depending on element to sort by', () => {
    expect(service.compare(SortBy.SECTION_NAME, SortOrder.ASC, 'Team 2', 'Team 11')).toEqual(-1);
    expect(service.compare(SortBy.RESPONDENT_NAME, SortOrder.ASC, 'Team 2', 'Team 11')).toEqual(1);
  });

  it('should compare floating point numbers correctly', () => {
    expect(service.compareNumbers('30.5', '30.31', SortOrder.ASC)).toEqual(1);
    expect(service.compareNumbers('0.67', '0.66', SortOrder.DESC)).toEqual(-1);
    expect(service.compareNumbers('0.76', '0.76', SortOrder.ASC)).toEqual(0);
    expect(service.compareNumbers('1.76', '1.76', SortOrder.DESC)).toEqual(-0);
  });

  it('should compare NaN correctly', () => {
    expect(service.compareNumbers('-', '1.34', SortOrder.ASC)).toEqual(1);
    expect(service.compareNumbers('-', '1.34', SortOrder.DESC)).toEqual(1);
    expect(service.compareNumbers('1.34', 'NaN', SortOrder.ASC)).toEqual(-1);
    expect(service.compareNumbers('1.34', 'NaN', SortOrder.DESC)).toEqual(-1);
    expect(service.compareNumbers('-', 'NaN', SortOrder.ASC)).toEqual(1);
    expect(service.compareNumbers('NaN', '-', SortOrder.DESC)).toEqual(1);
  });
});
