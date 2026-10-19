package com.mycompany.promptzal_3;

import com.formdev.flatlaf.FlatDarkLaf;
import com.mycompany.promptzal_3.ui.Colores;
import com.mycompany.promptzal_3.ui.VentanaPrincipal;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;


public class PromptZal_3 {

    public static void main(String[] args) {
        FlatDarkLaf.setup();

        UIManager.put("Component.arc", 10);
        UIManager.put("Button.arc", 10);
        UIManager.put("TextComponent.arc", 8);
        UIManager.put("Panel.background", Colores.FONDO);
        UIManager.put("Viewport.background", Colores.FONDO);
        UIManager.put("ScrollPane.background", Colores.FONDO);
        UIManager.put("TabbedPane.background", Colores.PANEL);
        UIManager.put("TabbedPane.selectedBackground", Colores.FONDO);
        UIManager.put("TabbedPane.underlineColor", Colores.ACENTO);
        UIManager.put("Table.background", Colores.PANEL);
        UIManager.put("Table.gridColor", Colores.BORDE);
        UIManager.put("Tree.background", Colores.PANEL);
        UIManager.put("ToolBar.background", Colores.PANEL);
        UIManager.put("MenuBar.background", Colores.PANEL);
        UIManager.put("MenuBar.borderColor", Colores.BORDE);
        UIManager.put("Component.accentColor", Colores.ACENTO);
        UIManager.put("Component.focusColor", Colores.ACENTO);

        SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));
    }
}
