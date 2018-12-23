`timescale 1ns / 1ps

module PatternRecognizerTest;

reg clk;
reg x;
reg TYPE;
wire [3:0] y;

PatternRecognizer uut (
	.clk(clk), 
	.x(x), 
	.TYPE(TYPE), 
	.y(y)
);

// First bit is type, rest is test string.
parameter
	test1 = 29'b00101101011010101011010110101,
	test2 = 29'b10100100100101010110101101001,
	test3 = 29'b00101110010010100110010110101,
	test4 = 29'b11010101001010101011001000101;

reg [28:0] test = test1; // Test to be run
reg [4:0] ind = 27;

always @ (*) begin
	#5 clk <= ~clk;
end

initial begin
	clk = 1;
	TYPE = test[28];
	
	#4;
	forever begin
		x <= test[ind];
		ind <= ind-1;
		#10;
	end
end
endmodule

