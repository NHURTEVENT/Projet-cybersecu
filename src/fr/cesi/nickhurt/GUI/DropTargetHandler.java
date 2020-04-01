package fr.cesi.nickhurt.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;

public class DropTargetHandler implements DropTargetListener {

    protected void processDrag(DropTargetDragEvent dtde) {
        if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            dtde.acceptDrag(DnDConstants.ACTION_COPY);
        } else {
            dtde.rejectDrag();
        }
    }

    @Override
    public void dragEnter(DropTargetDragEvent dtde) {
        processDrag(dtde);
//            SwingUtilities.invokeLater(new DragUpdate(true, dtde.getLocation()));
//            repaint();
    }

    @Override
    public void dragOver(DropTargetDragEvent dtde) {
        processDrag(dtde);
//            SwingUtilities.invokeLater(new DragUpdate(true, dtde.getLocation()));
//            repaint();
    }

    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {
    }

    @Override
    public void dragExit(DropTargetEvent dte) {
//            SwingUtilities.invokeLater(new DragUpdate(false, null));
//            repaint();
    }

    @Override
    public void drop(DropTargetDropEvent dtde) {

//            SwingUtilities.invokeLater(new DragUpdate(false, null));

        Transferable transferable = dtde.getTransferable();
        if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            dtde.acceptDrop(dtde.getDropAction());
            try {

                List transferData = (List) transferable.getTransferData(DataFlavor.javaFileListFlavor);
                if (transferData != null) {
                    importFiles(transferData);
                    dtde.dropComplete(true);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            dtde.rejectDrop();
        }
    }

    protected void importFiles(final List files) {
        Runnable run = () -> System.out.println("dropped " + files.getItemCount() + " files");
        SwingUtilities.invokeLater(run);
    }

}
