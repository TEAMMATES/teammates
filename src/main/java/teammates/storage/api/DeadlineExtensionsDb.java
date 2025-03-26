package teammates.storage.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.LoadType;
import com.googlecode.objectify.cmd.Query;

import teammates.common.datatransfer.AttributesDeletionQuery;
import teammates.common.datatransfer.attributes.DeadlineExtensionAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.TimeHelper;
import teammates.storage.entity.DeadlineExtension;

/**
 * Handles CRUD operations for deadline extensions.
 *
 * @see DeadlineExtension
 * @see DeadlineExtensionAttributes
 */
public final class DeadlineExtensionsDb extends EntitiesDb<DeadlineExtension, DeadlineExtensionAttributes> {

    private static final DeadlineExtensionsDb instance = new DeadlineExtensionsDb();

    private DeadlineExtensionsDb() {
        // prevent initialization
    }

    public static DeadlineExtensionsDb inst() {
        return instance;
    }

    /**
     * Gets a deadline extension by {@code courseId}, {@code feedbackSessionName},
     * {@code userEmail} and {@code isInstructor}.
     */
    public DeadlineExtensionAttributes getDeadlineExtension(
            String courseId, String feedbackSessionName, String userEmail, boolean isInstructor) {
        assert courseId != null;
        assert feedbackSessionName != null;
        assert userEmail != null;

        return makeAttributesOrNull(getDeadlineExtensionEntity(
                DeadlineExtension.generateId(courseId, feedbackSessionName, userEmail, isInstructor)));
    }

    /**
     * Updates a deadline extension.
     *
     * @return the updated deadline extension
     * @throws InvalidParametersException if the updated deadline extension is not valid
     * @throws EntityDoesNotExistException if the deadline extension cannot be found
     */
    public DeadlineExtensionAttributes updateDeadlineExtension(DeadlineExtensionAttributes.UpdateOptions updateOptions)
            throws InvalidParametersException, EntityDoesNotExistException {
        assert updateOptions != null;

        DeadlineExtension deadlineExtension = getDeadlineExtensionEntity(
                DeadlineExtension.generateId(updateOptions.getCourseId(),
                        updateOptions.getFeedbackSessionName(),
                        updateOptions.getUserEmail(),
                        updateOptions.getIsInstructor()));
        if (deadlineExtension == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT + updateOptions);
        }

        DeadlineExtensionAttributes newAttributes = makeAttributes(deadlineExtension);

        newAttributes.update(updateOptions);
        newAttributes.sanitizeForSaving();

        if (!newAttributes.isValid()) {
            throw new InvalidParametersException(newAttributes.getInvalidityInfo());
        }

        // update only if change
        boolean hasSameAttributes =
                this.<String>hasSameValue(deadlineExtension.getUserEmail(), newAttributes.getUserEmail())
                && this.<Instant>hasSameValue(deadlineExtension.getEndTime(), newAttributes.getEndTime())
                && this.<Boolean>hasSameValue(deadlineExtension.getSentClosingSoonEmail(),
                        newAttributes.getSentClosingSoonEmail());

        if (hasSameAttributes) {
            log.info(String.format(OPTIMIZED_SAVING_POLICY_APPLIED, DeadlineExtension.class.getSimpleName(), updateOptions));
            return newAttributes;
        }

        if (updateOptions.getUserEmail().equals(newAttributes.getUserEmail())) {
            saveEntity(newAttributes.toEntity());
        } else {
            // User email updated. Delete and recreate entity.
            try {
                createEntity(newAttributes);
            } catch (EntityAlreadyExistsException eaee) {
                throw new InvalidParametersException(eaee);
            }
            deleteDeadlineExtension(updateOptions.getCourseId(), updateOptions.getFeedbackSessionName(),
                    updateOptions.getUserEmail(), updateOptions.getIsInstructor());
        }

