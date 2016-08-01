package org.gof.core.support;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class myConfig {
	private Properties propertie;
	private FileInputStream inputFile;
	private FileOutputStream outputFile;
	
	public myConfig(){
		propertie = new Properties();
	}
	
	public myConfig(String filePath){
//		String filePath = Utils.class.getClassLoader().getResource(name).getPath();
		propertie = new Properties();
		try {
			inputFile = new FileInputStream(filePath);
//			inputFile = new FileInputStream("./myconfig.properties");

//			InputStream inputFile = myConfig.class.getResourceAsStream("./myconfig.properties");//文件必须再class类文件夹中
			propertie.load(inputFile);//加载inputstream
			inputFile.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("读取属性文件--->失败！- 原因：文件路径错误或者文件不存在"); 
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("装载文件--->失败!");  
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 重载 key值
	 * @param key
	 * @return
	 */
	public String getValue(String key){
		String res = "";
		if(propertie.containsKey(key)){
			String value = propertie.getProperty(key);
			res = value;
		}
		return res;
	}
	
	/**
	 * 重载 路径+key值
	 * @param fileName
	 * @param key
	 * @return
	 */
	public String getValue(String fileName,String key){
		String res = "";
		try {
			inputFile = new FileInputStream(fileName);
			propertie.load(inputFile);
			inputFile.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("读取属性文件--->失败！- 原因：文件路径错误或者文件不存在"); 
			e.printStackTrace();
			return res;
		} catch (IOException e) {
			System.out.println("装载文件--->失败!");  
			// TODO Auto-generated catch block
			e.printStackTrace();
			return res;
		}
		
		if(propertie.containsKey(key)){
			String value = propertie.getProperty(key);
			res = value;
		}
		return res;
	}
	
	/**
	 * 清理对象值
	 */
	public void clear(){
		propertie.clear();
	}
	
	/**
	 * 
	 * @param key
	 * @param value
	 */
	public void setValueByKey(String key,String value){
		if(propertie.containsKey(key)){
			propertie.setProperty(key, value);
		}else{
			System.out.println("配置表没有改key");
		}
	}
	
	/**
	 * 
	 * @param key
	 * @param value
	 */
	public void setNewKeyValue(String key,String value){
		if(!propertie.containsKey(key)){
			propertie.setProperty(key, value);
		}else{
			System.out.println("配置表已经包含改key");
		}	
	}
	


}
