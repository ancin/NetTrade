package net.sf.jasperreports.swing;

import net.sf.jasperreports.engine.JasperPrint;

public class EditableJRViewer extends JRViewer {

    /**  */
    private static final long serialVersionUID = 1L;

    public EditableJRViewer(JasperPrint jrPrint) {
        super(jrPrint);
    }

    @Override
    protected JRViewerPanel createViewerPanel() {
        return new EditableJRViewerPanel(viewerContext);
    }
}
