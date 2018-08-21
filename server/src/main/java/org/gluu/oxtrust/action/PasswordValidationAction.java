/*
 * oxTrust is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.gluu.oxtrust.action;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Size;

import org.gluu.oxtrust.ldap.service.IPersonService;
import org.gluu.oxtrust.model.GluuCustomPerson;
import org.gluu.persist.exception.AuthenticationException;
import org.slf4j.Logger;
import org.xdi.service.security.Secure;
import org.xdi.util.StringHelper;

/**
 * Action class for password validation
 * 
 * @author Yuriy Movchan Date: 12/20/2012
 */
@RequestScoped
@Named
@Secure("#{permissionService.hasPermission('profile', 'access')}")
public class PasswordValidationAction implements Cloneable, Serializable {

	private static final long serialVersionUID = 1952428504080910113L;

	@Inject
	private Logger log;

	@Inject
	private IPersonService personService;

	private String oldPassword = "";

	@Size(min = 3, max = 60, message = "Password length must be between {min} and {max} characters.")
	private String password = "";

	@Size(min = 3, max = 60, message = "Password length must be between {min} and {max} characters.")
	private String confirm = "";

	private UIComponent graphValidator;

	@AssertTrue(message = "Different passwords entered!")
	public boolean isPasswordsEquals() {
		return password.equals(confirm);
	}

	public void reset() {
		this.password = this.confirm = null;
	}

	public void storeNewPassword(GluuCustomPerson person, boolean validateOldPassword) {
		if (validateOldPassword) {
			boolean resultValidateOldPassword = false;
			try {
				if ((person != null) && StringHelper.isNotEmpty(person.getUid())) {
					resultValidateOldPassword = personService.authenticate(person.getUid(), oldPassword);
				}
			} catch (AuthenticationException ex) {
				log.debug("Failed to verify old person password", ex);
			}

			if (!resultValidateOldPassword) {
				if (graphValidator == null) {
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Old password isn't valid!", "Old password isn't valid!"));

				} else {
					FacesContext.getCurrentInstance().addMessage(graphValidator.getClientId(), new FacesMessage(
							FacesMessage.SEVERITY_ERROR, "Old password isn't valid!", "Old password isn't valid!"));
				}
			}
		} else {
			if (isSame()) {
				person.setUserPassword(this.password);
				personService.updatePerson(person);
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, "Successfully changed!", "Successfully changed!"));
			} else {
				FacesContext.getCurrentInstance().addMessage(graphValidator.getClientId(),
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Password and confirm password value don't match",
								"Password and confirm password value don't match"));
			}
		}
	}

	private boolean isSame() {
		return this.password.equals(this.confirm);
	}

	public UIComponent getGraphValidator() {
		return graphValidator;
	}

	public void setGraphValidator(UIComponent graphValidator) {
		this.graphValidator = graphValidator;
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setConfirm(String confirm) {
		this.confirm = confirm;
	}

	public String getPassword() {
		return password;
	}

	public String getConfirm() {
		return confirm;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}