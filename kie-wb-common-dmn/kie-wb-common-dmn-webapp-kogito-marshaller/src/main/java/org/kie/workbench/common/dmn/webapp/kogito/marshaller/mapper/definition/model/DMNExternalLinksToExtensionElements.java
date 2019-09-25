/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model;

import java.util.Objects;

import jsinterop.base.Js;
import jsinterop.base.JsArrayLike;
import org.kie.workbench.common.dmn.api.property.dmn.DMNExternalLink;
import org.kie.workbench.common.dmn.api.property.dmn.DocumentationLinks;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDMNElement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDRGElement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.kie.JSITAttachment;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.JsUtils;

class DMNExternalLinksToExtensionElements {

    static void loadExternalLinksFromExtensionElements(final JSITDRGElement source,
                                                       final org.kie.workbench.common.dmn.api.definition.model.DRGElement target) {

        if (!Objects.isNull(source.getExtensionElements())) {
            final JsArrayLike<Object> extensions = JSITDMNElement.JSIExtensionElements.getAny(source.getExtensionElements());
            if (!Objects.isNull(extensions)) {
                for (int i = 0; i < extensions.getLength(); i++) {
                    final Object extension = extensions.getAt(i);
                    if (JSITAttachment.instanceOf(extension)) {
                        final JSITAttachment jsiExtension = Js.uncheckedCast(extension);
                        final DMNExternalLink external = new DMNExternalLink();
                        external.setDescription(jsiExtension.getName());
                        external.setUrl(jsiExtension.getUrl());
                        target.getLinksHolder().getValue().addLink(external);
                    }
                }
            }
        }
    }

    static void loadExternalLinksIntoExtensionElements(final org.kie.workbench.common.dmn.api.definition.model.DRGElement source,
                                                       final JSITDRGElement target) {

        if (Objects.isNull(source.getLinksHolder()) || Objects.isNull(source.getLinksHolder().getValue())) {
            return;
        }

        final DocumentationLinks links = source.getLinksHolder().getValue();
        final JSITDMNElement.JSIExtensionElements elements = getOrCreateExtensionElements(target);

        removeAllExistingLinks(elements);

        for (final DMNExternalLink link : links.getLinks()) {
            final JSITAttachment attachment = JSITAttachment.newInstance();
            attachment.setName(link.getDescription());
            attachment.setUrl(link.getUrl());
            JSITDMNElement.JSIExtensionElements.addAny(elements, attachment);
        }
        target.setExtensionElements(elements);
    }

    private static void removeAllExistingLinks(final JSITDMNElement.JSIExtensionElements elements) {
        final JSITDMNElement.JSIExtensionElements others = JSITDMNElement.JSIExtensionElements.newInstance();
        // TODO {gcardosi} add because present in original json
        others.setAny(JsUtils.getNativeArray());
        final JsArrayLike<Object> any = JSITDMNElement.JSIExtensionElements.getAny(elements);
        for (int i = 0; i < any.getLength(); i++) {
            final Object extension = any.getAt(i);
            if (!JSITAttachment.instanceOf(extension)) {
                JSITDMNElement.JSIExtensionElements.addAny(others, extension);
            }
        }
        elements.setAny(others.getAny());
    }

    private static JSITDMNElement.JSIExtensionElements getOrCreateExtensionElements(final JSITDRGElement target) {
        // TODO {gcardosi} add because  present in original json
        JSITDMNElement.JSIExtensionElements toReturn = target.getExtensionElements() == null
                ? JSITDMNElement.JSIExtensionElements.newInstance()
                : target.getExtensionElements();
        if (!Objects.isNull(toReturn) && Objects.isNull(toReturn.getAny())) {
            toReturn.setAny(JsUtils.getNativeArray());
        }
        return toReturn;
    }
}