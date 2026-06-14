import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { SavingCompleteModalComponent } from './saving-complete-modal.component';

describe('SavingCompleteModalComponent', () => {
  let component: SavingCompleteModalComponent;
  let fixture: ComponentFixture<SavingCompleteModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [NgbActiveModal, provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();

    fixture = TestBed.createComponent(SavingCompleteModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render a submitted summary', () => {
    component.questions = [{ questionNumber: 1 } as never];
    component.submittedQuestions = [1];
    component.notYetAnsweredQuestions = [];
    component.failToSaveQuestions = {};
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('Submission summary');
    expect(fixture.nativeElement.textContent).toContain('Submitted');
    expect(fixture.nativeElement.textContent).toContain('Q1');
  });

  it('should render errors and unanswered questions', () => {
    component.questions = [{ questionNumber: 2 } as never];
    component.submittedQuestions = [];
    component.notYetAnsweredQuestions = [2];
    component.failToSaveQuestions = { 2: 'Invalid responses provided.' };
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('Errors');
    expect(fixture.nativeElement.textContent).toContain('Not answered yet');
    expect(fixture.nativeElement.textContent).toContain('Question 2');
  });
});
