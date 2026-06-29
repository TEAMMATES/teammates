/* tslint:disable */
/* eslint-disable */

export interface Account extends ApiOutput {
  accountId: string;
  email: string;
  instructors: Instructor[];
  students: Student[];
}

export interface AccountVerificationRequest extends ApiOutput {
  accountVerificationRequestId: string;
  accountId: string;
  email: string;
  name: string;
  institute: string;
  country: string;
  status: AccountVerificationRequestStatus;
  comments?: string;
  rejectionType?: AccountVerificationRequestRejectionType;
  rejectionAdditionalComments?: string;
  createdDemoCourseAt?: number;
  createdAt: number;
}

export interface AccountVerificationRequests extends ApiOutput {
  accountVerificationRequests: AccountVerificationRequest[];
}

export interface ApiOutput {
}

export interface AuthInfo extends ApiOutput {
  loginUrl: string;
  user?: UserInfo;
  masquerade: boolean;
}

export interface Config extends ApiOutput {
  loginMethods: LoginMethod[];
  frontendUrl: string;
}

export interface ConstsumOptionRow {
  option: string;
  pointsReceived: number[];
  total: number;
  average: number;
}

export interface ConstsumRecipientRow {
  recipientName: string;
  recipientEmail?: string;
  recipientTeam: string;
  isCurrentRecipient: boolean;
  pointsReceived: number[];
  total: number;
  average: number;
  averageExcludingSelf?: number;
}

export interface Course extends ApiOutput {
  courseId: string;
  courseName: string;
  timeZone: string;
  institute: string;
  country: string;
  instituteId: string;
  creationTimestamp: number;
  deletionTimestamp: number;
}

export interface CourseJoinKeyAccess extends ApiOutput {
  decision: CourseJoinKeyAccessDecision;
  message: string;
}

export interface Courses extends ApiOutput {
  courses: CourseView[];
}

export interface CourseSection extends ApiOutput {
  sectionId: string;
  sectionName: string;
}

export interface CourseSections extends ApiOutput {
  sections: CourseSection[];
}

export interface CourseView extends ApiOutput {
  course: Course;
  instructorPermissions?: InstructorCoursePermissions;
}

export interface CourseWideRow {
  teamName: string;
  recipientName: string;
  recipientEmail?: string;
  claimed: number;
  perceived: number;
  diff: number;
  ratingsReceived: number[];
}

export interface DeadlineExtension extends ApiOutput {
  feedbackSessionId: string;
  userId: string;
  userDeadlineExtension: number;
}

export interface DeadlineExtensions extends ApiOutput {
  userDeadlines: { [index: string]: number };
}

export interface EnrollErrorResults {
  studentEmail: string;
  errorMessage: string;
}

export interface EnrollStudents extends ApiOutput {
  studentsData: Students;
  unsuccessfulEnrolls: EnrollErrorResults[];
}

export interface FeedbackConstantSumOptionsQuestionDetails extends FeedbackQuestionDetails {
  constSumOptions: string[];
  pointsPerOption: boolean;
  forceUnevenDistribution: boolean;
  distributePointsFor: string;
  points: number;
  minPoint?: number;
  maxPoint?: number;
}

export interface FeedbackConstantSumOptionsResponseDetails extends FeedbackResponseDetails {
  answers: number[];
}

export interface FeedbackConstantSumRecipientsQuestionDetails extends FeedbackQuestionDetails {
  pointsPerOption: boolean;
  forceUnevenDistribution: boolean;
  distributePointsFor: string;
  points: number;
}

export interface FeedbackConstantSumRecipientsResponseDetails extends FeedbackResponseDetails {
  answers: number[];
}

export interface FeedbackConstsumOptionsStatistics extends FeedbackQuestionResultsStatistics {
  options: ConstsumOptionRow[];
}

export interface FeedbackConstsumRecipientsStatistics extends FeedbackQuestionResultsStatistics {
  rows: ConstsumRecipientRow[];
}

export interface FeedbackContributionCourseWideStatistics extends FeedbackQuestionResultsStatistics {
  rows: CourseWideRow[];
}

export interface FeedbackContributionQuestionDetails extends FeedbackQuestionDetails {
  isZeroSum: boolean;
  isNotSureAllowed: boolean;
}

export interface FeedbackContributionRecipientStatistics extends FeedbackQuestionResultsStatistics {
  myView: RecipientView;
  teamView: RecipientView;
}

export interface FeedbackContributionResponseDetails extends FeedbackResponseDetails {
  answer: number;
}

