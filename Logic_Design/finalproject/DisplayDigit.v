`timescale 1ns / 1ps

// Display a single digit
module DisplayDigit(val, dsp);
input wire [3:0] val;
output reg [6:0] dsp = 7'b0000000;

always @(*) begin
	case (val)
		4'd0: dsp = 7'b1111110;
		4'd1: dsp = 7'b0110000;
		4'd2: dsp = 7'b1101101;
		4'd3: dsp = 7'b1111001;
		4'd4: dsp = 7'b0110011;
		4'd5: dsp = 7'b1011011;
		4'd6: dsp = 7'b1011111;
		4'd7: dsp = 7'b1110000;
		4'd8: dsp = 7'b1111111;
		4'd9: dsp = 7'b1111011;
		default: dsp = 7'b0000000;
	endcase
end

endmodule
