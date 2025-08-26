<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
    	<meta charset="UTF-8">
        <title>Pagina Acquisto</title>
    </head>
    <body>
        <h1>Acquisto Page</h1>
        <form action="FindAuctionByKey" method="post">
        	<input type="text" name="searchBar" required/>
        	<button type="submit">Cerca</button><br>
        </form>
        <c:if test="${not empty sessionScope.searchError }">
        	<c:out value="${sessionScope.searchError }"/>
        	<c:remove var="searchError" scope="session"/>
        </c:if>
        <c:choose>
        	<c:when test="${not empty sessionScope.openAuctionList }">
        	<h4 style="color : red;">Risultati Ricerca</h4>
        		<table>
		        	<tr><td>ID Asta</td> <td>Data Scadenza</td></tr>
		        	<c:forEach var="auction" items="${sessionScope.openAuctionList }">
		        		<tr>
		        			<td> <a href="<c:url value="/Offerta?id=${auction.id_asta }"/>" >${auction.id_asta}</a>  </td>
		        			<td> <c:out value="${auction.data_scadenza }"/> </td>
		        		</tr>
		        	</c:forEach>
		        </table> 
		        <c:remove var="openAuctionList" scope="session"/>
        	</c:when>
        	<c:otherwise><c:out value="Nessun'asta trovata con parola chiave ${searchBar }"/></c:otherwise>
        </c:choose>
    </body>
</html>