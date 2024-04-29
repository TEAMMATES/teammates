package teammates.client.scripts.sql;

import teammates.client.connector.DatastoreClient;

public class TestScript extends DatastoreClient{

    public static void main(String[] args) {
        TestScript script = new TestScript();
        script.doOperationRemotely();
    }

    @Override
    protected void doOperation() {
        // Seed datastore
        String[] args = {};
        try {
            SeedDb.main(args);
        // migrate non-course
        // patch notif and account req created-at
        // verify non-course
        // migrate course
        // verify course
        } catch (Exception e){
            
        }
    }
    
}
