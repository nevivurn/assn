`timescale 1ns / 1ps

module Debounce(clk, in, out);
input wire clk, in;
output reg out = 0;

parameter CNT = 3'd7;

reg [2:0] cnt = CNT;
always @(posedge clk) begin
	out <= 0;
	if (!in) cnt <= CNT;
	else if (cnt != 0) cnt <= cnt-1;
	else out <= 1;
end

endmodule
