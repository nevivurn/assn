`timescale 1ns / 1ps

module DigitalClock(hwclk, mode, set, op1, op2, op3, reset,
	dsp0, dsp1, dsp2, dsp3, dsp4, dsp5);
input wire hwclk, mode, set, op1, op2, op3, reset;
output wire [6:0] dsp0, dsp1, dsp2, dsp3, dsp4, dsp5;

// Slow down clock to 100Hz
wire clk;
Clock100 clock100(hwclk, clk);

// Synchronize inputs
wire smode, sset, sop1, sop2, sreset;
PulseGen
	pulseMode(clk, mode, smode),
	pulseSet(clk, set, sset),
	pulseOp1(clk, op1, sop1),
	pulseOp2(clk, op2, sop2);
Debounce debounceReset(clk, reset, sreset);

// Mode FSM
reg [2:0] modeState = 3'b001;
wire modeClock = modeState[0], modeAlarm = modeState[1], modeStopwatch = modeState[2];
always @(posedge clk) begin
	if (sreset) modeState <= 3'b001;
	else if (smode) modeState <= {modeState[1:0], modeState[2]};
end

// Per-mode outputs
wire [6:0] dspClock [5:0], dspAlarm [5:0], dspStopwatch [5:0];
wire [6:0] dspOut [5:0];
DisplayMux
	dispMux0(modeState, dspClock[0], dspAlarm[0], dspStopwatch[0], dspOut[0]),
	dispMux1(modeState, dspClock[1], dspAlarm[1], dspStopwatch[1], dspOut[1]),
	dispMux2(modeState, dspClock[2], dspAlarm[2], dspStopwatch[2], dspOut[2]),
	dispMux3(modeState, dspClock[3], dspAlarm[3], dspStopwatch[3], dspOut[3]),
	dispMux4(modeState, dspClock[4], dspAlarm[4], dspStopwatch[4], dspOut[4]),
	dispMux5(modeState, dspClock[5], dspAlarm[5], dspStopwatch[5], dspOut[5]);

// Brightness control
wire fast_op3;
PulseGen pulseIn(hwclk, op3, fast_op3);
Brightness
	bright0(hwclk, fast_op3, sreset, dspOut[0], dsp0),
	bright1(hwclk, fast_op3, sreset, dspOut[1], dsp1),
	bright2(hwclk, fast_op3, sreset, dspOut[2], dsp2),
	bright3(hwclk, fast_op3, sreset, dspOut[3], dsp3),
	bright4(hwclk, fast_op3, sreset, dspOut[4], dsp4),
	bright5(hwclk, fast_op3, sreset, dspOut[5], dsp5);

wire alarm;
wire [5:0] alarm_minute, alarm_hour;

ModeClock clockMod(clk, sset&modeClock, sop1&modeClock, sop2&modeClock, sreset,
	dspClock[0], dspClock[1], dspClock[2], dspClock[3], dspClock[4], dspClock[5],
	alarm, alarm_minute, alarm_hour);
ModeAlarm alarmMod(clk, sset&modeAlarm, sop1&modeAlarm, sop2&modeAlarm, sreset,
	dspAlarm[0], dspAlarm[1], dspAlarm[2], dspAlarm[3], dspAlarm[4], dspAlarm[5],
	alarm, alarm_minute, alarm_hour);

ModeStopwatch stopwatchMod(clk, sset&modeStopwatch, sop1&modeStopwatch, sreset,
	dspStopwatch[0], dspStopwatch[1], dspStopwatch[2], dspStopwatch[3], dspStopwatch[4], dspStopwatch[5]);

endmodule
