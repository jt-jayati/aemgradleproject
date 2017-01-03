<%@page session="false"%><%@ include file="/libs/foundation/global.jsp" %><%
%><%@ page contentType="text/html; charset=utf-8" import="
    com.day.cq.i18n.I18n,
    java.util.Calendar"
%><%

    final I18n i18n = new I18n(slingRequest);
    final String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));

%>
<footer>
    <nav><cq:include path="toolbar" resourceType="foundation/components/toolbar"/></nav>
    <p class="copyright"><%= xssAPI.filterHTML(i18n.get("&copy; {0} My Project. All rights reserved.", null, year)) %></p>
</footer>