export interface FeedbackMcqMsqCourseWideStatistics extends FeedbackQuestionResultsStatistics {
  hasAnswers: boolean;
  hasWeights: boolean;
  rows: McqMsqOptionRow[];
  perRecipientRows: McqMsqPerRecipientRow[];
}

export interface FeedbackMcqMsqRecipientStatistics extends FeedbackQuestionResultsStatistics {
  hasAnswers: boolean;
  hasWeights: boolean;
  rows: McqMsqOptionRow[];
}

export interface FeedbackMcqQuestionDetails extends FeedbackQuestionDetails {
  hasAssignedWeights: boolean;
  mcqWeights: number[];
  mcqOtherWeight: number;
  mcqChoices: string[];
  otherEnabled: boolean;
  questionDropdownEnabled: boolean;
  generateOptionsFor: QuestionRecipientType;
}

export interface FeedbackMcqResponseDetails extends FeedbackResponseDetails {
  answer: string;
  isOther: boolean;
  otherFieldContent: string;
}

export interface FeedbackMsqQuestionDetails extends FeedbackQuestionDetails {
  msqChoices: string[];
  otherEnabled: boolean;
  hasAssignedWeights: boolean;
  msqWeights: number[];
  msqOtherWeight: number;
  generateOptionsFor: QuestionRecipientType;
  maxSelectableChoices: number;
  minSelectableChoices: number;
}

export interface FeedbackMsqResponseDetails extends FeedbackResponseDetails {
  answers: string[];
  isOther: boolean;
  otherFieldContent: string;
}

export interface FeedbackNumericalScaleQuestionDetails extends FeedbackQuestionDetails {
  minScale: number;
  maxScale: number;
  step: number;
}

export interface FeedbackNumericalScaleResponseDetails extends FeedbackResponseDetails {
  answer: number;
}

export interface FeedbackNumScaleStatistics extends FeedbackQuestionResultsStatistics {
  rows: NumScaleRecipientRow[];
}

export interface FeedbackQuestion extends ApiOutput {
  feedbackQuestionId: string;
  questionBrief: string;
  questionDescription: string;
  questionDetails: FeedbackQuestionDetails;
  giverType: QuestionGiverType;
  recipientType: QuestionRecipientType;
  numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting;
  customNumberOfEntitiesToGiveFeedbackTo: number;
  questionNumber: number;
  questionType: FeedbackQuestionType;
  showResponsesTo: FeedbackVisibilityType[];
  showGiverNameTo: FeedbackVisibilityType[];
  showRecipientNameTo: FeedbackVisibilityType[];
}

export interface FeedbackQuestionDetails {
  questionType: FeedbackQuestionType;
  questionText: string;
}

export interface FeedbackQuestionRecipient extends ApiOutput {
  name: string;
  identifier: string;
  section: string;
  team: string;
}

export interface FeedbackQuestionRecipients extends ApiOutput {
  recipients: FeedbackQuestionRecipient[];
}

export interface FeedbackQuestionResponses extends ApiOutput {
  questionResponses: { [index: string]: FeedbackResponse[] };
}

export interface FeedbackQuestionResultsStatistics {
  questionType: FeedbackQuestionType;
  statisticsView: FeedbackQuestionResultsStatisticsView;
}

export interface FeedbackQuestions extends ApiOutput {
  questions: FeedbackQuestion[];
}

export interface FeedbackRankOptionsQuestionDetails extends FeedbackRankQuestionDetails {
  options: string[];
}

export interface FeedbackRankOptionsResponseDetails extends FeedbackResponseDetails {
  answers: number[];
}

export interface FeedbackRankOptionsStatistics extends FeedbackQuestionResultsStatistics {
  options: RankOptionsOptionRow[];
}

export interface FeedbackRankQuestionDetails extends FeedbackQuestionDetails {
  minOptionsToBeRanked: number;
  maxOptionsToBeRanked: number;
  areDuplicatesAllowed: boolean;
}

export interface FeedbackRankRecipientsQuestionDetails extends FeedbackRankQuestionDetails {
}

export interface FeedbackRankRecipientsResponseDetails extends FeedbackResponseDetails {
  answer: number;
}

export interface FeedbackRankRecipientsStatistics extends FeedbackQuestionResultsStatistics {
  rows: RankRecipientsRow[];
}

export interface FeedbackResponse extends ApiOutput {
  feedbackResponseId: string;
  giverIdentifier: string;
  recipientIdentifier: string;
  responseDetails: FeedbackResponseDetails;
  giverComment?: string;
}

