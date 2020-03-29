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

  isCollapsed: boolean = false;
  isCollapsed1: boolean = false;
  isCollapsed2: boolean = false;
  isCollapsed3: boolean = false;
  isCollapsed4: boolean = false;
  isCollapsed5: boolean = false;
  isCollapsed6: boolean = false;
  isCollapsed7: boolean = false;
  isCollapsed8: boolean = false;
  isCollapsed9: boolean = false;
  isCollapsed10: boolean = false;
  isCollapsed11: boolean = false;
  isCollapsed12: boolean = false;
  isCollapsed13: boolean = false;
  isCollapsed14: boolean = false;
  isCollapsed15: boolean = false;
  isCollapsed16: boolean = false;
  isCollapsed17: boolean = false;
  isCollapsed18: boolean = false;
  isCollapsed19: boolean = false;

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
