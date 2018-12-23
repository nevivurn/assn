`timescale 1ns / 1ps

// Mux for display output
module DisplayMux(sel, a, b, c, out);
input wire [2:0] sel;
input wire [6:0] a, b, c;
output reg [6:0] out = 7'b0000000;

always @(*) begin
	out = 7'b0000000;
	if (sel[0]) out = a;
	else if (sel[1]) out = b;
	else if (sel[2]) out = c;
end

endmodule
