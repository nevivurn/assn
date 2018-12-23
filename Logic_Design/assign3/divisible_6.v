`timescale 1ns / 1ps

module divisible_2(
    input [3:0] x,
	 output reg div
	 );
always @ (x) begin
	div = x%2 == 0;
end
endmodule

module divisible_3(
    input [3:0] x,
	 output reg div
	 );
always @ (x) begin
	div = x%3 == 0;
end
endmodule

module divisible_6(
    input [3:0] x,
	 output div
	 );

wire d2, d3;
divisible_2 div2(x, d2);
divisible_3 div3(x, d3);
and(div, d2, d3);
endmodule
