import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ContributionRatingsListComponent } from './contribution-ratings-list.component';
import { ContributionComponent } from './contribution.component';

describe('ContributionRatingsListComponent', () => {
  let component: ContributionRatingsListComponent;
  let fixture: ComponentFixture<ContributionRatingsListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ContributionRatingsListComponent, ContributionComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ContributionRatingsListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
