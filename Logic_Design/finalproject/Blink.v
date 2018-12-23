`timescale 1ns / 1ps

module Blink(clk, enable, in, out);
input wire clk, enable;
input wire [6:0] in;
output wire [6:0] out;

wire cycle;
Clock #(7'd20) clkCycle(clk, cycle);

reg show = 1;
always @(posedge clk) begin
	if (!enable) show <= 1;
	else if (cycle) show <= ~show;
end

assign out = show ? in : 7'b0000000;

endmodule
