`timescale 1ns / 1ps

module BlockDecryptorCBC_test;

	// Inputs
	reg [7:0] c;
	reg [3:0] k;
	reg [3:0] iv;

	// Outputs
	wire [7:0] p;

	// Instantiate the Unit Under Test (UUT)
	BlockDecryptorCBC uut (
		.c(c), 
		.k(k), 
		.iv(iv), 
		.p(p)
	);

	initial begin
		// Initialize Inputs
		c = 0;
		k = 4'd11;
		iv = 4'd9;

		// Wait 100 ns for global reset to finish
		#20 c = 8'd25;
		#20 c = 8'd145;
		#20 c = 8'd91;
		#20 c = 8'd108;
		#20 c = 8'd229;
	end
      
endmodule

