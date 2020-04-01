import { TestBed } from '@angular/core/testing';
import { InstructorHelpDataSharingService } from './instructor-help-data-sharing.service';

describe('TestSuitName', () => {
  let service: InstructorHelpDataSharingService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [InstructorHelpDataSharingService],
    });
    service = TestBed.get(InstructorHelpDataSharingService); // inject InstructorHelpDataSharingService instance
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should catch emitted currStudentProfileEdit value correctly', () => {
    // Emit value
    service.collapseStudentProfileEdit(true);
    // Check caught value
    service.currStudentProfileEdit.subscribe((isStudentProfileEditCollapsed: boolean) =>
      expect(isStudentProfileEditCollapsed).toBe(true),
    );
  });

  it('should catch emitted currPeerEvalTips value correctly', () => {
    // Emit value
    service.collapsePeerEvalTips(true);
    // Check caught value
    service.currPeerEvalTips.subscribe((isCurrPeerEvalTipsCollapsed: boolean) =>
      expect(isCurrPeerEvalTipsCollapsed).toBe(true),
    );
  });
});
