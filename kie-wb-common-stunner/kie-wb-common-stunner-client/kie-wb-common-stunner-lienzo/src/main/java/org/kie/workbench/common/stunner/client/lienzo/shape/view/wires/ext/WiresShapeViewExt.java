/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.ext;

import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.wires.IControlHandle;
import com.ait.lienzo.client.core.shape.wires.IControlHandleList;
import com.ait.lienzo.client.core.shape.wires.LayoutContainer;
import com.ait.lienzo.client.core.shape.wires.WiresLayoutContainer;
import com.ait.lienzo.client.core.shape.wires.event.AbstractWiresDragEvent;
import com.ait.lienzo.client.core.shape.wires.event.AbstractWiresResizeEvent;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.LinearGradient;
import com.google.gwt.event.shared.HandlerRegistration;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.ViewEventHandlerManager;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.WiresShapeView;
import org.kie.workbench.common.stunner.client.lienzo.util.LienzoShapeUtils;
import org.kie.workbench.common.stunner.client.lienzo.util.ShapeControlPointsHelper;
import org.kie.workbench.common.stunner.core.client.shape.view.HasControlPoints;
import org.kie.workbench.common.stunner.core.client.shape.view.HasEventHandlers;
import org.kie.workbench.common.stunner.core.client.shape.view.HasFillGradient;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.client.shape.view.event.DragContext;
import org.kie.workbench.common.stunner.core.client.shape.view.event.DragEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.DragHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ResizeEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ResizeHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.event.TextClickEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.TextDoubleClickEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.TextEnterEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.TextExitEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEventType;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewHandler;

