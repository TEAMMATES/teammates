import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { environment } from '../../../environments/environment';
import { HttpRequestService } from '../../../services/http-request.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { ErrorMessageOutput } from '../../message-output';

interface StudentListStudentData {
  studentName: string;
  studentEmail: string;
  studentStatus: string;
  photoUrl: string;
}

interface StudentListTeamData {
  teamName: string;
  students: StudentListStudentData[];
}

interface StudentListSectionData {
  sectionName: string;
  teams: StudentListTeamData[];
}

interface CourseAttributes {
  id: string;
  name: string;
}

interface CourseStats {
  sectionsTotal: string;
  teamsTotal: string;
  studentsTotal: string;
}

interface CourseDetailsBundle {
  course: CourseAttributes;
  stats: CourseStats;
}

interface InstructorAttributes {
  googleId: string;
  name: string;
  email: string;
  displayedName: string;
  isArchived: boolean;
  isDisplayedToStudents: boolean;
}

interface CourseInfo {
  courseDetails: CourseDetailsBundle;
  currentInstructor: InstructorAttributes;
  instructors: InstructorAttributes[];
  studentListHtmlTableAsString: string;
  sections: StudentListSectionData[];
  hasSection: boolean;
}

/**
 * Instructor course details page.
 */
@Component({
  selector: 'tm-instructor-course-details-page',
  templateUrl: './instructor-course-details-page.component.html',
  styleUrls: ['./instructor-course-details-page.component.scss'],
})
export class InstructorCourseDetailsPageComponent implements OnInit {

  user: string = '';
  courseDetails? : CourseDetailsBundle;
  currentInstructor? : InstructorAttributes;
  instructors? : InstructorAttributes[];
  sections? : StudentListSectionData[];

  private backendUrl: string = environment.backendUrl;

  constructor(private route: ActivatedRoute, private httpRequestService: HttpRequestService,
              private statusMessageService: StatusMessageService, private ngbModal: NgbModal) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.user = queryParams.user;
      this.loadCourseDetails(queryParams.courseid);
    });
  }
  /**
   * Loads the course's details based on the given course ID and email.
   */
  loadCourseDetails(courseid: string): void {
    const paramMap: { [key: string]: string } = { courseid };
    this.httpRequestService.get('/courses/details', paramMap).subscribe((resp: CourseInfo) => {
      this.courseDetails = resp.courseDetails;
      this.currentInstructor = resp.currentInstructor;
      this.instructors = resp.instructors;
      this.sections = resp.sections;

      if (!this.courseDetails) {
        this.statusMessageService.showErrorMessage('Error retrieving course details');
      }
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
  }

}
