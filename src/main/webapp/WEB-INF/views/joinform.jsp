<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="ko">
<head>

    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>
    <!-- page title -->
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
                            <h1 class="font-montserrat-reg large-heading">회원가입</h1>
                            <div class="page-content">
                                <p>- 직접 email과 암호를 입력해서 회원가입을 하실 수 있습니다.</p>
                                <p>- Facebook로그인 버튼을 누르면 가입과 동시에 로그인할 수 있습니다.</p>
                            </div>
                        </div>
                    </div>

                    <div class="col-xlarge-8">

                        <div id="success-message" class="notification"><p class="font-opensans-reg"></p></div>
                        <div id="error-message" class="notification"><p class="font-opensans-reg"></p></div>

                        <form method="post" action="/users/join" >
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                            <div class="row">
                                <div class="col-xlarge-6 col-medium-6">
                                    <input type="text" class="input-field" name="id" id="id" value="" placeholder="로그인ID - email을 입력해주세요." tabindex="1" />
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-xlarge-6 col-medium-6">
                                    <input type="text" class="input-field" name="name" id="name" value="" placeholder="이름" tabindex="1" />
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-xlarge-6 col-medium-6">
                                    <input type="text" class="input-field" name="nickName" id="nickName" value="" placeholder="별명" tabindex="1" />
                                </div>
                            </div>

                            <div class="row">
                                <div class="col-xlarge-6 col-medium-6">
                                    <input type="password" class="input-field" name="password" id="password" value="" placeholder="암호" tabindex="2" />
                                </div>
                            </div>
                            <input type="button" class="secondary-button font-montserrat-reg hov-bk" id="joinBtn" value="회원가입" />
                        </form>
                    </div>

                </div>

                <div class="row">
                    <div class="col-xlarge-4"></div>
                    <div class="col-xlarge-8">&nbsp;</div>
                </div>


                <div class="row">
                    <div class="col-xlarge-4"></div>
                    <div class="col-xlarge-8"><a href="/join/facebook"><img src="/assets/img/join_facebook.png"></a></div>
                </div>

            </div>


        </div>
    </section>


    <!-- 본문내용 끝 -->

</div>

<script>
    function validateEmail(email) {
        var re = /^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$/i;
        return re.test(email);
    }

    $(document).ready(function () {
        $('#id').focus();

        $('#joinBtn').click(function () {
            var errorMsg = '';
            if($('#id').val() == '') {
                errorMsg += 'id를 입력해주세요.\n';
            }else {
                var email = $('#id').val();

                if(!validateEmail(email)){
                    errorMsg += 'email형식에 맞지 않습니다.\n';
                }

                $.ajax({
                    url: '/api/users/existEmail',
                    method: 'get',
                    data: {'email': email},
                    async: false,
                    contentType: "application/json",
                    success: function (exist) {
                        console.log(exist);
                        if (exist == 'true') {
                            errorMsg += '이미 가입한 email입니다.\n';
                        }
                    },
                    error: function (err) {
                        console.log(err.toString());
                    }
                });
            }

            if($('#name').val() == '')
                errorMsg += '이름을 입력해주세요.\n';

            if($('#nickName').val() == '') {
                errorMsg += '별명을 입력해주세요.\n';
            }else{
                var nickName = $('#nickName').val();
                $.ajax({
                    url: '/api/users/existNickName',
                    method: 'get',
                    data: {'nickName': nickName},
                    async: false,
                    contentType: "application/json",
                    success: function (exist) {
                        console.log(exist);
                        if (exist == 'true') {
                            errorMsg += '이미 존재하는 별명입니다.\n';
                        }
                    },
                    error: function (err) {
                        console.log(err.toString());
                    }
                });
            }

            if($('#name').val().length < 2)
                errorMsg += '이름은 2글자 이상 입력해주세요.\n';

            if($('#password').val() == '')
                errorMsg += '암호를 입력해주세요.\n';

            if($('#password').val().length < 5)
                errorMsg += '암호는 5글자 이상 입력해주세요.\n';

            if(errorMsg != ''){
                alert(errorMsg);
                return;
            }else{
                var id = $('#id').val();
                var name = $('#name').val();
                var nickName = $('#nickName').val();
                var password = $('#password').val();

                var JSONObject= {
                    "email" : id,
                    "name" : name,
                    "nickName" : nickName,
                    "password" : password
                };
                var jsonData = JSON.stringify( JSONObject );
                var token = $("meta[name='_csrf']").attr("content");
                var header = $("meta[name='_csrf_header']").attr("content");
                $.ajaxSetup({
                    headers:
                        { 'X-CSRF-TOKEN': token }
                });
                $.ajax({
                    url: '/api/users/join',
                    method: 'post',
                    data: jsonData,
                    dataType: "json",
                    contentType: "application/json",
                    success: function (exist) {
                        alert('가입되었습니다. 이메일 인증 후 사용해주세요.');
                        $(location).attr('href','/users/extrauser');
                        return;
                    },
                    error: function (err) {
                        console.log(err.toString());
                    }
                });
            }


        });
    });
</script>

</body>
</html>