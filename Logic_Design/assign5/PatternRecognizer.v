`timescale 1ns / 1ps

module PatternRecognizer(
	input clk,
	input x,
	input TYPE,
	output reg [3:0] y
);

parameter
	S0 = 3'd0,
	S1A = 3'd1, S2A = 3'd2, S3A = 3'd3, S4A = 3'd4,
	S1B = 3'd5, S2B = 3'd6, S3B = 3'd7;

reg [3:0] state = S0, next;
initial y = 4'd0;

always @ (state, x) begin
	case (state)
	S0:  next <= !x ? S1A : S1B;
	S1A: next <=  x ? S2A : S1A;
	S2A: next <= !x ? S3A : S1B;
	S3A: next <=  x ? S4A : S1A;
	S4A: next <= !x ? S3A : S1B;
	S1B: next <= !x ? S2B : S1B;
	S2B: next <=  x ? S3B : S1A;
	S3B: next <= !x ? S3A : S1B;
	endcase
end

always @ (negedge clk) state <= next;

always @ (state) begin
	if (state == S4A || (!TYPE && state == S3B))
		y <= y+1;
end

endmodule
