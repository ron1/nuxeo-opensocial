/*
 * (C) Copyright 2006-2009 Nuxeo SA (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Leroy Merlin (http://www.leroymerlin.fr/) - initial implementation
 *     Thomas Roger
 */

package org.nuxeo.ecm.spaces.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.spaces.api.Space;
import org.nuxeo.ecm.spaces.api.SpaceManager;
import org.nuxeo.ecm.spaces.api.SpaceProvider;
import org.nuxeo.ecm.spaces.api.exceptions.SpaceException;
import org.nuxeo.ecm.spaces.api.exceptions.SpaceNotFoundException;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.DefaultComponent;

/**
 *
 * @author 10044893
 *
 */
public class SpaceManagerImpl extends DefaultComponent implements SpaceManager {

    private static final Log log = LogFactory.getLog(SpaceManagerImpl.class);

    protected static final String SPACE_PROVIDER_EP = "spaceProviders";

    protected Map<String, SpaceProvider> spaceProviders;

    @Override
    public void activate(ComponentContext context) throws Exception {
        spaceProviders = new HashMap<String, SpaceProvider>();
    }

    @Override
    public void deactivate(ComponentContext context) throws Exception {
        spaceProviders = null;
    }

    public Space getSpaceFromId(String spaceId, CoreSession session)
            throws SpaceException {
        DocumentRef spaceRef = new IdRef(spaceId);
        try {
            if (session.exists(spaceRef)) {
                return session.getDocument(spaceRef).getAdapter(Space.class);
            }
        } catch (ClientException e) {
            log.error(e.getMessage());
            log.debug(e, e);
        }
        throw new SpaceNotFoundException("No space found for id: " + spaceId);
    }

    @Override
    public Space getSpace(String spaceProviderName, CoreSession session,
            DocumentModel contextDocument, String spaceName)
            throws SpaceException {
        SpaceProvider spaceProvider = spaceProviders.get(spaceProviderName);
        if (spaceProvider != null) {
            return spaceProvider.getSpace(session, contextDocument, spaceName);
        } else {
            String message = String.format(
                    "No Space found for '%s' provider and '%s' space name",
                    spaceProviderName, spaceName);
            throw new SpaceNotFoundException(message);
        }
    }

    @Override
    public Space getSpace(String spaceProviderName, CoreSession session,
            DocumentModel contextDocument) throws SpaceException {
        return getSpace(spaceProviderName, session, contextDocument, null);
    }

    @Override
    public Space getSpace(String spaceProviderName, CoreSession session)
            throws SpaceException {
        return getSpace(spaceProviderName, session, null, null);
    }

    @Override
    public Space getSpace(String spaceProviderName,
            DocumentModel contextDocument, String spaceName)
            throws SpaceException {
        return getSpace(spaceProviderName, contextDocument.getCoreSession(),
                contextDocument, spaceName);
    }

    @Override
    public Space getSpace(String spaceProviderName,
            DocumentModel contextDocument) throws SpaceException {
        return getSpace(spaceProviderName, contextDocument.getCoreSession(),
                contextDocument, null);
    }

    @Override
    public void registerContribution(Object contribution,
            String extensionPoint, ComponentInstance contributor)
            throws Exception {
        if (SPACE_PROVIDER_EP.equals(extensionPoint)) {
            SpaceProviderDescriptor descriptor = (SpaceProviderDescriptor) contribution;
            String name = descriptor.getName();
            if (name == null) {
                log.error("Cannot register space provider without a name");
                return;
            }
            boolean enabled = descriptor.isEnabled();
            if (spaceProviders.containsKey(name)) {
                log.info("Overriding space provider with name " + name);
                if (!enabled) {
                    spaceProviders.remove(name);
                    log.info("Disabled space provider with name " + name);
                }
            }
            if (enabled) {
                log.info("Registering space provider with name " + name);
                spaceProviders.put(descriptor.getName(),
                        descriptor.getSpaceProvider());
            }
        }
    }

    @Override
    public void unregisterContribution(Object contribution,
            String extensionPoint, ComponentInstance contributor)
            throws Exception {
        if (SPACE_PROVIDER_EP.equals(extensionPoint)) {
            SpaceProviderDescriptor descriptor = (SpaceProviderDescriptor) contribution;
            String name = descriptor.getName();
            spaceProviders.remove(name);
            log.info("Unregistering space provider with name " + name);
        }
    }

}
