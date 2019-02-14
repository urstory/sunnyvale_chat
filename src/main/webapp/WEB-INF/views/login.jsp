<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="ko">
<head>

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
                            <h1 class="font-montserrat-reg large-heading">로그인</h1>
                            <div class="page-content">
                                <p>- Sunnyvale에 오신 것을 환영합니다.</p>
                            </div>
                        </div>
                    </div>

                    <div class="col-xlarge-8">

                        <div id="success-message" class="notification"><p class="font-opensans-reg"></p></div>
                        <div id="error-message" class="notification"><p class="font-opensans-reg"></p></div>

                        <form method="post" action="/users/login">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                            <div class="row">
                                <div class="col-xlarge-6 col-medium-6 col-small-6">
                                    <input type="text" class="input-field" name="id" id="id" value="" placeholder="로그인ID" tabindex="1" />
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-xlarge-6 col-medium-6 col-small-6">
                                    <input type="password" class="input-field" name="password" id="password" value="" placeholder="암호" tabindex="2" />
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-xlarge-2 col-medium-2 col-small-2">
                                    로그인유지
                                </div>
                                <div class="col-xlarge-4 col-medium-4 col-small-4">
                                    <input type="checkbox"  name="remember-me" id="remember-me" tabindex="3" checked/>
                                </div>
                            </div>

                            <div class="row">
                                <div class="col-xlarge-6 col-medium-6 col-small-6">
                                    &nbsp;
                                </div>
                            </div>

                            <div class="row">
                                <div class="col-xlarge-6 col-medium-6 col-small-6">
                                    <input type="submit" class="secondary-button font-montserrat-reg hov-bk" value="로그인" />
                                </div>
                            </div>
                        </form>
                        <div class="row">
                            <div class="col-xlarge-6 col-medium-6 col-small-6">
                                &nbsp;
                            </div>
                        </div>
                    </div>

                </div>

                <div class="row">
                    <div class="col-xlarge-4 col-medium-4 col-small-4"></div>
                    <div class="col-xlarge-8 col-medium-8 col-small-8">&nbsp;</div>
                </div>


                <div class="row">
                    <div class="col-xlarge-4 col-medium-4 col-small-4"></div>
                    <div class="col-xlarge-8 col-medium-8 col-small-8"><a href="/login/facebook"><img src="/assets/img/facebook_login.png" width="200"></a></div>
                </div>

                <div class="row">
                    <div class="col-xlarge-4 col-medium-4 col-small-4"></div>
                    <div class="col-xlarge-8 col-medium-8 col-small-8">&nbsp;</div>
                </div>


                <div class="row">
                    <div class="col-xlarge-4 col-medium-4 col-small-4"></div>
                    <div class="col-xlarge-8 col-medium-8 col-small-8"><a href="/users/findPassword">암호 찾기</a></div>
                </div>

            </div>


        </div>
    </section>


    <!-- 본문내용 끝 -->

</div>

<script>
    $(document).ready(function () {

        $('#id').focus();
    });
</script>

</body>
</html>