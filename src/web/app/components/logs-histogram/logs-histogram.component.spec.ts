// @ts-ignore
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { LogsHistogramComponent } from './logs-histogram.component';

describe('LogsHistogramComponent', () => {
  let component: LogsHistogramComponent;
  let fixture: ComponentFixture<LogsHistogramComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [LogsHistogramComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LogsHistogramComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  
  //test whether there will be 2 bars
  it('should display bars based on provided data',()=>{
    //set the attributes of data
    component.data=[
      {sourceLocation:{file:'file1.ts',function:'functionA'},numberOfTimes:5},
      {sourceLocation: { file: 'file2.ts', function: 'functionB' }, numberOfTimes: 10},
    ];
    fixture.detectChanges();//trigger the changes detect in Angular
    const bars = fixture.debugElement.nativeElement.querySelectorAll('.bar');
    expect(bars.length).toBe(2);
  });

  //test how it would handle when there is no input data
  it('should handle no data gracefully', () => {
    component.data = [];//this is the empty input data
    fixture.detectChanges();

    const bars = fixture.debugElement.nativeElement.querySelectorAll('.bar');
    expect(bars.length).toBe(0);//see whether there is really not any bars
  });

  it('should show tooltip with correct data on bar hover', () => {
    component.data = [
      { sourceLocation: { file: 'file1.ts', function: 'functionA' }, numberOfTimes: 5 },
    ];
    fixture.detectChanges();

    //simulate the action that the mouse hovering the bar
    const bar = fixture.debugElement.nativeElement.querySelector('.bar');
    bar.dispatchEvent(new Event('mouseover'));

    //enquire about tool tip
    const tooltip = document.body.querySelector('div');
    expect(tooltip).toBeTruthy();
    expect(tooltip.innerHTML).toContain('file1.ts');
    expect(tooltip.innerHTML).toContain('functionA');
    expect(tooltip.innerHTML).toContain('5');
  });

});
