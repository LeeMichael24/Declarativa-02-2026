package org.example.screen.components;

import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import org.example.screen.Theme;

/**
 * Boton dibujado a mano: rectangulo redondeado con un borde inferior mas
 * oscuro que da relieve. Al presionarlo baja y el relieve desaparece.
 */
public class Buttons extends JButton {

    private static final int ARCO = 14;
    private static final int RELIEVE = 3;

    private final Color fondo;

    public Buttons(String texto, Color fondo, Color letra, int tamFuente) {
        super(texto);
        this.fondo = fondo;

        setFont(Theme.normal(Font.BOLD, tamFuente));
        setForeground(letra);
        setBorder(new EmptyBorder(8, 18, 8 + RELIEVE, 18));
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setRolloverEnabled(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    /** Para los botones que deben medir siempre lo mismo (ej. los operadores). */
    public Buttons fijo(int ancho, int alto) {
        Dimension d = new Dimension(ancho, alto);
        setPreferredSize(d);
        setMinimumSize(d);
        setMaximumSize(d);
        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        ButtonModel m = getModel();
        boolean hundido = m.isArmed() && m.isPressed();
        int ancho = getWidth() - 1;
        int alto = getHeight() - 1 - RELIEVE;
        int y = hundido ? RELIEVE : 0;

        if (!hundido) {
            g2.setColor(mezclar(fondo, Color.BLACK, 0.22f));
            g2.fillRoundRect(0, RELIEVE, ancho, alto, ARCO, ARCO);
        }
        g2.setColor(m.isRollover() ? mezclar(fondo, Color.WHITE, 0.15f) : fondo);
        g2.fillRoundRect(0, y, ancho, alto, ARCO, ARCO);
        g2.setColor(mezclar(fondo, Color.BLACK, 0.12f));
        g2.drawRoundRect(0, y, ancho, alto, ARCO, ARCO);

        // El texto se centra a mano para que baje junto con el boton
        g2.setFont(getFont());
        g2.setColor(getForeground());
        FontMetrics fm = g2.getFontMetrics();
        int tx = (getWidth() - fm.stringWidth(getText())) / 2;
        int ty = y + (alto - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(getText(), tx, ty);
        g2.dispose();
    }

    /** Mezcla dos colores: t = 0 deja a, t = 1 deja b. */
    private static Color mezclar(Color a, Color b, float t) {
        return new Color(
                Math.round(a.getRed() + (b.getRed() - a.getRed()) * t),
                Math.round(a.getGreen() + (b.getGreen() - a.getGreen()) * t),
                Math.round(a.getBlue() + (b.getBlue() - a.getBlue()) * t));
    }
}
