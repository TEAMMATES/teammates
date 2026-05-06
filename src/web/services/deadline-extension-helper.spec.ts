import { DeadlineExtensionHelper } from './deadline-extension-helper';
import {
  InstructorExtensionTableColumnModel,
  StudentExtensionTableColumnModel,
} from '../app/pages-instructor/instructor-session-individual-extension-page/extension-table-column-model';
import { Instructor, JoinState, Student } from '../types/api-output';
import { InstructorPermissionRole } from '../types/api-request';

const timeNow = Date.now();
const fixedLengthOfTime = 1000000;

const ongoingExtension: Record<string, number> = { ongoingExtension1: timeNow + fixedLengthOfTime };
const notOngoingExtension1: Record<string, number> = { notOngoingExtension1: timeNow - fixedLengthOfTime };
const notOngoingExtension2: Record<string, number> = { notOngoingExtension2: timeNow };
const hasOngoingDeadlines: Record<string, number> = {
  ...ongoingExtension,
  ...notOngoingExtension1,
  ...notOngoingExtension2,
};
const hasNoOngoingDeadlines: Record<string, number> = {
  ...notOngoingExtension1,
  ...notOngoingExtension2,
};

const student1: Student = {
  userId: 'student1-id',
  email: 'student1Model@example.com',
  courseId: 'course1',
  name: 'student1Model',
  teamName: '1',
  sectionName: '1',
};
const student1Model: StudentExtensionTableColumnModel = {
  userId: 'student1-id',
  sectionName: '1',
  teamName: '1',
  name: 'student1Model',
  email: 'student1Model@example.com',
  extensionDeadline: 0,
  hasExtension: true,
  isSelected: false,
};
const student2: Student = {
  userId: 'student2-id',
  email: 'student2Model@example.com',
  courseId: 'course1',
  name: 'student2Model',
  teamName: '2',
  sectionName: '1',
};
const student2Model: StudentExtensionTableColumnModel = {
  userId: 'student2-id',
  sectionName: '1',
  teamName: '2',
  name: 'student2Model',
  email: 'student2Model@example.com',
  extensionDeadline: 0,
  hasExtension: true,
  isSelected: false,
};
const instructor1: Instructor = {
  userId: 'instructor1-id',
  courseId: '1',
  role: InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
  email: 'instructor1Model@example.com',
  name: 'instructor1Model',
  joinState: JoinState.JOINED,
};
const instructor1ModelWithExtension: InstructorExtensionTableColumnModel = {
  userId: 'instructor1-id',
  name: 'instructor1Model',
  email: 'instructor1Model@example.com',
  role: InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
  extensionDeadline: 3,
  hasExtension: true,
  isSelected: false,
};
const instructor2: Instructor = {
  userId: 'instructor2-id',
  courseId: '1',
  role: InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
  email: 'instructor2Model@example.com',
  name: 'instructor2Model',
  joinState: JoinState.JOINED,
};
const instructor2ModelWithExtension: InstructorExtensionTableColumnModel = {
  userId: 'instructor2-id',
  name: 'instructor2Model',
  email: 'instructor2Model@example.com',
  role: InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
  extensionDeadline: 3,
  hasExtension: true,
  isSelected: false,
};

describe('DeadlineExtensionHelper', () => {
  beforeEach(() => jest.useFakeTimers().setSystemTime(timeNow));

  it('should filter and set deadlines before given end time correctly', () => {
    expect(
      Object.keys(DeadlineExtensionHelper.getDeadlinesBeforeOrEqualToEndTime(hasOngoingDeadlines, timeNow)).length,
    ).toEqual(2);
    expect(
      Object.keys(DeadlineExtensionHelper.getDeadlinesBeforeOrEqualToEndTime(hasNoOngoingDeadlines, timeNow)).length,
    ).toEqual(2);
  });

  it('should map students correctly', () => {
    const student1Extension: Record<string, number> = { 'student1-id': 0 };
    const student2Extension: Record<string, number> = { 'student2-id': 0 };
    const studentModels = DeadlineExtensionHelper.mapStudentsToStudentModels(
      [student1, student2],
      { ...student1Extension, ...student2Extension },
      timeNow,
    );

    expect(studentModels).toEqual([student1Model, student2Model]);
  });

  it('should map instructors correctly', () => {
    const instructor1Extension: Record<string, number> = { 'instructor1-id': 3 };
    const instructor2Extension: Record<string, number> = { 'instructor2-id': 3 };
    const instructorModels = DeadlineExtensionHelper.mapInstructorsToInstructorModels(
      [instructor1, instructor2],
      { ...instructor1Extension, ...instructor2Extension },
      timeNow,
    );

    expect(instructorModels).toEqual([instructor1ModelWithExtension, instructor2ModelWithExtension]);
  });

  it('should get deadlines correctly after updating deadlines for creation', () => {
    const existingExtensionForStudentModel1: Record<string, number> = { 'student1-id': 0 };
    const existingExtensionForInstructorModel1: Record<string, number> = { 'instructor1-id': 0 };
    const deadlinesToCopyFrom = {
      ...hasOngoingDeadlines,
      ...existingExtensionForStudentModel1,
      ...existingExtensionForInstructorModel1,
    };

    const updatedDeadlines = DeadlineExtensionHelper.getUpdatedDeadlinesForCreation(
      [student1Model, student2Model],
      [instructor1ModelWithExtension, instructor2ModelWithExtension],
      deadlinesToCopyFrom,
      100,
    );
    expect(Object.keys(updatedDeadlines).length).toEqual(7);
    expect(updatedDeadlines['student1-id']).toEqual(100);
    expect(updatedDeadlines['student2-id']).toEqual(100);
    expect(updatedDeadlines['instructor1-id']).toEqual(100);
    expect(updatedDeadlines['instructor2-id']).toEqual(100);
  });

  it('should get deadlines correctly after updating deadlines for deletion', () => {
    const student1ModelExtension: Record<string, number> = { 'student1-id': 0 };
    const instructor1ModelExtension: Record<string, number> = { 'instructor1-id': 0 };
    const deadlinesToCopyFrom = { ...hasOngoingDeadlines, ...student1ModelExtension, ...instructor1ModelExtension };

    expect(
      Object.keys(
        DeadlineExtensionHelper.getUpdatedDeadlinesForDeletion(
          [student1Model, student2Model],
          [instructor1ModelWithExtension, instructor2ModelWithExtension],
          deadlinesToCopyFrom,
        ),
      ).length,
    ).toEqual(3);
  });
});
