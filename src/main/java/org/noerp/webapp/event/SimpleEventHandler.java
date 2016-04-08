/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package org.noerp.webapp.event;

import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.noerp.base.util.Debug;
import org.noerp.base.util.UtilHttp;
import org.noerp.base.util.UtilProperties;
import org.noerp.minilang.MiniLangException;
import org.noerp.minilang.SimpleMethod;
import org.noerp.webapp.control.ConfigXMLReader;
import org.noerp.webapp.control.ConfigXMLReader.Event;
import org.noerp.webapp.control.ConfigXMLReader.RequestMap;

/**
 * SimpleEventHandler - Simple Event Mini-Lang Handler
 */
public class SimpleEventHandler implements EventHandler {

    public static final String module = SimpleEventHandler.class.getName();
    /** Contains the property file name for translation of error messages. */
    public static final String err_resource = "WebappUiLabels";

    /**
     * @see org.noerp.webapp.event.EventHandler#init(javax.servlet.ServletContext)
     */
    public void init(ServletContext context) throws EventHandlerException {
    }

    /**
     * @see org.noerp.webapp.event.EventHandler#invoke(ConfigXMLReader.Event, ConfigXMLReader.RequestMap, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public String invoke(Event event, RequestMap requestMap, HttpServletRequest request, HttpServletResponse response) throws EventHandlerException {
        String xmlResource = event.path;
        String eventName = event.invoke;
        Locale locale = UtilHttp.getLocale(request);

        if (Debug.verboseOn()) Debug.logVerbose("[Set path/method]: " + xmlResource + " / " + eventName, module);

        if (xmlResource == null) {
            throw new EventHandlerException("XML Resource (eventPath) cannot be null");
        }
        if (eventName == null) {
            throw new EventHandlerException("Event Name (eventMethod) cannot be null");
        }

        Debug.logVerbose("[Processing]: SIMPLE Event", module);
        try {
            String eventReturn = SimpleMethod.runSimpleEvent(xmlResource, eventName, request, response);
            if (Debug.verboseOn()) Debug.logVerbose("[Event Return]: " + eventReturn, module);
            return eventReturn;
        } catch (MiniLangException e) {
            Debug.logError(e, module);
            String errMsg = UtilProperties.getMessage(SimpleEventHandler.err_resource, "simpleEventHandler.event_not_completed", (locale != null ? locale : Locale.getDefault())) + ": ";
            request.setAttribute("_ERROR_MESSAGE_", errMsg + e.getMessage());
            return "error";
        }
    }
}
