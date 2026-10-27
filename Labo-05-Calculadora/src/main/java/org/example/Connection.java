package org.example;

import alice.tuprolog.Prolog;
import alice.tuprolog.SolveInfo;
import alice.tuprolog.Struct;
import alice.tuprolog.Theory;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

/**
 * Conexion con Prolog: carga calculo.pl, arma la consulta calcular/3 y
 * traduce lo que responde Prolog (ok(Valor) o error(Mensaje)).
 */
public class Connection {

    /** Lo que se le devuelve a la ventana despues de cada consulta. */
    public record Respuesta(boolean exito, String texto, String operacion, String consulta) {
    }

    // Solo digitos, punto, parentesis, espacios y los 4 operadores
    private static final Pattern PERMITIDOS = Pattern.compile("[0-9.+\\-*/() ]+");

    // Los decimales se muestran con 12 cifras significativas como maximo
    private static final MathContext CIFRAS = new MathContext(12);

    private final Prolog engine = new Prolog();

    public Connection() throws IOException {
        try (InputStream in = getClass().getResourceAsStream("/calculo.pl")) {
            if (in == null) {
                throw new IOException("No se encontró calculo.pl en resources");
            }
            // Se lee como UTF-8 para que los mensajes con tildes salgan bien en cualquier sistema
            engine.setTheory(new Theory(new String(in.readAllBytes(), StandardCharsets.UTF_8)));
        } catch (alice.tuprolog.InvalidTheoryException e) {
            throw new IOException("calculo.pl tiene un error en la línea " + e.line, e);
        }
    }

    /** Recibe la operación tal como la escribió el usuario, ej. "12 × (3 − 1)". */
    public Respuesta consultar(String operacion) {
        String termino = aSintaxisProlog(operacion);
        if (termino.isEmpty()) {
            return new Respuesta(false, "Escribe una operación primero", "ninguna", "");
        }
        if (!PERMITIDOS.matcher(termino).matches()) {
            return new Respuesta(false, "Solo se permiten números y + − × ÷", "ninguna", "");
        }

        String consulta = "calcular((" + termino + "), R, Op).";
        try {
            SolveInfo info = engine.solve(consulta);
            if (!info.isSuccess()) {
                return new Respuesta(false, "Prolog no encontró respuesta", "ninguna", consulta);
            }
            Struct r = (Struct) info.getTerm("R").getTerm();
            String op = info.getTerm("Op").getTerm().toString();

            if (r.getName().equals("error")) {
                String mensaje = ((Struct) r.getArg(0).getTerm()).getName();
                return new Respuesta(false, mensaje, op, consulta);
            }
            alice.tuprolog.Number valor = (alice.tuprolog.Number) r.getArg(0).getTerm();
            if (!valor.isInteger() && !Double.isFinite(valor.doubleValue())) {
                return new Respuesta(false, "El resultado es demasiado grande", op, consulta);
            }
            return new Respuesta(true, formatear(valor), op, consulta);
        } catch (Exception e) {
            // MalformedGoalException y similares: la operación no es un término válido
            return new Respuesta(false, "Operación incompleta o mal escrita", "ninguna", consulta);
        }
    }

    /**
     * Cambia los símbolos de pantalla por los de Prolog (× → *, ÷ → /, − → -)
     * y separa los operadores con espacios, para que "3--2" llegue como "3 - - 2".
     */
    static String aSintaxisProlog(String texto) {
        String t = texto.replace('×', '*').replace('÷', '/').replace('−', '-').replace('x', '*');
        // Un "+" usado como signo (al inicio, tras "(" o tras otro operador) no
        // cambia el valor, y tuProlog no lo reconoce como prefijo: se quita
        t = t.replaceAll("(^|[(+\\-*/])\\s*\\+", "$1");
        t = t.replaceAll("([+\\-*/()])", " $1 ");
        // ".5" -> "0.5" y "5." -> "5.0", porque Prolog no acepta ninguno de los dos
        t = t.replaceAll("(?<![0-9])\\.", "0.").replaceAll("\\.(?![0-9])", ".0");
        return t.trim().replaceAll("\\s+", " ").replace("( ", "(").replace(" )", ")");
    }

    /** 7 queda "7", 6 / 3 (que da 2.0) queda "2" y 0.1 + 0.2 queda "0.3". */
    private static String formatear(alice.tuprolog.Number n) {
        if (n.isInteger()) {
            return Long.toString(n.longValue());
        }
        BigDecimal d = BigDecimal.valueOf(n.doubleValue()).round(CIFRAS).stripTrailingZeros();
        return d.signum() == 0 ? "0" : d.toPlainString();
    }
}
