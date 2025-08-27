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
        <h1>Login Asta On-Line</h1>
        <form action="login" method="post">
            Nome Utente:<input type="text" name="username" value="${sessionScope.username }" required><br>
            Password:<input type="password" name="password" required><br>
            <input type="submit" name="loginButton" value="Login">
            <c:if test="${not empty sessionScope.error}">
            	<p style="color : red;">${sessionScope.error}</p>
            	<c:remove var="error" scope="session"/>
            </c:if>
        </form>
    </body>
</html>