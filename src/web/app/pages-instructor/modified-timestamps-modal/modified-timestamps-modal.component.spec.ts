import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModifiedTimestampModalComponent } from './modified-timestamps-modal.component';

describe('ModifiedTimestampModalComponent', () => {
  let component: ModifiedTimestampModalComponent;
  let fixture: ComponentFixture<ModifiedTimestampModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ModifiedTimestampModalComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ModifiedTimestampModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
