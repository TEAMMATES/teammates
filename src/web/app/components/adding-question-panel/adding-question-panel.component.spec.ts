import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterModule } from '@angular/router';
import { NgbDropdownModule } from '@ng-bootstrap/ng-bootstrap';

import { AjaxLoadingModule } from '../../components/ajax-loading/ajax-loading.module';
import { TeammatesCommonModule } from '../../components/teammates-common/teammates-common.module';
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
        RouterModule,
        NgbDropdownModule,
        TeammatesCommonModule,
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
});
