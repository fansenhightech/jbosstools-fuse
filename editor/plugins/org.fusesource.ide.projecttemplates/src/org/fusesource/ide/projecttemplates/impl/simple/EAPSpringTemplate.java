/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.projecttemplates.impl.simple;

import java.io.IOException;
import java.io.InputStream;

import org.fusesource.ide.projecttemplates.adopters.AbstractProjectTemplate;
import org.fusesource.ide.projecttemplates.adopters.configurators.MavenTemplateConfigurator;
import org.fusesource.ide.projecttemplates.adopters.configurators.TemplateConfiguratorSupport;
import org.fusesource.ide.projecttemplates.adopters.creators.TemplateCreatorSupport;
import org.fusesource.ide.projecttemplates.adopters.creators.UnzipStreamCreator;
import org.fusesource.ide.projecttemplates.adopters.util.CamelDSLType;
import org.fusesource.ide.projecttemplates.util.NewProjectMetaData;

/**
 * @author bfitzpat
 */
public class EAPSpringTemplate extends AbstractProjectTemplate {

	/* (non-Javadoc)
	 * @see org.fusesource.ide.projecttemplates.adopters.AbstractProjectTemplate#supportsDSL(org.fusesource.ide.projecttemplates.adopters.util.CamelDSLType)
	 */
	@Override
	public boolean supportsDSL(CamelDSLType type) {
		switch (type) {
			case BLUEPRINT:	return false;
			case SPRING:	return true;
			case JAVA:		return false;
			default:		return false;
		}	
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.fusesource.ide.projecttemplates.adopters.AbstractProjectTemplate#getCreator(org.fusesource.ide.projecttemplates.util.NewProjectMetaData)
	 */
	@Override
	public TemplateCreatorSupport getCreator(NewProjectMetaData projectMetaData) {
		return new EAPSpringUnzipTemplateCreator();
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.projecttemplates.adopters.AbstractProjectTemplate#getConfigurator()
	 */
	@Override
	public TemplateConfiguratorSupport getConfigurator() {
		return new MavenTemplateConfigurator();
	}

	/**
	 * creator class for the CBR simple template 
	 */
	private class EAPSpringUnzipTemplateCreator extends UnzipStreamCreator {

		private static final String TEMPLATE_FOLDER = "templates/";
		private static final String TEMPLATE_SPRING = "template-medium-eap-wildfly-spring.zip";
		
		/* (non-Javadoc)
		 * @see org.fusesource.ide.projecttemplates.adopters.creators.InputStreamCreator#getTemplateStream(org.fusesource.ide.projecttemplates.util.NewProjectMetaData)
		 */
		@Override
		public InputStream getTemplateStream(NewProjectMetaData metadata) throws IOException {
			String bundleEntry = null;
			switch (metadata.getDslType()) {
				case SPRING:	bundleEntry = String.format("%s%s", TEMPLATE_FOLDER, TEMPLATE_SPRING);
								break;
				default:
			}
			return getTemplateStream(bundleEntry);
		}
	}
}
