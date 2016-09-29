<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>All cars</title>
</head>
<body>
<table width="600px">
    <tr>
        <td><b>ID</b></td>
        <td><b>Name</b></td>
        <td><b>Price</b></td>
    </tr>
    <c:forEach var="car" items="${cars}">
        <tr>
            <td>${car.id}</td>
            <td>${car.name}</td>
            <td>${car.price}</td>
            <td><a href="/edit?id=${car.id}">Edit</a> | <a href="/delete?id=${car.id}">Delete</a></td>
        </tr>
    </c:forEach>
    <tr>
        <td colspan="5">
            <a href="/add">Add car</a>
        </td>
    </tr>
</table>
</body>
</html>
