[33mcommit 1c2b94d63a7be533d96e149223c0f4368bcea1cd[m[33m ([m[1;36mHEAD -> [m[1;32mmaster[m[33m, [m[1;31mupstream/master[m[33m)[m
Author: Mo Zongran <mzongran@comp.nus.edu.sg>
Date:   Sat Oct 3 11:19:56 2020 +0800

    [#10315] Refactor search result for instructor search (#10709)
    
    * Refactor search result for instructor search
    
    * Migrate tests to instructor search component from search service
    
    * Fix test
    
    Co-authored-by: Zongran Mo <zongran.mo@shopee.com>
    Co-authored-by: Ahmed Bahajjaj <Ahmed_Bahajjaj@u.nus.edu>

[1mdiff --git a/src/web/app/pages-instructor/instructor-search-page/instructor-search-page.component.spec.ts b/src/web/app/pages-instructor/instructor-search-page/instructor-search-page.component.spec.ts[m
[1mindex 7af41509ee..396027a0cd 100644[m
[1m--- a/src/web/app/pages-instructor/instructor-search-page/instructor-search-page.component.spec.ts[m
[1m+++ b/src/web/app/pages-instructor/instructor-search-page/instructor-search-page.component.spec.ts[m
[36m@@ -1,14 +1,73 @@[m
 import { HttpClientTestingModule } from '@angular/common/http/testing';[m
[31m-[m
 import { async, ComponentFixture, TestBed } from '@angular/core/testing';[m
 import { RouterTestingModule } from '@angular/router/testing';[m
[31m-import { JoinState } from '../../../types/api-output';[m
[32m+[m[32mimport { of } from 'rxjs';[m
[32m+[m
[32m+[m[32mimport { HttpRequestService } from '../../../services/http-request.service';[m
[32m+[m[32mimport { ResourceEndpoints } from '../../../types/api-endpoints';[m
[32m+[m[32mimport { InstructorPrivilege, JoinState, Student, Students } from '../../../types/api-output';[m
[32m+[m[32mimport { StudentListRowModel } from '../../components/student-list/student-list.component';[m
 import { InstructorSearchPageComponent } from './instructor-search-page.component';[m
 import { InstructorSearchPageModule } from './instructor-search-page.module';[m
[32m+[m[32mimport { SearchStudentsListRowTable } from './student-result-table/student-result-table.component';[m
 [m
 describe('InstructorSearchPageComponent', () => {[m
   let component: InstructorSearchPageComponent;[m
   let fixture: ComponentFixture<InstructorSearchPageComponent>;[m
[32m+[m[32m  let spyHttpRequestService: any;[m
[32m+[m[32m  let coursesWithStudents: SearchStudentsListRowTable[];[m
[32m+[m
[32m+[m[32m  const mockStudents: Students = {[m
[32m+[m[32m    students: [[m
[32m+[m[32m      {[m
[32m+[m[32m        email: 'alice@example.com',[m
[32m+[m[32m        courseId: 'CS3281',[m
[32m+[m[32m        name: 'Alice',[m
[32m+[m[32m        joinState: JoinState.JOINED,[m
[32m+[m[32m        teamName: 'Team 1',[m
[32m+[m[32m        sectionName: 'Section 1',[m
[32m+[m[32m      },[m
[32m+[m[32m      {[m
[32m+[m[32m        email: 'bob@example.com',[m
[32m+[m[32m        courseId: 'CS3281',[m
[32m+[m[32m        name: 'Bob',[m
[32m+[m[32m        joinState: JoinState.JOINED,[m
[32m+[m[32m        teamName: 'Team 1',[m
[32m+[m[32m        sectionName: 'Section 1',[m
[32m+[m[32m      },[m
[32m+[m[32m      {[m
[32m+[m[32m        email: 'chloe@example.com',[m
[32m+[m[32m        courseId: 'CS3281',[m
[32m+[m[32m        name: 'Chloe',[m
[32m+[m[32m        joinState: JoinState.JOINED,[m
[32m+[m[32m        teamName: 'Team 1',[m
[32m+[m[32m        sectionName: 'Section 2',[m
[32m+[m[32m      },[m
[32m+[m[32m      {[m
[32m+[m[32m        email: 'david@example.com',[m
[32m+[m[32m        courseId: 'CS3282',[m
[32m+[m[32m        name: 'David',[m
[32m+[m[32m        joinState: JoinState.JOINED,[m
[32m+[m[32m        teamName: 'Team 1',[m
[32m+[m[32m        sectionName: 'Section 2',[m
[32m+[m[32m      },[m
[32m+[m[32m    ],[m
[32m+[m[32m  };[m
[32m+[m
[32m+[m[32m  beforeEach(() => {[m
[32m+[m[32m    spyHttpRequestService = {[m
[32m+[m[32m      get: jest.fn(),[m
[32m+[m[32m      post: jest.fn(),[m
[32m+[m[32m      put: jest.fn(),[m
[32m+[m[32m      delete: jest.fn(),[m
[32m+[m[32m    };[m
[32m+[m[32m    TestBed.configureTestingModule({[m
[32m+[m[32m      imports: [HttpClientTestingModule],[m
[32m+[m[32m      providers: [[m
[32m+[m[32m        { provide: HttpRequestService, useValue: spyHttpRequestService },[m
[32m+[m[32m      ],[m
[32m+[m[32m    });[m
[32m+[m[32m  });[m
 [m
   beforeEach(async(() => {[m
     TestBed.configureTestingModule({[m
[36m@@ -27,6 +86,11 @@[m [mdescribe('InstructorSearchPageComponent', () => {[m
     fixture.detectChanges();[m
   });[m
 [m
[32m+[m[32m  beforeEach(() => {[m
[32m+[m[32m    const { students }: { students: Student[] } = mockStudents;[m
[32m+[m[32m    coursesWithStudents = component.getCoursesWithStudents(students);[m
[32m+[m[32m  });[m
[32m+[m
   it('should create', () => {[m
     expect(component).toBeTruthy();[m
   });[m
[36m@@ -100,4 +164,122 @@[m [mdescribe('InstructorSearchPageComponent', () => {[m
     fixture.detectChanges();[m
     expect(fixture).toMatchSnapshot();[m
   });[m
[32m+[m
[32m+[m[32m  it('should parse students into courses with sections correctly', () => {[m
[32m+[m[32m    const { students }: { students: Student[] } = mockStudents;[m
[32m+[m
[32m+[m[32m    // Number of courses should match[m
[32m+[m[32m    expect(coursesWithStudents.length).toEqual([m
[32m+[m[32m        Array.from(new Set(students.map((s: Student) => s.courseId))).length,[m
[32m+[m[32m    );[m
[32m+[m
[32m+[m[32m    // Number of sections in a course should match[m
[32m+[m[32m    expect([m
[32m+[m[32m        Array.from([m
[32m+[m[32m            new Set([m
[32m+[m[32m                coursesWithStudents[m
[32m+[m[32m                    .filter((t: SearchStudentsListRowTable) => t.courseId === students[0].courseId)[0][m
[32m+[m[32m                    .students.map((studentModel: StudentListRowModel) => studentModel.student.sectionName),[m
[32m+[m[32m            ),[m
[32m+[m[32m        ).length,[m
[32m+[m[32m    ).toEqual([m
[32m+[m[32m        Array.from([m
[32m+[m[32m            new Set([m
[32m+[m[32m                students[m
[32m+[m[32m                    .filter((s: Student) => s.courseId === students[0].courseId)[m
[32m+[m[32m                    .map((s: Student) => s.sectionName),[m
[32m+[m[32m            ),[m
[32m+[m[32m        ).length,[m
[32m+[m[32m    );[m
[32m+[m
[32m+[m[32m    // Number of students in a section should match[m
[32m+[m[32m    expect([m
[32m+[m[32m        coursesWithStudents[m
[32m+[m[32m            .filter((t: SearchStudentsListRowTable) => t.courseId === students[0].courseId)[0][m
[32m+[m[32m            .students.filter((s: StudentListRowModel) => s.student.sectionName === students[0].sectionName)[m
[32m+[m[32m            .length,[m
[32m+[m[32m    ).toEqual([m
[32m+[m[32m        students.filter((s: Student) => s.sectionName === students[0].sectionName).length,[m
[32m+[m[32m    );[m
[32m+[m[32m  });[m
[32m+[m
[32m+[m[32m  it('should execute GET when fetching privileges', () => {[m
[32m+[m[32m    spyHttpRequestService.get.mockImplementation((endpoint: string) => {[m
[32m+[m[32m      expect(endpoint).toEqual(ResourceEndpoints.INSTRUCTOR_PRIVILEGE);[m
[32m+[m[32m      return of<InstructorPrivilege>({[m
[32m+[m[32m        canModifyCourse: true,[m
[32m+[m[32m        canModifySession: true,[m
[32m+[m[32m        canModifyStudent: true,[m
[32m+[m[32m        canModifyInstructor: true,[m
[32m+[m[32m        canViewStudentInSections: true,[m
[32m+[m[32m        canModifySessionCommentsInSections: true,[m
[32m+[m[32m        canViewSessionInSections: true,[m
[32m+[m[32m        canSubmitSessionInSections: true,[m
[32m+[m[32m      });[m
[32m+[m[32m    });[m
[32m+[m[32m    component.getPrivileges(coursesWithStudents);[m
[32m+[m
[32m+[m[32m    for (const course of coursesWithStudents) {[m
[32m+[m[32m      for (const studentModel of course.students) {[m
[32m+[m[32m        expect(spyHttpRequestService.get).toHaveBeenCalledWith([m
[32m+[m[32m          ResourceEndpoints.INSTRUCTOR_PRIVILEGE,[m
[32m+[m[32m          {[m
[32m+[m[32m            courseid: course.courseId,[m
[32m+[m[32m            sectionname: studentModel.student.sectionName,[m
[32m+[m[32m          },[m
[32m+[m[32m        );[m
[32m+[m[32m      }[m
[32m+[m[32m    }[m
[32m+[m[32m  });[m
[32m+[m
[32m+[m[32m  it('should combine privileges and course data correctly', () => {[m
[32m+[m[32m    const basePrivilege: InstructorPrivilege = {[m
[32m+[m[32m      canModifyCourse: true,[m
[32m+[m[32m      canModifySession: true,[m
[32m+[m[32m      canModifyStudent: true,[m
[32m+[m[32m      canModifyInstructor: true,[m
[32m+[m[32m      canViewStudentInSections: true,[m
[32m+[m[32m      canModifySessionCommentsInSections: true,[m
[32m+[m[32m      canViewSessionInSections: true,[m
[32m+[m[32m      canSubmitSessionInSections: true,[m
[32m+[m[32m    };[m
[32m+[m[32m    const mockPrivilegesArray: InstructorPrivilege[] = [[m
[32m+[m[32m      basePrivilege,[m
[32m+[m[32m      {[m
[32m+[m[32m        ...basePrivilege,[m
[32m+[m[32m        canViewStudentInSections: false,[m
[32m+[m[32m        canModifyStudent: true,[m
[32m+[m[32m      },[m
[32m+[m[32m      {[m
[32m+[m[32m        ...basePrivilege,[m
[32m+[m[32m        canViewStudentInSections: true,[m
[32m+[m[32m        canModifyStudent: false,[m
[32m+[m[32m      },[m
[32m+[m[32m      {[m
[32m+[m[32m        ...basePrivilege,[m
[32m+[m[32m        canViewStudentInSections: false,[m
[32m+[m[32m        canModifyStudent: false,[m
[32m+[m[32m      },[m
[32m+[m[32m    ];[m
[32m+[m[32m    component.combinePrivileges([coursesWithStudents, mockPrivilegesArray]);[m
[32m+[m
[32m+[m[32m    const course1Student1: StudentListRowModel = coursesWithStudents[0].students[0];[m
[32m+[m[32m    expect(course1Student1.isAllowedToViewStudentInSection).toEqual(true);[m
[32m+[m[32m    expect(course1Student1.isAllowedToModifyStudent).toEqual(true);[m
[32m+[m
[32m+[m[32m    const course1Student2: StudentListRowModel = coursesWithStudents[0].students[1];[m
[32m+[m[32m    expect(course1Student2.isAllowedToViewStudentInSection).toEqual(false);[m
[32m+[m[32m    expect(course1Student2.isAllowedToModifyStudent).toEqual(true);[m
[32m+[m
[32m+[m[32m    const course1Student3: StudentListRowModel = coursesWithStudents[0].students[2];[m
[32m+[m[32m    expect(course1Student3.isAllowedToViewStudentInSection).toEqual(true);[m
[32m+[m[32m    expect(course1Student3.isAllowedToModifyStudent).toEqual(false);[m
[32m+[m
[32m+[m[32m    const course2Student1: StudentListRowModel = coursesWithStudents[1].students[0];[m
[32m+[m[32m    expect(course2Student1.isAllowedToViewStudentInSection).toEqual(false);[m
[32m+[m[32m    expect(course2Student1.isAllowedToModifyStudent).toEqual(false);[m
[32m+[m
[32m+[m[32m    expect(mockPrivilegesArray.length).toEqual(0);[m
[32m+[m[32m  });[m
[32m+[m
 });[m
[1mdiff --git a/src/web/app/pages-instructor/instructor-search-page/instructor-search-page.component.ts b/src/web/app/pages-instructor/instructor-search-page/instructor-search-page.component.ts[m
[1mindex 6f0c0cd9cb..5af0bea21d 100644[m
[1m--- a/src/web/app/pages-instructor/instructor-search-page/instructor-search-page.component.ts[m
[1m+++ b/src/web/app/pages-instructor/instructor-search-page/instructor-search-page.component.ts[m
[36m@@ -1,10 +1,16 @@[m
 import { Component, OnInit } from '@angular/core';[m
 import { ActivatedRoute } from '@angular/router';[m
 import { forkJoin, Observable, of } from 'rxjs';[m
[31m-import { finalize } from 'rxjs/operators';[m
[32m+[m[32mimport { finalize, map, mergeMap } from 'rxjs/operators';[m
 [m
 import { InstructorSearchResult, SearchService } from '../../../services/search.service';[m
 import { StatusMessageService } from '../../../services/status-message.service';[m
[32m+[m[32mimport {[m
[32m+[m[32m  CommentSearchResult,[m
[32m+[m[32m  InstructorPrivilege,[m
[32m+[m[32m  Student,[m
[32m+[m[32m} from '../../../types/api-output';[m
[32m+[m[32mimport { StudentListRowModel } from '../../components/student-list/student-list.component';[m
 import { ErrorMessageOutput } from '../../error-message-output';[m
 import { SearchCommentsTable } from './comment-result-table/comment-result-table.component';[m
 import { SearchParams } from './instructor-search-bar/instructor-search-bar.component';[m
[36m@@ -57,16 +63,32 @@[m [mexport class InstructorSearchPageComponent implements OnInit {[m
     this.isSearching = true;[m
     forkJoin([[m
       this.searchParams.isSearchForComments[m
[31m-          ? this.searchService.searchComment(this.searchParams.searchKey)[m
[31m-          : of({}) as Observable<InstructorSearchResult>,[m
[32m+[m[32m          ? this.searchService.searchComment(this.searchParams.searchKey).pipe([m
[32m+[m[32m              map((resp: InstructorSearchResult) => {[m
[32m+[m[32m                return {[m
[32m+[m[32m                  searchCommentTables: this.getSearchCommentsTable(resp.comments),[m
[32m+[m[32m                  searchStudentTables: [],[m
[32m+[m[32m                };[m
[32m+[m[32m              }),[m
[32m+[m[32m          )[m
[32m+[m[32m          : of({}) as Observable<TransformedInstructorSearchResult>,[m
       this.searchParams.isSearchForStudents[m
[31m-          ? this.searchService.searchInstructor(this.searchParams.searchKey)[m
[31m-          : of({}) as Observable<InstructorSearchResult>,[m
[32m+[m[32m          ? this.searchService.searchInstructor(this.searchParams.searchKey).pipe([m
[32m+[m[32m            map((res: InstructorSearchResult) => this.getCoursesWithStudents(res.students)),[m
[32m+[m[32m            mergeMap((coursesWithStudents: SearchStudentsListRowTable[]) =>[m
[32m+[m[32m                forkJoin([[m
[32m+[m[32m                  of(coursesWithStudents),[m
[32m+[m[32m                  this.getPrivileges(coursesWithStudents),[m
[32m+[m[32m                ]),[m
[32m+[m[32m            ),[m
[32m+[m[32m            map((res: [SearchStudentsListRowTable[], InstructorPrivilege[]]) => this.combinePrivileges(res)),[m
[32m+[m[32m          )[m
[32m+[m[32m          : of({}) as Observable<TransformedInstructorSearchResult>,[m
     ]).pipe([m
         finalize(() => this.isSearching = false),[m
[31m-    ).subscribe((resp: InstructorSearchResult[]) => {[m
[31m-      this.commentTables = resp[0].searchCommentsTables;[m
[31m-      const searchStudentsTable: SearchStudentsListRowTable[] = resp[1].searchStudentsTables;[m
[32m+[m[32m    ).subscribe((resp: TransformedInstructorSearchResult[]) => {[m
[32m+[m[32m      this.commentTables = resp[0].searchCommentTables;[m
[32m+[m[32m      const searchStudentsTable: SearchStudentsListRowTable[] = resp[1].searchStudentTables;[m
       const hasStudents: boolean = !!([m
           searchStudentsTable && searchStudentsTable.length[m
       );[m
[36m@@ -84,4 +106,85 @@[m [mexport class InstructorSearchPageComponent implements OnInit {[m
       this.statusMessageService.showErrorToast(resp.error.message);[m
     });[m
   }[m
[32m+[m
[32m+[m[32m  private getSearchCommentsTable(searchResults: CommentSearchResult[]): SearchCommentsTable[] {[m
[32m+[m[32m    return searchResults.map((res: CommentSearchResult) => ({[m
[32m+[m[32m      feedbackSession: res.feedbackSession,[m
[32m+[m[32m      questions: res.questions,[m
[32m+[m[32m    }));[m
[32m+[m[32m  }[m
[32m+[m
[32m+[m[32m  getCoursesWithStudents(students: Student[]): SearchStudentsListRowTable[] {[m
[32m+[m[32m    const distinctCourses: string[] = Array.from([m
[32m+[m[32m        new Set(students.map((s: Student) => s.courseId)),[m
[32m+[m[32m    );[m
[32m+[m[32m    const coursesWithStudents: SearchStudentsListRowTable[] = distinctCourses.map([m
[32m+[m[32m        (courseId: string) => ({[m
[32m+[m[32m          courseId,[m
[32m+[m[32m          students: Array.from([m
[32m+[m[32m              new Set([m
[32m+[m[32m                  students[m
[32m+[m[32m                      .filter((s: Student) => s.courseId === courseId),[m
[32m+[m[32m              ),[m
[32m+[m[32m          ).map((s: Student) => ({[m
[32m+[m[32m            student: s,[m
[32m+[m[32m            isAllowedToViewStudentInSection: false,[m
[32m+[m[32m            isAllowedToModifyStudent: false,[m
[32m+[m[32m          })),[m
[32m+[m[32m        }),[m
[32m+[m[32m    );[m
[32m+[m
[32m+[m[32m    return coursesWithStudents;[m
[32m+[m[32m  }[m
[32m+[m
[32m+[m[32m  getPrivileges([m
[32m+[m[32m      coursesWithStudents: SearchStudentsListRowTable[],[m
[32m+[m[32m  ): Observable<InstructorPrivilege[]> {[m
[32m+[m[32m    if (coursesWithStudents.length === 0) {[m
[32m+[m[32m      return of([]);[m
[32m+[m[32m    }[m
[32m+[m[32m    const privileges: Observable<InstructorPrivilege>[] = [];[m
[32m+[m[32m    coursesWithStudents.forEach((course: SearchStudentsListRowTable) => {[m
[32m+[m[32m      const sectionToPrivileges: Record<string, Observable<InstructorPrivilege>> = {};[m
[32m+[m[32m      Array.from([m
[32m+[m[32m          new Set(course.students.map((studentModel: StudentListRowModel) => studentModel.student.sectionName)),[m
[32m+[m[32m      ).forEach((section: string) => {[m
[32m+[m[32m        sectionToPrivileges[section] = this.searchService.searchInstructorPrivilege(course.courseId, section);[m
[32m+[m[32m      });[m
[32m+[m[32m      course.students.forEach((studentModel: StudentListRowModel) =>[m
[32m+[m[32m          privileges.push(sectionToPrivileges[studentModel.student.sectionName]),[m
[32m+[m[32m      );[m
[32m+[m[32m    });[m
[32m+[m[32m    return forkJoin(privileges);[m
[32m+[m[32m  }[m
[32m+[m
[32m+[m[32m  combinePrivileges([m
[32m+[m[32m      [coursesWithStudents, privileges]: [SearchStudentsListRowTable[], InstructorPrivilege[]],[m
[32m+[m[32m  ): TransformedInstructorSearchResult {[m
[32m+[m[32m    /**[m
[32m+[m[32m     * Pop the privilege objects one at a time and attach them to the results. This is possible[m
[32m+[m[32m     * because `forkJoin` guarantees that the `InstructorPrivilege` results are returned in the[m
[32m+[m[32m     * same order the requests were made.[m
[32m+[m[32m     */[m
[32m+[m[32m    for (const course of coursesWithStudents) {[m
[32m+[m[32m      for (const studentModel of course.students) {[m
[32m+[m[32m        const sectionPrivileges: InstructorPrivilege | undefined = privileges.shift();[m
[32m+[m[32m        if (!sectionPrivileges) { continue; }[m
[32m+[m
[32m+[m[32m        studentModel.isAllowedToViewStudentInSection = sectionPrivileges.canViewStudentInSections;[m
[32m+[m[32m        studentModel.isAllowedToModifyStudent = sectionPrivileges.canModifyStudent;[m
[32m+[m[32m      }[m
[32m+[m[32m    }[m
[32m+[m
[32m+[m[32m    return {[m
[32m+[m[32m      searchStudentTables: coursesWithStudents,[m
[32m+[m[32m      searchCommentTables: [],[m
[32m+[m[32m    };[m
[32m+[m[32m  }[m
[32m+[m
[32m+[m[32m}[m
[32m+[m
[32m+[m[32minterface TransformedInstructorSearchResult {[m
[32m+[m[32m  searchCommentTables: SearchCommentsTable[];[m
[32m+[m[32m  searchStudentTables: SearchStudentsListRowTable[];[m
 }[m
[1mdiff --git a/src/web/services/search.service.spec.ts b/src/web/services/search.service.spec.ts[m
[1mindex 332fc30696..bff6ca897b 100644[m
[1m--- a/src/web/services/search.service.spec.ts[m
[1m+++ b/src/web/services/search.service.spec.ts[m
[36m@@ -1,11 +1,6 @@[m
 import { TestBed } from '@angular/core/testing';[m
 [m
 import { HttpClientTestingModule } from '@angular/common/http/testing';[m
[31m-import { of } from 'rxjs';[m
[31m-import { StudentListRowModel } from '../app/components/student-list/student-list.component';[m
