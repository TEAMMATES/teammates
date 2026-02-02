import { Response } from '../../app/components/question-types/question-statistics/question-statistics';
import {
  FeedbackConstantSumResponseDetails,
  FeedbackContributionResponseDetails,
  FeedbackMcqResponseDetails,
  FeedbackMsqResponseDetails,
  FeedbackNumericalScaleResponseDetails,
  FeedbackRankOptionsResponseDetails,
  FeedbackRankRecipientsResponseDetails,
  FeedbackResponseDetails,
  FeedbackRubricResponseDetails,
  FeedbackTextResponseDetails,
  ResponseOutput,
} from '../api-output';

export class ResponseDetailsCaster {
  static constSum(d: FeedbackResponseDetails): FeedbackConstantSumResponseDetails {
    return d as FeedbackConstantSumResponseDetails;
  }

  static contrib(d: FeedbackResponseDetails): FeedbackContributionResponseDetails {
    return d as FeedbackContributionResponseDetails;
  }

  static mcq(d: FeedbackResponseDetails): FeedbackMcqResponseDetails {
    return d as FeedbackMcqResponseDetails;
  }

  static msq(d: FeedbackResponseDetails): FeedbackMsqResponseDetails {
    return d as FeedbackMsqResponseDetails;
  }

  static numscale(d: FeedbackResponseDetails): FeedbackNumericalScaleResponseDetails {
    return d as FeedbackNumericalScaleResponseDetails;
  }

  static rankOptions(d: FeedbackResponseDetails): FeedbackRankOptionsResponseDetails {
    return d as FeedbackRankOptionsResponseDetails;
  }

  static rankRecipients(d: FeedbackResponseDetails): FeedbackRankRecipientsResponseDetails {
    return d as FeedbackRankRecipientsResponseDetails;
  }

  static rubric(d: FeedbackResponseDetails): FeedbackRubricResponseDetails {
    return d as FeedbackRubricResponseDetails;
  }

  static text(d: FeedbackResponseDetails): FeedbackTextResponseDetails {
    return d as FeedbackTextResponseDetails;
  }
}

export class ResponseOutputCaster {
  static constSum(rs: ResponseOutput[]): Response<FeedbackConstantSumResponseDetails>[] {
    return rs.map((r) => {
      return {
        ...r,
        responseDetails: r.responseDetails as FeedbackConstantSumResponseDetails,
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
