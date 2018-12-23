`timescale 1ns / 1ps

module ModeClock(clk, set, op1, op2, reset,
	dsp0, dsp1, dsp2, dsp3, dsp4, dsp5,
	alarm, alarm_minute, alarm_hour);
input wire clk, set, op1, op2, reset;
output wire [6:0] dsp0, dsp1, dsp2, dsp3, dsp4, dsp5;

input wire alarm;
input wire [6:0] alarm_minute, alarm_hour;

// 1Hz clock
wire secClk;
Clock clockSec(clk, secClk);

reg [5:0] seconds = 6'd0, minutes = 6'd0, hours = 6'd0;
reg [5:0] next_seconds = 6'd0, next_minutes = 6'd0, next_hours = 6'd0;
always @(*) begin
	next_seconds = seconds+1;
	next_minutes = minutes;
	next_hours = hours;

	if (next_seconds == 60) begin
		next_seconds = 0;
		next_minutes = minutes+1;
	end
	if (next_minutes == 60) begin
		next_minutes = 0;
		next_hours = hours+1;
	end
	if (next_hours == 24) next_hours = 0;
end

// Check for second=1 for obvious reasons
wire alarmMatch = alarm && (next_seconds == 1) && (next_minutes == alarm_minute) && (next_hours == alarm_hour);

parameter
	SET_OFF = 3'd0,
	SET_APM = 3'd1, SET_HALF = 3'd2,
	SET_HR = 3'd3, SET_MIN = 3'd4;

reg [2:0] setMode = SET_OFF;
reg dispMode = 0, alarmed = 0;

// Input aliases
wire display = op1 && setMode == SET_OFF;
wire aoff = op2 && setMode == SET_OFF;
wire up = op1 && setMode != SET_OFF;
wire down = op2 && setMode != SET_OFF;

always @(posedge clk) begin
	if (!alarm) alarmed <= 0;

	if (reset) begin
		seconds <= 0;
		minutes <= 0;
		hours <= 0;
		// next_* not reset here, handled by combinatorial logic
		setMode <= SET_OFF;
		dispMode <= 0;
		alarmed <= 0;
	end else if (set) begin
		alarmed <= 0;
		seconds <= 0;
		case (setMode)
			SET_OFF: setMode <= dispMode ? SET_APM : SET_HR;
			SET_APM: setMode <= SET_HALF;
			SET_HALF: setMode <= SET_MIN;
			SET_HR: setMode <= SET_MIN;
			SET_MIN: setMode <= SET_OFF;
			default:;
		endcase
	end else if (display) begin
		dispMode <= ~dispMode;
	end else if (aoff) begin
		alarmed <= 0;
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
	end else if (setMode == SET_OFF) begin
		if (secClk) begin
			seconds <= next_seconds;
			minutes <= next_minutes;
			hours <= next_hours;
		end
		if (alarmMatch) alarmed <= 1;
	end
end


wire [6:0] apmDsp = (hours < 12) ? 7'b1110111 : 7'b1100111;
wire [6:0] hourDsp [1:0];
wire [6:0] halfDsp [1:0];
wire [6:0] minuteDsp [1:0];
wire [6:0] secondDsp [1:0];
DisplayDigits
	dispHours(hours, hourDsp[1], hourDsp[0]),
	dispHalf((hours < 12) ? hours : hours-12, halfDsp[1], halfDsp[0]),
	dispMinutes(minutes, minuteDsp[1], minuteDsp[0]),
	dispSeconds(seconds, secondDsp[1], secondDsp[0]);

// Blinking
wire alarm_trigger = alarm && alarmed && setMode == SET_OFF;
wire [2:0] blinkCond;
assign blinkCond[0] = dispMode ? setMode == SET_APM : setMode == SET_HR;
assign blinkCond[1] = dispMode ? setMode == SET_HALF : setMode == SET_MIN;
assign blinkCond[2] = dispMode && setMode == SET_MIN;
Blink
	blink0(clk, alarm_trigger || blinkCond[0], dispMode ? apmDsp : hourDsp[0], dsp0),
	blink1(clk, alarm_trigger || blinkCond[0], dispMode ? 7'b0000000 : hourDsp[1], dsp1),
	blink2(clk, alarm_trigger || blinkCond[1], dispMode ? halfDsp[0] : minuteDsp[0], dsp2),
	blink3(clk, alarm_trigger || blinkCond[1], dispMode ? halfDsp[1] : minuteDsp[1], dsp3),
	blink4(clk, alarm_trigger || blinkCond[2], dispMode ? minuteDsp[0] : secondDsp[0], dsp4),
	blink5(clk, alarm_trigger || blinkCond[2], dispMode ? minuteDsp[1] : secondDsp[1], dsp5);

endmodule
