package org.example.screen.components;

import org.example.screen.Theme;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

/**
 * Tarjeta donde se ve la respuesta de Prolog:
 *   - arriba, que operacion se hizo (Suma, Division, ...)
 *   - al centro, el resultado en grande
 *   - abajo, la consulta exacta que se le mando a Prolog
 */
public class PanelResult extends JPanel {

    private final JLabel lblOperacion = new JLabel("Resultado");
    private final JLabel lblValor = new JLabel(" ");
    private final JLabel lblConsulta = new JLabel(" ");

    public PanelResult() {
        super(new BorderLayout(0, 8));
        setBackground(Theme.TARJETA);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDE),
                BorderFactory.createEmptyBorder(14, 18, 14, 18)));

        lblOperacion.setFont(Theme.normal(Font.BOLD, 13));
        lblOperacion.setForeground(Theme.TENUE);

        lblValor.setFont(Theme.numeros(Font.BOLD, 34));
        lblValor.setForeground(Theme.TEXTO);

        lblConsulta.setFont(Theme.numeros(Font.PLAIN, 12));
        lblConsulta.setForeground(Theme.TENUE);

        add(lblOperacion, BorderLayout.NORTH);
        add(lblValor, BorderLayout.CENTER);
        add(lblConsulta, BorderLayout.SOUTH);
    }

    public void mostrarValor(String operacion, String valor, String consulta) {
        pintar(operacion, valor, Theme.numeros(Font.BOLD, 34), Theme.TEXTO, consulta);
    }

    public void mostrarError(String mensaje, String consulta) {
        pintar("Sin resultado", mensaje, Theme.normal(Font.BOLD, 20), Theme.FALLA, consulta);
    }

    public void limpiar() {
        pintar("Resultado", " ", lblValor.getFont(), Theme.TEXTO, "");
    }

    /** El valor que se ve ahora (vacio si hay un error o no hay nada). */
    public String getValor() {
        return lblValor.getForeground().equals(Theme.FALLA) ? "" : lblValor.getText().trim();
    }

    private void pintar(String operacion, String valor, Font fuente, Color color, String consulta) {
        lblOperacion.setText(operacion);
        lblValor.setText(valor);
        lblValor.setFont(fuente);
        lblValor.setForeground(color);
        lblConsulta.setText(consulta.isEmpty() ? " " : "?- " + consulta);
    }
}
