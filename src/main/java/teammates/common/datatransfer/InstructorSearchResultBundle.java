package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.logic.core.InstructorsLogic;

import com.google.appengine.api.search.Cursor;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.google.gson.Gson;

public class InstructorSearchResultBundle extends SearchResultBundle {
    
    public List<InstructorAttributes> instructorList = new ArrayList<InstructorAttributes>();
    public Cursor cursor = null;
    private int numberOfResults = 0;
    private InstructorsLogic instructorsLogic = InstructorsLogic.inst();

    /**
     * This method should be used by admin only since the previous searching does not restrict the 
     * visibility according to the logged-in user's google ID. Therefore,This fromResults method 
     * does not require a googleID as a parameter. Returned results bundle will contain information
     * related to matched instructors only.
     * @param results
     * @return studentResultBundle containing information related to matched students only.
     */   
    public InstructorSearchResultBundle getInstructorsfromResults(Results<ScoredDocument> results){
        if(results == null) {
            return this;
        }
        
        cursor = results.getCursor();
        
        for(ScoredDocument doc:results){
            InstructorAttributes instructor = new Gson().fromJson(doc.getOnlyField(Const.SearchDocumentField.INSTRUCTOR_ATTRIBUTE).getText(), 
                                                                  InstructorAttributes.class);
            
            if(instructorsLogic.getInstructorForRegistrationKey(StringHelper.encrypt(instructor.key)) == null){
                instructorsLogic.deleteDocument(instructor);
                continue;
            }
            
            instructorList.add(instructor);
            numberOfResults++;
        }
        
        sortInstructorResultList();
        
        return this;
    }
    
    
    private void sortInstructorResultList(){
        
        Collections.sort(instructorList, new Comparator<InstructorAttributes>(){
            @Override
            public int compare(InstructorAttributes ins1, InstructorAttributes ins2){
                int compareResult = ins1.courseId.compareTo(ins2.courseId);
                if(compareResult != 0){
                    return compareResult;
                }               
                
                compareResult = ins1.role.compareTo(ins2.role);
                if(compareResult != 0){
                    return compareResult;
                }
                
                compareResult = ins1.name.compareTo(ins2.name);
                if(compareResult != 0){
                    return compareResult;
                }
                      
                return ins1.email.compareTo(ins2.email);
            }
        });
    }
    
    
    @Override
    public int getResultSize() {
        return numberOfResults;
    }

}
