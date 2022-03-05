import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NotificationEditFormComponent } from './notification-edit-form.component';

describe('NotificationEditFormComponent', () => {
  let component: NotificationEditFormComponent;
  let fixture: ComponentFixture<NotificationEditFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [NotificationEditFormComponent],
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NotificationEditFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
