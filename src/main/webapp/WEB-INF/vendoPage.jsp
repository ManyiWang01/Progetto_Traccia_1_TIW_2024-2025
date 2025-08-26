<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
    	<meta charset="UTF-8">
        <title>Asta On-Line</title>
    </head>
    <body>
        <h1>Vendo Page</h1>
        <div id="openAuction">
        	<c:if test="${not empty openAuctionList}">
	        	<c:forEach var="auction" item="${openAuctionList}">
	        		ID_Asta: <c:out value="${auction.id_asta}  "/>
	        		<c:set var="articleList" value="${articleMap[auction.id_asta]"/>
	        		<c:forEach var="article" item="articleList">
	        			ID_Articolo <c:out value="${article.id_articolo }"/>
	        			Nome_Articolo <c:out value="${article.nome_articolo }"/>
	        		</c:forEach>
	        	</c:forEach>
        	</c:if>
        	
        </div>
        <div id="closedAuction">
        	
        </div>
    </body>
</html>