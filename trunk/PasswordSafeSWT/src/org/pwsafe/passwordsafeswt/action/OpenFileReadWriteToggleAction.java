/*
 * Copyright (c) 2008-2009 David Muller <roxon@users.sourceforge.net>.
 * All rights reserved. Use of the code is allowed under the
 * Artistic License 2.0 terms, as specified in the LICENSE file
 * distributed with this code, or available from
 * http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pwsafe.passwordsafeswt.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.pwsafe.passwordsafeswt.PasswordSafeJFace;
import org.pwsafe.passwordsafeswt.dialog.PasswordDialog;

/**
 * Open command that opens a new safe with the opposite read-write mode than the one opened at the moment.
 * The label and tooltip is set according to the current read-only mode.
 *
 * @author roxon
 */
public class OpenFileReadWriteToggleAction extends Action {

    public OpenFileReadWriteToggleAction() {
    	// set Labels to descriptive default first
        super(Messages.getString("OpenFileReadWriteToggleAction.Label")); //$NON-NLS-1$
        setImageDescriptor(ImageDescriptor.createFromURL(this.getClass().getClassLoader().getResource("org/pwsafe/passwordsafeswt/images/tool_newbar_open.gif"))); //$NON-NLS-1$
        setToolTipText(Messages.getString("OpenFileReadWriteToggleAction.Tooltip")); //$NON-NLS-1$
    }

    /**
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
	public void run() {
        PasswordSafeJFace app = PasswordSafeJFace.getApp();
        boolean cancelled = app.saveAppIfDirty();
        if (!cancelled) {
            FileDialog fod = new FileDialog(app.getShell(), SWT.OPEN);
            // TODO get the file extension from some enum as soon as passwordsafelib is changed to Java 5
            fod.setFilterExtensions(new String[] { "*.psafe3", "*.pws3", "*.dat", "*.*" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            fod.setFilterNames(new String[] { Messages.getString("OpenFileAction.FilterLabel.V3Files"), Messages.getString("OpenFileAction.FilterLabel.S3Files"), Messages.getString("OpenFileAction.FilterLabel.V2Files"), Messages.getString("OpenFileAction.FilterLabel.AllFiles")} ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            String fileName = fod.open();
            if (fileName != null) {
                PasswordDialog pd = new PasswordDialog(app.getShell());
                pd.setVerified(false);
                pd.setFileName(fileName);
                StringBuilder password = pd.open();
                if (password != null) {
                    try {
                        //open the safe with inverted read-write mode 
                        app.openFile(fileName, password, ! app.isReadOnly());
                    } catch (Exception e) {
                        app.displayErrorDialog(Messages.getString("OpenFileAction.ErrorDialog.Label"), Messages.getString("OpenFileAction.ErrorDialog.Message"), e); //$NON-NLS-1$ //$NON-NLS-2$
                    }
                }
            }
        }
    }

	public void setOpenReadOnlyMode(boolean enabled) {
		String label = null;
		String tooltip = null;
		if (enabled) {
			label = Messages.getString("OpenFileReadWriteToggleAction.ReadOnlyLabel"); //$NON-NLS-1$)
			tooltip = Messages.getString("OpenFileReadWriteToggleAction.ReadOnlyTooltip"); //$NON-NLS-1$)			
		} else {
			label = Messages.getString("OpenFileReadWriteToggleAction.ReadWriteLabel"); //$NON-NLS-1$)
			tooltip = Messages.getString("OpenFileReadWriteToggleAction.ReadWriteTooltip"); //$NON-NLS-1$)
		}
		
		setText(label); 
		setToolTipText(tooltip); 

		
	}

}