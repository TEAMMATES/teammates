import { Injectable } from '@angular/core';
import { StudentListRowModel } from '../app/pages-instructor/student-list/student-list.component';

/**
 * The statistics of a course
 */
export interface CourseStatistics {
  numOfSections: number;
  numOfTeams: number;
  numOfStudents: number;
}

/**
 * Handles the calculation of statistics
 */
@Injectable({
  providedIn: 'root',
})
export class StatisticsCalculatorService {

  constructor() { }

  /**
   * Calculates the statistics for a course from a list of students in the course
   */
  calculateCourseStatistics(studentList: StudentListRowModel[]): CourseStatistics {
    const teams: Set<string> = new Set();
    const sections: Set<string> = new Set();
    studentList.forEach((student: StudentListRowModel) => {
      teams.add(student.team);
      sections.add(student.sectionName);
    });
    return {
      numOfSections: sections.size,
      numOfTeams: teams.size,
      numOfStudents: studentList.length,
    };
  }
}
