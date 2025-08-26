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
        	<c:choose>
        		<c:when test="${not empty openAuctionList}">
	        		<c:forEach var="auction" item="${openAuctionList}">
		        		IDAsta: <c:out value="${auction.id_asta}  "/>
		        		OffertaMassima: <c:out value="${offerMap[aucion.id_asta] }"/>
		        		TempoRimanente: <c:out value="${remainingTime[auction.id_asta] }"/>
		        		<c:set var="articleList" value="${articleMap[auction.id_asta]"/>
		        		<c:forEach var="article" item="articleList">
		        			IDArticolo <c:out value="${article.id_articolo }"/>
		        			NomeArticolo <c:out value="${article.nome_articolo }"/>
		        		</c:forEach>
		        	</c:forEach>
        		</c:when>
        		<c:otherwise>
        			<c:out value="Non ci sono aste aperte"/>
        		</c:otherwise>
        	</c:choose>
        </div>
        <div id="closedAuction">
        	<c:choose>
        		<c:when test="${not empty closedAuctionList}">
	        		<c:forEach var="auction" item="${closedAuctionList}">
		        		IDAsta: <c:out value="${auction.id_asta}  "/>
		        		OffertaMassima: <c:out value="${offerMap[aucion.id_asta] }"/>
		        		TempoRimanente: <c:out value="${remainingTime[auction.id_asta] }"/>
		        		<c:set var="articleList" value="${articleMap[auction.id_asta]"/>
		        		<c:forEach var="article" item="articleList">
		        			IDArticolo <c:out value="${article.id_articolo }"/>
		        			NomeArticolo <c:out value="${article.nome_articolo }"/>
		        		</c:forEach>
		        	</c:forEach>
        		</c:when>
        		<c:otherwise>
        			<c:out value="Non ci sono aste chiuse"/>
        		</c:otherwise>
        	</c:choose>
        </div>
    </body>
</html>