export interface FeedbackResponseDetails {
  questionType: FeedbackQuestionType;
}

export interface FeedbackResponses extends ApiOutput {
  responses: FeedbackResponse[];
}

export interface FeedbackRubricQuestionDetails extends FeedbackQuestionDetails {
  hasAssignedWeights: boolean;
  rubricWeightsForEachCell: number[][];
  rubricChoices: string[];
  rubricSubQuestions: string[];
  rubricDescriptions: string[][];
}

export interface FeedbackRubricResponseDetails extends FeedbackResponseDetails {
  answer: number[];
}

export interface FeedbackRubricStatistics extends FeedbackQuestionResultsStatistics {
  subQuestions: string[];
  choices: string[];
  hasWeights: boolean;
  rows: RubricSubQuestionRow[];
  rowsExcludeSelf: RubricSubQuestionRow[];
  perRecipientStats: RubricPerRecipientStats[];
}

export interface FeedbackSession extends ApiOutput {
  feedbackSessionId: string;
  courseId: string;
  timeZone: string;
  feedbackSessionName: string;
  instructions: string;
  submissionStartTimestamp: number;
  submissionEndTimestamp: number;
  deletedAtTimestamp?: number;
  sessionVisibleFromTimestamp?: number;
  resultVisibleFromTimestamp?: number;
  gracePeriod: number;
  sessionVisibleSetting: SessionVisibleSetting;
  customSessionVisibleTimestamp?: number;
  responseVisibleSetting: ResponseVisibleSetting;
  customResponseVisibleTimestamp?: number;
  submissionStatus: FeedbackSessionSubmissionStatus;
  publishStatus: FeedbackSessionPublishStatus;
  isClosingSoonEmailEnabled: boolean;
  isPublishedEmailEnabled: boolean;
  createdAtTimestamp: number;
}

export interface FeedbackSessionLog {
  feedbackSessionLogId: string;
  user: User;
  feedbackSessionLogType: FeedbackSessionLogType;
  timestamp: number;
}

export interface FeedbackSessionLogs extends ApiOutput {
  feedbackSessionLogs: { [index: string]: FeedbackSessionLog[] };
}

export interface FeedbackSessions extends ApiOutput {
  feedbackSessions: FeedbackSessionView[];
}

export interface FeedbackSessionStats extends ApiOutput {
  submittedTotal: number;
  expectedTotal: number;
}

export interface FeedbackSessionSubmittedGiverSet extends ApiOutput {
  studentGivers: string[];
  instructorGivers: string[];
  studentNonGivers: string[];
  instructorNonGivers: string[];
}

export interface FeedbackSessionView extends ApiOutput {
  feedbackSession: FeedbackSession;
  instructorPermissions?: InstructorFeedbackSessionPermissions;
  userDeadlineExtension?: number;
}

export interface FeedbackTextQuestionDetails extends FeedbackQuestionDetails {
  recommendedLength?: number;
  shouldAllowRichText: boolean;
}

export interface FeedbackTextResponseDetails extends FeedbackResponseDetails {
  answer: string;
}

export interface HasResponses extends ApiOutput {
  hasResponses: boolean;
  hasResponsesBySession?: { [index: string]: boolean };
}

export interface Institute extends ApiOutput {
  id: string;
  name: string;
  country: string;
}

export interface Institutes extends ApiOutput {
  institutes: Institute[];
}

export interface Instructor extends ApiOutput {
  userId: string;
  courseId: string;
  email: string;
  name: string;
  institute: string;
  courseName: string;
  accountId?: string;
  isDisplayedToStudents?: boolean;
  displayedToStudentsAs?: string;
  role?: InstructorPermissionRole;
  joinState: JoinState;
}

export interface InstructorCoursePermissions extends ApiOutput {
  canModifyCourse: boolean;
  canModifyStudent: boolean;
  canModifyInstructor: boolean;
}

export interface InstructorCourses extends ApiOutput {
  courses: Course[];
  instructorPermissions: { [index: string]: InstructorCoursePermissions };
}

export interface InstructorFeedbackSessionPermissions extends ApiOutput {
  canModifySession: boolean;
  canSubmitSession: boolean;
  canViewSession: boolean;
}

export interface InstructorPermissionSet {
  canModifyCourse: boolean;
  canModifyInstructor: boolean;
  canModifySession: boolean;
  canModifyStudent: boolean;
  canViewSession: boolean;
  canSubmitSession: boolean;
}

