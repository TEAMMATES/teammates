package teammates.test.scenariobuilder;

import teammates.storage.entity.BaseEntity;

/**
 * Base class for all "Given" builders in test scenarios.
 *
 * @param <T> The type of entity being built.
 */
public abstract class GivenBase<T extends BaseEntity> {
    /**
     * The GivenData instance that provides access to shared data and helper methods.
     */
    protected GivenData given;
    /**
     * The entity being built by this builder.
     */
    protected T entity;

    GivenBase(GivenData given) {
        this.given = given;
    }

    /**
     * Ensures that the entity is in a consistent state before being built.
     * This involves setting default values for mandatory relationships if they have
     * not been set explicitly.
     */
    abstract void ensureConsistent();

    /**
     * Builds the entity after ensuring consistency.
     */
    public final T build() {
        ensureConsistent();
        if (!entity.isValid()) {
            throw new AssertionError(entity.getInvalidityInfo());
        }
        return entity;
    }
}
