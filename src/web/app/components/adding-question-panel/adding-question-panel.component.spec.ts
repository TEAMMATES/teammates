import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { NgbDropdownModule } from '@ng-bootstrap/ng-bootstrap';

import { AjaxLoadingModule } from '../ajax-loading/ajax-loading.module';
import { TeammatesCommonModule } from '../teammates-common/teammates-common.module';
import { TeammatesRouterModule } from '../teammates-router/teammates-router.module';
import { AddingQuestionPanelComponent } from './adding-question-panel.component';

describe('AddingQuestionPanelComponent', () => {
  let component: AddingQuestionPanelComponent;
  let fixture: ComponentFixture<AddingQuestionPanelComponent>;

  beforeEach(waitForAsync(() => {
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
});
