import { DebugElement } from '@angular/core';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { InstructorListInfoTableRowModel, StudentListInfoTableRowModel } from './respondent-list-info-table-model';
import { RespondentListInfoTableComponent } from './respondent-list-info-table.component';
import { TableComparatorService } from '../../../../services/table-comparator.service';
import { createBuilder } from '../../../../test-helpers/generic-builder';
import testEventEmission from '../../../../test-helpers/test-event-emitter';
import { SortOrder } from '../../../../types/sort-properties';

describe('StudentListInfoTableComponent', () => {
  let component: RespondentListInfoTableComponent;
  let fixture: ComponentFixture<RespondentListInfoTableComponent>;

  const studentTableId = '#student-list-table';
  const instructorTableId = '#instructor-list-table';

  const studentModelBuilder = createBuilder<StudentListInfoTableRowModel>({
    email: 'student@gmail.com',
    name: 'Student',
    teamName: 'Team A',
    sectionName: 'Section 1',
    hasSubmittedSession: false,
    isSelected: false,
  });

  const instructorModelBuilder = createBuilder<InstructorListInfoTableRowModel>({
    email: 'instructor@gmail.com',
    name: 'Instructor',
    hasSubmittedSession: false,
    isSelected: false,
  });

  const selectTableRowByIndex = (tableId: string, index: number): DebugElement => {
    const table = fixture.debugElement.query(By.css(tableId));
    const rows = table.queryAll(By.css('tbody tr'));
    const row = rows[index];
    return row;
  };

  const selectStudentRowByIndex = (index: number): DebugElement => {
    return selectTableRowByIndex(studentTableId, index);
  };

  const selectInstructorRowByIndex = (index: number): DebugElement => {
    return selectTableRowByIndex(instructorTableId, index);
  };

  const clickRowCheckBox = (row: DebugElement): void => {
    const checkBox = row.query(By.css('input[type="checkbox"]'));
    checkBox.nativeElement.click();
  };

  const selectTableHeaderByText = (tableId: string, text: string): DebugElement | null => {
    const table = fixture.debugElement.query(By.css(tableId));
    const headers = table.queryAll(By.css('thead th'));
    const headerWithText = headers.find((header) => header.nativeElement.textContent.includes(text));
    return headerWithText ?? null;
  };

  const selectStudentTableHeaderByText = (text: string): DebugElement | null => {
    return selectTableHeaderByText(studentTableId, text);
  };

  const selectInstructorTableHeaderByText = (text: string): DebugElement | null => {
    return selectTableHeaderByText(instructorTableId, text);
  };

  const selectTableHeaderCheckBox = (tableId: string): DebugElement => {
    const table = fixture.debugElement.query(By.css(tableId));
    const headerCheckBox = table.query(By.css('thead th input[type="checkbox"]'));
    return headerCheckBox;
  };

  const selectStudentTableHeaderCheckBox = (): DebugElement => {
    return selectTableHeaderCheckBox(studentTableId);
  };

  const selectInstructorTableHeaderCheckBox = (): DebugElement => {
    return selectTableHeaderCheckBox('#instructor-list-table');
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [RespondentListInfoTableComponent],
      imports: [FormsModule],
      providers: [TableComparatorService],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    TestBed.inject(TableComparatorService);
    fixture = TestBed.createComponent(RespondentListInfoTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('sortStudentsTableRows: should reverse the sort order and emit rows sorted by sectionName', () => {
    let emittedRows: StudentListInfoTableRowModel[] | undefined;
    testEventEmission(component.studentListInfoTableRowModelsChange, (sortedRows) => { emittedRows = sortedRows; });

    component.studentListInfoTableRowModels = [
      studentModelBuilder.sectionName('A').build(),
      studentModelBuilder.sectionName('C').build(),
      studentModelBuilder.sectionName('B').build(),
    ];
    component.studentListInfoTableSortOrder = SortOrder.DESC;

    fixture.detectChanges();

    const studentSectionHeader = selectStudentTableHeaderByText('Section');

    expect(studentSectionHeader).toBeTruthy();
    studentSectionHeader?.nativeElement.click();

    expect(component.studentListInfoTableSortOrder).toBe(SortOrder.ASC);
    expect(emittedRows).toStrictEqual([
      studentModelBuilder.sectionName('A').build(),
      studentModelBuilder.sectionName('B').build(),
      studentModelBuilder.sectionName('C').build(),
    ]);
  });

  it('sortStudentsTableRows: should reverse the sort order and emit rows sorted by teamName', () => {
    let emittedRows: StudentListInfoTableRowModel[] | undefined;
    testEventEmission(component.studentListInfoTableRowModelsChange, (sortedRows) => { emittedRows = sortedRows; });

    component.studentListInfoTableRowModels = [
      studentModelBuilder.teamName('A').build(),
      studentModelBuilder.teamName('C').build(),
      studentModelBuilder.teamName('B').build(),
    ];
    component.studentListInfoTableSortOrder = SortOrder.DESC;

    fixture.detectChanges();

    const studentTeamHeader = selectStudentTableHeaderByText('Team');

    expect(studentTeamHeader).toBeTruthy();
    studentTeamHeader?.nativeElement.click();

    expect(component.studentListInfoTableSortOrder).toBe(SortOrder.ASC);
    expect(emittedRows).toStrictEqual([
      studentModelBuilder.teamName('A').build(),
      studentModelBuilder.teamName('B').build(),
      studentModelBuilder.teamName('C').build(),
    ]);
  });

  it('sortStudentsTableRows: should reverse the sort order and emit rows sorted by name', () => {
    let emittedRows: StudentListInfoTableRowModel[] | undefined;
    testEventEmission(component.studentListInfoTableRowModelsChange, (sortedRows) => { emittedRows = sortedRows; });

    component.studentListInfoTableRowModels = [
      studentModelBuilder.name('A').build(),
      studentModelBuilder.name('C').build(),
      studentModelBuilder.name('B').build(),
    ];
    component.studentListInfoTableSortOrder = SortOrder.DESC;

    fixture.detectChanges();

    const studentNameHeader = selectStudentTableHeaderByText('Student Name');

    expect(studentNameHeader).toBeTruthy();
    studentNameHeader?.nativeElement.click();

    expect(component.studentListInfoTableSortOrder).toBe(SortOrder.ASC);
    expect(emittedRows).toStrictEqual([
      studentModelBuilder.name('A').build(),
      studentModelBuilder.name('B').build(),
      studentModelBuilder.name('C').build(),
    ]);
  });

  it('sortStudentsTableRows: should reverse the sort order and emit rows sorted by email', () => {
    let emittedRows: StudentListInfoTableRowModel[] | undefined;
    testEventEmission(component.studentListInfoTableRowModelsChange, (sortedRows) => { emittedRows = sortedRows; });

    component.studentListInfoTableRowModels = [
      studentModelBuilder.email('A').build(),
      studentModelBuilder.email('C').build(),
      studentModelBuilder.email('B').build(),
    ];
    component.studentListInfoTableSortOrder = SortOrder.DESC;

    fixture.detectChanges();

    const studentEmailHeader = selectStudentTableHeaderByText('Email');

    expect(studentEmailHeader).toBeTruthy();
    studentEmailHeader?.nativeElement.click();

    expect(component.studentListInfoTableSortOrder).toBe(SortOrder.ASC);
    expect(emittedRows).toStrictEqual([
      studentModelBuilder.email('A').build(),
      studentModelBuilder.email('B').build(),
      studentModelBuilder.email('C').build(),
    ]);
  });

  it('sortStudentsTableRows: should reverse the sort order and emit rows sorted by submitted status', () => {
    let emittedRows: StudentListInfoTableRowModel[] | undefined;
    testEventEmission(component.studentListInfoTableRowModelsChange, (sortedRows) => { emittedRows = sortedRows; });

    component.studentListInfoTableRowModels = [
      studentModelBuilder.hasSubmittedSession(true).build(),
      studentModelBuilder.hasSubmittedSession(false).build(),
      studentModelBuilder.hasSubmittedSession(true).build(),
    ];
    component.shouldDisplayHasSubmittedSessionColumn = true;
    component.studentListInfoTableSortOrder = SortOrder.DESC;

    fixture.detectChanges();

    const studentSubmittedHeader = selectStudentTableHeaderByText('Submitted?');

    expect(studentSubmittedHeader).toBeTruthy();
    studentSubmittedHeader?.nativeElement.click();

    expect(component.studentListInfoTableSortOrder).toBe(SortOrder.ASC);
    expect(emittedRows).toStrictEqual([
      studentModelBuilder.hasSubmittedSession(false).build(),
      studentModelBuilder.hasSubmittedSession(true).build(),
      studentModelBuilder.hasSubmittedSession(true).build(),
    ]);
  });

  it('sortInstructorsTableRows: should reverse the sort order and emit rows sorted by instructor name', () => {
    let emittedRows: InstructorListInfoTableRowModel[] | undefined;
    testEventEmission(component.instructorListInfoTableRowModelsChange, (sortedRows) => { emittedRows = sortedRows; });

    component.instructorListInfoTableRowModels = [
      instructorModelBuilder.name('Instructor A').build(),
      instructorModelBuilder.name('Instructor C').build(),
      instructorModelBuilder.name('Instructor B').build(),
    ];
    component.instructorListInfoTableSortOrder = SortOrder.DESC;
    fixture.detectChanges();

    const instructorNameHeader = selectInstructorTableHeaderByText('Instructor Name');
    expect(instructorNameHeader).toBeTruthy();

    instructorNameHeader?.nativeElement.click();

    expect(component.instructorListInfoTableSortOrder).toBe(SortOrder.ASC);
    expect(emittedRows).toStrictEqual([
      instructorModelBuilder.name('Instructor A').build(),
      instructorModelBuilder.name('Instructor B').build(),
      instructorModelBuilder.name('Instructor C').build(),
    ]);
  });

  it('sortInstructorsTableRows: should reverse the sort order and emit rows sorted by instructor email', () => {
    let emittedRows: InstructorListInfoTableRowModel[] | undefined;
    testEventEmission(component.instructorListInfoTableRowModelsChange, (sortedRows) => { emittedRows = sortedRows; });

    component.instructorListInfoTableRowModels = [
      instructorModelBuilder.email('Instructor A').build(),
      instructorModelBuilder.email('Instructor C').build(),
      instructorModelBuilder.email('Instructor B').build(),
    ];
    component.instructorListInfoTableSortOrder = SortOrder.DESC;
    fixture.detectChanges();

    const instructorEmailHeader = selectInstructorTableHeaderByText('Email');
    expect(instructorEmailHeader).toBeTruthy();

    instructorEmailHeader?.nativeElement.click();

    expect(component.instructorListInfoTableSortOrder).toBe(SortOrder.ASC);
    expect(emittedRows).toStrictEqual([
      instructorModelBuilder.email('Instructor A').build(),
      instructorModelBuilder.email('Instructor B').build(),
      instructorModelBuilder.email('Instructor C').build(),
    ]);
  });

  it('sortInstructorsTableRows: should reverse the sort order and emit rows sorted by'
    + 'instructor submitted status', () => {
      let emittedRows: InstructorListInfoTableRowModel[] | undefined;
      testEventEmission(component.instructorListInfoTableRowModelsChange,
        (sortedRows) => { emittedRows = sortedRows; });

      component.instructorListInfoTableRowModels = [
        instructorModelBuilder.hasSubmittedSession(true).build(),
        instructorModelBuilder.hasSubmittedSession(false).build(),
        instructorModelBuilder.hasSubmittedSession(true).build(),
      ];
      component.shouldDisplayHasSubmittedSessionColumn = true;
      component.instructorListInfoTableSortOrder = SortOrder.DESC;
      fixture.detectChanges();

      const instructorEmailHeader = selectInstructorTableHeaderByText('Submitted?');
      expect(instructorEmailHeader).toBeTruthy();

      instructorEmailHeader?.nativeElement.click();

      expect(component.instructorListInfoTableSortOrder).toBe(SortOrder.ASC);
      expect(emittedRows).toStrictEqual([
        instructorModelBuilder.hasSubmittedSession(false).build(),
        instructorModelBuilder.hasSubmittedSession(true).build(),
        instructorModelBuilder.hasSubmittedSession(true).build(),
      ]);
    });

  it('handleSelectionOfStudentRow: should toggle isSelected of student model', () => {
    component.studentListInfoTableRowModels = [
      studentModelBuilder.name('Student A').isSelected(false).build(),
      studentModelBuilder.name('Student B').isSelected(true).build(),
      studentModelBuilder.name('Student C').isSelected(false).build(),
    ];

    let emittedRows: StudentListInfoTableRowModel[] | undefined;
    testEventEmission(component.studentListInfoTableRowModelsChange,
      (sortedRows) => { emittedRows = sortedRows; }, false);
    const handleSelectionOfStudentRowSpy = jest.spyOn(component, 'handleSelectionOfStudentRow');
    fixture.detectChanges();

    clickRowCheckBox(selectStudentRowByIndex(1));

    expect(handleSelectionOfStudentRowSpy).toHaveBeenCalledTimes(1);
    expect(emittedRows).toStrictEqual([
      studentModelBuilder.name('Student A').isSelected(false).build(),
      studentModelBuilder.name('Student B').isSelected(false).build(),
      studentModelBuilder.name('Student C').isSelected(false).build(),
    ]);
  });

  it('handleSelectionOfInstructorRow: should toggle isSelected of instructor model', () => {
    component.instructorListInfoTableRowModels = [
      instructorModelBuilder.name('Instructor A').isSelected(false).build(),
      instructorModelBuilder.name('Instructor B').isSelected(false).build(),
      instructorModelBuilder.name('Instructor C').isSelected(true).build(),
    ];

    let emittedRows: InstructorListInfoTableRowModel[] | undefined;
    testEventEmission(component.instructorListInfoTableRowModelsChange,
      (sortedRows) => { emittedRows = sortedRows; }, false);
    const handleSelectionOfInstructorRowSpy = jest.spyOn(component, 'handleSelectionOfInstructorRow');
    fixture.detectChanges();

    clickRowCheckBox(selectInstructorRowByIndex(1));

    expect(handleSelectionOfInstructorRowSpy).toHaveBeenCalledTimes(1);
    expect(emittedRows).toStrictEqual([
      instructorModelBuilder.name('Instructor A').isSelected(false).build(),
      instructorModelBuilder.name('Instructor B').isSelected(true).build(),
      instructorModelBuilder.name('Instructor C').isSelected(true).build(),
    ]);
  });

  it('isAllStudentsSelected: should return true if every student isSelected', () => {
    component.studentListInfoTableRowModels = [
      studentModelBuilder.isSelected(true).build(),
      studentModelBuilder.isSelected(true).build(),
      studentModelBuilder.isSelected(true).build(),
      studentModelBuilder.isSelected(true).build(),
      studentModelBuilder.isSelected(true).build(),
    ];

    expect(component.isAllStudentsSelected).toBeTruthy();
  });

  it('isAllStudentsSelected: should return false if at least one student !isSelected', () => {
    component.studentListInfoTableRowModels = [
      studentModelBuilder.isSelected(true).build(),
      studentModelBuilder.isSelected(true).build(),
      studentModelBuilder.isSelected(false).build(),
      studentModelBuilder.isSelected(true).build(),
      studentModelBuilder.isSelected(true).build(),
    ];

    expect(component.isAllStudentsSelected).toBeFalsy();
  });

  it('isAllInstructorsSelected: should return true if every instructor isSelected', () => {
    component.instructorListInfoTableRowModels = [
      instructorModelBuilder.isSelected(true).build(),
      instructorModelBuilder.isSelected(true).build(),
      instructorModelBuilder.isSelected(true).build(),
      instructorModelBuilder.isSelected(true).build(),
      instructorModelBuilder.isSelected(true).build(),
    ];

    expect(component.isAllInstructorsSelected).toBeTruthy();
  });

  it('isAllInstructorsSelected: should return false if at least one instructor !isSelected', () => {
    component.instructorListInfoTableRowModels = [
      instructorModelBuilder.isSelected(true).build(),
      instructorModelBuilder.isSelected(true).build(),
      instructorModelBuilder.isSelected(false).build(),
      instructorModelBuilder.isSelected(true).build(),
      instructorModelBuilder.isSelected(true).build(),
    ];

    expect(component.isAllInstructorsSelected).toBeFalsy();
  });

  it('changeSelectionStatusForAllStudentsHandler: should set all isSelected to true if not all are selected', () => {
    component.studentListInfoTableRowModels = [
      studentModelBuilder.isSelected(true).build(),
      studentModelBuilder.isSelected(false).build(),
      studentModelBuilder.isSelected(true).build(),
    ];

    let emittedRows: StudentListInfoTableRowModel[] | undefined;
    testEventEmission(component.studentListInfoTableRowModelsChange,
      (newRows) => { emittedRows = newRows; });
    const changeSelectionStatusForAllStudentsHandlerSpy =
      jest.spyOn(component, 'changeSelectionStatusForAllStudentsHandler');

    fixture.detectChanges();

    selectStudentTableHeaderCheckBox().nativeElement.click();
    expect(changeSelectionStatusForAllStudentsHandlerSpy).toHaveBeenCalledTimes(1);
    expect(emittedRows).toStrictEqual([
      studentModelBuilder.isSelected(true).build(),
      studentModelBuilder.isSelected(true).build(),
      studentModelBuilder.isSelected(true).build(),
    ]);
  });

  it('changeSelectionStatusForAllStudentsHandler: should set all isSelected to false if'
    + 'checkbox is clicked twice', () => {
      component.studentListInfoTableRowModels = [
        studentModelBuilder.isSelected(true).build(),
        studentModelBuilder.isSelected(true).build(),
        studentModelBuilder.isSelected(true).build(),
      ];

      let emittedRows: StudentListInfoTableRowModel[] | undefined;
      testEventEmission(component.studentListInfoTableRowModelsChange, (newRows) => { emittedRows = newRows; }, false);
      const changeSelectionStatusForAllStudentsHandlerSpy =
        jest.spyOn(component, 'changeSelectionStatusForAllStudentsHandler');

      fixture.detectChanges();

      selectStudentTableHeaderCheckBox().nativeElement.click();
      selectStudentTableHeaderCheckBox().nativeElement.click();
      expect(changeSelectionStatusForAllStudentsHandlerSpy).toHaveBeenCalledTimes(2);
      expect(emittedRows).toStrictEqual([
        studentModelBuilder.isSelected(false).build(),
        studentModelBuilder.isSelected(false).build(),
        studentModelBuilder.isSelected(false).build(),
      ]);
    });

  it('changeSelectionStatusForAllInstructorsHandler: should set all isSelected to true if not all are selected', () => {
    component.instructorListInfoTableRowModels = [
      instructorModelBuilder.isSelected(true).build(),
      instructorModelBuilder.isSelected(false).build(),
      instructorModelBuilder.isSelected(true).build(),
    ];

    let emittedRows: InstructorListInfoTableRowModel[] | undefined;
    testEventEmission(component.instructorListInfoTableRowModelsChange, (newRows) => { emittedRows = newRows; });
    const changeSelectionStatusForAllInstructorsHandlerSpy =
      jest.spyOn(component, 'changeSelectionStatusForAllInstructorsHandler');

    fixture.detectChanges();

    selectInstructorTableHeaderCheckBox().nativeElement.click();
    expect(changeSelectionStatusForAllInstructorsHandlerSpy).toHaveBeenCalledTimes(1);
    expect(emittedRows).toStrictEqual([
      instructorModelBuilder.isSelected(true).build(),
      instructorModelBuilder.isSelected(true).build(),
      instructorModelBuilder.isSelected(true).build(),
    ]);
  });

  it('changeSelectionStatusForAllInstructorsHandler: should set all isSelected to false'
    + 'if checkbox is clicked twice', () => {
      component.instructorListInfoTableRowModels = [
        instructorModelBuilder.isSelected(true).build(),
        instructorModelBuilder.isSelected(true).build(),
        instructorModelBuilder.isSelected(true).build(),
      ];

      let emittedRows: InstructorListInfoTableRowModel[] | undefined;
      testEventEmission(component.instructorListInfoTableRowModelsChange,
        (newRows) => { emittedRows = newRows; }, false);
      const changeSelectionStatusForAllInstructorsHandlerSpy =
        jest.spyOn(component, 'changeSelectionStatusForAllInstructorsHandler');

      fixture.detectChanges();

      selectInstructorTableHeaderCheckBox().nativeElement.click();
      selectInstructorTableHeaderCheckBox().nativeElement.click();
      expect(changeSelectionStatusForAllInstructorsHandlerSpy).toHaveBeenCalledTimes(2);
      expect(emittedRows).toStrictEqual([
        instructorModelBuilder.isSelected(false).build(),
        instructorModelBuilder.isSelected(false).build(),
        instructorModelBuilder.isSelected(false).build(),
      ]);
    });

});
