import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { SingleQuestionSaveErrorModalComponent } from './single-question-save-error-modal.component';

describe('SingleQuestionSaveErrorModalComponent', () => {
  let component: SingleQuestionSaveErrorModalComponent;
  let fixture: ComponentFixture<SingleQuestionSaveErrorModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [NgbActiveModal],
    }).compileComponents();

    fixture = TestBed.createComponent(SingleQuestionSaveErrorModalComponent);
    component = fixture.componentInstance;
  });

  it('should show the question number and error details', () => {
    component.questionNumber = 3;
    component.errorMessage = 'Invalid response';
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('Question 3 could not be submitted');
    expect(fixture.nativeElement.textContent).toContain('Invalid response');
  });
});
