package teammates.sqllogic.core;

import java.util.UUID;

import teammates.storage.sqlapi.UsersDb;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;

/**
 * Handles operations related to user (instructor & student).
 *
 * @see User
 * @see UsersDb
 */
public final class UsersLogic {

    private static final UsersLogic instance = new UsersLogic();

    private UsersDb usersDb;

    private UsersLogic() {
        // prevent initialization
    }

    public static UsersLogic inst() {
        return instance;
    }

    void initLogicDependencies(UsersDb usersDb) {
        this.usersDb = usersDb;
    }

    /**
     * Gets instructor associated with {@code id}.
     *
     * @param id    Id of Instructor.
     * @return      Returns Instructor if found else null.
     */
    public Instructor getInstructor(UUID id) {
        assert id != null;

        return usersDb.getInstructor(id);
    }

    /**
     * Gets student associated with {@code id}.
     *
     * @param id    Id of Student.
     * @return      Returns Student if found else null.
     */
    public Student getStudent(UUID id) {
        assert id != null;

        return usersDb.getStudent(id);
    }
}
