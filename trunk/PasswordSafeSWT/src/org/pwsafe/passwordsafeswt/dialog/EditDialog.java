/*
 * Copyright (c) 2008-2010 David Muller <roxon@users.sourceforge.net>.
 * All rights reserved. Use of the code is allowed under the
 * Artistic License 2.0 terms, as specified in the LICENSE file
 * distributed with this code, or available from
 * http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pwsafe.passwordsafeswt.dialog;

import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pwsafe.lib.datastore.PwsEntryBean;
import org.pwsafe.passwordsafeswt.PasswordSafeJFace;
import org.pwsafe.passwordsafeswt.preference.JpwPreferenceConstants;
import org.pwsafe.passwordsafeswt.state.LockState;
import org.pwsafe.passwordsafeswt.util.ShellHelpers;
import org.pwsafe.passwordsafeswt.util.UserPreferences;
import org.pwsafe.util.PassphraseUtils;


/**
 * The Dialog that allows a user to edit password entries.
 *
 * @author Glen Smith
 */
public class EditDialog extends Dialog implements Observer {

	private static final Log log = LogFactory.getLog(EditDialog.class);

	private static final int PERCENT_NOTES_WIDTH = 68;

	private Text txtNotes;
	private Text txtPassword;
	private Text txtUsername;
	private Text txtTitle;
	private Text txtGroup;
	private Text txtUrl;
	private Text txtAutotype;
	private Composite timesGroup;
	private CLabel passwordChange;
	private CLabel changed;
	private CLabel lastAccess;
	private CLabel createTime;
	private Text txtPasswordExpire;
    private volatile boolean dirty;
	protected Object result;
	protected Shell shell;
    private final PwsEntryBean entryToEdit;

	public EditDialog(Shell parent, int style, PwsEntryBean entryToEdit) {
		super(parent, style);
        this.entryToEdit = entryToEdit;
	}

	public EditDialog(Shell parent, PwsEntryBean entryToEdit) {
		this(parent, SWT.NONE, entryToEdit);
	}

	public Object open() {
		createContents();
		ShellHelpers.centreShell(getParent(), shell);
		shell.layout();
		shell.pack();
		shell.open();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		return result;
	}



	/**
	 * Returns whether the data in the dialog has been updated by the user.
	 *
	 * @return true if the data has been updated, false otherwise
	 */
    public boolean isDirty() {
    	return dirty;
    }

    /**
     * Marks the dialog as having data that needs to be updated.
     *
     * @param dirty true if the dialog data needs saving, false otherwise.
     */
    public void setDirty(boolean dirty) {
    	this.dirty = dirty;
    }


	protected void createContents() {
		shell = new Shell(getParent(), SWT.RESIZE | SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shell.setImage(JFaceResources.getImage(PasswordSafeJFace.JPW_ICON));
		shell.setSize(600, 603);
		shell.setText(Messages.getString("EditDialog.Title")); //$NON-NLS-1$
		final GridLayout gridLayout_2 = new GridLayout();
		gridLayout_2.marginWidth = 5;
		gridLayout_2.marginHeight = 5;
		shell.setLayout(gridLayout_2);
		shell.setMinimumSize(300, 400);

		// Setup adapter to catch any keypress and mark dialog dirty
		KeyAdapter dirtyKeypress = new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				setDirty(true);
			}
		};

