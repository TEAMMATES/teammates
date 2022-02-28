import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { ContributionComponent } from './contribution.component';

describe('ContributionComponent', () => {
  let component: ContributionComponent;
  let fixture: ComponentFixture<ContributionComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ContributionComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ContributionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
