package teammates.sqllogic.core;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.sqlapi.UsersDb;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.storage.sqlentity.User;

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
     * Create an instructor.
     * @return the created instructor
     * @throws InvalidParametersException if the instructor is not valid
     * @throws EntityAlreadyExistsException if the instructor already exists in the database.
     */
    public Instructor createInstructor(Instructor instructor) throws InvalidParametersException, EntityAlreadyExistsException {
        return usersDb.createInstructor(instructor);
    }

    /**
     * Create an student.
     * @return the created student
     * @throws InvalidParametersException if the student is not valid
     * @throws EntityAlreadyExistsException if the student already exists in the database.
     */
    public Student createStudent(Student student) throws InvalidParametersException, EntityAlreadyExistsException {
        return usersDb.createStudent(student);
    }

    /**
     * Gets an instructor by instructor id.
     * @param id of instructor.
     * @return the specified instructor.
     */
    public Instructor geInstructor(Integer id) {
        return usersDb.getInstructor(id);
    }

    /**
     * Gets an student by student id.
     * @param id of student.
     * @return the specified student.
     */
    public Student geStudent(Integer id) {
        return usersDb.getStudent(id);
    }

    /**
     * Updates an instructor or student.
     */
    public <T extends User> T updateUser(T user) throws InvalidParametersException, EntityDoesNotExistException {
        return usersDb.updateUser(user);
    }

    /**
     * Deletes an instructor or student.
     */
    public <T extends User> void deleteUser(T user) {
        usersDb.deleteUser(user);
    }
}
