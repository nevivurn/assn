`timescale 1ns / 1ps

module BlockCipherCBC_test;

	// Inputs
	reg [7:0] p;
	reg [3:0] k;
	reg [3:0] iv;

	// Outputs
	wire [7:0] c;

	// Instantiate the Unit Under Test (UUT)
	BlockCipherCBC uut (
		.p(p), 
		.k(k), 
		.iv(iv), 
		.c(c)
	);

	initial begin
		// Initialize Inputs
		p = 0;
		k = 4'd11;
		iv = 4'd9;

		// Wait 100 ns for global reset to finish
		#20 p = 8'd12;
		#20 p = 8'd64;
		#20 p = 8'd137;
		#20 p = 8'd177;
		#20 p = 8'd255;
	end
      
endmodule

