/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.xviewer.action;

import java.util.logging.Level;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.nebula.widgets.xviewer.Activator;
import org.eclipse.nebula.widgets.xviewer.IXViewerLabelProvider;
import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.util.internal.HtmlUtil;
import org.eclipse.nebula.widgets.xviewer.util.internal.XViewerLog;
import org.eclipse.nebula.widgets.xviewer.util.internal.dialog.HtmlDialog;
import org.eclipse.nebula.widgets.xviewer.util.internal.images.XViewerImageCache;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class ViewSelectedCellDataAction extends Action {

   private final XViewer xViewer;
   private final Option option;
   private final Clipboard clipboard;
   public static enum Option {
      View,
      Copy
   }

   public ViewSelectedCellDataAction(XViewer xViewer, Clipboard clipboard, Option option) {
      super(option.name() + " Selected Cell Data");
      this.xViewer = xViewer;
      this.clipboard = clipboard;
      this.option = option;
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return XViewerImageCache.getImageDescriptor("report.gif");
   }

   @Override
   public void run() {
      try {
         TreeColumn treeCol = xViewer.getRightClickSelectedColumn();
         TreeItem treeItem = xViewer.getRightClickSelectedItem();
         if (treeCol != null) {
            XViewerColumn xCol = (XViewerColumn) treeCol.getData();
            String data = null;

            if (xCol instanceof IXViewerValueColumn) {
               data =
                  ((IXViewerValueColumn) xCol).getColumnText(treeItem.getData(), xCol,
                     xViewer.getRightClickSelectedColumnNum());
            } else {
               data =
                  ((IXViewerLabelProvider) xViewer.getLabelProvider()).getColumnText(treeItem.getData(), xCol,
                     xViewer.getRightClickSelectedColumnNum());
            }
            if (data != null && !data.equals("")) {
               if (option == Option.View) {
                  String html = HtmlUtil.simplePage(HtmlUtil.pre(HtmlUtil.textToHtml(data)));
                  new HtmlDialog(treeCol.getText() + " Data", treeCol.getText() + " Data", html).open();
               } else {
                  clipboard.setContents(new Object[] {data}, new Transfer[] {TextTransfer.getInstance()});
               }
            }
         }
      } catch (Exception ex) {
         XViewerLog.logAndPopup(Activator.class, Level.SEVERE, ex);
      }
   }

}
