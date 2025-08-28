<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
    	<meta charset="UTF-8">
        <title>Dettaglio Offerte</title>
    </head>
    <body>
        <div id="auctionOffer">
        	<c:remove var="auctionID" scope="session"/>
        	<h4 style="color : red;">Offerte Asta: </h4>
        	<c:if test="${not empty auction }" >
        		<table>
        			<tr> <td>ID Asta</td> <td>Prezzo Iniziale</td> <td>Rialzo Minimo</td> <td>Data Scadenza</td></tr>
        			<tr>
        				<td> <c:out value="${auction.id_asta}"/> </td>
        				<td> <c:out value="${auction.p_iniziale }(EUR)"/> </td>
        				<td> <c:out value="${auction.min_rialzo }(EUR)"/> </td>
        				<td> <c:out value="${auction.data_scadenza }"/> </td>
        			</tr>
        		</table>
        		<br>
        	</c:if>
        	<c:choose>
        		<c:when test="${not empty offerError }">
        			<c:out value="${offerError }"/>
        		</c:when>
        		<c:otherwise>
	        		<c:if test="${not empty articleList }">
	        			<table>
		        			<tr> <td>ID Articolo</td> <td>Nome Articolo</td></tr>
			        		<c:forEach var="article" items="${articleList }">
			        			<tr>
				        			<td> <c:out value="${article.id_articolo }"/> </td>
				        			<td> <c:out value="${article.nome_articolo }"/> </td>
			        			</tr>
			        		</c:forEach>
		        		</table>
		        		<br>
	        		</c:if>
	        		<c:if test="${not empty emptyArticle }">
			       		<c:out value="${emptyArticle }"/><br><br>
			        </c:if>
	        		<c:if test="${not empty offerList }">
			       		<table>
		      				<tr> <td>Utente</td> <td>Offerta</td> <td>Data</td></tr>
		      				<c:forEach var="offer" items="${offerList }">
		        			<tr>
			        			<td> <c:out value="${offer.user }"/> </td>
			        			<td> <c:out value="${offer.p_offerta }(EUR)"/> </td>
			        			<td> <c:out value="${offer.data_offerta }"/> </td>
		        			</tr>
		        			</c:forEach>
		       			</table>
		       			<br>
			        </c:if>
			        <c:if test="${not empty emptyOffer }">
			        	<c:out value="${emptyOffer }"/><br><br>
			        </c:if>
        		</c:otherwise>
        	</c:choose>
	        <form action="MakeAnOffer" method="post">
	        	<c:set var="offerta" value="${auction.p_iniziale }"/>
	        	Prezzo Offerto:
	        	<c:if test="${not empty maxOffer }">
	        		<c:set var="offerta" value="${maxOffer.p_offerta }"/>
	        	</c:if>
	        	<input type="number" name="prezzo" step="1.00" min="${offerta + auction.min_rialzo }" value="${offerta + auction.min_rialzo }" required>
	        	<button type="submit">Offri</button>
	        </form>
        </div>
    </body>
</html>