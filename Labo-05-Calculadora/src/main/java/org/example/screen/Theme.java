package org.example.screen;

import java.awt.Color;
import java.awt.Font;

/** Colores y fuentes de toda la interfaz (tema claro, tipo papel). */
public final class Theme {

    private Theme() {
    }

    public static final Color FONDO     = new Color(0xF3EFE7);
    public static final Color TARJETA   = new Color(0xFFFFFF);
    public static final Color BORDE     = new Color(0xDDD5C6);
    public static final Color TEXTO     = new Color(0x2B2A27);
    public static final Color TENUE     = new Color(0x857D70);
    public static final Color SUAVE     = new Color(0xE9E0CF);

    public static final Color OPERADOR  = new Color(0x2A7F7A);
    public static final Color CALCULAR  = new Color(0xE07A3F);
    public static final Color EXITO     = new Color(0x2E7D4F);
    public static final Color FALLA     = new Color(0xB3412E);
    public static final Color AVISO     = new Color(0xA86B00);

    public static Font normal(int estilo, int tamano) {
        return new Font(Font.DIALOG, estilo, tamano);
    }

    public static Font numeros(int estilo, int tamano) {
        return new Font(Font.MONOSPACED, estilo, tamano);
    }
}