export interface InstructorPrivilege extends ApiOutput {
  privileges: InstructorPrivileges;
}

export interface InstructorPrivileges {
  instructorId?: string;
  courseLevel: InstructorPermissionSet;
  sectionLevel: { [index: string]: InstructorPermissionSet };
  sessionLevel: { [index: string]: { [index: string]: InstructorPermissionSet } };
}

export interface Instructors extends ApiOutput {
  instructors: Instructor[];
}

export interface JoinLink extends ApiOutput {
  joinLink: string;
}

export interface McqMsqOptionRow {
  option: string;
  weight?: number;
  count: number;
  percentage: number;
  weightedPercentage?: number;
}

export interface McqMsqPerRecipientRow {
  recipientName: string;
  recipientEmail?: string;
  recipientTeam: string;
  responseCountPerOption: { [index: string]: number };
  total: number;
  average: number;
}

export interface MessageOutput extends ApiOutput {
  message: string;
}

export interface Notification extends ApiOutput {
  notificationId: string;
  startTimestamp: number;
  endTimestamp: number;
  createdAt: number;
  style: NotificationStyle;
  targetUser: NotificationTargetUser;
  title: string;
  message: string;
}

export interface Notifications extends ApiOutput {
  notifications: Notification[];
}

export interface NumScaleRecipientRow {
  recipientName: string;
  recipientEmail?: string;
  recipientTeam: string;
  isCurrentRecipient: boolean;
  average?: number;
  min?: number;
  max?: number;
  averageExcludingSelf?: number;
}

export interface OngoingSession {
  feedbackSessionId: string;
  sessionStatus: string;
  accountId: string;
  startTime: number;
  endTime: number;
  creatorEmail: string;
  courseId: string;
  feedbackSessionName: string;
}

export interface OngoingSessions extends ApiOutput {
  totalOngoingSessions: number;
  totalOpenSessions: number;
  totalClosedSessions: number;
  totalAwaitingSessions: number;
  totalInstitutes: number;
  sessions: { [index: string]: OngoingSession[] };
}

export interface QuestionOutput {
  feedbackQuestion: FeedbackQuestion;
  questionStatistics?: FeedbackQuestionResultsStatistics;
  allResponses: ResponseOutput[];
}

export interface RankOptionsOptionRow {
  option: string;
  ranksReceived: number[];
  overallRank: number;
}

export interface RankRecipientsRow {
  recipientName: string;
  recipientEmail: string;
  recipientTeam: string;
  ranksReceived: number[];
  selfRank: number;
  overallRank: number;
  rankExcludingSelf: number;
  rankInTeam: number;
  rankInTeamExcludingSelf: number;
}

export interface ReadNotification extends ApiOutput {
  readNotificationId: string;
  accountId: string;
  notificationId: string;
}

export interface ReadNotifications extends ApiOutput {
  readNotifications: string[];
}

export interface RecipientView {
  ofMe: number;
  ofOthers: number[];
}

export interface ResponseInstructorComment extends ApiOutput {
  responseInstructorCommentId: string;
  giverId: string;
  commentGiverName: string;
  commentText: string;
  createdAt: number;
}

export interface ResponseOutput {
  isMissingResponse: boolean;
  responseId: string;
  giver: string;
  userIdForModeration?: string;
  giverUserId?: string;
  giverTeamId?: string;
  giverTeam: string;
  giverEmail?: string;
  giverSectionId?: string;
  giverSection: string;
  recipient: string;
  recipientUserId?: string;
  recipientSectionId?: string;
  recipientTeamId?: string;
  recipientTeam: string;
  recipientEmail?: string;
  recipientSection: string;
  responseDetails: FeedbackResponseDetails;
  participantComment?: string;
  instructorComments: ResponseInstructorComment[];
}

export interface RubricChoiceCell {
  percentage: number;
  count: number;
  weight?: number;
}

export interface RubricPerCriterionRow {
  subQuestion: string;
  cells: RubricChoiceCell[];
  total?: number;
  average?: number;
}

export interface RubricPerRecipientStats {
  recipientName: string;
  recipientEmail?: string;
  recipientTeam: string;
  perCriterionRows: RubricPerCriterionRow[];
  overallCells: RubricChoiceCell[];
  overallTotal?: number;
  overallAverage?: number;
  subQuestionAverages: number[];
}

export interface RubricSubQuestionRow {
  subQuestion: string;
  cells: RubricChoiceCell[];
  weightAverage?: number;
}

