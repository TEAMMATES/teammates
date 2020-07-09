import { DOCUMENT } from '@angular/common';
import { Component, EventEmitter, Inject, OnInit, Output } from '@angular/core';
import { PageScrollService } from 'ngx-page-scroll-core';
import { environment } from '../../../../environments/environment';
import { collapseAnim } from '../../../components/teammates-common/collapse-anim';
import { InstructorHelpSectionComponent } from '../instructor-help-section.component';

/**
 * Courses section of the Instructor Help Page
 */
@Component({
  selector: 'tm-instructor-help-courses-section',
  templateUrl: './instructor-help-courses-section.component.html',
  styleUrls: ['./instructor-help-courses-section.component.scss'],
  animations: [collapseAnim],
})
export class InstructorHelpCoursesSectionComponent extends InstructorHelpSectionComponent implements OnInit {

  readonly supportEmail: string = environment.supportEmail;

  isAddStudentsCollapsed: boolean = false;
  isSizeLimitCollapsed: boolean = false;
  isNoTeamsCollapsed: boolean = false;
  isSectionsCollapsed: boolean = false;
  isEnrollSectionsCollapsed: boolean = false;
  isAddInstructorCollapsed: boolean = false;
  isEditInstructorCollapsed: boolean = false;
  isInstructorAccessCollapsed: boolean = false;
  isPrivilegesCollapsed: boolean = false;
  isViewStudentsCollapsed: boolean = false;
  isChangeSectionCollapsed: boolean = false;
  isDisappearedCourseCollapsed: boolean = false;
  isDelStudentsCollapsed: boolean = false;
  isArchiveCourseCollapsed: boolean = false;
  isViewArchivedCollapsed: boolean = false;
  isCourseUnarchiveCollapsed: boolean = false;
  isViewDelCollapsed: boolean = false;
  isRestoreCollapsed: boolean = false;
  isDelCollapsed: boolean = false;
  isRestoreAllCollapsed: boolean = false;
  @Output() collapseStudentEditDetails: EventEmitter<boolean> = new EventEmitter<boolean>();

  constructor(private pageScrollService: PageScrollService,
              @Inject(DOCUMENT) private document: any) {
    super();
  }

  ngOnInit(): void {
  }

  /**
   * Scrolls to an HTML element with a given target id.
   */
  jumpTo(target: string): boolean {
    this.pageScrollService.scroll({
      document: this.document,
      scrollTarget: `#${target}`,
      scrollOffset: 70,
    });
    if (target === 'student-edit-details') {
      this.collapseStudentEditDetails.emit(true);
    }
    return false;
  }
}
