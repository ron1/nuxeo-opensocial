package org.nuxeo.opensocial.container.client.ui;

import org.nuxeo.opensocial.container.client.images.ImageBundle;
import org.nuxeo.opensocial.container.client.ui.api.HasColor;
import org.nuxeo.opensocial.container.client.ui.enume.ColorsEnum;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Stéphane Fourrier
 */
public class Color extends Composite implements HasClickHandlers, HasColor {

    private Widget widget;

    private String color;

    private boolean isSelected;

    public Color(ColorsEnum color, boolean isSelected) {
        this.isSelected = isSelected;
        if (color.equals(ColorsEnum.TRANSPARENT)) {
            widget = new Image(ImageBundle.INSTANCE.colorNone().getURL());
        } else {
            widget = new SimplePanel();
            widget.getElement().getStyle().setBackgroundColor(
                    color.getCssColor());
        }
        this.color = color.getCssColor();
        init();

        initWidget(widget);
    }

    private void init() {
        widget.setStyleName("color-off");

        addMouseOverHandler(new MouseOverHandler() {
            public void onMouseOver(MouseOverEvent event) {
                widget.setStyleName("color-on");
            }
        });

        addMouseOutHandler(new MouseOutHandler() {
            public void onMouseOut(MouseOutEvent event) {
                if (!isSelected)
                    widget.setStyleName("color-off");
            }
        });
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;

        if (isSelected)
            widget.setStyleName("color-on");
        else
            widget.setStyleName("color-off");
    }

    public String getColorAsString() {
        return color;
    }

    public HandlerRegistration addClickHandler(ClickHandler handler) {
        return addDomHandler(handler, ClickEvent.getType());
    }

    public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
        return addDomHandler(handler, MouseOverEvent.getType());
    }

    public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
        return addDomHandler(handler, MouseOutEvent.getType());
    }
}
