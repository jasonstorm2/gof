<%@ page language="java" import="java.util.*" pageEncoding="gb2312"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'input.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->

  </head>
  
  <body>请录入图书信息<p>
  <form action="input.do" method="post">
  <font size="4">图书类型</font>
  <select name="bookType">
  <option value="文学评论">文学评论</option>
  <option value="小说">小说</option>
  <option value="诗歌">诗歌</option>
  <option value="散文">散文</option>  
  </select><p>
  
  书名：<input type="text" size="40" name="bookName"><p>
  作者：<input type="text" size="30" name="bookAuthor">
  <input type="submit" value="提交">
  </form>    
  </body>
</html>