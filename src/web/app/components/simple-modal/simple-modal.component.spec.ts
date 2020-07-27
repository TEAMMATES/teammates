import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { AjaxLoadingComponent } from '../ajax-loading/ajax-loading.component';
import { SimpleModalComponent } from './simple-modal.component';

describe('SimpleModalComponent', () => {
  let component: SimpleModalComponent;
  let fixture: ComponentFixture<SimpleModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [SimpleModalComponent, AjaxLoadingComponent],
      providers: [NgbActiveModal],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SimpleModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
