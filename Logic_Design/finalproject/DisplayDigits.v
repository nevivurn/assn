`timescale 1ns / 1ps

// Display 2 digits
module DisplayDigits(val, dspLo, dspHi);
input wire [6:0] val;
output wire [6:0] dspLo, dspHi;

reg [3:0] lo = 4'd0, hi = 4'd0;
always @(*) begin
/* verilator lint_off WIDTH */
	if (val < 7'd10) begin
		lo = val[3:0];
		hi = 4'd0;
	end else if (val < 7'd20) begin
		lo = val-7'd10;
		hi = 4'd1;
	end else if (val < 7'd30) begin
		lo = val-7'd20;
		hi = 4'd2;
	end else if (val < 7'd40) begin
		lo = val-7'd30;
		hi = 4'd3;
	end else if (val < 7'd50) begin
		lo = val-7'd40;
		hi = 4'd4;
	end else if (val < 7'd60) begin
		lo = val-7'd50;
		hi = 4'd5;
	end else if (val < 7'd70) begin
		lo = val-7'd60;
		hi = 4'd6;
	end else if (val < 7'd80) begin
		lo = val-7'd70;
		hi = 4'd7;
	end else if (val < 7'd90) begin
		lo = val-7'd80;
		hi = 4'd8;
	end else begin
		lo = val-7'd90;
		hi = 4'd9;
	end
/* verilator lint_on WIDTH */
end

DisplayDigit
	dispLo(lo, dspLo),
	dispHi(hi, dspHi);

endmodule
