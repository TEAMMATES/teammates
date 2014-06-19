package teammates.client.scripts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import com.google.appengine.repackaged.org.apache.commons.collections.MultiMap;
import com.google.appengine.repackaged.org.apache.commons.collections.map.MultiValueMap;

import teammates.client.remoteapi.RemoteApiClient;
import teammates.common.datatransfer.EntityAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.logic.api.Logic;
import teammates.storage.datastore.Datastore;

public class OfflineBackup extends RemoteApiClient {
    
    public static void main(String[] args) throws IOException {
        OfflineBackup offlineBackup = new OfflineBackup();
        offlineBackup.doOperationRemotely();
    }
    
    protected void doOperation() {
        Datastore.initialize();
        Vector<String> logs = getModifiedLogs();
        retrieveAllEntities(mapModifiedEntities(logs));
    }
    
    
    private Vector<String> getModifiedLogs() {
        Vector<String> modifiedLogs = new Vector<String>();
        
        try {
            //Opens a URL connection to obtain the entity modified logs
            URL myURL = new URL("http://4-18-dot-teammates-shawn.appspot.com/entityModifiedLogs");
            
            URLConnection myURLConnection = myURL.openConnection();        
        
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    myURLConnection.getInputStream()));
            String logMessage;
            while ((logMessage = in.readLine()) != null) {
                //System.out.println(logMessage);
                modifiedLogs.add(logMessage);
            }
            in.close();
        } 
        
        catch (IOException e) { 
            // new URL() failed
            // ...
        } 
        
        return modifiedLogs;
    }
    
   
    private MultiMap mapModifiedEntities(Vector<String> modifiedLogs) {
        
        //Removes all duplicates using a set
        Set<String> entities = new HashSet<String>();
        for(String entity : modifiedLogs) {
            entities.add(entity);
        }
        
        //Puts all the entities into a multimap based on entity type to make 
        //it easier to retrieve all entities of a certain type
        Iterator<String> it = entities.iterator();
  
        MultiMap entitiesMap = new MultiValueMap();
        
        while(it.hasNext()) {
            String entity = it.next();
            String tokens[] = entity.split(":");
            String type = tokens[0];
            String[] id = tokens[1].split(", ");
            entitiesMap.put(type, id);
        }
        
        return entitiesMap;
    }
    
    @SuppressWarnings("unchecked")
    private void retrieveAllEntities(MultiMap entityMap) {

        Set<String> keys = entityMap.keySet();
        Iterator<String> it = keys.iterator();
        
        while(it.hasNext()) {
            String entityType = it.next();
            Collection<String[]> ids = (Collection<String[]>) entityMap.get(entityType);
            
            Iterator<String[]> idit = ids.iterator();
            
            while(idit.hasNext()) {
                String[] id = idit.next();
                EntityAttributes ea = retrieveEntity(entityType,id);
                //System.out.println(ea.getIdentificationString());
            }
        }
    }
    
    private EntityAttributes retrieveEntity(String type, String[] id) {
        System.out.println(type);
        switch(type) {
            case "Instructor":
                Logic logic = new Logic();
                InstructorAttributes e = logic.getInstructorForEmail(id[1], id[0]);
                System.out.println(e.getIdentificationString());
                return e;
        }
        return null;
    }
}
