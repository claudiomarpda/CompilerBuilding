program teste; {program example for syntax analysis after the lexical analysis}
var
valor1: integer;
valor_2: real;

procedure my_procedure(a : integer; b : real; c : boolean);
var
d : integer;
e : real;

begin
valor1 := 10 * 1 * 2 / 2;
valor1 := 10 + 1 * 2 - 1;
valor_2 := 2.5;
end.
