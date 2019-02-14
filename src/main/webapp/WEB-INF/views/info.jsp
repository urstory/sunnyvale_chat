<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="ko">
<head>

    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>

    <title>Sunnyvale - Main</title>

    <script  type="text/javascript" src="/webjars/jquery/3.3.1-2/jquery.min.js"></script>

</head>


<body class="page-template-blog">



<div id="main-content">

    <!-- 본문내용 시작 -->
    <!-- contact form section -->
    <section class="contact-form-section">
        <div class="container">

            <div class="section-inner no-border">

                <div class="row">

                    <!-- contact text content -->
                    <div class="col-xlarge-4">
                        <div class="contact-text-content">
                            <h1 class="font-montserrat-reg large-heading">회원정보</h1>
                            <div class="page-content">
                                <p>- 소셜회원으로 가입한 회원은 소셜 연결을 끊기전에 암호를 입력해야 합니다.</p>
                                <p>- 소셜회원으로 가입한 회원은 암호 변경시 기존 암호에 값을 넣지 말아주세요.</p>
                            </div>
                        </div>
                    </div>

                    <div class="col-xlarge-8">

                        <div id="success-message" class="notification"><p class="font-opensans-reg"></p></div>
                        <div id="error-message" class="notification"><p class="font-opensans-reg"></p></div>

                        <div class="row">
                            <div class="col-xlarge-4 col-medium-4">
                                <h3 class="font-montserrat-reg">이름</h3>
                            </div>

                            <div class="col-xlarge-6 col-medium-6">
                                <h3 class="font-montserrat-reg">${loginUser.name}</h3>
                            </div>
                        </div>

                        <div class="row">
                            <div class="col-xlarge-12 col-medium-12">
                                &nbsp;
                            </div>
                        </div>

                        <div class="row">
                            <div class="col-xlarge-4 col-medium-4">
                                <h3 class="font-montserrat-reg">email</h3>
                            </div>

                            <div class="col-xlarge-6 col-medium-6">
                                <h3 class="font-montserrat-reg">${loginUser.email}</h3>
                            </div>
                        </div>

                        <div class="row">
                            <div class="col-xlarge-12 col-medium-12">
                                &nbsp;
                            </div>
                        </div>

                        <div class="row">
                            <div class="col-xlarge-4 col-medium-4">
                                <h3 class="font-montserrat-reg">별명</h3>
                            </div>

                            <div class="col-xlarge-4 col-medium-4">
                                <h3 class="font-montserrat-reg"><input type="text" id="nickName" value="${loginUser.nickName}"></h3>
                            </div>
                            <div class="col-xlarge-2 col-medium-2">
                                <h3 class="font-montserrat-reg"><input type="button" id="updateNickNameBtn" value="별명수정"></h3>
                            </div>
                        </div>

                        <div class="row">
                            <div class="col-xlarge-12 col-medium-12">
                                &nbsp;
                            </div>
                        </div>

                        <div class="row">
                            <div class="col-xlarge-4 col-medium-4">
                                <h3 class="font-montserrat-reg">암호</h3>
                            </div>

                            <div class="col-xlarge-4 col-medium-4">
                                <h3 class="font-montserrat-reg"><input type="password" id="existingPassword" value="" placeholder="기존암호"></h3>
                                <h3 class="font-montserrat-reg"><input type="password" id="password1" value="" placeholder="암호"></h3>
                                <h3 class="font-montserrat-reg"><input type="password" id="password2" value="" placeholder="암호확인"></h3>
                            </div>
                            <div class="col-xlarge-2 col-medium-2">
                                <h3 class="font-montserrat-reg"><input type="button" id="updatePassword" value="암호수정"></h3>
                            </div>
                        </div>

                        <div class="row">
                            <div class="col-xlarge-12 col-medium-12">
                                &nbsp;
                            </div>
                        </div>

                        <div class="row">
                            <div class="col-xlarge-4 col-medium-4">
                                <h3 class="font-montserrat-reg">페이스북 연동</h3>
                            </div>

                            <div class="col-xlarge-6 col-medium-6">
                                <label class="switch">
                                    <c:if test="${hasUserConnection == true}">
                                        <input type="checkbox" checked id="connectFacebook">
                                    </c:if>
                                    <c:if test="${hasUserConnection != true}">
                                        <input type="checkbox" id="connectFacebook">
                                    </c:if>

                                    <span class="slider"></span>
                                </label>
                            </div>
                        </div>
                    </div>

                </div>


            </div>


        </div>
    </section>


    <!-- 본문내용 끝 -->

</div>

<script>
    $(document).ready(function () {

        $("#updatePassword").click(function () {
            var existingPassword = $("#existingPassword").val();
            var password1 = $("#password1").val();
            var password2 = $("#password2").val();
            if(password1 != password2){
                alert('암호와 암호확인은 값이 같아야 합니다.');
                return;
            }
            var errorMsg = '';

            var JSONObject= {
                "existingPassword" : existingPassword,
                "password1" : password1,
                "password2" : password2

            };
            var jsonData = JSON.stringify( JSONObject );
            var token = $("meta[name='_csrf']").attr("content");
            var header = $("meta[name='_csrf_header']").attr("content");
            $.ajaxSetup({
                headers:
                    { 'X-CSRF-TOKEN': token }
            });

            $.ajax({
                url: '/api/users/changePassword',
                method: 'put',
                data: jsonData,
                contentType: "application/json",
                success: function (result) {
                    if (result == 'ok') {
                        alert('암호가 변경되었습니다.');
                    }else{
                        alert('잘못된 값을 입력했습니다.');
                    }
                },
                error: function (err) {
                    console.log(err.toString());
                }
            });
        }); // updateNickname


        $("#updateNickNameBtn").click(function () {
            var nickName = $("#nickName").val();
            var errorMsg = '';
            $.ajax({
                url: '/api/users/existNickName',
                method: 'get',
                data: {'nickName': nickName},
                async: false,
                contentType: "application/json",
                success: function (exist) {
                    if (exist == 'true') {
                        errorMsg += '이미 존재하는 별명입니다.\n';
                    }
                },
                error: function (err) {
                    console.log(err.toString());
                }
            });
            if(errorMsg != ''){
                alert(errorMsg);
                return;
            }


            var JSONObject= {
                "nickName" : nickName
            };
            var jsonData = JSON.stringify( JSONObject );
            var token = $("meta[name='_csrf']").attr("content");
            var header = $("meta[name='_csrf_header']").attr("content");
            $.ajaxSetup({
                headers:
                    { 'X-CSRF-TOKEN': token }
            });

            $.ajax({
                url: '/api/users/changeNickName',
                method: 'put',
                data: jsonData,
                contentType: "application/json",
                success: function (result) {
                    alert('닉네임이 변경되었습니다.');
                },
                error: function (err) {
                    console.log(err.toString());
                }
            });
        }); // updateNickname

        $("#connectFacebook").change(function(){
            if($("#connectFacebook").is(":checked")){
                window.location.replace("/connect/facebook");
            }else{
                $.ajax({
                    url : '/api/users/disconnectFacebook',
                    method : 'get',
                    contentType: "application/json",
                    success : function (data) {
                       if(data == 'fail'){
                           alert('소셜 연결을 끊으시려면 암호를 입력해야 합니다.')
                           $("#connectFacebook").prop("checked", true);
                        }
                    },
                    error : function (err) {
                        console.log(err.toString());
                    }
                });
            }
        });
    });
</script>

</body>
</html>