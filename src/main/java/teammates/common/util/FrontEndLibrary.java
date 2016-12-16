package teammates.common.util;

/**
 * Represents the directory of a client-side external library.
 */
public enum FrontEndLibrary {
    
    BOOTBOX("/js/lib/", "https://unpkg.com/bootbox@4.4.0/", "bootbox.min.js"),
    BOOTSTRAP_CSS("/stylesheets/lib/", "https://unpkg.com/bootstrap@3.1.1/dist/css/", "bootstrap.min.css"),
    BOOTSTRAP_THEME_CSS("/stylesheets/lib/", "https://unpkg.com/bootstrap@3.1.1/dist/css/", "bootstrap-theme.min.css"),
    BOOTSTRAP("/js/lib/", "https://unpkg.com/bootstrap@3.1.1/dist/js/", "bootstrap.min.js"),
    D3("/js/lib/", "https://unpkg.com/d3@3.5.17/", "d3.min.js"),
    DATAMAPS("/js/lib/", "https://unpkg.com/datamaps@0.5.8/dist/", "datamaps.none.min.js"),
    JQUERY("/js/lib/", "https://unpkg.com/jquery@1.12.4/dist/", "jquery.min.js"),
    JQUERY_GUILLOTINE("/js/lib/", "https://unpkg.com/guillotine@1.3.1/js/", "jquery.guillotine.min.js"),
    JQUERY_GUILLOTINE_CSS("/stylesheets/lib/", "https://unpkg.com/guillotine@1.3.1/css/", "jquery.guillotine.css"),
    JQUERY_HIGHLIGHT("/js/lib/", "https://unpkg.com/jquery-highlight@3.3.0/", "jquery.highlight.js"),
    JQUERY_PRINTTHIS("/js/lib/", "https://unpkg.com/printthis@0.1.5/", "printThis.js"),
    JQUERY_UI("/js/lib/", "https://unpkg.com/jquery-ui-dist@1.12.1/", "jquery-ui.min.js"),
    MOMENT("/js/lib/", "https://unpkg.com/moment@2.17.1/min/", "moment.min.js"),
    MOMENT_TIMEZONE("/js/lib/", "https://unpkg.com/moment-timezone@0.5.9/builds/",
                    "moment-timezone-with-data-2010-2020.min.js"),
    TINYMCE("/js/lib/", "https://unpkg.com/tinymce@4.5.1/", "tinymce.min.js"),
    TOPOJSON("/js/lib/", "https://unpkg.com/topojson@1.6.27/build/", "topojson.min.js"),
    WORLDMAP("/js/lib/", "https://unpkg.com/datamaps@0.5.8/src/js/data/", "world.hires.topo.json");
    
    private final String localSrc;
    private final String cdnSrc;
    
    FrontEndLibrary(String localDir, String cdnDir, String fileName) {
        this.localSrc = localDir + fileName;
        this.cdnSrc = cdnDir + fileName;
    }
    
    /**
     * Gets the full directory of the specified library, chosen based on the build's environment.
     * <ul>
     * <li>Local files are used on development to enable purely offline testing.</li>
     * <li>CDN files are used on production to reduce the load on Appspot's server.</li>
     * </ul>
     */
    public String getScriptSource() {
        return Config.isDevServer() ? localSrc : cdnSrc;
    }
    
}
