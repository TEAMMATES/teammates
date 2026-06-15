package teammates.ui.output;

import java.util.List;

import teammates.storage.entity.Institute;

/**
 * Output data for a list of institutes.
 */
public class InstitutesData implements ApiOutput {
    private final List<InstituteData> institutes;

    public InstitutesData(List<Institute> institutes) {
        this.institutes = institutes.stream()
                .map(InstituteData::new)
                .toList();
    }

    public List<InstituteData> getInstitutes() {
        return institutes;
    }
}
