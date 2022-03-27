import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { first } from 'rxjs/operators';
import { InstructorData } from '../instructor-data';

import { NewInstructorDataRowComponent } from './new-instructor-data-row.component';

describe('NewInstructorDataRowComponent', () => {
  let component: NewInstructorDataRowComponent;
  let fixture: ComponentFixture<NewInstructorDataRowComponent>;
  let expectedInstructorData: InstructorData;
  let expectedIndex: number;
  let expectedIsAddDisabled: boolean;

  let addButtonDe: any;
  let addButtonEl: any;
  let editButtonDe: any;
  let editButtonEl: any;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule],
      declarations: [NewInstructorDataRowComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NewInstructorDataRowComponent);
    component = fixture.componentInstance;
    expectedInstructorData = {
      name: 'Instructor',
      email: 'instructor@instruct.or',
      institution: 'Institutional Institution of Institute',
      status: 'PENDING',
      isCurrentlyBeingEdited: false,
    };
    expectedIndex = 0;
    expectedIsAddDisabled = false;
    component.instructor = expectedInstructorData;
    component.index = expectedIndex;
    component.isAddDisabled = expectedIsAddDisabled;
    fixture.detectChanges();
    addButtonDe = fixture.debugElement
      .query(By.css(`#add-instructor-${expectedIndex}`));
    addButtonEl = addButtonDe.nativeElement;
    editButtonDe = fixture.debugElement
      .query(By.css(`#edit-instructor-${expectedIndex}`));
    editButtonEl = editButtonDe.nativeElement;
  });

  it('should create', () => {
    expect(component).toBeTruthy();

    expect(component.isBeingEdited).toBeFalsy();

    expect(component.editedInstructorName).toEqual(expectedInstructorData.name);
    expect(component.editedInstructorEmail).toEqual(expectedInstructorData.email);
    expect(component.editedInstructorInstitution).toEqual(expectedInstructorData.institution);
  });

  it('should display the instructor name received as input', () => {
    const displayedInstructorName: string = fixture.debugElement
      .query(By.css(`#instructor-${expectedIndex}-name`))
      .nativeElement.textContent;
    expect(displayedInstructorName).toEqual(expectedInstructorData.name);
  });

  it('should display the instructor email received as input', () => {
    const displayedInstructorEmail: string = fixture.debugElement
      .query(By.css(`#instructor-${expectedIndex}-email`))
      .nativeElement.textContent;
    expect(displayedInstructorEmail).toEqual(expectedInstructorData.email);
  });

  it('should display the instructor institution received as input', () => {
    const displayedInstructorInstitution: string = fixture.debugElement
      .query(By.css(`#instructor-${expectedIndex}-institution`))
      .nativeElement.textContent;
    expect(displayedInstructorInstitution).toEqual(expectedInstructorData.institution);
  });

  it('should have an add button that is not disabled when isAddDisabled is false', () => {
    expect(addButtonEl.disabled).toBeFalsy();
  });

  it('should have an add button that is disabled when isAddDisabled is true', () => {
    component.isAddDisabled = true;
    fixture.detectChanges();
    expect(addButtonEl.disabled).toBeTruthy();
  });

  it('should emit addInstructorEvent when adding', () => {
    let hasEmitted: boolean = false;
    component.addInstructorEvent
      .pipe(first())
      .subscribe(() => {
        hasEmitted = true;
      });

    addButtonDe.triggerEventHandler('click', null);
    expect(hasEmitted).toBeTruthy();
  });

  it('should emit removeInstructorEvent when removing', () => {
    let hasEmitted: boolean = false;
    component.removeInstructorEvent
      .pipe(first())
      .subscribe(() => {
        hasEmitted = true;
      });

    fixture.debugElement
      .query(By.css(`#remove-instructor-${expectedIndex}`))
      .triggerEventHandler('click', null);
    expect(hasEmitted).toBeTruthy();
  });

  it('should emit true via toggleEditModeEvent when entering edit mode', () => {
    let isInEditMode: boolean | undefined;
    component.toggleEditModeEvent
      .pipe(first())
      .subscribe((isBeingEdited: boolean) => {
        isInEditMode = isBeingEdited;
      });

    editButtonDe.triggerEventHandler('click', null);
    expect(isInEditMode).toBeTruthy();
  });

  it('should emit false via toggleEditModeEvent when confirming the edit', () => {
    let isInEditMode: boolean | undefined;
    component.toggleEditModeEvent
      .subscribe((isBeingEdited: boolean) => {
        isInEditMode = isBeingEdited;
      });

    editButtonDe.triggerEventHandler('click', null);
    fixture.detectChanges();

    fixture.debugElement
      .query(By.css(`#confirm-edit-instructor-${expectedIndex}`))
      .triggerEventHandler('click', null);
    expect(isInEditMode).toBeFalsy();
  });

  it('should emit false via toggleEditModeEvent when cancelling the edit', () => {
    let isInEditMode: boolean | undefined;
    component.toggleEditModeEvent
      .subscribe((isBeingEdited: boolean) => {
        isInEditMode = isBeingEdited;
      });

    editButtonDe.triggerEventHandler('click', null);
    fixture.detectChanges();

    fixture.debugElement
      .query(By.css(`#cancel-edit-instructor-${expectedIndex}`))
      .triggerEventHandler('click', null);
    expect(isInEditMode).toBeFalsy();
  });

  it('should emit showRegisteredInstructorModalEvent when info button clicked', () => {
    component.instructor = {
      name: 'Instructor',
      email: 'instructor@instruct.or',
      institution: 'Institutional Institution of Institute',
      status: 'FAIL',
      statusCode: 409,
      isCurrentlyBeingEdited: false,
    };
    fixture.detectChanges();

    let hasEmitted = false;
    component.showRegisteredInstructorModalEvent
      .pipe(first())
      .subscribe(() => {
        hasEmitted = true;
      });

    fixture.debugElement
      .query(By.css(`#instructor-${expectedIndex}-registered-info-button`))
      .triggerEventHandler('click', null);
    expect(hasEmitted).toBeTruthy();
  });

  it('should not display more info button if statusCode is not 409', () => {
    component.instructor = {
      name: 'Instructor',
      email: 'instructor@instruct.or',
      institution: 'Institutional Institution of Institute',
      status: 'FAIL',
      statusCode: 500,
      isCurrentlyBeingEdited: false,
    };
    fixture.detectChanges();

    const debugElement = fixture.debugElement
      .query(By.css(`#instructor-${expectedIndex}-registered-info-button`));
    expect(debugElement).toBeNull();
  });

  it('should display more info button if statusCode is 409', () => {
    component.instructor = {
      name: 'Instructor',
      email: 'instructor@instruct.or',
      institution: 'Institutional Institution of Institute',
      status: 'FAIL',
      statusCode: 409,
      isCurrentlyBeingEdited: false,
    };
    fixture.detectChanges();

    const debugElement = fixture.debugElement
      .query(By.css(`#instructor-${expectedIndex}-registered-info-button`));
    expect(debugElement).toBeTruthy();
  });

  it('should set isBeingEdited to true when editing starts', () => {
    editButtonEl.click();

    expect(component.isBeingEdited).toBeTruthy();
  });

  it('should set isBeingEdited to false when the edit is confirmed', () => {
    editButtonEl.click();
    fixture.detectChanges();

    const confirmButtonEl: any = fixture.debugElement
      .query(By.css(`#confirm-edit-instructor-${expectedIndex}`))
      .nativeElement;
    confirmButtonEl.click();

    expect(component.isBeingEdited).toBeFalsy();
  });

  it('should set isBeingEdited to false when the edit is cancelled', () => {
    editButtonEl.click();
    fixture.detectChanges();

    const cancelButtonEl: any = fixture.debugElement
      .query(By.css(`#cancel-edit-instructor-${expectedIndex}`))
      .nativeElement;
    cancelButtonEl.click();

    expect(component.isBeingEdited).toBeFalsy();
  });

  it('should initially have the unedited instructor details when editing starts', () => {
    editButtonEl.click();

    expect(component.editedInstructorName).toEqual(expectedInstructorData.name);
    expect(component.editedInstructorEmail).toEqual(expectedInstructorData.email);
    expect(component.editedInstructorInstitution).toEqual(expectedInstructorData.institution);
  });

  it('should update the instructor details when the edit is confirmed', () => {
    editButtonEl.click();
    fixture.detectChanges();

    component.editedInstructorName = 'Edited Name';
    component.editedInstructorEmail = 'Edited@ema.il';
    component.editedInstructorInstitution = 'Edited Institution';
    const confirmButtonEl: any = fixture.debugElement
      .query(By.css(`#confirm-edit-instructor-${expectedIndex}`))
      .nativeElement;
    confirmButtonEl.click();

    expect(component.instructor.name).toEqual('Edited Name');
    expect(component.instructor.email).toEqual('Edited@ema.il');
    expect(component.instructor.institution).toEqual('Edited Institution');
  });

  it('should not update the instructor details when the edit is cancelled', () => {
    editButtonEl.click();
    fixture.detectChanges();

    component.editedInstructorName = 'Edited Name';
    component.editedInstructorEmail = 'Edited@ema.il';
    component.editedInstructorInstitution = 'Edited Institution';
    const cancelButtonEl: any = fixture.debugElement
      .query(By.css(`#cancel-edit-instructor-${expectedIndex}`))
      .nativeElement;
    cancelButtonEl.click();

    expect(component.instructor.name).toEqual(expectedInstructorData.name);
    expect(component.instructor.email).toEqual(expectedInstructorData.email);
    expect(component.instructor.institution).toEqual(expectedInstructorData.institution);
  });

  it('should reset the edited instructor details when the edit is cancelled', () => {
    editButtonEl.click();
    fixture.detectChanges();

    component.editedInstructorName = 'Edited Name';
    component.editedInstructorEmail = 'Edited@ema.il';
    component.editedInstructorInstitution = 'Edited Institution';
    const cancelButtonEl: any = fixture.debugElement
      .query(By.css(`#cancel-edit-instructor-${expectedIndex}`))
      .nativeElement;
    cancelButtonEl.click();

    expect(component.editedInstructorName).toEqual(expectedInstructorData.name);
    expect(component.editedInstructorEmail).toEqual(expectedInstructorData.email);
    expect(component.editedInstructorInstitution).toEqual(expectedInstructorData.institution);
  });

  it('should initially have the original instructor details when editing starts after a cancellation', () => {
    editButtonEl.click();
    fixture.detectChanges();

    component.editedInstructorName = 'Edited Name';
    component.editedInstructorEmail = 'Edited@ema.il';
    component.editedInstructorInstitution = 'Edited Institution';
    const cancelButtonEl: any = fixture.debugElement
      .query(By.css(`#cancel-edit-instructor-${expectedIndex}`))
      .nativeElement;
    cancelButtonEl.click();
    fixture.detectChanges();

    editButtonEl.click();

    expect(component.editedInstructorName).toEqual(expectedInstructorData.name);
    expect(component.editedInstructorEmail).toEqual(expectedInstructorData.email);
    expect(component.editedInstructorInstitution).toEqual(expectedInstructorData.institution);
  });

  it('should snap with default view', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with start of edit', () => {
    editButtonEl.click();
    fixture.detectChanges();

    expect(fixture).toMatchSnapshot();
  });

  it('should snap with instructor details edited', () => {
    editButtonEl.click();
    fixture.detectChanges();

    component.editedInstructorName = 'Edited Name';
    component.editedInstructorEmail = 'Edited@ema.il';
    component.editedInstructorInstitution = 'Edited Institution';
    fixture.detectChanges();

    expect(fixture).toMatchSnapshot();
  });

  it('should snap with edit confirmed', () => {
    editButtonEl.click();
    fixture.detectChanges();

    component.editedInstructorName = 'Edited Name';
    component.editedInstructorEmail = 'Edited@ema.il';
    component.editedInstructorInstitution = 'Edited Institution';
    const confirmButtonEl: any = fixture.debugElement
      .query(By.css(`#confirm-edit-instructor-${expectedIndex}`))
      .nativeElement;
    confirmButtonEl.click();
    fixture.detectChanges();

    expect(fixture).toMatchSnapshot();
  });

  it('should snap with edit cancelled', () => {
    editButtonEl.click();
    fixture.detectChanges();

    component.editedInstructorName = 'Edited Name';
    component.editedInstructorEmail = 'Edited@ema.il';
    component.editedInstructorInstitution = 'Edited Institution';
    const cancelButtonEl: any = fixture.debugElement
      .query(By.css(`#cancel-edit-instructor-${expectedIndex}`))
      .nativeElement;
    cancelButtonEl.click();
    fixture.detectChanges();

    expect(fixture).toMatchSnapshot();
  });
});