		//use a modify listener as the password field drops letter key events on Linux
		ModifyListener entryEdited = new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				setDirty(true);
			}

		};

		final Composite compositeLabel = new Composite(shell, SWT.NONE);
		final GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.widthHint = 550;
		compositeLabel.setLayoutData(gridData);
		compositeLabel.setLayout(new GridLayout());

		final Label labelInfo = new Label(compositeLabel, SWT.WRAP);
		final GridData gridData_1 = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
		gridData_1.widthHint = 550;

		labelInfo.setLayoutData(gridData_1);
		labelInfo.setText(Messages.getString("EditDialog.Info")); //$NON-NLS-1$

		final Composite compositeFields = new Composite(shell, SWT.NONE);
		compositeFields.setLayout(new FormLayout());
		final GridData gridData_c = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData_c.widthHint = 550;
		compositeFields.setLayoutData(gridData_c);

		final Label lblGroup = new Label(compositeFields, SWT.NONE);
		final FormData formData = new FormData();
		formData.top = new FormAttachment(0, 10);
		formData.left = new FormAttachment(0, 17);
		
		lblGroup.setLayoutData(formData);
		lblGroup.setText(Messages.getString("EditDialog.Group")); //$NON-NLS-1$

		txtGroup = new Text(compositeFields, SWT.BORDER);
		txtGroup.addKeyListener(dirtyKeypress);
		final FormData formData_1 = new FormData();
		// this sets the effective width of the labels column
		formData_1.left = new FormAttachment(lblGroup, 40, SWT.RIGHT);
		formData_1.top = new FormAttachment(lblGroup, 0, SWT.TOP);
		formData_1.right = new FormAttachment(43, 0);
		txtGroup.setLayoutData(formData_1);
        if (entryToEdit.getGroup() != null)
            txtGroup.setText(entryToEdit.getGroup());

		final Label lblTitle = new Label(compositeFields, SWT.NONE);
		final FormData formData_2 = new FormData();
		formData_2.top = new FormAttachment(txtGroup, 10, SWT.BOTTOM);
		formData_2.left = new FormAttachment(lblGroup, 0, SWT.LEFT);
		lblTitle.setLayoutData(formData_2);
		lblTitle.setText(Messages.getString("EditDialog.TitleLabel")); //$NON-NLS-1$

		txtTitle = new Text(compositeFields, SWT.BORDER);
		final FormData formData_3 = new FormData();
		formData_3.top = new FormAttachment(txtGroup, 10, SWT.BOTTOM);
		formData_3.left = new FormAttachment(txtGroup, 0, SWT.LEFT);
		formData_3.right = new FormAttachment(txtGroup, 0 , SWT.RIGHT);
		txtTitle.setLayoutData(formData_3);
		txtTitle.addKeyListener(dirtyKeypress);
        if (entryToEdit.getTitle() != null)
            txtTitle.setText(entryToEdit.getTitle());

		final Label lblUsername = new Label(compositeFields, SWT.NONE);
		final FormData formData_4 = new FormData();
		formData_4.top = new FormAttachment(txtTitle, 10, SWT.BOTTOM);
		formData_4.left = new FormAttachment(lblTitle, 0, SWT.LEFT);
		lblUsername.setLayoutData(formData_4);
		lblUsername.setText(Messages.getString("EditDialog.Username")); //$NON-NLS-1$

		txtUsername = new Text(compositeFields, SWT.BORDER);
		final FormData formData_5 = new FormData();
		formData_5.top = new FormAttachment(txtTitle, 10);
		formData_5.left = new FormAttachment(txtTitle, 0, SWT.LEFT);
		formData_5.right = new FormAttachment(txtTitle, 0 , SWT.RIGHT);
		txtUsername.setLayoutData(formData_5);
		txtUsername.addKeyListener(dirtyKeypress);
        if (entryToEdit.getUsername() != null)
            txtUsername.setText(entryToEdit.getUsername());

		final Label lblPassword = new Label(compositeFields, SWT.NONE);
		final FormData formData_6 = new FormData();
		formData_6.top = new FormAttachment(txtUsername, 10, SWT.BOTTOM);
		formData_6.left = new FormAttachment(lblUsername, 0, SWT.LEFT);
		lblPassword.setLayoutData(formData_6);
		lblPassword.setText(Messages.getString("EditDialog.Password")); //$NON-NLS-1$

		txtPassword = new Text(compositeFields, SWT.BORDER);
		final FormData formData_7 = new FormData();
		formData_7.top = new FormAttachment(txtUsername, 10, SWT.BOTTOM);
		formData_7.left = new FormAttachment(txtUsername, 0, SWT.LEFT);
		formData_7.right = new FormAttachment(txtUsername, 0 , SWT.RIGHT);
		txtPassword.setLayoutData(formData_7);
		txtPassword.addKeyListener(dirtyKeypress);
		if (!UserPreferences.getInstance().getBoolean(JpwPreferenceConstants.SHOW_PASSWORD_IN_EDIT_MODE)) {
        txtPassword.setEchoChar('*');
		}
        if (entryToEdit.getPassword() != null)
            txtPassword.setText(entryToEdit.getPassword().toString());
		txtPassword.addModifyListener(entryEdited);// important: add after setting content

		final Button btnShowPassword = new Button(compositeFields, SWT.NONE);
		btnShowPassword.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
                if (txtPassword.getEchoChar() != '\0') {
                	txtPassword.setEchoChar('\0');
                	btnShowPassword.setText(Messages.getString("EditDialog.HidePasswordButton")); //$NON-NLS-1$
                } else {
                	btnShowPassword.setText(Messages.getString("EditDialog.ShowPasswordButton")); //$NON-NLS-1$
                	txtPassword.setEchoChar('*');
                }
			}
		});
		final FormData formData_8 = new FormData();
		formData_8.left = new FormAttachment(txtPassword, 10);
		formData_8.top = new FormAttachment(txtUsername, 10, SWT.BOTTOM);
		formData_8.right = new FormAttachment(PERCENT_NOTES_WIDTH, 0);
		btnShowPassword.setLayoutData(formData_8);
		if (UserPreferences.getInstance().getBoolean(JpwPreferenceConstants.SHOW_PASSWORD_IN_EDIT_MODE)) {
			btnShowPassword.setText(Messages.getString("EditDialog.HidePasswordButton")); //$NON-NLS-1$
		} else {
			btnShowPassword.setText(Messages.getString("EditDialog.ShowPasswordButton")); //$NON-NLS-1$
		}

		final Label lblNotes = new Label(compositeFields, SWT.NONE);
		final FormData formData_9 = new FormData();
		formData_9.top = new FormAttachment(txtPassword, 5, SWT.BOTTOM);
		formData_9.left = new FormAttachment(lblPassword, 0, SWT.LEFT);
		lblNotes.setLayoutData(formData_9);
		lblNotes.setText(Messages.getString("EditDialog.Notes")); //$NON-NLS-1$

		txtNotes = new Text(compositeFields, SWT.V_SCROLL | SWT.MULTI | SWT.BORDER | SWT.WRAP);
		final FormData formData_10 = new FormData(SWT.DEFAULT, 100);
		txtNotes.setSize(100, 100);
		formData_10.bottom = new FormAttachment(100, -112);
		formData_10.top = new FormAttachment(txtPassword, 5, SWT.BOTTOM);
		formData_10.right = new FormAttachment(btnShowPassword, 0, SWT.RIGHT);
		formData_10.left = new FormAttachment(txtPassword, 0, SWT.LEFT);

		txtNotes.setLayoutData(formData_10);
		txtNotes.addKeyListener(dirtyKeypress);
        if (entryToEdit.getNotes() != null)
            txtNotes.setText(entryToEdit.getNotes());

        // New fields for V3 Files
		final Label lblUrl = new Label(compositeFields, SWT.NONE);
		FormData formDataTemp = new FormData();
		formDataTemp.top = new FormAttachment(txtNotes, 10, SWT.BOTTOM);
		formDataTemp.left = new FormAttachment(lblNotes, 0, SWT.LEFT);
		lblUrl.setLayoutData(formDataTemp);
		lblUrl.setText(Messages.getString("EditDialog.Url")); //$NON-NLS-1$

		txtUrl = new Text(compositeFields, SWT.BORDER);
		formDataTemp = new FormData();
		formDataTemp.top = new FormAttachment(txtNotes, 10, SWT.BOTTOM);
		formDataTemp.left = new FormAttachment(txtNotes, 0, SWT.LEFT);
		formDataTemp.right = new FormAttachment(txtNotes, 0 , SWT.RIGHT);
		txtUrl.setLayoutData(formDataTemp);
		txtUrl.addKeyListener(dirtyKeypress);
        if (entryToEdit.getUrl() != null)
    		txtUrl.setText(entryToEdit.getUrl());

		final Label lblAutotype = new Label(compositeFields, SWT.NONE);
		formDataTemp = new FormData();
		formDataTemp.top = new FormAttachment(txtUrl, 10, SWT.BOTTOM);
		formDataTemp.left = new FormAttachment(lblUrl, 0, SWT.LEFT);
		lblAutotype.setLayoutData(formDataTemp);
		lblAutotype.setText(Messages.getString("EditDialog.Autotype")); //$NON-NLS-1$

		txtAutotype = new Text(compositeFields, SWT.BORDER);
		formDataTemp = new FormData();
		formDataTemp.top = new FormAttachment(txtUrl, 10, SWT.BOTTOM);
		formDataTemp.left = new FormAttachment(txtUrl, 0, SWT.LEFT);
		formDataTemp.right = new FormAttachment(txtPassword, 0 , SWT.RIGHT);
		txtAutotype.setLayoutData(formDataTemp);
		txtAutotype.addKeyListener(dirtyKeypress);
        if (entryToEdit.getAutotype() != null)
    		txtAutotype.setText(entryToEdit.getAutotype());

		final Label lblPasswordExpire = new Label(compositeFields, SWT.NONE);
		final FormData fd_lblPasswordExpire = new FormData();
		fd_lblPasswordExpire.top = new FormAttachment(txtAutotype, 10, SWT.BOTTOM);
		fd_lblPasswordExpire.left = new FormAttachment(lblAutotype, 0, SWT.LEFT);
		lblPasswordExpire.setLayoutData(fd_lblPasswordExpire);
		lblPasswordExpire.setText(Messages.getString("EditDialog.PasswordExpires")); //$NON-NLS-1$

		txtPasswordExpire = new Text(compositeFields, SWT.BORDER);
		final FormData fd_txtPasswordExpire = new FormData();
		fd_txtPasswordExpire.left = new FormAttachment(txtAutotype, 0, SWT.LEFT);
		fd_txtPasswordExpire.right = new FormAttachment(txtAutotype, 0, SWT.RIGHT);
		fd_txtPasswordExpire.top = new FormAttachment(txtAutotype, 10, SWT.BOTTOM);
		txtPasswordExpire.setLayoutData(fd_txtPasswordExpire);
		txtPasswordExpire.addModifyListener(new ModifyListener() {
			Color red = null;
			Color normal = null;
			final Date now = new Date();
			public void modifyText(final ModifyEvent e) {
				final Text widget = ((Text)e.widget);
				final String dateText = widget.getText();
				if (dateText != null && dateText.length() > 0) {
					if (red == null) {
						red = shell.getDisplay().getSystemColor(SWT.COLOR_RED);
						normal = shell.getDisplay().getSystemColor(SWT.COLOR_WIDGET_FOREGROUND);
					}
					try {
						Date date = convertTextToDate(dateText);
						if (now.after(date)) {
							widget.setForeground(red);
						} else {
							widget.setForeground(normal);
						}
					} catch (ParseException e1) { // no prob
					}
				}

				
			}
		});
		txtPasswordExpire.setText(format(entryToEdit.getExpires()));
		txtPasswordExpire.addKeyListener(dirtyKeypress);
        addDateChooser (compositeFields);

        shell.setDefaultButton(createButtons(compositeFields, btnShowPassword));

        if (entryToEdit.getTitle() != null && entryToEdit.getTitle().length() > 0) {
        	createTimesComposite(shell);
        }
	}

	private void addDateChooser(Composite compositeFields) {
		Button open = new Button (compositeFields, SWT.PUSH);
		final FormData fd_dtPasswordExpire = new FormData();
		fd_dtPasswordExpire.left = new FormAttachment(txtPasswordExpire, 10, SWT.RIGHT);
		fd_dtPasswordExpire.top = new FormAttachment(txtPasswordExpire, 0, SWT.TOP);
		fd_dtPasswordExpire.bottom = new FormAttachment(txtPasswordExpire, 0, SWT.BOTTOM);
		open.setLayoutData(fd_dtPasswordExpire);
		open.setText (Messages.getString("EditDialog.Calendar")); //$NON-NLS-1$
		open.addSelectionListener (new SelectionAdapter () {
			@Override
			public void widgetSelected (SelectionEvent e) {
				DateDialog dialog = new DateDialog(shell);
				dialog.setDate(entryToEdit.getExpires());
				Date result = dialog.open();
				if (result != null && ! result.equals(entryToEdit.getExpires())) {
					txtPasswordExpire.setText(format(result));
					setDirty(true);
				}
			}
		});
	}

	/**
	 * Creates the controlling buttons on the dialog
	 * @param compositeFields
	 * @param btnShowPassword
	 * return the default button
	 */
	private Button createButtons(final Composite compositeFields, final Button btnShowPassword) {
		final Button btnOk = new Button(compositeFields, SWT.NONE);
		btnOk.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
                if (isDirty()) {
                	final Date now = new Date();
                	entryToEdit.setLastChange(now);
					entryToEdit.setLastAccess(now);
                    entryToEdit.setGroup(txtGroup.getText());
                    entryToEdit.setTitle(txtTitle.getText());
                    entryToEdit.setUsername(txtUsername.getText());
                    if (! txtPassword.getText().equals(entryToEdit.getPassword().toString())) {
                    	entryToEdit.setPassword(new StringBuilder(txtPassword.getText()));
                    	entryToEdit.setLastPwChange(now);
                    }
                    entryToEdit.setNotes(txtNotes.getText());
                    String fieldText = txtPasswordExpire.getText();
                    if (fieldText != null && (! fieldText.trim().equals(""))) { //$NON-NLS-1$
                    	try {
							Date expireDate = convertTextToDate(fieldText);

							entryToEdit.setExpires(expireDate);
						} catch (ParseException e1) {
				            MessageBox mb = new MessageBox(shell, SWT.ICON_WARNING | SWT.YES | SWT.NO);
				            mb.setText(Messages.getString("EditDialog.ExpiryNotValidMessage.Title")); //$NON-NLS-1$
				            mb.setMessage(Messages.getString("EditDialog.ExpiryNotValidMessage.Text")); //$NON-NLS-1$
				            int result = mb.open();
				            if (result == SWT.NO) {
				                return;
				            }

						}
                    } else {
                    	entryToEdit.setExpires(null);
                    }

					entryToEdit.setUrl(txtUrl.getText());
                    entryToEdit.setAutotype(txtAutotype.getText());
                    result = entryToEdit;
                } else {
                	result = null;
                }
                shell.dispose();
			}


		});
		final FormData formData_11 = new FormData();
		formData_11.top = new FormAttachment(txtGroup, 0, SWT.TOP);
		formData_11.left = new FormAttachment(100,-90);
		formData_11.right = new FormAttachment(100, -10);
		btnOk.setLayoutData(formData_11);
		btnOk.setText(Messages.getString("EditDialog.OkButton")); //$NON-NLS-1$

		final Button btnCancel = new Button(compositeFields, SWT.NONE);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
                result = null;
                shell.dispose();
			}
		});
		final FormData formData_12 = new FormData();
		formData_12.top = new FormAttachment(btnOk, 5);
		formData_12.left = new FormAttachment(btnOk, 0, SWT.LEFT);
		formData_12.right = new FormAttachment(btnOk, 0, SWT.RIGHT);
		btnCancel.setLayoutData(formData_12);
		btnCancel.setText(Messages.getString("EditDialog.CancelButton")); //$NON-NLS-1$

		final Button btnHelp = new Button(compositeFields, SWT.NONE);
		final FormData formData_13 = new FormData();
		formData_13.top = new FormAttachment(btnCancel, 5);
		formData_13.left = new FormAttachment(btnCancel, 0, SWT.LEFT);
		formData_13.right = new FormAttachment(btnCancel, 0, SWT.RIGHT);
		btnHelp.setLayoutData(formData_13);
		btnHelp.setText(Messages.getString("EditDialog.HelpButton")); //$NON-NLS-1$

		final Group group = new Group(compositeFields, SWT.NONE);
		group.setLayout(new GridLayout());
		group.setText(Messages.getString("EditDialog.RandomPassword")); //$NON-NLS-1$
		final FormData formData_14 = new FormData();
