<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--<link rel="stylesheet" href="resources/css/dictCss.css">--%>

<html>
<head>
    <meta charset="UTF-8">

    <script type="text/javascript" src="/resources/js/jquery.js"></script>
    <link rel="stylesheet" type="text/css" href="/resources/css/index.css">
    <link rel="stylesheet" type="text/css" href="/resources/css/reset.css">
</head>
<body>

    <div class="content">
        <form class="clearfix J_form">
            <img src="/resources/img/dict_logo.png">
            <input class="J_input" type="text" placeholder="请输入汉字词句">
            <input class="J_submit" type="submit" value="搜索" >
            <ul class="J_list list" ></ul>
        </form>
    </div>


    <script type="text/javascript">
        $(document).ready(function(){
            $('.J_input').bind('keyup' , function(){
                var
                liHtml ,
                para = 'queryWord='+$('.J_input').val(),
                url = '${website}public/search?'+para;

                $.getJSON(url,function(jsontt){

//                    alert("JSON Data: " + "ciao");

                    $('.J_list').html('');
                    $.each(jsontt.data,function(index,element){
                        liHtml = '<li>'+element+'</li>';
                        $('.J_list').append(liHtml);
                    })
                })
            });

            $(document).on('click' , '.J_list li' , function(ev){
                var value = $(ev.currentTarget).text();
                $('.J_input').val(value);
                $('.J_form').submit();
            })
        })
    </script>




    <%--<form action="${website}public/search" method="post">
                <input id="q" name="q" class="search_input" type="text" placeholder="请输入汉字词句" autocomplete="off"
                         style="color: rgb(153, 153, 153);" value="">
                <input id="search" class="search_submit" type="submit" value="查询">
            </form>--%>

    <%--<table id="contentTable" class="table table-striped table-bordered table-condensed">
        <thead>
        </thead>

        <tbody>
        <c:forEach items="${queryWords}" var="word">
            <tr>
                <td>${word}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>--%>

</body>
</html>