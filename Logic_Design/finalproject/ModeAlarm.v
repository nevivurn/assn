`timescale 1ns / 1ps

module ModeAlarm(clk, set, op1, op2, reset,
	dsp0, dsp1, dsp2, dsp3, dsp4, dsp5,
	alarm, minutes, hours);
input wire clk, set, op1, op2, reset;
output wire [6:0] dsp0, dsp1, dsp2, dsp3, dsp4, dsp5;

output reg alarm = 0;
output reg [5:0] minutes = 6'd0, hours = 6'd0;

parameter
	SET_OFF = 3'd0, SET_ON = 3'd5,
	SET_APM = 3'd1, SET_HALF = 3'd2,
	SET_HR = 3'd3, SET_MIN = 3'd4;

reg [2:0] setMode = SET_OFF;
reg dispMode = 0;

// Input aliases
wire display = op1 && (setMode == SET_OFF || setMode == SET_ON);
wire clear = op2 && (setMode == SET_OFF || setMode == SET_ON);
wire up = op1 && (setMode != SET_OFF || setMode == SET_ON);
wire down = op2 && (setMode != SET_OFF || setMode == SET_ON);

always @(posedge clk) begin
	if (reset) begin
		alarm <= 0;
		minutes <= 0;
		hours <= 0;
		setMode <= SET_OFF;
		dispMode <= 0;
	end else if (set) begin
		alarm <= 0;
		case (setMode)
			SET_OFF: setMode <= dispMode ? SET_APM : SET_HR;
			SET_ON: setMode <= dispMode ? SET_APM : SET_HR;
			SET_APM: setMode <= SET_HALF;
			SET_HALF: setMode <= SET_MIN;
			SET_HR: setMode <= SET_MIN;
			SET_MIN: begin
				alarm <= 1;
				setMode <= SET_ON;
			end
			default:;
		endcase
	end else if (display) begin
		dispMode <= ~dispMode;
	end else if (clear) begin
		alarm <= 0;
		minutes <= 0;
		hours <= 0;
		setMode <= SET_OFF;
	end else if (up) begin
		case (setMode)
			SET_APM: hours <= (hours < 12) ? hours+12 : hours-12;
			SET_HALF: hours <= (hours == 11 || hours == 23) ? hours-11 : hours+1;
			SET_HR: hours <= (hours == 23) ? 0 : hours+1;
			SET_MIN: minutes <= (minutes == 59) ? 0 : minutes+1;
			default:;
		endcase
	end else if (down) begin
		case (setMode)
			SET_APM: hours <= (hours < 12) ? hours+12 : hours-12;
			SET_HALF: hours <= (hours == 0 || hours == 12) ? hours+11 : hours-1;
			SET_HR: hours <= (hours == 0) ? 23 : hours-1;
			SET_MIN: minutes <= (minutes == 0) ? 59 : minutes-1;
			default:;
		endcase
	end
end

wire [6:0] apmDsp = (hours < 12) ? 7'b1110111 : 7'b1100111;
wire [6:0] hourDsp [1:0];
wire [6:0] halfDsp [1:0];
wire [6:0] minuteDsp [1:0];
DisplayDigits
	dispHours(hours, hourDsp[1], hourDsp[0]),
	dispHalf((hours < 12) ? hours : hours-12, halfDsp[1], halfDsp[0]),
	dispMinutes(minutes, minuteDsp[1], minuteDsp[0]);

// Blinking
wire [2:0] blinkCond;
assign blinkCond[0] = dispMode ? setMode == SET_APM : setMode == SET_HR;
assign blinkCond[1] = dispMode ? setMode == SET_HALF : setMode == SET_MIN;
assign blinkCond[2] = dispMode && setMode == SET_MIN;
Blink
	blink0(clk, blinkCond[0], setMode == SET_OFF ? 7'b000001 : (dispMode ? apmDsp : hourDsp[0]), dsp0),
	blink1(clk, blinkCond[0], setMode == SET_OFF ? 7'b000001 : (dispMode ? 7'b0000000 : hourDsp[1]), dsp1),
	blink2(clk, blinkCond[1], setMode == SET_OFF ? 7'b000001 : (dispMode ? halfDsp[0] : minuteDsp[0]), dsp2),
	blink3(clk, blinkCond[1], setMode == SET_OFF ? 7'b000001 : (dispMode ? halfDsp[1] : minuteDsp[1]), dsp3),
	blink4(clk, blinkCond[2], setMode == SET_OFF ? 7'b000001 : (dispMode ? minuteDsp[0] : 7'b1111110), dsp4),
	blink5(clk, blinkCond[2], setMode == SET_OFF ? 7'b000001 : (dispMode ? minuteDsp[1] : 7'b1111110), dsp5);

endmodule
