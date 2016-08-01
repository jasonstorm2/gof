package org.gof.core.gen;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.core.helpers.Loader;
import org.gof.core.support.PackageClass;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * 自动生成基类
 */
public abstract class GenBase {
	// 配置相关
	protected String pageEncoding = "UTF-8";
	protected Configuration configuration;			//FreeMarker配置
	
	// 文件夹相关
	protected String packageName;						//配置源文件夹
	protected String targetDir;						//输出目标文件夹

	public static String pluginDesFileName;				//插件指定生成的文件名

	private static final String CORE_PATH = "gof";		//CORE项目的相对路径 模板在这个项目中
	public static String userDir = System.getProperty("user.dir");		// 用户当前执行的所在路径
	
	// 文件生成相关
	protected List<Map<String, Object>> rootMaps = new ArrayList<>();		// 要放到模板的信息
	public boolean canGen;				// 是否能正常生成
	
	/**
	 *  创建和调整配置,这个在生命周期中只做一次
	 */
	public GenBase(String packageName, String targetDir) {
		this.packageName = packageName; 
		this.targetDir = targetDir;
		configuration = new Configuration();
		
		try {
			String tempDir = userDir + "/" + CORE_PATH + "/core/" + PackageClass.packageToPath("org.gof.core.gen") + "/templates";
			configuration.setDirectoryForTemplateLoading(new File(tempDir));
			configuration.setEncoding(Locale.getDefault(), pageEncoding);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 生成文件统一方法
	 * @param fileName
	 * @param fileSuffix
	 * @param rootMap
	 * @throws Exception
	 */
	protected void writeFile(String targetFileDir, String fileName, String tempName, Object rootMap) throws Exception {
		/* 1，获取模板 */
		Template temp = configuration.getTemplate(tempName, pageEncoding);
		temp.setEncoding(pageEncoding);
		
		// 判断目标文件夹不存在，则新建文件夹
		File dir = new File(targetFileDir);
		if(!dir.exists()) dir.mkdirs();
		
		/* 2，将模板和数据合并，并生成文件 */
		String fileFullName = targetFileDir + fileName;
		System.out.println("-------开始生成" + fileFullName + "文件......------");
		
		File target = new File(fileFullName);
		Writer out = new OutputStreamWriter(new FileOutputStream(target), pageEncoding);
		temp.process(rootMap, out);
		out.flush();
		out.close();

		System.out.println("-------" + fileName + "文件生成完毕!-------\n");
	}
	
	/**
	 * 自动生成文件的具体方法
	 * @throws Exception
	 */
	public void genFiles() throws Exception {
		// 判断不能生成
		if(!this.canGen) {
			System.out.println("代码生成失败, 请检查错误后重试!");
		} else {
			genGlobalFile(this.rootMaps);
			
			// 遍历生成文件
			for(Map<String, Object> rootMap :this.rootMaps) {
				genFileHandler(rootMap);
			}
		}
	}

	/**
	 * 生成类的核心方法, 各自子类实现
	 * @param rootMap
	 * @throws Exception
	 */
	protected abstract void genFileHandler(Map<String, Object> rootMap) throws Exception ;
	
	/**
	 * 对该类注解生成全局唯一的文件，子类可覆盖
	 * @param rootMaps
	 * @throws Exception
	 */
	private void genGlobalFile(List<Map<String,Object>> mapList) throws Exception{
		//判断是否为空
		int count = 0;
		for(Map<String,Object> map : mapList){
			count += map.size();
		}
		if(count == 0)
			return;
		
		if(this.rootMaps.size() == 0)
			return;
		Map<String,Object> data = new HashMap<>();
		data.put("rootPackageName", this.packageName);
		data.put("methodsList", this.rootMaps);
		genGlobalFileHandler(data);
	}
	protected void genGlobalFileHandler(Map<String, Object> rootMaps) throws Exception {
		
	}
	public static Set<Class<?>> getPluginGenDesFile(String packageName) {
		String pathStr = System.getProperty("java.class.path");
		String[] PATHS = pathStr.split(";");
		
		//处理插件发来的路径 (src/org/gof/demo/worldsrv/test/DebugManager.java)
		//替换.java为.class
		GenBase.pluginDesFileName = GenBase.pluginDesFileName.replace(".java", ".class");
		//去掉SRC目录
		GenBase.pluginDesFileName = GenBase.pluginDesFileName.replace("src", "");
		
		//去掉第一个seam
		if(GenBase.pluginDesFileName.indexOf("seam") != -1) {
			String temp = GenBase.pluginDesFileName.replaceFirst("seam", "");
			//如果有两个seam 则替换第一个出现的
			if(temp.indexOf("seam") != -1)
				GenBase.pluginDesFileName = GenBase.pluginDesFileName.replaceFirst("seam", "");
		}

		//获得包名 + 文件名
		packageName = GenBase.pluginDesFileName.substring(1, GenBase.pluginDesFileName.indexOf(".")).replace("/", ".");
		
		//返回值
		Set<Class<?>> result = new HashSet<Class<?>>();
		
		for(String path : PATHS) {
			//只处理Bin目录下的文件  处理过的目录跳过
			if(!path.contains("/bin")) continue;

			String filePath = GenBase.pluginDesFileName;
			
			try{
				//class文件
				File file = new File(path , filePath);
				
				if(file.exists()) {
					//得到类class信息
					Class<?> clazz = Class.forName(packageName);
					result.add(clazz);
				}

			}catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		return result;
	}
	
	public static Set<Class<?>> getClassSET(String packageName) throws ClassNotFoundException {
		Set<Class<?>> result = new HashSet<Class<?>>();
//		  File   file   =   new   File(jar文件全路径);   
//		  URL   url   =   file.toURI().toURL(); 
//		  URLClassLoader   loader   =   new   URLClassLoader(new   URL[]   {   url   });   
//		  Class   tidyClazz   =   loader.loadClass(所需class的含包名的全名);  

		try {
			// URL也可以采用此格式：  URL("file://D:/WorkSpaceMyEclipse/Meeting_Hall/WebRoot/WEB-INF/classes/")
			URLClassLoader loader = new URLClassLoader(new URL[]{new URL("file:D:/WorkSpaceMyEclipse/Meeting/WebRoot/WEB-INF/classes/")});

			String path = packageName;
			File file = new File(path);
			File[] tempList = file.listFiles();
			System.out.println("该目录下对象个数：" + tempList.length);
			for (int i = 0; i < tempList.length; i++) {
				if (tempList[i].isFile()) { //是文件
					// 读取某个文件夹下的所有文件
					String fileName = tempList[i].getName();
					System.out.println("文件名称：" + fileName);

					String className = "entity" + "." + fileName;
					String classSimpleName = className.substring(0, className.lastIndexOf('.'));
					//得到类class信息
//					Class<?> clazz = Class.forName(classSimpleName);
					Class<?> clazz = loader.loadClass(classSimpleName);//相对路径，相对于load加载路径的路径！
					
					//记录返回值
					result.add(clazz);			
					
				}
				if (tempList[i].isDirectory()) { //是目录
					// 读取某个文件夹下的所有文件夹
					System.out.println("文件夹：" + tempList[i]);
				}
			}
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} //注意这个URL
	

		return result;
	}
	
	/**
	 * 获得要生成的类
	 * @param packageName
	 * @return
	 */
	public static Set<Class<?>> getSources(String packageName) {
		Set<Class<?>> sources = null;
		
		if(StringUtils.isNotEmpty(GenBase.pluginDesFileName)) {
			sources = GenBase.getPluginGenDesFile(packageName);
		} else {
			// 获取待删除文件夹下的所有类
			sources = PackageClass.find(packageName);
		}
		
		return sources;
	}

}
