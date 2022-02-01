package teammates.storage.api;

import teammates.storage.entity.FeedbackResponseStatisticsHour;
import teammates.ui.webapi.FeedbackResponseStatisticsCountHourAction;

/**
 * Handles CRUD operations for FeedbackResponseStatisticsHour.
 *
 * @see FeedbackResponseStatisticsHour
 * @see FeedbackResponseStatisticsCountHourAction
 */
public class FeedbackResponseStatisticsHourDb {
/*     private static final Logger log = Logger.getLogger();

private static final int MAX_KEY_REGENERATION_TRIES = 10;

private static final FeedbackResponseStatisticsHourDb instance = new FeedbackResponseStatisticsHourDb();

private FeedbackResponseStatisticsHourDb() {
    // prevent initialization
}

public static FeedbackResponseStatisticsHourDb inst() {
    return instance;
}

/**
 * Updates a student by {@link StudentAttributes.UpdateOptions}.
 *
 * <p>If the student's email is changed, the student is re-created.
 *
 * @return updated student
 * @throws InvalidParametersException if attributes to update are not valid
 * @throws EntityDoesNotExistException if the student cannot be found
 * @throws EntityAlreadyExistsException if the student cannot be updated
 *         by recreation because of an existent student
 */
    /*
    public StudentAttributes updateStudent(StudentAttributes.UpdateOptions updateOptions)
            throws EntityDoesNotExistException, InvalidParametersException, EntityAlreadyExistsException {
        assert updateOptions != null;

        CourseStudent student = getCourseStudentEntityForEmail(updateOptions.getCourseId(), updateOptions.getEmail());
        if (student == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT + updateOptions);
        }

        StudentAttributes newAttributes = makeAttributes(student);
        newAttributes.update(updateOptions);

        newAttributes.sanitizeForSaving();
        if (!newAttributes.isValid()) {
            throw new InvalidParametersException(newAttributes.getInvalidityInfo());
        }

        boolean isEmailChanged = !student.getEmail().equals(newAttributes.getEmail());

        if (isEmailChanged) {
            newAttributes = createEntity(newAttributes);
            // delete the old student
            deleteStudent(student.getCourseId(), student.getEmail());

            return newAttributes;
        } else {
            // update only if change
            boolean hasSameAttributes =
                    this.<String>hasSameValue(student.getName(), newAttributes.getName())
                    && this.<String>hasSameValue(student.getComments(), newAttributes.getComments())
                    && this.<String>hasSameValue(student.getGoogleId(), newAttributes.getGoogleId())
                    && this.<String>hasSameValue(student.getTeamName(), newAttributes.getTeam())
                    && this.<String>hasSameValue(student.getSectionName(), newAttributes.getSection());
            if (hasSameAttributes) {
                log.info(String.format(OPTIMIZED_SAVING_POLICY_APPLIED, CourseStudent.class.getSimpleName(), updateOptions));
                return newAttributes;
            }

            student.setName(newAttributes.getName());
            student.setComments(newAttributes.getComments());
            student.setGoogleId(newAttributes.getGoogleId());
            student.setTeamName(newAttributes.getTeam());
            student.setSectionName(newAttributes.getSection());

            saveEntity(student);

            return makeAttributes(student);
        }
    }

    private CourseStudent getCourseStudentEntityForEmail(String courseId, String email) {
        return load().id(CourseStudent.generateId(email, courseId)).now();
    }

    private List<CourseStudent> getAllCourseStudentEntitiesForEmail(String email) {
        return load().filter("email =", email).list();
    }

    private CourseStudent getCourseStudentEntityForRegistrationKey(String registrationKey) {
        List<CourseStudent> studentList = load().filter("registrationKey =", registrationKey).list();

        // If registration key detected is not unique, something is wrong
        if (studentList.size() > 1) {
            log.severe("Duplicate registration keys detected for: "
                    + studentList.stream().map(s -> s.getUniqueId()).collect(Collectors.joining(", ")));
        }

        if (studentList.isEmpty()) {
            return null;
        }

        return studentList.get(0);
    }

    private Query<CourseStudent> getCourseStudentsForCourseQuery(String courseId) {
        return load().filter("courseId =", courseId);
    }

    private List<CourseStudent> getCourseStudentEntitiesForCourse(String courseId) {
        return getCourseStudentsForCourseQuery(courseId).list();
    }

    private Query<CourseStudent> getCourseStudentsForGoogleIdQuery(String googleId) {
        return load().filter("googleId =", googleId);
    }

    private List<CourseStudent> getCourseStudentEntitiesForGoogleId(String googleId) {
        return getCourseStudentsForGoogleIdQuery(googleId).list();
    }

    private List<CourseStudent> getCourseStudentEntitiesForTeam(String teamName, String courseId) {
        return load()
                .filter("teamName =", teamName)
                .filter("courseId =", courseId)
                .list();
    }

    private List<CourseStudent> getCourseStudentEntitiesForSection(String sectionName, String courseId) {
        return load()
                .filter("sectionName =", sectionName)
                .filter("courseId =", courseId)
                .list();
    }

    @Override
    LoadType<CourseStudent> load() {
        return ofy().load().type(CourseStudent.class);
    }

    @Override
    boolean hasExistingEntities(StudentAttributes entityToCreate) {
        return !load()
                .filterKey(Key.create(CourseStudent.class,
                        CourseStudent.generateId(entityToCreate.getEmail(), entityToCreate.getCourse())))
                .list()
                .isEmpty();
    }

    @Override
    StudentAttributes makeAttributes(CourseStudent entity) {
        assert entity != null;

        return StudentAttributes.valueOf(entity);
    }

    @Override
    CourseStudent convertToEntityForSaving(StudentAttributes attributes) throws EntityAlreadyExistsException {
        int numTries = 0;
        while (numTries < MAX_KEY_REGENERATION_TRIES) {
            CourseStudent student = attributes.toEntity();
            Key<CourseStudent> existingStudent =
                    load().filter("registrationKey =", student.getRegistrationKey()).keys().first().now();
            if (existingStudent == null) {
                return student;
            }
            numTries++;
        }
        log.severe("Failed to generate new registration key for student after " + MAX_KEY_REGENERATION_TRIES + " tries");
        throw new EntityAlreadyExistsException("Unable to create new student");
    }
    */
}
