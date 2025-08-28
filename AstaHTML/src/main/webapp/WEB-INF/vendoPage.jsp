<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
    	<meta charset="UTF-8">
        <title>Pagina Vendita</title>
    </head>
    <body>
        <h1>Vendo Page</h1>
        <div id="addArticleForm">
        	<h4 style="color : red;">Aggiungi Articolo: </h4>
        	<form action="AddArticle" method="post">
        		Nome Articolo: <input type="text" name="nome_articolo" value="${sessionScope.nome }" required/><br>
        		Immagine(URL): <input type="text" name="url_immagine" value="${sessionScope.immagine }" required/><br>
        		Prezzo(EUR): <input type="number" step="0.05" name="prezzo" min="0.05" value="${sessionScope.prezzo }" required/><br>
        		Descrizione:<br> <textarea name="descrizione" rows="4" cols="30" required>${sessionScope.descrizione }</textarea><br>
        		<input type="submit" value="Aggiungi"/>
        		<c:if test="${not empty sessionScope.addArticleError }">
        			<p style="color : red;">${sessionScope.addArticleError }</p>
        			<c:remove var="addArticleError" scope="session"/>
        		</c:if>
       			<c:remove var="nome" scope="session"/>
        		<c:remove var="immagine" scope="session"/>
        		<c:remove var="prezzo" scope="session"/>
        		<c:remove var="descrizione" scope="session"/>
        	</form>
        </div>
        <div id="createAuctionForm">
        	<h4 style="color : red;">Crea Asta: </h4>
        	<form action="CreateAuction" method="post">
        		Seleziona Articoli:
        		<br>
        		<c:choose>
	        		<c:when test="${not empty freeArticle }">
	        			<c:forEach var="article" items="${freeArticle }">
        					<table>
        						<tr>
        							<td><input type="checkbox" name="selectedElements[]" value="${article.id_articolo }"/></td>
        							<td><c:out value="${article.nome_articolo }"/></td>
        							<td><c:out value="${article.prezzo }(EUR)"/></td>
        						</tr>
        					</table>
	       				</c:forEach>
	        		</c:when>
	        		<c:otherwise>
	        			<c:out value="Non ci sono articoli liberi"/>
	        		</c:otherwise>
        		</c:choose>
        		<br>
        		Rialzo Minimo(EUR): <input type="number" step="1" name="rialzo_minimo" value="${sessionScope.rialzoMinimo }" min="1" required/><br>
        		Scadenza:
        		<c:choose> 
	        		<c:when test="${not empty sessionScope.date }">
	        			<input type="datetime-local" name="date" value="${sessionScope.date }" min="${timeNow }" required>
	        		</c:when>       				
        			<c:otherwise>
        				<input type="datetime-local" name="date" value="${timeNow }" min="${timeNow }" required>
        			</c:otherwise>
        		</c:choose>
        		<br>
        		<input type="submit" value="Crea"/>
        		<c:if test="${not empty sessionScope.astaError }">
        			<p style="color : red;">${sessionScope.astaError }</p>
        			<c:remove var="astaError" scope="session"/>
        		</c:if>
        		<c:remove var="rialzoMinimo" scope="session"/>
        		<c:remove var="date" scope="session"/>
        	</form>
        </div>
        <div id="openAuction">
        	<h4 style="color : red;">Asta Aperte: </h4>
	        <c:choose>
        		<c:when test="${not empty openAuctionList}">
	        		<table>
	        			<tr> <td>ID Asta</td> <td>Offerta Massima</td> <td>Tempo Rimanente</td></tr>
	        			<c:forEach var="auction" items="${openAuctionList}">
			        		<c:set var="id_asta" value="${auction.id_asta}"/>
			        			<tr>
			        				<td> <a href="<c:url value="/DettaglioAsta?id=${id_asta }"/>" >${id_asta}</a> </td>
			        				<c:set var="offer" value="Non ci sono offerte"/>
					        		<c:if test="${not empty offerMap[id_asta] && offerMap[id_asta] != 0}">
				        				<c:set var="offer" value="${offerMap[id_asta] }"/>
					        		</c:if>
						        	<td> <c:out value="${offer }"/> </td>
						        	<td> <c:out value="${remainingTime[id_asta] }"/> </td>
						        	<c:set var="articleList" value="${articleMap[id_asta] }"/>
						        	<td>
					        		<table>
					        			<tr> <td>ID Articolo</td> <td>Nome Articolo</td></tr>
						        		<c:forEach var="article" items="${articleList }">
						        			<tr>
							        			<td> <c:out value="${article.id_articolo }"/> </td>
							        			<td> <c:out value="${article.nome_articolo }"/> </td>
						        			</tr>
						        		</c:forEach>
					        		</table>
					        		</td>
			        			</tr>
			        	</c:forEach>
	        		</table>
        		</c:when>
        		<c:otherwise> <c:out value="Non ci sono aste aperte"/> </c:otherwise>
        	</c:choose>
        </div>
        
        <div id="closedAuction">
        	<h4 style="color : red;">Asta Chiuse: </h4>
	        <c:choose>
        		<c:when test="${not empty closedAuctionList}">
	        		<table>
	        			<tr> <td>ID Asta</td> <td>Offerta Massima</td> <td>Tempo Rimanente</td></tr>
	        			<c:forEach var="auction" items="${closedAuctionList}">
			        		<c:set var="id_asta" value="${auction.id_asta}"/>
			        			<tr>
			        				<td> <a href="<c:url value="/DettaglioAsta?id=${id_asta }"/>" >${id_asta}</a> </td>
			        				<c:set var="offer" value="Non ci sono offerte"/>
					        		<c:if test="${not empty offerMap[id_asta] }">
					        			<c:set var="offer" value="${offerMap[id_asta] }"/>
					        		</c:if>
						        	<td> <c:out value="${offer }"/> </td>
						        	<td> <c:out value="${remainingTime[id_asta] }"/> </td>
						        	<c:set var="articleList" value="${articleMap[id_asta] }"/>
						        	<td>
					        		<table>
					        			<tr> <td>ID Articolo</td> <td>Nome Articolo</td></tr>
						        		<c:forEach var="article" items="${articleList }">
						        			<tr>
							        			<td> <c:out value="${article.id_articolo }"/> </td>
							        			<td> <c:out value="${article.nome_articolo }"/> </td>
						        			</tr>
						        		</c:forEach>
					        		</table>
					        		</td>
			        			</tr>
			        	</c:forEach>
	        		</table>
        		</c:when>
        		<c:otherwise> <c:out value="Non ci sono aste chiuse"/> </c:otherwise>
        	</c:choose>
        </div>
    </body>
</html>