import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { saveAs } from 'file-saver';
import { generate } from 'rxjs';
import { concatMap, finalize, takeWhile } from 'rxjs/operators';
import { CourseService, CourseStatistics } from '../../../services/course.service';
import { InstructorService } from '../../../services/instructor.service';
import { ProgressBarService } from '../../../services/progress-bar.service';
import { SimpleModalService } from '../../../services/simple-modal.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentService } from '../../../services/student.service';
import { TableComparatorService } from '../../../services/table-comparator.service';
import {
  Course,
  Instructor,
  InstructorPermissionSet,
  InstructorPrivilege,
  Instructors,
  MessageOutput,
  Student,
  Students,
} from '../../../types/api-output';
import { Intent } from '../../../types/api-request';
import { SortBy, SortOrder } from '../../../types/sort-properties';
import { SimpleModalType } from '../../components/simple-modal/simple-modal-type';
import { JoinStatePipe } from '../../components/student-list/join-state.pipe';
import { StudentListRowModel } from '../../components/student-list/student-list.component';
import { ErrorMessageOutput } from '../../error-message-output';

interface CourseDetailsBundle {
  course: Course;
  stats: CourseStatistics;
}

interface StudentIndexedData {
  [key: string]: Student[];
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

  courseDetails: CourseDetailsBundle = {
    course: {
      courseId: '',
      courseName: '',
      institute: '',
      timeZone: '',
      creationTimestamp: 0,
      deletionTimestamp: 0,
    },
    stats: {
      numOfSections: 0,
      numOfTeams: 0,
      numOfStudents: 0,
    },
  };
  instructors: Instructor[] = [];
  students: StudentListRowModel[] = [];

  isLoadingCsv: boolean = false;
  isStudentsLoading: boolean = false;
  sectionsLoaded: number = 0;
  hasLoadingStudentsFailed: boolean = false;
  isDeleting: boolean = false;

  studentSortBy: SortBy = SortBy.NONE;
  studentSortOrder: SortOrder = SortOrder.ASC;