//		formData_14.left = new FormAttachment(txtNotes, 10, SWT.RIGHT);
		formData_14.left = new FormAttachment(100, -160);
//		formData_14.left = new FormAttachment(PERCENT_NOTES_WIDTH + 2, 0);
		formData_14.top = new FormAttachment(btnShowPassword, 5, SWT.TOP);
		formData_14.right = new FormAttachment(100, -10);
		group.setLayoutData(formData_14);

		final Button btnGenerate = new Button(group, SWT.NONE);
		btnGenerate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String generatedPassword = generatePassword();
				txtPassword.setText(generatedPassword);
			}
		});
		btnGenerate.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		btnGenerate.setText(Messages.getString("EditDialog.Generate")); //$NON-NLS-1$

		final Button chkOverride = new Button(group, SWT.CHECK);
		chkOverride.setText(Messages.getString("EditDialog.OverridePolicyButton")); //$NON-NLS-1$
		chkOverride.setEnabled(false); //TODO: Open policy dialog and generate a password with it on exit

		return btnOk;
	}

	/**
	 * Creates a line showing change information about the record.
	 * @param aShell to Add the Composite to
	 */
	private void createTimesComposite(final Shell aShell) {
		final GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		timesGroup = new Composite(aShell, SWT.NONE);
		timesGroup.setRedraw(true);
		final GridData timesGridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gridData.widthHint = 550;
		timesGroup.setLayoutData(timesGridData);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 8;
		timesGroup.setLayout(gridLayout);


		final CLabel createdLbl = new CLabel(timesGroup, SWT.NONE);
		createdLbl.setText(Messages.getString("EditDialog.Created")); //$NON-NLS-1$

		createTime = new CLabel(timesGroup, SWT.NONE);
		createTime.setText(format(entryToEdit.getCreated()));

		final CLabel lastAccessLbl = new CLabel(timesGroup, SWT.NONE);
		lastAccessLbl.setText(Messages.getString("EditDialog.LastAccess")); //$NON-NLS-1$

		lastAccess = new CLabel(timesGroup, SWT.NONE);
		lastAccess.setText(format(entryToEdit.getLastAccess()));

		final CLabel changedLbl = new CLabel(timesGroup, SWT.NONE);
		changedLbl.setText(Messages.getString("EditDialog.Changed")); //$NON-NLS-1$

		changed = new CLabel(timesGroup, SWT.NONE);
		changed.setText(format(entryToEdit.getLastChange()));

		final CLabel passwordChangeLbl = new CLabel(timesGroup, SWT.NONE);
		passwordChangeLbl.setText(Messages.getString("EditDialog.PasswordChange")); //$NON-NLS-1$

		passwordChange = new CLabel(timesGroup, SWT.NONE);
		passwordChange.setText(format(entryToEdit.getLastPwChange()));
	}

	private String generatePassword() {
		String BASE_LETTERS 								= String.valueOf(PassphraseUtils.LOWERCASE_CHARS);
		String BASE_DIGITS 									= String.valueOf(PassphraseUtils.DIGIT_CHARS);
        String BASE_LETTERS_EASY 							= "abcdefghjkmnpqrstuvwxyz"; //$NON-NLS-1$
        String BASE_DIGITS_EASY 							= "23456789"; //$NON-NLS-1$
		String BASE_SYMBOLS 								= "!@#$%^&*()"; //$NON-NLS-1$
		StringBuilder pwSet = new StringBuilder();

		UserPreferences.reload(); // make sure we have a fresh copy
		UserPreferences preferenceStore = UserPreferences.getInstance();

		String passwordLengthStr = preferenceStore.getString(JpwPreferenceConstants.DEFAULT_PASSWORD_LENGTH);
		int passwordLength = 0;
		if (passwordLengthStr != null && passwordLengthStr.trim().length() > 0) {
			passwordLength = Integer.parseInt(passwordLengthStr);
		}
		if (passwordLength <= 0)
			passwordLength = 8; //let's be sensible about this..

		boolean useLowerCase = preferenceStore.getBoolean(JpwPreferenceConstants.USE_LOWERCASE_LETTERS);
		boolean useUpperCase = preferenceStore.getBoolean(JpwPreferenceConstants.USE_UPPERCASE_LETTERS);
		boolean useDigits = preferenceStore.getBoolean(JpwPreferenceConstants.USE_DIGITS);
		boolean useSymbols = preferenceStore.getBoolean(JpwPreferenceConstants.USE_SYMBOLS);
		boolean useEasyToRead = preferenceStore.getBoolean(JpwPreferenceConstants.USE_EASY_TO_READ);

		if (useLowerCase) {
			if (useEasyToRead) {
                pwSet.append(BASE_LETTERS_EASY.toLowerCase());
            } else {
                pwSet.append(BASE_LETTERS.toLowerCase());
            }
		}

		if (useUpperCase) {
            if (useEasyToRead) {
                pwSet.append(BASE_LETTERS_EASY.toUpperCase());
            } else {
                pwSet.append(BASE_LETTERS.toUpperCase());
            }
		}

		if (useDigits) {
            if (useEasyToRead) {
                pwSet.append(BASE_DIGITS_EASY);
            } else {
                pwSet.append(BASE_DIGITS);
            }
		}


		if (useSymbols) {
			pwSet.append(BASE_SYMBOLS);
		}


		StringBuffer sb = new StringBuffer();
		if (pwSet.length() > 0) {
			SecureRandom rand = new SecureRandom();
			rand.setSeed(System.currentTimeMillis());
			for (int i = 0; i < passwordLength; i++) {
				int randOffset = rand.nextInt(pwSet.length());
				sb.append(pwSet.charAt(randOffset));
			}
		} else {
			sb.append(Messages.getString("EditDialog.MessageMustEditOptions")); //$NON-NLS-1$
		}

		return sb.toString();

	}

	private String format (Date aDate) {
		if (aDate != null)
			return DateFormat.getDateInstance().format(aDate);
		else
			return ""; //$NON-NLS-1$
	}

	private Date convertTextToDate(String fieldText) throws ParseException {
		Date expireDate = DateFormat.getDateInstance().parse(fieldText);
		Calendar cal = Calendar.getInstance();
		cal.setTime(expireDate);
		int year = cal.get(Calendar.YEAR);
		if (year < 2000) {
			if (year < 100)
				year += 2000; // avoid years like 07 passing as 0007 (Linux /
								// DE)
			else
				year += 100; // avoid years like 07 passing as 1907 (Win / US)
			cal.set(Calendar.YEAR, year);
			expireDate = cal.getTime();
		}
		return expireDate;
	}
	
    /**
     * This method is called whenever the lock state of the application changes.
     *
     * @param o   the observable LockState object.
     * @param arg the Boolean value that the lock state has been set to.
     */
    // todo move this method into a super-class (abstract LockStateObserver) if we have another similar dialog
    public void update(Observable o, Object arg) {
        if((o instanceof LockState) && (arg instanceof Boolean)) {
        	// we expect do be called on the swt event thread, so we simply do: 
        	final boolean lockState = (Boolean)arg;
       		shell.setVisible(! lockState);
            //shell.setActive(); // always??
        	int i = 0;
        }
    }

}
