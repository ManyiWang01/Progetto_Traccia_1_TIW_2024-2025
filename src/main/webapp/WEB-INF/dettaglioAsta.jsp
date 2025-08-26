<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
    	<meta charset="UTF-8">
        <title>Dettaglio Asta</title>
    </head>
    <body>
        <div id="auctionDetails">
        	<h4 style="color : red;">Dettaglio Asta: </h4>
	        <c:if test="${not empty asta}">
        		<table>
        			<tr> <td>ID Asta</td> <td>Prezzo Iniziale</td> <td>Rialzo Minimo</td> <td>Data Scadenza</td></tr>
	        			<tr>
	        				<td> <c:out value="${asta.id_asta}"/> </td>
	        				<td> <c:out value="${asta.p_iniziale }(EUR)"/> </td>
	        				<td> <c:out value="${asta.min_rialzo }(EUR)"/> </td>
	        				<td> <c:out value="${asta.data_scadenza }"/> </td>
	        			</tr>
        		</table>
        		<table>
        			<tr> <td>ID Articolo</td> <td>Nome Articolo</td></tr>
	        		<c:forEach var="article" items="${articoli}">
	        			<tr>
		        			<td> <c:out value="${article.id_articolo }"/> </td>
		        			<td> <c:out value="${article.nome_articolo }"/> </td>
	        			</tr>
	        		</c:forEach>
        		</table>
        		<c:choose>
        			<c:when test="${asta.status == false }">
        				<table>
		        			<c:choose>
		        				<c:when test="${not empty offerte }">
		        					<tr> <td>Utente</td> <td>Offerta</td> <td>Data</td></tr>
			        				<c:forEach var="offer" items="${offerte }">
					        			<tr>
						        			<td> <c:out value="${offer.user }"/> </td>
						        			<td> <c:out value="${offer.p_offerta }(EUR)"/> </td>
						        			<td> <c:out value="${offer.data_offerta }"/> </td>
					        			</tr>
					        		</c:forEach>
		        				</c:when>
		        				<c:otherwise> <tr> <td><c:out value="Non ci sono offerte"/></td> </tr> </c:otherwise>
		        			</c:choose>
		        		</table>
		        		<form action="EndAuction?id=${asta.id_asta }" method="get">
		        			<button type="submit" >CHIUDI</button>
		        		</form>
        			</c:when>
       				<c:otherwise>
        				<table>
		        			<c:choose>
		        				<c:when test="${not empty offertaMassima }">
		        					<tr> <td>Nome Vincitore</td> <td>Offerta Finale</td> <td>Indirizzo Spedizione</td></tr>
			        				<tr>
					        			<td> <c:out value="${winner.cognome } ${winner.nome }"/> </td>
					        			<td> <c:out value="${offertaMassima.p_offerta }(EUR)"/> </td>
					        			<td> <c:out value="${winner.indirizzo }"/> </td>
				        			</tr>
		        				</c:when>
		        				<c:otherwise> <tr> <td><c:out value="Nessuno ha vinto"/></td> </tr> </c:otherwise>
		        			</c:choose>
		        		</table>
        			</c:otherwise>
        		</c:choose>
       		</c:if>
        </div>
    </body>
</html>