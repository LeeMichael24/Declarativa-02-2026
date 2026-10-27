# Laboratorio 5 — Calculadora básica con Prolog + Java

Partimos de la calculadora de derivadas e integrales de la Guía 5 y la
convertimos en una **calculadora de las cuatro operaciones básicas**
(suma, resta, multiplicación y división). Se mantiene la misma idea de la
guía: una interfaz en **Java Swing** que le manda la operación a un archivo
`.pl` por medio de **tuProlog**, y Prolog es quien calcula el resultado.

## Integrantes

| Nombre | Carnet |
|--------|--------|
| Lee Michael Fuentes Narvaez | 00140221 |

## Estructura de archivos

Es la misma estructura que pide la guía:

```
Labo-05-Calculadora/
├── pom.xml
└── src/main/
    ├── java/org.example/
    │   ├── screen
    │   │   ├── components
    │   │   │   ├── Buttons.java       ← botón con relieve
    │   │   │   ├── PanelResult.java   ← tarjeta con el resultado
    │   │   │   └── TextField.java     ← campo donde se escribe la operación
    │   │   ├── Theme.java             ← colores y fuentes
    │   │   └── Window.java            ← ventana principal
    │   ├── Main.java                  ← carga Prolog y abre la ventana
    │   └── Connection.java            ← consulta calculo.pl con tuProlog
    └── resources/
        └── calculo.pl                 ← reglas en Prolog
```

## ¿Cómo funciona?

```
 Usuario escribe        Connection arma la consulta          Prolog responde
 ───────────────        ───────────────────────────          ───────────────
 12 × (3 − 1)    ──►    calcular((12 * (3 - 1)), R, Op).  ──►  R = ok(24)
                                                               Op = producto
```

1. Se escribe la operación en el campo (o con los botones `+ − × ÷ ( )`).
2. `Connection` cambia los símbolos de pantalla (`×`, `÷`, `−`) por los de
   Prolog (`*`, `/`, `-`) y consulta `calcular/3`.
3. En Prolog la operación ya es un **término**, o sea un árbol:
   `12 * (3 - 1)` es `*(12, -(3, 1))`. El predicado `evaluar/2` lo recorre
   de forma recursiva.
4. Prolog responde `ok(Valor)` o `error(Mensaje)` y la ventana lo muestra.

### Reglas de `calculo.pl`

| Predicado | Qué hace |
|-----------|----------|
| `operador(Simbolo, Operacion)` | Hechos: relacionan `+ - * /` con `suma`, `resta`, `producto` y `cociente`. |
| `aplicar(Operacion, A, B, R)` | Una regla por operación. En la división se revisa primero si el divisor es 0 y el corte (`!`) evita que se intente dividir. |
| `evaluar(Expresion, R)` | Caso base: un número. Caso recursivo: separa `Izq op Der` con `=..` y evalúa cada lado. |
| `combinar(Op, RI, RD, R)` | Si un lado ya trae `error(...)`, el error sube sin calcular nada más. |
| `sin_desborde(Exacto, Aprox, R)` | Los enteros de tuProlog son de 64 bits; si el resultado es enorme se usa la versión con decimales para no dar un número equivocado. |
| `operacion_principal(E, Op)` | La última operación que se hace (la raíz del árbol). |
| `calcular(E, R, Op)` | Punto de entrada que consulta Java. |

La división entre cero la detecta **Prolog**, no Java:

```prolog
?- calcular(8 / (2 - 2), R, Op).
R = error('No se puede dividir entre cero'),
Op = cociente.
```

## Cómo ejecutarlo

Se necesita **Java 17 o superior**.

- **IntelliJ IDEA:** abrir la carpeta `Labo-05-Calculadora` como proyecto Maven
  (descarga tuProlog solo) y ejecutar `Main.java`.
- **Maven:** `mvn compile exec:java`

## Uso

- Escribir la operación y presionar **Calcular** o **Enter**.
- Los botones de **Probar con** cargan ejemplos.
- **Borrar** o **Esc** limpia todo.
- **Copiar resultado** copia el número al portapapeles.
- Al escribir, `*` `/` `-` se cambian por `×` `÷` `−`, y no se aceptan letras.

## Pruebas realizadas

| Operación | Resultado | Operación principal |
|-----------|-----------|---------------------|
| `15 + 27` | `42` | Suma |
| `100 − 35.5` | `64.5` | Resta |
| `12 × (3 − 1)` | `24` | Multiplicación |
| `7 ÷ 2` | `3.5` | División |
| `2 + 3 × 4` | `14` | Suma (la `×` se hace primero) |
| `−(4 + 1) × 2` | `-10` | Multiplicación |
| `0.1 + 0.2` | `0.3` | Suma |
| `8 ÷ 0` | *No se puede dividir entre cero* | División |
| `5 ×` | *Operación incompleta o mal escrita* | — |