export interface SessionKeyAccess extends ApiOutput {
  decision: SessionKeyAccessDecision;
  message: string;
}

export interface SessionLinks extends ApiOutput {
  courseJoinLink: string;
  submissionLinks: SessionSubmissionLink[];
  resultsLinks: SessionResultLink[];
}

export interface SessionLinksRecoveryResponse extends ApiOutput {
  message: string;
}

export interface SessionResultLink {
  feedbackSessionId: string;
  name: string;
  submissionStartTimestamp: number;
  submissionEndTimestamp: number;
  timeZone: string;
  url: string;
}

export interface SessionResults extends ApiOutput {
  questions: QuestionOutput[];
}

export interface SessionSubmission extends ApiOutput {
  questions: SessionSubmissionQuestion[];
}

export interface SessionSubmissionLink {
  feedbackSessionId: string;
  name: string;
  submissionStartTimestamp: number;
  submissionEndTimestamp: number;
  timeZone: string;
  submissionStatus: FeedbackSessionSubmissionStatus;
  url: string;
}

export interface SessionSubmissionQuestion extends ApiOutput {
  question: FeedbackQuestion;
  recipients: FeedbackQuestionRecipient[];
  responses: FeedbackResponse[];
}

export interface Student extends ApiOutput {
  userId: string;
  email: string;
  courseId: string;
  name: string;
  teamId: string;
  teamName: string;
  sectionId: string;
  sectionName: string;
  institute: string;
  courseName: string;
  accountId?: string;
  comments?: string;
  joinState?: JoinState;
}

export interface Students extends ApiOutput {
  students: Student[];
}

export interface TimeZones extends ApiOutput {
  version: string;
  offsets: { [index: string]: number };
}

export interface UsageStatistics extends ApiOutput {
  startTime: number;
  numResponses: number;
  numCourses: number;
  numStudents: number;
  numInstructors: number;
  numAccountVerificationRequests: number;
}

export interface UsageStatisticsRange extends ApiOutput {
  result: UsageStatistics[];
}

export interface User extends ApiOutput {
  userId: string;
  email: string;
  courseId: string;
  name: string;
}

export interface UserInfo {
  accountId: string;
  accountEmail: string;
  isAdmin: boolean;
  isInstructor: boolean;
  isStudent: boolean;
  isMaintainer: boolean;
}

export interface UserQuestionOutput {
  feedbackQuestion: FeedbackQuestion;
  questionStatistics?: FeedbackQuestionResultsStatistics;
  hasResponseButNotVisibleForPreview: boolean;
  allResponses: ResponseOutput[];
  responsesToSelf: ResponseOutput[];
  responsesFromSelf: ResponseOutput[];
  otherResponses: ResponseOutput[][];
}

export interface UserSessionResults extends ApiOutput {
  questions: UserQuestionOutput[];
}

export enum AccountVerificationRequestRejectionType {
  ALREADY_VERIFIED = "ALREADY_VERIFIED",
  CANNOT_VERIFY_IDENTITY = "CANNOT_VERIFY_IDENTITY",
  NOT_OFFICIAL_EMAIL = "NOT_OFFICIAL_EMAIL",
  NOT_INSTRUCTOR_ACCOUNT = "NOT_INSTRUCTOR_ACCOUNT",
  OTHERS = "OTHERS",
}

export enum AccountVerificationRequestStatus {
  PENDING = "PENDING",
  REJECTED = "REJECTED",
  APPROVED = "APPROVED",
}

export enum CourseJoinKeyAccessDecision {
  VALID = "VALID",
  ALREADY_JOINED = "ALREADY_JOINED",
  SIGN_IN_REQUIRED = "SIGN_IN_REQUIRED",
  INVALID_KEY = "INVALID_KEY",
}

export enum FeedbackConstantSumDistributePointsType {
  DISTRIBUTE_ALL_UNEVENLY = "All options",
  DISTRIBUTE_SOME_UNEVENLY = "At least some options",
  NONE = "None",
}

export enum FeedbackQuestionResultsStatisticsView {
  COURSE_WIDE = "COURSE_WIDE",
  RECIPIENT = "RECIPIENT",
}

