program teste; {programa exemplo}
var
	valor1: integer;
	valor2: real;
	valor: integer;
	NUMERO: integer;
	valor3: integer;

procedure calcula_percentual (n1:real; per1:real); 
begin 
    per1 := n1 * (21/100); 
end

procedure calcula_2 (n1:integer; per1:real); 
begin 
    per1 := n1 * (54*33); 
end

begin
	valor1 := 10;
	valor2 := valor1 + 5;
	NUMERO := 3 + 5 + 7 - 9;

	if 3 > 4 then
	begin
		valor := 30;
	end;

	if 3 > 4
	then begin
		valor := 30;
	end
	else begin
		valor2 := valor2+1;
	end;

	while valor > 0 do
	begin
		valor := valor - 1;
	end;

	while 3 > 2 do
    begin
        valor3 := 4 + 3;
    end;

end.

