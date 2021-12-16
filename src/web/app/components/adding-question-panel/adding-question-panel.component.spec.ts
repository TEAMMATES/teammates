import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { NgbDropdownModule } from '@ng-bootstrap/ng-bootstrap';
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

  it('raises the selected event when event is invoked', () => {
    const type: FeedbackQuestionType = FeedbackQuestionType.MCQ;
    const button: any = fixture.nativeElement.querySelector('button');

    spyOn(component.templateQuestionModalEvent, 'emit');
    spyOn(component.populateAndShowNewQuestionFormEvent, 'emit');
    spyOn(component.copyQuestionsFromOtherSessionsEvent, 'emit');

    button.click();
    fixture.detectChanges();

    component.templateQuestionModalHandler();
    expect(component.templateQuestionModalEvent.emit).toHaveBeenCalled();

    button.click();
    fixture.detectChanges();

    component.populateAndShowNewQuestionFormHandler(FeedbackQuestionType.MCQ);
    expect(component.populateAndShowNewQuestionFormEvent.emit).toHaveBeenCalledWith(type);

    button.click();
    fixture.detectChanges();

    component.copyQuestionsFromOtherSessionsHandler();
    expect(component.copyQuestionsFromOtherSessionsEvent.emit).toHaveBeenCalled();
  });
});
