<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>회원 정보 수정</title>
</head>
<body>

<form name="frm" action="update.do" method="post">
<input type="hidden" name="num" value="${dto.num }"/>
<p><input type="text" name="name" value="${dto.name }"/></p>
<p><input type="text" name="age" value="${dto.age }"/></p>
<p> <input type="text" name="loc" value="${dto.loc }"/></p>
<p><input type="submit" value="send"/></p>
</form>
</body>
</html>