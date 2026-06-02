import {
  FeedbackConstantSumOptionsResponseDetails,
  FeedbackConstantSumRecipientsResponseDetails,
  FeedbackContributionResponseDetails,
  FeedbackMcqResponseDetails,
  FeedbackMsqResponseDetails,
  FeedbackNumericalScaleResponseDetails,
  FeedbackQuestionType,
  FeedbackRankOptionsResponseDetails,
  FeedbackRankRecipientsResponseDetails,
  FeedbackResponseDetails,
  FeedbackRubricResponseDetails,
  FeedbackTextResponseDetails,
  ResponseOutput,
} from '../api-output';
import { Response } from '../question-statistics.model';

export class ResponseDetailsTypeChecker {
  static isConstSumOptions(d: FeedbackResponseDetails): d is FeedbackConstantSumOptionsResponseDetails {
    return d.questionType === FeedbackQuestionType.CONSTSUM_OPTIONS;
  }

  static isConstSumRecipients(d: FeedbackResponseDetails): d is FeedbackConstantSumRecipientsResponseDetails {
    return d.questionType === FeedbackQuestionType.CONSTSUM_RECIPIENTS;
  }

  static isContrib(d: FeedbackResponseDetails): d is FeedbackContributionResponseDetails {
    return d.questionType === FeedbackQuestionType.CONTRIB;
  }

  static isMcq(d: FeedbackResponseDetails): d is FeedbackMcqResponseDetails {
    return d.questionType === FeedbackQuestionType.MCQ;
  }

  static isMsq(d: FeedbackResponseDetails): d is FeedbackMsqResponseDetails {
    return d.questionType === FeedbackQuestionType.MSQ;
  }

  static isNumscale(d: FeedbackResponseDetails): d is FeedbackNumericalScaleResponseDetails {
    return d.questionType === FeedbackQuestionType.NUMSCALE;
  }

  static isRankOptions(d: FeedbackResponseDetails): d is FeedbackRankOptionsResponseDetails {
    return d.questionType === FeedbackQuestionType.RANK_OPTIONS;
  }

  static isRankRecipients(d: FeedbackResponseDetails): d is FeedbackRankRecipientsResponseDetails {
    return d.questionType === FeedbackQuestionType.RANK_RECIPIENTS;
  }

  static isRubric(d: FeedbackResponseDetails): d is FeedbackRubricResponseDetails {
    return d.questionType === FeedbackQuestionType.RUBRIC;
  }

  static isText(d: FeedbackResponseDetails): d is FeedbackTextResponseDetails {
    return d.questionType === FeedbackQuestionType.TEXT;
  }
}

export class ResponseOutputCaster {
  static constSumOptions(rs: ResponseOutput[]): Response<FeedbackConstantSumOptionsResponseDetails>[] {
    return rs.map((r) => {
      return {
        ...r,
        responseDetails: r.responseDetails as FeedbackConstantSumOptionsResponseDetails,
      };
    });
  }

  static constSumRecipients(rs: ResponseOutput[]): Response<FeedbackConstantSumRecipientsResponseDetails>[] {
    return rs.map((r) => {
      return {
        ...r,
        responseDetails: r.responseDetails as FeedbackConstantSumRecipientsResponseDetails,
      };
    });
  }

  static contrib(rs: ResponseOutput[]): Response<FeedbackContributionResponseDetails>[] {
    return rs.map((r) => {
      return {
        ...r,
        responseDetails: r.responseDetails as FeedbackContributionResponseDetails,
      };
    });
  }

  static mcq(rs: ResponseOutput[]): Response<FeedbackMcqResponseDetails>[] {
    return rs.map((r) => {
      return {
        ...r,
        responseDetails: r.responseDetails as FeedbackMcqResponseDetails,
      };
    });
  }

  static msq(rs: ResponseOutput[]): Response<FeedbackMsqResponseDetails>[] {
    return rs.map((r) => {
      return {
        ...r,
        responseDetails: r.responseDetails as FeedbackMsqResponseDetails,
      };
    });
  }

  static numscale(rs: ResponseOutput[]): Response<FeedbackNumericalScaleResponseDetails>[] {
    return rs.map((r) => {
      return {
        ...r,
        responseDetails: r.responseDetails as FeedbackNumericalScaleResponseDetails,
      };
    });
  }

  static rankOptions(rs: ResponseOutput[]): Response<FeedbackRankOptionsResponseDetails>[] {
    return rs.map((r) => {
      return {
        ...r,
        responseDetails: r.responseDetails as FeedbackRankOptionsResponseDetails,
      };
    });
  }

  static rankRecipients(rs: ResponseOutput[]): Response<FeedbackRankRecipientsResponseDetails>[] {
    return rs.map((r) => {
      return {
        ...r,
        responseDetails: r.responseDetails as FeedbackRankRecipientsResponseDetails,
      };
    });
  }

  static rubric(rs: ResponseOutput[]): Response<FeedbackRubricResponseDetails>[] {
    return rs.map((r) => {
      return {
        ...r,
        responseDetails: r.responseDetails as FeedbackRubricResponseDetails,
      };
    });
  }

  static text(rs: ResponseOutput[]): Response<FeedbackTextResponseDetails>[] {
    return rs.map((r) => {
      return {
        ...r,
        responseDetails: r.responseDetails as FeedbackTextResponseDetails,
      };
    });
  }
}
