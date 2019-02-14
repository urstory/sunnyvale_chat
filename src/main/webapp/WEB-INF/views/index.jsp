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

	<!-- 본문 내용 시작 -->
	<section class="featured-section featured-narrow">
		<div id="featured-slideshow-outer" class="carousel-outer">

			<div id="featured-slideshow" class="carousel" data-autoplay="" data-autoplay-speed="" data-animation-in="fadeIn" data-animation-out="fadeOut">

				<div class="featured-slide" style="background-image:url('assets/img/featured_placeholder_1.jpg');">
					<div class="container">
						<div class="featured-slide-content featured-left">
							<h2 class="font-montserrat-reg">Welcome to Sunnyvale</h2>
							<p>즐거운 프로그래밍을 위해서......</p>
						</div>
					</div>
				</div>
			</div>
		</div>
	</section>

	<!-- 본문 내용 끝 -->
</div>

<script>
    $(document).ready(function () {
        $('.board-row').hover(
            function () {
                console.log('hover');
                $(this).children().removeClass('graytd');
                $(this).addClass('hover');
            },
            function () {
                $(this).removeClass('hover');
                $(this).children(':even').addClass('graytd');
            }
        );
    });
</script>

</body>
</html>