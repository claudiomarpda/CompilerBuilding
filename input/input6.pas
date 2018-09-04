program teste; {program example for syntax analysis after the lexical analysis}
var
valor1: integer;
valor_2: real;

procedure pro();
begin
end;

procedure pro1(a : integer);
begin
end;

procedure pro2(a : integer; b : real);
begin
    writeln(a);
    writeln(b);
end;

procedure pro3(a : integer; b : real; c : boolean);
var
d : integer;
e : real;
boo : boolean;
begin
end;

begin
    valor1 := 10 * (1 * 2) / 2;
    valor1 := 10 + 1 * 2 - 1;
    valor_2 := 2.5;
    boo := false;
    boo := true;
    boo := not boo;
    boo := true or false;
    boo := not true or not false;
    boo := false and false;
    boo := true or true;
    pro();
    pro1(1);
    pro2(1, 2);
    pro3(1, 2, true);
end.
