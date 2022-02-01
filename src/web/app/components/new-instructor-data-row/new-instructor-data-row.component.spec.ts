import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { first } from 'rxjs/operators';
import { InstructorData } from './instructor-data';

import { NewInstructorDataRowComponent } from './new-instructor-data-row.component';

describe('NewInstructorDataRowComponent', () => {
  let component: NewInstructorDataRowComponent;
  let fixture: ComponentFixture<NewInstructorDataRowComponent>;
  let expectedInstructorData: InstructorData;
  let expectedIndex: number;
  let expectedActiveRequests: number;

  let addButtonDe: any;
  let addButtonEl: any;
  let editButtonDe: any;

  beforeEach(async(() => {
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
    };
    expectedIndex = 0;
    expectedActiveRequests = 0;
    component.instructor = expectedInstructorData;
    component.index = expectedIndex;
    component.activeRequests = expectedActiveRequests;
    fixture.detectChanges();
    addButtonDe = fixture.debugElement
      .query(By.css(`#add-instructor-${expectedIndex}`));
    addButtonEl = addButtonDe.nativeElement;
    editButtonDe = fixture.debugElement
      .query(By.css(`#edit-instructor-${expectedIndex}`));
  });

  it('should create', () => {
    expect(component).toBeTruthy();
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

  it('should have an add button that is not disabled when there are zero active requests as input', () => {
    expect(addButtonEl.disabled).toBeFalsy();
  });

  it('should have an add button that is disabled when there are non-zero active requests as input', () => {
    component.activeRequests = 1;
    fixture.detectChanges();
    expect(addButtonEl.disabled).toBeTruthy();
  });

  it('should emit addInstructorEvent when adding', () => {
    let hasEmitted: boolean = false;
    component.addInstructorEvent
      .pipe(first())
      .subscribe(() => hasEmitted = true);

    addButtonDe.triggerEventHandler('click', null);
    expect(hasEmitted).toBeTruthy();
  });

  it('should emit removeInstructorEvent when removing', () => {
    let hasEmitted: boolean = false;
    component.removeInstructorEvent
      .pipe(first())
      .subscribe(() => hasEmitted = true);

    fixture.debugElement
      .query(By.css(`#remove-instructor-${expectedIndex}`))
      .triggerEventHandler('click', null);
    expect(hasEmitted).toBeTruthy();
  });

  it('should emit true via toggleEditModeEvent when entering edit mode', () => {
    let isInEditMode: boolean | undefined;
    component.toggleEditModeEvent
      .pipe(first())
      .subscribe((isBeingEdited: boolean) => isInEditMode = isBeingEdited);

    editButtonDe.triggerEventHandler('click', null);
    expect(isInEditMode).toBeTruthy();
  });

  it('should emit false via toggleEditModeEvent when confirming the edit', () => {
    let isInEditMode: boolean | undefined;
    component.toggleEditModeEvent
      .subscribe((isBeingEdited: boolean) => isInEditMode = isBeingEdited);

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
      .subscribe((isBeingEdited: boolean) => isInEditMode = isBeingEdited);

    editButtonDe.triggerEventHandler('click', null);
    fixture.detectChanges();

    fixture.debugElement
      .query(By.css(`#cancel-edit-instructor-${expectedIndex}`))
      .triggerEventHandler('click', null);
    expect(isInEditMode).toBeFalsy();
  });
});
