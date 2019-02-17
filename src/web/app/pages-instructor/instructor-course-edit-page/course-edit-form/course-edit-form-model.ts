/**
 * The form model of course details edit form.
 */
export interface CourseEditFormModel {
  courseId: string;
  courseName: string;
  timeZone: string;

  isEditable: boolean;
  isSaving: boolean;
}
