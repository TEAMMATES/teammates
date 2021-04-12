package teammates.client.scripts.statistics;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import teammates.common.util.Config;
import teammates.common.util.StringHelper;
import teammates.test.FileHelper;

/**
 * File storage service that saves stats/cache to encrypted/unencrypted files.
 */
public class FileStore {

    private static final String BASE_URI = "src/client/java/teammates/client/scripts/statistics/data/";

    /*
     * Creates the folder that will contain the stored data.
     */
    static {
        new File(BASE_URI).mkdir();
    }

    private static final String COURSE_TO_INSTITUTE_CACHE_FILEPATH = BASE_URI + "CourseToInstituteCache.encrypted";
    private static final String INSTITUTES_STATS_FILEPATH = BASE_URI + "InstitutesStats.encrypted";
    private static final String INSTITUTES_STATS_METADATA_FILEPATH = BASE_URI + "InstitutesStatsMetadata.json";

    private FileStore() {
        // utility class
    }

    /**
     * Gets the cache service.
     *
     * <p>If the cache is persisted to the disk, decrypts and parses it to warm up the cache.
     */
    public static CourseToInstituteCache getCourseToInstituteCacheFromFileIfPossible() throws Exception {
        // parse courseToInstituteCacheFile
        File courseToInstituteCacheFile = new File(COURSE_TO_INSTITUTE_CACHE_FILEPATH);
        CourseToInstituteCache courseToInstituteCache = new CourseToInstituteCache();
        if (courseToInstituteCacheFile.isFile()) {
            courseToInstituteCache = parseEncryptedJsonFile(COURSE_TO_INSTITUTE_CACHE_FILEPATH,
                    jsonReader -> {
                        CourseToInstituteCache cache = new CourseToInstituteCache();
                        jsonReader.beginObject();
                        while (jsonReader.hasNext()) {
                            cache.populate(jsonReader.nextName(), jsonReader.nextString());
                        }
                        jsonReader.endObject();
                        return cache;
                    });
        }

        return courseToInstituteCache;
    }

    /**
     * Encrypts and persists the cache to a file in disk.
     */
    public static void saveCourseToInstituteCacheToFile(CourseToInstituteCache courseToInstituteCache)
            throws Exception {
        saveEncryptedJsonToFile(COURSE_TO_INSTITUTE_CACHE_FILEPATH, courseToInstituteCache.asMap(),
                new TypeToken<Map<String, String>>(){}.getType());
    }

    /**
     * Decrypts and parses the statistics bundle that is saved in the disk.
     */
    public static StatisticsBundle getStatisticsBundleFromFileIfPossible() throws Exception {
        // parse metadata
        StatisticsBundle.InstitutesStatsMetadata metadata = new StatisticsBundle.InstitutesStatsMetadata();
        File metadataFile = new File(INSTITUTES_STATS_METADATA_FILEPATH);
        if (metadataFile.isFile()) {
            metadata = getSerializer()
                    .fromJson(FileHelper.readFile(INSTITUTES_STATS_METADATA_FILEPATH),
                            StatisticsBundle.InstitutesStatsMetadata.class);
        }

        // parse institutesStats
        File institutesStatsFile = new File(INSTITUTES_STATS_FILEPATH);
        Map<String, StatisticsBundle.InstituteStats> institutesStats = new HashMap<>();
        if (institutesStatsFile.isFile()) {
            institutesStats = parseEncryptedJsonFile(INSTITUTES_STATS_FILEPATH,
                    jsonReader -> getSerializer().fromJson(jsonReader,
                        new TypeToken<Map<String, StatisticsBundle.InstituteStats>>(){}.getType()));
        }

        // construct bundle
        StatisticsBundle statisticsBundle = new StatisticsBundle();
        statisticsBundle.setInstitutesStatsMetadata(metadata);
        statisticsBundle.setInstitutesStats(institutesStats);

        return statisticsBundle;
    }

    /**
     * Encrypts and persists the statistics bundle to the disk.
     */
    public static void saveStatisticsBundleToFile(StatisticsBundle statisticsBundle) throws Exception {
        // save metadata
        FileHelper.saveFile(INSTITUTES_STATS_METADATA_FILEPATH,
                getSerializer().toJson(statisticsBundle.getInstitutesStatsMetadata()));

        // save institutesStats
        saveEncryptedJsonToFile(INSTITUTES_STATS_FILEPATH, statisticsBundle.getInstitutesStats(),
                new TypeToken<Map<String, StatisticsBundle.InstituteStats>>(){}.getType());
    }

    private static <T> void saveEncryptedJsonToFile(String fileName, T object, Type typeOfObject) throws Exception {
        SecretKeySpec sks = new SecretKeySpec(StringHelper.hexStringToByteArray(Config.ENCRYPTION_KEY), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, sks, cipher.getParameters());

        try (OutputStream os = Files.newOutputStream(Paths.get(fileName))) {
            CipherOutputStream out = new CipherOutputStream(os, cipher);
            JsonWriter writer = new JsonWriter(new OutputStreamWriter(out));
            getSerializer().toJson(object, typeOfObject, writer);
            writer.close();
            out.close();
        }
    }

    private static <T> T parseEncryptedJsonFile(String fileName, CheckedFunction<JsonReader, T> parser) throws Exception {
        SecretKeySpec sks = new SecretKeySpec(StringHelper.hexStringToByteArray(Config.ENCRYPTION_KEY), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, sks);

        try (InputStream is = Files.newInputStream(Paths.get(fileName))) {
            CipherInputStream in = new CipherInputStream(is, cipher);
            JsonReader reader = new JsonReader(new InputStreamReader(in));
            T result = parser.apply(reader);
            reader.close();
            in.close();
            return result;
        }
    }

    private static Gson getSerializer() {
        return new GsonBuilder()
                .registerTypeAdapter(Instant.class, new InstantAdapter())
                .create();
    }

    /**
     * An adapter for Gson to serialize {@link Instant} type.
     */
    private static class InstantAdapter implements JsonSerializer<Instant>, JsonDeserializer<Instant> {

        @Override
        public JsonElement serialize(Instant instant, Type type, JsonSerializationContext context) {
            synchronized (this) {
                return new JsonPrimitive(DateTimeFormatter.ISO_INSTANT.format(instant));
            }
        }

        @Override
        public Instant deserialize(JsonElement element, Type type, JsonDeserializationContext context) {
            synchronized (this) {
                return Instant.parse(element.getAsString());
            }
        }
    }

    /**
     * Represents a function that accepts one argument and produces a result.
     *
     * <p>Exception can be thrown from the function body.
     *
     * @param <T> the type of the input to the function
     * @param <R> the type of the result of the function
     */
    @FunctionalInterface
    public interface CheckedFunction<T, R> {

        /**
         * Applies this function to the given argument.
         *
         * @param t the function argument
         * @return the function result
         */
        R apply(T t) throws Exception;
    }
}
