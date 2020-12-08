import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { DynamicComponentModule } from 'ng-dynamic-component';
import { SortableTableComponent } from '../../../sortable-table/sortable-table.component';
import { TeammatesRouterModule } from '../../../teammates-router/teammates-router.module';
import { ContributionQuestionStatisticsComponent } from './contribution-question-statistics.component';
import { ContributionComponent } from './contribution.component';

describe('ContributionQuestionStatisticsComponent', () => {
  let component: ContributionQuestionStatisticsComponent;
  let fixture: ComponentFixture<ContributionQuestionStatisticsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [NgbTooltipModule, DynamicComponentModule, RouterTestingModule, TeammatesRouterModule],
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
