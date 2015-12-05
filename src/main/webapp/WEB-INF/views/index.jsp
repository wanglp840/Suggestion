<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ page contentType="text/html;charset=UTF-8" %>
<link rel="stylesheet" href="resources/css/dictCss.css">



<html>
<head>
<body>

    <%--<form method="post" action="${website}search">
        <input id="q" class="search_input" type="text" placeholder="请输入汉字词句" autocomplete="off"
               value="" name="q" style="color: rgb(153, 153, 153);">
        <input id="search" class="search_submit" type="submit" value="查询">
    </form>--%>

    <form action="${website}public/search" method="post">
        <input id="q" name="q" class="search_input" type="text" placeholder="请输入汉字词句" autocomplete="off"
                 style="color: rgb(153, 153, 153);" value="">
        <input id="search" class="search_submit" type="submit" value="查询">
    </form>



    <table id="contentTable" class="table table-striped table-bordered table-condensed">
        <thead>
        </thead>

        <tbody>
        <c:forEach items="${queryWords}" var="word">
            <tr>
                <td>${word}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

</body>
</html>