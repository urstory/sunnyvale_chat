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
                            <h1 class="font-montserrat-reg large-heading">아직은 임시 회원</h1>
                            <div class="page-content">
                                <p>- 관리자 승인 후 사용가능합니다. </p>
                            </div>
                        </div>
                    </div>

                    <div class="col-xlarge-8">

                        <div id="success-message" class="notification"><p class="font-opensans-reg"></p></div>
                        <div id="error-message" class="notification"><p class="font-opensans-reg"></p></div>

                    </div>

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