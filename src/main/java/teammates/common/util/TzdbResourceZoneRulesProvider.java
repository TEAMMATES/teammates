package teammates.common.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.StreamCorruptedException;
import java.lang.reflect.Method;
import java.time.zone.ZoneOffsetTransition;
import java.time.zone.ZoneOffsetTransitionRule;
import java.time.zone.ZoneRules;
import java.time.zone.ZoneRulesException;
import java.time.zone.ZoneRulesProvider;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Loads time-zone rules for 'TZDB'. Modified from {@link java.time.zone.TzdbZoneRulesProvider}.
 * Reads the file 'tzdb.dat' from resources instead of the JRE library directory.
 */
public final class TzdbResourceZoneRulesProvider extends ZoneRulesProvider {

    /** Type for ZoneRules. */
    private static final byte ZRULES = 1;
    /** Type for ZoneOffsetTransition. */
    private static final byte ZOT = 2;
    /** Type for ZoneOffsetTransitionRule. */
    private static final byte ZOTRULE = 3;

    /**
     * All the regions that are available.
     */
    private List<String> regionIds;
    /**
     * Version Id of this tzdb rules.
     */
    private String versionId;
    /**
     * Region to rules mapping.
     */
    private final Map<String, Object> regionToRules = new ConcurrentHashMap<>();

    /**
     * Creates an instance.
     * Created by the {@code ServiceLoader}.
     *
     * @throws ZoneRulesException if unable to load
     */
    public TzdbResourceZoneRulesProvider() {
        try {
            try (DataInputStream dis = new DataInputStream(
                    new BufferedInputStream(FileHelper.getResourceAsStream("tzdb.dat")))) {
                load(dis);
            }
        } catch (Exception ex) {
            throw new ZoneRulesException("Unable to load TZDB time-zone rules from resource tzdb.dat", ex);
        }
    }

    @Override
    protected Set<String> provideZoneIds() {
        return new HashSet<>(regionIds);
    }

    @Override
    protected ZoneRules provideRules(String zoneId, boolean forCaching) {
        // forCaching flag is ignored because this is not a dynamic provider
        Object obj = regionToRules.get(zoneId);
        if (obj == null) {
            throw new ZoneRulesException("Unknown time-zone ID: " + zoneId);
        }
        try {
            if (obj instanceof byte[]) {
                byte[] bytes = (byte[]) obj;
                DataInputStream dis = new DataInputStream(new ByteArrayInputStream(bytes));
                obj = serRead(dis);
                regionToRules.put(zoneId, obj);
            }
            return (ZoneRules) obj;
        } catch (Exception ex) {
            throw new ZoneRulesException("Invalid binary time-zone data: TZDB:" + zoneId + ", version: " + versionId, ex);
        }
    }

    @Override
    protected NavigableMap<String, ZoneRules> provideVersions(String zoneId) {
        TreeMap<String, ZoneRules> map = new TreeMap<>();
        ZoneRules rules = getRules(zoneId, false);
        if (rules != null) {
            map.put(versionId, rules);
        }
        return map;
    }

    /**
     * Loads the rules from a DateInputStream, often in a jar file.
     *
     * @param dis  the DateInputStream to load, not null
     * @throws Exception if an error occurs
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // follow original method signature
    private void load(DataInputStream dis) throws Exception {
        if (dis.readByte() != 1) {
            throw new StreamCorruptedException("File format not recognised");
        }
        // group
        String groupId = dis.readUTF();
        if (!"TZDB".equals(groupId)) {
            throw new StreamCorruptedException("File format not recognised");
        }
        // versions
        int versionCount = dis.readShort();
        for (int i = 0; i < versionCount; i++) {
            versionId = dis.readUTF();
        }
        // regions
        int regionCount = dis.readShort();
        String[] regionArray = new String[regionCount];
        for (int i = 0; i < regionCount; i++) {
            regionArray[i] = dis.readUTF();
        }
        regionIds = Arrays.asList(regionArray);
        // rules
        int ruleCount = dis.readShort();
        Object[] ruleArray = new Object[ruleCount];
        for (int i = 0; i < ruleCount; i++) {
            byte[] bytes = new byte[dis.readShort()];
            dis.readFully(bytes);
            ruleArray[i] = bytes;
        }
        // link version-region-rules
        for (int i = 0; i < versionCount; i++) {
            int versionRegionCount = dis.readShort();
            regionToRules.clear();
            for (int j = 0; j < versionRegionCount; j++) {
                String region = regionArray[dis.readShort()];
                Object rule = ruleArray[dis.readShort() & 0xffff];
                regionToRules.put(region, rule);
            }
        }
    }

    @Override
    public String toString() {
        return "TZDB[" + versionId + "]";
    }

    /**
     * Modified from {@link java.time.zone.Ser#read}.
     */
    private static Object serRead(DataInput in) throws IOException {
        byte type = in.readByte();
        switch (type) {
        case ZRULES:
            return invokeReadExternal(ZoneRules.class, in); // ZoneRules.readExternal(in)
        case ZOT:
            return invokeReadExternal(ZoneOffsetTransition.class, in); // ZoneOffsetTransition.readExternal(in)
        case ZOTRULE:
            return invokeReadExternal(ZoneOffsetTransitionRule.class, in); // ZoneOffsetTransitionRule.readExternal(in)
        default:
            throw new StreamCorruptedException("Unknown serialized type");
        }
    }

    private static Object invokeReadExternal(Class<?> cls, DataInput in) throws IOException {
        try {
            Method m = cls.getDeclaredMethod("readExternal", DataInput.class);
            m.setAccessible(true);
            return m.invoke(null, in);
        } catch (ReflectiveOperationException e) {
            throw new IOException(e);
        }
    }
}
