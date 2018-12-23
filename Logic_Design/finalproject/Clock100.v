`timescale 1ns / 1ps

module Clock100(clk, out);
input wire clk;
output reg out = 0;

parameter FREQ = 19'd500000;

reg [18:0] cnt = FREQ;
always @(posedge clk) begin
	if (cnt != 0) begin
		cnt <= cnt-1;
		out <= 0;
	end else begin
		cnt <= FREQ-1;
		out <= 1;
	end
end

endmodule
