import { Component, OnInit } from '@angular/core';
import { AbstractControl, FormArray, FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import moment from 'moment-timezone';

import { CourseService } from '../../../services/course.service';
import { HttpRequestService } from '../../../services/http-request.service';
import { NavigationService } from '../../../services/navigation.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { TimezoneService } from '../../../services/timezone.service';
import { Course, MessageOutput } from '../../../types/api-output';
import { InstructorCreateRequest } from '../../../types/api-request';
import { ErrorMessageOutput } from '../../error-message-output';

interface CourseAttributes {
  id: string;
  name: string;
  timeZone: string;
}

interface CourseLevelPrivileges {
  canmodifycourse: boolean;
  canmodifyinstructor: boolean;
  canmodifysession: boolean;
  canmodifystudent: boolean;
  canviewstudentinsection: boolean;
  canviewsessioninsection: boolean;
  cansubmitsessioninsection: boolean;
  canmodifysessioncommentinsection: boolean;
}

interface SectionLevelPrivileges {
  canviewstudentinsection: boolean;
  canviewsessioninsection: boolean;
  cansubmitsessioninsection: boolean;
  canmodifysessioncommentinsection: boolean;
}

interface SessionLevelPrivileges {
  canviewsessioninsection: boolean;
  cansubmitsessioninsection: boolean;
  canmodifysessioncommentinsection: boolean;
}

interface Privileges {
  courseLevel: CourseLevelPrivileges;

  // Maps a section name to section level privileges
  sectionLevel: { [section: string]: SectionLevelPrivileges };

  // Maps a section name to a map mapping the session name and session privileges
  sessionLevel: { [section: string]: { [session: string]: SessionLevelPrivileges } };
}

interface InstructorAttributes {
  googleId: string;
  name: string;
  email: string;
  role: string;
  isDisplayedToStudents: boolean;
  displayedName: string;
  privileges: Privileges;
}

interface InstructorPrivileges {
  coowner: Privileges;
  manager: Privileges;
  observer: Privileges;
  tutor: Privileges;
  custom: Privileges;
}

interface CourseEditDetails {
  courseToEdit: CourseAttributes;
  instructorList: InstructorAttributes[];
  instructor: InstructorAttributes;
  instructorToShowIndex: number;
  sectionNames: string[];
  feedbackNames: string[];
  instructorPrivileges: InstructorPrivileges;
}

/**
 * Instructor course edit page.
 */
@Component({
  selector: 'tm-instructor-course-edit-page',
  templateUrl: './instructor-course-edit-page.component.html',
  styleUrls: ['./instructor-course-edit-page.component.scss'],
})
export class InstructorCourseEditPageComponent implements OnInit {

  user: string = '';
  timezone: string = '';
  timezones: string[] = [];

  isEditingCourse: boolean = false;
  formEditCourse!: FormGroup;

  formEditInstructors!: FormGroup;
  formInstructors!: FormArray;

  courseToEdit!: CourseAttributes;
  instructorList: InstructorAttributes[] = [];
  instructor!: InstructorAttributes;
  instructorToShowIndex: number = -1;
  sectionNames: string[] = [];
  feedbackNames: string[] = [];
  instructorPrivileges!: InstructorPrivileges;

  isAddingInstructor: boolean = false;
  formAddInstructor!: FormGroup;

  constructor(private route: ActivatedRoute,
              private router: Router,
              private navigationService: NavigationService,
              private timezoneService: TimezoneService,
              private httpRequestService: HttpRequestService,
              private statusMessageService: StatusMessageService,
              private courseService: CourseService,
              private fb: FormBuilder,
              private ngbModal: NgbModal) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.user = queryParams.user;
      this.getCourseEditDetails(queryParams.courseid);
    });

    this.timezones = Object.keys(this.timezoneService.getTzOffsets());
    this.timezone = moment.tz.guess();
  }

  /**
   * Replaces the timezone value with the detected timezone.
   */
  detectTimezone(): void {
    this.formEditCourse.controls.timeZone.setValue(this.timezone);
  }

  /**
   * Gets the placeholder content for displayed name when it is not displayed to students.
   */
  getPlaceholderForDisplayedName(isDisplayed: boolean): string {
    return isDisplayed ? 'E.g.Co-lecturer, Teaching Assistant'
        : '(This instructor will NOT be displayed to students)';
  }

  /**
   * Gets details related to the specified course.
   */
  getCourseEditDetails(courseid: string): void {
    const paramMap: { [key: string]: string } = { courseid };
    this.httpRequestService.get('/instructors/course/details', paramMap)
        .subscribe((resp: CourseEditDetails) => {
          this.courseToEdit = resp.courseToEdit;
          this.instructorList = resp.instructorList;
          this.instructor = resp.instructor;
          this.instructorToShowIndex = resp.instructorToShowIndex;
          this.sectionNames = resp.sectionNames;
          this.feedbackNames = resp.feedbackNames;
          this.instructorPrivileges = resp.instructorPrivileges;

          this.initEditCourseForm();
          this.initEditInstructorsForm();
        }, (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorMessage(resp.error.message);
        });
  }

  /**
   * Initialises the instructor course edit form with fields from the backend.
   */
  private initEditCourseForm(): void {
    this.formEditCourse = this.fb.group({
      id: [{ value: this.courseToEdit.id, disabled: true }],
      name: [{ value: this.courseToEdit.name, disabled: true }],
      timeZone: [{ value: this.courseToEdit.timeZone, disabled: true }],
    });
  }

  /**
   * Initialises the details panels with data from the backend for all instructors.
   */
  private initEditInstructorsForm(): void {
    this.formEditInstructors = this.fb.group({ formInstructors: [] });

    const control: FormArray = this.fb.array([]);
    this.instructorList.forEach((instructor: InstructorAttributes, index: number) => {
      const instructorPrivileges: Privileges = instructor.privileges;
      const instructorEmail: string = instructor.email ? instructor.email : '';
      const instructorDisplayedName: string = instructor.isDisplayedToStudents ? instructor.displayedName : '';

      const instructorForm: FormGroup = this.fb.group({
        googleId: [{ value: instructor.googleId, disabled: true }],
        name: [{ value: instructor.name, disabled: true }],
        email: [{ value: instructorEmail, disabled: true }],
        isDisplayedToStudents: [{ value: instructor.isDisplayedToStudents, disabled: true }],
        displayedName: [{ value: instructorDisplayedName, disabled: true }],
        role: [{ value: instructor.role }],
        privileges: [{ value: instructorPrivileges }],
        tunePermissions: this.fb.group({
          permissionsForCourse: this.fb.group(instructor.privileges.courseLevel),
          tuneSectionGroupPermissions: this.fb.array([]),
        }),
      });

      // Listen for specific course value changes
      const courseLevel: FormGroup = (instructorForm.controls.tunePermissions as FormGroup)
          .controls.permissionsForCourse as FormGroup;

      courseLevel.controls.canviewsessioninsection.valueChanges.subscribe((isAbleToView: boolean) => {
        if (!isAbleToView) {
          courseLevel.controls.canmodifysessioncommentinsection.setValue(false);
        }
      });

      courseLevel.controls.canmodifysessioncommentinsection.valueChanges.subscribe((isAbleToModify: boolean) => {
        if (isAbleToModify) {
          courseLevel.controls.canviewsessioninsection.setValue(true);
        }
      });

      (instructorForm.controls.tunePermissions as FormGroup).controls.tuneSectionGroupPermissions =
          this.initSectionGroupPermissions(instructor);

      // Listen for changes to custom privileges
      const roleControl: (AbstractControl | null) = instructorForm.get('role');
      const permissionsControl: (FormGroup | null) = instructorForm.get('tunePermissions') as FormGroup;

      if (roleControl != null && permissionsControl != null) {
        roleControl.valueChanges.subscribe((selectedRole: string) => {
          const panelId: string = `tune-permissions-${index}`;
          const panel: (HTMLElement | null) = document.getElementById(panelId);

          if (selectedRole === 'Custom' && panel != null) {
            panel.style.display = 'block';
            permissionsControl.controls.permissionsForCourse.reset(this.instructorList[index].privileges.courseLevel);
            permissionsControl.controls.tuneSectionGroupPermissions =
                this.initSectionGroupPermissions(this.instructorList[index]);
          } else if (panel != null) {
            panel.style.display = 'none';
          }
        });
      }

      control.push(instructorForm);
    });

    this.formEditInstructors.controls.formInstructors = control;
  }

  /**
   * Initialises section permissions for section group panels.
   */
  private initSectionGroupPermissions(instructor: InstructorAttributes): FormArray {
    const tuneSectionGroupPermissions: FormArray = this.fb.array([]);

    // Initialise section level privileges for each section group
    Object.keys(instructor.privileges.sectionLevel).forEach((sectionName: string) => {
      const sectionPrivileges: { [key: string]: SectionLevelPrivileges } = instructor.privileges.sectionLevel;
      const sectionPrivilegesForSection: SectionLevelPrivileges = sectionPrivileges[sectionName];
      const specialSectionPermissions: FormGroup = this.fb.group({
        permissionsForSection: this.fb.group(sectionPrivilegesForSection),
        permissionsForSessions: this.fb.group({}),
      });

      specialSectionPermissions.addControl(sectionName, this.fb.control(true));

      // Initialise remaining controls for non-special sections
      this.sectionNames.forEach((section: string) => {
        if (section !== sectionName) {
          specialSectionPermissions.addControl(section, this.fb.control(false));
        }
      });

      // Listen for specific section value changes
      const sectionLevel: FormGroup = specialSectionPermissions.controls.permissionsForSection as FormGroup;

      sectionLevel.controls.canviewsessioninsection.valueChanges.subscribe((isAbleToView: boolean) => {
        if (!isAbleToView) {
          sectionLevel.controls.canmodifysessioncommentinsection.setValue(false);
        }
      });

      sectionLevel.controls.canmodifysessioncommentinsection.valueChanges.subscribe((isAbleToModify: boolean) => {
        if (isAbleToModify) {
          sectionLevel.controls.canviewsessioninsection.setValue(true);
        }
      });

      // Initialise session level privileges for each section
      const sessionPrivilegesForSection: { [session: string]: SessionLevelPrivileges } =
          instructor.privileges.sessionLevel[sectionName];

      this.feedbackNames.forEach((feedback: string) => {
        let sessionPrivileges: FormGroup;
        if (sessionPrivilegesForSection != null && sessionPrivilegesForSection[feedback] != null) {
          sessionPrivileges = this.fb.group(sessionPrivilegesForSection[feedback]);
        } else {
          sessionPrivileges = this.fb.group({
            cansubmitsessioninsection: false,
            canviewsessioninsection: false,
            canmodifysessioncommentinsection: false,
          });
        }

        // Listen for specific session value changes
        sessionPrivileges.controls.canviewsessioninsection.valueChanges.subscribe((isAbleToSubmit: boolean) => {
          if (!isAbleToSubmit) {
            sessionPrivileges.controls.canmodifysessioncommentinsection.setValue(false);
          }
        });

        sessionPrivileges.controls.canmodifysessioncommentinsection.valueChanges
            .subscribe((isAbleToModify: boolean) => {
              if (isAbleToModify) {
                sessionPrivileges.controls.canviewsessioninsection.setValue(true);
              }
            });

        (specialSectionPermissions.controls.permissionsForSessions as FormGroup)
            .addControl(feedback, sessionPrivileges);
      });

      tuneSectionGroupPermissions.push(specialSectionPermissions);
    });

    return tuneSectionGroupPermissions;
  }

  /**
   * Toggles the edit course form depending on whether the edit button is clicked.
   */
  toggleIsEditingCourse(): void {
    this.isEditingCourse = !this.isEditingCourse;

    if (!this.isEditingCourse) {
      this.formEditCourse.controls.name.disable();
      this.formEditCourse.controls.timeZone.disable();
    } else {
      this.formEditCourse.controls.name.enable();
      this.formEditCourse.controls.timeZone.enable();
    }
  }

  /**
   * Deletes the current course and redirects to 'Courses' page if action is successful.
   */
  deleteCourse(): void {
    this.courseService.binCourse(this.courseToEdit.id).subscribe((course: Course) => {
      this.navigationService.navigateWithSuccessMessage(this.router, '/web/instructor/courses',
        `The course ${course.courseId} has been deleted. You can restore it from the Recycle Bin manually.`);
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
  }

  /**
   * Saves the updated course details.
   */
  onSubmitEditCourse(formEditCourse: FormGroup): void {
    const newName: string = formEditCourse.controls.name.value;
    const newTimeZone: string = formEditCourse.controls.timeZone.value;

    this.courseService.updateCourse(this.courseToEdit.id, {
      courseName: newName,
      timeZone: newTimeZone,
    }).subscribe((course: Course) => {
      this.statusMessageService.showSuccessMessage(`Updated course [${course.courseId}] details: `
          + `Name: ${course.courseName}, Time zone: ${course.timeZone}`);
      this.updateCourseDetails(newName, newTimeZone);
      this.toggleIsEditingCourse();
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
  }

  /**
   * Updates the stored course attributes entity.
   */
  updateCourseDetails(editedCourseName: string, editedCourseTimezone: string): void {
    this.courseToEdit.name = editedCourseName;
    this.courseToEdit.timeZone = editedCourseTimezone;
  }

  /**
   * Checks if the current instructor has a valid google id.
   */
  hasGoogleId(index: number): boolean {
    const googleId: string = this.instructorList[index].googleId;
    return googleId != null && googleId !== '';
  }

  /**
   * Enables/disables editing the displayed instructor name if it is/is not displayed to other students.
   */
  onChangeIsDisplayedToStudents(evt: any, instr: FormGroup, index: number): void {
    const displayedNameControl: (AbstractControl | null) = instr.controls.displayedName;
    const nameDisplayId: string = `name-display-${index}`;
    const displayedNameField: (HTMLInputElement | null) = document.getElementById(nameDisplayId) as HTMLInputElement;

    const isDisplayedToStudents: boolean = evt.target.checked;
    if (displayedNameControl != null) {
      if (isDisplayedToStudents) {
        displayedNameControl.enable();
        displayedNameControl.setValue('Instructor');
        displayedNameField.placeholder = this.getPlaceholderForDisplayedName(true);
      } else {
        displayedNameControl.disable();
        displayedNameControl.setValue('');
        displayedNameField.placeholder = this.getPlaceholderForDisplayedName(false);
      }
    }
  }

  /**
   * Toggles the edit instructor panel for a given instructor.
   * Instructor email cannot be edited when editing a yet-to-join instructor.
   */
  toggleIsEditingInstructor(control: FormGroup, index: number): void {
    const editBtnId: string = `btn-edit-${index}`;
    const cancelBtnId: string = `btn-cancel-${index}`;
    const saveBtnId: string = `btn-save-${index}`;

    const editBtn: (HTMLElement | null) = document.getElementById(editBtnId);
    const cancelBtn: (HTMLElement | null) = document.getElementById(cancelBtnId);
    const saveBtn: (HTMLElement | null) = document.getElementById(saveBtnId);

    let isEditBtnVisible: boolean = true;
    if (editBtn != null) {
      isEditBtnVisible = editBtn.style.display === 'inline-block';
    }

    const viewRoleId: string = `role-view-${index}`;
    const editRoleId: string = `role-edit-${index}`;

    const viewRole: (HTMLElement | null) = document.getElementById(viewRoleId);
    const editRole: (HTMLElement | null) = document.getElementById(editRoleId);

    const idControl: (AbstractControl | null) = control.controls.googleId;
    const roleControl: (AbstractControl | null) = control.controls.role;
    const displayNameControl: (AbstractControl | null) = control.controls.displayedName;
    const nameDisplayId: string = `name-display-${index}`;
    const displayedNameField: (HTMLInputElement | null) = document.getElementById(nameDisplayId) as HTMLInputElement;

    const permissionsPanelId: string = `tune-permissions-${index}`;
    const permissionsPanel: (HTMLElement | null) = document.getElementById(permissionsPanelId);

    if (editBtn != null && cancelBtn != null && saveBtn != null && viewRole != null && editRole != null
        && idControl != null && roleControl != null && displayNameControl != null && displayedNameField != null
        && permissionsPanel != null) {

      // If the instructor is currently being edited
      if (isEditBtnVisible) {
        editBtn.style.display = 'none';
        cancelBtn.style.display = 'inline-block';
        saveBtn.style.display = 'inline-block';

        viewRole.style.display = 'none';
        editRole.style.display = 'block';

        // Enable all form control elements except for the google id and possibly the displayed name
        control.enable();
        idControl.disable();
        roleControl.setValue(this.instructorList[index].role);

        if (this.instructorList[index].role === 'Custom') {
          permissionsPanel.style.display = 'block';
        }

        if (!this.instructorList[index].isDisplayedToStudents) {
          displayNameControl.disable();
        }

      } else {
        editBtn.style.display = 'inline-block';
        cancelBtn.style.display = 'none';
        saveBtn.style.display = 'none';

        viewRole.style.display = 'inline-block';
        editRole.style.display = 'none';

        control.disable();
        control.reset(this.instructorList[index]);
        permissionsPanel.style.display = 'none';

        if (!this.instructorList[index].isDisplayedToStudents) {
          displayNameControl.setValue('');
          displayedNameField.placeholder = this.getPlaceholderForDisplayedName(false);
        }
      }
    }

    // Disable editing email for yet-to-join instructor
    const email: string = 'email';
    const emailControl: (AbstractControl | null) = control.get(email);
    if (emailControl != null && !this.hasGoogleId(index)) {
      emailControl.disable();
    }
  }

  /**
   * Saves the updated instructor details.
   */
  onSubmitEditInstructor(instr: FormGroup, index: number): void {

    // Make a copy of the edited instructor
    const editedInstructor: InstructorAttributes =  {
      googleId: instr.controls.googleId.value,
      name: instr.controls.name.value,
      email: instr.controls.email.value,
      role: instr.controls.role.value,
      isDisplayedToStudents: instr.controls.isDisplayedToStudents.value,
      displayedName: instr.controls.displayedName.value,
      privileges: this.getPrivilegesForRole(instr.controls.role.value),
    };

    const paramsMap: { [key: string]: string } = {
      courseid: this.courseToEdit.id,
    };

    const reqBody: InstructorCreateRequest = {
      id: editedInstructor.googleId,
      name: editedInstructor.name,
      email: editedInstructor.email,
      roleName: editedInstructor.role,
      displayName: editedInstructor.displayedName,
      isDisplayedToStudent: editedInstructor.isDisplayedToStudents,
    };

    if (instr.controls.role.value === 'Custom') {
      const tuneCoursePermissions: (FormGroup | null) = (instr.controls.tunePermissions as FormGroup)
          .controls.permissionsForCourse as FormGroup;

      // Append custom course level privileges
      Object.keys(tuneCoursePermissions.controls).forEach((permission: string) => {
        if (tuneCoursePermissions.controls[permission].value) {
          paramsMap[permission] = 'true';
        }
      });
      editedInstructor.privileges.courseLevel = tuneCoursePermissions.value;

      // Append custom section level privileges
      const tuneSectionGroupPermissions: (FormArray | null) = (instr.controls.tunePermissions as FormGroup)
          .controls.tuneSectionGroupPermissions as FormArray;

      const newSectionLevelPrivileges: { [key: string]: SectionLevelPrivileges } = {};
      const newSessionLevelPrivileges: { [section: string]: { [session: string]: SessionLevelPrivileges } } = {};

      tuneSectionGroupPermissions.controls.forEach((sectionGroupPermissions: AbstractControl, panelIdx: number) => {
        const specialSections: string[] = [];

        // Mark section as special if it has been checked in a section group
        this.sectionNames.forEach((section: string, sectionIdx: number) => {
          if ((sectionGroupPermissions as FormGroup).controls[section].value) {
            paramsMap[`issectiongroup${sectionIdx}set`] = 'true';
            paramsMap[`sectiongroup${panelIdx}section${sectionIdx}`] = section;
            specialSections.push(section);
          }
        });

        // Include section permissions for a section group
        const permissionsInSection: (FormGroup | null) = (sectionGroupPermissions as FormGroup)
            .controls.permissionsForSection as FormGroup;
        Object.keys(permissionsInSection.controls).forEach((permission: string) => {
          if (permissionsInSection.controls[permission].value) {
            paramsMap[`${permission}sectiongroup${panelIdx}`] = 'true';
          }
        });

        // Save new section level privileges
        specialSections.forEach((section: string) => {
          newSectionLevelPrivileges[section] = permissionsInSection.value;
        });

        // Append custom session level privileges
        const permissionsForSessions: (FormGroup | null) = (sectionGroupPermissions as FormGroup)
            .controls.permissionsForSessions as FormGroup;
        const specialSessionsAndSessionPermissions: { [session: string]: SessionLevelPrivileges } = {};

        // Mark session as special if a session has different permissions from the section permissions
        const sectionLevelSessionPrivileges: SessionLevelPrivileges = {
          canviewsessioninsection: permissionsInSection.controls.canviewsessioninsection.value,
          cansubmitsessioninsection: permissionsInSection.controls.cansubmitsessioninsection.value,
          canmodifysessioncommentinsection: permissionsInSection.controls.canmodifysessioncommentinsection.value,
        };

        this.feedbackNames.forEach((feedback: string) => {
          const permissionsForSession: (FormGroup | null) = permissionsForSessions.controls[feedback] as FormGroup;
          if (permissionsForSession.value !== sectionLevelSessionPrivileges) {
            Object.keys(permissionsForSession.controls).forEach((permission: string) => {
              if (permissionsForSession.controls[permission].value) {
                paramsMap[`${permission}sectiongroup${panelIdx}feedback${feedback}`] = 'true';
              }
            });
            specialSessionsAndSessionPermissions[feedback] = permissionsForSession.value;
          }
        });

        if (Object.keys(specialSessionsAndSessionPermissions).length > 0) {
          paramsMap[`issectiongroup${panelIdx}sessionsset`] = 'true';
        }

        // Save new section level privileges
        specialSections.forEach((section: string) => {
          newSessionLevelPrivileges[section] = specialSessionsAndSessionPermissions;
        });
      });

      editedInstructor.privileges.sectionLevel = newSectionLevelPrivileges;
      editedInstructor.privileges.sessionLevel = newSessionLevelPrivileges;
    }

    this.httpRequestService.put('/instructor', paramsMap, reqBody)
        .subscribe((resp: MessageOutput) => {
          this.statusMessageService.showSuccessMessage(resp.message);
          this.updateInstructorDetails(index, editedInstructor);
          this.toggleIsEditingInstructor(instr, index);
        }, (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorMessage(resp.error.message);
        });
  }

  /**
   * Updates the stored instructor and instructor list entities.
   */
  updateInstructorDetails(index: number, instr: InstructorAttributes): void {
    const newPrivileges: Privileges = instr.privileges;

    // Update the stored instructor
    if (this.instructorList.length === 1) {
      // If there is only one instructor, the instructor can modify instructors by default
      newPrivileges.courseLevel.canmodifyinstructor = true;
      this.instructor = instr;
    }

    // Update elements for privileges if needed
    if (this.instructor.googleId === instr.googleId) {
      this.updateElementsForPrivileges();
    }

    // Update the stored instructor list
    this.instructorList[index] = instr;
  }

  /**
   * Gets the privileges for a particular role.
   */
  private getPrivilegesForRole(role: string): Privileges {
    if (role === 'Co-owner') {
      return this.instructorPrivileges.coowner;
    }

    if (role === 'Manager') {
      return this.instructorPrivileges.manager;
    }

    if (role === 'Observer') {
      return this.instructorPrivileges.observer;
    }

    if (role === 'Tutor') {
      return this.instructorPrivileges.tutor;
    }

    return this.instructorPrivileges.custom;
  }

  /**
   * Updates elements and buttons related to the current instructor's privileges.
   */
  private updateElementsForPrivileges(): void {
    const courseBtns: HTMLCollectionOf<Element> = document.getElementsByClassName('btn-course');
    for (const courseBtn of courseBtns as any) {
      (courseBtn as HTMLInputElement).disabled = !this.instructor.privileges.courseLevel.canmodifycourse;
    }

    const instrBtns: HTMLCollectionOf<Element> = document.getElementsByClassName('btn-instr');
    for (const instrBtn of instrBtns as any) {
      (instrBtn as HTMLInputElement).disabled = !this.instructor.privileges.courseLevel.canmodifyinstructor;
    }
  }

  /**
   * Toggles the add instructor form.
   */
  toggleIsAddingInstructor(): void {
    this.isAddingInstructor = !this.isAddingInstructor;

    if (this.isAddingInstructor) {
      this.initAddInstructorForm();
    }
  }

  /**
   * Initialises a new form for adding an instructor to the current course.
   */
  private initAddInstructorForm(): void {
    this.formAddInstructor = this.fb.group({
      googleId: [''],
      name: [''],
      email: [''],
      isDisplayedToStudents: [{ value: true }],
      displayedName: ['Instructor'],
      role: ['Co-owner'],
      privileges: this.getPrivilegesForRole('Co-owner'),
      tunePermissions: this.fb.group({
        permissionsForCourse: this.fb.group({
          canmodifycourse: true,
          canmodifyinstructor: true,
          canmodifysession: true,
          canmodifystudent: true,
          canviewstudentinsection: true,
          canviewsessioninsection: true,
          cansubmitsessioninsection: true,
          canmodifysessioncommentinsection: true,
        }),
        tuneSectionGroupPermissions: this.fb.array([]),
      }),
    });

    // Listen for changes to custom privileges
    const roleControl: (AbstractControl | null) = this.formAddInstructor.get('role');
    const permissionsControl: (FormGroup | null) = this.formAddInstructor.get('tunePermissions') as FormGroup;

    if (roleControl != null && permissionsControl != null) {
      roleControl.valueChanges.subscribe((selectedRole: string) => {
        const panelId: string = `tune-permissions-${this.instructorList.length}`;
        const panel: (HTMLElement | null) = document.getElementById(panelId);

        if (selectedRole === 'Custom' && panel != null) {
          panel.style.display = 'block';
          permissionsControl.controls.permissionsForCourse.reset();
          permissionsControl.controls.tuneSectionGroupPermissions = this.fb.array([]);
        } else if (panel != null) {
          panel.style.display = 'none';
        }
      });
    }
  }

  /**
   * Adds a new instructor to the current course.
   */
  onSubmitAddInstructor(formAddInstructor: FormGroup): void {
    // Create a copy of the added instructor
    const addedInstructor: InstructorAttributes = {
      googleId: formAddInstructor.controls.googleId.value,
      name: formAddInstructor.controls.name.value,
      email: formAddInstructor.controls.email.value,
      role: formAddInstructor.controls.role.value,
      isDisplayedToStudents: formAddInstructor.controls.isDisplayedToStudents.value.value,
      displayedName: formAddInstructor.controls.displayedName.value,
      privileges: this.getPrivilegesForRole(formAddInstructor.controls.role.value),
    };

    const paramsMap: { [key: string]: string } = {
      courseid: this.courseToEdit.id,
    };

    const reqBody: InstructorCreateRequest = {
      id: addedInstructor.googleId,
      name: addedInstructor.name,
      email: addedInstructor.email,
      roleName: addedInstructor.role,
      displayName: addedInstructor.displayedName,
      isDisplayedToStudent: addedInstructor.isDisplayedToStudents != null,
    };

    if (formAddInstructor.controls.role.value === 'Custom') {
      const tuneCoursePermissions: (FormGroup | null) = (formAddInstructor.controls.tunePermissions as FormGroup)
          .controls.permissionsForCourse as FormGroup;

      // Append custom course level privileges
      Object.keys(tuneCoursePermissions.controls).forEach((permission: string) => {
        if (tuneCoursePermissions.controls[permission].value) {
          paramsMap[permission] = 'true';
        }
      });
      addedInstructor.privileges.courseLevel = tuneCoursePermissions.value;

      // Append custom section level privileges
      const tuneSectionGroupPermissions: (FormArray | null) = (formAddInstructor.controls.tunePermissions as FormGroup)
          .controls.tuneSectionGroupPermissions as FormArray;

      const newSectionLevelPrivileges: { [key: string]: SectionLevelPrivileges } = {};
      const newSessionLevelPrivileges: { [section: string]: { [session: string]: SessionLevelPrivileges } } = {};

      tuneSectionGroupPermissions.controls.forEach((sectionGroupPermissions: AbstractControl, panelIdx: number) => {
        const specialSections: string[] = [];

        // Mark section as special if it has been checked in a section group
        this.sectionNames.forEach((section: string, sectionIdx: number) => {
          if ((sectionGroupPermissions as FormGroup).controls[section].value) {
            paramsMap[`issectiongroup${sectionIdx}set`] = 'true';
            paramsMap[`sectiongroup${panelIdx}section${sectionIdx}`] = section;
            specialSections.push(section);
          }
        });

        // Include section permissions for a section group
        const permissionsInSection: (FormGroup | null) = (sectionGroupPermissions as FormGroup)
            .controls.permissionsForSection as FormGroup;
        Object.keys(permissionsInSection.controls).forEach((permission: string) => {
          if (permissionsInSection.controls[permission].value) {
            paramsMap[`${permission}sectiongroup${panelIdx}`] = 'true';
          }
        });

        // Save new section level privileges
        specialSections.forEach((section: string) => {
          newSectionLevelPrivileges[section] = permissionsInSection.value;
        });

        // Append custom session level privileges
        const permissionsForSessions: (FormGroup | null) = (sectionGroupPermissions as FormGroup)
            .controls.permissionsForSessions as FormGroup;
        const specialSessionsAndSessionPermissions: { [session: string]: SessionLevelPrivileges } = {};

        // Mark session as special if a session has different permissions from the section permissions
        const sectionLevelSessionPrivileges: SessionLevelPrivileges = {
          canviewsessioninsection: permissionsInSection.controls.canviewsessioninsection.value,
          cansubmitsessioninsection: permissionsInSection.controls.cansubmitsessioninsection.value,
          canmodifysessioncommentinsection: permissionsInSection.controls.canmodifysessioncommentinsection.value,
        };

        this.feedbackNames.forEach((feedback: string) => {
          const permissionsForSession: (FormGroup | null) = permissionsForSessions.controls[feedback] as FormGroup;
          if (permissionsForSession.value !== sectionLevelSessionPrivileges) {
            Object.keys(permissionsForSession.controls).forEach((permission: string) => {
              if (permissionsForSession.controls[permission].value) {
                paramsMap[`${permission}sectiongroup${panelIdx}feedback${feedback}`] = 'true';
              }
            });
            specialSessionsAndSessionPermissions[feedback] = permissionsForSession.value;
          }
        });

        if (Object.keys(specialSessionsAndSessionPermissions).length > 0) {
          paramsMap[`issectiongroup${panelIdx}sessionsset`] = 'true';
        }

        // Save new section level privileges
        specialSections.forEach((section: string) => {
          newSessionLevelPrivileges[section] = specialSessionsAndSessionPermissions;
        });
      });

      addedInstructor.privileges.sectionLevel = newSectionLevelPrivileges;
      addedInstructor.privileges.sessionLevel = newSessionLevelPrivileges;
    }

    this.httpRequestService.post('/instructor', paramsMap, reqBody)
        .subscribe((resp: MessageOutput) => {
          this.statusMessageService.showSuccessMessage(resp.message);
          this.addToInstructorList(addedInstructor);
        }, (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorMessage(resp.error.message);
        });

    this.toggleIsAddingInstructor();
  }

  /**
   * Updates the stored instructor list and forms.
   */
  private addToInstructorList(instructor: InstructorAttributes): void {
    this.instructorList.push(instructor);
    this.initEditInstructorsForm();
  }

  /**
   * Opens a modal to confirm deleting an instructor.
   */
  onSubmitDeleteInstructor(deleteInstructorModal: NgbModal, index: number): void {
    this.ngbModal.open(deleteInstructorModal);

    const instructorToDelete: InstructorAttributes = this.instructorList[index];
    const modalId: string = 'delete-instr-modal';
    const courseId: string = this.courseToEdit.id;
    let modalContent: string = '';

    const modal: (HTMLElement | null) = document.getElementById(modalId);

    // Display different text depending on who is being deleted
    if (instructorToDelete.googleId === this.instructor.googleId) {
      modalContent = 'Are you sure you want to delete your instructor role from the '
          + `course ${courseId}? You will not be able to access the course anymore.`;
    } else {
      modalContent = `Are you sure you want to delete the instructor ${instructorToDelete.name} from the course `
          + `${courseId}? He/she will not be able to access the course anymore.`;
    }

    if (modal != null) {
      modal.innerText = modalContent;
    }
  }

  /**
   * Deletes an instructor from the given course.
   */
  deleteInstructor(index: number): void {
    const instructorToDelete: InstructorAttributes = this.instructorList[index];
    const paramsMap: { [key: string]: string } = {
      courseid: this.courseToEdit.id,
      instructorid: this.instructor.googleId,
    };

    this.httpRequestService.delete('/instructor', paramsMap)
        .subscribe(() => {
          if (instructorToDelete.googleId === this.instructor.googleId) {
            this.navigationService.navigateWithSuccessMessage(
                    this.router, '/web/instructor/courses', 'Instructor is successfully deleted.');
          } else {
            this.removeFromInstructorList(index);
            this.statusMessageService.showSuccessMessage('Instructor is successfully deleted.');
          }
        }, (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorMessage(resp.error.message);
        });
  }

  /**
   * Removes a deleted instructor from the stored instructor lists.
   */
  private removeFromInstructorList(index: number): void {
    this.instructorList.splice(index, 1);
    (this.formEditInstructors.controls.formInstructors as FormArray).removeAt(index);
  }

  /**
   * Opens a modal to show the privileges for a given instructor.
   */
  viewInstructorRole(viewInstructorRoleModal: NgbModal, index: number): boolean {
    this.ngbModal.open(viewInstructorRoleModal);

    const instructorToView: InstructorAttributes =  this.instructorList[index];
    this.initViewInstructorRole(instructorToView.role, instructorToView.privileges.courseLevel);
    return false;
  }

  /**
   * Opens a modal to show the privileges for a given role.
   */
  viewRolePrivileges(viewInstructorRoleModal: NgbModal, role: string): boolean {
    this.ngbModal.open(viewInstructorRoleModal);

    const privileges: Privileges = this.getPrivilegesForRole(role);
    this.initViewInstructorRole(role, privileges.courseLevel);
    return false;
  }

  /**
   * Initialises the modal showing privileges for given privileges.
   */
  initViewInstructorRole(role: string, courseLevelPrivileges: CourseLevelPrivileges): void {
    const modalTitleId: string = 'role-title';
    const modifyCourseId: string = 'canmodifycourse';
    const modifyInstructorId: string = 'canmodifyinstructor';
    const modifySessionId: string = 'canmodifysession';
    const modifyStudentId: string = 'canmodifystudent';
    const viewStudentInSectionId: string = 'canviewstudentinsection';
    const submitSessionInSectionId: string = 'cansubmitsessioninsection';
    const viewSessionInSectionId: string = 'canviewsessioninsection';
    const modifySessionCommentInSectionId: string = 'canmodifysessioncommentinsection';

    const modalTitle: (HTMLElement | null) = document.getElementById(modalTitleId);
    const canModifyCourse: (HTMLInputElement | null) = document.getElementById(modifyCourseId) as HTMLInputElement;
    const canModifyInstructor: (HTMLInputElement | null) =
        document.getElementById(modifyInstructorId) as HTMLInputElement;
    const canModifySession: (HTMLInputElement | null) = document.getElementById(modifySessionId) as HTMLInputElement;
    const canModifyStudent: (HTMLInputElement | null) = document.getElementById(modifyStudentId) as HTMLInputElement;
    const canViewStudentInSection: (HTMLInputElement | null) =
        document.getElementById(viewStudentInSectionId) as HTMLInputElement;
    const canSubmitSessionInSection: (HTMLInputElement | null) =
        document.getElementById(submitSessionInSectionId) as HTMLInputElement;
    const canViewSessionInSection: (HTMLInputElement | null) =
        document.getElementById(viewSessionInSectionId) as HTMLInputElement;
    const canModifySessionCommentInSection: (HTMLInputElement | null) =
        document.getElementById(modifySessionCommentInSectionId) as HTMLInputElement;

    if (modalTitle != null && canModifyCourse != null && canModifyInstructor != null && canModifySession != null
        && canModifyStudent != null && canViewStudentInSection != null && canSubmitSessionInSection != null
        && canViewSessionInSection != null && canModifySessionCommentInSection != null) {

      modalTitle.innerText = `Permissions for ${role}`;

      canModifyCourse.checked = courseLevelPrivileges.canmodifycourse;
      canModifyInstructor.checked = courseLevelPrivileges.canmodifyinstructor;
      canModifySession.checked = courseLevelPrivileges.canmodifysession;
      canModifyStudent.checked = courseLevelPrivileges.canmodifystudent;
      canViewStudentInSection.checked = courseLevelPrivileges.canviewstudentinsection;
      canSubmitSessionInSection.checked = courseLevelPrivileges.cansubmitsessioninsection;
      canViewSessionInSection.checked = courseLevelPrivileges.canviewsessioninsection;
      canModifySessionCommentInSection.checked = courseLevelPrivileges.canmodifysessioncommentinsection;
    }
  }

  /**
   * Opens a modal to confirm resending an invitation email to an instructor.
   */
  onSubmitResendEmail(resendEmailModal: NgbModal, index: number): void {
    this.ngbModal.open(resendEmailModal);

    const instructorToResend: InstructorAttributes = this.instructorList[index];
    const modalId: string = 'resend-email-modal';
    const courseId: string = this.courseToEdit.id;

    const modal: (HTMLElement | null) = document.getElementById(modalId);
    if (modal != null) {
      modal.innerText = `Do you wish to re-send the invitation email to instructor ${instructorToResend.name} `
          + `from course ${courseId}?`;
    }
  }

  /**
   * Re-sends an invitation email to an instructor in the course.
   */
  resendReminderEmail(index: number): void {
    const instructorToResend: InstructorAttributes = this.instructorList[index];

    this.courseService.remindInstructorForJoin(this.courseToEdit.id, instructorToResend.email)
        .subscribe((resp: MessageOutput) => {
          this.statusMessageService.showSuccessMessage(resp.message);
        }, (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorMessage(resp.error.message);
        });
  }

  /**
   * Adds an additional panel to modify custom section privileges for a given instructor.
   */
  addTuneSectionGroupPermissionsPanel(instr: FormGroup, index: number): void {
    const instructor: InstructorAttributes = this.instructorList[index];
    const newSection: FormGroup = this.fb.group({
      permissionsForSection: this.fb.group({
        canviewstudentinsection: instructor.privileges.courseLevel.canviewstudentinsection,
        cansubmitsessioninsection: instructor.privileges.courseLevel.cansubmitsessioninsection,
        canviewsessioninsection: instructor.privileges.courseLevel.canviewsessioninsection,
        canmodifysessioncommentinsection: instructor.privileges.courseLevel.canmodifysessioncommentinsection,
      }),
      permissionsForSessions: this.fb.group({}),
    });

    this.sectionNames.forEach((sectionName: string) => {
      newSection.addControl(sectionName, this.fb.control(false));
    });

    const sectionPrivileges: FormGroup = newSection.controls.permissionsForSection as FormGroup;
    sectionPrivileges.controls.canviewsessioninsection.valueChanges.subscribe((isAbleToSubmit: boolean) => {
      if (!isAbleToSubmit) {
        sectionPrivileges.controls.canmodifysessioncommentinsection.setValue(false);
      }
    });

    sectionPrivileges.controls.canmodifysessioncommentinsection.valueChanges.subscribe((isAbleToModify: boolean) => {
      if (isAbleToModify) {
        sectionPrivileges.controls.canviewsessioninsection.setValue(true);
      }
    });

    this.feedbackNames.forEach((feedback: string) => {
      const defaultSessionPrivileges: FormGroup = this.fb.group({
        canviewsessioninsection: false,
        cansubmitsessioninsection: false,
        canmodifysessioncommentinsection: false,
      });

      defaultSessionPrivileges.controls.canviewsessioninsection.valueChanges.subscribe((isAbleToSubmit: boolean) => {
        if (!isAbleToSubmit) {
          defaultSessionPrivileges.controls.canmodifysessioncommentinsection.setValue(false);
        }
      });

      defaultSessionPrivileges.controls.canmodifysessioncommentinsection.valueChanges
          .subscribe((isAbleToModify: boolean) => {
            if (isAbleToModify) {
              defaultSessionPrivileges.controls.canviewsessioninsection.setValue(true);
            }
          });

      (newSection.controls.permissionsForSessions as FormGroup).addControl(feedback, defaultSessionPrivileges);
    });

    ((instr.controls.tunePermissions as FormGroup).controls.tuneSectionGroupPermissions as FormArray).push(newSection);
  }

  /**
   * Adds a default tune section group permission panel.
   */
  addEmptyTuneSectionGroupPermissionsPanel(instr: FormGroup): void {
    const newSection: FormGroup = this.fb.group({
      permissionsForSection: this.fb.group({
        canviewstudentinsection: false,
        cansubmitsessioninsection: false,
        canviewsessioninsection: false,
        canmodifysessioncommentinsection: false,
      }),
      permissionsForSessions: this.fb.group({}),
    });

    this.sectionNames.forEach((sectionName: string) => {
      newSection.addControl(sectionName, this.fb.control(false));
    });

    const sectionPrivileges: FormGroup = newSection.controls.permissionsForSection as FormGroup;
    sectionPrivileges.controls.canviewsessioninsection.valueChanges.subscribe((isAbleToSubmit: boolean) => {
      if (!isAbleToSubmit) {
        sectionPrivileges.controls.canmodifysessioncommentinsection.setValue(false);
      }
    });

    sectionPrivileges.controls.canmodifysessioncommentinsection.valueChanges.subscribe((isAbleToModify: boolean) => {
      if (isAbleToModify) {
        sectionPrivileges.controls.canviewsessioninsection.setValue(true);
      }
    });

    this.feedbackNames.forEach((feedback: string) => {
      const defaultSessionPrivileges: FormGroup = this.fb.group({
        canviewsessioninsection: false,
        cansubmitsessioninsection: false,
        canmodifysessioncommentinsection: false,
      });

      defaultSessionPrivileges.controls.canviewsessioninsection.valueChanges.subscribe((isAbleToSubmit: boolean) => {
        if (!isAbleToSubmit) {
          defaultSessionPrivileges.controls.canmodifysessioncommentinsection.setValue(false);
        }
      });

      defaultSessionPrivileges.controls.canmodifysessioncommentinsection.valueChanges
          .subscribe((isAbleToModify: boolean) => {
            if (isAbleToModify) {
              defaultSessionPrivileges.controls.canviewsessioninsection.setValue(true);
            }
          });

      (newSection.controls.permissionsForSessions as FormGroup).addControl(feedback, defaultSessionPrivileges);
    });

    ((instr.controls.tunePermissions as FormGroup).controls.tuneSectionGroupPermissions as FormArray).push(newSection);
  }

  /**
   * Removes a panel to modify custom section privileges for a given instructor.
   */
  removeTuneSectionGroupPermissionsPanel(instr: FormGroup, index: number): void {
    ((instr.controls.tunePermissions as FormGroup).controls.tuneSectionGroupPermissions as FormArray).removeAt(index);
  }

  /**
   * Hides session level permissions for a section panel.
   */
  hideSessionLevelPermissions(panelIdx: number, sectionIdx: number): void {
    const table: (HTMLElement | null) = document.getElementById(`tune-session-permissions-${panelIdx}-${sectionIdx}`);
    const hideLink: (HTMLElement | null) = document.getElementById(`hide-link-${panelIdx}-${sectionIdx}`);
    const showLink: (HTMLElement | null) = document.getElementById(`show-link-${panelIdx}-${sectionIdx}`);

    if (table != null && hideLink != null && showLink != null) {
      table.style.display = 'none';
      hideLink.style.display = 'none';
      showLink.style.display = 'block';
    }
  }

  /**
   * Shows session level permissions for a section panel.
   */
  showSessionLevelPermissions(panelIdx: number, sectionIdx: number): void {
    const table: (HTMLElement | null) = document.getElementById(`tune-session-permissions-${panelIdx}-${sectionIdx}`);
    const hideLink: (HTMLElement | null) = document.getElementById(`hide-link-${panelIdx}-${sectionIdx}`);
    const showLink: (HTMLElement | null) = document.getElementById(`show-link-${panelIdx}-${sectionIdx}`);

    if (table != null && hideLink != null && showLink != null) {
      table.style.display = 'block';
      hideLink.style.display = 'block';
      showLink.style.display = 'none';
    }
  }
}
