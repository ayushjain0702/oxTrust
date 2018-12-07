/*
 * oxTrust is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.gluu.oxtrust.service;

import org.gluu.oxtrust.api.RegistrationManagementService;
import org.gluu.oxtrust.api.attributes.AttributeWebResource;
import org.gluu.oxtrust.api.customscripts.CustomScriptManagementService;
import org.gluu.oxtrust.api.errors.ConstraintViolationExceptionMapper;
import org.gluu.oxtrust.api.errors.InvalidScriptDataExceptionMapper;
import org.gluu.oxtrust.ws.rs.scim2.BulkWebService;
import org.gluu.oxtrust.ws.rs.scim2.GroupWebService;
import org.gluu.oxtrust.ws.rs.scim2.ResourceTypeWS;
import org.gluu.oxtrust.ws.rs.scim2.SchemaWebService;
import org.gluu.oxtrust.ws.rs.scim2.ServiceProviderConfigWS;
import org.gluu.oxtrust.ws.rs.scim2.UserWebService;

import javax.ws.rs.core.Application;
import javax.ws.rs.ext.Provider;
import java.util.HashSet;
import java.util.Set;

/**
 * Integration with Resteasy
 *
 * @author Yuriy Movchan
 * @version June 6, 2017
 */
// TODO: Try to move to test source folder
@Provider
public class TestResteasyInitializer extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        HashSet<Class<?>> classes = new HashSet<Class<?>>();
        classes.add(UserWebService.class);
        classes.add(GroupWebService.class);
        classes.add(BulkWebService.class);
        classes.add(ResourceTypeWS.class);
        classes.add(SchemaWebService.class);
        classes.add(ServiceProviderConfigWS.class);

        // API
        classes.add(AttributeWebResource.class);
        classes.add(RegistrationManagementService.class);
        classes.add(CustomScriptManagementService.class);

        // Providers
        classes.add(InvalidScriptDataExceptionMapper.class);
        classes.add(ConstraintViolationExceptionMapper.class);

        return classes;
    }

}