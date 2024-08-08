package com.cherkashin.vitaliy.modbus.transport.stable_rxtx.listener;

/** оповещатель о событиях */
public interface IWatchDogTimerNotify {
	/** оповещение о пришедшем на порт событии */
	public void notifyWatchDog();
}
