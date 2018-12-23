`timescale 1ns / 1ps

// Translate given one-hot encoding to 3-bit opcode
module OpcodeTranslate(
	input [5:0] operator,
	output reg [2:0] out
);

always @ (operator) begin
	if (operator[0])  out <= 1;
	else if (operator[1]) out <= 2;
	else if (operator[2]) out <= 3;
	else if (operator[5]) out <= 6;
	else if (operator[4]) out <= 5;
	else if (operator[3]) out <= 4;
	else out <= 0;
end

endmodule

// Display the given number, or '-' on 16, off on other values
module Display(
	input [4:0] value,
	output reg [6:0] dsp
);

always @ (*) begin
	case (value)
		5'd0: dsp <= 7'b1111110;
		5'd1: dsp <= 7'b0110000;
		5'd2: dsp <= 7'b1101101;
		5'd3: dsp <= 7'b1111001;
		5'd4: dsp <= 7'b0110011;
		5'd5: dsp <= 7'b1011011;
		5'd6: dsp <= 7'b1011111;
		5'd7: dsp <= 7'b1110000;
		5'd8: dsp <= 7'b1111111;
		5'd9: dsp <= 7'b1111011;
		5'd10: dsp <= 7'b1110111;
		5'd11: dsp <= 7'b0011111;
		5'd12: dsp <= 7'b1001110;
		5'd13: dsp <= 7'b0111101;
		5'd14: dsp <= 7'b1001111;
		5'd15: dsp <= 7'b1000111;
		5'd16: dsp <= 7'b0000001;
		default: dsp <= 0;
	endcase
end

endmodule

// Mux for choosing 1 out of 6 outputs to be displayed
module Mux(
	input [4:0] out0,
	input [4:0] out1,
	input [4:0] out2,
	input [4:0] out3,
	input [4:0] out4,
	input [4:0] out5,
	input [4:0] out6,
	input [2:0] sel,
	output reg [4:0] out
);

always @ (*) begin
	case (sel)
		3'd0: out <= out0;
		3'd1: out <= out1;
		3'd2: out <= out2;
		3'd3: out <= out3;
		3'd4: out <= out4;
		3'd5: out <= out5;
		3'd6: out <= out6;
		default: out <= 5'bxxxx; // Cannot happen
	endcase
end

endmodule

// Simple display
module Op0(
	input [9:0] operand,
	output [4:0] out0,
	output [4:0] out1,
	output [4:0] out2,
	output [4:0] out3,
	output [4:0] out4,
	output [4:0] out5
);

// Reverse bits and extract sign for convenience
wire sign_a = operand[0];
wire [3:0] in_a = {operand[1], operand[2], operand[3], operand[4]};
wire sign_b = operand[5];
wire [3:0] in_b = {operand[6], operand[7], operand[8], operand[9]};

// Sign output
assign out0 = sign_a & (in_a != 0) ? 5'd16 : 5'd17;
assign out3 = sign_b & (in_b != 0) ? 5'd16 : 5'd17;
// TODO: decimal output. In hex, these are 0 always.
// TODO: remove leading 0
assign out1 = 5'd0;
assign out4 = 5'd0;
// Nums
assign out2 = in_a;
assign out5 = in_b;

endmodule

// Binary display
// Only take lower 6 bits
module Op1(
	input [5:0] operand,
	output [4:0] out0,
	output [4:0] out1,
	output [4:0] out2,
	output [4:0] out3,
	output [4:0] out4,
	output [4:0] out5
);

assign out0 = operand[0];
assign out1 = operand[1];
assign out2 = operand[2];
assign out3 = operand[3];
assign out4 = operand[4];
assign out5 = operand[5];

endmodule

// Adder
module Op2(
	input [9:0] operand,
	output [4:0] out0,
	output [4:0] out1,
	output [4:0] out2,
	output [4:0] out3,
	output [4:0] out4,
	output [4:0] out5
);

// Reverse bits and extract sign for convenience
wire sign_a = operand[0];
wire [4:0] in_a = {1'd0, operand[1], operand[2], operand[3], operand[4]};
wire sign_b = operand[5];
wire [4:0] in_b = {1'd0, operand[6], operand[7], operand[8], operand[9]};

// Signed inputs & sum
reg [5:0] signed_a;
reg [5:0] signed_b;
wire [5:0] sum = signed_a + signed_b;

// Perform addition
always @ (sign_a, sign_b, in_a, in_b) begin
	if (sign_a) signed_a <= ~in_a + 1'b1;
	else signed_a <= in_a;
	if (sign_b) signed_b <= ~in_b + 1'b1;
	else signed_b <= in_b;
end

// Translate to sign-magnitude
reg [4:0] out;
always @ (sum) begin
	if (sum[5]) out[4:0] <= ~(sum[4:0] - 1'b1);
	else out[4:0] <= sum[4:0];
end

// Always empty
assign out0 = 5'd17;
assign out1 = 5'd17;
// Sign
assign out2 = sum[5] ? 5'd16 : 5'd17;
// Always 0
assign out3 = 5'd0;
// Sum
assign out4 = out[4];
assign out5 = out[3:0];
endmodule

// Multiplier
module Op3(
	input [9:0] operand,
	output [4:0] out0,
	output [4:0] out1,
	output [4:0] out2,
	output [4:0] out3,
	output [4:0] out4,
	output [4:0] out5
);

// Product, 4x4 multiplier does not work for some reason
wire [7:0] in_a = {4'b0, operand[1], operand[2], operand[3], operand[4]};
wire [7:0] in_b = {4'b0, operand[6], operand[7], operand[8], operand[9]};
wire [7:0] product = in_a * in_b;

// Always off
assign out0 = 5'd17;
assign out1 = 5'd17;
// Sign
assign out2 = operand[0]^operand[5] ? 5'd16 : 5'd17;
// TODO: remove leding zeroes and such
assign out3 = 5'd0;
// Actual product
assign out4 = {1'd0, product[7:4]};
assign out5 = {1'd0, product[3:0]};
endmodule

// Bit counter
module Op4(
	input [5:0] operand,
	output [4:0] out0,
	output [4:0] out1,
	output [4:0] out2,
	output [4:0] out3,
	output [4:0] out4,
	output [4:0] out5
);

// Inverse to count 0s
wire [5:0] inverse = ~operand;
// Count
wire [3:0] cnt1 = operand[0]+operand[1]+operand[2]+operand[3]+operand[4]+operand[5];
wire [3:0] cnt0 = inverse[0]+inverse[1]+inverse[2]+inverse[3]+inverse[4]+inverse[5];

assign out0 = 5'd17;
assign out1 = 5'd0;
assign out2 = cnt0;
assign out3 = 5'd17;
assign out4 = 5'd0;
assign out5 = cnt1;

endmodule

module Op5(
	input [9:0] operand,
	output [4:0] out0,
	output [4:0] out1,
	output [4:0] out2,
	output [4:0] out3,
	output [4:0] out4,
	output [4:0] out5
);


// Extract arguments for convenience
wire direction = operand[0];
wire [5:0] val = operand[6:1];
wire [2:0] shift = {operand[7], operand[8], operand[9]};

reg [5:0] shifted;
always @ (*) begin
	if (direction) shifted <= val << shift;
	else shifted <= val >> shift;
end

assign out0 = shifted[0];
assign out1 = shifted[1];
assign out2 = shifted[2];
assign out3 = shifted[3];
assign out4 = shifted[4];
assign out5 = shifted[5];

endmodule

// Determine if there are 2 or more 1's in the given input.
module Median(
	input a,
	input b,
	input c,
	output out
);

assign out = (a&b) | (a&c) | (b&c);

endmodule

// Median filter
// Only take lower 6 bits
module Op6(
	input [5:0] operand,
	output [4:0] out0,
	output [4:0] out1,
	output [4:0] out2,
	output [4:0] out3,
	output [4:0] out4,
	output [4:0] out5
);

// Set unused bits
assign out0 = 17;
assign out1[4:1] = 0;
assign out2[4:1] = 0;
assign out3[4:1] = 0;
assign out4[4:1] = 0;
assign out5[4:1] = 0;

Median med1(operand[0], operand[1], operand[2], out1[0]);
Median med2(operand[1], operand[2], operand[3], out2[0]);
Median med3(operand[2], operand[3], operand[4], out3[0]);
Median med4(operand[3], operand[4], operand[5], out4[0]);
Median med5(operand[4], operand[5], operand[0], out5[0]);

endmodule

// Glue everything together
module ALU(
	input [9:0] operand,
	input [5:0] operator,
	output [6:0] d0,
	output [6:0] d1,
	output [6:0] d2,
	output [6:0] d3,
	output [6:0] d4,
	output [6:0] d5
);

// Decode opcode
wire [2:0] opcode;
OpcodeTranslate translate(operator, opcode);

// <disgusting>
// Binary values to be displayed
wire [4:0] dsp0, dsp1, dsp2, dsp3, dsp4, dsp5;
// Values produced by each opcode
wire [4:0] val0_0, val0_1, val0_2, val0_3, val0_4, val0_5;
wire [4:0] val1_0, val1_1, val1_2, val1_3, val1_4, val1_5;
wire [4:0] val2_0, val2_1, val2_2, val2_3, val2_4, val2_5;
wire [4:0] val3_0, val3_1, val3_2, val3_3, val3_4, val3_5;
wire [4:0] val4_0, val4_1, val4_2, val4_3, val4_4, val4_5;
wire [4:0] val5_0, val5_1, val5_2, val5_3, val5_4, val5_5;
wire [4:0] val6_0, val6_1, val6_2, val6_3, val6_4, val6_5;
// Select the right set of values
Mux mux0(val0_0, val1_0, val2_0, val3_0, val4_0, val5_0, val6_0, opcode, dsp0);
Mux mux1(val0_1, val1_1, val2_1, val3_1, val4_1, val5_1, val6_1, opcode, dsp1);
Mux mux2(val0_2, val1_2, val2_2, val3_2, val4_2, val5_2, val6_2, opcode, dsp2);
Mux mux3(val0_3, val1_3, val2_3, val3_3, val4_3, val5_3, val6_3, opcode, dsp3);
Mux mux4(val0_4, val1_4, val2_4, val3_4, val4_4, val5_4, val6_4, opcode, dsp4);
Mux mux5(val0_5, val1_5, val2_5, val3_5, val4_5, val5_5, val6_5, opcode, dsp5);
// </disgusting>

Op0 op0(operand, val0_0, val0_1, val0_2, val0_3, val0_4, val0_5);
Op1 op1(operand[9:4], val1_0, val1_1, val1_2, val1_3, val1_4, val1_5);
Op2 op2(operand, val2_0, val2_1, val2_2, val2_3, val2_4, val2_5);
Op3 op3(operand, val3_0, val3_1, val3_2, val3_3, val3_4, val3_5);
Op4 op4(operand[9:4], val4_0, val4_1, val4_2, val4_3, val4_4, val4_5);
Op5 op5(operand, val5_0, val5_1, val5_2, val5_3, val5_4, val5_5);
Op6 op6(operand[9:4], val6_0, val6_1, val6_2, val6_3, val6_4, val6_5);

// Map final values to output
Display display0(dsp0, d0);
Display display1(dsp1, d1);
Display display2(dsp2, d2);
Display display3(dsp3, d3);
Display display4(dsp4, d4);
Display display5(dsp5, d5);

endmodule
