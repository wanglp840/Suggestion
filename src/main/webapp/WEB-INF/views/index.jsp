<%@ page contentType="text/html;charset=UTF-8" %>

<html>
	<head>
		<title>search</title>
		<meta charset="utf-8" />
		<link rel="stylesheet" href="/resources/css/base.css" />
		<link rel="stylesheet" href="/resources/css/index.css" />
		<script src="/resources/js/jquery.js"></script>
		<script src="/resources/js/index_bak.js" charset="UTF-8"></script>
	</head>
	<body>
		<script type="text/javascript">
			var requestUrl = '${website}public/search';
		</script>

		<div class="page">
			<div class="logo">
				<a href="#">
					<img src="/resources/img/dict_logo.png" alt="logo">
				</a>
			</div>
			<div class="search-holder ">
				<form id="J_searchForm" action="" type=""><!-- 点击搜词form表单提交 action type-->
					<input type="text" id="J_searchInp" name="queryWord" autocomplete="off" placeholder="请输入搜索内容"/><!-- 点击搜词form表单提交 输入框name值   自行修改-->
					<button type="submit">搜词</button>
				</form>
			</div>
			<div class="input-prompt-container has-show" id="J_promptHolder" style="display: none">
				<ul id="J_listHolder" class="input-prompt-list"></ul>
			</div>
		</div>
	</body>
</html>