import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminNotificationsPageComponent } from './admin-notifications-page.component';

describe('AdminNotificationsPageComponent', () => {
  let component: AdminNotificationsPageComponent;
  let fixture: ComponentFixture<AdminNotificationsPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AdminNotificationsPageComponent],
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AdminNotificationsPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
