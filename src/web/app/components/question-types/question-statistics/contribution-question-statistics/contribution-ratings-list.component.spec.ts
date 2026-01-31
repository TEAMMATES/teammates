import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ContributionRatingsListComponent } from './contribution-ratings-list.component';

describe('ContributionRatingsListComponent', () => {
  let component: ContributionRatingsListComponent;
  let fixture: ComponentFixture<ContributionRatingsListComponent>;

  beforeEach(() => {
    fixture = TestBed.createComponent(ContributionRatingsListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
