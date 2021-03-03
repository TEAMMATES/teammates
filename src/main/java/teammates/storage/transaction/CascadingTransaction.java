package teammates.storage.transaction;

import teammates.common.exception.CascadingTransactionException;

/**
 * Base class for all transactions that should be committed together in cascading way.
 * <p>* This class instance of transaction can always commit.</p>
 */
public class CascadingTransaction {

    private CascadingTransaction upstreamTransaction;

    public CascadingTransaction() {
        this.upstreamTransaction = this;
    }

    /**
     * Attach the current transaction to an upstream transaction that must be done before this.
     */
    public CascadingTransaction withUpstreamTransaction(CascadingTransaction transaction) {
        upstreamTransaction = transaction;
        return this;
    }

    /**
     * Attach the current transaction to an upstream transaction that must be done before this.
     */
    public boolean hasUpstreamTransaction() {
        return this.upstreamTransaction == this;
    }

    public CascadingTransaction getUpstreamTransaction() {
        return upstreamTransaction;
    }

    /**
     * Commit the current transaction to the datastore.
     * All the exceptions are aggregated as {@link CascadingTransaction}
     * <p>The base transaction always succeeds and marks the end of a transaction cascade upstream.</p>
     *
     * @throws CascadingTransactionException Aggregate of any exception that is thrown during the cascading transaction.
     */
    public void commit() throws CascadingTransactionException {
        // Always succeeds.
    }

}
