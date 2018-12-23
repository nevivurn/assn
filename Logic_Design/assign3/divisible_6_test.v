`timescale 1ns / 1ps

module divisible_6_test;

	// Inputs
	reg [3:0] x;

	// Outputs
	wire div;

	// Instantiate the Unit Under Test (UUT)
	divisible_6 uut (
		.x(x), 
		.div(div)
	);

	initial begin
		// Initialize Inputs
		x = 1;

		// Wait 100 ns for global reset to finish
		#20;
        
		// Add stimulus here
		repeat (15) begin
			x = x+1;
			#20;
		end

	end
      
endmodule

