<%@page session="false"%><%@ include file="/libs/foundation/global.jsp" %><%
%><%@ page contentType="text/html; charset=utf-8" %>
<div>
<%= properties.get("title", currentPage.getTitle()) %>
</div>
  <% com.jt.HelloService firstService = sling.getService(com.jt.HelloService.class);
%>
<div>
This page invokes the AEM KeyService
</div>
<div>
   
The value of the key is: <%=firstService.getRepositoryName()%>
</div>