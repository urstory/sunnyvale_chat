<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="ko">
<head>

    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
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
                            <h1 class="font-montserrat-reg large-heading">소셜 가입</h1>
                            <div class="page-content">
                                <p>- 이름을 입력하세요.</p>
                                <p>- 인증을 확인할 수 있는 메일 주소를 입력해주세요. 메일 주소를 잘못입력하면, 회원 인증을 할 수 없으며 해당 사이트를 제대로 사용할 수 없습니다.</p>
                            </div>
                        </div>
                    </div>

                    <div class="col-xlarge-8">

                        <div id="success-message" class="notification"><p class="font-opensans-reg"></p></div>
                        <div id="error-message" class="notification"><p class="font-opensans-reg"></p></div>


                        <form method="post" action="/users/socialjoin" id="updateSocialInfoForm">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                            <div class="row">
                                <div class="col-xlarge-2 col-medium-2">
                                    email
                                </div>
                                <div class="col-xlarge-6 col-medium-6">
                                    <input type="text" class="input-field" name="email" id="email" value="${loginUser.email}" placeholder="email" tabindex="1" />
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-xlarge-2 col-medium-2">
                                    이름
                                </div>
                                <div class="col-xlarge-6 col-medium-6">
                                    <input type="text" class="input-field" name="name" id="name" value="${loginUser.name}" placeholder="이름" tabindex="2" />
                                </div>
                            </div>
                            <input type="button" id="updateSocialInfoBtn" class="secondary-button font-montserrat-reg hov-bk" value="변경" />
                        </form>
                    </div>

                </div>

                <div class="row">
                    <div class="col-xlarge-4"></div>
                    <div class="col-xlarge-8">&nbsp;</div>
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

    $("#updateSocialInfoBtn").click(function(){
        var email = $('#email').val();

        if(email == '') {
            alert('email을 입력해주세요.');
            return;
        }
        if(!validateEmail(email)){
            alert('email 형식에 맞지 않습니다.');
            return;
        }
        if($('#name').val() == '') {
            alert('name을 입력해주세요.');
            return;
        }

        var errorMsg = "";
        $.ajax({
            url: '/api/users/existEmail',
            method: 'get',
            data: {'email': email},
            contentType: "application/json",
            success: function (exist) {
                console.log(exist);
                if (exist == 'true') {
                    alert('이미 가입한 email입니다.');
                }else{
                    $('#updateSocialInfoForm').submit();
                }
            },
            error: function (err) {
                console.log(err.toString());
            }
        });

    });

    $(document).ready(function () {
        $('#name').focus();
    });
</script>

</body>
</html>