        return newAttributes;
    }

    /**
     * Updates all deadline extensions of a user in a course with new email.
     */
    public void updateDeadlineExtensionsWithNewEmail(String courseId, String oldEmail,
            String newEmail, boolean isInstructor) throws InvalidParametersException {
        assert courseId != null;
        assert oldEmail != null;
        assert newEmail != null;

        if (oldEmail.equals(newEmail)) {
            // No update necessary
            return;
        }

        List<DeadlineExtension> entitiesToUpdate = load().project()
                .filter("courseId =", courseId)
                .filter("userEmail =", oldEmail)
                .filter("isInstructor =", isInstructor)
                .list();

        for (DeadlineExtension deadlineExtension : entitiesToUpdate) {
            try {
                updateDeadlineExtension(DeadlineExtensionAttributes
                        .updateOptionsBuilder(courseId, deadlineExtension.getFeedbackSessionName(), oldEmail, isInstructor)
                        .withNewEmail(newEmail)
                        .build());
            } catch (EntityDoesNotExistException ednee) {
                // ignore: deadline extension entity already deleted
            }
        }
    }

    private DeadlineExtension getDeadlineExtensionEntity(String id) {
        return load().id(id).now();
    }

    /**
     * Deletes a deadline extension.
     */
    public void deleteDeadlineExtension(
            String courseId, String feedbackSessionName, String userEmail, boolean isInstructor) {
        assert courseId != null;
        assert feedbackSessionName != null;
        assert userEmail != null;

        deleteEntity(Key.create(DeadlineExtension.class,
                DeadlineExtension.generateId(courseId, feedbackSessionName, userEmail, isInstructor)));
    }

    /**
     * Deletes deadline extensions using {@link AttributesDeletionQuery}.
     */
    public void deleteDeadlineExtensions(AttributesDeletionQuery query) {
        assert query != null;
        assert verifyValidDeletionQuery(query);

        Query<DeadlineExtension> entitiesToDelete = load().project().filter("courseId =", query.getCourseId());

        if (query.isFeedbackSessionNamePresent()) {
            entitiesToDelete = entitiesToDelete.filter("feedbackSessionName =", query.getFeedbackSessionName());
        } else if (query.isUserEmailPresent() && query.isIsInstructorPresent()) {
            entitiesToDelete = entitiesToDelete.filter("userEmail =", query.getUserEmail());
            entitiesToDelete = entitiesToDelete.filter("isInstructor =", query.getIsInstructor());
        }

        deleteEntity(entitiesToDelete.keys().list());
    }

    private boolean verifyValidDeletionQuery(AttributesDeletionQuery query) {
        if (!query.isCourseIdPresent()) {
            return false;
        }

        boolean isValidForDeletingAllInCourse = !query.isIsInstructorPresent() && !query.isUserEmailPresent()
                && !query.isFeedbackSessionNamePresent();
        boolean isValidForDeletingAllInSession = !query.isIsInstructorPresent() && !query.isUserEmailPresent()
                && query.isFeedbackSessionNamePresent();
        boolean isValidForDeletingAllForUser = query.isIsInstructorPresent() && query.isUserEmailPresent()
                && !query.isFeedbackSessionNamePresent();

        return isValidForDeletingAllInCourse || isValidForDeletingAllInSession || isValidForDeletingAllForUser;
    }

    /**
     * Gets a list of deadline extensions with endTime coming up soon
     * and possibly need a closing soon email to be sent.
     */
    public List<DeadlineExtensionAttributes> getDeadlineExtensionsPossiblyNeedingClosingSoonEmail() {
        return new ArrayList<>(makeAttributes(getDeadlineExtensionEntitiesPossiblyNeedingClosingSoonEmail()));
    }

    private List<DeadlineExtension> getDeadlineExtensionEntitiesPossiblyNeedingClosingSoonEmail() {
        return load()
                .filter("endTime >=", Instant.now())
                .filter("endTime <=", TimeHelper.getInstantDaysOffsetFromNow(1))
                .filter("sentClosingSoonEmail =", false)
                .list();
    }

    @Override
    LoadType<DeadlineExtension> load() {
        return ofy().load().type(DeadlineExtension.class);
    }

    @Override
    boolean hasExistingEntities(DeadlineExtensionAttributes entityToCreate) {
        Key<DeadlineExtension> keyToFind = Key.create(DeadlineExtension.class, DeadlineExtension.generateId(
                entityToCreate.getCourseId(), entityToCreate.getFeedbackSessionName(),
                entityToCreate.getUserEmail(), entityToCreate.getIsInstructor()));
        return !load().filterKey(keyToFind).keys().list().isEmpty();
    }

    @Override
    DeadlineExtensionAttributes makeAttributes(DeadlineExtension entity) {
        assert entity != null;

        return DeadlineExtensionAttributes.valueOf(entity);
    }

}
