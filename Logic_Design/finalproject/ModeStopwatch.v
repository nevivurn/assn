`timescale 1ns / 1ps

module ModeStopwatch(clk, set, clear, reset,
	dsp0, dsp1, dsp2, dsp3, dsp4, dsp5);
input wire clk, set, clear, reset;
output wire [6:0] dsp0, dsp1, dsp2, dsp3, dsp4, dsp5;

reg [6:0] centisecs = 7'd0, next_centisecs = 7'd0;
reg [5:0] seconds = 6'd0, next_seconds = 6'd0;
reg [5:0] minutes = 6'd0, next_minutes = 6'd0;
always @(*) begin
	if (centisecs == 99 && seconds == 59 && minutes == 59) begin
		// Don't increment past maximum
		next_centisecs = centisecs;
		next_seconds = seconds;
		next_minutes = minutes;
	end else begin
		next_centisecs = centisecs+1;
		next_seconds = seconds;
		next_minutes = minutes;

		if (next_centisecs == 100) begin
			next_centisecs = 0;
			next_seconds = seconds+1;
		end
		if (next_seconds == 60) begin
			next_seconds = 0;
			next_minutes = minutes+1;
		end
	end
end

reg enable = 0;
always @(posedge clk) begin
	if (reset) begin
		centisecs <= 0;
		seconds <= 0;
		minutes <= 0;
		enable <= 0;
	end else if (set) begin
		enable <= ~enable;
	end else if (!enable && clear) begin
		centisecs <= 0;
		seconds <= 0;
		minutes <= 0;
	end else if (enable) begin
		centisecs <= next_centisecs;
		seconds <= next_seconds;
		minutes <= next_minutes;
	end
end

DisplayDigits
	dispMinutes(minutes, dsp1, dsp0),
	dispSeconds(seconds, dsp3, dsp2),
	dispCentisecs(centisecs, dsp5, dsp4);

endmodule
