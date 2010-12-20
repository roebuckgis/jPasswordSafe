/*
 * Copyright (c) 2008-2010 David Muller <roxon@users.sourceforge.net>.
 * All rights reserved. Use of the code is allowed under the
 * Artistic License 2.0 terms, as specified in the LICENSE file
 * distributed with this code, or available from
 * http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pwsafe.passwordsafeswt.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;
import org.pwsafe.lib.datastore.PwsEntryBean;
import org.pwsafe.lib.file.PwsFieldTypeV2;
import org.pwsafe.lib.file.PwsFieldTypeV3;
import org.pwsafe.lib.file.PwsRecord;
import org.pwsafe.lib.file.PwsRecordV3;

/**
 * Label provider for tree viewer.
 * Also implements {@link org.eclipse.jface.viewers.ITableLabelProvider} to allow for tree columns.
 *
 * @author Glen Smith
 */
public class PasswordTreeLabelProvider extends AbstractTableLabelProvider implements ILabelProvider {

	private static final Log log = LogFactory.getLog(PasswordTreeLabelProvider.class);
    
	/**
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object node) {
		return null;
	}


    /* (non-Javadoc)
     * TODO: Merge this with the getColumnText method from {@link PasswordTableLabelProvider}.
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
     */
    public String getColumnText(Object element, int columnIndex) {
        String result = "";
        switch (columnIndex) {
        case 0:
            result = "<unknown node type>";
            if (element instanceof String) {
                result = element.toString();
            } else if (element instanceof PwsEntryBean) {
            	PwsEntryBean theEntry = (PwsEntryBean) element;
                result = theEntry.getTitle();
              } else if (element instanceof PwsRecord) { // deprecated
                PwsRecord record = (PwsRecord) element;
                if (record instanceof PwsRecordV3) {
                	result = PwsEntryBean.getSafeValue(record, PwsFieldTypeV3.TITLE);
                } else {
                	result = PwsEntryBean.getSafeValue(record, PwsFieldTypeV2.TITLE);
                }
            } else if (element instanceof PasswordTreeContentProvider.TreeGroup) {
                result = element.toString();
            }
            break;
        case 1:
        	if (element instanceof PwsEntryBean) {
            	PwsEntryBean theEntry = (PwsEntryBean) element;
                result = theEntry.getUsername();
        	} else if (element instanceof PwsRecord) {// deprecated
            	if (element instanceof PwsRecordV3) {
            		result = PwsEntryBean.getSafeValue((PwsRecord) element, PwsFieldTypeV3.USERNAME);
            	} else {
            		result = PwsEntryBean.getSafeValue((PwsRecord) element, PwsFieldTypeV2.USERNAME);	
            	}
            }
            break;
        case 2:
        	if (element instanceof PwsEntryBean) {
            	PwsEntryBean theEntry = (PwsEntryBean) element;
                result = theEntry.getNotes();
        	} else if (element instanceof PwsRecord) {// deprecated
            	if (element instanceof PwsRecordV3) {
            		result = PwsEntryBean.getSafeValue((PwsRecord) element, PwsFieldTypeV3.NOTES);
            	} else {
            		result = PwsEntryBean.getSafeValue((PwsRecord) element, PwsFieldTypeV2.NOTES);	
            	}
            }
        	if (result != null) {
            	result = result.replace('\t',' ').replace('\r', ' ');//.replace('\n',' ')
        	}
            break;
        case 3:
        	if (element instanceof PwsEntryBean) {
            	PwsEntryBean theEntry = (PwsEntryBean) element;
                result = theEntry.getPassword() != null ? theEntry.getPassword().toString() : null;
        	} else if (element instanceof PwsRecord) {// deprecated
            	if (element instanceof PwsRecordV3) {
            		result = PwsEntryBean.getSafeValue((PwsRecord) element, PwsFieldTypeV3.PASSWORD);
            	} else {
            		result = PwsEntryBean.getSafeValue((PwsRecord) element, PwsFieldTypeV2.PASSWORD);	
            	}
            }
            break;
        }
        return result;
    }


	/**
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object node) {
        String result = "<unknown node type>";
		if (node instanceof String) {
			result = node.toString();
		} else if (node instanceof PwsEntryBean) {
			PwsEntryBean theEntry = (PwsEntryBean) node;
			result = theEntry.getTitle();
		} else if (node instanceof PasswordTreeContentProvider.TreeGroup) {
		    result = node.toString();
		} else if (node instanceof PwsRecord) { // deprecated
		    PwsRecord record = (PwsRecord) node;
        	if (record instanceof PwsRecordV3) {
        		result = PwsEntryBean.getSafeValue(record, PwsFieldTypeV3.TITLE);
        	} else {
        		result = PwsEntryBean.getSafeValue(record, PwsFieldTypeV2.TITLE);	
        	}
		} 
        return result;
	}

}
