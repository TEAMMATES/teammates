package teammates.ui.template;

import java.util.List;

public class AdminTrashEmailTable {
    private int numEmailsTrash;
    private List<AdminTrashEmailRow> rows;
    private ElementTag emptyTrashButton;
    
    public AdminTrashEmailTable(final int numEmailsTrash, final List<AdminTrashEmailRow> rows,
                                                     final String emptyTrashBinActionUrl) {
        this.numEmailsTrash = numEmailsTrash;
        this.rows = rows;
        this.emptyTrashButton = createEmptyTrashButton(emptyTrashBinActionUrl);
    }
    
    public int getNumEmailsTrash() {
        return numEmailsTrash;
    }
    
    public List<AdminTrashEmailRow> getRows() {
        return rows;
    }
    
    public ElementTag getEmptyTrashButton() {
        return emptyTrashButton;
    }  
    
    private ElementTag createEmptyTrashButton(final String emptyTrashBinActionUrl) {
        return new ElementTag("", "class", "btn btn-danger btn-xs", "href", emptyTrashBinActionUrl);
    }
}