export enum FeedbackQuestionType {
  TEXT = "TEXT",
  MCQ = "MCQ",
  MSQ = "MSQ",
  NUMSCALE = "NUMSCALE",
  CONSTSUM_OPTIONS = "CONSTSUM_OPTIONS",
  CONSTSUM_RECIPIENTS = "CONSTSUM_RECIPIENTS",
  CONTRIB = "CONTRIB",
  RUBRIC = "RUBRIC",
  RANK_OPTIONS = "RANK_OPTIONS",
  RANK_RECIPIENTS = "RANK_RECIPIENTS",
}

export enum FeedbackSessionLogType {
  ACCESS = "ACCESS",
  SUBMISSION = "SUBMISSION",
  VIEW_RESULT = "VIEW_RESULT",
}

export enum FeedbackSessionPublishStatus {
  PUBLISHED = "PUBLISHED",
  NOT_PUBLISHED = "NOT_PUBLISHED",
}

export enum FeedbackSessionSubmissionStatus {
  NOT_VISIBLE = "NOT_VISIBLE",
  VISIBLE_NOT_OPEN = "VISIBLE_NOT_OPEN",
  OPEN = "OPEN",
  GRACE_PERIOD = "GRACE_PERIOD",
  CLOSED = "CLOSED",
}

export enum FeedbackVisibilityType {
  RECIPIENT = "RECIPIENT",
  GIVER_TEAM_MEMBERS = "GIVER_TEAM_MEMBERS",
  RECIPIENT_TEAM_MEMBERS = "RECIPIENT_TEAM_MEMBERS",
  STUDENTS = "STUDENTS",
  INSTRUCTORS = "INSTRUCTORS",
}

export enum InstructorPermissionRole {
  COOWNER = "COOWNER",
  MANAGER = "MANAGER",
  OBSERVER = "OBSERVER",
  TUTOR = "TUTOR",
  CUSTOM = "CUSTOM",
}

export enum JoinState {
  JOINED = "JOINED",
  NOT_JOINED = "NOT_JOINED",
}

export enum LoginMethod {
  GOOGLE = "google",
  DEV_SERVER = "devserver",
}

export enum NotificationStyle {
  PRIMARY = "PRIMARY",
  SECONDARY = "SECONDARY",
  SUCCESS = "SUCCESS",
  DANGER = "DANGER",
  WARNING = "WARNING",
  INFO = "INFO",
  LIGHT = "LIGHT",
  DARK = "DARK",
}

export enum NotificationTargetUser {
  STUDENT = "STUDENT",
  INSTRUCTOR = "INSTRUCTOR",
  GENERAL = "GENERAL",
}

export enum NumberOfEntitiesToGiveFeedbackToSetting {
  CUSTOM = "CUSTOM",
  UNLIMITED = "UNLIMITED",
}

export enum QuestionGiverType {
  SESSION_CREATOR = "SESSION_CREATOR",
  STUDENTS = "STUDENTS",
  INSTRUCTORS = "INSTRUCTORS",
  TEAMS = "TEAMS",
}

export enum QuestionRecipientType {
  SELF = "SELF",
  STUDENTS = "STUDENTS",
  STUDENTS_IN_SAME_SECTION = "STUDENTS_IN_SAME_SECTION",
  STUDENTS_EXCLUDING_SELF = "STUDENTS_EXCLUDING_SELF",
  INSTRUCTORS = "INSTRUCTORS",
  TEAMS = "TEAMS",
  TEAMS_IN_SAME_SECTION = "TEAMS_IN_SAME_SECTION",
  TEAMS_EXCLUDING_SELF = "TEAMS_EXCLUDING_SELF",
  OWN_TEAM = "OWN_TEAM",
  OWN_TEAM_MEMBERS = "OWN_TEAM_MEMBERS",
  OWN_TEAM_MEMBERS_INCLUDING_SELF = "OWN_TEAM_MEMBERS_INCLUDING_SELF",
  NONE = "NONE",
}

export enum ResponseVisibleSetting {
  CUSTOM = "CUSTOM",
  AT_VISIBLE = "AT_VISIBLE",
  LATER = "LATER",
}

export enum SessionKeyAccessDecision {
  ALLOW_UNREGISTERED = "ALLOW_UNREGISTERED",
  ALLOW_SIGNED_IN = "ALLOW_SIGNED_IN",
  SIGN_IN_REQUIRED = "SIGN_IN_REQUIRED",
  SIGN_IN_WITH_ANOTHER_ACCOUNT = "SIGN_IN_WITH_ANOTHER_ACCOUNT",
  INVALID_KEY = "INVALID_KEY",
}

export enum SessionVisibleSetting {
  CUSTOM = "CUSTOM",
  AT_OPEN = "AT_OPEN",
}
