<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Mini-Search Engine</title>
<style>
#form {
margin-left: 40%;
margin-top: 3%;
}
/* .h1{
margin-left: 48%;
margin-top: 10%;
} */
</style>
</head>
<body>
<h1 style="
    margin-left: 48%;
">IRIS 14</h1>
<div id="form">
<form method="get" action="search" name="querySearch">
<input type="text" id="searchTextField" name="query" style="width: 300px;">
 <button type="submit">Search</button>
<select name="ranking">
<option value="1" selected>Vector Space Similarity</option>
<option value="2">Authorities</option>
<option value="3">Hubs</option>
<option value="4" >PageRank</option>
</select>
</form>
</div>
</body>
</html>