import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InstructorRequestFormComponent } from './instructor-request-form.component';
import { InstructorRequestFormModel } from './instructor-request-form-model';
import { By } from '@angular/platform-browser';

describe('InstructorRequestFormComponent', () => {
  let component: InstructorRequestFormComponent;
  let fixture: ComponentFixture<InstructorRequestFormComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [InstructorRequestFormComponent],
    });
    fixture = TestBed.createComponent(InstructorRequestFormComponent);
    component = fixture.componentInstance;

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should raise requestSubmissionEvent when submit', () => {
    spyOn(component.requestSubmissionEvent, 'emit');

    // Simulate form being submitted
    let expectedModel: InstructorRequestFormModel = {
      name: "John Doe",
      institution: "Example Institution",
      country: "Example Country",
      email: "jd@example.edu",
      homePage: "",
      comments: "",
    };
    component.name.setValue(expectedModel.name);
    component.institution.setValue(expectedModel.institution);
    component.country.setValue(expectedModel.country);
    component.email.setValue(expectedModel.email);
    component.homePage.setValue(expectedModel.email);
    component.comments.setValue(expectedModel.comments);
    let btn = fixture.debugElement.query(By.css('btn[type="submit"]')).nativeElement;
    btn.click();

    expect(component.requestSubmissionEvent.emit).toHaveBeenCalledTimes(1);

    // TODO: test value
    // let submissionModel: InstructorRequestFormModel | null;
    // component.requestSubmissionEvent.pipe(first()).subscribe((data: InstructorRequestFormModel) => (submissionModel = data));
    // expect(component.requestSubmissionEvent.emit).toHaveBeenCalledWith(expectedModel);
  });
});
