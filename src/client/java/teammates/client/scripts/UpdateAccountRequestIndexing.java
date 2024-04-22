package teammates.client.scripts;

import java.util.HashMap;
import java.util.List;

import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import teammates.client.util.BackDoor;
import teammates.client.util.ClientProperties;
import teammates.common.datatransfer.SqlDataBundle;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.AccountRequest;

/**
 * Script to trigger indexing for all account requests.
 */
public class UpdateAccountRequestIndexing {
    public static void main(String[] args) {
        UpdateAccountRequestIndexing updater = new UpdateAccountRequestIndexing();
        updater.initSession();
        updater.updateAccountRequestIndexing();
    }

    /**
     * Initializes the Hibernate session.
     */
    public void initSession() {
        String connectionUrl = ClientProperties.SCRIPT_API_URL;
        String username = ClientProperties.SCRIPT_API_NAME;
        String password = ClientProperties.SCRIPT_API_PASSWORD;

        HibernateUtil.buildSessionFactory(connectionUrl, username, password);
    }

    /**
     * Updates the indexing for all account requests using the backdoor and batch processing.
     */
    public void updateAccountRequestIndexing() {
        HibernateUtil.beginTransaction();
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<AccountRequest> cr = cb.createQuery(AccountRequest.class);
        Root<AccountRequest> root = cr.from(AccountRequest.class);
        cr.select(root);
        TypedQuery<AccountRequest> query = HibernateUtil.createQuery(cr);
        List<AccountRequest> accountRequests = query.getResultList();
        HibernateUtil.commitTransaction();

        int batchSize = 100;
        int currentBatchSize = 0;

        for (AccountRequest accountRequest : accountRequests) {
            try {
                SqlDataBundle dataBundle = new SqlDataBundle();
                dataBundle.accountRequests = new HashMap<>();
                dataBundle.accountRequests.put(accountRequest.getId().toString(), accountRequest);

                currentBatchSize++;

                if (currentBatchSize == batchSize) {
                    BackDoor.getInstance().putSqlDocuments(dataBundle);
                    dataBundle.accountRequests.clear();
                    currentBatchSize = 0;
                }
            } catch (Exception e) {
                System.out.println("Failed to index account request with ID: " + accountRequest.getId());
                e.printStackTrace();
            }
        }
    }
}
