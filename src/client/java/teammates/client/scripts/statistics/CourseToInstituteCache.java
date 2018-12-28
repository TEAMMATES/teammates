package teammates.client.scripts.statistics;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.List;
import java.util.Map;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.googlecode.objectify.Key;

import teammates.common.util.StringHelper;
import teammates.storage.entity.Account;
import teammates.storage.entity.Instructor;

/**
 * An in-memory cache service that provides {@code courseId} to institute name mapping.
 */
public class CourseToInstituteCache {

    private static final String UNKNOWN_INSTITUTE = "Unknown Institute";

    private LoadingCache<String, String> cache;

    public CourseToInstituteCache() {
        cache = CacheBuilder.newBuilder()
                .maximumSize(2000000) // will approximately occupy 100MB memory space
                .build(new CacheLoader<String, String>() {
                    @Override
                    public String load(String courseId) {
                        List<Instructor> instructors =
                                ofy().load().type(Instructor.class).filter("courseId =", courseId).list();

                        for (Instructor instructor : instructors) {
                            if (StringHelper.isEmpty(instructor.getGoogleId())) {
                                continue;
                            }

                            Account account = ofy().load().key(Key.create(Account.class, instructor.getGoogleId())).now();
                            if (account != null && !StringHelper.isEmpty(account.getInstitute())) {
                                return account.getInstitute();
                            }
                        }

                        return UNKNOWN_INSTITUTE;
                    }
                });
    }

    /**
     * Populates the cache with existing courseId to institute mapping.
     */
    public void populate(String courseId, String institute) {
        cache.put(courseId, institute);
    }

    /**
     * Gets the a map that represents the mapping between courseId and institute.
     */
    public Map<String, String> asMap() {
        return cache.asMap();
    }

    /**
     * Gets the institute name associated with the course identified by {@code courseId}.
     *
     * <p>If the mapping cannot be found in the cache, several Datastore queries are issued to find the mapping.
     */
    public String get(String courseId) {
        return cache.getUnchecked(courseId);
    }

}
