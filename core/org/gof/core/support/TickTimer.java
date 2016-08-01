package org.gof.core.support;

import org.gof.core.Port;

public class TickTimer {
	private long interval = -1;				//间隔时间
	private long nextTime = -1;				//下次执行时间
	
	public TickTimer() { }
	
	public TickTimer(long interval) {
		this(interval, false);
	}
	
	public TickTimer(long curr, long interval) {
		this(curr, interval, false);
	}
	
	public TickTimer(long interval, boolean immediate) {
		start(interval, immediate);
	}
	
	public TickTimer(long curr, long interval, boolean immediate) {
		start(curr, interval, immediate);
	}
	
	/**
	 * 开始
	 * @param interval 执行间隔
	 */
	public void start(long interval) {
		start(interval, false);
	}
	
	public void start(long curr, long interval) {
		start(curr, interval, false);
	}
	
	/**
	 * 开始
	 * @param interval 执行间隔
	 * @param immediate 立即执行一次
	 */
	public void start(long interval, boolean immediate) {
		this.interval = interval;
		if(immediate) {
			if(Port.getCurrent() == null) {
				this.nextTime = System.currentTimeMillis();
			} else {
				this.nextTime = Port.getTime();
			}
		} else {
			if(Port.getCurrent() == null) {
				this.nextTime = System.currentTimeMillis() + interval;
			} else {
				this.nextTime = Port.getTime() + interval;
			}
		}
	}
	
	public void start(long curr, long interval, boolean immediate) {
		this.interval = interval;
		if(immediate) {
			this.nextTime = curr;
		} else {
			this.nextTime = curr + interval;
		}
	}
	
	/**
	 * 停止
	 */
	public void stop() {
		nextTime = -1;
		interval = -1;
	}
	
	/**
	 * 本次时间已到
	 * @param curr
	 * @return
	 */
	public boolean isOnce(long curr) {
		//未初始化或已停止
		if(interval == -1) {
			return false;
		}
		
		//未达到时间
		if(nextTime > curr) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * 周期间隔时间已到
	 * @param curr
	 * @return
	 */
	public boolean isPeriod(long curr) {
		//未初始化或已停止
		if(interval == -1) {
			return false;
		}
		
		//时间未到
		if(!this.isOnce(curr)) {
			return false;
		}
		
		//更新周期时间
		nextTime += interval;
		
		return true;
	}
	
	/**
	 * 是否是开始状态
	 * @return
	 */
	public boolean isStarted() {
		return interval != -1; 
	}

	public long getInterval() {
		return interval;
	}
	
	/**
	 * 距最近时间点还有多久，需要配合isOnce或者isPeriod使用
	 * @param curr
	 * @return
	 */
	public long getTimeLeft(long curr) {
		if(isOnce(curr)) return 0;
		return nextTime - curr;
	}
	
	/**
	 * 重新计时
	 */
	public void reStart() {
		this.nextTime = Port.getTime() + interval;
	}
	
	public void reStart(long curr) {
		this.nextTime = curr + interval;
	}
	
	/**
	 * 强制设置下一时刻而不改变间隔
	 */
	public void setTimeNext(long timeNext) {
		this.nextTime = timeNext;
	}
}
