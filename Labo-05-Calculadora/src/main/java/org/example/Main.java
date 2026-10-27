package org.example;

import org.example.screen.Window;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Primero se carga calculo.pl y luego se abre la ventana
                Connection prolog = new Connection();
                new Window(prolog).setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "No se pudo cargar Prolog:\n" + e.getMessage(),
                        "Calculadora básica", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
