package teammates.storage.entity;

import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import teammates.common.datatransfer.participanttypes.ResponseGiverType;

/**
 * Embeddable value object that identifies a giver (or editor) of a feedback
 * response or comment.
 */
@Embeddable
public class ResponseGiver {

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ResponseGiverType giverType;

    @Column(nullable = false)
    private UUID giverId;

    protected ResponseGiver() {
        // required by Hibernate
    }

    public ResponseGiver(ResponseGiverType giverType, UUID giverId) {
        this.giverType = giverType;
        this.giverId = giverId;
    }

    public ResponseGiverType getGiverType() {
        return giverType;
    }

    public UUID getGiverId() {
        return giverId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ResponseGiver)) {
            return false;
        }
        ResponseGiver other = (ResponseGiver) o;
        return giverType == other.giverType && Objects.equals(giverId, other.giverId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(giverType, giverId);
    }

    @Override
    public String toString() {
        return "ResponseGiver [giverType=" + giverType + ", giverId=" + giverId + "]";
    }
}
