$(function(){
	localPro();

	<!--按键抬起发送请求-->
	$('#J_searchInp').on('keyup', function(e){
		var queryWord = $(this).val();
		if(queryWord.length != 0){
			$.ajax({
				url: 'public/search',  //搜索关键字请求
				type: 'get',//请求方式
				dataType: 'json',
				data: {
					queryWord: queryWord  //关键字参数
				},
				success: function(rs){
					if(rs.code == 0){
						if(rs.data.length != 0){
							showPro();
							renderPro(rs.data, queryWord);
						}else{
							hidePro();
						}
						
					}
				}
			})
		}else{
			hidePro();
		}
	});

	$('#J_listHolder').delegate('li', 'click', function(e){
		var 
		    keyword = $(this).text();
		$('#J_searchInp').val(keyword);
		$('#J_searchForm').submit();
	});
	$(document).on('click', function(e){
		var target = e.target;
		if($(target).parent('.input-prompt-container').length == 0){
			hidePro();
		}
	});
	$('#J_listHolder').delegate('li', 'mouseover', function(e){
		$('#J_listHolder li').removeClass('active');
		$(this).addClass('active');
	});
	$('#J_listHolder').on('mouseout', function(e){
		$('#J_listHolder li').removeClass('active');
	});
});

function renderPro(data, queryWord){
	//$.each(data, function(index, item){
	//	//var otherStr = item.strContent.replace(queryWord, "");
	//	var otherStr = item.strContent;
	//		str += '<li><b>'+queryWord+'</b>'+otherStr;
	//	if(item.tip){
	//		str += '<span>'+item.tip+'</span></li>';
	//	}else{
	//		str += '</li>';
	//	}
	//});
	//$('#J_listHolder').empty().append(str);

	var str = '';
	$.each(data, function(index, item){
		var otherStr = item.strContent,
			handleQ = item.handledQuery.trim(),
			handleLength = handleQ.length;
		var	execStr = '';
		for(var i = 0; i < handleLength; i++){
			if(i != handleLength - 1){
				execStr += handleQ[i]+'[\\s|,]*';
			}else{
				execStr += handleQ[i]+'[\\s|,]*';
			}
		}
		var execObj = new RegExp(execStr)
		var matchStr = otherStr.match(execObj);
		otherStr = otherStr.replace(matchStr, "<b>"+matchStr+"</b>");
		str += '<li>'+otherStr+'</li>';
	});
	$('#J_listHolder').empty().append(str);
}

function localPro(){
	var 
		position = $('#J_searchInp').offset()
		topVal = position.top,
		left = position.left,
		height = $('#J_searchInp').height(),
		proTop = topVal + height + 30;
	$('#J_promptHolder').offset({top: proTop, left: left});
}

function showPro(){
	$('#J_promptHolder').show();
}
function hidePro(){
	$('#J_promptHolder').hide();
}