  constructor(private route: ActivatedRoute,
              private statusMessageService: StatusMessageService,
              private progressBarService: ProgressBarService,
              private courseService: CourseService,
              private ngbModal: NgbModal,
              private simpleModalService: SimpleModalService,
              private studentService: StudentService,
              private instructorService: InstructorService,
              private tableComparatorService: TableComparatorService) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.loadCourseDetails(queryParams.courseid);
    });
  }

  /**
   * Loads the course's details based on the given course ID.
   */
  loadCourseDetails(courseid: string): void {
    this.loadCourseName(courseid);
    this.loadInstructors(courseid);
    this.loadStudents(courseid);
  }

  /**
   * Loads the name of the course
   */
  private loadCourseName(courseid: string): void {
    this.courseService.getCourseAsInstructor(courseid).subscribe({
      next: (course: Course) => {
        this.courseDetails.course = course;
      },
      error: (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(resp.error.message);
      },
    });
  }

  /**
   * Loads the instructors in the course
   */
  private loadInstructors(courseid: string): void {
    this.instructorService.loadInstructors({ courseId: courseid, intent: Intent.FULL_DETAIL })
    .subscribe({
      next: (instructors: Instructors) => {
        this.instructors = instructors.instructors;
      },
      error: (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(resp.error.message);
      },
    });
  }

  /**
   * Loads the students in the course
   */
  loadStudents(courseid: string): void {
    this.hasLoadingStudentsFailed = false;
    this.isStudentsLoading = true;
    this.studentService.getStudentsFromCourse({ courseId: courseid })
    .subscribe({
      next: (students: Students) => {
        this.students = []; // Reset the list of students
        this.sectionsLoaded = 0; // Reset sections loaded
        const sections: StudentIndexedData = students.students.reduce((acc: StudentIndexedData, x: Student) => {
          const term: string = x.sectionName;
          (acc[term] = acc[term] || []).push(x);
          return acc;
        }, {});
        const sectionLength: number = Object.keys(sections).length;

        this.instructorService.loadInstructorPrivilege({
          courseId: courseid,
        }).subscribe({
          next: (instructorPrivilege: InstructorPrivilege) => {
            const courseLevelPrivilege: InstructorPermissionSet = instructorPrivilege.privileges.courseLevel;

            Object.keys(sections).forEach((sectionName: string) => {
              const sectionLevelPrivilege: InstructorPermissionSet =
                  instructorPrivilege.privileges.sectionLevel[sectionName] || courseLevelPrivilege;

              const studentsInSection: Student[] = sections[sectionName];
              const studentModels: StudentListRowModel[] = studentsInSection.map((studentInSection: Student) => {
                return {
                  student: studentInSection,
                  isAllowedToViewStudentInSection: sectionLevelPrivilege.canViewStudentInSections,
                  isAllowedToModifyStudent: sectionLevelPrivilege.canModifyStudent,
                };
              });

              this.students.push(...studentModels);
              this.students.sort(this.sortStudentBy(SortBy.NONE, SortOrder.ASC));

              this.sectionsLoaded += 1;
              if (this.sectionsLoaded === sectionLength) {
                this.isStudentsLoading = false;
              }
            });
          },
          error: (resp: ErrorMessageOutput) => {
            this.isStudentsLoading = false;
            this.hasLoadingStudentsFailed = true;
            this.statusMessageService.showErrorToast(resp.error.message);
          },
        });

        if (!sectionLength) {
          this.isStudentsLoading = false;
        }

        this.courseDetails.stats = this.courseService.calculateCourseStatistics(students.students);

      },
      error: (resp: ErrorMessageOutput) => {
        this.isStudentsLoading = false;
        this.hasLoadingStudentsFailed = true;
        this.statusMessageService.showErrorToast(resp.error.message);
      },
    });
  }

  /**
   * Open the modal for different buttons and link.
   */
  openModal(content: any): void {
    this.ngbModal.open(content);
  }

  openRemindStudentModal(): void {
    const modalContent: string = `Usually, there is no need to use this feature because
      TEAMMATES sends an automatic invite to students at the opening time of each session.
      Send a join request to all yet-to-join students in ${this.courseDetails.course.courseId} anyway?`;
    this.simpleModalService.openConfirmationModal(
        'Sending join requests?', SimpleModalType.INFO, modalContent).result.then(() => {
          this.remindAllStudentsFromCourse(this.courseDetails.course.courseId);
        }, () => {});
  }

  openDeleteAllStudentsConfirmationModal(): void {
    const modalContent = `Are you sure you want to remove all students from the course 
        ${this.courseDetails.course.courseId}? This will also delete their feedback responses and comments.`;
    this.simpleModalService.openConfirmationModal(
        'Delete all students?', SimpleModalType.DANGER, modalContent).result.then(() => {
          this.deleteAllStudentsFromCourse(this.courseDetails.course.courseId);
        }, () => {});
  }

  /**
   * Delete all the students in a course.
   */
  deleteAllStudentsFromCourse(courseId: string): void {
    this.isDeleting = true;

    const totalNumberOfStudents = this.courseDetails.stats.numOfStudents;
    const numOfStudentsToDeletePerRequest = 100;
    const totalNumOfRequests = Math.ceil(totalNumberOfStudents / numOfStudentsToDeletePerRequest);
    let numOfRequestsCompleted = 0;

    let deleteAborted = false;
    let hasFailedToDelete = false;
    const modalContent = `Deleting all students from the course ${this.courseDetails.course.courseId}, 
        this may take a while...`;
    const loadingModal: NgbModalRef = this.simpleModalService.openLoadingModal(
        'Delete Progress', SimpleModalType.LOAD, modalContent);

    loadingModal.result.then(() => {
      // Modal is closed
      this.isDeleting = false;

      if (numOfRequestsCompleted === totalNumOfRequests) {
        this.statusMessageService.showSuccessToast('All the students have been removed from the course');
      }

      if (!hasFailedToDelete && numOfRequestsCompleted < totalNumOfRequests) {
        deleteAborted = true;
        this.statusMessageService.showWarningToast('Delete aborted. '
            + 'Note that some students may not have been deleted.');
      }
    }, () => {});

    generate({
      initialState: 0,
      condition: (x) => x < totalNumOfRequests,
      iterate: (x) => x + 1,
    }).pipe(
        concatMap(() => this.studentService.batchDeleteStudentsFromCourse(
            { courseId, limit: numOfStudentsToDeletePerRequest })),
        takeWhile(() => !deleteAborted),
        finalize(() => {
          loadingModal.close();
        }),
    ).subscribe({
      next: () => {
        numOfRequestsCompleted += 1;
        const percentageProgress = Math.round(100 * numOfRequestsCompleted / totalNumOfRequests);
        this.progressBarService.updateProgress(percentageProgress);
      },
      complete: () => {
        if (deleteAborted) {
          this.loadStudents(courseId);
          return;
        }

        // Reset list of students and course stats
        this.students = [];
        this.courseDetails.stats = {
          numOfStudents: 0,
          numOfSections: 0,
          numOfTeams: 0,
        };
      },
      error: (resp: ErrorMessageOutput) => {
        hasFailedToDelete = true;
        if (!deleteAborted) {
          this.statusMessageService.showErrorToast(resp.error.message);
        }
      },
    },
    );
  }

  /**
   * Download all the students from a course.
   */
  downloadAllStudentsFromCourse(courseId: string): void {
    this.isLoadingCsv = true;
    const filename: string = `${courseId.concat('_studentList')}.csv`;
    let blob: any;

    this.studentService.loadStudentListAsCsv({ courseId })
      .pipe(finalize(() => {
        this.isLoadingCsv = false;
      }))
      .subscribe({
        next: (resp: string) => {
          blob = new Blob([resp], { type: 'text/csv' });
          saveAs(blob, filename);
        },
        error: (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      });
  }

  /**
   * Remind all yet to join students in a course.
   */
  remindAllStudentsFromCourse(courseId: string): void {
    this.courseService.remindUnregisteredStudentsForJoin(courseId).subscribe({
      next: (resp: MessageOutput) => {
        this.statusMessageService.showSuccessToast(resp.message);
      },
      error: (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(resp.error.message);
      },
    });
  }

  /**
   * Obtain a string without quotations.
   */
  getTableData(line: string): string[] {
    const output: string[] = [];
    let inquote: boolean = false;

    let buffer: string = '';
    const data: string[] = line.split('');

    for (let i: number = 0; i < data.length; i += 1) {
      if (data[i] === '"') {
        if (i + 1 < data.length && data[i + 1] === '"') {
          i += 1;
        } else {
          inquote = !inquote;
          continue;
        }
      }

      if (data[i] === ',') {
        if (inquote) {
          buffer = buffer.concat(data[i]);
        } else {
          output.push(buffer);
          buffer = '';
        }
      } else {
        buffer = buffer.concat(data[i]);
      }
    }
    output.push(buffer.trim());
    return output;
  }

  /**
   * Removes the student from course and update the course statistics
   */
  removeStudentFromCourse(studentEmail: string): void {
    this.courseService.removeStudentFromCourse(this.courseDetails.course.courseId, studentEmail).subscribe({
      next: () => {
        this.students =
            this.students.filter((studentModel: StudentListRowModel) => studentModel.student.email !== studentEmail);

        const students: Student[] = this.students.map((studentModel: StudentListRowModel) => studentModel.student);
        this.courseDetails.stats = this.courseService.calculateCourseStatistics(students);

        this.statusMessageService
            .showSuccessToast(`Student is successfully deleted from course "${this.courseDetails.course.courseId}"`);
      },
      error: (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(resp.error.message);
      },
    });
  }

  /**
   * Sorts the student list.
   */
  sortStudentList(by: SortBy): void {
    this.studentSortBy = by;
    this.studentSortOrder =
      this.studentSortOrder === SortOrder.DESC ? SortOrder.ASC : SortOrder.DESC;
    this.students.sort(this.sortStudentBy(by, this.studentSortOrder));
  }

  /**
   * Returns a function to determine the order of sort for students.
   */
  sortStudentBy(by: SortBy, order: SortOrder):
    ((a: StudentListRowModel, b: StudentListRowModel) => number) {
    const joinStatePipe: JoinStatePipe = new JoinStatePipe();
    if (by === SortBy.NONE) {
      // Default order: section name > team name > student name
      return ((a: StudentListRowModel, b: StudentListRowModel): number => {
        return this.tableComparatorService
            .compare(SortBy.SECTION_NAME, order, a.student.sectionName, b.student.sectionName)
          || this.tableComparatorService.compare(SortBy.TEAM_NAME, order, a.student.teamName, b.student.teamName)
          || this.tableComparatorService.compare(SortBy.RESPONDENT_NAME, order, a.student.name, b.student.name);
      });
    }
    return (a: StudentListRowModel, b: StudentListRowModel): number => {
      let strA: string;
      let strB: string;
      switch (by) {
        case SortBy.SECTION_NAME:
          strA = a.student.sectionName;
          strB = b.student.sectionName;
          break;
        case SortBy.RESPONDENT_NAME:
          strA = a.student.name;
          strB = b.student.name;
          break;
        case SortBy.TEAM_NAME:
          strA = a.student.teamName;
          strB = b.student.teamName;
          break;
        case SortBy.RESPONDENT_EMAIL:
          strA = a.student.email;
          strB = b.student.email;
          break;
        case SortBy.JOIN_STATUS:
          strA = joinStatePipe.transform(a.student.joinState);
          strB = joinStatePipe.transform(b.student.joinState);
          break;
        default:
          strA = '';
          strB = '';
      }

      return this.tableComparatorService.compare(by, order, strA, strB);
    };
  }
}
