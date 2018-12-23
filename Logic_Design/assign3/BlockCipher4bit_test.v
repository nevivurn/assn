`timescale 1ns / 1ps

module BlockCipher4bit_test;

	// Inputs
	reg [3:0] p;
	reg [3:0] k;

	// Outputs
	wire [3:0] c;

	// Instantiate the Unit Under Test (UUT)
	BlockCipher4bit uut (
		.p(p), 
		.k(k), 
		.c(c)
	);

	initial begin
		// Initialize Inputs
		p = 0;
		k = 0;

		// Wait 100 ns for global reset to finish
		#100;
        
		// Add stimulus here
		p = 4'b1101;
		k = 4'b1011;

	end
      
endmodule

