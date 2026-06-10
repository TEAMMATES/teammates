import { ElementRef, EmbeddedViewRef, Injector, TemplateRef } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NgbToastModule } from '@ng-bootstrap/ng-bootstrap/toast';
import { ToastComponent } from './toast.component';
import { Toast } from './toast';

class MockTemplateRef extends TemplateRef<void> {
  override elementRef: ElementRef = new ElementRef(null);
  override createEmbeddedView(_context: void, injector?: Injector): EmbeddedViewRef<void> {
    // eslint-disable-next-line @typescript-eslint/no-base-to-string, @typescript-eslint/restrict-template-expressions
    throw new Error(`Method not implemented with injector ${injector}.`);
  }
}

describe('ToastComponent', () => {
  let component: ToastComponent;
  let fixture: ComponentFixture<ToastComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NgbToastModule],
    }).compileComponents();

    fixture = TestBed.createComponent(ToastComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should set autohide', () => {
    const toast: Toast = { message: 'Test message', autohide: false, classes: '' };
    component.toast = toast;
    component.setAutohide(true);
    expect(component.toast.autohide).toBe(true);
  });

  it('should remove toast', () => {
    const toast: Toast = { message: 'Test message', autohide: false, classes: '' };
    component.toast = toast;
    vi.spyOn(component.toastChange, 'emit');
    component.removeToast();
    expect(component.toast).toBe(null);
    expect(component.toastChange.emit).toHaveBeenCalledWith(null);
  });

  it('should return false if message is not a TemplateRef', () => {
    const toast: Toast = { message: 'Test message', autohide: false, classes: '' };
    component.toast = toast;
    expect(component.isTemplate()).toBe(false);
  });

  it('should return true if message is a TemplateRef', () => {
    const toast: Toast = { message: new MockTemplateRef(), autohide: false, classes: '' };
    component.toast = toast;
    expect(component.isTemplate()).toBe(true);
  });
});
