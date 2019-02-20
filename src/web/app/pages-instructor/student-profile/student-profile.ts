import { StudentProfileUpdateRequest } from '../../../types/api-request';
import { Gender } from '../../../types/gender';

/**
 * Contains a student's profile
 */
export interface StudentProfile {
  shortName: string;
  email: string;
  institute: string;
  nationality: string;
  gender: Gender;
  moreInfo: string;
  pictureKey: string;
}

/**
 * Contains a student's details.
 */
export interface StudentDetails {
  studentProfile: StudentProfileUpdateRequest;
  name: string;
  requestId: string;
}
