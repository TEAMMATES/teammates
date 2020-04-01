import { Component, OnInit } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { InstructorHelpDataSharingService } from '../../../../services/instructor-help-data-sharing.service';
import { InstructorHelpSectionComponent } from '../instructor-help-section.component';

/**
 * Courses section of the Instructor Help Page
 */
@Component({
  selector: 'tm-instructor-help-courses-section',
  templateUrl: './instructor-help-courses-section.component.html',
  styleUrls: ['./instructor-help-courses-section.component.scss'],
})
export class InstructorHelpCoursesSectionComponent extends InstructorHelpSectionComponent implements OnInit {

  readonly supportEmail: string = environment.supportEmail;

  isAddStudentsCollapsed: boolean = false;
  isSizeLimitCollapsed: boolean = false;
  isNoTeamsCollapsed: boolean = false;
  isSectionsCollapsed: boolean = false;
  isEnrollSectionsCollapsed: boolean = false;
  isAddInstrCollapsed: boolean = false;
  isEditInstrCollapsed: boolean = false;
  isInstrAccessCollapsed: boolean = false;
  isPrivilegesCollapsed: boolean = false;
  isViewStudsCollapsed: boolean = false;
  isChangeSectionCollapsed: boolean = false;
  isDisappCourseCollapsed: boolean = false;
  isDelStudentsCollapsed: boolean = false;
  isArchiveCourseCollapsed: boolean = false;
  isViewArchivedCollapsed: boolean = false;
  isCourseUnarchCollapsed: boolean = false;
  isViewDelCollapsed: boolean = false;
  isRestoreCollapsed: boolean = false;
  isDelCollapsed: boolean = false;
  isRestoreAllCollapsed: boolean = false;

  constructor(private data: InstructorHelpDataSharingService) {
    super();
  }

  ngOnInit(): void {
  }

  /**
   * Collapses question on editing student's profile
   */
  collapseStudentProfileEdit(): void {
    this.data.collapseStudentProfileEdit(true);
  }

  /**
   * To scroll to a specific HTML id
   */
  jumpTo(target: string): boolean {
    const destination: Element | null = document.getElementById(target);
    if (destination) {
      destination.scrollIntoView();
      // to prevent the navbar from covering the text
      window.scrollTo(0, window.pageYOffset - 50);
      if (target === 'student-edit-details') {
        this.collapseStudentProfileEdit();
      }
    }
    return false;
  }
}
