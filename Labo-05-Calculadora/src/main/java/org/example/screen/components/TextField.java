package org.example.screen.components;

import org.example.screen.Theme;

import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * Campo donde se escribe la operacion. Solo deja escribir numeros,
 * operadores y parentesis, y cambia * / - por × ÷ − para que se lea mejor.
 */
public class TextField extends JTextField {

    private static final String ACEPTADOS = "0123456789.()+×÷− ";

    private final String sugerencia;

    public TextField(String sugerencia) {
        super(22);
        this.sugerencia = sugerencia;

        setFont(Theme.numeros(Font.PLAIN, 22));
        setForeground(Theme.TEXTO);
        setBackground(Theme.TARJETA);
        setCaretColor(Theme.OPERADOR);
        subrayar(Theme.BORDE);

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                subrayar(Theme.OPERADOR);
            }

            @Override
            public void focusLost(FocusEvent e) {
                subrayar(Theme.BORDE);
            }
        });

        ((AbstractDocument) getDocument()).setDocumentFilter(new SoloOperaciones());
    }

    /** Escribe un texto donde esta el cursor (lo usan los botones + − × ÷). */
    public void insertar(String texto) {
        replaceSelection(texto);
        requestFocusInWindow();
    }

    private void subrayar(Color color) {
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 3, 1, color),
                BorderFactory.createEmptyBorder(10, 14, 8, 14)));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!getText().isEmpty() || sugerencia == null) {
            return;
        }
        // Texto de ayuda en gris mientras el campo esta vacio
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setColor(Theme.TENUE);
        g2.setFont(getFont().deriveFont(Font.ITALIC));
        int base = getInsets().top + g2.getFontMetrics().getAscent();
        g2.drawString(sugerencia, getInsets().left, base);
        g2.dispose();
    }

    /** Filtra lo que se escribe o se pega en el campo. */
    private static class SoloOperaciones extends DocumentFilter {

        @Override
        public void insertString(FilterBypass fb, int pos, String texto, AttributeSet a)
                throws BadLocationException {
            replace(fb, pos, 0, texto, a);
        }

        @Override
        public void replace(FilterBypass fb, int pos, int largo, String texto, AttributeSet a)
                throws BadLocationException {
            if (texto == null) {
                super.replace(fb, pos, largo, null, a);
                return;
            }
            StringBuilder limpio = new StringBuilder();
            for (char c : texto.toCharArray()) {
                char simbolo = switch (c) {
                    case '*', 'x', 'X' -> '×';
                    case '/' -> '÷';
                    case '-' -> '−';
                    case ',' -> '.';
                    default -> c;
                };
                if (ACEPTADOS.indexOf(simbolo) >= 0) {
                    limpio.append(simbolo);
                }
            }
            if (limpio.length() < texto.length()) {
                Toolkit.getDefaultToolkit().beep();
            }
            super.replace(fb, pos, largo, limpio.toString(), a);
        }
    }
}
