package org.example.screen;

import org.example.Connection;
import org.example.screen.components.Buttons;
import org.example.screen.components.PanelResult;
import org.example.screen.components.TextField;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Map;

/** Ventana de la calculadora basica. */
public class Window extends JFrame {

    // Operaciones de prueba que se cargan con un clic
    private static final String[] PRUEBAS = {
            "15 + 27", "100 − 35.5", "12 × (3 − 1)", "7 ÷ 2", "8 ÷ 0"
    };

    // Botones que escriben en el campo: lo que dice el boton -> lo que inserta
    private static final String[][] SIMBOLOS = {
            {"+", " + "}, {"−", " − "}, {"×", " × "}, {"÷", " ÷ "}, {"(", "("}, {")", ")"}
    };

    // Nombre que devuelve Prolog -> nombre que se muestra
    private static final Map<String, String> OPERACIONES = Map.of(
            "suma", "Suma",
            "resta", "Resta",
            "producto", "Multiplicación",
            "cociente", "División",
            "ninguna", "Número");

    private final Connection prolog;
    private final TextField campo = new TextField("Ej: 12 × (3 − 1)");
    private final PanelResult resultado = new PanelResult();
    private final JLabel estado = new JLabel(" ");

    public Window(Connection prolog) {
        super("Calculadora básica en Prolog");
        this.prolog = prolog;

        campo.addActionListener(e -> calcular());   // Enter = calcular

        Box columna = Box.createVerticalBox();
        apilar(columna, titulo("Operación"), 6);
        apilar(columna, campo, 10);
        apilar(columna, filaPruebas(), 16);
        apilar(columna, filaBotones(), 20);
        apilar(columna, resultado, 12);
        apilar(columna, filaEstado(), 0);

        JPanel contenido = new JPanel(new BorderLayout(0, 18));
        contenido.setBackground(Theme.FONDO);
        contenido.setBorder(BorderFactory.createEmptyBorder(22, 26, 18, 26));
        contenido.add(encabezado(), BorderLayout.NORTH);
        contenido.add(columna, BorderLayout.CENTER);
        setContentPane(contenido);

        // Esc borra todo
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "limpiar");
        getRootPane().getActionMap().put("limpiar", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limpiar();
            }
        });

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(680, 560));
        pack();
        setLocationRelativeTo(null);
        avisar("Escribe una operación y presiona Calcular o Enter", Theme.TENUE);
    }

    // ---------------------------------------------------------------- partes de la ventana

    private JPanel encabezado() {
        JLabel nombre = new JLabel("Calculadora básica");
        nombre.setFont(Theme.normal(Font.BOLD, 24));
        nombre.setForeground(Theme.TEXTO);

        JLabel detalle = new JLabel("Suma, resta, multiplicación y división resueltas con reglas en Prolog");
        detalle.setFont(Theme.normal(Font.PLAIN, 13));
        detalle.setForeground(Theme.TENUE);

        JPanel cabecera = new JPanel(new BorderLayout(0, 4));
        cabecera.setOpaque(false);
        cabecera.add(nombre, BorderLayout.NORTH);
        cabecera.add(detalle, BorderLayout.SOUTH);
        return cabecera;
    }

    private JPanel filaPruebas() {
        JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        fila.setOpaque(false);

        JLabel texto = new JLabel("Probar con:");
        texto.setFont(Theme.normal(Font.PLAIN, 12));
        texto.setForeground(Theme.TENUE);
        fila.add(texto);

        for (String prueba : PRUEBAS) {
            Buttons b = new Buttons(prueba, Theme.SUAVE, Theme.TEXTO, 12);
            b.addActionListener(e -> {
                campo.setText(prueba);
                campo.requestFocusInWindow();
            });
            fila.add(b);
        }
        return fila;
    }

    private JPanel filaBotones() {
        JPanel operadores = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        operadores.setOpaque(false);
        for (String[] s : SIMBOLOS) {
            boolean esParentesis = s[0].equals("(") || s[0].equals(")");
            Buttons b = new Buttons(s[0],
                    esParentesis ? Theme.SUAVE : Theme.OPERADOR,
                    esParentesis ? Theme.TEXTO : Color.WHITE, 20).fijo(48, 46);
            b.addActionListener(e -> campo.insertar(s[1]));
            operadores.add(b);
        }

        Buttons borrar = new Buttons("Borrar", Theme.SUAVE, Theme.TEXTO, 14);
        borrar.addActionListener(e -> limpiar());
        Buttons calcular = new Buttons("Calcular  =", Theme.CALCULAR, Color.WHITE, 15);
        calcular.addActionListener(e -> calcular());

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        acciones.setOpaque(false);
        acciones.add(borrar);
        acciones.add(calcular);

        JPanel fila = new JPanel(new BorderLayout());
        fila.setOpaque(false);
        fila.add(operadores, BorderLayout.WEST);
        fila.add(acciones, BorderLayout.EAST);
        return fila;
    }

    private JPanel filaEstado() {
        estado.setFont(Theme.normal(Font.PLAIN, 12));

        Buttons copiar = new Buttons("Copiar resultado", Theme.SUAVE, Theme.TEXTO, 12);
        copiar.addActionListener(e -> copiar());

        JPanel fila = new JPanel(new BorderLayout());
        fila.setOpaque(false);
        fila.add(estado, BorderLayout.CENTER);
        fila.add(copiar, BorderLayout.EAST);
        return fila;
    }

    // ---------------------------------------------------------------- acciones

    private void calcular() {
        Connection.Respuesta r = prolog.consultar(campo.getText());
        if (r.exito()) {
            resultado.mostrarValor(OPERACIONES.getOrDefault(r.operacion(), r.operacion()), r.texto(), r.consulta());
            avisar("Listo, resuelto por Prolog", Theme.EXITO);
        } else {
            resultado.mostrarError(r.texto(), r.consulta());
            avisar(r.consulta().isEmpty() ? "Revisa lo que escribiste" : "Prolog respondió con un error", Theme.FALLA);
        }
        campo.requestFocusInWindow();
    }

    private void limpiar() {
        campo.setText("");
        resultado.limpiar();
        avisar("Campo limpio", Theme.TENUE);
        campo.requestFocusInWindow();
    }

    private void copiar() {
        String valor = resultado.getValor();
        if (valor.isEmpty()) {
            avisar("No hay resultado para copiar", Theme.AVISO);
            return;
        }
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(valor), null);
        avisar("Se copió " + valor, Theme.OPERADOR);
    }

    private void avisar(String mensaje, Color color) {
        estado.setText(mensaje);
        estado.setForeground(color);
    }

    // ---------------------------------------------------------------- ayudas de armado

    private static void apilar(Box columna, JComponent c, int separacion) {
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        // Solo el panel de resultado crece si la ventana se agranda; las demas filas no
        if (!(c instanceof PanelResult)) {
            c.setMaximumSize(new Dimension(Integer.MAX_VALUE, c.getPreferredSize().height));
        }
        columna.add(c);
        if (separacion > 0) {
            columna.add(Box.createVerticalStrut(separacion));
        }
    }

    private static JLabel titulo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(Theme.normal(Font.BOLD, 13));
        l.setForeground(Theme.TENUE);
        return l;
    }
}
