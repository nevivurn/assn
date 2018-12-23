`timescale 1ns / 1ps

module Brightness(hwclk, set, reset, in, out);
input wire hwclk, set, reset;
input wire [6:0] in;
output reg [6:0] out = 7'b0000000;

reg [1:0] val = 2'd0;
reg [2:0] cnt = 3'd7;
always @(posedge hwclk) begin
	cnt <= cnt-1;
	out <= in;
	if (cnt < 3'd2*val) out <= 7'b0000000;
	if (reset) val <= 0;
	else if (set) val <= val+1;
end

endmodule
