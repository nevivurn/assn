`timescale 1ns / 1ps

module BlockCipher4bit(
    input [3:0] p,
    input [3:0] k,
    output reg [3:0] c
    );

always @ (p, k) begin
	c = p^k;
	c[0] <= c[1];
	c[1] <= c[0];
	c[2] <= c[3];
	c[3] <= c[2];
end

endmodule

module BlockCipherCBC(
    input [7:0] p,
	 input [3:0] k,
	 input [3:0] iv,
	 output [7:0] c
    );

wire [3:0] upper;
wire [3:0] lower;
assign upper = iv^p[7:4];
assign lower = c[7:4]^p[3:0];

BlockCipher4bit upperCipher(upper, k, c[7:4]);
BlockCipher4bit lowerCipher(lower, k, c[3:0]);

endmodule

module BlockDecryptorCBC(
    input [7:0] c,
	 input [3:0] k,
	 input [3:0] iv,
	 output [7:0] p
    );

// Key with bits swapped
wire [3:0] sk;
assign sk = {k[2], k[3], k[0], k[1]};

wire [3:0] upper;
wire [3:0] lower;
BlockCipher4bit upperCipher(c[7:4], sk, upper);
BlockCipher4bit lowerCipher(c[3:0], sk, lower);

assign p[7:4] = iv^upper;
assign p[3:0] = c[7:4]^lower;

endmodule
