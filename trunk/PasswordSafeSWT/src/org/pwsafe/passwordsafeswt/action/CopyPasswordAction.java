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
import org.eclipse.swt.dnd.Clipboard;
import org.pwsafe.lib.datastore.PwsEntryBean;
import org.pwsafe.passwordsafeswt.PasswordSafeJFace;

/**
 * Copies the password from selected item to the clipboard.
 *
 * @author Glen Smith
 */
public class CopyPasswordAction extends Action {

    public CopyPasswordAction() {
        super(Messages.getString("CopyPasswordAction.Label")); //$NON-NLS-1$
        setAccelerator( SWT.MOD1 | 'C'  );
        setImageDescriptor(ImageDescriptor.createFromURL(this.getClass().getClassLoader().getResource("org/pwsafe/passwordsafeswt/images/tool_newbar_pwd.gif"))); //$NON-NLS-1$
        setToolTipText(Messages.getString("CopyPasswordAction.Tooltip")); //$NON-NLS-1$

    }

    /**
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
	public void run() {
        PasswordSafeJFace app = PasswordSafeJFace.getApp();

        PwsEntryBean selected = app.getSelectedRecord();
        if (selected == null)
        	return;
 
        // retrieve filled Entry for sparse
        PwsEntryBean theEntry = app.getPwsDataStore().getEntry(selected.getStoreIndex());

        Clipboard cb = new Clipboard(app.getShell().getDisplay());

        app.copyToClipboard(cb, theEntry, theEntry.getPassword().toString());

        cb.dispose();
    }
}
