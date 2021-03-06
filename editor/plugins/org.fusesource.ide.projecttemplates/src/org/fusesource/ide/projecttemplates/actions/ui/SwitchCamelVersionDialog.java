/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.projecttemplates.actions.ui;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.fusesource.ide.camel.model.service.core.CamelServiceManagerUtil;
import org.fusesource.ide.camel.model.service.core.util.CamelCatalogUtils;
import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.projecttemplates.internal.Messages;

/**
 * @author lheinema
 */
public class SwitchCamelVersionDialog extends TitleAreaDialog {

	private String selectedCamelVersion;
	private Combo versionCombo;

	/**
	 * 
	 * @param parentShell
	 */
	public SwitchCamelVersionDialog(Shell parentShell) {
		super(parentShell);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#create()
	 */
	@Override
	public void create() {
		super.create();
		setTitle(Messages.switchCamelVersionDialogName);
		setMessage(Messages.switchCamelVersionDialogTitle, IMessageProvider.INFORMATION);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(Messages.switchCamelVersionDialogName);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);

		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout layout = new GridLayout(2, false);
		container.setLayout(layout);

		createVersionCombo(container);
		
		return area;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#isResizable()
	 */
	@Override
	protected boolean isResizable() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	protected void okPressed() {
		BusyIndicator.showWhile(Display.getCurrent(), () -> { 
			validate();
			if (getButton(IDialogConstants.OK_ID).isEnabled()) {
				saveInput();
				close();
			}
		});
	}

	private void createVersionCombo(Composite container) {
		Label lbtVersion = new Label(container, SWT.NONE);
		lbtVersion.setText(Messages.switchCamelVersionDialogVersionsLabel);

		GridData dataVersion = new GridData();
		dataVersion.grabExcessHorizontalSpace = true;
		dataVersion.horizontalAlignment = GridData.FILL;

		versionCombo = new Combo(container, SWT.DROP_DOWN | SWT.RIGHT);
		versionCombo.setLayoutData(dataVersion);
		versionCombo.setItems(CamelCatalogUtils.getAllCamelCatalogVersions().stream()
				.sorted((String o1, String o2) -> o2.compareToIgnoreCase(o1)).toArray(String[]::new));
		if (!Strings.isBlank(selectedCamelVersion)) {
			versionCombo.setText(selectedCamelVersion);
		}
		versionCombo.addSelectionListener(new SelectionAdapter() {
			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				getButton(IDialogConstants.OK_ID).setEnabled(true);
			}
		});
		versionCombo.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				getButton(IDialogConstants.OK_ID).setEnabled(true);
			}
		});
	}

	private void validate() {
		final String camelVersion = versionCombo.getText();
		setMessage(NLS.bind(Messages.validatingCamelVersionMessage, camelVersion), IMessageProvider.INFORMATION);
		while(Display.getCurrent().readAndDispatch()) {
			// wait
		}
		boolean valid = CamelServiceManagerUtil.getManagerService().isCamelVersionExisting(camelVersion);
		getButton(IDialogConstants.OK_ID).setEnabled(valid);
		if (!valid) {
			setMessage(NLS.bind(Messages.invalidCamelVersionMessage, camelVersion), IMessageProvider.ERROR);
		}
	}
	
	private void saveInput() {
		this.selectedCamelVersion = this.versionCombo.getText();
	}

	/**
	 * @return the selectedCamelVersion
	 */
	public String getSelectedCamelVersion() {
		return this.selectedCamelVersion;
	}

	/**
	 * @param selectedCamelVersion
	 *            the selectedCamelVersion to set
	 */
	public void setSelectedCamelVersion(String selectedCamelVersion) {
		this.selectedCamelVersion = selectedCamelVersion;
	}
}
