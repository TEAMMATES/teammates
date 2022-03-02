package teammates.common.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.StreamCorruptedException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.zone.ZoneOffsetTransition;
import java.time.zone.ZoneOffsetTransitionRule;
import java.time.zone.ZoneRules;
import java.time.zone.ZoneRulesException;
import java.time.zone.ZoneRulesProvider;
import java.util.ArrayList;
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
        NavigableMap<String, ZoneRules> map = new TreeMap<>();
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
     * @throws IOException if an error occurs
     */
    private void load(DataInputStream dis) throws IOException {
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
     * Modified from {@link java.time.zone.Ser#read(DataInput)}.
     */
    private static Object serRead(DataInput in) throws IOException {
        byte type = in.readByte();
        switch (type) {
        case ZRULES:
            return zrulesReadExternal(in); // ZoneRules.readExternal(in)
        case ZOT:
            return zotReadExternal(in); // ZoneOffsetTransition.readExternal(in)
        case ZOTRULE:
            return zotruleReadExternal(in); // ZoneOffsetTransitionRule.readExternal(in)
        default:
            throw new StreamCorruptedException("Unknown serialized type");
        }
    }

    /**
     * Modified from {@link java.time.zone.ZoneRules#readExternal(DataInput)}.
     */
    private static ZoneRules zrulesReadExternal(DataInput in) throws IOException {
        int stdSize = in.readInt();
        long[] stdTrans = new long[stdSize];
        for (int i = 0; i < stdSize; i++) {
            stdTrans[i] = serReadEpochSec(in);
        }
        ZoneOffset[] stdOffsets = new ZoneOffset[stdSize + 1];
        for (int i = 0; i < stdOffsets.length; i++) {
            stdOffsets[i] = serReadOffset(in);
        }
        int savSize = in.readInt();
        long[] savTrans = new long[savSize];
        for (int i = 0; i < savSize; i++) {
            savTrans[i] = serReadEpochSec(in);
        }
        ZoneOffset[] savOffsets = new ZoneOffset[savSize + 1];
        for (int i = 0; i < savOffsets.length; i++) {
            savOffsets[i] = serReadOffset(in);
        }
        int ruleSize = in.readByte();
        ZoneOffsetTransitionRule[] rules = new ZoneOffsetTransitionRule[ruleSize];
        for (int i = 0; i < ruleSize; i++) {
            rules[i] = zotruleReadExternal(in);
        }
        // return new ZoneRules(stdTrans, stdOffsets, savTrans, savOffsets, rules);

        List<ZoneOffsetTransition> standardOffsetTransitionList = new ArrayList<>();
        List<ZoneOffsetTransition> transitionList = new ArrayList<>();
        for (int i = 0; i < stdTrans.length; i++) {
            ZoneOffset zofBefore = stdOffsets[i];
            ZoneOffset zofAfter = stdOffsets[i + 1];
            ZoneOffsetTransition zot = ZoneOffsetTransition.of(
                    LocalDateTime.ofEpochSecond(stdTrans[i], 0, zofBefore), zofBefore, zofAfter);
            standardOffsetTransitionList.add(zot);
        }
        for (int i = 0; i < savTrans.length; i++) {
            ZoneOffset zofBefore = savOffsets[i];
            ZoneOffset zofAfter = savOffsets[i + 1];
            ZoneOffsetTransition zot = ZoneOffsetTransition.of(
                    LocalDateTime.ofEpochSecond(savTrans[i], 0, zofBefore), zofBefore, zofAfter);
            transitionList.add(zot);
        }
        return ZoneRules.of(stdOffsets[0], savOffsets[0], standardOffsetTransitionList, transitionList,
                Arrays.asList(rules));
    }

    /**
     * Modified from {@link java.time.zone.ZoneOffsetTransition#readExternal(DataInput)}.
     */
    private static ZoneOffsetTransition zotReadExternal(DataInput in) throws IOException {
        @SuppressWarnings("PMD.PrematureDeclaration") // DataInput needs to be read in a specific sequence
        long epochSecond = serReadEpochSec(in);
        ZoneOffset before = serReadOffset(in);
        ZoneOffset after = serReadOffset(in);
        if (before.equals(after)) {
            throw new IllegalArgumentException("Offsets must not be equal");
        }
        // return new ZoneOffsetTransition(epochSecond, before, after);

        return ZoneOffsetTransition.of(LocalDateTime.ofEpochSecond(epochSecond, 0, before), before, after);
    }

    /**
     * Modified from {@link java.time.zone.ZoneOffsetTransitionRule#readExternal(DataInput)}.
     */
    private static ZoneOffsetTransitionRule zotruleReadExternal(DataInput in) throws IOException {
        int data = in.readInt();
        Month month = Month.of(data >>> 28);
        int dom = ((data & (63 << 22)) >>> 22) - 32;
        int dowByte = (data & (7 << 19)) >>> 19;
        DayOfWeek dow = dowByte == 0 ? null : DayOfWeek.of(dowByte);
        int timeByte = (data & (31 << 14)) >>> 14;
        ZoneOffsetTransitionRule.TimeDefinition defn =
                ZoneOffsetTransitionRule.TimeDefinition.values()[(data & (3 << 12)) >>> 12];
        int stdByte = (data & (255 << 4)) >>> 4;
        int beforeByte = (data & (3 << 2)) >>> 2;
        int afterByte = data & 3;
        LocalTime time = timeByte == 31 ? LocalTime.ofSecondOfDay(in.readInt()) : LocalTime.of(timeByte % 24, 0);
        ZoneOffset std = stdByte == 255
                ? ZoneOffset.ofTotalSeconds(in.readInt())
                : ZoneOffset.ofTotalSeconds((stdByte - 128) * 900);
        ZoneOffset before = beforeByte == 3
                ? ZoneOffset.ofTotalSeconds(in.readInt())
                : ZoneOffset.ofTotalSeconds(std.getTotalSeconds() + beforeByte * 1800);
        ZoneOffset after = afterByte == 3
                ? ZoneOffset.ofTotalSeconds(in.readInt())
                : ZoneOffset.ofTotalSeconds(std.getTotalSeconds() + afterByte * 1800);
        return ZoneOffsetTransitionRule.of(month, dom, dow, time, timeByte == 24, defn, std, before, after);
    }

    /**
     * Modified from {@link java.time.zone.Ser#readEpochSec(DataInput)}.
     */
    private static long serReadEpochSec(DataInput in) throws IOException {
        int hiByte = in.readByte() & 255;
        if (hiByte == 255) {
            return in.readLong();
        }
        int midByte = in.readByte() & 255;
        int loByte = in.readByte() & 255;
        long tot = (hiByte << 16) + (midByte << 8) + loByte;
        return (tot * 900) - 4575744000L;
    }

    /**
     * Modified from {@link java.time.zone.Ser#readOffset(DataInput)}.
     */
    private static ZoneOffset serReadOffset(DataInput in) throws IOException {
        int offsetByte = in.readByte();
        return offsetByte == 127 ? ZoneOffset.ofTotalSeconds(in.readInt()) : ZoneOffset.ofTotalSeconds(offsetByte * 900);
    }

}
