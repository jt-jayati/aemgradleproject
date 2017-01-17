<%@page session="false"%><%@ include file="/libs/foundation/global.jsp" %><%
%><%@ page contentType="text/html; charset=utf-8" %>
<div>
<%= properties.get("title", currentPage.getTitle()) %>
</div>
  <% com.ttn.HelloService firstService = sling.getService(com.ttn.HelloService.class);
%>
<div>
This page invokes the AEM KeyService
</div>
<div>
   
The value of the key is: <%=firstService.getName()%>
</div>