package org.nuxeo.opensocial.container.client.ui;

import java.util.ArrayList;
import java.util.List;

import org.nuxeo.opensocial.container.client.ui.api.HasId;
import org.nuxeo.opensocial.container.client.ui.api.HasWebContents;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Stéphane Fourrier
 */
public class UnitWidget extends FlowPanel implements HasWebContents, HasId {
    private String id;

    public UnitWidget(String cssStyle) {
        this.setStyleName(cssStyle);
        this.addStyleName("yui-unit");
    }

    public boolean hasWebContents() {
        if (this.getWidgetCount() != 0)
            return true;
        else
            return false;
    }

    public void setId(String id) {
        this.id = id;
        this.getElement().setAttribute("id", this.id);
    }

    public String getId() {
        return id;
    }

    public void addWebContent(Widget webContent, long webContentPosition) {
        this.insert(webContent, (int) webContentPosition);
    }

    public void addWebContent(Widget webContent) {
        this.add(webContent);
    }

    public void removeWebContent(int index) {
        this.remove(index);
    }

    public Widget getWebContent(int index) {
        return this.getWidget(index);
    }

    public HasId getWebContent(String webContentId) {
        for (Widget webContent : this.getChildren()) {
            if (webContent.getElement().getAttribute("id").equals(webContentId)) {
                return (HasId) webContent;
            }
        }
        return null;
    }

    public int getWebContentPosition(Widget webContent) {
        return this.getWidgetIndex(webContent);
    }

    public List<Widget> getWebContents() {
        List<Widget> widgets = new ArrayList<Widget>();

        for (Widget widget : this.getChildren()) {
            widgets.add(widget);
        }

        return widgets;
    }
}
