package teammates.ui.controller;


import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.ThreadHelper;
import teammates.logic.api.GateKeeper;
import teammates.test.driver.BackDoor;

import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;
import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.google.appengine.api.search.SearchServiceFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

public class AdminSearchPageAction extends Action {
    
    @Override
    protected ActionResult execute() throws EntityDoesNotExistException{
        
        new GateKeeper().verifyAdminPrivileges(account);
    
        AdminSearchPageData data = new AdminSearchPageData(account);
          
        String searchKey = getRequestParamValue(Const.ParamsNames.ADMIN_SEARCH_KEY);
        String searchButtonHit = getRequestParamValue(Const.ParamsNames.ADMIN_SEARCH_BUTTON_HIT);
        String ifRebuildDoc = getRequestParamValue(Const.ParamsNames.ADMIN_SEARCH_REBUILD_DOC);
        
        if(ifRebuildDoc != null && ifRebuildDoc.equals("true")){
            rebuildDocument();
            return createShowPageResult(Const.ViewURIs.ADMIN_SEARCH, data);
        }
        
        if(searchKey == null || searchKey.trim().isEmpty()){
            
            if(searchButtonHit != null){             
                statusToUser.add("Search key cannot be empty");
                isError = true;
            }
            return createShowPageResult(Const.ViewURIs.ADMIN_SEARCH, data);
        }
        
        
        data.studentResultBundle  = logic.searchStudents(searchKey, "");
        
        int numOfResults = data.studentResultBundle.getResultSize();
        if(numOfResults > 0){
            statusToUser.add("Total results found: " + numOfResults);
            isError = false;
        } else {
            statusToUser.add("No result found, please try again");
            isError = true;
        }
        
        
        
        return createShowPageResult(Const.ViewURIs.ADMIN_SEARCH, data);
    }
    
    
    private void rebuildDocument(){
        //rebuild document to update search index to latest datastore records.
        Queue queue = QueueFactory.getQueue("search-document");
        queue.add(TaskOptions.Builder.withUrl("/searchTask").method(TaskOptions.Method.GET));
        statusToUser.add("Rebuild task submitted, please check again in a few minutes.");
        ThreadHelper.waitBriefly();
    }
}
