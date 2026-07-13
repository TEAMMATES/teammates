import { TestBed } from '@angular/core/testing';

import { provideRouter } from '@angular/router';
import { LinkService } from './link.service';

describe('Link Service', () => {
  let service: LinkService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideRouter([])],
    });
    service = TestBed.inject(LinkService);
  });

  it('should generate the instructor welcome link', () => {
    expect(service.generateInstructorWelcomeLink('student-key-001')).toBe(
      `${globalThis.location.origin}/web/instructor/welcome/student-key-001`,
    );
  });

  it('should generate the manage account link', () => {
    expect(service.generateManageAccountLink('00000000-0000-4000-8000-000000000001', '/manage-account')).toBe(
      '/web/admin/accounts/00000000-0000-4000-8000-000000000001',
    );
  });

  it('filterEmptyParams should filter empty params', () => {
    const params: { [key: string]: string } = { courseId: '#123?123', filterThis: '' };
    service.filterEmptyParams(params);
    expect(Object.keys(params).length).toEqual(1);
  });
});
