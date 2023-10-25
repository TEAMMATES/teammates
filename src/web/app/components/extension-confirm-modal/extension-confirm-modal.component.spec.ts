import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { TimezoneService } from '../../../services/timezone.service';
import {
  FeedbackSession, FeedbackSessionPublishStatus, FeedbackSessionSubmissionStatus,
} from '../../../types/api-output';
import { InstructorPermissionRole, ResponseVisibleSetting, SessionVisibleSetting } from '../../../types/api-request';
import { SortBy, SortOrder } from '../../../types/sort-properties';
import {
  InstructorExtensionTableColumnModel,
  StudentExtensionTableColumnModel,
} from '../../pages-instructor/instructor-session-individual-extension-page/extension-table-column-model';
import { ExtensionConfirmModalComponent, ExtensionModalType } from './extension-confirm-modal.component';
import { ExtensionConfirmModalModule } from './extension-confirm-modal.module';

describe('ExtensionConfirmModalComponent', () => {
  const testFeedbackSession: FeedbackSession = {
    courseId: 'testId1',
    timeZone: 'Asia/Singapore',
    feedbackSessionName: 'Test Session',
    instructions: 'Instructions',
    submissionStartTimestamp: 1000000000000,
    submissionEndTimestamp: 1500000000000,
    gracePeriod: 0,
    sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
    responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
    submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
    publishStatus: FeedbackSessionPublishStatus.PUBLISHED,
    isClosingEmailEnabled: true,
    isPublishedEmailEnabled: true,
    createdAtTimestamp: 0,
    studentDeadlines: {},
    instructorDeadlines: {},
  };

  const studentModel1: StudentExtensionTableColumnModel = {
    sectionName: 'Test Section 1',
    teamName: 'Test Section 1',
    name: 'Test Student 1',
    email: 'testStudent1@gmail.com',
    extensionDeadline: 1500000000000,
    hasExtension: false,
    isSelected: true,
  };
  const studentModel2: StudentExtensionTableColumnModel = {
    sectionName: 'Test Section 2',
    teamName: 'Test Section 2',
    name: 'Test Student 2',
    email: 'testStudent2@gmail.com',
    extensionDeadline: 1510000000000,
    hasExtension: true,
    isSelected: true,
  };
  const studentModel3: StudentExtensionTableColumnModel = {
    sectionName: 'Test Section 3',
    teamName: 'Test Section 3',
    name: 'Test Student 3',
    email: 'testStudent3@gmail.com',
    extensionDeadline: 1520000000000,
    hasExtension: true,
    isSelected: true,
  };

  const instructorModel1: InstructorExtensionTableColumnModel = {
    name: 'Test InstructorTutor 1',
    email: 'testInstructorTutor1@gmail.com',
    role: InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_TUTOR,
    extensionDeadline: 1000000000,
    hasExtension: true,
    isSelected: false,
  };

  const instructorModel2: InstructorExtensionTableColumnModel = {
    name: 'Test Instructor 2',
    email: 'testInstructor2@gmail.com',
    extensionDeadline: 1100000000,
    hasExtension: true,
    isSelected: false,
  };

  const instructorModel3: InstructorExtensionTableColumnModel = {
    name: 'Test InstructorManager 3',
    email: 'testInstructorManager3@gmail.com',
    role: InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_MANAGER,
    extensionDeadline: 1200000000,
    hasExtension: true,
    isSelected: true,
  };

  const instructorModel4: InstructorExtensionTableColumnModel = {
    name: 'Test Instructor 4',
    email: 'testInstructor4@gmail.com',
    extensionDeadline: 1300000000,
    hasExtension: true,
    isSelected: true,
  };

  const testTimeString = '5 Apr 2000 2:00:00';

  let component: ExtensionConfirmModalComponent;
  let fixture: ComponentFixture<ExtensionConfirmModalComponent>;
  let timeZoneService: TimezoneService;
  let sortBy: SortBy;
  let sortOrder: SortOrder;
  let students: StudentExtensionTableColumnModel[];
  let instructors: InstructorExtensionTableColumnModel[];
  let sortedStudents: StudentExtensionTableColumnModel[];
  let sortedInstructors: InstructorExtensionTableColumnModel[];

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule, ExtensionConfirmModalModule],
        providers: [NgbActiveModal],
      }).compileComponents();
    }),
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(ExtensionConfirmModalComponent);
    timeZoneService = TestBed.inject(TimezoneService);
    jest.spyOn(timeZoneService, 'formatToString').mockReturnValue(testTimeString);
    component = fixture.componentInstance;
    component.feedbackSessionTimeZone = 'Asia/Singapore';
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with the extended students and instructors', () => {
    component.studentData = [studentModel1, studentModel2, studentModel3];
    component.instructorData = [instructorModel2, instructorModel1, instructorModel3];
    component.extensionTimestamp = testFeedbackSession.submissionEndTimestamp;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('use ngOnInit to initialise', () => {
    component.selectedStudents = [studentModel3];
    component.selectedInstructors = [instructorModel3];
    component.ngOnInit();
    component.extensionTimestamp = testFeedbackSession.submissionEndTimestamp;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('test emit from onConfirm()', () => {
    const spy = jest.spyOn(component.confirmExtensionCallbackEvent, 'emit');
    component.onConfirm();
    expect(spy).toHaveBeenCalledTimes(1);
  });

  it('test sortBy for students', () => {
    sortBy = SortBy.SECTION_NAME;
    sortOrder = SortOrder.ASC;
    const event = { sortBy, sortOrder };

    const spy = jest.spyOn(component.sortStudentListEvent, 'emit');
    component.sortStudentColumnsByEventHandler(event);
    expect(spy).toHaveBeenCalledTimes(1);
  });

  it('test sortBy for instructors', () => {
    sortBy = SortBy.RESPONDENT_NAME;
    sortOrder = SortOrder.DESC;
    const event = { sortBy, sortOrder };

    const spy = jest.spyOn(component.sortInstructorListEvent, 'emit');
    component.sortInstructorsColumnsByEventHandler(event);
    expect(spy).toHaveBeenCalledTimes(1);
  });

  /**
   * Tests for getAriaSortStudent
   */
  it('test getAriaSortStudent return none', () => {
    sortBy = SortBy.TEAM_NAME;
    const value = component.getAriaSortStudent(sortBy);
    expect(value).toEqual('none');
  });

  it('test getAriaSortStudent return ascending', () => {
    sortBy = SortBy.SESSION_END_DATE;
    component.sortStudentOrder = SortOrder.ASC;
    const value = component.getAriaSortStudent(sortBy);
    expect(value).toEqual('ascending');
  });

  it('test getAriaSortStudent return descending', () => {
    sortBy = SortBy.SESSION_END_DATE;
    component.sortStudentOrder = SortOrder.DESC;
    const value = component.getAriaSortStudent(sortBy);
    expect(value).toEqual('descending');
  });

  /**
   * Tests for getAriaSortInstructor
   */
  it('test getAriaSortInstructor return none', () => {
    sortBy = SortBy.RESPONDENT_NAME;
    const value = component.getAriaSortInstructor(sortBy);
    expect(value).toEqual('none');
  });

  it('test getAriaSortInstructor return ascending', () => {
    sortBy = SortBy.SESSION_END_DATE;
    component.sortInstructorOrder = SortOrder.ASC;
    const value = component.getAriaSortInstructor(sortBy);
    expect(value).toEqual('ascending');
  });

  it('test getAriaSortInstructor return descending', () => {
    sortBy = SortBy.SESSION_END_DATE;
    component.sortInstructorOrder = SortOrder.DESC;
    const value = component.getAriaSortInstructor(sortBy);
    expect(value).toEqual('descending');
  });

  /**
   * Tests for sortStudentPanelBy
   */
  beforeEach(() => {
    students = [studentModel2, studentModel1, studentModel3];
    component.sortStudentOrder = SortOrder.ASC;
  });

  it('test sortStudentPanelsBy section_name', () => {
    sortBy = SortBy.SECTION_NAME;
    sortedStudents = students.sort(component.sortStudentPanelsBy(sortBy));
    expect(sortedStudents[0]).toEqual(studentModel1);
    expect(sortedStudents[1]).toEqual(studentModel2);
    expect(sortedStudents[2]).toEqual(studentModel3);
  });

  it('test sortStudentPanelsBy team_name', () => {
    sortBy = SortBy.TEAM_NAME;
    component.sortStudentOrder = SortOrder.DESC;
    sortedStudents = students.sort(component.sortStudentPanelsBy(sortBy));
    expect(sortedStudents[0]).toEqual(studentModel3);
    expect(sortedStudents[1]).toEqual(studentModel2);
    expect(sortedStudents[2]).toEqual(studentModel1);
  });

  it('test sortStudentPanelsBy respondent_name', () => {
    sortBy = SortBy.RESPONDENT_NAME;
    sortedStudents = students.sort(component.sortStudentPanelsBy(sortBy));
    expect(sortedStudents[0]).toEqual(studentModel1);
    expect(sortedStudents[1]).toEqual(studentModel2);
    expect(sortedStudents[2]).toEqual(studentModel3);
  });

  it('test sortStudentPanelsBy respondent_email', () => {
    sortBy = SortBy.RESPONDENT_EMAIL;
    component.sortStudentOrder = SortOrder.DESC;
    sortedStudents = students.sort(component.sortStudentPanelsBy(sortBy));
    expect(sortedStudents[0]).toEqual(studentModel3);
    expect(sortedStudents[1]).toEqual(studentModel2);
    expect(sortedStudents[2]).toEqual(studentModel1);
  });

  it('test sortStudentPanelsBy session_end_date', () => {
    sortBy = SortBy.SESSION_END_DATE;
    sortedStudents = students.sort(component.sortStudentPanelsBy(sortBy));
    expect(sortedStudents[0]).toEqual(studentModel1);
    expect(sortedStudents[1]).toEqual(studentModel2);
    expect(sortedStudents[2]).toEqual(studentModel3);
  });

  it('test sortStudentPanelsBy invalid sortBy', () => {
    sortBy = SortBy.SESSION_COMPLETION_STATUS;
    sortedStudents = students.sort(component.sortStudentPanelsBy(sortBy));
    expect(sortedStudents[0]).toEqual(studentModel2);
    expect(sortedStudents[1]).toEqual(studentModel1);
    expect(sortedStudents[2]).toEqual(studentModel3);
  });

  /**
   * Tests for sortInstructorPanelBy
   */
  beforeEach(() => {
    instructors = [instructorModel2, instructorModel4, instructorModel1, instructorModel3];
    component.sortInstructorOrder = SortOrder.DESC;
  });

  it('test sortInstructorPanelsBy respondent_name', () => {
    sortBy = SortBy.RESPONDENT_NAME;
    sortedInstructors = instructors.sort(component.sortInstructorPanelsBy(sortBy));
    expect(sortedInstructors[0]).toEqual(instructorModel1);
    expect(sortedInstructors[1]).toEqual(instructorModel3);
    expect(sortedInstructors[2]).toEqual(instructorModel4);
    expect(sortedInstructors[3]).toEqual(instructorModel2);
  });

  it('test sortInstructorPanelsBy respondent_email', () => {
    sortBy = SortBy.RESPONDENT_EMAIL;
    component.sortInstructorOrder = SortOrder.ASC;
    sortedInstructors = instructors.sort(component.sortInstructorPanelsBy(sortBy));
    expect(sortedInstructors[0]).toEqual(instructorModel2);
    expect(sortedInstructors[1]).toEqual(instructorModel4);
    expect(sortedInstructors[2]).toEqual(instructorModel3);
    expect(sortedInstructors[3]).toEqual(instructorModel1);
  });

  it('test sortInstructorPanelsBy instructor_permission_role', () => {
    sortBy = SortBy.INSTRUCTOR_PERMISSION_ROLE;
    sortedInstructors = instructors.sort(component.sortInstructorPanelsBy(sortBy));
    expect(sortedInstructors[0]).toEqual(instructorModel2);
    expect(sortedInstructors[1]).toEqual(instructorModel4);
    expect(sortedInstructors[2]).toEqual(instructorModel3);
    expect(sortedInstructors[3]).toEqual(instructorModel1);
  });

  it('test sortInstructorPanelsBy session_end_date', () => {
    sortBy = SortBy.SESSION_END_DATE;
    component.sortInstructorOrder = SortOrder.ASC;
    sortedInstructors = instructors.sort(component.sortInstructorPanelsBy(sortBy));
    expect(sortedInstructors[0]).toEqual(instructorModel1);
    expect(sortedInstructors[1]).toEqual(instructorModel2);
    expect(sortedInstructors[2]).toEqual(instructorModel3);
    expect(sortedInstructors[3]).toEqual(instructorModel4);
  });

  it('test sortInstructorPanelsBy invalid sortBy', () => {
    sortBy = SortBy.SESSION_COMPLETION_STATUS;
    sortedInstructors = instructors.sort(component.sortInstructorPanelsBy(sortBy));
    expect(sortedInstructors[0]).toEqual(instructorModel2);
    expect(sortedInstructors[1]).toEqual(instructorModel4);
    expect(sortedInstructors[2]).toEqual(instructorModel1);
    expect(sortedInstructors[3]).toEqual(instructorModel3);
  });

  /**
   * Test ExtentionModalType.DELETE branch
   */
  const studentModel4: StudentExtensionTableColumnModel = {
    sectionName: 'Test Section 4',
    teamName: 'Test Section 4',
    name: 'Test Student 4',
    email: 'testStudent4@gmail.com',
    extensionDeadline: 1530000000000,
    hasExtension: false,
    isSelected: true,
  };

  it('Test ExtentionModalType.DELETE branch', () => {
    component.modalType = ExtensionModalType.DELETE;
    component.studentData = [studentModel4];
    component.instructorData = [instructorModel4];
    component.extensionTimestamp = testFeedbackSession.submissionEndTimestamp;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  /**
   * Test ExtentionModalType.SESSION_DELETE branch
   */
  const instructorModel5: InstructorExtensionTableColumnModel = {
    name: 'Test InstructorCustom 5',
    email: 'testInstructorCustom5@gmail.com',
    role: InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_CUSTOM,
    extensionDeadline: 1250000000,
    hasExtension: true,
    isSelected: true,
  };

  const studentModel5: StudentExtensionTableColumnModel = {
    sectionName: 'Test Section 5',
    teamName: 'Test Section 5',
    name: 'Test Student 5',
    email: 'testStudent5@gmail.com',
    extensionDeadline: 1550000000000,
    hasExtension: true,
    isSelected: true,
  };

  it('Test ExtentionModalType.SESSION_DELETE branch', () => {
    component.modalType = ExtensionModalType.SESSION_DELETE;
    component.studentData = [studentModel5];
    component.instructorData = [instructorModel5];
    component.extensionTimestamp = testFeedbackSession.submissionEndTimestamp;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });
});
