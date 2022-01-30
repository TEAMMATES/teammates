import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminStatisticsPageComponent } from './admin-statistics-page.component';

describe('AdminStatisticsPageComponent', () => {
  let component: AdminStatisticsPageComponent;
  let fixture: ComponentFixture<AdminStatisticsPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AdminStatisticsPageComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdminStatisticsPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
