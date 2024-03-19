import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { NgbToastModule } from '@ng-bootstrap/ng-bootstrap';
import { ToastComponent } from './toast.component';

describe('ToastComponent', () => {
  let component: ToastComponent;
  let fixture: ComponentFixture<ToastComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ToastComponent],
      imports: [NgbToastModule],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ToastComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should set autohide', () => {
    component.toast = { message: 'Test message', autohide: false, classes: '' };
    component.setAutohide(true);
    expect(component.toast.autohide).toBe(true);
  });

  it('should remove toast', () => {
    component.toast = { message: 'Test message', autohide: false, classes: '' };
    jest.spyOn(component.toastChange, 'emit');
    component.removeToast();
    expect(component.toast).toBe(null);
    expect(component.toastChange.emit).toHaveBeenCalledWith(null);
  });

  it('should return false if message is not a TemplateRef', () => {
    component.toast = { message: 'Test message', autohide: false, classes: '' };
    expect(component.isTemplate()).toBe(false);
  });
});
