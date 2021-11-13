import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { NgbDropdownModule } from '@ng-bootstrap/ng-bootstrap';
import { of } from 'rxjs/internal/observable/of';
import { FeedbackQuestionType } from '../../../types/api-request';
import { AjaxLoadingModule } from '../ajax-loading/ajax-loading.module';
import { TeammatesCommonModule } from '../teammates-common/teammates-common.module';
import { TeammatesRouterModule } from '../teammates-router/teammates-router.module';
import { AddingQuestionPanelComponent } from './adding-question-panel.component';

describe('AddingQuestionPanelComponent', () => {
  let component: AddingQuestionPanelComponent;
  let fixture: ComponentFixture<AddingQuestionPanelComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        AddingQuestionPanelComponent,
      ],
      imports: [
        AjaxLoadingModule,
        RouterTestingModule,
        NgbDropdownModule,
        TeammatesCommonModule,
        TeammatesRouterModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AddingQuestionPanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('raises the selected event when templateQuestionModalEvent is invoked', () => {
    spyOn(component.templateQuestionModalEvent, 'emit');

    const button: any = fixture.nativeElement.querySelector('button');

    button.click();
    fixture.detectChanges();

    component.templateQuestionModalHandler();

    expect(component.templateQuestionModalEvent.emit).toHaveBeenCalled();
  });

  it('raises the selected event when populateAndShowNewQuestionFormEvent is invoked', () => {
    const type: FeedbackQuestionType = FeedbackQuestionType.MCQ;
    const button: any = fixture.nativeElement.querySelector('button');

    spyOn(component.populateAndShowNewQuestionFormEvent, 'emit').and.returnValue(of(type));
    button.click();
    fixture.detectChanges();

    component.populateAndShowNewQuestionFormHandler(FeedbackQuestionType.MCQ);

    expect(component.populateAndShowNewQuestionFormEvent.emit).toHaveBeenCalledWith(type);
  });

  it('raises the selected event when templateQuestionModalEvent is invoked', () => {
    const button: any = fixture.nativeElement.querySelector('button');

    spyOn(component.copyQuestionsFromOtherSessionsEvent, 'emit');
    button.click();
    fixture.detectChanges();

    component.copyQuestionsFromOtherSessionsHandler();

    expect(component.copyQuestionsFromOtherSessionsEvent.emit).toHaveBeenCalled();
  });
});
