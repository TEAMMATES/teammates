import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { first } from 'rxjs';
import { InstructorRequestFormModel } from './instructor-request-form-model';
import { InstructorRequestFormComponent } from './instructor-request-form.component';

describe('InstructorRequestFormComponent', () => {
  let component: InstructorRequestFormComponent;
  let fixture: ComponentFixture<InstructorRequestFormComponent>;
  const typicalModel: InstructorRequestFormModel = {
    name: 'John Doe',
    institution: 'Example Institution',
    country: 'Example Country',
    email: 'jd@example.edu',
    homePage: 'xyz.example.edu/john',
    comments: '',
  };

  /**
   * Fills in form fields with the given data.
   *
   * @param data Data to fill form with.
   */
  function fillFormWith(data: InstructorRequestFormModel): void {
    component.name.setValue(data.name);
    component.institution.setValue(data.institution);
    component.country.setValue(data.country);
    component.email.setValue(data.email);
    component.homePage.setValue(data.homePage);
    component.comments.setValue(data.comments);
  }

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [InstructorRequestFormComponent],
      imports: [ReactiveFormsModule],
    });
    fixture = TestBed.createComponent(InstructorRequestFormComponent);
    component = fixture.componentInstance;

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should emit requestSubmissionEvent once when submit button is clicked', () => {
    jest.spyOn(component.requestSubmissionEvent, 'emit');

    fillFormWith(typicalModel);
    const submitButton = fixture.debugElement.query(By.css('#submit-button'));
    submitButton.nativeElement.click();

    expect(component.requestSubmissionEvent.emit).toHaveBeenCalledTimes(1);
  });

  it('should emit requestSubmissionEvent with the correct data when form is submitted', () => {
    // Listen for emitted value
    let actualModel: InstructorRequestFormModel | null = null;
    component.requestSubmissionEvent.pipe(first())
        .subscribe((data: InstructorRequestFormModel) => { actualModel = data; });

    fillFormWith(typicalModel);
    component.onSubmit();

    expect(actualModel).toBeTruthy();
    expect(actualModel!.name).toBe(typicalModel.name);
    expect(actualModel!.institution).toBe(typicalModel.institution);
    expect(actualModel!.country).toBe(typicalModel.country);
    expect(actualModel!.email).toBe(typicalModel.email);
    expect(actualModel!.homePage).toBe(typicalModel.homePage);
    expect(actualModel!.comments).toBe(typicalModel.comments);
  });
});
