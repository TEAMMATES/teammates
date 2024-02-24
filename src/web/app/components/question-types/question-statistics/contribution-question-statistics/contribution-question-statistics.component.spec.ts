import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { DynamicModule } from 'ng-dynamic-component';
import { ContributionQuestionStatisticsComponent } from './contribution-question-statistics.component';
import { ContributionComponent } from './contribution.component';
import { SortableTableComponent } from '../../../sortable-table/sortable-table.component';
import { TeammatesRouterModule } from '../../../teammates-router/teammates-router.module';

describe('ContributionQuestionStatisticsComponent', () => {
  let component: ContributionQuestionStatisticsComponent;
  let fixture: ComponentFixture<ContributionQuestionStatisticsComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [NgbTooltipModule, DynamicModule, RouterTestingModule, TeammatesRouterModule],
      declarations: [
        ContributionQuestionStatisticsComponent,
        ContributionComponent,
        SortableTableComponent,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ContributionQuestionStatisticsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