public class WiresShapeViewExt<T extends WiresShapeViewExt>
        extends WiresShapeView<T>
        implements
        HasTitle<T>,
        HasControlPoints<T>,
        HasEventHandlers<T, Shape<?>>,
        HasFillGradient<T> {

    private ViewEventHandlerManager eventHandlerManager;
    private WiresTextDecorator textViewDecorator;
    private Type fillGradientType = null;
    private String fillGradientStartColor = null;
    private String fillGradientEndColor = null;

    public WiresShapeViewExt(final ViewEventType[] supportedEventTypes,
                             final MultiPath path) {
        this(path,
             new WiresLayoutContainer());
        setEventHandlerManager(new ViewEventHandlerManager(getGroup(),
                                                           path,
                                                           supportedEventTypes));
    }

    protected WiresShapeViewExt(final MultiPath path,
                                final LayoutContainer layoutContainer) {
        super(path,
              layoutContainer);
        setListening(true);
    }

    protected void setEventHandlerManager(final ViewEventHandlerManager eventHandlerManager) {
        this.eventHandlerManager = eventHandlerManager;
        this.textViewDecorator = new WiresTextDecorator(eventHandlerManager);
        addTextAsChild();
    }

    @Override
    public boolean supports(final ViewEventType type) {
        return eventHandlerManager.supports(type);
    }

    @Override
    public Shape<?> getAttachableShape() {
        return getShape();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setTitle(final String title) {
        textViewDecorator.setTitle(title);
        return cast();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setTitlePosition(final Position position) {
        if (textViewDecorator.setTitlePosition(position)) {
            removeChild(textViewDecorator.getView());
            addTextAsChild();
        }
        return cast();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setTitleRotation(final double degrees) {
        textViewDecorator.setTitleRotation(degrees);
        return cast();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setTitleStrokeColor(final String color) {
        textViewDecorator.setTitleStrokeColor(color);
        return cast();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setTitleFontFamily(final String fontFamily) {
        textViewDecorator.setTitleFontFamily(fontFamily);
        return cast();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setTitleFontSize(final double fontSize) {
        textViewDecorator.setTitleFontSize(fontSize);
        return cast();
    }

    @Override
    public T setTitleFontColor(final String fillColor) {
        textViewDecorator.setTitleFontColor(fillColor);
        return cast();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setTitleAlpha(final double alpha) {
        textViewDecorator.setTitleAlpha(alpha);
        return cast();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setTitleStrokeWidth(final double strokeWidth) {
        textViewDecorator.setTitleStrokeWidth(strokeWidth);
        return cast();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T moveTitleToTop() {
        textViewDecorator.moveTitleToTop();
        return cast();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setFillGradient(final Type type,
                             final String startColor,
                             final String endColor) {
        this.fillGradientType = type;
        this.fillGradientStartColor = startColor;
        this.fillGradientEndColor = endColor;
        if (null != getShape()) {
            final BoundingBox bb = getShape().getBoundingBox();
            final double width = bb.getWidth();
            final double height = bb.getHeight();
            updateFillGradient(width,
                               height);
        }
        return cast();
    }

    @SuppressWarnings("unchecked")
    public T updateFillGradient(final double width,
                                final double height) {
        if (this.fillGradientType != null
                && this.fillGradientStartColor != null
                && this.fillGradientEndColor != null) {
            final LinearGradient gradient = LienzoShapeUtils.getLinearGradient(fillGradientStartColor,
                                                                               fillGradientEndColor,
                                                                               width,
                                                                               height);
            getShape().setFillGradient(gradient);
        }
        return cast();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T showControlPoints(final ControlPointType type) {
        IControlHandleList ctrls = loadControls(translate(type));
        if (null != ctrls && ControlPointType.RESIZE.equals(type)) {
            // Apply this workaround for now when using the resize control points.
            ShapeControlPointsHelper.showOnlyLowerRightCP(ctrls);
        } else if (null != ctrls) {
            ctrls.show();
        }
        return cast();
    }

    public T updateControlPoints(final ControlPointType type) {
        if (areControlsVisible()) {
            showControlPoints(type);
        }
        return cast();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T hideControlPoints() {
        IControlHandleList ctrls = getControls();
        if (null != ctrls) {
            ctrls.hide();
        }
        return cast();
    }

    @Override
    public boolean areControlsVisible() {
        return null != getControls() && getControls().isVisible();
    }

    @Override
    public void destroy() {
        super.destroy();
        if (null != textViewDecorator) {
            textViewDecorator.destroy();
        }
        if (null != eventHandlerManager) {
            eventHandlerManager.destroy();
            eventHandlerManager = null;
        }
        this.fillGradientEndColor = null;
        this.fillGradientStartColor = null;
        this.fillGradientType = null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T addHandler(final ViewEventType type,
                        final ViewHandler<? extends ViewEvent> eventHandler) {
        if (supports(type)) {
            boolean delegate = true;
            if (ViewEventType.DRAG.equals(type)) {
                final HandlerRegistration[] registrations = registerDragHandler((DragHandler) eventHandler);
                if (null != registrations) {
                    eventHandlerManager.addHandlersRegistration(type,
                                                                registrations);
                }
                delegate = false;
            } else if (ViewEventType.RESIZE.equals(type)) {
                final HandlerRegistration[] registrations = registerResizeHandler((ResizeHandler) eventHandler);
                if (null != registrations) {
                    eventHandlerManager.addHandlersRegistration(type,
                                                                registrations);
                }
                delegate = false;
            }
            if (ViewEventType.TEXT_ENTER.equals(type)) {
                delegate = false;
                textViewDecorator.setTextEnterHandler((ViewHandler<TextEnterEvent>) eventHandler);
            }
            if (ViewEventType.TEXT_EXIT.equals(type)) {
                textViewDecorator.setTextExitHandler((ViewHandler<TextExitEvent>) eventHandler);
                delegate = false;
            }
            if (ViewEventType.TEXT_CLICK.equals(type)) {
                textViewDecorator.setTextClickHandler((ViewHandler<TextClickEvent>) eventHandler);
                delegate = false;
            }
            if (ViewEventType.TEXT_DBL_CLICK.equals(type)) {
                textViewDecorator.setTextDblClickHandler((ViewHandler<TextDoubleClickEvent>) eventHandler);
                delegate = false;
            }
            if (delegate) {
                eventHandlerManager.addHandler(type,
                                               eventHandler);
            }
        }
        return cast();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T removeHandler(final ViewHandler<? extends ViewEvent> eventHandler) {
        eventHandlerManager.removeHandler(eventHandler);
        return cast();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T enableHandlers() {
        eventHandlerManager.enable();
        return cast();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T disableHandlers() {
        eventHandlerManager.disable();
        return cast();
    }

    private void addTextAsChild() {
        this.addChild(textViewDecorator.getView(),
                      textViewDecorator.getLayout());
        // Ensure text element is listening for events.
        textViewDecorator.getView().setListening(true);
    }

    private IControlHandle.ControlHandleType translate(final ControlPointType type) {
        if (type.equals(ControlPointType.RESIZE)) {
            return IControlHandle.ControlHandleStandardType.RESIZE;
        }
        return IControlHandle.ControlHandleStandardType.MAGNET;
    }

    // TODO: listen for WiresMoveEvent's as well?
    private HandlerRegistration[] registerDragHandler(final ViewHandler<DragEvent> eventHandler) {
        if (!getAttachableShape().isDraggable()) {
            final DragHandler dragHandler = (DragHandler) eventHandler;
            setDraggable(true);
            HandlerRegistration dragStartReg = addWiresDragStartHandler(wiresDragStartEvent -> {
                final DragEvent e = buildDragEvent(wiresDragStartEvent);
                dragHandler.start(e);
            });
            HandlerRegistration dragMoveReg = addWiresDragMoveHandler(wiresDragMoveEvent -> {
                final DragEvent e = buildDragEvent(wiresDragMoveEvent);
                dragHandler.handle(e);
            });
            HandlerRegistration dragEndReg = addWiresDragEndHandler(wiresDragEndEvent -> {
                final DragEvent e = buildDragEvent(wiresDragEndEvent);
                dragHandler.end(e);
            });
            return new HandlerRegistration[]{dragStartReg, dragMoveReg, dragEndReg};
        }
        return null;
    }

    private HandlerRegistration[] registerResizeHandler(final ViewHandler<ResizeEvent> eventHandler) {
        final ResizeHandler resizeHandler = (ResizeHandler) eventHandler;
        setResizable(true);
        HandlerRegistration r0 = addWiresResizeStartHandler(wiresResizeStartEvent -> {
            final ResizeEvent event = buildResizeEvent(wiresResizeStartEvent);
            resizeHandler.start(event);
        });
        HandlerRegistration r1 = addWiresResizeStepHandler(wiresResizeStepEvent -> {
            final ResizeEvent event = buildResizeEvent(wiresResizeStepEvent);
            resizeHandler.handle(event);
        });
        HandlerRegistration r2 = addWiresResizeEndHandler(wiresResizeEndEvent -> {
            final ResizeEvent event = buildResizeEvent(wiresResizeEndEvent);
            resizeHandler.end(event);
        });
        return new HandlerRegistration[]{r0, r1, r2};
    }

    private DragEvent buildDragEvent(final AbstractWiresDragEvent sourceDragEvent) {
        final double x = sourceDragEvent.getX();
        final double y = sourceDragEvent.getY();
        final double cx = sourceDragEvent.getNodeDragEvent().getX();
        final double cy = sourceDragEvent.getNodeDragEvent().getY();
        final int dx = sourceDragEvent.getNodeDragEvent().getDragContext().getDx();
        final int dy = sourceDragEvent.getNodeDragEvent().getDragContext().getDy();
        final DragContext dragContext = new DragContext(dx,
                                                        dy,
                                                        () -> sourceDragEvent.getNodeDragEvent().getDragContext().reset());
        return new DragEvent(x,
                             y,
                             cx,
                             cy,
                             dragContext);
    }

    private ResizeEvent buildResizeEvent(final AbstractWiresResizeEvent sourceResizeEvent) {
        final double x = sourceResizeEvent.getX();
        final double y = sourceResizeEvent.getY();
        final double cx = sourceResizeEvent.getNodeDragEvent().getX();
        final double cy = sourceResizeEvent.getNodeDragEvent().getY();
        final double w = sourceResizeEvent.getWidth();
        final double h = sourceResizeEvent.getHeight();
        return new ResizeEvent(x,
                               y,
                               cx,
                               cy,
                               w,
                               h);
    }

    @SuppressWarnings("unchecked")
    private T cast() {
        return (T) this;
    }
}