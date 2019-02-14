<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="ko" xmlns="http://www.w3.org/1999/xhtml" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no"/>
    <script  type="text/javascript" src="/webjars/jquery/3.3.1-2/jquery.min.js"></script>
    <script  type="text/javascript" src="/webjars/sockjs-client/1.0.2/sockjs.min.js"></script>

    <script th:inline="javascript">
        var chatRoomId = ${chatRoomId};
    </script>
    <script src="/js/chatroom.js"></script>
    <title>chat room</title>
</head>

<body>

<div class="jumbotron">
    <h1>chat room</h1>
</div>

<div class="container">

    <div class="col-sm-12 col-md-12">
        <textarea cols="80" rows="15" id="chatArea" class="form-control"></textarea>
    </div>
    <div class="col-sm-12 col-md-12">
        <input type="text" id="chatInput" class="form-control"/>
        <input type="button" id="sendBtn" value="전송" class="btn btn-primary btn-small"/>
    </div>

</div>
</body>
</html>