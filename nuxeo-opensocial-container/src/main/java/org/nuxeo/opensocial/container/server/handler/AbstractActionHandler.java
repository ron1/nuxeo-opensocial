package org.nuxeo.opensocial.container.server.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.repository.RepositoryManager;
import org.nuxeo.ecm.spaces.api.Space;
import org.nuxeo.ecm.spaces.api.SpaceManager;
import org.nuxeo.opensocial.container.client.rpc.AbstractAction;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.transaction.TransactionHelper;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;
import net.customware.gwt.dispatch.shared.Result;

/**
 * This class abstracts all the job for getting the CoreSession, make sure
 * session is cleaned after being used and all Tx stuff goes well
 *
 * @author Stéphane Fourrier
 */
public abstract class AbstractActionHandler<T extends AbstractAction<R>, R extends Result>
        implements ActionHandler<T, R> {

    private static final Log log = LogFactory.getLog(AbstractActionHandler.class);

    public final R execute(T action, ExecutionContext context)
            throws ActionException {
        CoreSession session = openSession(action.getRepositoryName());

        if (!TransactionHelper.startTransaction()) {
            log.warn("Unable to start transaction : there will be no Tx support");
        }

        if (session != null) {
            try {
                R result = doExecute(action, context, session);
                session.save();
                return result;
            } catch (Exception e) {
                TransactionHelper.setTransactionRollbackOnly();
                throw new ActionException(
                        "Error occured during action... rollbacking : "
                                + e.getMessage(), e);
            } finally {
                TransactionHelper.commitOrRollbackTransaction();
                CoreInstance.getInstance().close(session);
            }
        } else {
            throw new ActionException("Unable to open session");
        }
    }

    public void rollback(T action, R result, ExecutionContext context)
            throws ActionException {
        TransactionHelper.setTransactionRollbackOnly();
    }

    /**
     * Real job takes place here
     *
     * @throws Exception
     */
    protected abstract R doExecute(T action, ExecutionContext context,
            CoreSession session) throws Exception;

    protected Space getSpaceFromId(String spaceId, CoreSession session)
            throws ClientException {
        SpaceManager spaceManager = getSpaceManager();
        return spaceManager.getSpaceFromId(spaceId, session);
    }

    protected SpaceManager getSpaceManager() throws ClientException {
        try {
            return Framework.getService(SpaceManager.class);
        } catch (Exception e) {
            throw new ClientException("Unable to get Space Manager", e);
        }
    }

    protected CoreSession openSession(String repositoryName)
            throws ActionException {
        try {
            RepositoryManager m = Framework.getService(RepositoryManager.class);
            return m.getRepository(repositoryName).open();
        } catch (Exception e) {
            throw new ActionException("Unable to get session", e);
        }
    }

}
