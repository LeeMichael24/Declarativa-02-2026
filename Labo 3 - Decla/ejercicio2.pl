% Caso base: Si N es 1, la suma es 1. Se debe colocar primero.
suma_n(1, 1).

% Caso recursivo: Si N > 1, sumamos N al resultado de la suma de N-1.
suma_n(N, R) :-
    N > 1,
    N_Ant is N - 1,
    suma_n(N_Ant, R_Ant),
    R is N + R_Ant.