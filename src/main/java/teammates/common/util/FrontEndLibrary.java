package teammates.common.util;

import com.google.gson.JsonObject;

/**
 * Represents the directory of a client-side external library.
 */
public final class FrontEndLibrary {

    public static final String BLANKET;
    public static final String BOOTBOX;
    public static final String BOOTSTRAP_CSS;
    public static final String BOOTSTRAP_THEME_CSS;
    public static final String BOOTSTRAP;
    public static final String D3;
    public static final String DATAMAPS;
    public static final String ELASTICLUNR;
    public static final String HANDSONTABLE_CSS;
    public static final String HANDSONTABLE;
    public static final String JQUERY;
    public static final String JQUERY_GUILLOTINE;
    public static final String JQUERY_GUILLOTINE_CSS;
    public static final String JQUERY_HIGHLIGHT;
    public static final String JQUERY_PRINTTHIS;
    public static final String JQUERY_UI;
    public static final String MARK_JS;
    public static final String MOMENT;
    public static final String QUNIT_CSS;
    public static final String QUNIT;
    public static final String TINYMCE;
    public static final String TOPOJSON;
    public static final String WORLDMAP;

    private static final JsonObject DEPENDENCIES_CONFIG;

    private FrontEndLibrary() {
        // utility class; not meant to be instantiated
    }

    static {

        String dependenciesConfigString = FileHelper.readResourceFile("package.json");
        DEPENDENCIES_CONFIG = JsonUtils.parse(dependenciesConfigString).getAsJsonObject()
                                       .get("dependencies").getAsJsonObject();

        BLANKET = getLibrarySource("blanket", "dist/qunit/blanket.min.js");
        BOOTBOX = getLibrarySource("bootbox", "bootbox.min.js");
        BOOTSTRAP_CSS = getLibrarySource("bootstrap", "dist/css/bootstrap.min.css");
        BOOTSTRAP_THEME_CSS = getLibrarySource("bootstrap", "dist/css/bootstrap-theme.min.css");
        BOOTSTRAP = getLibrarySource("bootstrap", "dist/js/bootstrap.min.js");
        D3 = getLibrarySource("d3", "d3.min.js");
        DATAMAPS = getLibrarySource("datamaps", "dist/datamaps.none.min.js");
        ELASTICLUNR = getLibrarySource("elasticlunr", "elasticlunr.min.js");
        HANDSONTABLE_CSS = getLibrarySource("handsontable", "dist/handsontable.full.min.css");
        HANDSONTABLE = getLibrarySource("handsontable", "dist/handsontable.full.min.js");
        JQUERY = getLibrarySource("jquery", "dist/jquery.min.js");
        JQUERY_GUILLOTINE = getLibrarySource("guillotine", "js/jquery.guillotine.min.js");
        JQUERY_GUILLOTINE_CSS = getLibrarySource("guillotine", "css/jquery.guillotine.css");
        JQUERY_HIGHLIGHT = getLibrarySource("jquery-highlight", "jquery.highlight.js");
        JQUERY_PRINTTHIS = getLibrarySource("printthis", "printThis.js");
        JQUERY_UI = getLibrarySource("jquery-ui-dist", "jquery-ui.min.js");
        MARK_JS = getLibrarySource("mark.js", "dist/jquery.mark.min.js");
        MOMENT = getLibrarySource("moment", "min/moment.min.js");
        QUNIT_CSS = getLibrarySource("qunit", "qunit/qunit.css");
        QUNIT = getLibrarySource("qunit", "qunit/qunit.js");
        TINYMCE = getLibrarySource("tinymce", "tinymce.min.js");
        TOPOJSON = getLibrarySource("topojson", "build/topojson.min.js");
        WORLDMAP = getLibrarySource("datamaps", "src/js/data/world.hires.topo.json");
    }

    private static String getLibrarySource(String libraryNameInNpm, String fileDir) {
        return getCdnBaseUrlForLibrary(libraryNameInNpm) + libraryNameInNpm + "@"
                + DEPENDENCIES_CONFIG.get(libraryNameInNpm).getAsString() + "/" + fileDir;
    }

    private static String getCdnBaseUrlForLibrary(String libraryNameInNpm) {
        // jsDelivr is more reliable than unpkg, but doesn't host required large files from the datamaps library
        return "datamaps".equals(libraryNameInNpm) ? "https://unpkg.com/" : "https://cdn.jsdelivr.net/npm/";
    }

}
