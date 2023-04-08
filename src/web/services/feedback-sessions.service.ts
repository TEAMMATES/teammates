import { Injectable } from '@angular/core';
import { forkJoin, Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import {
  InstructorSessionResultSectionType,
} from '../app/pages-instructor/instructor-session-result-page/instructor-session-result-section-type.enum';
import { default as templateSessions } from '../data/template-sessions.json';
import { ResourceEndpoints } from '../types/api-const';
import {
  FeedbackQuestion,
  FeedbackSession,
  FeedbackSessionPublishStatus,
  FeedbackSessions,
  FeedbackSessionStats,
  FeedbackSessionSubmittedGiverSet,
  HasResponses,
  MessageOutput,
  OngoingSessions,
  SessionLinksRecoveryResponse,
  SessionResults,
  Student,
  Students,
} from '../types/api-output';
import {
  FeedbackSessionCreateRequest,
  FeedbackSessionRespondentRemindRequest,
  FeedbackSessionUpdateRequest,
  Intent,
} from '../types/api-request';
import { HttpRequestService } from './http-request.service';
import { SessionResultCsvService } from './session-result-csv.service';
import { StudentService } from './student.service';

/**
 * A template session.
 */
export interface TemplateSession {
  name: string;
  questions: FeedbackQuestion[];
}

/**
 * Handles sessions related logic provision.
 */
@Injectable({
  providedIn: 'root',
})
export class FeedbackSessionsService {

  constructor(private httpRequestService: HttpRequestService,
              private sessionResultCsvService: SessionResultCsvService,
              private studentService: StudentService) {
  }

  /**
   * Gets template sessions.
   */
  getTemplateSessions(): TemplateSession[] {
    return templateSessions as any;
  }

  /**
   * Retrieves a feedback session by calling API.
   */
  getFeedbackSession(queryParams: {
    courseId: string,
    feedbackSessionName: string,
    intent: Intent,
    key?: string,
    moderatedPerson?: string,
    previewAs?: string,
  }): Observable<FeedbackSession> {
    // load feedback session
    const paramMap: Record<string, string> = {
      intent: queryParams.intent,
      courseid: queryParams.courseId,
      fsname: queryParams.feedbackSessionName,
    };

    if (queryParams.key) {
      paramMap['key'] = queryParams.key;
    }

    if (queryParams.moderatedPerson) {
      paramMap['moderatedperson'] = queryParams.moderatedPerson;
    }

    if (queryParams.previewAs) {
      paramMap['previewas'] = queryParams.previewAs;
    }

    return this.httpRequestService.get(ResourceEndpoints.SESSION, paramMap);
  }

  /**
   * Creates a feedback session by calling API.
   */
  createFeedbackSession(courseId: string, request: FeedbackSessionCreateRequest): Observable<FeedbackSession> {
    const paramMap: Record<string, string> = { courseid: courseId };
    return this.httpRequestService.post(ResourceEndpoints.SESSION, paramMap, request);
  }

  /**
   * Updates a feedback session by calling API.
   */
  updateFeedbackSession(courseId: string, feedbackSessionName: string, request: FeedbackSessionUpdateRequest,
    isNotifyDeadlines: boolean = false):
      Observable<FeedbackSession> {
    const paramMap: Record<string, string> = {
      courseid: courseId,
      fsname: feedbackSessionName,
      notifydeadlines: String(isNotifyDeadlines),
    };
    return this.httpRequestService.put(ResourceEndpoints.SESSION, paramMap, request);
  }

  /**
   * Deletes a feedback session by calling API.
   */
  deleteFeedbackSession(courseId: string, feedbackSessionName: string): Observable<FeedbackSession> {
    const paramMap: Record<string, string> = { courseid: courseId, fsname: feedbackSessionName };
    return this.httpRequestService.delete(ResourceEndpoints.SESSION, paramMap);
  }

  /**
   * Gets all ongoing session by calling API.
   */
  getOngoingSessions(startTime: number, endTime: number): Observable<OngoingSessions> {
    const paramMap: Record<string, string> = {
      starttime: String(startTime),
      endtime: String(endTime),
    };
    return this.httpRequestService.get(ResourceEndpoints.SESSIONS_ONGOING, paramMap);
  }

  /**
   * Gets all sessions for the instructor by calling API.
   */
  getFeedbackSessionsForInstructor(courseId?: string): Observable<FeedbackSessions> {

    let paramMap: Record<string, string>;
    if (courseId) {
      paramMap = {
        entitytype: 'instructor',
        courseid: courseId,
      };
    } else {
      paramMap = {
        entitytype: 'instructor',
        isinrecyclebin: 'false',
      };
    }

    return this.httpRequestService.get(ResourceEndpoints.SESSIONS, paramMap);
  }

  /**
   * Gets all sessions in the recycle bin for the instructor by calling API.
   */
  getFeedbackSessionsInRecycleBinForInstructor(): Observable<FeedbackSessions> {

    const paramMap: Record<string, string> = {
      entitytype: 'instructor',
      isinrecyclebin: 'true',
    };

    return this.httpRequestService.get(ResourceEndpoints.SESSIONS, paramMap);
  }

  /**
   * Gets all sessions for the student by calling API.
   */
  getFeedbackSessionsForStudent(entityType: string, courseId?: string): Observable<FeedbackSessions> {

    let paramMap: Record<string, string>;
    if (courseId) {
      paramMap = {
        entitytype: entityType,
        courseid: courseId,
      };
    } else {
      paramMap = {
        entitytype: entityType,
      };
    }

    return this.httpRequestService.get(ResourceEndpoints.SESSIONS, paramMap);
  }

  /**
   * Checks if there are responses for a specific question in a feedback session (request sent by instructor).
   */
  hasResponsesForQuestion(questionId: string): Observable<HasResponses> {
    const paramMap: Record<string, string> = {
      entitytype: 'instructor',
      questionid: questionId,
    };
    return this.httpRequestService.get(ResourceEndpoints.HAS_RESPONSES, paramMap);
  }

  /**
   * Checks if there is response of a student for a feedback session (request sent by student).
   */
  hasStudentResponseForFeedbackSession(courseId: string, feedbackSessionName: string): Observable<HasResponses> {
    const paramMap: Record<string, string> = {
      entitytype: 'student',
      courseid: courseId,
      fsname: feedbackSessionName,

    };
    return this.httpRequestService.get(ResourceEndpoints.HAS_RESPONSES, paramMap);
  }

  /**
   * Checks if there is response of a student for an array of feedback sessions (request sent by student).
   */
  hasStudentResponseForAllFeedbackSessionsInCourse(courseId: string): Observable<HasResponses> {
    const paramMap: Record<string, string> = {
      entitytype: 'student',
      courseid: courseId,
    };

    return this.httpRequestService.get(ResourceEndpoints.HAS_RESPONSES, paramMap);
  }

  /**
   * Sends e-mails to remind respondents who have not submitted their feedback.
   */
  remindFeedbackSessionSubmissionForRespondents(
      courseId: string, feedbackSessionName: string, request: FeedbackSessionRespondentRemindRequest)
      : Observable<MessageOutput> {
    const paramMap: Record<string, string> = {
      courseid: courseId,
      fsname: feedbackSessionName,
    };

    return this.httpRequestService.post(ResourceEndpoints.SESSION_REMIND_SUBMISSION, paramMap, request);
  }

  /**
   * Sends e-mails to remind respondents on the published results link.
   */
  remindResultsLinkToRespondents(
      courseId: string, feedbackSessionName: string, request: FeedbackSessionRespondentRemindRequest)
      : Observable<MessageOutput> {
    const paramMap: Record<string, string> = {
      courseid: courseId,
      fsname: feedbackSessionName,
    };

    return this.httpRequestService.post(ResourceEndpoints.SESSION_REMIND_RESULT, paramMap, request);
  }

  /**
   * Gets a set of givers that has given at least one response in the feedback session.
   */
  getFeedbackSessionSubmittedGiverSet(queryParams: { courseId: string, feedbackSessionName: string }):
      Observable<FeedbackSessionSubmittedGiverSet> {
    const paramMap: Record<string, string> = {
      courseid: queryParams.courseId,
      fsname: queryParams.feedbackSessionName,
    };
    return this.httpRequestService.get(ResourceEndpoints.SESSION_SUBMITTED_GIVER_SET, paramMap);
  }

  /**
   * Gets a list of students who have not responded to feedback session.
   */
  getFeedbackSessionNonSubmitterList(courseId: string, feedbackSessionName: string):
    Observable<Student[]> {
    const allStudentsObservable: Observable<Students> = this.studentService.getStudentsFromCourse({
      courseId,
    });
    const studentsWithResponseObservable: Observable<FeedbackSessionSubmittedGiverSet> =
      this.getFeedbackSessionSubmittedGiverSet({
        courseId,
        feedbackSessionName,
      });
    return forkJoin([
      allStudentsObservable,
      studentsWithResponseObservable,
    ]).pipe(map((result: any[]) => {
      const allStudents: Student[] = (result[0] as Students).students;
      const studentEmailsWithResponse: string[] = (result[1] as FeedbackSessionSubmittedGiverSet).giverIdentifiers;
      return allStudents.filter((student: Student) =>
        !studentEmailsWithResponse.includes(student.email));
    },
    ));
  }

  /**
   * Downloads list of non-responders.
   */
  downloadFeedbackSessionNonSubmitterList(courseId: string, feedbackSessionName: string):
    Observable<string> {
    return this.getFeedbackSessionNonSubmitterList(courseId, feedbackSessionName)
      .pipe(map((students: Student[]) =>
        this.sessionResultCsvService.getCsvForNonSubmitterList(students),
      ));
  }

  /**
   * publishes a feedback session.
   */
  publishFeedbackSession(courseId: string, feedbackSessionName: string): Observable<FeedbackSession> {
    const paramMap: Record<string, string> = {
      courseid: courseId,
      fsname: feedbackSessionName,
    };

    return this.httpRequestService.post(ResourceEndpoints.SESSION_PUBLISH, paramMap);
  }

  /**
   * Unpublishes a feedback session.
   */
  unpublishFeedbackSession(courseId: string, feedbackSessionName: string): Observable<FeedbackSession> {
    const paramMap: Record<string, string> = {
      courseid: courseId,
      fsname: feedbackSessionName,
    };

    return this.httpRequestService.delete(ResourceEndpoints.SESSION_PUBLISH, paramMap);
  }

  /**
   * Load session statistics.
   */
  loadSessionStatistics(courseId: string, feedbackSessionName: string): Observable<FeedbackSessionStats> {
    const paramMap: Record<string, string> = {
      courseid: courseId,
      fsname: feedbackSessionName,
    };

    return this.httpRequestService.get(ResourceEndpoints.SESSION_STATS, paramMap);
  }

  /**
   * Download session results.
   */
  downloadSessionResults(courseId: string,
                         feedbackSessionName: string,
                         intent: Intent,
                         indicateMissingResponses: boolean,
                         showStatistics: boolean,
                         questionId?: string,
                         groupBySection?: string,
                         sectionDetail?: InstructorSessionResultSectionType): Observable<string> {
    return this.getFeedbackSessionResults({
      courseId,
      feedbackSessionName,
      intent,
      questionId,
      groupBySection,
    }).pipe(
        map((results: SessionResults) =>
            this.sessionResultCsvService.getCsvForSessionResult(
                results, indicateMissingResponses, showStatistics,
                groupBySection, sectionDetail,
            ),
        ),
    );
  }

  /**
   * Retrieves the results for a feedback session.
   */
  getFeedbackSessionResults(queryParams: {
    courseId: string,
    feedbackSessionName: string,
    intent: Intent,
    questionId?: string,
    groupBySection?: string,
    key?: string,
    sectionByGiverReceiver?: string,
    previewAs?: string,
  }): Observable<SessionResults> {
    const paramMap: Record<string, string> = {
      courseid: queryParams.courseId,
      fsname: queryParams.feedbackSessionName,
      intent: queryParams.intent,
    };

    if (queryParams.questionId) {
      paramMap['questionid'] = queryParams.questionId;
    }

    if (queryParams.groupBySection) {
      paramMap['frgroupbysection'] = queryParams.groupBySection;
    }

    if (queryParams.key) {
      paramMap['key'] = queryParams.key;
    }

    if (queryParams.sectionByGiverReceiver) {
      paramMap['sectionByGiverReceiver'] = queryParams.sectionByGiverReceiver;
    }

    if (queryParams.previewAs) {
      paramMap['previewas'] = queryParams.previewAs;
    }

    return this.httpRequestService.get(ResourceEndpoints.RESULT, paramMap);
  }

  /**
   * Soft delete a session by moving it to the recycle bin.
   */
  moveSessionToRecycleBin(courseId: string, feedbackSessionName: string): Observable<any> {
    const paramMap: Record<string, string> = {
      courseid: courseId,
      fsname: feedbackSessionName,
    };

    return this.httpRequestService.put(ResourceEndpoints.BIN_SESSION, paramMap);
  }

  /**
   * Removes a session from the recycle bin.
   */
  deleteSessionFromRecycleBin(courseId: string, feedbackSessionName: string): Observable<FeedbackSession> {
    const paramMap: Record<string, string> = {
      courseid: courseId,
      fsname: feedbackSessionName,
    };

    return this.httpRequestService.delete(ResourceEndpoints.BIN_SESSION, paramMap);
  }

  sendFeedbackSessionLinkToRecoveryEmail(queryParam: {
    sessionLinksRecoveryEmail: string,
    captchaResponse: string,
  }): Observable<SessionLinksRecoveryResponse> {
    const paramMap: Record<string, string> = {
      studentemail: queryParam.sessionLinksRecoveryEmail,
      captcharesponse: queryParam.captchaResponse,
    };

    return this.httpRequestService.post(ResourceEndpoints.SESSION_LINKS_RECOVERY, paramMap);
  }

  /**
   * Checks if a given feedback session is still open.
   */
  isFeedbackSessionOpen(feedbackSession: FeedbackSession): boolean {
    const date: number = Date.now();
    return date >= feedbackSession.submissionStartTimestamp && date < feedbackSession.submissionEndTimestamp;
  }

  /**
   * Checks if a given feedback session is awaiting.
   */
  isFeedbackSessionAwaiting(feedbackSession: FeedbackSession): boolean {
    const date: number = Date.now();
    return date < feedbackSession.submissionStartTimestamp;
  }

  /**
   * Checks if a given feedback session is published.
   */
  isFeedbackSessionPublished(feedbackSession: FeedbackSession): boolean {
    return feedbackSession.publishStatus === FeedbackSessionPublishStatus.PUBLISHED;
  }

}
