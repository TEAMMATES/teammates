import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InstructorRequestFormComponent } from './instructor-request-form.component';
import { InstructorRequestFormModel } from './instructor-request-form-model';
import { By } from '@angular/platform-browser';
import { first } from 'rxjs';
import { ReactiveFormsModule } from '@angular/forms';

describe('InstructorRequestFormComponent', () => {
  let component: InstructorRequestFormComponent;
  let fixture: ComponentFixture<InstructorRequestFormComponent>;
  let typicalModel: InstructorRequestFormModel = {
    name: "John Doe",
    institution: "Example Institution",
    country: "Example Country",
    email: "jd@example.edu",
    homePage: "xyz.example.edu/john",
    comments: "",
  }; 

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

  it('should raise requestSubmissionEvent when submit button is clicked', () => {
    jest.spyOn(component.requestSubmissionEvent, 'emit');

    fillFormWith(typicalModel);
    let submitButton = fixture.debugElement.query(By.css('#submit-button'));
    submitButton.nativeElement.click();

    expect(component.requestSubmissionEvent.emit).toHaveBeenCalledTimes(1);
  });

  it('should raise requestSubmissionEvent with the correct data when form is submitted', () => {
    let actualModel: InstructorRequestFormModel | null = null;
    // Listen for event
    component.requestSubmissionEvent.pipe(first())
        .subscribe((data: InstructorRequestFormModel) => (actualModel = data));

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

  function fillFormWith(data: InstructorRequestFormModel) {
    component.name.setValue(data.name);
    component.institution.setValue(data.institution);
    component.country.setValue(data.country);
    component.email.setValue(data.email);
    component.homePage.setValue(data.homePage);
    component.comments.setValue(data.comments);
  }
});
