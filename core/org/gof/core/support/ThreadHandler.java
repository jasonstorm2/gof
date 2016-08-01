package org.gof.core.support;

import org.gof.core.interfaces.IThreadCase;
import org.gof.core.support.log.LogCore;

public class ThreadHandler extends Thread {
	private final IThreadCase member;		//被调用的对象
	private volatile boolean running;			//是否在运行中
	private volatile boolean wating = false;			//是否在等待中
	
	/**
	 * 构造函数
	 * @param master
	 */
	public ThreadHandler(IThreadCase master) {
		this.member = master;
	}
	
	/**
	 * 开始
	 * @return
	 */
	public void startup() {
		//已在运行中 忽略新的运行请求
		if(running) return;
		
		//设置为运行状态
		running = true;
		
		//启动线程
		start();
	}
	
	/**
	 * 结束
	 * @return
	 */
	public void cleanup() {
		//非运行中 忽略结束请求
		if(!running) return;
		
		//设置为停止状态
		running = false;
	}
	
	@Override
	public void run() {
		//开始前的准备
		member.caseStart();
		//运行中就不断轮询
		while(running) {
			try {
				// 如果等待中，就不处理
				if(wating) {
					Thread.sleep(1);
					continue;
				}
				
				//处理正常逻辑
				member.caseRunOnce();
				Thread.sleep(1);
			} catch (Exception e) {
				//不做任何处理 仅仅抛出异常
				//避免因为一个任务的出错 造成后续的任务无法继续执行
				LogCore.core.error("", e);
			}
		}
		//结束时的清理
		member.caseStop();
	}
	
	/**
	 * 暂停当前线程
	 */
	public void pauseT() {
		//忽略同状态
		if(wating) return;
			
		wating = true;
	}
	
	/**
	 * 恢复当前线程
	 */
	public void resumeT() {
		//忽略同状态
		if(!wating) return;
			
		wating = false;
	}
}
