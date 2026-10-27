% =====================================================================
%  calculo.pl  -  Laboratorio 5
%  Base de conocimiento de la calculadora basica (+, -, *, /).
%
%  La expresion llega desde Java ya convertida en un termino de Prolog.
%  Por ejemplo, el texto  (8 + 2) * 3  es el termino  *(+(8, 2), 3),
%  o sea un arbol donde cada nodo es una operacion y cada hoja un numero.
%
%  evaluar/2 recorre ese arbol y siempre responde con uno de dos
%  terminos:
%      ok(Valor)        -> la expresion se pudo calcular
%      error(Mensaje)   -> algo salio mal (ej. dividir entre cero)
%
%  Asi el error se detecta aqui en Prolog y "sube" por el arbol hasta
%  llegar a Java, en lugar de revisar el texto desde la interfaz.
% =====================================================================


% ---------------------------------------------------------------------
%  HECHOS
%  operador(Simbolo, Operacion): los cuatro simbolos que entiende la
%  calculadora y el nombre de la operacion que representa cada uno.
%  Los simbolos van entre comillas porque tuProlog no los acepta
%  sueltos como argumento ('+' y + son el mismo atomo).
% ---------------------------------------------------------------------
operador('+', suma).
operador('-', resta).
operador('*', producto).
operador('/', cociente).


% ---------------------------------------------------------------------
%  aplicar(Operacion, A, B, Resultado)
%  Una regla por cada operacion basica; A y B ya son numeros.
% ---------------------------------------------------------------------
aplicar(suma, A, B, ok(R)) :-
    Exacto is A + B,
    Aprox is A * 1.0 + B,
    sin_desborde(Exacto, Aprox, R).

aplicar(resta, A, B, ok(R)) :-
    Exacto is A - B,
    Aprox is A * 1.0 - B,
    sin_desborde(Exacto, Aprox, R).

aplicar(producto, A, B, ok(R)) :-
    Exacto is A * B,
    Aprox is A * 1.0 * B,
    sin_desborde(Exacto, Aprox, R).

% El divisor cero se revisa primero; el corte evita que Prolog
% intente la regla de abajo y termine dividiendo entre cero.
aplicar(cociente, _, B, error('No se puede dividir entre cero')) :-
    B =:= 0, !.

% Se multiplica por 1.0 para que 7 / 2 de 3.5 y no una division entera.
aplicar(cociente, A, B, ok(R)) :-
    R is (A * 1.0) / B.


% ---------------------------------------------------------------------
%  sin_desborde(Exacto, Aprox, R)
%  Los enteros de tuProlog son de 64 bits: si un resultado pasa de ese
%  limite, el valor exacto sale mal sin avisar. Por eso tambien se
%  calcula con decimales (Aprox) y, si el numero es muy grande, se usa
%  ese en lugar del entero.
% ---------------------------------------------------------------------
sin_desborde(_, Aprox, Aprox) :-
    abs(Aprox) >= 9.0e18, !.
sin_desborde(Exacto, _, Exacto).


% ---------------------------------------------------------------------
%  combinar(Operacion, ResIzq, ResDer, Resultado)
%  Si alguno de los dos lados ya trae un error, ese error se devuelve
%  tal cual; si ambos estan bien, se aplica la operacion.
% ---------------------------------------------------------------------
combinar(_, error(M), _, error(M)) :- !.
combinar(_, _, error(M), error(M)) :- !.
combinar(Operacion, ok(A), ok(B), R) :-
    aplicar(Operacion, A, B, R).


% ---------------------------------------------------------------------
%  evaluar(Expresion, Resultado)
% ---------------------------------------------------------------------

% Caso base: un numero ya esta evaluado.
evaluar(N, ok(N)) :-
    number(N), !.

% Signo menos delante de algo, ej. -(4 + 1). Se ve como 0 - X.
evaluar(-X, R) :- !,
    evaluar(X, RX),
    combinar(resta, ok(0), RX, R).

% Caso recursivo: Izq <simbolo> Der.
% =.. separa el termino en [Simbolo, Izq, Der] y el hecho operador/2
% dice si ese simbolo es una de las cuatro operaciones.
evaluar(Expresion, R) :-
    Expresion =.. [Simbolo, Izq, Der],
    operador(Simbolo, Operacion), !,
    evaluar(Izq, RIzq),
    evaluar(Der, RDer),
    combinar(Operacion, RIzq, RDer, R).

% Cualquier otra cosa (letras, funciones, etc.) no es valida.
evaluar(_, error('Expresión no válida')).


% ---------------------------------------------------------------------
%  operacion_principal(Expresion, Operacion)
%  La operacion que se hace al final (la raiz del arbol).
%  En 2 + 3 * 4 la principal es suma, porque * se resuelve antes.
% ---------------------------------------------------------------------
operacion_principal(Expresion, Operacion) :-
    Expresion =.. [Simbolo, _, _],
    operador(Simbolo, Operacion), !.
operacion_principal(_, ninguna).


% ---------------------------------------------------------------------
%  calcular(Expresion, Resultado, Operacion)
%  Punto de entrada: es lo unico que Java consulta.
% ---------------------------------------------------------------------
calcular(Expresion, Resultado, Operacion) :-
    evaluar(Expresion, Resultado),
    operacion_principal(Expresion, Operacion).
