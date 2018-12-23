`timescale 1ns / 1ps

// Makes input produce a pulse
module PulseGen(clk, in, out);
input wire clk, in;
output reg out = 0;

wire debounced;
Debounce debounce(clk, in, debounced);

reg [1:0] state = 2'd0;
always @(posedge clk) begin
	out <= 0;
	if (!debounced) state <= 0;
	else if (state == 0) state <= 1;
	else if (state == 1) begin
		state <= 2;
		out <= 1;
	end
end

endmodule
