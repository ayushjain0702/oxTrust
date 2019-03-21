/*
 * oxTrust is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.gluu.oxtrust.ldap.service;

import java.io.IOException;
import java.io.Serializable;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.gluu.oxtrust.config.ConfigurationFactory;
import org.gluu.oxtrust.model.GluuConfiguration;
import org.gluu.persist.PersistenceEntryManager;
import org.gluu.persist.exception.BasePersistenceException;
import org.slf4j.Logger;
import org.xdi.config.oxtrust.AppConfiguration;
import org.xdi.config.oxtrust.CacheRefreshConfiguration;
import org.xdi.config.oxtrust.ImportPersonConfig;
import org.xdi.config.oxtrust.LdapOxAuthConfiguration;
import org.xdi.config.oxtrust.LdapOxTrustConfiguration;
import org.xdi.service.JsonService;
import org.xdi.service.cache.CacheConfiguration;

/**
 * Provides operations with JSON oxAuth/oxTrust configuration
 * 
 * @author Yuriy Movchan Date: 12.15.2010
 */
@Stateless
@Named("jsonConfigurationService")
public class JsonConfigurationService implements Serializable {

	private static final long serialVersionUID = -3840968275007784641L;

	@Inject
	private Logger log;

	@Inject
	private PersistenceEntryManager ldapEntryManager;
	@Inject
	private JsonService jsonService;

	@Inject
	private ConfigurationFactory configurationFactory;

	@Inject
	private ConfigurationService configurationService;

	public AppConfiguration getOxTrustappConfiguration() {
		LdapOxTrustConfiguration ldapOxTrustConfiguration = getOxTrustConfiguration();
		return ldapOxTrustConfiguration.getApplication();
	}

	public CacheConfiguration getOxMemCacheConfiguration() {
		CacheConfiguration cachedConfiguration = configurationService.getConfiguration().getCacheConfiguration();
		return cachedConfiguration;
	}

	public boolean saveOxMemCacheConfiguration(CacheConfiguration cachedConfiguration) {
		GluuConfiguration gluuConfiguration = configurationService.getConfiguration();
		gluuConfiguration.setCacheConfiguration(cachedConfiguration);
		configurationService.updateConfiguration(gluuConfiguration);
		return true;
	}

	public ImportPersonConfig getOxTrustImportPersonConfiguration() {
		LdapOxTrustConfiguration ldapOxTrustConfiguration = getOxTrustConfiguration();
		return ldapOxTrustConfiguration.getImportPersonConfig();
	}

	public CacheRefreshConfiguration getOxTrustCacheRefreshConfiguration() {
		LdapOxTrustConfiguration ldapOxTrustConfiguration = getOxTrustConfiguration();
		return ldapOxTrustConfiguration.getCacheRefresh();
	}

	private LdapOxTrustConfiguration getOxTrustConfiguration() {
		String configurationDn = configurationFactory.getConfigurationDn();

		LdapOxTrustConfiguration ldapOxTrustConfiguration = loadOxTrustConfig(configurationDn);
		return ldapOxTrustConfiguration;
	}

	public String getOxAuthDynamicConfigJson() throws JsonGenerationException, JsonMappingException, IOException {
		String configurationDn = configurationFactory.getConfigurationDn();

		LdapOxAuthConfiguration ldapOxAuthConfiguration = loadOxAuthConfig(configurationDn);
		return ldapOxAuthConfiguration.getOxAuthConfigDynamic();
	}

	public org.xdi.oxauth.model.configuration.AppConfiguration getOxauthAppConfiguration() throws IOException {
		return jsonService.jsonToObject(getOxAuthDynamicConfigJson(),
				org.xdi.oxauth.model.configuration.AppConfiguration.class);
	}

	public boolean saveOxTrustappConfiguration(AppConfiguration oxTrustappConfiguration) {
		LdapOxTrustConfiguration ldapOxTrustConfiguration = getOxTrustConfiguration();
		ldapOxTrustConfiguration.setApplication(oxTrustappConfiguration);
		ldapOxTrustConfiguration.setRevision(ldapOxTrustConfiguration.getRevision() + 1);
		ldapEntryManager.merge(ldapOxTrustConfiguration);
		return true;
	}

	public boolean saveOxTrustImportPersonConfiguration(ImportPersonConfig oxTrustImportPersonConfiguration) {
		LdapOxTrustConfiguration ldapOxTrustConfiguration = getOxTrustConfiguration();
		ldapOxTrustConfiguration.setImportPersonConfig(oxTrustImportPersonConfiguration);
		ldapOxTrustConfiguration.setRevision(ldapOxTrustConfiguration.getRevision() + 1);
		ldapEntryManager.merge(ldapOxTrustConfiguration);
		return true;
	}

	public boolean saveOxTrustCacheRefreshConfiguration(CacheRefreshConfiguration oxTrustCacheRefreshConfiguration) {
		LdapOxTrustConfiguration ldapOxTrustConfiguration = getOxTrustConfiguration();
		ldapOxTrustConfiguration.setCacheRefresh(oxTrustCacheRefreshConfiguration);
		ldapOxTrustConfiguration.setRevision(ldapOxTrustConfiguration.getRevision() + 1);
		ldapEntryManager.merge(ldapOxTrustConfiguration);
		return true;
	}

	public boolean saveOxAuthAppConfiguration(org.xdi.oxauth.model.configuration.AppConfiguration appConfiguration) {
		try {
			String appConfigurationJson = jsonService.objectToJson(appConfiguration);
			return saveOxAuthDynamicConfigJson(appConfigurationJson);
		} catch (IOException e) {
			log.error("Failed to serialize AppConfiguration", e);
		}
		return false;
	}

	public boolean saveOxAuthDynamicConfigJson(String oxAuthDynamicConfigJson)
			throws JsonParseException, JsonMappingException, IOException {
		String configurationDn = configurationFactory.getConfigurationDn();

		LdapOxAuthConfiguration ldapOxAuthConfiguration = loadOxAuthConfig(configurationDn);
		ldapOxAuthConfiguration.setOxAuthConfigDynamic(oxAuthDynamicConfigJson);
		ldapOxAuthConfiguration.setRevision(ldapOxAuthConfiguration.getRevision() + 1);
		ldapEntryManager.merge(ldapOxAuthConfiguration);
		return true;
	}

	private LdapOxTrustConfiguration loadOxTrustConfig(String configurationDn) {
		try {
			LdapOxTrustConfiguration conf = ldapEntryManager.find(LdapOxTrustConfiguration.class, configurationDn);

			return conf;
		} catch (BasePersistenceException ex) {
			log.error("Failed to load configuration from LDAP");
		}

		return null;
	}

	private LdapOxAuthConfiguration loadOxAuthConfig(String configurationDn) {
		try {
			configurationDn = configurationDn.replace("ou=oxtrust", "ou=oxauth");
			LdapOxAuthConfiguration conf = ldapEntryManager.find(LdapOxAuthConfiguration.class, configurationDn);
			return conf;
		} catch (BasePersistenceException ex) {
			log.error("Failed to load configuration from LDAP");
		}

		return null;
	}

}
