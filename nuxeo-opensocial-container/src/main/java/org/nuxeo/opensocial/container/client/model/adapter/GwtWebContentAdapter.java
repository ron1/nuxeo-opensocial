package org.nuxeo.opensocial.container.client.model.adapter;

import org.nuxeo.opensocial.container.client.Container;
import org.nuxeo.opensocial.container.client.gin.ClientInjector;
import org.nuxeo.opensocial.container.client.presenter.CustomWebContentPresenter;
import org.nuxeo.opensocial.container.client.presenter.PortletPresenter;
import org.nuxeo.opensocial.container.client.view.CustomWebContentWidget;
import org.nuxeo.opensocial.container.client.view.PortletWidget;
import org.nuxeo.opensocial.container.shared.webcontent.WebContentData;

import net.customware.gwt.presenter.client.EventBus;
import net.customware.gwt.presenter.client.Presenter;
import net.customware.gwt.presenter.client.widget.WidgetDisplay;

/**
 * @author Stéphane Fourrier
 */
public class GwtWebContentAdapter {
    private final ClientInjector injector = Container.injector;

    private Presenter containerPresenter;

    private EventBus eventBus;

    private WebContentData data;

    public GwtWebContentAdapter(WebContentData webContentData, EventBus eventBus) {
        this.data = webContentData;
        this.eventBus = eventBus;

        Presenter webContentPresenter = injector.getGadgetFactory().getPresenterFor(
                webContentData);

        webContentPresenter.bind();

        addContainerForWebContent(webContentPresenter);
    }

    private void addContainerForWebContent(Presenter webContentPresenter) {
        WidgetDisplay containerDisplay;

        if (data.isInAPorlet()) {
            containerDisplay = new PortletWidget();

            containerPresenter = new PortletPresenter(
                    (org.nuxeo.opensocial.container.client.presenter.PortletPresenter.Display) containerDisplay,
                    eventBus, data, webContentPresenter);
        } else {
            containerDisplay = new CustomWebContentWidget();

            containerPresenter = new CustomWebContentPresenter(
                    (org.nuxeo.opensocial.container.client.presenter.CustomWebContentPresenter.Display) containerDisplay,
                    eventBus, data, webContentPresenter);
        }
    }

    public Presenter getContainerPresenter() {
        return containerPresenter;
    }
}
